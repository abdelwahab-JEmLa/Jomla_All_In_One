package f_Wifi

import android.content.Context
import android.os.Build
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class NearbyConnectionService(
    private val context: Context,
    private val onDataReceived: (String) -> Unit // Ajout du callback
) {
    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Searching : ConnectionState()
        data class Connected(val endpointId: String) : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            // Accepter automatiquement la connexion
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    _connectionState.value = ConnectionState.Connected(endpointId)
                }
                else -> {
                    _connectionState.value = ConnectionState.Error("Erreur de connexion")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            _connectionState.value = ConnectionState.Disconnected
        }
    }



    fun startAdvertising(serviceId: String) {
        val options = AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()

        connectionsClient.startAdvertising(
            Build.MODEL, // Nom de l'appareil
            serviceId,
            connectionLifecycleCallback,
            options
        ).addOnFailureListener { e ->
            _connectionState.value = ConnectionState.Error("Erreur publicité: ${e.message}")
        }
    }

    fun startDiscovery(serviceId: String) {
        val options = DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()

        connectionsClient.startDiscovery(
            serviceId,
            object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    connectionsClient.requestConnection(
                        Build.MODEL,
                        endpointId,
                        connectionLifecycleCallback
                    )
                }

                override fun onEndpointLost(endpointId: String) {
                    // Gérer la perte de l'endpoint
                }
            },
            options
        ).addOnFailureListener { e ->
            _connectionState.value = ConnectionState.Error("Erreur découverte: ${e.message}")
        }
    }

    fun sendData(endpointId: String, data: String) {
        val payload = Payload.fromBytes(data.toByteArray())
        connectionsClient.sendPayload(endpointId, payload)
            .addOnFailureListener { e ->
                _connectionState.value = ConnectionState.Error("Erreur envoi: ${e.message}")
            }
    }

    fun stop() {
        connectionsClient.stopAllEndpoints()
        scope.cancel()
    }
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            when (payload.type) {
                Payload.Type.BYTES -> {
                    val data = payload.asBytes()?.let { String(it) }
                    data?.let { onDataReceived(it) } // Appel du callback
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Gestion des mises à jour de transfert si nécessaire
        }
    }
}
