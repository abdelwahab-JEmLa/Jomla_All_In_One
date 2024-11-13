package f_Wifi.Main

import A0_Models.ProductDisplayController
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import f_Wifi.ConnectionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ClientPresentationViewModel(context: Context) : ViewModel() {

    private val _productDisplayController = MutableStateFlow(ProductDisplayController())
    val productDisplayController = _productDisplayController.asStateFlow()

    private val connectionManager = ConnectionManager(
        context,
        onPayloadReceivedInteger = {
            updateScrollPositionFromRecived(it)
        },
        onReceive = { receivedId ->
            _productDisplayController.update { currentState ->
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
        _productDisplayController.update { it.copy(scrollPosition = position) }
    }

    fun sendScrollPositionToClient(position: Int) {
        viewModelScope.launch {
            connectionManager.sendData(position)
        }
    }
    fun updateTypePhone(type: Boolean = false): Unit {
        _productDisplayController.update { it.copy(isHostPhone = type) }
    }

    fun startAsHost() = connectionManager.startAsHost()
    fun startAsClient() = connectionManager.startAsClient()
    fun disconnect() = connectionManager.disconnect()
}

