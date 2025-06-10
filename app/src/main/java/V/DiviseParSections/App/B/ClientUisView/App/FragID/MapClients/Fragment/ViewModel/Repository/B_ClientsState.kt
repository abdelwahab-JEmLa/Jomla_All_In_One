package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class B_ClientsState (
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3
){
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _clients = mutableStateOf<List<B_ClientInfosProtoJuin3>>(emptyList())
    val clients: State<List<B_ClientInfosProtoJuin3>> = _clients

    val clientsVal by derivedStateOf { _clients.value }

    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: State<Float> = _loadingProgress

    val isEmpty: Boolean by derivedStateOf { _clients.value.isEmpty() }
    val size: Int by derivedStateOf { _clients.value.size }
    val maxId: Long by derivedStateOf {
        _clients.value.maxOfOrNull { it.id } ?: 0L
    }
    val isLoading: Boolean by derivedStateOf { _clients.value.isEmpty() && !_isInitialized.value }

    private val _isInitialized = mutableStateOf(false)
    val isInitialized: State<Boolean> = _isInitialized

    init {
        composScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    val clients =
                        model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList()
                    updateClients(clients)
                }
            }
        }

    }
    fun updateClients(newClients: List<B_ClientInfosProtoJuin3>) {
        _clients.value = newClients
        _isInitialized.value = true
    }

    fun addClient(client: B_ClientInfosProtoJuin3) {
        _clients.value += client
    }

    fun updateClient(updatedClient: B_ClientInfosProtoJuin3) {
        _clients.value = _clients.value.map { client ->
            if (client.id == updatedClient.id) updatedClient else client }
    }

    fun findClientById(id: Long): B_ClientInfosProtoJuin3? {
        return _clients.value.find { it.id == id }
    }
    fun removeClient(clientId: Long) { _clients.value = _clients.value.filter { it.id != clientId } }
}
