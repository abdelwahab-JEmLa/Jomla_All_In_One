package Z_MasterOfApps.Kotlin._WorkingON.WO_

import Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephoneRepository
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class ConnectionManager(
    private val viewModel: HeadViewModel,
    private val context: Context,
    private val j_AppInstalleDonTelephoneRepository: J_AppInstalleDonTelephoneRepository
) : ViewModel() {
    private val _connectionUiState = MutableStateFlow(ConnectionUiState())
    private var endpointId: String? = null
    private val serviceId = "com.example.clientjetpack"
    private val strategy = Strategy.P2P_POINT_TO_POINT

    private val isReconnecting = AtomicBoolean(false)
    private var reconnectionJob: Job? = null
    private var connectionMonitorJob: Job? = null
    private var retryCount = 0
    private val maxRetries = 10
    private val baseRetryDelayMs = 3000L

    private var lastConnectionMode: ConnectionMode = ConnectionMode.NONE

    private enum class ConnectionMode {
        HOST, CLIENT, NONE
    }

    init {
        viewModelScope.launch {
            Log.d(TAG, "Initializing ConnectionManager")
            // Get current device name
            val currentDeviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
            Log.d(TAG, "Current device name: $currentDeviceName")

            // Find the current phone in the repository
            val currentPhone = j_AppInstalleDonTelephoneRepository.modelDatas.find {
                it.infosDeBase.nom == currentDeviceName
            }

            if (currentPhone != null) {
                Log.d(TAG, "Found current phone in repository: ${currentPhone.id}")
                // Check if this is a receiver phone
                val isReceiver = currentPhone.etatesMutable.itsReciverTelephone
                Log.d(TAG, "Is this a receiver phone? $isReceiver")

                if (!isReceiver) {
                    // This is a host device
                    Log.d(TAG, "This is a host device, setting up host configuration")
                    currentPhone.etatesMutable.nearbyWifiAdressIpConexion = "host_${currentDeviceName.replace(" ", "_")}"
                    j_AppInstalleDonTelephoneRepository.updatePhones()
                    Log.d(TAG, "Set nearbyWifiAdressIpConexion to: ${currentPhone.etatesMutable.nearbyWifiAdressIpConexion}")

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Log.d(TAG, "Starting as host (Android API 33+)")
                        startAsHost()
                    } else {
                        Log.d(TAG, "Not starting as host because device API level (${Build.VERSION.SDK_INT}) is below TIRAMISU (33)")
                    }
                    lastConnectionMode = ConnectionMode.HOST
                } else {
                    Log.d(TAG, "This is a client device, looking for host phone")
                    val hostPhone = j_AppInstalleDonTelephoneRepository.modelDatas.find {
                        !it.etatesMutable.itsReciverTelephone && it.etatesMutable.nearbyWifiAdressIpConexion.isNotEmpty()
                    }

                    if (hostPhone != null) {
                        Log.d(TAG, "Found host phone: ${hostPhone.infosDeBase.nom} with connection: ${hostPhone.etatesMutable.nearbyWifiAdressIpConexion}")
                        currentPhone.etatesMutable.nearbyWifiAdressIpConexion = hostPhone.etatesMutable.nearbyWifiAdressIpConexion
                        j_AppInstalleDonTelephoneRepository.updatePhones()
                        Log.d(TAG, "Set client nearbyWifiAdressIpConexion to: ${currentPhone.etatesMutable.nearbyWifiAdressIpConexion}")

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Log.d(TAG, "Starting as client (Android API 33+)")
                            startAsClient()
                        } else {
                            Log.d(TAG, "Not starting as client because device API level (${Build.VERSION.SDK_INT}) is below TIRAMISU (33)")
                        }
                        lastConnectionMode = ConnectionMode.CLIENT
                    } else {
                        Log.e(TAG, "No host phone found in repository!")
                    }
                }

                // Set up connection monitoring
                viewModelScope.launch {
                    Log.d(TAG, "Starting connection monitoring job")
                    while (true) {
                        delay(10000) // Check connection every 10 seconds
                        Log.d(TAG, "Connection check: isConnected=${_connectionUiState.value.isConnected}, lastMode=$lastConnectionMode")
                        if (!_connectionUiState.value.isConnected &&
                            lastConnectionMode != ConnectionMode.NONE &&
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Log.d(TAG, "Connection not detected, initiating reconnection")
                            initiateReconnection()
                        }
                    }
                }
            } else {
                Log.e(TAG, "Current phone not found in repository! Device: $currentDeviceName")
            }
        }
    }

    private fun handlePayload(payload: String) {
        Log.d(TAG, "Received payload: ${payload.take(50)}${if (payload.length > 50) "..." else ""}")
        WifiUpdateClientDisplayerStats.fromPayload(payload)?.let { (messageType, content) ->
            Log.d(TAG, "Parsed message type: $messageType, content: ${content.take(20)}${if (content.length > 20) "..." else ""}")
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
        } ?: Log.e(TAG, "Failed to parse payload: $payload")
    }

    fun updateDisplayController(update: ProductDisplayController.() -> ProductDisplayController) {
        viewModel._uiState.update { it.copy(productDisplayController = update(it.productDisplayController)) }
    }

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) {
        Log.d(TAG, "Sending order to client: $orderName, data: $data")
        viewModelScope.launch {
            sendData("$orderName$data")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun initiateReconnection() {
        Log.d(TAG, "Initiating reconnection, isReconnecting=${isReconnecting.get()}, retryCount=$retryCount/$maxRetries")
        if (isReconnecting.compareAndSet(false, true)) {
            reconnectionJob?.cancel()
            reconnectionJob = viewModelScope.launch {
                try {
                    delay(2000)
                    Log.d(TAG, "Checking connection status before reconnection attempt")

                    if (!_connectionUiState.value.isConnected) {
                        val backoffDelay = calculateBackoffDelay()
                        Log.d(TAG, "Connection still lost, waiting $backoffDelay ms before retry #${retryCount + 1}")
                        delay(backoffDelay)

                        _connectionUiState.update { it.copy(
                            connectionStatus = "Tentative de reconnexion #${retryCount + 1}",
                            reconnectionAttempts = retryCount + 1
                        )}

                        when (lastConnectionMode) {
                            ConnectionMode.HOST -> {
                                Log.d(TAG, "Reconnecting as HOST")
                                startAsHost()
                            }
                            ConnectionMode.CLIENT -> {
                                Log.d(TAG, "Reconnecting as CLIENT")
                                startAsClient()
                            }
                            ConnectionMode.NONE -> {
                                Log.e(TAG, "No connection mode set, giving up")
                                handleFinalDisconnection()
                            }
                        }

                        retryCount++

                        _connectionUiState.update { it.copy(
                            lastSuccessfulConnection = System.currentTimeMillis()
                        )}
                    } else {
                        Log.d(TAG, "Reconnection unnecessary, connection restored")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error during reconnection attempt: ${e.message}", e)
                    handleError("Échec de la reconnexion: ${e.message}")
                } finally {
                    isReconnecting.set(false)
                    Log.d(TAG, "Reconnection attempt completed, isReconnecting set to false")
                }
            }
        } else {
            Log.d(TAG, "Reconnection already in progress, skipping new attempt")
        }
    }

    private fun shouldAttemptReconnection(): Boolean {
        val shouldRetry = !_connectionUiState.value.isConnected &&
                retryCount < maxRetries &&
                lastConnectionMode != ConnectionMode.NONE
        Log.d(TAG, "Should attempt reconnection? $shouldRetry (connected=${_connectionUiState.value.isConnected}, retries=$retryCount/$maxRetries, mode=$lastConnectionMode)")
        return shouldRetry
    }

    @SuppressLint("NewApi")
    private fun handleDisconnection(disconnectedEndpointId: String) {
        Log.d(TAG, "Handling disconnection for endpoint: $disconnectedEndpointId, current endpoint: $endpointId")
        if (endpointId == disconnectedEndpointId) {
            this.endpointId = null
            updateConnectionStatus("Déconnecté")
            _connectionUiState.update { it.copy(
                isConnected = false,
                lastSuccessfulConnection = System.currentTimeMillis()
            )}
            Log.d(TAG, "Connection state updated to disconnected")

            if (shouldAttemptReconnection()) {
                Log.d(TAG, "Will attempt reconnection")
                initiateReconnection()
            } else {
                Log.d(TAG, "Will not attempt reconnection, handling final disconnection")
                handleFinalDisconnection()
            }
        } else {
            Log.d(TAG, "Ignoring disconnection for different endpoint")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d(TAG, "Connection initiated with endpoint: $endpointId, name: ${info.endpointName}")
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            updateConnectionStatus("Connexion en cours avec ${info.endpointName}...")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            Log.d(TAG, "Connection result for endpoint $endpointId: ${result.status.statusCode}")
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    this@ConnectionManager.endpointId = endpointId
                    updateConnectionStatus("Connecté")
                    _connectionUiState.update { it.copy(isConnected = true) }
                    retryCount = 0
                    startConnectionMonitoring()
                    sendData("Connection established")
                    Log.d(TAG, "Connection successfully established with endpoint $endpointId")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.e(TAG, "Connection rejected with endpoint $endpointId")
                    handleConnectionFailure("Connexion rejetée")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.e(TAG, "Connection error with endpoint $endpointId")
                    handleConnectionFailure("Erreur de connexion")
                }
                else -> {
                    Log.e(TAG, "Unknown connection status code: ${result.status.statusCode}")
                    handleConnectionFailure("Erreur inconnue")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "Disconnected from endpoint: $endpointId")
            handleDisconnection(endpointId)
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                try {
                    val rawMessage = String(payload.asBytes()!!)
                    Log.d(TAG, "Payload received from $endpointId: ${rawMessage.take(50)}${if (rawMessage.length > 50) "..." else ""}")
                    handlePayload(rawMessage)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing payload from $endpointId", e)
                }
            } else {
                Log.w(TAG, "Received non-bytes payload from $endpointId: ${payload.type}")
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            when (update.status) {
                PayloadTransferUpdate.Status.SUCCESS -> {
                    Log.d(TAG, "Payload transfer succeeded: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
                }
                PayloadTransferUpdate.Status.FAILURE -> {
                    Log.e(TAG, "Payload transfer failed: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
                    handleTransferFailure()
                }
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    Log.v(TAG, "Payload transfer in progress: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
                }
                PayloadTransferUpdate.Status.CANCELED -> {
                    Log.w(TAG, "Payload transfer canceled: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
                    handleTransferFailure()
                }
            }
        }
    }

    private fun handleTransferFailure() {
        Log.e(TAG, "Handling transfer failure")
        viewModelScope.launch {
            if (_connectionUiState.value.isConnected) {
                Log.d(TAG, "Still connected despite transfer failure, checking connection health")
                checkConnectionHealth()
            }
        }
    }

    private fun startConnectionMonitoring() {
        Log.d(TAG, "Starting connection monitoring")
        connectionMonitorJob?.cancel()
        connectionMonitorJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)
                Log.d(TAG, "Checking connection health (periodic check)")
                checkConnectionHealth()
            }
        }
    }

    private fun checkConnectionHealth() {
        endpointId?.let { endpoint ->
            try {
                Log.d(TAG, "Sending ping to test connection with endpoint $endpoint")
                sendData("ping")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send ping to endpoint $endpoint", e)
                handleConnectionFailure("Perte de connexion détectée")
            }
        } ?: Log.w(TAG, "No endpoint to check connection health")
    }

    @SuppressLint("NewApi")
    private fun handleConnectionFailure(reason: String) {
        Log.e(TAG, "Connection failure: $reason (retryCount=$retryCount, maxRetries=$maxRetries)")
        if (!isReconnecting.get() && retryCount < maxRetries) {
            Log.d(TAG, "Initiating reconnection after connection failure")
            initiateReconnection()
        } else if (retryCount >= maxRetries) {
            Log.e(TAG, "Maximum retry attempts reached, handling final disconnection")
            handleFinalDisconnection()
        }
    }

    private fun calculateBackoffDelay(): Long {
        val delay = baseRetryDelayMs * (1L shl retryCount.coerceAtMost(5))
        Log.d(TAG, "Calculated backoff delay for retry #$retryCount: $delay ms")
        return delay
    }

    private fun handleFinalDisconnection() {
        Log.e(TAG, "Handling final disconnection after exhausting reconnection attempts")
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
            Log.d(TAG, "Starting as host")
            if (!checkRequiredPermissions()) {
                Log.e(TAG, "Missing required permissions for Nearby Connections")
                val missingPermissions = requiredPermissions.filter { permission ->
                    ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
                }
                Log.e(TAG, "Missing permissions: $missingPermissions")
                handleError("Permissions manquantes")
                return@launch
            }

            Log.d(TAG, "All required permissions granted")
            lastConnectionMode = ConnectionMode.HOST
            _connectionUiState.update { it.copy(isHostPhone = true) }

            try {
                val advertisingOptions = AdvertisingOptions.Builder()
                    .setStrategy(strategy)
                    .build()

                Log.d(TAG, "Starting advertising with service ID: $serviceId and strategy: $strategy")
                Nearby.getConnectionsClient(context).startAdvertising(
                    "Host Device",
                    serviceId,
                    connectionLifecycleCallback,
                    advertisingOptions
                ).addOnSuccessListener {
                    Log.d(TAG, "Successfully started advertising as host")
                    updateConnectionStatus("En attente de connexion...")
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Failed to start advertising: ${e.message}", e)
                    handleConnectionFailure("Erreur de démarrage du mode hôte: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during advertising: ${e.message}", e)
                handleConnectionFailure(e.message ?: "Erreur inconnue")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() {
        viewModelScope.launch {
            Log.d(TAG, "Starting as client")
            if (!checkRequiredPermissions()) {
                Log.e(TAG, "Missing required permissions for Nearby Connections")
                val missingPermissions = requiredPermissions.filter { permission ->
                    ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
                }
                Log.e(TAG, "Missing permissions: $missingPermissions")
                handleError("Permissions manquantes")
                return@launch
            }

            Log.d(TAG, "All required permissions granted")
            lastConnectionMode = ConnectionMode.CLIENT
            _connectionUiState.update { it.copy(isHostPhone = false) }

            try {
                val discoveryOptions = DiscoveryOptions.Builder()
                    .setStrategy(strategy)
                    .build()

                Log.d(TAG, "Starting discovery with service ID: $serviceId and strategy: $strategy")
                Nearby.getConnectionsClient(context).startDiscovery(
                    serviceId,
                    endpointDiscoveryCallback,
                    discoveryOptions
                ).addOnSuccessListener {
                    Log.d(TAG, "Successfully started discovery as client")
                    updateConnectionStatus("Recherche d'appareils...")
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Failed to start discovery: ${e.message}", e)
                    handleConnectionFailure("Erreur de démarrage de la recherche: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during discovery: ${e.message}", e)
                handleConnectionFailure(e.message ?: "Erreur inconnue")
            }
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "Endpoint found: $endpointId, name: ${info.endpointName}, serviceId: ${info.serviceId}")
            Nearby.getConnectionsClient(context)
                .requestConnection(
                    "Client Device",
                    endpointId,
                    connectionLifecycleCallback
                )
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully requested connection to endpoint: $endpointId")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to request connection to endpoint: $endpointId", e)
                    handleConnectionFailure("Erreur de connexion: ${e.message}")
                }
        }

        override fun onEndpointLost(endpointId: String) {
            Log.w(TAG, "Endpoint lost: $endpointId")
        }
    }

    fun sendData(data: Any) {
        endpointId?.let { endpoint ->
            try {
                val payload = when (data) {
                    is String -> Payload.fromBytes(data.toByteArray())
                    else -> {
                        Log.e(TAG, "Unsupported data type for sending: ${data.javaClass.simpleName}")
                        return
                    }
                }

                Log.d(TAG, "Sending data to endpoint $endpoint: ${(data as? String)?.take(50)}${if ((data as? String)?.length ?: 0 > 50) "..." else ""}")
                Nearby.getConnectionsClient(context)
                    .sendPayload(endpoint, payload)
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully sent payload to endpoint $endpoint")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to send payload to endpoint $endpoint", e)
                        handleTransferFailure()
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while sending data to endpoint $endpoint", e)
                handleTransferFailure()
            }
        } ?: Log.e(TAG, "Cannot send data: No connected endpoint")
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting and cleaning up resources")
        connectionMonitorJob?.cancel()
        reconnectionJob?.cancel()

        try {
            Log.d(TAG, "Stopping Nearby Connections services")
            Nearby.getConnectionsClient(context).apply {
                stopAdvertising()
                stopDiscovery()
                stopAllEndpoints()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during disconnect/cleanup", e)
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
        Log.d(TAG, "Disconnect complete, connection state reset")
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

        if (missingPermissions.isNotEmpty()) {
            Log.w(TAG, "Missing permissions: $missingPermissions")
        } else {
            Log.d(TAG, "All required permissions granted")
        }

        return missingPermissions.isEmpty()
    }

    private fun updateConnectionStatus(status: String) {
        Log.d(TAG, "Updating connection status: $status")
        _connectionUiState.update { it.copy(
            connectionStatus = status,
            error = null
        )}
    }

    private fun handleError(error: String) {
        Log.e(TAG, "Error: $error")
        _connectionUiState.update { it.copy(
            error = error,
            connectionStatus = "Erreur: $error"
        )}
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared, cleaning up resources")
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
