package com.example.user.gambling.connection

import android.content.Context
import com.google.android.gms.nearby.connection.*
import java.nio.charset.StandardCharsets

/**
 * Handles a huge part of the connection process by using Nearby Connections API.
 * connectionLifecycleCallback and payloadCallback are supposed to be handled in the specific
 * location and not in this class.
 * @param connectionsClient provided by the API handles all connections and everything related to it.
 * @param connectionLifecycleCallback should at least handle onConnectionInitiated().
 * @param username unique identifier for the device.
 */
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

    /**
     * Callbacks for finding other devices.
     * If another device is found, onEndpointFound is called and a connection is requested.
     */
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            connectionsClient.requestConnection(username, endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {}
    }

    /**
     * Method to start scanning for advertisements.
     * One device can advertise and scan at the same time.
     */
    private fun startDiscovery() {
        connectionsClient.startDiscovery(
                context.packageName, endpointDiscoveryCallback,
                DiscoveryOptions.Builder().setStrategy(strategy).build())
    }

    /**
     * Method to start advertising, so that the device can be found by a scanning device.
     * One device can advertise and scan at the same time.
     */
    private fun startAdvertising() {
        connectionsClient.startAdvertising(
                username, context.packageName, connectionLifecycleCallback,
                AdvertisingOptions.Builder().setStrategy(strategy).build())
    }
}