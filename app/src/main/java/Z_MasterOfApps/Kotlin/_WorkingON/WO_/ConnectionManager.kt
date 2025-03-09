package Z_MasterOfApps.Kotlin._WorkingON.WO_

import Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephoneRepository
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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
    private val maxRetries = 50
    private val baseRetryDelayMs = 3000L

    private var lastConnectionMode: ConnectionMode = ConnectionMode.NONE
    private var isAdvertising = false
    private var isDiscovering = false

    private enum class ConnectionMode {
        HOST, CLIENT, NONE
    }

    init {
        viewModelScope.launch {
            logI("Initializing ConnectionManager")

            // Wait for the repository to finish loading data
            // Replace the fixed delay with a check for progressRepo completion
            var timeout = 60000L // 60 second timeout as a safety measure
            val startTime = System.currentTimeMillis()

            while (j_AppInstalleDonTelephoneRepository.progressRepo.value < 1.0f) {
                // Check if we've exceeded the timeout
                if (System.currentTimeMillis() - startTime > timeout) {
                    logE("Repository loading timeout after ${timeout/1000} seconds")
                    break
                }

                logI("Waiting for repository to load, progress: ${j_AppInstalleDonTelephoneRepository.progressRepo.value}")
                delay(500) // Check every half second

                // Exit if the coroutine is cancelled
                if (!isActive) return@launch
            }

            logI("Repository loaded, progress: ${j_AppInstalleDonTelephoneRepository.progressRepo.value}")

            // Get current device name without potential extras
            val manufacturerModel = "${Build.MANUFACTURER} ${Build.MODEL}"
            logI("Raw device name: $manufacturerModel")

            // Clean up device name (remove potential extras like "pk" suffix)
            val currentDeviceName = manufacturerModel.trim().split(" ").take(4).joinToString(" ")
            logI("Cleaned current device name: $currentDeviceName")

            // Check for and clean up duplicate entries
            cleanupDuplicateDevices(currentDeviceName)

            // Log available devices in repository for debugging
            logI("Available devices in repository after cleanup: ${j_AppInstalleDonTelephoneRepository.modelDatas.map { "${it.id}: ${it.infosDeBase.nom}" }}")

            // More flexible device matching
            val currentPhone = j_AppInstalleDonTelephoneRepository.modelDatas.find { phone ->
                // Try exact match first
                if (phone.infosDeBase.nom == currentDeviceName) return@find true

                // Then try case-insensitive contains match
                if (phone.infosDeBase.nom.contains(currentDeviceName, ignoreCase = true)) return@find true

                // Then try matching just the model part if it's distinctive enough
                val model = Build.MODEL.trim()
                if (model.length > 3 && phone.infosDeBase.nom.contains(model, ignoreCase = true)) return@find true

                false
            }

            if (currentPhone != null) {
                logI("Found current phone in repository: ${currentPhone.id} - ${currentPhone.infosDeBase.nom}")
                // Check if this is a receiver phone
                val isReceiver = currentPhone.etatesMutable.itsReciverTelephone
                logI("Is this a receiver phone? $isReceiver")

                if (!isReceiver) {
                    // This is a host device
                    logI("This is a host device, setting up host configuration")
                    currentPhone.etatesMutable.nearbyWifiAdressIpConexion = "host_${currentDeviceName.replace(" ", "_")}"
                    j_AppInstalleDonTelephoneRepository.updatePhones()
                    logI("Set nearbyWifiAdressIpConexion to: ${currentPhone.etatesMutable.nearbyWifiAdressIpConexion}")

                    // Start as host
                    logI("Starting as host")
                    startAsHost()
                    lastConnectionMode = ConnectionMode.HOST
                } else {
                    logI("This is a client device, looking for host phone")
                    val hostPhone = j_AppInstalleDonTelephoneRepository.modelDatas.find {
                        !it.etatesMutable.itsReciverTelephone && it.etatesMutable.nearbyWifiAdressIpConexion.isNotEmpty()
                    }

                    if (hostPhone != null) {
                        logI("Found host phone: ${hostPhone.infosDeBase.nom} with connection: ${hostPhone.etatesMutable.nearbyWifiAdressIpConexion}")
                        currentPhone.etatesMutable.nearbyWifiAdressIpConexion = hostPhone.etatesMutable.nearbyWifiAdressIpConexion
                        j_AppInstalleDonTelephoneRepository.updatePhones()
                        logI("Set client nearbyWifiAdressIpConexion to: ${currentPhone.etatesMutable.nearbyWifiAdressIpConexion}")

                        // Start as client
                        logI("Starting as client")
                        startAsClient()
                        lastConnectionMode = ConnectionMode.CLIENT
                    } else {
                        logE("No host phone found in repository!")
                        logI("Converting this device to a host as fallback")

                        // Convert this device to a host
                        currentPhone.etatesMutable.itsReciverTelephone = false
                        currentPhone.etatesMutable.nearbyWifiAdressIpConexion = "host_${currentDeviceName.replace(" ", "_")}"
                        j_AppInstalleDonTelephoneRepository.updatePhones()

                        startAsHost()
                        lastConnectionMode = ConnectionMode.HOST
                    }
                }

                // Set up connection monitoring
                startConnectionMonitoring()
            } else {
                logE("Current phone not found in repository! Device: $currentDeviceName")
                logI("Attempting to register the device automatically")

                // Auto-register this device if possible
                val newPhone = createNewPhoneEntry(currentDeviceName)
                if (newPhone != null) {
                    logI("Successfully registered new device with ID: ${newPhone.id}")

                    // Set up as host by default for a new device
                    newPhone.etatesMutable.nearbyWifiAdressIpConexion = "host_${currentDeviceName.replace(" ", "_")}"
                    j_AppInstalleDonTelephoneRepository.updatePhones()

                    logI("Starting as host for new device")
                    startAsHost()
                    lastConnectionMode = ConnectionMode.HOST

                    // Start monitoring
                    startConnectionMonitoring()
                }
            }
        }
    }

    private fun cleanupDuplicateDevices(deviceName: String) {
        // Find all entries matching this device
        val matchingDevices = j_AppInstalleDonTelephoneRepository.modelDatas.filter { phone ->
            phone.infosDeBase.nom == deviceName ||
                    phone.infosDeBase.nom.contains(deviceName, ignoreCase = true) ||
                    deviceName.contains(phone.infosDeBase.nom, ignoreCase = true)
        }

        if (matchingDevices.size > 1) {
            logI("Found ${matchingDevices.size} potential duplicate entries for '$deviceName'")

            // Keep the first one and remove others
            val deviceToKeep = matchingDevices.first()
            matchingDevices.drop(1).forEach { duplicate ->
                logI("Removing duplicate device: ID ${duplicate.id} - ${duplicate.infosDeBase.nom}")
                j_AppInstalleDonTelephoneRepository.modelDatas.remove(duplicate)
            }

            // Update repository
            j_AppInstalleDonTelephoneRepository.updatePhones()
            logI("Repository cleaned, kept device ID: ${deviceToKeep.id}")
        }
    }

    private fun startConnectionMonitoring() {
        logI("Starting connection monitoring job")
        connectionMonitorJob?.cancel()
        connectionMonitorJob = viewModelScope.launch {
            while (isActive) {
                delay(10000) // Check connection every 10 seconds
                logD("Connection check: isConnected=${_connectionUiState.value.isConnected}, lastMode=$lastConnectionMode")
                if (!_connectionUiState.value.isConnected && lastConnectionMode != ConnectionMode.NONE) {
                    logI("Connection not detected, initiating reconnection")
                    initiateReconnection()
                }
            }
        }
    }

    private fun createNewPhoneEntry(deviceName: String): Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephone? {
        try {
            // Find the highest ID currently in use
            val maxId = j_AppInstalleDonTelephoneRepository.modelDatas.maxOfOrNull { it.id } ?: 0
            val newId = maxId + 1

            // Create new phone entry
            val newPhone = Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephone(newId).apply {
                infosDeBase.nom = deviceName
                // Get screen width in dp
                val displayMetrics = context.resources.displayMetrics
                val widthInDp = (displayMetrics.widthPixels / displayMetrics.density).toInt()
                infosDeBase.widthScreen = widthInDp

                // If it's a tablet or has "TAB" in the name, make it a receiver
                val isTablet = widthInDp > 400 || deviceName.contains("TAB", ignoreCase = true)
                infosDeBase.itsTablette = isTablet
                etatesMutable.itsReciverTelephone = isTablet

                // If it's a receiver, set nearbyWifiAdressIpConexion to empty to start as client
                if (isTablet) {
                    etatesMutable.nearbyWifiAdressIpConexion = ""
                } else {
                    etatesMutable.nearbyWifiAdressIpConexion = "host_${deviceName.replace(" ", "_")}"
                }
            }

            // Add to repository
            j_AppInstalleDonTelephoneRepository.modelDatas.add(newPhone)
            j_AppInstalleDonTelephoneRepository.updatePhones()

            return newPhone
        } catch (e: Exception) {
            logE("Failed to create new phone entry", e)
            return null
        }
    }

    private fun handlePayload(payload: String) {
        logD("Received payload: ${payload.take(50)}${if (payload.length > 50) "..." else ""}")
        WifiUpdateClientDisplayerStats.fromPayload(payload)?.let { (messageType, content) ->
            logD("Parsed message type: $messageType, content: ${content.take(20)}${if (content.length > 20) "..." else ""}")
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
        } ?: logE("Failed to parse payload: $payload")
    }

    fun updateDisplayController(update: ProductDisplayController.() -> ProductDisplayController) {
        viewModel._uiState.update { it.copy(productDisplayController = update(it.productDisplayController)) }
    }

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) {
        logI("Sending order to client: $orderName, data: $data")
        viewModelScope.launch {
            sendData("$orderName$data")
        }
    }

    fun initiateReconnection() {
        logI("Initiating reconnection, isReconnecting=${isReconnecting.get()}, retryCount=$retryCount/$maxRetries")
        if (isReconnecting.compareAndSet(false, true)) {
            reconnectionJob?.cancel()
            reconnectionJob = viewModelScope.launch {
                try {
                    delay(2000)
                    logI("Checking connection status before reconnection attempt")

                    if (!_connectionUiState.value.isConnected) {
                        val backoffDelay = calculateBackoffDelay()
                        logI("Connection still lost, waiting $backoffDelay ms before retry #${retryCount + 1}")
                        delay(backoffDelay)

                        _connectionUiState.update { it.copy(
                            connectionStatus = "Tentative de reconnexion #${retryCount + 1}",
                            reconnectionAttempts = retryCount + 1
                        )}

                        // First ensure we cleanup any existing connections
                        stopNearbyServices()
                        delay(500) // Brief delay to allow services to stop

                        when (lastConnectionMode) {
                            ConnectionMode.HOST -> {
                                logI("Reconnecting as HOST")
                                startAsHost()
                            }
                            ConnectionMode.CLIENT -> {
                                logI("Reconnecting as CLIENT")
                                startAsClient()
                            }
                            ConnectionMode.NONE -> {
                                logE("No connection mode set, giving up")
                                handleFinalDisconnection()
                            }
                        }

                        retryCount++

                        _connectionUiState.update { it.copy(
                            lastSuccessfulConnection = System.currentTimeMillis()
                        )}
                    } else {
                        logI("Reconnection unnecessary, connection restored")
                    }
                } catch (e: Exception) {
                    logE("Error during reconnection attempt: ${e.message}", e)
                    handleError("Échec de la reconnexion: ${e.message}")
                } finally {
                    isReconnecting.set(false)
                    logI("Reconnection attempt completed, isReconnecting set to false")
                }
            }
        } else {
            logI("Reconnection already in progress, skipping new attempt")
        }
    }

    private fun shouldAttemptReconnection(): Boolean {
        val shouldRetry = !_connectionUiState.value.isConnected &&
                retryCount < maxRetries &&
                lastConnectionMode != ConnectionMode.NONE
        logD("Should attempt reconnection? $shouldRetry (connected=${_connectionUiState.value.isConnected}, retries=$retryCount/$maxRetries, mode=$lastConnectionMode)")
        return shouldRetry
    }

    private fun handleDisconnection(disconnectedEndpointId: String) {
        logI("Handling disconnection for endpoint: $disconnectedEndpointId, current endpoint: $endpointId")
        if (endpointId == disconnectedEndpointId) {
            this.endpointId = null
            updateConnectionStatus("Déconnecté")
            _connectionUiState.update { it.copy(
                isConnected = false,
                lastSuccessfulConnection = System.currentTimeMillis()
            )}
            logI("Connection state updated to disconnected")

            if (shouldAttemptReconnection()) {
                logI("Will attempt reconnection")
                initiateReconnection()
            } else {
                logI("Will not attempt reconnection, handling final disconnection")
                handleFinalDisconnection()
            }
        } else {
            logI("Ignoring disconnection for different endpoint")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            logI("Connection initiated with endpoint: $endpointId, name: ${info.endpointName}")
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            updateConnectionStatus("Connexion en cours avec ${info.endpointName}...")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            logI("Connection result for endpoint $endpointId: ${result.status.statusCode}")
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    this@ConnectionManager.endpointId = endpointId
                    updateConnectionStatus("Connecté")
                    _connectionUiState.update { it.copy(isConnected = true) }
                    retryCount = 0
                    sendData("Connection established")
                    logI("Connection successfully established with endpoint $endpointId")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    logE("Connection rejected with endpoint $endpointId")
                    handleConnectionFailure("Connexion rejetée")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    logE("Connection error with endpoint $endpointId")
                    handleConnectionFailure("Erreur de connexion")
                }
                else -> {
                    logE("Unknown connection status code: ${result.status.statusCode}")
                    handleConnectionFailure("Erreur inconnue")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            logI("Disconnected from endpoint: $endpointId")
            handleDisconnection(endpointId)
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                try {
                    val rawMessage = String(payload.asBytes()!!)
                    logD("Payload received from $endpointId: ${rawMessage.take(50)}${if (rawMessage.length > 50) "..." else ""}")
                    handlePayload(rawMessage)
                } catch (e: Exception) {
                    logE("Error processing payload from $endpointId", e)
                }
            } else {
                logW("Received non-bytes payload from $endpointId: ${payload.type}")
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            when (update.status) {
                PayloadTransferUpdate.Status.SUCCESS -> {
                    logD("Payload transfer succeeded: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
                }
                PayloadTransferUpdate.Status.FAILURE -> {
                    logE("Payload transfer failed: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
                    handleTransferFailure()
                }
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    logV("Payload transfer in progress: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
                }
                PayloadTransferUpdate.Status.CANCELED -> {
                    logW("Payload transfer canceled: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
                    handleTransferFailure()
                }
            }
        }
    }

    private fun handleTransferFailure() {
        logE("Handling transfer failure")
        viewModelScope.launch {
            if (_connectionUiState.value.isConnected) {
                logI("Still connected despite transfer failure, checking connection health")
                checkConnectionHealth()
            }
        }
    }

    private fun checkConnectionHealth() {
        endpointId?.let { endpoint ->
            try {
                logD("Sending ping to test connection with endpoint $endpoint")
                sendData("ping")
            } catch (e: Exception) {
                logE("Failed to send ping to endpoint $endpoint", e)
                handleConnectionFailure("Perte de connexion détectée")
            }
        } ?: logW("No endpoint to check connection health")
    }

    private fun handleConnectionFailure(reason: String) {
        logE("Connection failure: $reason (retryCount=$retryCount, maxRetries=$maxRetries)")
        if (!isReconnecting.get() && retryCount < maxRetries) {
            logI("Initiating reconnection after connection failure")
            initiateReconnection()
        } else if (retryCount >= maxRetries) {
            logE("Maximum retry attempts reached, handling final disconnection")
            handleFinalDisconnection()
        }
    }

    private fun calculateBackoffDelay(): Long {
        val delay = baseRetryDelayMs * (1L shl retryCount.coerceAtMost(5))
        logD("Calculated backoff delay for retry #$retryCount: $delay ms")
        return delay
    }

    private fun handleFinalDisconnection() {
        logE("Handling final disconnection after exhausting reconnection attempts")
        disconnect()
        _connectionUiState.update {
            it.copy(
                isConnected = false,
                error = "Connexion perdue après plusieurs tentatives",
                connectionStatus = "Déconnecté définitivement"
            )
        }
    }

    private fun stopNearbyServices() {
        logI("Stopping all Nearby Connections services")
        try {
            if (isAdvertising) {
                logI("Stopping advertising")
                Nearby.getConnectionsClient(context).stopAdvertising()
                isAdvertising = false
            }

            if (isDiscovering) {
                logI("Stopping discovery")
                Nearby.getConnectionsClient(context).stopDiscovery()
                isDiscovering = false
            }

            // Small delay to ensure services are properly stopped
            Thread.sleep(100)
        } catch (e: Exception) {
            logE("Error stopping Nearby services", e)
        }
    }

    fun startAsHost() {
        viewModelScope.launch {
            logI("Starting as host")
            if (!checkRequiredPermissions()) {
                logE("Missing required permissions for Nearby Connections")
                val missingPermissions = getRequiredPermissions().filter { permission ->
                    ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
                }
                logE("Missing permissions: $missingPermissions")
                handleError("Permissions manquantes")
                return@launch
            }

            logI("All required permissions granted")
            lastConnectionMode = ConnectionMode.HOST
            _connectionUiState.update { it.copy(isHostPhone = true) }

            try {
                // First stop any existing advertising/discovery
                stopNearbyServices()

                val advertisingOptions = AdvertisingOptions.Builder()
                    .setStrategy(strategy)
                    .build()

                logI("Starting advertising with service ID: $serviceId and strategy: $strategy")
                Nearby.getConnectionsClient(context).startAdvertising(
                    "Host Device",
                    serviceId,
                    connectionLifecycleCallback,
                    advertisingOptions
                ).addOnSuccessListener {
                    isAdvertising = true
                    logI("Successfully started advertising as host")
                    updateConnectionStatus("En attente de connexion...")
                }.addOnFailureListener { e ->
                    isAdvertising = false
                    logE("Failed to start advertising: ${e.message}", e)
                    handleConnectionFailure("Erreur de démarrage du mode hôte: ${e.message}")
                }
            } catch (e: Exception) {
                logE("Exception during advertising: ${e.message}", e)
                handleConnectionFailure(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun startAsClient() {
        viewModelScope.launch {
            logI("Starting as client")
            if (!checkRequiredPermissions()) {
                logE("Missing required permissions for Nearby Connections")
                val missingPermissions = getRequiredPermissions().filter { permission ->
                    ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
                }
                logE("Missing permissions: $missingPermissions")
                handleError("Permissions manquantes")
                return@launch
            }

            logI("All required permissions granted")
            lastConnectionMode = ConnectionMode.CLIENT
            _connectionUiState.update { it.copy(isHostPhone = false) }

            try {
                // First stop any existing advertising/discovery
                stopNearbyServices()

                val discoveryOptions = DiscoveryOptions.Builder()
                    .setStrategy(strategy)
                    .build()

                logI("Starting discovery with service ID: $serviceId and strategy: $strategy")
                Nearby.getConnectionsClient(context).startDiscovery(
                    serviceId,
                    endpointDiscoveryCallback,
                    discoveryOptions
                ).addOnSuccessListener {
                    isDiscovering = true
                    logI("Successfully started discovery as client")
                    updateConnectionStatus("Recherche d'appareils...")
                }.addOnFailureListener { e ->
                    isDiscovering = false
                    logE("Failed to start discovery: ${e.message}", e)
                    handleConnectionFailure("Erreur de démarrage de la recherche: ${e.message}")
                }
            } catch (e: Exception) {
                logE("Exception during discovery: ${e.message}", e)
                handleConnectionFailure(e.message ?: "Erreur inconnue")
            }
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            logI("Endpoint found: $endpointId, name: ${info.endpointName}, serviceId: ${info.serviceId}")
            Nearby.getConnectionsClient(context)
                .requestConnection(
                    "Client Device",
                    endpointId,
                    connectionLifecycleCallback
                )
                .addOnSuccessListener {
                    logI("Successfully requested connection to endpoint: $endpointId")
                }
                .addOnFailureListener { e ->
                    logE("Failed to request connection to endpoint: $endpointId", e)
                    handleConnectionFailure("Erreur de connexion: ${e.message}")
                }
        }

        override fun onEndpointLost(endpointId: String) {
            logW("Endpoint lost: $endpointId")
        }
    }

    fun sendData(data: Any) {
        endpointId?.let { endpoint ->
            try {
                val payload = when (data) {
                    is String -> Payload.fromBytes(data.toByteArray())
                    else -> {
                        logE("Unsupported data type for sending: ${data.javaClass.simpleName}")
                        return
                    }
                }

                logD("Sending data to endpoint $endpoint: ${(data as? String)?.take(50)}${if ((data as? String)?.length ?: 0 > 50) "..." else ""}")
                Nearby.getConnectionsClient(context)
                    .sendPayload(endpoint, payload)
                    .addOnSuccessListener {
                        logD("Successfully sent payload to endpoint $endpoint")
                    }
                    .addOnFailureListener { e ->
                        logE("Failed to send payload to endpoint $endpoint", e)
                        handleTransferFailure()
                    }
            } catch (e: Exception) {
                logE("Exception while sending data to endpoint $endpoint", e)
                handleTransferFailure()
            }
        } ?: logE("Cannot send data: No connected endpoint")
    }

    fun disconnect() {
        logI("Disconnecting and cleaning up resources")
        connectionMonitorJob?.cancel()
        reconnectionJob?.cancel()

        try {
            stopNearbyServices()
            Nearby.getConnectionsClient(context).stopAllEndpoints()
            logI("All endpoints stopped")
        } catch (e: Exception) {
            logE("Error during disconnect/cleanup", e)
        }

        endpointId = null
        lastConnectionMode = ConnectionMode.NONE
        retryCount = 0
        isReconnecting.set(false)
        isAdvertising = false
        isDiscovering = false

        _connectionUiState.update {
            it.copy(
                isConnected = false,
                isHostPhone = false,
                connectionStatus = "Déconnecté",
                error = null
            )
        }
        logI("Disconnect complete, connection state reset")
    }

    // Modified to get appropriate permissions based on API level
    private fun getRequiredPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
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
    }



    private fun checkRequiredPermissions(): Boolean {
        val permissions = getRequiredPermissions()
        val missingPermissions = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            logW("Missing permissions: $missingPermissions")
        } else {
            logI("All required permissions granted")
        }

        return missingPermissions.isEmpty()
    }

    private fun updateConnectionStatus(status: String) {
        logI("Updating connection status: $status")
        _connectionUiState.update { it.copy(
            connectionStatus = status,
            error = null
        )}
    }

    private fun handleError(error: String) {
        logE("Error: $error")
        _connectionUiState.update { it.copy(
            error = error,
            connectionStatus = "Erreur: $error"
        )}
    }

    override fun onCleared() {
        super.onCleared()
        logI("ViewModel cleared, cleaning up resources")
        disconnect()
        connectionMonitorJob?.cancel()
        reconnectionJob?.cancel()
    }

    // Improved logging functions to ensure visibility in Logcat
    private fun logV(message: String) {
        Log.v(APP_TAG, "$TAG: $message")
    }

    private fun logD(message: String) {
        Log.d(APP_TAG, "$TAG: $message")
    }

    private fun logI(message: String) {
        Log.i(APP_TAG, "$TAG: $message")
    }

    private fun logW(message: String) {
        Log.w(APP_TAG, "$TAG: $message")
    }

    private fun logE(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(APP_TAG, "$TAG: $message", throwable)
        } else {
            Log.e(APP_TAG, "$TAG: $message")
        }
    }

    companion object {
        // Using a consistent prefix for all logs to make filtering easier
        const val APP_TAG = "MyApp"
        const val TAG = "ConnectionManager"

        // Static logging functions for use in other parts of the app
        fun logD(message: String) {
            Log.d(APP_TAG, "$TAG: $message")
        }

        fun logE(message: String, throwable: Throwable? = null) {
            if (throwable != null) {
                Log.e(APP_TAG, "$TAG: $message", throwable)
            } else {
                Log.e(APP_TAG, "$TAG: $message")
            }
        }
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
