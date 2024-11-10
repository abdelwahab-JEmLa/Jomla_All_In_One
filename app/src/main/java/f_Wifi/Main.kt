package f_Wifi

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConnectionUiState(
    val connectionStatus: String = "Déconnecté",
    val isConnected: Boolean = false,
    val isHost: Boolean = false,
    val error: String? = null,
    val messages: List<String> = emptyList()
)

class ConnectionManager(  //TODO cree moi des logs par imojis
    private val context: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    private var endpointId: String? = null
    private val serviceId = "com.example.clientjetpack"
    private val strategy = Strategy.P2P_STAR

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d(TAG, "Connection initiated with: ${info.endpointName}")
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            updateConnectionStatus("Connexion en cours avec ${info.endpointName}...")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    this@ConnectionManager.endpointId = endpointId
                    updateConnectionStatus("Connecté")
                    _uiState.update { it.copy(isConnected = true) }
                    sendMessage("Connection established")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    handleError("Connexion rejetée")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    handleError("Erreur de connexion")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            this@ConnectionManager.endpointId = null
            updateConnectionStatus("Déconnecté")
            _uiState.update { it.copy(isConnected = false) }
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val message = String(payload.asBytes()!!)
                Log.d(TAG, "Message reçu: $message")
                _uiState.update {
                    it.copy(messages = it.messages + message)
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Handle transfer status updates if needed
        }
    }

    fun startAsHost() {
        viewModelScope.launch {

            _uiState.update { it.copy(isHost = true) }
            val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()

            Nearby.getConnectionsClient(context)
                .startAdvertising(
                    "Host Device",
                    serviceId,
                    connectionLifecycleCallback,
                    advertisingOptions
                )
                .addOnSuccessListener {
                    updateConnectionStatus("En attente de connexion...")
                }
                .addOnFailureListener { e ->
                    handleError("Erreur de démarrage du mode hôte: ${e.message}")
                }
        }
    }

    fun startAsClient() {
        viewModelScope.launch {

            _uiState.update { it.copy(isHost = false) }
            val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()

            Nearby.getConnectionsClient(context)
                .startDiscovery(
                    serviceId,
                    object : EndpointDiscoveryCallback() {
                        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                            Nearby.getConnectionsClient(context).requestConnection(
                                "Client Device",
                                endpointId,
                                connectionLifecycleCallback
                            )
                        }

                        override fun onEndpointLost(endpointId: String) {
                            Log.d(TAG, "Endpoint perdu: $endpointId")
                        }
                    },
                    discoveryOptions
                )
                .addOnSuccessListener {
                    updateConnectionStatus("Recherche d'appareils...")
                }
                .addOnFailureListener { e ->
                    handleError("Erreur de démarrage de la recherche: ${e.message}")
                }
        }
    }

    fun sendMessage(message: String) {
        endpointId?.let { endpoint ->
            val payload = Payload.fromBytes(message.toByteArray())
            Nearby.getConnectionsClient(context).sendPayload(endpoint, payload)
                .addOnFailureListener { e ->
                    handleError("Erreur d'envoi du message: ${e.message}")
                }
        }
    }

    fun disconnect() {
        Nearby.getConnectionsClient(context).apply {
            stopAdvertising()
            stopDiscovery()
            stopAllEndpoints()
        }
        endpointId = null
        updateConnectionStatus("Déconnecté")
        _uiState.update { it.copy(
            isConnected = false,
            isHost = false
        )}
    }

    private fun updateConnectionStatus(status: String) {
        _uiState.update { it.copy(connectionStatus = status) }
    }

    private fun handleError(error: String) {
        Log.e(TAG, error)
        _uiState.update { it.copy(error = error) }
    }


    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

    companion object {
        private const val TAG = "ConnectionManager"
    }
}
