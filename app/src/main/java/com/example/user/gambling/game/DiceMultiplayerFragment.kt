package com.example.user.gambling.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.user.gambling.R
import com.example.user.gambling.connection.Connection
import com.example.user.gambling.connection.PermissionHandler
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import java.nio.charset.StandardCharsets.UTF_8

class DiceMultiplayerFragment : android.support.v4.app.Fragment() {

    private var connection: Connection? = null

    private var score = 0
    private var opponentScore = 0

    private var statusText: TextView? = null
    private var findOpponentButton: Button? = null
    private var scoreText: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dice_multiplayer, container, false)

        statusText = view.findViewById(R.id.textViewConnStatus)
        statusText!!.text = getString(R.string.dice_multi_status_disconnected)

        findOpponentButton = view.findViewById(R.id.buttonConnFindOpponent)
        findOpponentButton!!.setOnClickListener {
            connection!!.findOpponent()
            statusText!!.text = getString(R.string.dice_multi_status_searching)
            findOpponentButton!!.isEnabled = false
        }
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
                connectionLifecycleCallback)
    }

    override fun onPause() {
        super.onPause()
        connection!!.connectionsClient.stopAllEndpoints()
    }

    // Callbacks for receiving payloads
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            opponentScore = String(payload.asBytes()!!, UTF_8).toInt()
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status ==
                    PayloadTransferUpdate.Status.SUCCESS
                    && score != 0
                    && opponentScore != 0) {
                scoreText!!.text = getString(R.string.dice_multi_score, score, opponentScore)
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

                statusText!!.text = getString(R.string.dice_multi_status_connected)
                findOpponentButton!!.isEnabled = false

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
            } else {
                Toast.makeText(
                        activity,
                        getString(R.string.dice_multi_toast_conn_failed),
                        Toast.LENGTH_SHORT).show()
            }
        }

        override fun onDisconnected(endpointId: String) {
            Toast.makeText(
                    activity,
                    getString(R.string.dice_multi_toast_disconnected),
                    Toast.LENGTH_SHORT).show()
        }
    }
}