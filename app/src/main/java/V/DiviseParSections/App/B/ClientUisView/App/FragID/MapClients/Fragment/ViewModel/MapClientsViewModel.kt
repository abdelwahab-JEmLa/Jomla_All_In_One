package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.ActiveDatasFragNewProto
import Application4.App.Fragment.ID1.Fragment.ViewModel.FlowsFunctions_ActiveDatasFragNewProto
import Application4.App.Fragment.ID1.Fragment.ViewModel.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View.get_Found_Or_Default_M8BonVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.M2Client
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Update.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Update.deleteData
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.IRecordingHandler
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.Z_AutreStatesCompoRepository
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.Parent.AppSettingsSaverModel
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Filter
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.views.MapView
import java.util.Date

@Stable
data class UiState(
    val b_ClientInfosProtoJuin3List: List<M2Client> = emptyList(),
    val c3_TransactionCommercialList: List<M8BonVent> = emptyList(),
    val secteursList: List<E1SecteurDeClients> = emptyList(),
    val panelsGroupeList: List<Z_AutreStatesCompoRepository.PanelsGroupeButton> = emptyList(),
    val markerStatusDialogActiveM2Client: M2Client? = null,
    val mainLoadingProgress: Float = 0f,
    val isLoading: Boolean = false,
    val error: String? = null,
    val m2Client_In_ShowEditMarkerMode: M2Client? = null,
)

class MapClientsViewModel(
    private val context: Context,
    val aCentralFacade: ACentralFacade,
    val focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val recordingHandler: IRecordingHandler,
    val appDatabase: AppDatabase,
    val repositorysMainSetter_NewProtoPatterns: RepositorysMainSetter_NewProtoPatterns = RepositorysMainSetter_NewProtoPatterns(
        appDatabase = appDatabase,
        context = context
    ),
    val active_Datas: ActiveDatasFragNewProto = ActiveDatasFragNewProto(),
) : ViewModel() {
    val repo2Client = aCentralFacade.repositorysMainGetter.repo2Client
    val getter = aCentralFacade.repositorysMainGetter
    val setter = aCentralFacade.repositorysMainSetter

    // Repository references
    val groupeRepositorysProtoAvJuin3 =
        a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
    val b_ClientDataBaseRepository =
        a_MasterRepositorysGrpProtoJuin3.b_ClientInfosProtoJuin3Repository
    val secteurRepo = groupeRepositorysProtoAvJuin3.repositorys_Model.e1SecteurDeClientsRepository
    val c3_BonAchate_List = getter.repo8BonVent.datasValue
    val transactionsState = getter.repo8BonVent

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val bProto_ClientsDataBase: List<M2Client>
        get() = this.repo2Client.datasState.value

    // UI State variables
    var auClickeCaUpdateClientPar by mutableStateOf(M2Client.TypeDeSonMagasine.ATAYAT_MOUKASSARAT)
    var mapReloadTrigger by mutableIntStateOf(0)
    var afficheLesJoursAuNoms by mutableStateOf(true)
    var filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList by mutableStateOf<List<String>>(
        emptyList()
    )

    private fun updateUiState() {
        _uiState.value = _uiState.value.copy(
            b_ClientInfosProtoJuin3List = this.repo2Client.datasState.value,
            c3_TransactionCommercialList = transactionsState.datasValue,
            mainLoadingProgress = getter.loadingProgress!!,
            isLoading = this.repo2Client.isLoading,
            error = null
        )
    }

    fun update_uiState_m2Client_In_ShowEditMarkerMode(m2Client_In_ShowEditMarkerMode: M2Client? = null) {
        _uiState.value = _uiState.value.copy(
            m2Client_In_ShowEditMarkerMode = m2Client_In_ShowEditMarkerMode,
        )
    }
    fun update_active_Compt(compt: M09AppCompt) {
        active_Datas.active_M9Compt = compt
        repositorysMainSetter_NewProtoPatterns.update_M9AppCompt(compt)
    }

    fun update_filter_marqueClient(mode: VisibleClientsNow) {
        val compt = active_Datas.active_M9Compt ?: return
        update_active_Compt(compt.copy(filter_marqueClient_Name = mode.name))
    }

    init {
        initializeDataObservers()
        collectActiveM9Compt()
    }

    private fun collectActiveM9Compt() {
        viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto
                .getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
                    dao_M9AppCompt = appDatabase.dao_M9AppCompt(),
                    activeDatasFragNewProto = active_Datas,
                ).collect { compt ->
                    compt?.filter_marqueClient_Name
                        ?.let { name -> VisibleClientsNow.entries.find { it.name == name } }
                        ?.let { active_Datas.filter_marqueClient_enum_entries = it }
                }
        }
    }
    fun set_M2Client_UiState_In_MarkerStatusDialog(data: M2Client?) {
        _uiState.value = _uiState.value.copy(
            markerStatusDialogActiveM2Client = data
        )
    }

    fun clear_UiState_MarkerStatusDialog_Active_M2Client() {
        _uiState.value = _uiState.value.copy(
            markerStatusDialogActiveM2Client = null
        )
    }

    private fun initializeDataObservers() {
        viewModelScope.launch {
            snapshotFlow { transactionsState.datasValue }.collect { transactionsList ->
                updateUiState()
            }
        }

        viewModelScope.launch {
            snapshotFlow { getter.repo9AppCompt.currentAppCompt }.collect { transactionsList ->
                updateUiState()
            }
        }

        // Observe clients data
        viewModelScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    val clients =
                        model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList()
                    this@MapClientsViewModel.repo2Client.updateClients(clients)

                    // Update UI State
                    updateUiState()
                }
            }
        }

    }

    fun getLastTransaction(m2Client: M2Client): M8BonVent? {
        return getter.get_Last_M8BonVent_Par_M2Client(
            m2Client
        )
    }

    fun updateData(client: M2Client) {
        viewModelScope.launch {
            b_ClientDataBaseRepository.addOrUpdateData(client)
            this@MapClientsViewModel.repo2Client.updateClient(client)
            updateUiState()
        }
        mapReloadTrigger++
    }

    fun onClickAddMarkerButton(mapView: MapView) {
        val center = mapView.mapCenter
        if (center.latitude == 0.0) return

        try {
            val newID = if (this.repo2Client.isEmpty) {
                1L
            } else {
                this.repo2Client.maxId + 1
            }

            val newnom = "ز.$newID"

            val newClientAchteur = M2Client().apply {
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
                repo2Client.upsert(newClientAchteur)
                delay(1500)
                add_Cible(newClientAchteur)
                delay(1500)
                updateUiState()
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    fun add_Cible(m2Client:M2Client): Unit {
        val activeCentralValues = focusedValuesGetter.active_Central_Values

        val actuelle_Ciblage_MaxPosition = activeCentralValues
            .actuelle_Ciblage_MaxPosition

        val newPosition = actuelle_Ciblage_MaxPosition + 1
        val found_Or_Default_M8BonVent = get_Found_Or_Default_M8BonVent(
            aCentralFacade = aCentralFacade,
            relative_M2Client = m2Client,
            etateActuellementEst = M8BonVent.EtateActuellementEst.Cible,
        )        ?: return


        aCentralFacade.repositorysMainSetter
            .addNew_M8BonVent(
                found_Or_Default_M8BonVent.default_If_No_Found
                    .copy(
                        position_Don_Lis_Cible_Clients_au_VentPeriod = newPosition
                    )
            )

        focusedValuesGetter.update_activeCentralValues(
            activeCentralValues.copy(
                actuelle_Ciblage_MaxPosition = newPosition
            )
        )
    }

    fun deleteUnSeulData(data: M2Client) {
        viewModelScope.launch {
            b_ClientDataBaseRepository.deleteData(data)
            this@MapClientsViewModel.repo2Client.removeClient(data.id)
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
        Filter_Leur_Last_TRX_Est_Credit(Icons.Default.Map, Color.Red),
        Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME(Icons.Default.Map, Color.Red),
        AFFICHE_COMMANDE_LIVRAI_Filter(Icons.Default.Filter, Color.Blue),
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
}
