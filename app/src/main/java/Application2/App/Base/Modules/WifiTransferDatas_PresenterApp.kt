package Application2.App.Base.Modules

import Application2.App.Base.Repository.ActiveCentralValues_app2
import Application4.App.Modules.Wi.Module.Wifi_Messages_Types_NewProto
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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

data class ProductDisplayController_App2(
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
    val isHostPhone: Boolean = true,
    val switchRoles: Boolean = true,
    val testMessageByWifi: String = "",
    val error: String? = null,
)

@SuppressLint("StaticFieldLeak")
class WifiTransferDatas_PresenterApp(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    var list_M1Produit: List<M01Produit>,
    var list_M3CouleurProduit: List<M3CouleurProduitInfos>,
    private val onGetActiveCentralValues: () -> ActiveCentralValues_app2,
    private val onUpdateActiveCentralValues: (ActiveCentralValues_app2) -> Unit,
) {
    private val _state = MutableStateFlow(ProductDisplayController_App2())
    val state: StateFlow<ProductDisplayController_App2> = _state.asStateFlow()

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
                val cur = onGetActiveCentralValues()
                if (cur.hide_prix_lence_vent_buttons != shouldHide)
                    onUpdateActiveCentralValues(cur.copy(hide_prix_lence_vent_buttons = shouldHide))
            }
        }
    }

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) {
        coroutineScope.launch { sendData("$orderName$data") }
    }

    fun sendOrderToClientDisplayerT(order: Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats, data: Any? = null) {
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
        } catch (_: Exception) {}
        endpointId = null
        lastConnectionMode = ConnectionMode.NONE
        retryCount = 0
        isReconnecting.set(false)
        _state.update { it.copy(isConnected = false, connectionStatus = "Déconnecté", error = null) }
    }

    fun cancel() = disconnect()

    fun sendData(data: Any) {
        endpointId?.let { ep ->
            try {
                val payload = when (data) {
                    is String -> Payload.fromBytes(data.toByteArray())
                    else -> return
                }
                Nearby.getConnectionsClient(context).sendPayload(ep, payload)
            } catch (_: Exception) {}
        }
    }

    private fun handlePayload(payload: String) {
        Wifi_Messages_Types_NewProto.fromPayload(payload)
            ?.let { (type, content) ->
                when (type) {
                    Wifi_Messages_Types_NewProto.ClientMainGridScrollPosition ->
                        _state.update { it.copy(mainGridScrollPosition = content.toIntOrNull() ?: it.mainGridScrollPosition) }
                    Wifi_Messages_Types_NewProto.ClientWindowsDisplayedProductId ->
                        _state.update { it.copy(clientWindowsDisplayedProductId = content.toLongOrNull()) }
                    Wifi_Messages_Types_NewProto.DISMISS_PRODUCT_INFO ->
                        _state.update { it.copy(clientWindowsDisplayedProductId = null, searchWindowsDisplaye = "") }
                    Wifi_Messages_Types_NewProto.SearchWindowsDisplaye ->
                        _state.update { it.copy(searchWindowsDisplaye = content) }
                    Wifi_Messages_Types_NewProto.WindowsPickerDisplayedQuantity ->
                        _state.update { it.copy(clientWindowsPickerDisplayedQuantity = content.toIntOrNull() ?: it.clientWindowsPickerDisplayedQuantity) }
                    Wifi_Messages_Types_NewProto.ClientWindowsSelectedColorId ->
                        _state.update { it.copy(clientWindowsSelectedColorId = content.toLongOrNull() ?: it.clientWindowsSelectedColorId) }
                    Wifi_Messages_Types_NewProto.ClientWindowsLazyRowSupColorsScrolle ->
                        _state.update { it.copy(clientWindowsLazyRowSupColorsScroll = content.toIntOrNull() ?: it.clientWindowsLazyRowSupColorsScroll) }
                    Wifi_Messages_Types_NewProto.NewArregmentColorsJsonStruct ->
                        _state.update { it.copy(newArregmentColorsJsonStruct = content) }
                    Wifi_Messages_Types_NewProto.FilterProduitsParCatalogueBsonID_ET_Autres_Types ->
                        _state.update { it.copy(filterProduitsParCatalogueBsonID = content) }
                    Wifi_Messages_Types_NewProto.Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran -> {
                        list_M3CouleurProduit.find { it.keyID == content }
                            ?.let { toggleExpandedCouleur(it)
                            }
                    }
                    Wifi_Messages_Types_NewProto.Collapse_Client_Expanded_Produit -> {
                        _state.update { it.copy(expanded_M3CouleurProduitInfos = null, expanded_M1Produit = null) }
                        val cur = onGetActiveCentralValues()
                        onUpdateActiveCentralValues(cur.copy(expanded_M3CouleurProduitInfos = null, expanded_M1Produit = null))
                    }
                    else -> Unit
                }
            }
    }

    fun toggleExpandedCouleur(couleur: M3CouleurProduitInfos) {
        val cur = onGetActiveCentralValues()
        val newColor = if (cur.expanded_M3CouleurProduitInfos?.keyID == couleur.keyID) null else couleur
        val newProduit = newColor?.let { list_M1Produit.find { p -> p.keyID == couleur.parentBProduitInfosKeyID } }
        _state.update { it.copy(expanded_M3CouleurProduitInfos = newColor, expanded_M1Produit = newProduit) }
        onUpdateActiveCentralValues(cur.copy(expanded_M3CouleurProduitInfos = newColor, expanded_M1Produit = newProduit))
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            updateConnectionStatus("Connexion en cours avec ${info.endpointName}...")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    this@WifiTransferDatas_PresenterApp.endpointId = endpointId
                    _state.update { it.copy(isConnected = true) }
                    updateConnectionStatus("Connecté")
                    retryCount = 0
                    startConnectionMonitoring()
                    sendData("Connection established")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> handleConnectionFailure("Connexion rejetée")
                ConnectionsStatusCodes.STATUS_ERROR -> handleConnectionFailure("Erreur de connexion")
                else -> handleConnectionFailure("Code inconnu: ${result.status.statusCode}")
            }
        }

        override fun onDisconnected(endpointId: String) {
            if (this@WifiTransferDatas_PresenterApp.endpointId == endpointId) {
                this@WifiTransferDatas_PresenterApp.endpointId = null
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
            if (payload.type == Payload.Type.BYTES)
                try {
                    handlePayload(String(payload.asBytes()!!)
                    ) } catch (_: Exception) {}
        }
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) = Unit
    }

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
                        ConnectionMode.HOST -> startAsHost()
                        ConnectionMode.CLIENT -> startAsClient()
                        ConnectionMode.NONE -> handleFinalDisconnection()
                        else -> {}
                    }
                    retryCount++
                } catch (_: Exception) {
                } finally {
                    isReconnecting.set(false)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun handleConnectionFailure(reason: String) {
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

    @SuppressLint("ObsoleteSdkInt")
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
    private fun checkRequiredPermissions(): Boolean =
        requiredPermissions.none {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
}
