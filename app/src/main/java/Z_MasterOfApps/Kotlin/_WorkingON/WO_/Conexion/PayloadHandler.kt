package Z_MasterOfApps.Kotlin._WorkingON.WO_.Conexion

import Z_MasterOfApps.Kotlin._WorkingON.WO_.ConnectionManager
import Z_MasterOfApps.Kotlin._WorkingON.WO_.WifiUpdateClientDisplayerStats
import com.example.clientjetpack.Models.ProductDisplayController

class PayloadHandler(private val connectionManager: ConnectionManager) {
    
    fun handlePayload(payload: String) {
        connectionManager.logD("Received payload: ${payload.take(50)}${if (payload.length > 50) "..." else ""}")

        // Handle special control messages first
        if (payload == "ping" || payload == "Connection established") {
            connectionManager.logD("Received control message: $payload")
            return
        }

         WifiUpdateClientDisplayerStats.fromPayload(payload)?.let { (messageType, content) ->
            connectionManager.logD("Parsed message type: $messageType, content: ${content.take(20)}${if (content.length > 20) "..." else ""}")
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
        } ?: connectionManager.logD("Unrecognized payload format: $payload")
    }

    private fun updateDisplayController(update: ProductDisplayController.() -> ProductDisplayController) {
        connectionManager.updateDisplayController(update)
    }
}
