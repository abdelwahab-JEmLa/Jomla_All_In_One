package f_Wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
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

class ConnectionManager(
    private val context: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    private var endpointId: String? = null
    private val serviceId = "com.example.clientjetpack"
    private val strategy = Strategy.P2P_STAR

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "🔍 Endpoint trouvé: ${info.endpointName}")
            Log.d(TAG, "🤝 Tentative de connexion à: $endpointId")
            Nearby.getConnectionsClient(context).requestConnection(
                "Client Device",
                endpointId,
                connectionLifecycleCallback
            ).addOnSuccessListener {
                Log.d(TAG, "✅ Demande de connexion envoyée")
            }.addOnFailureListener { e ->
                Log.e(TAG, "❌ Échec de la demande de connexion: ${e.message}")
            }
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "💨 Endpoint perdu: $endpointId")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d(TAG, "🌟 Connexion initiée avec: ${info.endpointName}")
            Log.d(TAG, "🔄 Acceptation de la connexion en cours...")
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            updateConnectionStatus("Connexion en cours avec ${info.endpointName}...")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(TAG, "✨ Connexion établie avec succès!")
                    this@ConnectionManager.endpointId = endpointId
                    updateConnectionStatus("Connecté")
                    _uiState.update { it.copy(isConnected = true) }
                    sendMessage("Connection established")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.e(TAG, "🚫 Connexion rejetée")
                    handleError("Connexion rejetée")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.e(TAG, "💥 Erreur de connexion")
                    handleError("Erreur de connexion")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "👋 Déconnexion de: $endpointId")
            this@ConnectionManager.endpointId = null
            updateConnectionStatus("Déconnecté")
            _uiState.update { it.copy(isConnected = false) }
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val message = String(payload.asBytes()!!)
                Log.d(TAG, "📩 Message reçu: $message")
                _uiState.update {
                    it.copy(messages = it.messages + message)
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            when (update.status) {
                PayloadTransferUpdate.Status.SUCCESS -> {
                    Log.d(TAG, "✅ Transfert réussi")
                }
                PayloadTransferUpdate.Status.FAILURE -> {
                    Log.e(TAG, "❌ Échec du transfert")
                }
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    Log.d(TAG, "⏳ Transfert en cours: ${update.bytesTransferred}/${update.totalBytes}")
                }
                PayloadTransferUpdate.Status.CANCELED -> {
                    Log.d(TAG, "🚫 Transfert annulé")
                }
            }
        }
    }

    private fun checkRequiredPermissions(): Boolean {
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val nearbyDevices = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val result = coarseLocation && (fineLocation || nearbyDevices)
        Log.d(TAG, if (result) "✅ Permissions OK" else "⚠️ Permissions manquantes")
        return result
    }

    fun startAsHost() {
        viewModelScope.launch {
            Log.d(TAG, "🏠 Démarrage en mode hôte...")
            if (!checkRequiredPermissions()) {
                handleError("Permissions de localisation manquantes")
                return@launch
            }

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
                    Log.d(TAG, "📡 Mode hôte activé avec succès")
                    updateConnectionStatus("En attente de connexion...")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "💥 Échec du mode hôte: ${e.message}")
                    handleError("Erreur de démarrage du mode hôte: ${e.message}")
                }
        }
    }

    fun startAsClient() {
        viewModelScope.launch {
            Log.d(TAG, "👤 Démarrage en mode client...")
            if (!checkRequiredPermissions()) {
                handleError("Permissions de localisation manquantes")
                return@launch
            }

            _uiState.update { it.copy(isHost = false) }
            val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()

            Nearby.getConnectionsClient(context)
                .startDiscovery(
                    serviceId,
                    endpointDiscoveryCallback,
                    discoveryOptions
                )
                .addOnSuccessListener {
                    Log.d(TAG, "🔍 Recherche démarrée avec succès")
                    updateConnectionStatus("Recherche d'appareils...")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "💥 Échec de la recherche: ${e.message}")
                    handleError("Erreur de démarrage de la recherche: ${e.message}")
                }
        }
    }

    fun sendMessage(message: String) {
        endpointId?.let { endpoint ->
            Log.d(TAG, "📤 Envoi du message: $message")
            val payload = Payload.fromBytes(message.toByteArray())
            Nearby.getConnectionsClient(context).sendPayload(endpoint, payload)
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Message envoyé avec succès")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "❌ Échec de l'envoi: ${e.message}")
                    handleError("Erreur d'envoi du message: ${e.message}")
                }
        }
    }

    fun disconnect() {
        Log.d(TAG, "🔌 Déconnexion en cours...")
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
        Log.d(TAG, "👋 Déconnexion terminée")
    }

    private fun updateConnectionStatus(status: String) {
        Log.d(TAG, "📊 Status: $status")
        _uiState.update { it.copy(connectionStatus = status) }
    }

    private fun handleError(error: String) {
        Log.e(TAG, "⚠️ Erreur: $error")
        _uiState.update { it.copy(error = error) }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "🧹 Nettoyage du ViewModel")
        disconnect()
    }

    companion object {
        private const val TAG = "ConnectionManager"
    }


}
