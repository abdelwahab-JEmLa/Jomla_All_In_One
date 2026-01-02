package Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("StaticFieldLeak")
class WifiTransferDatas(
    private val context: Context,
    val repositorysMainGetter: RepositorysMainGetter,
    private val onPayloadReceiveRaw: (String) -> Unit = {},
) : ViewModel() {
    val appComptComposeRepositoryProtoJuin17 = repositorysMainGetter.repo9AppCompt

    private val _connectionUiState = MutableStateFlow(ConnectionUiState())
    val connectionUiState: StateFlow<ConnectionUiState> = _connectionUiState.asStateFlow()

    @Volatile
    private var endpointId: String? = null
    private val connectionStateMutex = Mutex()

    private val serviceId = "com.example.clientjetpack"
    private val strategy = Strategy.P2P_POINT_TO_POINT

    private val isReconnecting = AtomicBoolean(false)
    private var reconnectionJob: Job? = null
    private var connectionMonitorJob: Job? = null
    private var retryCount = 0
    private val maxRetries = 10
    private val baseRetryDelayMs = 3000L

    private var lastConnectionMode: ConnectionMode = ConnectionMode.NONE

    // Buffer pour les messages en attente
    private val pendingMessages = mutableListOf<String>()
    private val pendingMessagesMutex = Mutex()
    private val maxPendingMessages = 50 // Limite pour éviter la surcharge mémoire

    private enum class ConnectionMode {
        HOST, CLIENT, NONE
    }

    // Helper to atomically check connection and get endpoint
    private suspend fun getConnectionInfo(): Pair<Boolean, String?> {
        return connectionStateMutex.withLock {
            _connectionUiState.value.isConnected to endpointId
        }
    }

    // Helper to atomically update connection state
    private suspend fun setConnectionState(connected: Boolean, endpoint: String?) {
        connectionStateMutex.withLock {
            endpointId = endpoint
            _connectionUiState.update { it.copy(isConnected = connected) }
            Log.d(TAG, "🔄 État connexion mis à jour: isConnected=$connected, endpointId=$endpoint")
        }

        // Si reconnecté, envoyer les messages en attente
        if (connected && endpoint != null) {
            flushPendingMessages()
        }
    }

    // Envoyer tous les messages en attente
    private suspend fun flushPendingMessages() {
        pendingMessagesMutex.withLock {
            if (pendingMessages.isNotEmpty()) {
                Log.d(TAG, "📬 Envoi de ${pendingMessages.size} messages en attente")
                val messagesToSend = pendingMessages.toList()
                pendingMessages.clear()

                messagesToSend.forEach { message ->
                    sendDataInternal(message)
                }
            }
        }
    }

    fun sendOrderToClientDisplayerT(
        orderName: WifiUpdateClientDisplayerStats, data: Any? = null
    ) {
        viewModelScope.launch {
            sendData("${orderName.prefix}$data")
        }
    }

    private fun handle_Text_Payload(payload: String) {
        val parts = payload.split("=", limit = 2)

        if (parts.size == 2) {
            val command = parts[0]
            val data = parts[1]

            when (command) {
                "Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran" -> {
                    appComptComposeRepositoryProtoJuin17.currentAppCompt?.let { currentAppCompt ->
                        appComptComposeRepositoryProtoJuin17.upsert(
                            currentAppCompt.copy(
                                active_ProduitKeyID_Au_DroopDown_PresenterEcran = data
                            )
                        )
                        Log.d(
                            TAG,
                            "✅ Updated active_ProduitKeyID_Au_DroopDown_PresenterEcran to: $data"
                        )
                    } ?: run {
                        Log.e(TAG, "❌ Cannot update: currentAppCompt is null")
                    }
                }

                else -> {
                    Log.d(TAG, "📩 Unhandled command: $command with data: $data")
                }
            }
        } else {
            Log.d(TAG, "📩 Invalid payload format (no '=' separator): $payload")
        }
    }

    private fun handlePayload(payload: String) {
        WifiUpdateClientDisplayerStats.fromPayload(payload)?.let { (messageType, content) ->
            when (messageType) {
                WifiUpdateClientDisplayerStats.FilterProduitsParCatalogueBsonID_ET_Autres_Types -> {
                    appComptComposeRepositoryProtoJuin17.upsert(
                        appComptComposeRepositoryProtoJuin17.currentAppCompt!!.copy(
                            presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId = content
                        )
                    )

                    val relative_Produit = repositorysMainGetter.find_M1Produit_ByKeyID(content)
                    appComptComposeRepositoryProtoJuin17.upsert(
                        appComptComposeRepositoryProtoJuin17.currentAppCompt!!.copy(
                            active_ProduitKeyID_Au_DroopDown_PresenterEcran = if (relative_Produit == null) "" else relative_Produit.keyID
                        )
                    )
                }

                else -> {}
            }
        } ?: Log.d(
            "presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId",
            "📩 Unhandled message received: $payload"
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initiateReconnection() {
        if (isReconnecting.compareAndSet(false, true)) {
            reconnectionJob?.cancel()
            reconnectionJob = viewModelScope.launch {
                try {
                    Log.d(TAG, "🔄 Tentative de reconnexion #${retryCount + 1}")

                    delay(2000)

                    val (isConnected, _) = getConnectionInfo()
                    if (!isConnected) {
                        val backoffDelay = calculateBackoffDelay()
                        delay(backoffDelay)

                        _connectionUiState.update {
                            it.copy(
                                connectionStatus = "Tentative de reconnexion #${retryCount + 1}",
                                reconnectionAttempts = retryCount + 1
                            )
                        }

                        when (lastConnectionMode) {
                            ConnectionMode.HOST -> startAsHost()
                            ConnectionMode.CLIENT -> startAsClient()
                            ConnectionMode.NONE -> {
                                Log.e(TAG, "❌ Aucun mode de connexion précédent connu")
                                handleFinalDisconnection()
                            }
                        }

                        retryCount++

                        _connectionUiState.update {
                            it.copy(
                                lastSuccessfulConnection = System.currentTimeMillis()
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "💥 Erreur lors de la tentative de reconnexion", e)
                    handleError("Échec de la reconnexion: ${e.message}")
                } finally {
                    isReconnecting.set(false)
                }
            }
        }
    }

    private suspend fun shouldAttemptReconnection(): Boolean {
        val (isConnected, _) = getConnectionInfo()
        return !isConnected && retryCount < maxRetries && lastConnectionMode != ConnectionMode.NONE
    }

    @SuppressLint("NewApi")
    private fun handleDisconnection(disconnectedEndpointId: String) {
        viewModelScope.launch {
            connectionStateMutex.withLock {
                if (endpointId == disconnectedEndpointId) {
                    endpointId = null
                    updateConnectionStatus("Déconnecté")
                    _connectionUiState.update {
                        it.copy(
                            isConnected = false,
                            lastSuccessfulConnection = System.currentTimeMillis()
                        )
                    }
                    Log.d(TAG, "🔄 Déconnexion appliquée: endpointId=null, isConnected=false")
                }
            }

            if (shouldAttemptReconnection()) {
                initiateReconnection()
            } else {
                handleFinalDisconnection()
            }
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d(TAG, "🌟 Connexion initiée avec: ${info.endpointName}")
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            updateConnectionStatus("Connexion en cours avec ${info.endpointName}...")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(TAG, "✅ Connexion établie avec succès!")
                    viewModelScope.launch {
                        setConnectionState(connected = true, endpoint = endpointId)
                        updateConnectionStatus("Connecté")
                        retryCount = 0
                        startConnectionMonitoring()
                        sendData("Connection established")
                    }
                }

                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.e(TAG, "🚫 Connexion rejetée")
                    handleConnectionFailure("Connexion rejetée")
                }

                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.e(TAG, "💥 Erreur de connexion")
                    handleConnectionFailure("Erreur de connexion")
                }

                else -> {
                    Log.e(TAG, "❓ Statut de connexion inconnu: ${result.status.statusCode}")
                    handleConnectionFailure("Erreur inconnue")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "👋 Déconnexion détectée: $endpointId")
            handleDisconnection(endpointId)
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                try {
                    val rawMessage = String(payload.asBytes()!!)
                    onPayloadReceiveRaw(rawMessage)

                    Log.d(
                        "presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId",
                        " rawMessage: $rawMessage"
                    )

                    handlePayload(rawMessage)
                    Log.d(TAG, "✉️ Message reçu et traité")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Erreur lors du traitement du payload", e)
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
                    handleTransferFailure()
                }

                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    val progress = if (update.totalBytes > 0) {
                        (update.bytesTransferred * 100 / update.totalBytes)
                    } else 0
                    Log.d(TAG, "⏳ Transfert: $progress%")
                }

                PayloadTransferUpdate.Status.CANCELED -> {
                    Log.d(TAG, "🚫 Transfert annulé")
                    handleTransferFailure()
                }
            }
        }
    }

    private fun handleTransferFailure() {
        viewModelScope.launch {
            val (isConnected, _) = getConnectionInfo()
            if (isConnected) {
                Log.d(TAG, "🔄 Tentative de réenvoi des données...")
            }
        }
    }

    private fun startConnectionMonitoring() {
        connectionMonitorJob?.cancel()
        connectionMonitorJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)
                checkConnectionHealth()
            }
        }
    }

    private fun checkConnectionHealth() {
        viewModelScope.launch {
            val (isConnected, endpoint) = getConnectionInfo()
            if (isConnected && endpoint != null) {
                try {
                    sendData("ping")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Erreur lors de la vérification de la connexion", e)
                    handleConnectionFailure("Perte de connexion détectée")
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun handleConnectionFailure(reason: String) {
        Log.e(TAG, "⚠️ Échec de connexion: $reason")
        viewModelScope.launch {
            if (!isReconnecting.get() && retryCount < maxRetries) {
                initiateReconnection()
            } else if (retryCount >= maxRetries) {
                Log.e(TAG, "🛑 Nombre maximum de tentatives atteint")
                handleFinalDisconnection()
            }
        }
    }

    private fun calculateBackoffDelay(): Long {
        return baseRetryDelayMs * (1L shl retryCount.coerceAtMost(5))
    }

    private fun handleFinalDisconnection() {
        Log.d(TAG, "👋 Déconnexion finale")
        disconnect()
        _connectionUiState.update {
            it.copy(
                isConnected = false,
                error = "Connexion perdue après plusieurs tentatives",
                connectionStatus = "Déconnecté définitivement"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() {
        viewModelScope.launch {
            Log.d(TAG, "🏠 Démarrage en mode hôte...")
            if (!checkRequiredPermissions()) {
                handleError("Permissions manquantes")
                return@launch
            }

            lastConnectionMode = ConnectionMode.HOST
            _connectionUiState.update { it.copy(isHostPhone = true) }

            try {
                val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()

                Nearby.getConnectionsClient(context).startAdvertising(
                    "Host Device", serviceId, connectionLifecycleCallback, advertisingOptions
                ).addOnSuccessListener {
                    Log.d(TAG, "📡 Mode hôte activé")
                    updateConnectionStatus("En attente de connexion...")
                }.addOnFailureListener { e ->
                    Log.e(TAG, "💥 Échec du mode hôte", e)
                    handleConnectionFailure("Erreur de démarrage du mode hôte: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 Exception lors du démarrage du mode hôte", e)
                handleConnectionFailure(e.message ?: "Erreur inconnue")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() {
        viewModelScope.launch {
            Log.d(TAG, "👥 Démarrage en mode client...")
            if (!checkRequiredPermissions()) {
                handleError("Permissions manquantes")
                return@launch
            }

            lastConnectionMode = ConnectionMode.CLIENT
            _connectionUiState.update { it.copy(isHostPhone = false) }

            try {
                val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()

                Nearby.getConnectionsClient(context).startDiscovery(
                    serviceId, endpointDiscoveryCallback, discoveryOptions
                ).addOnSuccessListener {
                    Log.d(TAG, "🔍 Recherche démarrée")
                    updateConnectionStatus("Recherche d'appareils...")
                }.addOnFailureListener { e ->
                    Log.e(TAG, "💥 Échec de la recherche", e)
                    handleConnectionFailure("Erreur de démarrage de la recherche: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 Exception lors du démarrage de la recherche", e)
                handleConnectionFailure(e.message ?: "Erreur inconnue")
            }
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "🔍 Endpoint trouvé: ${info.endpointName}")

            Nearby.getConnectionsClient(context).requestConnection(
                "ClientAchteur Device", endpointId, connectionLifecycleCallback
            ).addOnSuccessListener {
                Log.d(TAG, "✅ Demande de connexion envoyée")
            }.addOnFailureListener { e ->
                Log.e(TAG, "❌ Échec de la demande de connexion", e)
                handleConnectionFailure("Erreur de connexion: ${e.message}")
            }
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "💨 Endpoint perdu: $endpointId")
        }
    }

    fun sendData(data: Any) {
        viewModelScope.launch {
            val dataStr = data.toString()
            Log.d("sendData", "📤 sendData() appelé avec: $dataStr")

            // CRITICAL: Atomically check both isConnected and endpointId together
            val (isConnected, endpoint) = getConnectionInfo()

            Log.d("sendData", "🔗 État atomique: isConnected=$isConnected, endpointId=$endpoint")

            if (!isConnected) {
                // Ne pas ignorer les messages importants - les mettre en buffer
                if (!dataStr.equals("ping", ignoreCase = true) &&
                    !dataStr.equals("Connection established", ignoreCase = true)) {

                    pendingMessagesMutex.withLock {
                        if (pendingMessages.size < maxPendingMessages) {
                            pendingMessages.add(dataStr)
                            Log.d("sendData", "📥 Message mis en buffer (${pendingMessages.size} en attente): $dataStr")
                        } else {
                            Log.e("sendData", "⚠️ Buffer plein, message ignoré: $dataStr")
                        }
                    }
                } else {
                    Log.d("sendData", "⏭️ Message système ignoré (non connecté): $dataStr")
                }
                return@launch
            }

            if (endpoint == null) {
                Log.e("sendData", "❌ INCOHÉRENCE: isConnected=true mais endpointId=null!")
                Log.e("sendData", "❌ Force déconnexion pour corriger l'état. Données: $dataStr")
                // Force fix the inconsistent state
                setConnectionState(connected = false, endpoint = null)

                // Buffer le message si important
                if (!dataStr.equals("ping", ignoreCase = true) &&
                    !dataStr.equals("Connection established", ignoreCase = true)) {
                    pendingMessagesMutex.withLock {
                        if (pendingMessages.size < maxPendingMessages) {
                            pendingMessages.add(dataStr)
                            Log.d("sendData", "📥 Message bufferisé après incohérence: $dataStr")
                        }
                    }
                }
                return@launch
            }

            // SIMPLE: Vérifier et envoyer les messages en attente SI connecté et SI ce n'est pas déjà un message bufferisé
            val messagesCount = pendingMessagesMutex.withLock { pendingMessages.size }
            if (messagesCount > 0) {
                Log.d("sendData", "🔔 ${messagesCount} messages en attente détectés, envoi immédiat...")
                flushPendingMessages()
            }

            // Puis envoyer le message actuel
            sendDataInternal(dataStr, endpoint)
        }
    }

    // Fonction interne pour l'envoi réel
    private suspend fun sendDataInternal(dataStr: String, endpoint: String? = null) {
        val actualEndpoint = endpoint ?: connectionStateMutex.withLock { endpointId } ?: run {
            Log.e("sendData", "❌ Aucun endpoint disponible pour: $dataStr")
            return
        }

        try {
            val payload = Payload.fromBytes(dataStr.toByteArray())

            Nearby.getConnectionsClient(context).sendPayload(actualEndpoint, payload)
                .addOnSuccessListener {
                    Log.d("sendData", "✅ Données envoyées avec succès: $dataStr")
                }
                .addOnFailureListener { e ->
                    Log.e("sendData", "❌ Échec de l'envoi des données: $dataStr", e)

                    // Re-buffer le message si l'envoi échoue et que ce n'est pas un ping
                    if (!dataStr.equals("ping", ignoreCase = true) &&
                        !dataStr.equals("Connection established", ignoreCase = true)) {
                        viewModelScope.launch {
                            pendingMessagesMutex.withLock {
                                if (!pendingMessages.contains(dataStr) && pendingMessages.size < maxPendingMessages) {
                                    pendingMessages.add(dataStr)
                                    Log.d("sendData", "🔄 Message re-bufferisé après échec: $dataStr")
                                }
                            }
                        }
                    }
                    handleTransferFailure()
                }
        } catch (e: Exception) {
            Log.e("sendData", "❌ Exception lors de l'envoi des données: $dataStr", e)
            handleTransferFailure()
        }
    }

    fun disconnect() {
        Log.d(TAG, "🔌 Déconnection...")
        connectionMonitorJob?.cancel()
        reconnectionJob?.cancel()

        viewModelScope.launch {
            try {
                Nearby.getConnectionsClient(context).apply {
                    stopAdvertising()
                    stopDiscovery()
                    stopAllEndpoints()
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erreur lors de la déconnexion", e)
            }

            setConnectionState(connected = false, endpoint = null)

            // Vider le buffer des messages en attente
            pendingMessagesMutex.withLock {
                if (pendingMessages.isNotEmpty()) {
                    Log.d(TAG, "🗑️ ${pendingMessages.size} messages en attente supprimés")
                    pendingMessages.clear()
                }
            }

            lastConnectionMode = ConnectionMode.NONE
            retryCount = 0
            isReconnecting.set(false)

            _connectionUiState.update {
                it.copy(
                    isHostPhone = false,
                    connectionStatus = "Déconnecté",
                    error = null
                )
            }

            Log.d(TAG, "👋 Déconnexion terminée")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkRequiredPermissions(): Boolean {
        val missingPermissions = requiredPermissions.filter { permission ->
            val isGranted = ContextCompat.checkSelfPermission(
                context, permission
            ) != PackageManager.PERMISSION_GRANTED

            Log.d(TAG, "🔍 Permission $permission: ${if (!isGranted) "MANQUANTE" else "OK"}")
            isGranted
        }

        if (missingPermissions.isNotEmpty()) {
            Log.e(TAG, "❌ Permissions manquantes: ${missingPermissions.joinToString()}")
            return false
        }

        Log.d(TAG, "✅ Toutes les permissions sont accordées")
        return true
    }

    private fun updateConnectionStatus(status: String) {
        Log.d(TAG, "📊 Status: $status")
        _connectionUiState.update {
            it.copy(
                connectionStatus = status,
                error = null
            )
        }
    }

    private fun handleError(error: String) {
        Log.e(TAG, "⚠️ Erreur: $error")
        _connectionUiState.update {
            it.copy(
                error = error, connectionStatus = "Erreur: $error"
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "🧹 Nettoyage du ViewModel")
        disconnect()
        connectionMonitorJob?.cancel()
        reconnectionJob?.cancel()
    }

    companion object {
        private const val TAG = "WifiTransferDatas"
    }
}

data class ConnectionUiState(
    val connectionStatus: String = "Déconnecté",
    val isConnected: Boolean = false,
    val isHostPhone: Boolean = false,
    val error: String? = null,
    val lastSuccessfulConnection: Long? = null,
    val reconnectionAttempts: Int = 0
)

enum class WifiUpdateClientDisplayerStats(val prefix: String) {
    ClientMainGridScrollPosition("ClientMainGridScrollPosition"),
    ClientWindowsLazyRowSupColorsScrolle("ClientWindowsLazyRowSupColorsScrolle"),
    ClientWindowsDisplayedProductId("ClientWindowsDisplayedProductId"),
    ClientWindowsSelectedColorId("clientWindowsSelectedColorId"),
    DISMISS_PRODUCT_INFO("DismissWindowsInfosProduct"),
    WindowsPickerDisplayedQuantity("WindowsPickerDisplayedQuantity"),
    SearchWindowsDisplaye("SearchWindowsDisplaye"),
    NewArregmentColorsJsonStruct("NewArregmentColorsJsonStruct"),
    FilterProduitsParCatalogueBsonID_ET_Autres_Types("FilterProduitsParCatalogueBsonID_ET_Autres_Types"),
    Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran("Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran");

    companion object {
        fun fromPayload(payload: String): Pair<WifiUpdateClientDisplayerStats, String>? {
            return entries.firstOrNull { payload.startsWith(it.prefix) }?.let {
                it to payload.removePrefix(it.prefix)
            }
        }
    }
}
