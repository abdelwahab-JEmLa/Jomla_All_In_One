package Z_MasterOfApps.Kotlin._WorkingON.WO_

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.Models.ProductDisplayController
import com.example.clientjetpack.ViewModel.HeadViewModel
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
import java.util.concurrent.atomic.AtomicBoolean

class ConnectionManager(
    private val viewModel: HeadViewModel,
    private val context: Context
) : ViewModel() {

    private val _connectionUiState = MutableStateFlow(ConnectionUiState())
    val connectionUiState: StateFlow<ConnectionUiState> = _connectionUiState.asStateFlow()

    private var endpointId: String? = null
    private val serviceId = "com.example.clientjetpack"
    private val strategy = Strategy.P2P_POINT_TO_POINT

    private val isReconnecting = AtomicBoolean(false)
    private var reconnectionJob: Job? = null
    private var connectionMonitorJob: Job? = null
    private var retryCount = 0
    private val maxRetries = 10
    private val baseRetryDelayMs = 3000L
    val tag = ""

    private var lastConnectionMode: ConnectionMode = ConnectionMode.NONE

    private enum class ConnectionMode {
        HOST, CLIENT, NONE
    }

    private fun handlePayload(payload: String) {
        WifiUpdateClientDisplayerStats.fromPayload(payload)?.let { (messageType, content) ->
            when (messageType) {
                WifiUpdateClientDisplayerStats.ClientMainGridScrollPosition -> updateDisplayController {
                    copy(mainGridScrollPosition = content.toInt())
                }

                WifiUpdateClientDisplayerStats.ClientWindowsDisplayedProductId -> updateDisplayController {
                    copy(clientWindowsDisplayedProductId = content.toLong())
                }

                WifiUpdateClientDisplayerStats.DISMISS_PRODUCT_INFO -> updateDisplayController {
                    copy(
                        clientWindowsDisplayedProductId = null,
                        searchWindowsDisplaye = ""
                    )
                }

                WifiUpdateClientDisplayerStats.ClientWindowsLazyRowSupColorsScrolle -> updateDisplayController {
                    copy(clientWindowsLazyRowSupColorsScroll = content.toInt())
                }

                WifiUpdateClientDisplayerStats.WindowsPickerDisplayedQuantity -> updateDisplayController {
                    copy(
                        clientWindowsPickerDisplayedQuantity = if (content == "0")
                            1 else {
                            content.toInt()
                        }
                    )
                }

                WifiUpdateClientDisplayerStats.ClientWindowsSelectedColorId -> updateDisplayController {
                    copy(clientWindowsSelectedColorId = content.toLong())
                }

                WifiUpdateClientDisplayerStats.SearchWindowsDisplaye -> updateDisplayController {
                    copy(searchWindowsDisplaye = content)
                }

                WifiUpdateClientDisplayerStats.NewArregmentColorsJsonStruct -> updateDisplayController {
                    copy(newArregmentColorsJsonStruct = content)
                }
            }
        }
    }

     fun updateDisplayController(update: ProductDisplayController.() -> ProductDisplayController) {
        viewModel._uiState.update { it.copy(productDisplayController = update(it.productDisplayController)) }
    }

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) {
        viewModelScope.launch {
            sendData("$orderName$data")
        }
    }


    fun addHostDevice(deviceName: String) {
        viewModelScope.launch {
            val device = viewModel.uiState.value.devicesTypeManager.find { it.name == deviceName }
                ?: return@launch

            val updatedDevice = device.copy(isHost = true)

            viewModel.database.devicesTypeManagerDao().insert(updatedDevice)

            viewModel.refDevicesTypeManager
                .child(updatedDevice.id.toString())
                .setValue(updatedDevice)
                .addOnSuccessListener {}
                .addOnFailureListener {}

            viewModel._uiState.update { currentState ->
                currentState.copy(
                    devicesTypeManager = currentState.devicesTypeManager.map {
                        if (it.id == updatedDevice.id) updatedDevice else it
                    }
                )
            }
        }
    }

    fun removeHostDevice(deviceName: String) {
        viewModelScope.launch {
            val device = viewModel.uiState.value.devicesTypeManager.find { it.name == deviceName }
                ?: return@launch

            val updatedDevice = device.copy(isHost = false)

            viewModel.database.devicesTypeManagerDao().insert(updatedDevice)

            viewModel.refDevicesTypeManager
                .child(updatedDevice.id.toString())
                .setValue(updatedDevice)
                .addOnSuccessListener {}
                .addOnFailureListener {}

            viewModel._uiState.update { currentState ->
                currentState.copy(
                    devicesTypeManager = currentState.devicesTypeManager.map {
                        if (it.id == updatedDevice.id) updatedDevice else it
                    }
                )
            }
        }
    }

     fun getHostDevices(): List<String> {
        return viewModel.uiState.value.devicesTypeManager
            .filter { it.isHost }
            .map { it.name }
    }

    fun updateTypePhone(type: Boolean = false) {
        updateDisplayController {
            copy(isHostPhone = type)
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun initializeConnection() {
        val currentDevice = Build.MODEL.lowercase()
        val isHostDevice = getHostDevices().any { deviceName ->
            currentDevice.contains(deviceName)
        }

        if (isHostDevice) {
            updateTypePhone(true)
        } else {
            updateTypePhone(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
     fun initiateReconnection() {
        if (isReconnecting.compareAndSet(false, true)) {
            reconnectionJob?.cancel()
            reconnectionJob = viewModelScope.launch {
                try {
                    delay(2000)

                    if (!_connectionUiState.value.isConnected) {
                        val backoffDelay = calculateBackoffDelay()
                        delay(backoffDelay)

                        _connectionUiState.update { it.copy(
                            connectionStatus = "Tentative de reconnexion #${retryCount + 1}",
                            reconnectionAttempts = retryCount + 1
                        )}

                        when (lastConnectionMode) {
                            ConnectionMode.HOST -> startAsHost()
                            ConnectionMode.CLIENT -> startAsClient()
                            ConnectionMode.NONE -> {
                                handleFinalDisconnection()
                            }
                        }

                        retryCount++

                        _connectionUiState.update { it.copy(
                            lastSuccessfulConnection = System.currentTimeMillis()
                        )}
                    }
                } catch (e: Exception) {
                    handleError("Échec de la reconnexion: ${e.message}")
                } finally {
                    isReconnecting.set(false)
                }
            }
        }
    }

    private fun shouldAttemptReconnection(): Boolean {
        return !_connectionUiState.value.isConnected &&
                retryCount < maxRetries &&
                lastConnectionMode != ConnectionMode.NONE
    }

    @SuppressLint("NewApi")
    private fun handleDisconnection(disconnectedEndpointId: String) {
        if (endpointId == disconnectedEndpointId) {
            this.endpointId = null
            updateConnectionStatus("Déconnecté")
            _connectionUiState.update { it.copy(
                isConnected = false,
                lastSuccessfulConnection = System.currentTimeMillis()
            )}

            if (shouldAttemptReconnection()) {
                initiateReconnection()
            } else {
                handleFinalDisconnection()
            }
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            updateConnectionStatus("Connexion en cours avec ${info.endpointName}...")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    this@ConnectionManager.endpointId = endpointId
                    updateConnectionStatus("Connecté")
                    _connectionUiState.update { it.copy(isConnected = true) }
                    retryCount = 0
                    startConnectionMonitoring()
                    sendData("Connection established")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    handleConnectionFailure("Connexion rejetée")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    handleConnectionFailure("Erreur de connexion")
                }
                else -> {
                    handleConnectionFailure("Erreur inconnue")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            handleDisconnection(endpointId)
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                try {
                    val rawMessage = String(payload.asBytes()!!)
                    handlePayload(rawMessage)
                } catch (e: Exception) {
                    // Error handling
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            when (update.status) {
                PayloadTransferUpdate.Status.SUCCESS -> {
                    // Transfer successful
                }
                PayloadTransferUpdate.Status.FAILURE -> {
                    handleTransferFailure()
                }
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    // Transfer in progress
                }
                PayloadTransferUpdate.Status.CANCELED -> {
                    handleTransferFailure()
                }
            }
        }
    }

    private fun handleTransferFailure() {
        viewModelScope.launch {
            if (_connectionUiState.value.isConnected) {
                // Retry logic
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
        endpointId?.let { endpoint ->
            try {
                sendData("ping")
            } catch (e: Exception) {
                handleConnectionFailure("Perte de connexion détectée")
            }
        }
    }

    @SuppressLint("NewApi")
    private fun handleConnectionFailure(reason: String) {
        if (!isReconnecting.get() && retryCount < maxRetries) {
            initiateReconnection()
        } else if (retryCount >= maxRetries) {
            handleFinalDisconnection()
        }
    }

    private fun calculateBackoffDelay(): Long {
        return baseRetryDelayMs * (1L shl retryCount.coerceAtMost(5))
    }

    private fun handleFinalDisconnection() {
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
            if (!checkRequiredPermissions()) {
                handleError("Permissions manquantes")
                return@launch
            }

            // Add explicit logging
            android.util.Log.d(TAG, "Starting as host with service ID: $serviceId and strategy: $strategy")

            lastConnectionMode = ConnectionMode.HOST
            _connectionUiState.update { it.copy(isHostPhone = true) }

            try {
                val advertisingOptions = AdvertisingOptions.Builder()
                    .setStrategy(strategy)
                    .build()

                Nearby.getConnectionsClient(context).startAdvertising(
                    "Host Device",
                    serviceId,
                    connectionLifecycleCallback,
                    advertisingOptions
                ).addOnSuccessListener {
                    android.util.Log.d(TAG, "Successfully started advertising")
                    updateConnectionStatus("En attente de connexion...")
                }.addOnFailureListener { e ->
                    android.util.Log.e(TAG, "Failed to start advertising: ${e.message}", e)
                    handleConnectionFailure("Erreur de démarrage du mode hôte: ${e.message}")
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Exception during advertising: ${e.message}", e)
                handleConnectionFailure(e.message ?: "Erreur inconnue")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() {
        viewModelScope.launch {
            if (!checkRequiredPermissions()) {
                handleError("Permissions manquantes")
                return@launch
            }

            lastConnectionMode = ConnectionMode.CLIENT
            _connectionUiState.update { it.copy(isHostPhone = false) }

            try {
                val discoveryOptions = DiscoveryOptions.Builder()
                    .setStrategy(strategy)
                    .build()

                Nearby.getConnectionsClient(context).startDiscovery(
                    serviceId,
                    endpointDiscoveryCallback,
                    discoveryOptions
                ).addOnSuccessListener {
                    updateConnectionStatus("Recherche d'appareils...")
                }.addOnFailureListener { e ->
                    handleConnectionFailure("Erreur de démarrage de la recherche: ${e.message}")
                }
            } catch (e: Exception) {
                handleConnectionFailure(e.message ?: "Erreur inconnue")
            }
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Nearby.getConnectionsClient(context)
                .requestConnection(
                    "Client Device",
                    endpointId,
                    connectionLifecycleCallback
                )
                .addOnSuccessListener {
                    // Success
                }
                .addOnFailureListener { e ->
                    handleConnectionFailure("Erreur de connexion: ${e.message}")
                }
        }

        override fun onEndpointLost(endpointId: String) {
            // Endpoint lost
        }
    }

    fun sendData(data: Any) {
        endpointId?.let { endpoint ->
            try {
                val payload = when (data) {
                    is String -> Payload.fromBytes(data.toByteArray())
                    else -> {
                        return
                    }
                }

                Nearby.getConnectionsClient(context)
                    .sendPayload(endpoint, payload)
                    .addOnSuccessListener {
                        // Success
                    }
                    .addOnFailureListener { e ->
                        handleTransferFailure()
                    }
            } catch (e: Exception) {
                handleTransferFailure()
            }
        }
    }

    fun disconnect() {
        connectionMonitorJob?.cancel()
        reconnectionJob?.cancel()

        try {
            Nearby.getConnectionsClient(context).apply {
                stopAdvertising()
                stopDiscovery()
                stopAllEndpoints()
            }
        } catch (e: Exception) {
            // Error handling
        }

        endpointId = null
        lastConnectionMode = ConnectionMode.NONE
        retryCount = 0
        isReconnecting.set(false)

        _connectionUiState.update {
            it.copy(
                isConnected = false,
                isHostPhone = false,
                connectionStatus = "Déconnecté",
                error = null
            )
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
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }

        return missingPermissions.isEmpty()
    }

    private fun updateConnectionStatus(status: String) {
        _connectionUiState.update { it.copy(
            connectionStatus = status,
            error = null
        )}
    }

    private fun handleError(error: String) {
        _connectionUiState.update { it.copy(
            error = error,
            connectionStatus = "Erreur: $error"
        )}
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
        connectionMonitorJob?.cancel()
        reconnectionJob?.cancel()
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
    NewArregmentColorsJsonStruct("NewArregmentColorsJsonStruct")
    ;

    companion object {
        fun fromPayload(payload: String): Pair<WifiUpdateClientDisplayerStats, String>? {
            return entries.firstOrNull { payload.startsWith(it.prefix) }?.let {
                it to payload.removePrefix(it.prefix)
            }
        }
    }
}
