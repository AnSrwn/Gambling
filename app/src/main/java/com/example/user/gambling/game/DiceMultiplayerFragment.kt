package com.example.user.gambling.game

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.user.gambling.R
import com.example.user.gambling.connection.Connection
import com.example.user.gambling.connection.PermissionHandler
import com.example.user.gambling.models.ScoreViewModel
import com.example.user.gambling.models.UserNameViewModel
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import java.nio.charset.StandardCharsets.UTF_8

/**
 * In this fragment, the user connects to another device and is able to play with another user.
 * For the actual game, gameFragment is used, but the multiplayerFragment is always running (in the
 * background) to manage the connection.
 * After one round, the score of the user and the opponent is displayed in the multiplayerFragment.
 */
class DiceMultiplayerFragment : android.support.v4.app.Fragment() {

    private var connection: Connection? = null
    private var username = "defaultName"

    private var myScore = 0
    private var opponentScore = 0
    private var myTotalScore = 0
    private var opponentTotalScore = 0

    private var findOpponentButton: Button? = null
    private var btnContinueGame: Button? = null
    private var yourScoreText: TextView? = null
    private var opponentScoreText: TextView? = null
    private var totalScoreText: TextView? = null

    private var loadingPanel: FrameLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dice_multiplayer, container, false)

        setAllViews(view)

        setViewsVisibility(false)

        btnContinueGame!!.setOnClickListener {
            startGameFragment()
            btnContinueGame!!.visibility = View.GONE
        }

        findOpponentButton!!.setOnClickListener {
            connection!!.findOpponent()
            findOpponentButton!!.text = getString(R.string.dice_multi_status_searching)
            findOpponentButton!!.isEnabled = false
        }

        registerForPlayerNameUpdates()
        registerForScoreUpdates()

        return view
    }

    override fun onResume() {
        super.onResume()
        if(!PermissionHandler().requestPermissions(activity!!)) {
            Toast.makeText(
                    activity,
                    getString(R.string.dice_multi_toast_permissions_denied),
                    Toast.LENGTH_SHORT).show()
        }
        connection = Connection(
                Nearby.getConnectionsClient(requireActivity()),
                context!!,
                connectionLifecycleCallback,
                username)
    }

    override fun onPause() {
        super.onPause()
        connection!!.connectionsClient.stopAllEndpoints()
    }

    /**
     * Callbacks for receiving payloads.
     * To display the socres, the user must have his/her own score and the opponents score.
     * Otherwise a waiting screen is displayed until both scores are there.
     */
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            opponentScore = String(payload.asBytes()!!, UTF_8).toInt()

            activity?.let {
                val scoreViewModel = ViewModelProviders.of(it).get(ScoreViewModel::class.java)
                scoreViewModel.opponentScore.postValue(opponentScore)
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status ==
                    PayloadTransferUpdate.Status.SUCCESS
                    && myScore != 0
                    && opponentScore != 0) {

                loadingPanel!!.visibility = View.GONE

                setTotalScore(myScore, opponentScore)

                yourScoreText!!.text = getString(R.string.dice_multi_your_score, myScore)
                opponentScoreText!!.text = getString(R.string.dice_multi_opponent_score, opponentScore)
                totalScoreText!!.text = getString(
                        R.string.dice_multi_total_score,
                        myTotalScore,
                        opponentTotalScore,
                        connection!!.opponentName)

                myScore = 0
                opponentScore = 0

                setViewsVisibility(true)
            }
        }
    }

    /**
     * Callbacks for connections to other devices.
     * After the connection is established, the username of the opponent is saved,
     * the search for other devices is stopped and the gameFragment is started.
     * If the connection is interrupted, the multiplayerFragment is resetted.
     */
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            connection!!.connectionsClient.acceptConnection(endpointId, payloadCallback)
            connection!!.opponentName = connectionInfo.endpointName
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                connection!!.stopOpponentSearch()
                connection!!.opponentEndpointId = endpointId

                findOpponentButton!!.visibility = View.GONE

                startGameFragment()
            } else {
                Toast.makeText(
                        activity,
                        getString(R.string.dice_multi_toast_conn_failed),
                        Toast.LENGTH_SHORT).show()
            }
        }

        override fun onDisconnected(endpointId: String) {
            resetDiceMultiplayerFragment()
            showMultiplayerFragment()

            Toast.makeText(
                    activity,
                    getString(R.string.dice_multi_toast_disconnected),
                    Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * The multiplayerFragment is made visible again.
     * In case, that the user is in the gameFragment at the moment, the gameFragment is removed from
     * the stack.
     */
    private fun showMultiplayerFragment() {
        val gameFragment = fragmentManager!!.findFragmentByTag("gameFragment")
        if (gameFragment != null) {
            fragmentManager!!.beginTransaction().remove(gameFragment).commit()
        }

        val multiplayerFragment = fragmentManager!!.findFragmentByTag("multiplayerFragment")
        fragmentManager!!.beginTransaction().show(multiplayerFragment!!).commit()
    }

    /**
     * This method calculates, if the user or the opponent wins this round.
     * @param myScore score of the user
     * @param opponentScore score of the opponent
     */
    private fun setTotalScore(myScore: Int, opponentScore: Int) {
        when {
            myScore > opponentScore -> {
                myTotalScore++
                Toast.makeText(
                        activity,
                        getString(R.string.dice_multi_toast_round_won),
                        Toast.LENGTH_SHORT).show()
            }
            opponentScore > myScore -> {
                opponentTotalScore++
                Toast.makeText(
                        activity,
                        getString(R.string.dice_multi_toast_round_lost),
                        Toast.LENGTH_SHORT).show()
            }
            else -> Toast.makeText(
                    activity,
                    getString(R.string.dice_multi_toast_round_tie),
                    Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * This method finds all views which are needed in this fragment.
     */
    private fun setAllViews(view: View) {
        yourScoreText = view.findViewById(R.id.textViewMyScore)
        opponentScoreText = view.findViewById(R.id.textViewOpponentScore)
        totalScoreText = view.findViewById(R.id.textViewTotalScore)
        findOpponentButton = view.findViewById(R.id.buttonConnFindOpponent)
        btnContinueGame = view.findViewById(R.id.buttonContinueMultiGame)
        loadingPanel = view.findViewById(R.id.waitForOpponentLoadingPanel)
    }

    /**
     * This method sets the visibility of the score texts and the continue button.
     * @param visible if true, the visibilities are set to visible, otherwise to gone.
     */
    private fun setViewsVisibility(visible: Boolean) {
        if (visible) {
            yourScoreText!!.visibility = View.VISIBLE
            opponentScoreText!!.visibility = View.VISIBLE
            totalScoreText!!.visibility = View.VISIBLE
            btnContinueGame!!.visibility = View.VISIBLE
        } else {
            yourScoreText!!.visibility = View.GONE
            opponentScoreText!!.visibility = View.GONE
            totalScoreText!!.visibility = View.GONE
            btnContinueGame!!.visibility = View.GONE
        }
    }

    /**
     * This method resets the active multiplayerFragment.
     */
     private fun resetDiceMultiplayerFragment() {
        findOpponentButton!!.text = getString(R.string.dice_multi_find_opponent)
        findOpponentButton!!.isEnabled = true
        findOpponentButton!!.visibility = View.VISIBLE

        loadingPanel!!.visibility = View.GONE
        setViewsVisibility(false)


        myTotalScore = 0
        opponentTotalScore = 0
    }

    /**
     * Opens the gameFragment and telling, that it is a Multiplayer game.
     * Hides the active multiplayerFragment, so that it still runs in the background.
     */
    private fun startGameFragment() {
        val diceGameFragment = DiceGameFragment()

        val bundle = Bundle()
        bundle.putBoolean("isMultiplayer", true)
        diceGameFragment.arguments = bundle

        fragmentManager!!.beginTransaction().add(
                com.example.user.gambling.R.id.fragmentContainer,
                diceGameFragment,
                "gameFragment").addToBackStack(null).commit()

        val multiplayerFragment = fragmentManager!!.findFragmentByTag("multiplayerFragment")
        fragmentManager!!.beginTransaction().hide(multiplayerFragment!!).commit()
    }

    /**
     * Method to get the username.
     */
    private fun registerForPlayerNameUpdates() {
        activity?.let { fragmentActivity ->
            val sharedViewModel = ViewModelProviders.of(fragmentActivity).get(UserNameViewModel::class.java)
            sharedViewModel.userName.observe(this, Observer { i ->
                i?.let {
                    username = it
                }
            })
        }
    }

    /**
     * Method to get the score, which the user got in the gameFragment.
     */
    private fun registerForScoreUpdates() {
        activity?.let { fragmentActivity ->
            val sharedViewModel = ViewModelProviders.of(fragmentActivity).get(ScoreViewModel::class.java)
            sharedViewModel.myScore.observe(this, Observer { i ->
                i?.let {
                    myScore = it
                    connection?.sendScore(myScore)
                    if(connection != null) {
                        loadingPanel!!.visibility = View.VISIBLE
                    }
                }
            })
        }
    }
}