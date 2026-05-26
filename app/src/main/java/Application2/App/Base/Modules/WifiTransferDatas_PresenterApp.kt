package Application2.App.Base.Modules

import Application2.App.Base.Repository.ActiveCentralValues_app2
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import Application4.App.Modules.Wi.Module.Wifi_Messages_Types_NewProto
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
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
import org.json.JSONObject
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
    /**
     * The tablette-filter mode sent by the host via [Wifi_Messages_Types_NewProto.Change_Filtered_Produits_Du_TabletteDisplayer].
     * null means no active filter (any mode other than Tablette_Et_Echants).
     */
    val filter_Affichage_Mode_Proto: Filter_Affichage_Mode_Proto? = Filter_Affichage_Mode_Proto.Tablette_Produits_Seulement,
    val isConnected: Boolean = false,
    val connectionStatus: String = "Déconnecté",
    val isHostPhone: Boolean = true,
    val switchRoles: Boolean = true,
    val testMessageByWifi: String = "",
    val error: String? = null,
    val affiche_pub_abdelwahab_electro_gro_store: Boolean = false,
)
@SuppressLint("StaticFieldLeak")
class WifiTransferDatas_PresenterApp(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    var list_M1Produit: List<M01Produit>,
    var list_M3CouleurProduit: List<M3CouleurProduitInfos>,
    private val onGetActiveCentralValues: () -> ActiveCentralValues_app2,
    private val onUpdateActiveCentralValues: (ActiveCentralValues_app2) -> Unit,
    /**
     * Called after [Update_Depot_Count_Par_Chain_Key_to_NewCount] is received and the
     * in-memory [list_M3CouleurProduit] has already been patched.
     * The ViewModel uses this to persist the changes to the DAO and refresh UiState.
     *
     * @param updates list of (keyID, newCount) pairs for every colour that was updated.
     */
    private val onUpdateDepotCounts: (updates: List<Pair<String, Int>>) -> Unit = {},
) {
    private val _state = MutableStateFlow(ProductDisplayController_App2())
    val state: StateFlow<ProductDisplayController_App2> = _state.asStateFlow()

    private var endpointId: String? = null
    private val serviceId = "com.example.clientjetpack"
    private val strategy = Strategy.P2P_POINT_TO_POINT
    private val isReconnecting = AtomicBoolean(false)
    private val isConnectingToEndpoint = AtomicBoolean(false)
    private var reconnectionJob: Job? = null
    private var advertiseDiscoverJob: Job? = null   // tracks the single in-flight _advertise/_discover
    private var connectionMonitorJob: Job? = null
    private var retryCount = 0
    private val maxRetries = 10
    private val baseRetryDelayMs = 3000L
    private val radioSettleMs = 1500L
    private var lastConnectionMode = ConnectionMode.NONE
    /** False after hardReset(); set back to true only by an explicit startAsHost/Client call. */
    private var allowAutoReconnect = true

    private enum class ConnectionMode { HOST, CLIENT, NONE }

    companion object { private const val TAG = "WifiPresenter" }

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

    fun sendOrderToClientDisplayerT(order: Wifi_Messages_Types_NewProto, data: Any? = null) {
        coroutineScope.launch { sendData("${order.prefix}$data") }
    }

    fun updateTypePhone(isHost: Boolean = false) {
        _state.update { it.copy(isHostPhone = isHost) }
    }

    // ── Public entry points (manual user click) ──────────────────────────────

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() {
        reconnectionJob?.cancel()
        advertiseDiscoverJob?.cancel()
        isReconnecting.set(false)
        retryCount = 0
        allowAutoReconnect = true
        lastConnectionMode = ConnectionMode.HOST
        _state.update { it.copy(isHostPhone = true) }
        Log.d(TAG, "startAsHost (manual): retryCount=$retryCount allowAutoReconnect=$allowAutoReconnect")
        advertiseDiscoverJob = coroutineScope.launch { _advertise() }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() {
        reconnectionJob?.cancel()
        advertiseDiscoverJob?.cancel()
        isReconnecting.set(false)
        retryCount = 0
        allowAutoReconnect = true
        lastConnectionMode = ConnectionMode.CLIENT
        _state.update { it.copy(isHostPhone = false) }
        Log.d(TAG, "startAsClient (manual): retryCount=$retryCount allowAutoReconnect=$allowAutoReconnect")
        advertiseDiscoverJob = coroutineScope.launch { _discover() }
    }

    // ── Internal Nearby start helpers ─────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun _advertise() {
        if (!checkRequiredPermissions()) {
            Log.w(TAG, "_advertise: permissions manquantes"); handleError("Permissions manquantes"); return
        }
        try { Nearby.getConnectionsClient(context).stopAdvertising() } catch (_: Exception) {}
        delay(radioSettleMs)   // let the radio settle before restarting — prevents STATUS_RADIO_ERROR
        Log.d(TAG, "_advertise: startAdvertising (retryCount=$retryCount)")
        try {
            Nearby.getConnectionsClient(context).startAdvertising(
                "Host Device", serviceId, connectionLifecycleCallback,
                AdvertisingOptions.Builder().setStrategy(strategy).build()
            ).addOnSuccessListener {
                Log.d(TAG, "_advertise: OK")
                updateConnectionStatus("En attente de connexion...")
            }.addOnFailureListener { e ->
                Log.e(TAG, "_advertise: FAILED — ${e.message}")
                handleConnectionFailure("Erreur hôte: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "_advertise: exception — ${e.message}")
            handleConnectionFailure(e.message ?: "Erreur inconnue")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun _discover() {
        if (!checkRequiredPermissions()) {
            Log.w(TAG, "_discover: permissions manquantes"); handleError("Permissions manquantes"); return
        }
        try { Nearby.getConnectionsClient(context).stopDiscovery() } catch (_: Exception) {}
        delay(radioSettleMs)   // let the radio settle before restarting — prevents STATUS_RADIO_ERROR
        Log.d(TAG, "_discover: startDiscovery (retryCount=$retryCount)")
        try {
            Nearby.getConnectionsClient(context).startDiscovery(
                serviceId, endpointDiscoveryCallback,
                DiscoveryOptions.Builder().setStrategy(strategy).build()
            ).addOnSuccessListener {
                Log.d(TAG, "_discover: OK")
                updateConnectionStatus("Recherche d'appareils...")
            }.addOnFailureListener { e ->
                Log.e(TAG, "_discover: FAILED — ${e.message}")
                handleConnectionFailure("Erreur recherche: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "_discover: exception — ${e.message}")
            handleConnectionFailure(e.message ?: "Erreur inconnue")
        }
    }

    fun disconnect() {
        connectionMonitorJob?.cancel()
        reconnectionJob?.cancel()
        advertiseDiscoverJob?.cancel()
        try {
            Nearby.getConnectionsClient(context).apply {
                stopAdvertising(); stopDiscovery(); stopAllEndpoints()
            }
        } catch (_: Exception) {}
        endpointId = null
        lastConnectionMode = ConnectionMode.NONE
        retryCount = 0
        isReconnecting.set(false)
        isConnectingToEndpoint.set(false)
        _state.update { it.copy(isConnected = false, connectionStatus = "Déconnecté", error = null) }
    }

    fun cancel() = disconnect()

    /**
     * Nuclear reset: cancels every job, stops all Nearby endpoints, blocks all
     * auto-reconnect until the next explicit startAsHost/Client call.
     */
    fun hardReset() {
        Log.d(TAG, "hardReset: début — endpointId=$endpointId, retryCount=$retryCount, lastMode=$lastConnectionMode, allowAutoReconnect=$allowAutoReconnect")
        connectionMonitorJob?.cancel()
        reconnectionJob?.cancel()
        advertiseDiscoverJob?.cancel()
        isReconnecting.set(false)
        isConnectingToEndpoint.set(false)
        allowAutoReconnect = false
        retryCount = maxRetries + 1
        lastConnectionMode = ConnectionMode.NONE
        endpointId = null
        try {
            Nearby.getConnectionsClient(context).apply {
                stopAdvertising()
                stopDiscovery()
                stopAllEndpoints()
            }
            Log.d(TAG, "hardReset: Nearby stopAll OK")
        } catch (e: Exception) {
            Log.e(TAG, "hardReset: Nearby stopAll FAILED — ${e.message}")
        }
        _state.value = ProductDisplayController_App2(
            connectionStatus = "Réinitialisé",
            error = null
        )
        Log.d(TAG, "hardReset: terminé — allowAutoReconnect=$allowAutoReconnect, retryCount=$retryCount")
    }

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
                            ?.let { toggleExpandedCouleur(it) }
                    }
                    Wifi_Messages_Types_NewProto.Collapse_Client_Expanded_Produit -> {
                        _state.update { it.copy(expanded_M3CouleurProduitInfos = null, expanded_M1Produit = null) }
                        val cur = onGetActiveCentralValues()
                        onUpdateActiveCentralValues(cur.copy(expanded_M3CouleurProduitInfos = null, expanded_M1Produit = null))
                    }
                    // content == enum name for Tablette_Et_Echants, or "null" for any other mode.
                    // Update tabletteDisplayerMode so the ViewModel can mirror it into UiState.filter_des_produits.
                    Wifi_Messages_Types_NewProto.Update_affiche_pub_abdelwahab_electro_gro_store ->
                        _state.update {
                            it.copy(
                                affiche_pub_abdelwahab_electro_gro_store =
                                    content.toBooleanStrictOrNull() ?: true
                            )
                        }
                    Wifi_Messages_Types_NewProto.Change_Filtered_Produits_Du_TabletteDisplayer -> {
                        val mode = Filter_Affichage_Mode_Proto.entries
                            .find { it.name == content }   // null when content == "null" or unrecognised
                        _state.update { it.copy(filter_Affichage_Mode_Proto = mode) }
                    }
                    // Payload shape (from the host Button_StockOptions_SubtractFromDepot):
                    // {"list_m3_a_Update_Leur_Count_Depot":[{"keyID":"...","count_Don_Depot":2},...]}
                    // 1. Parse the JSON and collect (keyID → newCount) pairs.
                    // 2. Patch list_M3CouleurProduit in-memory so the UI reacts immediately.
                    // 3. Delegate DAO persistence + UiState refresh to the ViewModel via onUpdateDepotCounts.
                    Wifi_Messages_Types_NewProto.Update_Depot_Count_Par_Chain_Key_to_NewCount -> {
                        try {
                            val jsonArray = JSONObject(content)
                                .getJSONArray("list_m3_a_Update_Leur_Count_Depot")
                            val updates = buildList {
                                repeat(jsonArray.length()) { i ->
                                    val item = jsonArray.getJSONObject(i)
                                    add(item.getString("keyID") to item.getInt("count_Don_Depot"))
                                }
                            }
                            Log.d(
                                "DepotSync",
                                "[PresenterApp] Received Update_Depot_Count — ${updates.size} entries: " +
                                        updates.joinToString { (k, v) -> "$k→$v" }
                            )
                            // Patch in-memory list so downstream UI (e.g. expanded card) is instant.
                            val updatesMap = updates.toMap()
                            list_M3CouleurProduit = list_M3CouleurProduit.map { couleur ->
                                updatesMap[couleur.keyID]
                                    ?.let { newCount -> couleur.copy(count_Don_Depot = newCount) }
                                    ?: couleur
                            }
                            // Persist to DAO + refresh UiState via ViewModel callback.
                            onUpdateDepotCounts(updates)
                        } catch (_: Exception) {}
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
            Log.d(TAG, "onConnectionResult: endpoint=$endpointId status=${result.status.statusCode} allowAutoReconnect=$allowAutoReconnect")
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
            Log.d(TAG, "onDisconnected: endpoint=$endpointId storedEndpoint=${this@WifiTransferDatas_PresenterApp.endpointId} allowAutoReconnect=$allowAutoReconnect retryCount=$retryCount")
            if (this@WifiTransferDatas_PresenterApp.endpointId == endpointId) {
                this@WifiTransferDatas_PresenterApp.endpointId = null
                _state.update { it.copy(isConnected = false, connectionStatus = "Déconnecté") }
                if (allowAutoReconnect && retryCount < maxRetries && lastConnectionMode != ConnectionMode.NONE) {
                    Log.d(TAG, "onDisconnected: → initiateReconnection")
                    initiateReconnection()
                } else if (!allowAutoReconnect) {
                    Log.d(TAG, "onDisconnected: allowAutoReconnect=false → ignoré (post-reset)")
                    _state.update { it.copy(connectionStatus = "Réinitialisé", error = null) }
                } else {
                    Log.d(TAG, "onDisconnected: retryCount=$retryCount >= maxRetries → handleFinalDisconnection")
                    handleFinalDisconnection()
                }
            } else {
                Log.w(TAG, "onDisconnected: endpoint=$endpointId ignoré (ne correspond pas au nôtre)")
            }
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "onEndpointFound: endpoint=$endpointId")
            Nearby.getConnectionsClient(context)
                .requestConnection("ClientAchteur Device", endpointId, connectionLifecycleCallback)
                .addOnFailureListener { e ->
                    Log.e(TAG, "onEndpointFound: requestConnection FAILED — ${e.message}")
                    handleConnectionFailure("Erreur connexion: ${e.message}")
                }
        }
        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "onEndpointLost: endpoint=$endpointId")
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES)
                try { handlePayload(String(payload.asBytes()!!)) } catch (_: Exception) {}
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
        Log.d(TAG, "initiateReconnection: retryCount=$retryCount isReconnecting=${isReconnecting.get()} allowAutoReconnect=$allowAutoReconnect lastMode=$lastConnectionMode")
        if (isReconnecting.compareAndSet(false, true)) {
            reconnectionJob?.cancel()
            reconnectionJob = coroutineScope.launch {
                try {
                    retryCount++   // increment FIRST so it counts toward maxRetries
                    val delayMs = 2000 + baseRetryDelayMs * (1L shl (retryCount - 1).coerceAtMost(5))
                    Log.d(TAG, "initiateReconnection: attente ${delayMs}ms avant tentative #$retryCount")
                    delay(delayMs)
                    if (!allowAutoReconnect) {
                        Log.d(TAG, "initiateReconnection: annulé (allowAutoReconnect=false après délai)")
                        return@launch
                    }
                    updateConnectionStatus("Reconnexion #$retryCount…")
                    when (lastConnectionMode) {
                        ConnectionMode.HOST   -> { advertiseDiscoverJob = coroutineScope.launch { _advertise() } }
                        ConnectionMode.CLIENT -> { advertiseDiscoverJob = coroutineScope.launch { _discover() } }
                        ConnectionMode.NONE   -> handleFinalDisconnection()
                    }
                } catch (_: Exception) {
                } finally {
                    isReconnecting.set(false)
                }
            }
        } else {
            Log.d(TAG, "initiateReconnection: déjà en cours, ignoré")
        }
    }

    @SuppressLint("NewApi")
    private fun handleConnectionFailure(reason: String) {
        Log.d(TAG, "handleConnectionFailure: reason='$reason' allowAutoReconnect=$allowAutoReconnect retryCount=$retryCount isReconnecting=${isReconnecting.get()}")
        if (!allowAutoReconnect) {
            Log.d(TAG, "handleConnectionFailure: allowAutoReconnect=false → ignoré (post-reset)")
            _state.update { it.copy(connectionStatus = "Réinitialisé", error = null) }
            return
        }
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
