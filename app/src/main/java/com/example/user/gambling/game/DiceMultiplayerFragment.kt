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
import com.example.user.gambling.game.score.ScoreViewModel
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import java.nio.charset.StandardCharsets.UTF_8

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

    // Callbacks for receiving payloads
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

    // Callbacks for connections to other devices
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

    private fun showMultiplayerFragment() {
        val gameFragment = fragmentManager!!.findFragmentByTag("gameFragment")
        if (gameFragment != null) {
            fragmentManager!!.beginTransaction().remove(gameFragment).commit()
        }

        val multiplayerFragment = fragmentManager!!.findFragmentByTag("multiplayerFragment")
        fragmentManager!!.beginTransaction().show(multiplayerFragment!!).commit()
    }

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

    private fun setAllViews(view: View) {
        yourScoreText = view.findViewById(R.id.textViewMyScore)
        opponentScoreText = view.findViewById(R.id.textViewOpponentScore)
        totalScoreText = view.findViewById(R.id.textViewTotalScore)
        findOpponentButton = view.findViewById(R.id.buttonConnFindOpponent)
        btnContinueGame = view.findViewById(R.id.buttonContinueMultiGame)
        loadingPanel = view.findViewById(R.id.waitForOpponentLoadingPanel)
    }

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

     private fun resetDiceMultiplayerFragment() {
        findOpponentButton!!.text = getString(R.string.dice_multi_find_opponent)
        findOpponentButton!!.isEnabled = true
        findOpponentButton!!.visibility = View.VISIBLE

        loadingPanel!!.visibility = View.GONE
        setViewsVisibility(false)


        myTotalScore = 0
        opponentTotalScore = 0
    }

    private fun startGameFragment() {
        val diceGameFragment = DiceGameFragment()

        val bundle = Bundle()
        bundle.putBoolean("isMultiplayer", true)
        diceGameFragment.arguments = bundle

        fragmentManager!!.beginTransaction().add(
                com.example.user.gambling.R.id.fragmentContainer,
                diceGameFragment,
                "gameFragment").commit()

        val multiplayerFragment = fragmentManager!!.findFragmentByTag("multiplayerFragment")
        fragmentManager!!.beginTransaction().hide(multiplayerFragment!!).commit()
    }

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