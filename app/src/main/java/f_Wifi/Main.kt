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
    private var retryCount = 0
    private val MAX_RETRIES = 3
    private val RETRY_DELAY = 3000L // 3 seconds

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    private var isDiscovering = false
    private var isAdvertising = false
    private var pendingRetry: Job? = null

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d("ScrollSync", "Connection initiated with endpoint: $endpointId, name: ${info.endpointName}")
            scope.launch(Dispatchers.Main) {
                try {
                    activeEndpointId = endpointId
                    _connectionState.value = ConnectionState.Connecting(endpointId)
                    connectionsClient.acceptConnection(endpointId, payloadCallback)
                        .addOnFailureListener { e ->
                            handleConnectionFailure(endpointId, e, "Failed to accept connection")
                        }
                } catch (e: Exception) {
                    handleConnectionFailure(endpointId, e, "Error during connection initiation")
                }
            }
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            scope.launch(Dispatchers.Main) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        Log.d("ScrollSync", "Successfully connected to endpoint: $endpointId")
                        retryCount = 0
                        activeEndpointId = endpointId
                        _connectionState.value = ConnectionState.Connected(endpointId)
                        stopDiscoveryAndAdvertising()
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        handleConnectionFailure(endpointId, Exception("Connection rejected"), "Connection rejected")
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        handleConnectionFailure(endpointId, Exception("Connection error"), "General connection error")
                    }
                    else -> {
                        handleConnectionFailure(
                            endpointId,
                            Exception("Unknown error code: ${result.status.statusCode}"),
                            "Unknown connection result"
                        )
                    }
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            scope.launch(Dispatchers.Main) {
                if (endpointId == activeEndpointId) {
                    Log.d("ScrollSync", "Disconnected from endpoint: $endpointId")
                    activeEndpointId = null
                    _connectionState.value = ConnectionState.Disconnected
                    retryConnection()
                }
            }
        }
    }

    private fun handleConnectionFailure(endpointId: String, error: Exception, message: String) {
        scope.launch(Dispatchers.Main) {
            Log.e("ScrollSync", "$message: ${error.message}")
            if (endpointId == activeEndpointId) {
                activeEndpointId = null
                _connectionState.value = ConnectionState.Error("$message: ${error.message}")
                retryConnection()
            }
        }
    }

    private fun retryConnection() {
        pendingRetry?.cancel()
        pendingRetry = scope.launch {
            if (retryCount < MAX_RETRIES) {
                retryCount++
                delay(RETRY_DELAY * retryCount)
                Log.d("ScrollSync", "Attempting retry #$retryCount")
                when {
                    isAdvertising -> startAdvertising("filter_service")
                    isDiscovering -> startDiscovery("filter_service")
                }
            } else {
                Log.e("ScrollSync", "Max retries reached")
                _connectionState.value = ConnectionState.Error("Failed to establish connection after $MAX_RETRIES attempts")
                stop()
            }
        }
    }

    private fun stopDiscoveryAndAdvertising() {
        connectionsClient.apply {
            if (isDiscovering) stopDiscovery()
            if (isAdvertising) stopAdvertising()
        }
        isDiscovering = false
        isAdvertising = false
    }

    fun startAdvertising(serviceId: String) {
        scope.launch(Dispatchers.Main) {
            try {
                stopDiscoveryAndAdvertising()

                val advertisingOptions = AdvertisingOptions.Builder()
                    .setStrategy(Strategy.P2P_POINT_TO_POINT)
                    .build()

                isAdvertising = true
                _connectionState.value = ConnectionState.Searching

                connectionsClient.startAdvertising(
                    Build.MODEL,
                    serviceId,
                    connectionLifecycleCallback,
                    advertisingOptions
                ).addOnFailureListener { e ->
                    handleConnectionFailure("", e, "Failed to start advertising")
                }
            } catch (e: Exception) {
                handleConnectionFailure("", e, "Error during advertising setup")
            }
        }
    }

    fun startDiscovery(serviceId: String) {
        scope.launch(Dispatchers.Main) {
            try {
                stopDiscoveryAndAdvertising()

                val discoveryOptions = DiscoveryOptions.Builder()
                    .setStrategy(Strategy.P2P_POINT_TO_POINT)
                    .build()

                isDiscovering = true
                _connectionState.value = ConnectionState.Searching

                connectionsClient.startDiscovery(
                    serviceId,
                    endpointDiscoveryCallback,
                    discoveryOptions
                ).addOnFailureListener { e ->
                    handleConnectionFailure("", e, "Failed to start discovery")
                }
            } catch (e: Exception) {
                handleConnectionFailure("", e, "Error during discovery setup")
            }
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            scope.launch(Dispatchers.Main) {
                if (_connectionState.value !is ConnectionState.Connected) {
                    try {
                        connectionsClient.requestConnection(
                            Build.MODEL,
                            endpointId,
                            connectionLifecycleCallback
                        ).addOnFailureListener { e ->
                            handleConnectionFailure(endpointId, e, "Failed to request connection")
                        }
                    } catch (e: Exception) {
                        handleConnectionFailure(endpointId, e, "Error requesting connection")
                    }
                }
            }
        }

        override fun onEndpointLost(endpointId: String) {
            if (endpointId == activeEndpointId) {
                handleConnectionFailure(endpointId, Exception("Endpoint lost"), "Lost connection to endpoint")
            }
        }
    }

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Searching : ConnectionState()
        data class Connecting(val endpointId: String) : ConnectionState()
        data class Connected(val endpointId: String) : ConnectionState()
        data class Error(val message: String) : ConnectionState()
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
                handleConnectionFailure(endpointId, e,"cc1")
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
                    handleConnectionFailure(endpointId, Exception("Transfer failed"),"cc")
                }
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    val progress = (update.bytesTransferred * 100 / update.totalBytes)
                    Log.d("ScrollSync", "Transfer progress: $progress%")
                }
            }
        }
    }


}
