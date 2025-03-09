package Z_MasterOfApps.Kotlin._WorkingON.WO_

import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Payload

class DataSender(private val connectionManager: ConnectionManager) {
    
    fun sendData(data: Any) {
        connectionManager.endpointId?.let { endpoint ->
            try {
                val payload = when (data) {
                    is String -> Payload.fromBytes(data.toByteArray())
                    else -> {
                        connectionManager.logE("Unsupported data type for sending: ${data.javaClass.simpleName}")
                        return
                    }
                }

                connectionManager.logD("Sending data to endpoint $endpoint: ${(data as? String)?.take(50)}${if ((data as? String)?.length ?: 0 > 50) "..." else ""}")
                Nearby.getConnectionsClient(connectionManager.context)
                    .sendPayload(endpoint, payload)
                    .addOnSuccessListener {
                        connectionManager.logD("Successfully sent payload to endpoint $endpoint")
                    }
                    .addOnFailureListener { e ->
                        connectionManager.logE("Failed to send payload to endpoint $endpoint", e)
                        connectionManager.handleTransferFailure()
                    }
            } catch (e: Exception) {
                connectionManager.logE("Exception while sending data to endpoint $endpoint", e)
                connectionManager.handleTransferFailure()
            }
        } ?: connectionManager.logE("Cannot send data: No connected endpoint")
    }
}
