package H2_ActivePackages.P2_EStorePresentationToClient.ViewModel

import H1_APPMainCompnenents.Models.ProductDisplayController
import H2_ActivePackages.P2_EStorePresentationToClient.Modules.ConnectionManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ClientPresentationViewModel(context: Context) : ViewModel() {

    private val _productPresentationController = MutableStateFlow(ProductDisplayController())
    val productPresentationController = _productPresentationController.asStateFlow()

    private val connectionManager = ConnectionManager(
        context,
        onPayloadReceivedInteger = {
            updateScrollPositionFromRecived(it)
        },
        onReceive = { receivedId ->
            _productPresentationController.update { currentState ->
                currentState.copy(prodectIdWhoInfoDisplayed = receivedId)
            }
        }
    )


    fun sendOrderToClient(name: String,data: Any) {
        viewModelScope.launch {
            connectionManager.sendOrder(name,data)
        }
    }

    private fun updateScrollPositionFromRecived(position: Int): Unit {
        _productPresentationController.update { it.copy(scrollPosition = position) }
    }

    fun sendScrollPositionToClient(position: Int) {
        viewModelScope.launch {
            connectionManager.sendData(position)
        }
    }
    fun updateTypePhone(type: Boolean = false): Unit {
        _productPresentationController.update { it.copy(isHostPhone = type) }
    }

    fun startAsHost() = connectionManager.startAsHost()
    fun startAsClient() = connectionManager.startAsClient()
    fun disconnect() = connectionManager.disconnect()
}

