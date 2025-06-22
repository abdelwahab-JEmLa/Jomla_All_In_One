package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Proto.ViewModel.Repository.A2_Passive

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
class B_ClientsStateCompoRepository (
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3
){
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<B_ClientInfosProtoJuin3>>(emptyList())
    val datasState: State<List<B_ClientInfosProtoJuin3>> = this._datas
    val datasValue by derivedStateOf { this._datas.value }

    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: State<Float> = _loadingProgress
    val isLoading: Boolean by derivedStateOf { this._datas.value.isEmpty() && !_isInitialized.value }
    private val _isInitialized = mutableStateOf(false)
    val isInitialized: State<Boolean> = _isInitialized

    val isEmpty: Boolean by derivedStateOf { this._datas.value.isEmpty() }
    val size: Int by derivedStateOf { this._datas.value.size }
    val maxId: Long by derivedStateOf {
        this._datas.value.maxOfOrNull { it.id } ?: 0L
    }

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

    fun findClientById(id: Long): B_ClientInfosProtoJuin3? {
        return this._datas.value.find { it.id == id }
    }
    fun removeClient(clientId: Long) { this._datas.value = this._datas.value.filter { it.id != clientId } }

    fun updateClients(newClients: List<B_ClientInfosProtoJuin3>) {
        this._datas.value = newClients
        _isInitialized.value = true
    }

    fun addClient(client: B_ClientInfosProtoJuin3) {
        this._datas.value += client
    }

    fun updateClient(updatedClient: B_ClientInfosProtoJuin3) {
        this._datas.value = this._datas.value.map { client ->
            if (client.id == updatedClient.id)
                updatedClient.withProperKeyFireBaseAndTimeTamp()
            else client
        }
    }
}
