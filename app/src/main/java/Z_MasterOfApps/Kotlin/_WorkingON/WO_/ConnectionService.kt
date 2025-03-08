package Z_MasterOfApps.Kotlin._WorkingON.WO_

import Z_MasterOfApps.Kotlin._WorkingON.WO_.ConnectionManager
import Z_MasterOfApps.Kotlin._WorkingON.WO_.ConnectionUiState
import Z_MasterOfApps.Kotlin._WorkingON.WO_.WifiUpdateClientDisplayerStats
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.Models.ProductDisplayController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Service responsible for handling all connection-related functionality.
 * This extracts connection logic from HeadViewModel to improve separation of concerns.
 */
class ConnectionService(
    private val context: Context,
    private val onDisplayControllerUpdate: (ProductDisplayController.() -> ProductDisplayController) -> Unit,
    private val onErrorUpdate: (String?) -> Unit
) : ViewModel() {
    private val tag = "ConnectionService"
    
    // Pass connection events to this service via the connection manager
    private val connectionManager = ConnectionManager(
        context = context,
        onPayloadReceiveRaw = { payload -> handlePayload(payload) }
    )
    
    // Expose connection state for observers
    val connectionState: StateFlow<ConnectionUiState> = connectionManager.connectionUiState
    
    /**
     * Handles incoming payloads and updates the ProductDisplayController accordingly
     */
    private fun handlePayload(payload: String) {
        WifiUpdateClientDisplayerStats.fromPayload(payload)?.let { (messageType, content) ->
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
        } ?: Log.d(tag, "📩 Unhandled message received: $payload")
    }
    
    /**
     * Updates the ProductDisplayController via callback to the parent ViewModel
     */
    private fun updateDisplayController(update: ProductDisplayController.() -> ProductDisplayController) {
        onDisplayControllerUpdate(update)
    }
    
    /**
     * Sends data over the connection
     */
    fun sendData(data: String) {
        viewModelScope.launch {
            connectionManager.sendData(data)
        }
    }
    
    /**
     * Starts this device as a host in the connection
     * Requires Android Tiramisu (API 33) or higher
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() = connectionManager.startAsHost()
    
    /**
     * Starts this device as a client in the connection
     * Requires Android Tiramisu (API 33) or higher
     */
    fun startAsClient() = connectionManager.startAsClient()
    
    /**
     * Disconnects from the current connection
     */
    fun disconnect() = connectionManager.disconnect()
    
    override fun onCleared() {
        super.onCleared()
        Log.d(tag, "🧹 Nettoyage du ConnectionService")
        disconnect()
    }

}
