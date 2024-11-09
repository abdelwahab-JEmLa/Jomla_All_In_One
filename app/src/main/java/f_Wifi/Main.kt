package f_Wifi

import android.content.Context
import android.os.Build
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class NearbyConnectionService(
    private val context: Context,
    private val onDataReceived: (String) -> Unit
) {
    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var activeEndpointId: String? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    // Keep track of ongoing discovery or advertising
    private var isDiscovering = false
    private var isAdvertising = false

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d("ScrollSync", "Connection initiated with endpoint: $endpointId, name: ${info.endpointName}")
            activeEndpointId = endpointId
            // Automatically accept the connection
            connectionsClient.acceptConnection(endpointId, payloadCallback)
                .addOnSuccessListener {
                    Log.d("ScrollSync", "Connection accepted with endpoint: $endpointId")
                }
                .addOnFailureListener { e ->
                    Log.e("ScrollSync", "Failed to accept connection with endpoint: $endpointId", e)
                    handleConnectionFailure(endpointId, e)
                }
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            Log.d("ScrollSync", "Connection result for endpoint: $endpointId, status: ${result.status}")
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d("ScrollSync", "Successfully connected to endpoint: $endpointId")
                    activeEndpointId = endpointId
                    _connectionState.value = ConnectionState.Connected(endpointId)
                    // Stop discovery once connected
                    stopDiscovery()
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.e("ScrollSync", "Connection rejected with endpoint: $endpointId")
                    handleConnectionFailure(endpointId, Exception("Connection rejected"))
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.e("ScrollSync", "Connection error with endpoint: $endpointId")
                    handleConnectionFailure(endpointId, Exception("Connection error"))
                }
                else -> {
                    Log.e("ScrollSync", "Unknown connection result with endpoint: $endpointId, code: ${result.status.statusCode}")
                    handleConnectionFailure(endpointId, Exception("Unknown error"))
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d("ScrollSync", "Disconnected from endpoint: $endpointId")
            if (endpointId == activeEndpointId) {
                activeEndpointId = null
                _connectionState.value = ConnectionState.Disconnected
                // Restart discovery or advertising based on previous state
                restartConnectionProcess()
            }
        }
    }

    private fun handleConnectionFailure(endpointId: String, error: Exception) {
        if (endpointId == activeEndpointId) {
            activeEndpointId = null
            _connectionState.value = ConnectionState.Error(error.message ?: "Unknown error")
            // Restart the connection process after a delay
            scope.launch {
                delay(1000)
                restartConnectionProcess()
            }
        }
    }

    private fun restartConnectionProcess() {
        when {
            isAdvertising -> startAdvertising("filter_service")
            isDiscovering -> startDiscovery("filter_service")
        }
    }

    private fun stopDiscovery() {
        if (isDiscovering) {
            connectionsClient.stopDiscovery()
            isDiscovering = false
        }
    }

    private fun stopAdvertising() {
        if (isAdvertising) {
            connectionsClient.stopAdvertising()
            isAdvertising = false
        }
    }

    fun startAdvertising(serviceId: String) {
        Log.d("ScrollSync", "Starting advertising with serviceId: $serviceId")
        // Stop any ongoing discovery or advertising
        stop()

        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()

        connectionsClient.startAdvertising(
            Build.MODEL,
            serviceId,
            connectionLifecycleCallback,
            advertisingOptions
        ).addOnSuccessListener {
            Log.d("ScrollSync", "Successfully started advertising")
            isAdvertising = true
            _connectionState.value = ConnectionState.Searching
        }.addOnFailureListener { e ->
            Log.e("ScrollSync", "Failed to start advertising", e)
            isAdvertising = false
            _connectionState.value = ConnectionState.Error("Failed to start advertising: ${e.message}")
            // Retry after delay
            retryConnection { startAdvertising(serviceId) }
        }
    }

    fun startDiscovery(serviceId: String) {
        Log.d("ScrollSync", "Starting discovery with serviceId: $serviceId")
        // Stop any ongoing discovery or advertising
        stop()

        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()

        connectionsClient.startDiscovery(
            serviceId,
            endpointDiscoveryCallback,
            discoveryOptions
        ).addOnSuccessListener {
            Log.d("ScrollSync", "Successfully started discovery")
            isDiscovering = true
            _connectionState.value = ConnectionState.Searching
        }.addOnFailureListener { e ->
            Log.e("ScrollSync", "Failed to start discovery", e)
            isDiscovering = false
            _connectionState.value = ConnectionState.Error("Failed to start discovery: ${e.message}")
            // Retry after delay
            retryConnection { startDiscovery(serviceId) }
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d("ScrollSync", "Found endpoint: $endpointId, name: ${info.endpointName}")
            if (_connectionState.value !is ConnectionState.Connected) {
                connectionsClient.requestConnection(
                    Build.MODEL,
                    endpointId,
                    connectionLifecycleCallback
                ).addOnSuccessListener {
                    Log.d("ScrollSync", "Successfully requested connection to endpoint: $endpointId")
                }.addOnFailureListener { e ->
                    Log.e("ScrollSync", "Failed to request connection to endpoint: $endpointId", e)
                    handleConnectionFailure(endpointId, e)
                }
            }
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d("ScrollSync", "Lost endpoint: $endpointId")
            if (endpointId == activeEndpointId) {
                handleConnectionFailure(endpointId, Exception("Endpoint lost"))
            }
        }
    }

    private fun retryConnection(action: () -> Unit) {
        scope.launch {
            delay(5000) // Wait 5 seconds before retry
            action()
        }
    }

    fun stop() {
        Log.d("ScrollSync", "Stopping all endpoints")
        connectionsClient.stopAllEndpoints()
        stopDiscovery()
        stopAdvertising()
        activeEndpointId = null
        _connectionState.value = ConnectionState.Disconnected
    }

    fun sendData(endpointId: String, data: String) {
        Log.d("ScrollSync", "Attempting to send data: $data to endpoint: $endpointId")
        if (_connectionState.value !is ConnectionState.Connected || endpointId != activeEndpointId) {
            Log.e("ScrollSync", "Cannot send data - not connected to endpoint: $endpointId")
            return
        }

        val payload = Payload.fromBytes(data.toByteArray())
        connectionsClient.sendPayload(endpointId, payload)
            .addOnSuccessListener {
                Log.d("ScrollSync", "Successfully sent data to endpoint: $endpointId")
            }
            .addOnFailureListener { e ->
                Log.e("ScrollSync", "Failed to send data to endpoint: $endpointId", e)
                handleConnectionFailure(endpointId, e)
            }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (endpointId != activeEndpointId) {
                Log.w("ScrollSync", "Received payload from inactive endpoint: $endpointId")
                return
            }

            Log.d("ScrollSync", "Received payload from: $endpointId")
            when (payload.type) {
                Payload.Type.BYTES -> {
                    payload.asBytes()?.let { bytes ->
                        val data = String(bytes)
                        Log.d("ScrollSync", "Decoded payload data: $data")
                        onDataReceived(data)
                    }
                }
                else -> Log.w("ScrollSync", "Received unsupported payload type")
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (endpointId != activeEndpointId) return

            when (update.status) {
                PayloadTransferUpdate.Status.SUCCESS -> {
                    Log.d("ScrollSync", "Transfer completed successfully")
                }
                PayloadTransferUpdate.Status.FAILURE -> {
                    Log.e("ScrollSync", "Transfer failed")
                    handleConnectionFailure(endpointId, Exception("Transfer failed"))
                }
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    val progress = (update.bytesTransferred * 100 / update.totalBytes)
                    Log.d("ScrollSync", "Transfer progress: $progress%")
                }
            }
        }
    }

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Searching : ConnectionState()
        data class Connected(val endpointId: String) : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }
}
