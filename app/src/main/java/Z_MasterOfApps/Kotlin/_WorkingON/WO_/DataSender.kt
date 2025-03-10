package Z_MasterOfApps.Kotlin._WorkingON.WO_

import androidx.lifecycle.viewModelScope
import com.google.android.gms.nearby.Nearby
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DataSender(private val connectionManager: ConnectionManager) {

    // In DataSender class
    fun sendData(message: String) {
        val endpoint = connectionManager.endpointId
        if (endpoint != null) {
            try {
                // Check message size and handle large messages appropriately
                if (message.length > 1024) { // If message is large
                    connectionManager.logD("Large message detected, breaking into chunks")
                    // Split message into chunks and send separately with delay between
                    // This is a simple solution - proper chunking would be better
                    val chunks = message.chunked(1024)
                    connectionManager.viewModelScope.launch {
                        for (chunk in chunks) {
                            Nearby.getConnectionsClient(connectionManager.context)
                                .sendPayload(endpoint, com.google.android.gms.nearby.connection.Payload.fromBytes(chunk.toByteArray()))
                            delay(100) // Add small delay between chunks
                        }
                    }
                } else {
                    // Send small messages as normal
                    connectionManager.logD("Sending data to endpoint $endpoint: ${message.take(50)}${if (message.length > 50) "..." else ""}")
                    Nearby.getConnectionsClient(connectionManager.context)
                        .sendPayload(endpoint, com.google.android.gms.nearby.connection.Payload.fromBytes(message.toByteArray()))
                }
            } catch (e: Exception) {
                connectionManager.logE("Failed to send payload to endpoint $endpoint", e)
                connectionManager.handleTransferFailure()
            }
        } else {
            connectionManager.logE("Cannot send data: No connected endpoint")
        }
    }
}
