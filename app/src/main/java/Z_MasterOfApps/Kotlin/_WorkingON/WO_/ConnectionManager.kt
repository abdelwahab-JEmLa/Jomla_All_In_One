package Z_MasterOfApps.Kotlin._WorkingON.WO_


import Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephoneRepository
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
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class ConnectionManager(
    val viewModel: HeadViewModel,
    val context: Context,
    private val j_AppInstalleDonTelephoneRepository: J_AppInstalleDonTelephoneRepository
) : ViewModel() {

    private val _connectionUiState = MutableStateFlow(ConnectionUiState())
    var endpointId: String? = null
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

    // Create instances of extracted components
    private val permissionHandler = PermissionHandler(context ,this)
    private val payloadHandler = PayloadHandler(this)
    private val displayController = DisplayController(this)
    val dataSender = DataSender(this)
    private val payloadCallback = NearbyPayloadCallback(this, payloadHandler)

    private enum class ConnectionMode {
        HOST, CLIENT, NONE
    }

    init {
        viewModelScope.launch {
            logI("Initializing ConnectionManager")

            val timeout = 60000L // 60 second timeout as a safety measure
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

    // Use the extracted components' methods instead of local implementations
    fun updateDisplayController(update: ProductDisplayController.() -> ProductDisplayController) {
        displayController.updateDisplayController(update)
    }

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) {
        displayController.sendOrderToClientDisplayer(orderName, data)
    }

    fun initiateReconnection() {
        logI("Initiating reconnection, isReconnecting=${isReconnecting.get()}, retryCount=$retryCount/$maxRetries")
        if (isReconnecting.compareAndSet(false, true)) {
            reconnectionJob?.cancel()
            reconnectionJob = viewModelScope.launch {
                try {
                    delay(2000)
                    logI("Checking connection status before reconnection attempt")

                    // Check if we're TRULY disconnected before attempting reconnection
                    val isActuallyConnected = checkActualConnectionStatus()

                    if (!isActuallyConnected) {
                        val backoffDelay = calculateBackoffDelay()
                        logI("Connection still lost, waiting $backoffDelay ms before retry #${retryCount + 1}")
                        delay(backoffDelay)

                        _connectionUiState.update { it.copy(
                            connectionStatus = "Tentative de reconnexion #${retryCount + 1}",
                            reconnectionAttempts = retryCount + 1
                        )}

                        // First ensure we properly clean up any existing connections
                        cleanupExistingConnections()
                        delay(1000) // Longer delay to ensure services are fully stopped

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
                        logI("Reconnection unnecessary, connection actually exists")
                        // Make sure UI state reflects the actual connection status
                        _connectionUiState.update { it.copy(
                            isConnected = true,
                            connectionStatus = "Connecté",
                            error = null
                        )}
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

    private fun checkActualConnectionStatus(): Boolean {
        return try {
            // Check if endpoint ID is not null AND if the Nearby API confirms we have connected endpoints
            val hasConnectedEndpoints = endpointId != null &&
                    Nearby.getConnectionsClient(context).let { client ->
                        // This is a best-effort check - the Nearby API doesn't have a direct "isConnected" method
                        // so we're using a small ping test
                        if (endpointId != null) {
                            try {
                                client.sendPayload(endpointId!!, com.google.android.gms.nearby.connection.Payload.fromBytes("ping".toByteArray()))
                                true
                            } catch (e: Exception) {
                                logE("Failed ping test", e)
                                false
                            }
                        } else false
                    }

            logI("Connection status check: hasConnectedEndpoints=$hasConnectedEndpoints")
            hasConnectedEndpoints
        } catch (e: Exception) {
            logE("Error checking connection status", e)
            false
        }
    }

    private fun shouldAttemptReconnection(): Boolean {
        val shouldRetry = !_connectionUiState.value.isConnected &&
                retryCount < maxRetries &&
                lastConnectionMode != ConnectionMode.NONE
        logD("Should attempt reconnection? $shouldRetry (connected=${_connectionUiState.value.isConnected}, retries=$retryCount/$maxRetries, mode=$lastConnectionMode)")
        return shouldRetry
    }

    private fun cleanupExistingConnections() {
        logI("Thoroughly cleaning up all existing connections")
        try {
            // Step 1: Stop discovery and advertising
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

            // Step 2: Disconnect from any existing endpoints
            endpointId?.let { endpoint ->
                logI("Explicitly disconnecting from endpoint: $endpoint")
                try {
                    Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpoint)
                } catch (e: Exception) {
                    logE("Error disconnecting from specific endpoint", e)
                }
            }

            // Step 3: Stop all endpoints as a final cleanup
            logI("Stopping all endpoints")
            Nearby.getConnectionsClient(context).stopAllEndpoints()

            // Reset connection state
            endpointId = null

            // Update UI state to reflect disconnection
            _connectionUiState.update { it.copy(
                isConnected = false,
                connectionStatus = "Déconnecté temporairement"
            )}

            // Small delay to ensure services are properly stopped
            Thread.sleep(200)
        } catch (e: Exception) {
            logE("Error during connection cleanup", e)
        }
    }

    // 4. Modify the startAsClient method to handle duplicate endpoints better
    fun startAsClient() {
        viewModelScope.launch {
            logI("Starting as client")
            if (!permissionHandler.checkRequiredPermissions()) {
                logE("Missing required permissions for Nearby Connections")
                val missingPermissions =permissionHandler. getRequiredPermissions().filter { permission ->
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
                // First stop any existing discovery/connections
                cleanupExistingConnections()

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

            // Check if we're already connected to this endpoint or any other endpoint
            if (this@ConnectionManager.endpointId == endpointId) {
                logI("Already connected to this endpoint, skipping connection request")
                return
            }

            // If we're connected to a different endpoint, disconnect from it first
            if (this@ConnectionManager.endpointId != null) {
                logI("Already connected to a different endpoint, disconnecting first")
                cleanupExistingConnections()

                // Use viewModelScope to launch a coroutine for the delay
                viewModelScope.launch {
                    delay(500) // Brief delay to allow disconnection to complete

                    // Continue with connection request after delay
                    requestConnection(endpointId)
                }
            } else {
                // Directly request connection if not connected to any endpoint
                requestConnection(endpointId)
            }
        }

        private fun requestConnection(endpointId: String) {
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
                    // Only handle as a connection failure if it's not already connected error
                    if (e.message?.contains("STATUS_ALREADY_CONNECTED_TO_ENDPOINT") != true) {
                        handleConnectionFailure("Erreur de connexion: ${e.message}")
                    } else {
                        logI("Already connected to endpoint, updating connection state")
                        // If we're already connected, update the UI state to reflect this
                        _connectionUiState.update { it.copy(
                            isConnected = true,
                            connectionStatus = "Connecté"
                        )}
                    }
                }
        }

        override fun onEndpointLost(endpointId: String) {
            logW("Endpoint lost: $endpointId")

            // Only trigger reconnection if this is the endpoint we were connected to
            if (this@ConnectionManager.endpointId == endpointId) {
                logI("Lost connection to current endpoint, will attempt reconnection")
                this@ConnectionManager.endpointId = null
                _connectionUiState.update { it.copy(
                    isConnected = false,
                    connectionStatus = "Connexion perdue"
                )}

                // Only initiate reconnection if we're not already reconnecting
                if (!isReconnecting.get()) {
                    initiateReconnection()
                }
            }
        }
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
                    dataSender.sendData("Connection established")
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
    fun handleTransferFailure() {
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
                dataSender.sendData("ping")
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
    // 7. Improve the connection initialization logic in startAsHost method
    fun startAsHost() {
        viewModelScope.launch {
            logI("Starting as host")
            if (!permissionHandler.checkRequiredPermissions()) {
                logE("Missing required permissions for Nearby Connections")
                val missingPermissions = permissionHandler.getRequiredPermissions().filter { permission ->
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
                // Clean up any existing connections before starting advertising
                cleanupExistingConnections()

                val advertisingOptions = AdvertisingOptions.Builder()
                    .setStrategy(strategy)
                    .build()

                logI("Starting advertising with service ID: $serviceId and strategy: $strategy")
                Nearby.getConnectionsClient(context).startAdvertising(
                    "Host Device",  // Changed from "Host ????????????" to a clean string
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

    private val APP_TAG = "MyApp"

    fun logV(message: String) {
        Log.v(APP_TAG, " $message")
    }

    fun logD(message: String) {
        Log.d(APP_TAG, " $message")
    }

    fun logI(message: String) {
        Log.i(APP_TAG, " $message")
    }

    fun logW(message: String) {
        Log.w(APP_TAG, " $message")
    }

    fun logE(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(APP_TAG, " $message", throwable)
        } else {
            Log.e(APP_TAG, " $message")
        }
    }


}
