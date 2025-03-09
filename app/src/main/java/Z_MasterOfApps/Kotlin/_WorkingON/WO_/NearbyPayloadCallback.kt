package Z_MasterOfApps.Kotlin._WorkingON.WO_

import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate

class NearbyPayloadCallback(
    private val connectionManager: ConnectionManager,
    private val payloadHandler: PayloadHandler
) : PayloadCallback() {
    
    override fun onPayloadReceived(endpointId: String, payload: Payload) {
        if (payload.type == Payload.Type.BYTES) {
            try {
                val rawMessage = String(payload.asBytes()!!)
                connectionManager.logD("Payload received from $endpointId: ${rawMessage.take(50)}${if (rawMessage.length > 50) "..." else ""}")
                payloadHandler.handlePayload(rawMessage)
            } catch (e: Exception) {
                connectionManager.logE("Error processing payload from $endpointId", e)
            }
        } else {
            connectionManager.logW("Received non-bytes payload from $endpointId: ${payload.type}")
        }
    }

    override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
        when (update.status) {
            PayloadTransferUpdate.Status.SUCCESS -> {
                connectionManager.logD("Payload transfer succeeded: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
            }
            PayloadTransferUpdate.Status.FAILURE -> {
                connectionManager.logE("Payload transfer failed: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
                connectionManager.handleTransferFailure()
            }
            PayloadTransferUpdate.Status.IN_PROGRESS -> {
                connectionManager.logV("Payload transfer in progress: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
            }
            PayloadTransferUpdate.Status.CANCELED -> {
                connectionManager.logW("Payload transfer canceled: endpointId=$endpointId, bytesTransferred=${update.bytesTransferred}")
                connectionManager.handleTransferFailure()
            }
        }
    }
}
