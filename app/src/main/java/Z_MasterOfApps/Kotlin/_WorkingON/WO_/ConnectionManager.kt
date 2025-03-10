package Z_MasterOfApps.Kotlin._WorkingON.WO_

import Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephoneRepository
import Z_MasterOfApps.Kotlin._WorkingON.WO_.Conexion.ConnectionComponents
import Z_MasterOfApps.Kotlin._WorkingON.WO_.Conexion.NearbyPayloadCallback
import Z_MasterOfApps.Kotlin._WorkingON.WO_.Conexion.PayloadHandler
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.Models.ProductDisplayController
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class ConnectionManager(
    val viewModel: HeadViewModel,
    val context: Context,
    private val j_AppInstalleDonTelephoneRepository: J_AppInstalleDonTelephoneRepository
) : ViewModel() {

    val _connectionUiState = MutableStateFlow(ConnectionUiState())
    var endpointId: String? = null
    private val serviceId = "com.example.clientjetpack"
    private val strategy = Strategy.P2P_POINT_TO_POINT

    private val isReconnecting = AtomicBoolean(false)
    private var reconnectionJob: Job? = null
    private var connectionMonitorJob: Job? = null
    private var retryCount = 0
    private val maxRetries = 50
    private val baseRetryDelayMs = 3000L

    var lastConnectionMode: ConnectionMode = ConnectionMode.NONE
    var isAdvertising = false
    var isDiscovering = false

    // Create instances of extracted components
     val permissionHandler = PermissionHandler(context, this)
    private val payloadHandler = PayloadHandler(this)
    private val displayController = DisplayController(this)
    val dataSender = DataSender(this)
    private val payloadCallback = NearbyPayloadCallback(this, payloadHandler)

    // New components class for handling device management
     val connectionComponents = ConnectionComponents(context, this, j_AppInstalleDonTelephoneRepository)

    enum class ConnectionMode {
        HOST, CLIENT, NONE
    }

    init {
        initializeModule()
    }

    private fun initializeModule() {
        viewModelScope.launch {
            logI("Initializing ConnectionManager")

            // Use the extracted components class for initialization
            connectionComponents.initializeModule()
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
                                connectionComponents.startAsHost(connectionLifecycleCallback, permissionHandler)
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

    fun startConnectionMonitoring() {
        logI("Starting connection monitoring job")
        connectionMonitorJob?.cancel()
        connectionMonitorJob = viewModelScope.launch {
            delay(5000) // Initial delay to let connection stabilize
            while (isActive) {
                logD("Connection check: isConnected=${_connectionUiState.value.isConnected}, lastMode=$lastConnectionMode")
                if (!_connectionUiState.value.isConnected && lastConnectionMode != ConnectionMode.NONE) {
                    logI("Connection not detected, initiating reconnection")
                    initiateReconnection()
                }
                delay(5000) // Check every 5 seconds instead of 10
            }
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

    fun cleanupExistingConnections() {
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

    // Using the extracted components class for client mode
    fun startAsClient() {
        connectionComponents.startAsClient(connectionLifecycleCallback, endpointDiscoveryCallback, permissionHandler)
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

     val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            logI("Connection initiated with endpoint: $endpointId, name: ${info.endpointName}")
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

                    // Add delay before sending first message
                    viewModelScope.launch {
                        delay(1000) // Wait 1 second to stabilize connection
                        dataSender.sendData("Connection established")
                    }

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
                // Add delay before checking health
                delay(500)  // Give the connection a moment to stabilize
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

    fun handleConnectionFailure(reason: String) {
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
    fun updateConnectionStatus(status: String) {
        logI("Updating connection status: $status")
        _connectionUiState.update { it.copy(
            connectionStatus = status,
            error = null
        )}
    }

    fun handleError(error: String) {
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
