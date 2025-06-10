package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update.deleteData
import Z_CodePartageEntreApps.DataBase.Juin3.Proto._1_5_Vendeur._1_5_Vendeur
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.IRecordingHandler
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.Parent.AppSettingsSaverModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.views.MapView
import java.util.Date

// ===============================================
// COMPOSE STATE CLASSES
// ===============================================

@Stable
data class PanelsGroupeButtonT(
    val key: Keys,
    val isVisible: Boolean = false,
) {
    enum class Keys {
        MapSecteursPolygenHandelButtons,
        autres,
    }
}

@Stable
class ClientsStateT {
    private val _clients = mutableStateOf<List<B_ClientInfosProtoJuin3>>(emptyList())
    val clients: State<List<B_ClientInfosProtoJuin3>> = _clients

    // Propriétés dérivées calculées automatiquement
    val isEmpty: Boolean by derivedStateOf { _clients.value.isEmpty() }
    val size: Int by derivedStateOf { _clients.value.size }
    val maxId: Long by derivedStateOf {
        _clients.value.maxOfOrNull { it.id } ?: 0L
    }
    val isLoading: Boolean by derivedStateOf { _clients.value.isEmpty() && !_isInitialized.value }

    private val _isInitialized = mutableStateOf(false)
    val isInitialized: State<Boolean> = _isInitialized

    fun updateClients(newClients: List<B_ClientInfosProtoJuin3>) {
        _clients.value = newClients
        _isInitialized.value = true
    }

    fun addClient(client: B_ClientInfosProtoJuin3) {
        _clients.value = _clients.value + client
    }

    fun updateClient(updatedClient: B_ClientInfosProtoJuin3) {
        _clients.value = _clients.value.map { client ->
            if (client.id == updatedClient.id) updatedClient else client
        }
    }

    fun removeClient(clientId: Long) {
        _clients.value = _clients.value.filter { it.id != clientId }
    }

    fun findClientById(id: Long): B_ClientInfosProtoJuin3? {
        return _clients.value.find { it.id == id }
    }
}

@Stable
class TransactionsStateT {
    private val _transactions = mutableStateOf<List<C3_TransactionCommercial>>(emptyList())
    val transactions: State<List<C3_TransactionCommercial>> = _transactions

    val size: Int by derivedStateOf { _transactions.value.size }
    val isEmpty: Boolean by derivedStateOf { _transactions.value.isEmpty() }

    fun updateTransactions(newTransactions: List<C3_TransactionCommercial>) {
        _transactions.value = newTransactions
    }

    fun getLastTransactionForClient(clientId: Long): C3_TransactionCommercial? {
        return _transactions.value
            .filter { it.clientAcheteurID == clientId }
            .maxByOrNull { it.timestamps }
    }
}

@Stable
class AppStateT{
    private val _activeCompt = mutableStateOf<_1_5_Vendeur?>(_1_5_Vendeur())
    val activeCompt: State<_1_5_Vendeur?> = _activeCompt

    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: State<Float> = _loadingProgress

    private val _secteursList = mutableStateOf<List<E1SecteurDeClients>>(emptyList())
    val secteursList: State<List<E1SecteurDeClients>> = _secteursList

    private val _panelsGroupeList = mutableStateOf(
        listOf(
            PanelsGroupeButton(PanelsGroupeButton.Keys.MapSecteursPolygenHandelButtons, false),
            PanelsGroupeButton(PanelsGroupeButton.Keys.autres, false),
        )
    )
    val panelsGroupeList: State<List<PanelsGroupeButton>> = _panelsGroupeList

    fun updateActiveCompt(newActiveCompt: _1_5_Vendeur?) {
        _activeCompt.value = newActiveCompt
    }

    fun updateLoadingProgress(progress: Float) {
        _loadingProgress.value = progress
    }

    fun updateSecteursList(newSecteurs: List<E1SecteurDeClients>) {
        _secteursList.value = newSecteurs
    }

    fun updatePanelsGroupe(newPanels: List<PanelsGroupeButton>) {
        _panelsGroupeList.value = newPanels
    }
}

// ===============================================
// MAIN VIEWMODEL WITH COMPOSE STATE
// ===============================================

class MapClientsViewModelT(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val recordingHandler: IRecordingHandler,
    val appDatabase: AppDatabase
) : ViewModel() {

    // Repository references
    val groupeRepositorysProtoAvJuin3 = a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
    val b_ClientDataBaseRepository = a_MasterRepositorysGrpProtoJuin3.b_ClientInfosProtoJuin3Repository
    val secteurRepo = groupeRepositorysProtoAvJuin3.repositorys_Model.e1SecteurDeClientsRepository
    val c3_BonAchate_List = groupeRepositorysProtoAvJuin3.repositorys_Model.c3TransactionCommercialRepository.modelDatasSnapList

    // Compose States
    val clientsState = ClientsStateT()
    val transactionsState = TransactionsStateT()
    val appState = AppStateT()

    // Backward compatibility - delegates to compose state
    val bProto_ClientsDataBase: List<B_ClientInfosProtoJuin3>
        get() = clientsState.clients.value

    val secteurList = secteurRepo.listCollected

    // UI State variables
    var auClickeCaUpdateClientPar by mutableStateOf(B_ClientInfosProtoJuin3.TypeDeSonMagasine.ATAYAT_MOUKASSARAT)
    var mapReloadTrigger by mutableIntStateOf(0)
    var afficheLesJoursAuNoms by mutableStateOf(true)
    var filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList by mutableStateOf<List<String>>(emptyList())

    // StateFlow for reactive updates
    private var _currentActiveSectorId = MutableStateFlow<Long?>(null)
    val currentActiveSectorId: StateFlow<Long?> = _currentActiveSectorId

    // Dialog states
    val showSecteurDialog = mutableStateOf(false)
    val showAddSecteurDialog = mutableStateOf(false)

    // Derived properties using compose state
    val isClientsLoading: Boolean by derivedStateOf { clientsState.isLoading }
    val clientsCount: Int by derivedStateOf { clientsState.size }
    val hasClients: Boolean by derivedStateOf { !clientsState.isEmpty }

    init {
        initializeDataObservers()
    }

    private fun initializeDataObservers() {
        // Observe clients data
        viewModelScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    val clients = model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList()
                    clientsState.updateClients(clients)
                    appState.updateLoadingProgress(model.progress)
                }
            }
        }

        // Observe transactions data
        viewModelScope.launch {
            snapshotFlow {
                a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
                    .repositorys_Model
                    .c3TransactionCommercialRepository
                    .modelDatasSnapList
                    .toList()
            }.collect { transactionList ->
                transactionsState.updateTransactions(transactionList)
            }
        }

        // Observe vendeur data
        viewModelScope.launch {
            snapshotFlow {
                a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
                    .repositorys_Model
                    .repository_1_5_Vendeur
                    .modelDatasSnapList
                    .toList()
            }.collect { list ->
                val activeCompt = _1_5_Vendeur.getActiveComptPourCeTelephone(list)
                appState.updateActiveCompt(activeCompt)
            }
        }
    }

    // ===============================================
    // BUSINESS LOGIC METHODS
    // ===============================================

    fun getLastTransaction(client: B_ClientInfosProtoJuin3): C3_TransactionCommercial? {
        return transactionsState.getLastTransactionForClient(client.id)
    }

    fun updateData(client: B_ClientInfosProtoJuin3) {
        viewModelScope.launch {
            b_ClientDataBaseRepository.addOrUpdateData(client)
            clientsState.updateClient(client)
        }
        mapReloadTrigger++
    }

    fun onClickAddMarkerButton(mapView: MapView) {
        val center = mapView.mapCenter
        if (center.latitude == 0.0) return

        try {
            val newID = if (clientsState.isEmpty) {
                1L
            } else {
                clientsState.maxId + 1
            }

            val newnom = "ز.$newID"

            val newClientAchteur = B_ClientInfosProtoJuin3().apply {
                id = newID
                nom = newnom
                cUnClientTemporaire = true
                typeDeSonMagasine = auClickeCaUpdateClientPar
                latitude = center.latitude
                longitude = center.longitude
                title = newnom
                snippet = "ClientAchteur temporaire"
            }

            viewModelScope.launch {
                b_ClientDataBaseRepository.addOrUpdateData(newClientAchteur)
                clientsState.addClient(newClientAchteur)
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    fun deleteUnSeulData(data: B_ClientInfosProtoJuin3) {
        viewModelScope.launch {
            b_ClientDataBaseRepository.deleteData(data)
            clientsState.removeClient(data.id)
        }
    }

    fun updateLongAppSetting(value: Long, name: String = "clientBuyerNowId") {
        viewModelScope.launch {
            try {
                val appSettingsSaverModel = AppSettingsSaverModel(
                    id = 1,
                    name = name,
                    valueLong = value,
                    date = Date()
                )

                Firebase.database.getReference("A_AppSettingsSaverModel")
                    .child(appSettingsSaverModel.id.toString())
                    .setValue(appSettingsSaverModel)
                    .await()
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun updateActiveComptIdClientOuvertPoutCeCompt(data: Long) {
        val currentActiveCompt = appState.activeCompt.value ?: return
        val updatedCompt = currentActiveCompt.copy(idClientOuvertPoutCeCompt = data)

        appState.updateActiveCompt(updatedCompt)

        a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin.repositorys_Model
            .repository_1_5_Vendeur
            .updateUnSeulData(updatedCompt)
    }

    // ===============================================
    // FILTER AND VISIBILITY METHODS
    // ===============================================

    enum class VisibleClientsNow(val icon: Any, val couleur: Color = Color.White) {
        AFFICHE_CIBLE_POUR_VENDEUR(Icons.Default.Map, Color.Red),
        CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX(Icons.Default.SettingsBackupRestore, Color.Blue),
        showNonAbsentClientsOnly(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl),
        affichePourCollecteurCommendes(LottieJsonGetterR_Raw_Icons.afficheFenetre),
        showAtayClients(LottieJsonGetterR_Raw_Icons.atay),
        showClientsOnlyAcEtateCIBLE_POUR_2(Icons.Default.CheckCircleOutline),
        showAlimentionlients(LottieJsonGetterR_Raw_Icons.alimentation),
        showClientsWithConfirmedProducts(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl),
        showAll(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl);
    }

    @Composable
    fun getFilteredClients(filter: VisibleClientsNow): List<B_ClientInfosProtoJuin3> {
        val clients by clientsState.clients
        val transactions by transactionsState.transactions
        val activeCompt by appState.activeCompt

        return remember(clients, transactions, activeCompt, filter) {
            when (filter) {
                VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> {
                    clients.filter { client ->
                        val lastTransaction = transactions
                            .filter { it.clientAcheteurID == client.id }
                            .maxByOrNull { it.timestamps }
                        lastTransaction?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.Cible
                    }
                }
                VisibleClientsNow.showAtayClients -> {
                    clients.filter { it.typeDeSonMagasine == B_ClientInfosProtoJuin3.TypeDeSonMagasine.ATAYAT_MOUKASSARAT }
                }
                VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> {
                    clients.filter { client ->
                        val lastTransaction = transactions
                            .filter { it.clientAcheteurID == client.id }
                            .maxByOrNull { it.timestamps }
                        lastTransaction?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.CIBLE_POUR_2
                    }
                }
                VisibleClientsNow.showAll -> clients
                else -> clients
            }
        }
    }

    // ===============================================
    // CLEANUP METHODS
    // ===============================================

    fun cleanupResources() {
        viewModelScope.launch {
            try {
                cancelActiveOperations()
                mapReloadTrigger = 0
                filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList = emptyList()
                _currentActiveSectorId.value = null
                println("Map resources cleaned up successfully")
            } catch (e: Exception) {
                println("Error during resource cleanup: ${e.message}")
            }
        }
    }

    fun cancelActiveOperations() {
        try {
            mapReloadTrigger++
            afficheLesJoursAuNoms = true
            _currentActiveSectorId.value = null
            println("Active operations canceled successfully")
        } catch (e: Exception) {
            println("Error canceling active operations: ${e.message}")
        }
    }

    fun startRecordIfNot() {
        recordingHandler.startRecordIfNot()
    }

    // ===============================================
    // COMPOSE UTILITIES
    // ===============================================

    @Composable
    fun rememberClientById(clientId: Long): B_ClientInfosProtoJuin3? {
        val clients by clientsState.clients
        return remember(clients, clientId) {
            clients.find { it.id == clientId }
        }
    }

    @Composable
    fun rememberClientsCount(): Int {
        val clients by clientsState.clients
        return remember(clients) { clients.size }
    }

    @Composable
    fun rememberIsLoading(): Boolean {
        val isInitialized by clientsState.isInitialized
        val clients by clientsState.clients
        return remember(isInitialized, clients) {
            !isInitialized && clients.isEmpty()
        }
    }
}
