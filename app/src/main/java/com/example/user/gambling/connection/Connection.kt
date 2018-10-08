package com.example.user.gambling.connection

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.connection.*
import java.nio.charset.StandardCharsets

class Connection(
        val connectionsClient: ConnectionsClient,
        private val context: Context,
        private val connectionLifecycleCallback: ConnectionLifecycleCallback,
        private val username: String) {

    private val strategy = Strategy.P2P_STAR

    var opponentEndpointId: String? = null
    var opponentName: String? = null

    fun sendScore(myResult: Int) {
        connectionsClient.sendPayload(
                opponentEndpointId!!,
                Payload.fromBytes(myResult.toString().toByteArray(StandardCharsets.UTF_8)))
    }

    fun findOpponent() {
        startAdvertising()
        startDiscovery()
    }

    fun stopOpponentSearch() {
        connectionsClient.stopDiscovery()
        connectionsClient.stopAdvertising()
    }

    // Callbacks for finding other devices
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.i(ContentValues.TAG, "onEndpointFound: endpoint found, connecting")
            connectionsClient.requestConnection(username, endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {}
    }

    private fun startDiscovery() {
        connectionsClient.startDiscovery(
                context.packageName, endpointDiscoveryCallback,
                DiscoveryOptions.Builder().setStrategy(strategy).build())
    }

    private fun startAdvertising() {
        connectionsClient.startAdvertising(
                username, context.packageName, connectionLifecycleCallback,
                AdvertisingOptions.Builder().setStrategy(strategy).build())
    }
}