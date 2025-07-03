package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update.deleteData
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.IRecordingHandler
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.Z_AutreStatesCompoRepository
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.Parent.AppSettingsSaverModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.views.MapView
import java.util.Date

@Stable
data class UiState(
    val b_ClientInfosProtoJuin3List: List<HClientInfos> = emptyList(),
    val c3_TransactionCommercialList: List<GBonVent> = emptyList(),
    val secteursList: List<E1SecteurDeClients> = emptyList(),
    val panelsGroupeList: List<Z_AutreStatesCompoRepository.PanelsGroupeButton> = emptyList(),
    val mainLoadingProgress: Float = 0f,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MapClientsViewModel(
    val aCentral: ACentralFacade,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val recordingHandler: IRecordingHandler,
    val appDatabase: AppDatabase
) : ViewModel() {
    val getter = aCentral.getter
    val setter = aCentral.setter

    val gBonVentRepo = getter.id8BonVentRepository
    // Repository references
    val groupeRepositorysProtoAvJuin3 =
        a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
    val b_ClientDataBaseRepository =
        a_MasterRepositorysGrpProtoJuin3.b_ClientInfosProtoJuin3Repository
    val secteurRepo = groupeRepositorysProtoAvJuin3.repositorys_Model.e1SecteurDeClientsRepository
    val c3_BonAchate_List =getter.id8BonVentRepository.datasValue

    // Compose States
    val transactionsState = getter.id8BonVentRepository
    val clientsState = getter.iD2ClientRepository
    val appState = getter.zAppComptRepositoryComposable

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val bProto_ClientsDataBase: List<HClientInfos>
        get() = clientsState.datasState.value

    // UI State variables
    var auClickeCaUpdateClientPar by mutableStateOf(HClientInfos.TypeDeSonMagasine.ATAYAT_MOUKASSARAT)
    var mapReloadTrigger by mutableIntStateOf(0)
    var afficheLesJoursAuNoms by mutableStateOf(true)
    var filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList by mutableStateOf<List<String>>(
        emptyList()
    )

    private fun updateUiState() {
        _uiState.value = UiState(
            b_ClientInfosProtoJuin3List = clientsState.datasState.value,
            c3_TransactionCommercialList = transactionsState.datasValue,
            mainLoadingProgress = getter.loadingProgress!!,
            isLoading = clientsState.isLoading,
            error = null
        )
    }

    init {
        initializeDataObservers()
    }

    private fun initializeDataObservers() {
        viewModelScope.launch {
            snapshotFlow { transactionsState.datasValue }.collect { transactionsList ->
                updateUiState()
            }
        }

        viewModelScope.launch {
            snapshotFlow { getter.zAppComptRepositoryComposable.currentAppCompt }.collect { transactionsList ->
                updateUiState()
            }
        }

        // Observe clients data
        viewModelScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    val clients =
                        model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList()
                    clientsState.updateClients(clients)

                    // Update UI State
                    updateUiState()
                }
            }
        }

    }

    fun getLastTransaction(client: HClientInfos): GBonVent? {
        return getter.getClientLastTransaction(
            client.id,
        )
    }

    fun updateData(client: HClientInfos) {
        viewModelScope.launch {
            b_ClientDataBaseRepository.addOrUpdateData(client)
            clientsState.updateClient(client)
            updateUiState()
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

            val newClientAchteur = HClientInfos().apply {
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
                updateUiState()
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    fun deleteUnSeulData(data: HClientInfos) {
        viewModelScope.launch {
            b_ClientDataBaseRepository.deleteData(data)
            clientsState.removeClient(data.id)
            updateUiState()
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

    // ===============================================
    // CLEANUP METHODS
    // ===============================================

    fun cleanupResources() {
        viewModelScope.launch {
            try {
                cancelActiveOperations()
                mapReloadTrigger = 0
                filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList = emptyList()
                updateUiState()
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
            println("Active operations canceled successfully")
        } catch (e: Exception) {
            println("Error canceling active operations: ${e.message}")
        }
    }

    fun startRecordIfNot() {
        recordingHandler.startRecordIfNot()
    }

    fun ouvreBonVent(idClientOuSonMarqueMapEstOuvert: Long): Unit {
        if (idClientOuSonMarqueMapEstOuvert == 0L) {
            setter.cleanFermeAppComptOnVentBonVent()
        } else {
        }

        updateLongAppSetting(idClientOuSonMarqueMapEstOuvert)
        startRecordIfNot()
    }

    fun ajoutUnBonVentHistorique(clickedClient: Long, newEtate: GBonVent.EtateActuellementEst) {
    }

    fun update_bOuvertDialogMapMarqueHClientKey(clientOldId: Long) {
        setter.update_bOuvertDialogMapMarqueHClientKey(clientOldId)
    }
    fun clear_onVentGBonVentKeyId_EtbOuvertDialogMapMarqueHClientKey(): Unit {
        setter.clear_onVentGBonVentKeyId_EtbOuvertDialogMapMarqueHClientKey()
    }

    fun dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey() {
        setter.dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey()
    }

    // Add these methods to MapClientsViewModel class

    fun ajoutUnBonVentHistorique(buttonNewKeyGenerateur: String, clickedClient: Long, newEtate: GBonVent.EtateActuellementEst) {
        setter.ajouteNewBonVent(buttonNewKeyGenerateur, clickedClient, newEtate)
    }

    fun updateBonVentHistorique(buttonNewKeyGenerateur: String, clickedClient: Long, newEtate: GBonVent.EtateActuellementEst) {
        setter.updateComptAppErExistKey(buttonNewKeyGenerateur, clickedClient, newEtate)
    }
}
