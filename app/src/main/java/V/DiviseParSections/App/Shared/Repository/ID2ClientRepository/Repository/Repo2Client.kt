package V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository

import EntreApps.Shared.Models.M2Client
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter.Companion.getListDesParentKeys
import V.DiviseParSections.App.Shared.Repository.A.Base.functions_central.runtime_throw_Erreur_Pour_Regle_Le_Real_Bug
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Init.initializeDataReturn
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory.DataBaseInitFactory_2ClientProtoJuil28
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo2Client(
    val dataBaseCreationFactoryProtoJuil28: DataBaseInitFactory_2ClientProtoJuil28,
    val dataBaseCreationFactory: dataBaseCreationFactoryMID2ClientRepository,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val zAppComptRepositoryComposable: Repo9AppCompt,
) {
    val TAG = "Repo2Client"
    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M2Client>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        repoScope.launch {
            dataBaseCreationFactoryProtoJuil28.dao.getAllFlow().collect { _datas.value = it }
        }
    }
    fun refresh_Datas() {
        repoScope.launch {
            try {
                dataBaseCreationFactory.dao.deleteAll()

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = emptyList()
                }

                val freshDataFromFirebase = dataBaseCreationFactory.initializeDataReturn()

                dataBaseCreationFactory.dao.insertAll(freshDataFromFirebase)

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = freshDataFromFirebase
                }

            } catch (e: Exception) {
            }
        }
    }


    fun addNew(data: M2Client) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        dataBaseCreationFactory.set(dataUpdate)
    }

    fun updateIfExist(data: M2Client) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            repoScope.launch {
                withContext(Dispatchers.Main) {
                    runtime_throw_Erreur_Pour_Regle_Le_Real_Bug("updateIfExist")
                }
            }
            return
        }

        val updatedItem = data.copy(
            keyID = datasValue[existingIndex].keyID,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply {
                    this[existingIndex] = updatedItem
                }
            }
        }

        dataBaseCreationFactory.set(updatedItem)
    }

    //--------------------------------------------------------------------------
    fun removeClient(clientId: Long) {
        this._datas.value = this._datas.value.filter { it.id != clientId }
    }

    // In Repo2Client class (first file)
    fun delete_M2Client(data: M2Client) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            repoScope.launch {
                withContext(Dispatchers.Main) {
                    runtime_throw_Erreur_Pour_Regle_Le_Real_Bug("delete_M2Client: Client not found")
                }
            }
            return
        }

        repoScope.launch {
            // Remove from UI state
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply {
                    removeAt(existingIndex)
                }
            }
        }

        // Delete from database
        dataBaseCreationFactory.delete(data)
    }
    
    fun updateClients(newClients: List<M2Client>) {
        this._datas.value = newClients
        _isInitialized.value = true
    }

    fun addClient(client: M2Client) {
        this._datas.value += client
    }

    fun upsert(data: M2Client) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        val existingIndex = datasValue.indexOfFirst { it.keyID == dataUpdate.keyID }

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    if (existingIndex >= 0) {
                        this[existingIndex] = dataUpdate
                    } else {
                        add(dataUpdate)
                    }
                }
            }
        }
        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    private fun ancienRepoUpsertUneDataEtReturnVID(dataUpdate: M2Client) {
        dataBaseCreationFactory.set(dataUpdate)
    }

    fun updateClient(updatedClient: M2Client) {
        this._datas.value = this._datas.value.map { client ->
            if (client.id == updatedClient.id)
                updatedClient.with_Trigger_RealTime()
                    .copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
            else client
        }
    }

    fun findHClientInfosByKeyDeClient(parentID2ClientKeyByParent: String): M2Client {
        return datasValue.find { it.getTempKeyByParent() == parentID2ClientKeyByParent }
            ?: throw IllegalArgumentException("Client not found with keyByParent: $parentID2ClientKeyByParent")
    }

    fun findHClientInfosByKey(key: String): M2Client {
        val parentID2ClientKeyByParent = getListDesParentKeys(key)[M2Client.keyModel]
        return datasValue.find { it.getTempKeyByParent() == parentID2ClientKeyByParent }
            ?: throw IllegalArgumentException("Client not found with keyByParent: $parentID2ClientKeyByParent")
    }

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
    val datasState: State<List<M2Client>> = _datas
}

