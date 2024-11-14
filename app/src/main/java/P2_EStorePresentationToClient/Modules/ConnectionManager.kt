package P2_EStorePresentationToClient.Modules

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

class ConnectionManager(
    private val context: Context,
    onPayloadReceiveRaw: (String) -> Unit,
) : ViewModel() {
    private val _connectionUiState = MutableStateFlow(ConnectionUiState())
    val connectionUiState: StateFlow<ConnectionUiState> = _connectionUiState.asStateFlow()

    private var endpointId: String? = null
    private val serviceId = "com.example.clientjetpack"
    private val strategy = Strategy.P2P_STAR

    // Update payloadCallback to handle both string and integer messages
    fun sendOrder(name: String, data: Any) {
        when (name) {
            "idProdect" -> {
                if (data is Long) {
                    sendData("PRODUCT:$data")
                    Log.d(TAG, "📤 Product ID sent: $data")
                } else {
                    Log.e(TAG, "❌ Invalid data type for product ID: ${data.javaClass}")
                }
            }
            else -> {
                Log.e(TAG, "❌ Unknown order type: $name")
            }
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val rawMessage = String(payload.asBytes()!!)
                onPayloadReceiveRaw(rawMessage)

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

    fun sendData(data: Any) {
        endpointId?.let { endpoint ->
            val payload = when (data) {
                is String -> {
                    Log.d(TAG, "📤 Envoi du message texte: $data")
                    Payload.fromBytes(data.toByteArray())
                }
                is Int -> {
                    Log.d(TAG, "📤 Envoi de l'entier: $data")
                    Payload.fromBytes("INT:$data".toByteArray())
                }
                else -> {
                    Log.e(TAG, "❌ Type de données non supporté: ${data.javaClass}")
                    return
                }
            }

            Nearby.getConnectionsClient(context).sendPayload(endpoint, payload)
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Données envoyées avec succès")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "❌ Échec de l'envoi: ${e.message}")
                    handleError("Erreur d'envoi des données: ${e.message}")
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
        _connectionUiState.update { it.copy(
            isConnected = false,
            isHostPhone = false
        )}
        Log.d(TAG, "👋 Déconnexion terminée")
    }

    // Liste exhaustive des permissions nécessaires selon la version d'Android
    private val requiredPermissions = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.NEARBY_WIFI_DEVICES
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        else -> {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    private fun checkRequiredPermissions(): Boolean {
        val missingPermissions = requiredPermissions.filter { permission ->
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED

            // Log pour chaque permission
            Log.d(TAG, "Permission $permission: ${if (isGranted) "GRANTED" else "DENIED"}")

            !isGranted
        }

        if (missingPermissions.isNotEmpty()) {
            Log.e(TAG, "Permissions manquantes: ${missingPermissions.joinToString()}")
            return false
        }

        return true
    }

    fun startAsClient() {
        viewModelScope.launch {
            Log.d(TAG, "Démarrage de la recherche...")

            if (!checkRequiredPermissions()) {
                val error = "Permissions manquantes pour la découverte Nearby"
                Log.e(TAG, error)
                handleError(error)
                return@launch
            }

            Log.d(TAG, "Toutes les permissions sont accordées, démarrage de la découverte...")
            _connectionUiState.update { it.copy(isHostPhone = false) }

            try {
                val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()

                Nearby.getConnectionsClient(context)
                    .startDiscovery(
                        serviceId,
                        endpointDiscoveryCallback,
                        discoveryOptions
                    )
                    .addOnSuccessListener {
                        Log.d(TAG, "Découverte démarrée avec succès")
                        updateConnectionStatus("Recherche d'appareils...")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Erreur lors du démarrage de la découverte", e)
                        handleError("Erreur de démarrage de la recherche: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception lors du démarrage de la découverte", e)
                handleError("Exception lors du démarrage de la découverte: ${e.message}")
            }
        }
    }


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
                    _connectionUiState.update { it.copy(isConnected = true) }
                    sendData("Connection established")
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
            _connectionUiState.update { it.copy(isConnected = false) }
        }
    }

    fun startAsHost() {
        viewModelScope.launch {
            Log.d(TAG, "🏠 Démarrage en mode hôte...")
            if (!checkRequiredPermissions()) {
                handleError("Permissions de localisation manquantes")
                return@launch
            }

            _connectionUiState.update { it.copy(isHostPhone = true) }
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



    private fun updateConnectionStatus(status: String) {
        Log.d(TAG, "📊 Status: $status")
        _connectionUiState.update { it.copy(connectionStatus = status) }
    }

    private fun handleError(error: String) {
        Log.e(TAG, "⚠️ Erreur: $error")
        _connectionUiState.update { it.copy(error = error) }
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
data class ConnectionUiState(
    val connectionStatus: String = "Déconnecté",
    val isConnected: Boolean = false,
    val isHostPhone: Boolean = false,
    val error: String? = null,
    val messages: List<String> = emptyList(),
    val scrollPosition: Int? = 0,
)
