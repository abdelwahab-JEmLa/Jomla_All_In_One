package P2_EStorePresentationToClient.ViewModel

import com.example.clientjetpack.Models.ProductDisplayController
import P2_EStorePresentationToClient.Modules.ConnectionManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ClientPresentationViewModel(context: Context) : ViewModel() {

    private val _displayerStats = MutableStateFlow(ProductDisplayController())
    val displayerStats = _displayerStats.asStateFlow()

    private val connectionManager = ConnectionManager(
        context,
        onPayloadReceivedInteger = {
            updateScrollPositionFromRecived(it)
        },
        onReceive = { receivedId ->
            _displayerStats.update { currentState ->
                currentState.copy(windowsProductIdWhoInfoDisplayed = receivedId)
            }
        }
    )


    fun sendOrderToClient(name: String,data: Any) {
        viewModelScope.launch {
            connectionManager.sendOrder(name,data)
        }
    }

    private fun updateScrollPositionFromRecived(position: Int): Unit {
        _displayerStats.update { it.copy(clientDisplayerScrollPosition = position) }
    }

    fun sendScrollPositionToClient(position: Int) {
        viewModelScope.launch {
            connectionManager.sendData(position)
        }
    }
    fun updateTypePhone(type: Boolean = false): Unit {
        _displayerStats.update { it.copy(isHostPhone = type) }
    }

    fun startAsHost() = connectionManager.startAsHost()
    fun startAsClient() = connectionManager.startAsClient()
    fun disconnect() = connectionManager.disconnect()
}

