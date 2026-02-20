package Application2.App.Base.Modules

import Application2.App.Base.Repository.RepositorysMainGetter_app2
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

data class ProductDisplayController(
    val mainGridScrollPosition: Int = 0,
    var expanded_M3CouleurProduitInfos: M3CouleurProduitInfos? = null,
    var expanded_M1Produit: M01Produit? = null,
    val newArregmentColorsJsonStruct: String = "",
    val clientWindowsDisplayedProductId: Long? = null,
    val searchWindowsDisplaye: String = "",
    val clientWindowsPickerDisplayedQuantity: Int = 0,
    val clientWindowsSelectedColorId: Long = 0,
    val clientWindowsLazyRowSupColorsScroll: Int = 0,
    val filterProduitsParCatalogueBsonID: String = "",
    val isConnected: Boolean = false,
    val connectionStatus: String = "Déconnecté",
    val isHostPhone: Boolean = false,
    val switchRoles: Boolean = true,
    val testMessageByWifi: String = "",
    val error: String? = null,
)

/**
 * Plain injectable class — NOT a ViewModel.
 * Inject into ViewModel_MainFragment and pass viewModelScope as [coroutineScope].
 */
@SuppressLint("StaticFieldLeak")
class WifiTransferDatas_app2(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    var list_M1Produit: List<M01Produit>,
    var list_M3CouleurProduit: List<M3CouleurProduitInfos>,
    val repositorysMainGetter_app2: RepositorysMainGetter_app2,
) {
    private val _state = MutableStateFlow(ProductDisplayController())
    val state: StateFlow<ProductDisplayController> = _state.asStateFlow()

    private var endpointId: String? = null
    private val serviceId = "com.example.clientjetpack"
    private val strategy = Strategy.P2P_POINT_TO_POINT
    private val isReconnecting = AtomicBoolean(false)
    private var reconnectionJob: Job? = null
    private var connectionMonitorJob: Job? = null
    private var retryCount = 0
    private val maxRetries = 10
    private val baseRetryDelayMs = 3000L
    private var lastConnectionMode = ConnectionMode.NONE

    private enum class ConnectionMode { HOST, CLIENT, NONE }

    init {
        coroutineScope.launch {
            _state.collect { s ->
                val shouldHide = !s.isHostPhone && s.isConnected
                val cur = repositorysMainGetter_app2.active_Central_Values
                if (cur.hide_prix_lence_vent_buttons != shouldHide)
                    repositorysMainGetter_app2.update_ActiveCentralValues_app2(
                        cur.copy(hide_prix_lence_vent_buttons = shouldHide)
                    )
            }
        }
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) {
        coroutineScope.launch { sendData("$orderName$data") }
    }

    fun sendOrderToClientDisplayerT(order: WifiUpdateClientDisplayerStats_app2, data: Any? = null) {
        coroutineScope.launch { sendData("${order.prefix}$data") }
    }

    fun updateTypePhone(isHost: Boolean = false) {
        _state.update { it.copy(isHostPhone = isHost) }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() {
        coroutineScope.launch {
            if (!checkRequiredPermissions()) { handleError("Permissions manquantes"); return@launch }
            lastConnectionMode = ConnectionMode.HOST
            _state.update { it.copy(isHostPhone = true) }
            try {
                Nearby.getConnectionsClient(context).startAdvertising(
                    "Host Device", serviceId, connectionLifecycleCallback,
                    AdvertisingOptions.Builder().setStrategy(strategy).build()
                ).addOnSuccessListener {
                    updateConnectionStatus("En attente de connexion...")
                }.addOnFailureListener { e -> handleConnectionFailure("Erreur hôte: ${e.message}") }
            } catch (e: Exception) { handleConnectionFailure(e.message ?: "Erreur inconnue") }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() {
        coroutineScope.launch {
            if (!checkRequiredPermissions()) { handleError("Permissions manquantes"); return@launch }
            lastConnectionMode = ConnectionMode.CLIENT
            _state.update { it.copy(isHostPhone = false) }
            try {
                Nearby.getConnectionsClient(context).startDiscovery(
                    serviceId, endpointDiscoveryCallback,
                    DiscoveryOptions.Builder().setStrategy(strategy).build()
                ).addOnSuccessListener {
                    updateConnectionStatus("Recherche d'appareils...")
                }.addOnFailureListener { e -> handleConnectionFailure("Erreur recherche: ${e.message}") }
            } catch (e: Exception) { handleConnectionFailure(e.message ?: "Erreur inconnue") }
        }
    }

    fun disconnect() {
        connectionMonitorJob?.cancel()
        reconnectionJob?.cancel()
        try {
            Nearby.getConnectionsClient(context).apply {
                stopAdvertising(); stopDiscovery(); stopAllEndpoints()
            }
        } catch (e: Exception) { Log.e(TAG, "❌ Erreur déconnexion", e) }
        endpointId = null
        lastConnectionMode = ConnectionMode.NONE
        retryCount = 0
        isReconnecting.set(false)
        _state.update { it.copy(isConnected = false, connectionStatus = "Déconnecté", error = null) }
    }

    /** Call from ViewModel_MainFragment.onCleared() */
    fun cancel() {
        disconnect()
        Log.d(TAG, "🧹 WifiTransferDatas_app2 nettoyé")
    }

    // -----------------------------------------------------------------------
    // sendData
    // -----------------------------------------------------------------------

    fun sendData(data: Any) {
        endpointId?.let { ep ->
            try {
                val payload = when (data) {
                    is String -> Payload.fromBytes(data.toByteArray())
                    else -> { Log.e(TAG, "❌ Type non supporté: ${data.javaClass}"); return }
                }
                Nearby.getConnectionsClient(context).sendPayload(ep, payload)
                    .addOnFailureListener { Log.w(TAG, "⚠️ Échec envoi: $data") }
            } catch (e: Exception) { Log.e(TAG, "❌ Exception envoi: $data", e) }
        } ?: Log.e(TAG, "❌ Pas de endpoint. Données perdues: $data")
    }

    // -----------------------------------------------------------------------
    // Payload handling
    // -----------------------------------------------------------------------

    private fun handlePayload(payload: String) {
        WifiUpdateClientDisplayerStats_app2.fromPayload(payload)
            ?.let { (type, content) ->
                when (type) {
                    WifiUpdateClientDisplayerStats_app2.ClientMainGridScrollPosition ->
                        _state.update { it.copy(mainGridScrollPosition = content.toIntOrNull() ?: it.mainGridScrollPosition) }

                    WifiUpdateClientDisplayerStats_app2.ClientWindowsDisplayedProductId ->
                        _state.update { it.copy(clientWindowsDisplayedProductId = content.toLongOrNull()) }

                    WifiUpdateClientDisplayerStats_app2.DISMISS_PRODUCT_INFO ->
                        _state.update { it.copy(clientWindowsDisplayedProductId = null, searchWindowsDisplaye = "") }

                    WifiUpdateClientDisplayerStats_app2.SearchWindowsDisplaye ->
                        _state.update { it.copy(searchWindowsDisplaye = content) }

                    WifiUpdateClientDisplayerStats_app2.WindowsPickerDisplayedQuantity ->
                        _state.update { it.copy(clientWindowsPickerDisplayedQuantity = content.toIntOrNull() ?: it.clientWindowsPickerDisplayedQuantity) }

                    WifiUpdateClientDisplayerStats_app2.ClientWindowsSelectedColorId ->
                        _state.update { it.copy(clientWindowsSelectedColorId = content.toLongOrNull() ?: it.clientWindowsSelectedColorId) }

                    WifiUpdateClientDisplayerStats_app2.ClientWindowsLazyRowSupColorsScrolle ->
                        _state.update { it.copy(clientWindowsLazyRowSupColorsScroll = content.toIntOrNull() ?: it.clientWindowsLazyRowSupColorsScroll) }

                    WifiUpdateClientDisplayerStats_app2.NewArregmentColorsJsonStruct ->
                        _state.update { it.copy(newArregmentColorsJsonStruct = content) }

                    WifiUpdateClientDisplayerStats_app2.FilterProduitsParCatalogueBsonID_ET_Autres_Types ->
                        _state.update { it.copy(filterProduitsParCatalogueBsonID = content) }

                    WifiUpdateClientDisplayerStats_app2.Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran -> {
                        val couleur = list_M3CouleurProduit.find { it.keyID == content }
                        if (couleur != null) toggleExpandedCouleur(couleur)
                        else Log.e(TAG, "❌ Couleur introuvable pour keyID: $content")
                    }

                    else -> Log.d(TAG, "📩 Message non géré: $type | $payload")
                }
            } ?: Log.d(TAG, "📩 Payload non reconnu: $payload")
    }

    fun toggleExpandedCouleur(couleur: M3CouleurProduitInfos) {
        val cur = repositorysMainGetter_app2.active_Central_Values
        val newColor = if (cur.expanded_M3CouleurProduitInfos?.keyID == couleur.keyID) null else couleur
        val newProduit = newColor?.let {
            list_M1Produit.find { p -> p.keyID == couleur.parentBProduitInfosKeyID }
        }
        repositorysMainGetter_app2.update_ActiveCentralValues_app2(
            cur.copy(expanded_M3CouleurProduitInfos = newColor, expanded_M1Produit = newProduit)
        )
    }

    // -----------------------------------------------------------------------
    // Connection callbacks
    // -----------------------------------------------------------------------

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            updateConnectionStatus("Connexion en cours avec ${info.endpointName}...")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    this@WifiTransferDatas_app2.endpointId = endpointId
                    _state.update { it.copy(isConnected = true) }
                    updateConnectionStatus("Connecté")
                    retryCount = 0
                    startConnectionMonitoring()
                    sendData("Connection established")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> handleConnectionFailure("Connexion rejetée")
                ConnectionsStatusCodes.STATUS_ERROR               -> handleConnectionFailure("Erreur de connexion")
                else -> handleConnectionFailure("Code inconnu: ${result.status.statusCode}")
            }
        }

        override fun onDisconnected(endpointId: String) {
            if (this@WifiTransferDatas_app2.endpointId == endpointId) {
                this@WifiTransferDatas_app2.endpointId = null
                _state.update { it.copy(isConnected = false, connectionStatus = "Déconnecté") }
                if (retryCount < maxRetries && lastConnectionMode != ConnectionMode.NONE) initiateReconnection()
                else handleFinalDisconnection()
            }
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Nearby.getConnectionsClient(context)
                .requestConnection("ClientAchteur Device", endpointId, connectionLifecycleCallback)
                .addOnFailureListener { e -> handleConnectionFailure("Erreur connexion: ${e.message}") }
        }
        override fun onEndpointLost(endpointId: String) = Unit
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                try { handlePayload(String(payload.asBytes()!!)) }
                catch (e: Exception) { Log.e(TAG, "❌ Erreur traitement payload", e) }
            }
        }
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) = Unit
    }

    // -----------------------------------------------------------------------
    // Monitoring & reconnection
    // -----------------------------------------------------------------------

    private fun startConnectionMonitoring() {
        connectionMonitorJob?.cancel()
        connectionMonitorJob = coroutineScope.launch {
            while (isActive) { delay(5000); endpointId?.let { sendData("ping") } }
        }
    }

    @SuppressLint("NewApi")
    private fun initiateReconnection() {
        if (isReconnecting.compareAndSet(false, true)) {
            reconnectionJob?.cancel()
            reconnectionJob = coroutineScope.launch {
                try {
                    delay(2000 + baseRetryDelayMs * (1L shl retryCount.coerceAtMost(5)))
                    updateConnectionStatus("Reconnexion #${retryCount + 1}…")
                    when (lastConnectionMode) {
                        ConnectionMode.HOST   -> startAsHost()
                        ConnectionMode.CLIENT -> startAsClient()
                        ConnectionMode.NONE   -> handleFinalDisconnection()
                    }
                    retryCount++
                } catch (e: Exception) {
                    handleError("Échec reconnexion: ${e.message}")
                } finally {
                    isReconnecting.set(false)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun handleConnectionFailure(reason: String) {
        Log.e(TAG, "⚠️ $reason")
        if (!isReconnecting.get() && retryCount < maxRetries) initiateReconnection()
        else if (retryCount >= maxRetries) handleFinalDisconnection()
    }

    private fun handleFinalDisconnection() {
        disconnect()
        _state.update { it.copy(error = "Connexion perdue après plusieurs tentatives", connectionStatus = "Déconnecté définitivement") }
    }

    private fun updateConnectionStatus(status: String) {
        _state.update { it.copy(connectionStatus = status, error = null) }
    }

    private fun handleError(error: String) {
        _state.update { it.copy(error = error, connectionStatus = "Erreur: $error") }
    }

    // -----------------------------------------------------------------------
    // Permissions
    // -----------------------------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requiredPermissions = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES,
        )
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> arrayOf(
            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        else -> arrayOf(
            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkRequiredPermissions(): Boolean {
        val missing = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        return missing.isEmpty().also { ok ->
            if (!ok) Log.e(TAG, "❌ Permissions manquantes: ${missing.joinToString()}")
        }
    }

    companion object {
        private const val TAG = "WifiTransferDatas_app2"
    }
}

// ---------------------------------------------------------------------------

enum class WifiUpdateClientDisplayerStats_app2(val prefix: String) {
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
        fun fromPayload(payload: String): Pair<WifiUpdateClientDisplayerStats_app2, String>? =
            entries.firstOrNull { payload.startsWith(it.prefix) }
                ?.let { it to payload.removePrefix(it.prefix) }
    }
}
