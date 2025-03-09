package Z_MasterOfApps.Kotlin._WorkingON.WO_

import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.Models.ProductDisplayController
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DisplayController(private val connectionManager: ConnectionManager) {
    
    fun updateDisplayController(update: ProductDisplayController.() -> ProductDisplayController) {
        connectionManager.viewModel._uiState.update { it.copy(productDisplayController = update(it.productDisplayController)) }
    }

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) {
        connectionManager.logI("Sending order to client: $orderName, data: $data")
        connectionManager.viewModelScope.launch {
            connectionManager.dataSender.sendData("$orderName$data")
        }
    }
}
