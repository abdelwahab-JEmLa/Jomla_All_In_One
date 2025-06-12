package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.ViewModel
              /*
import V.DiviseParS/ections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.PanelsGroupeButton
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.PolygonGeoLimite
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
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SettingsBackupRestore
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
import org.osmdroid.api.IGeoPoint
import org.osmdroid.views.MapView
import java.util.Date


class MapClientsViewModelss(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val recordingHandler: IRecordingHandler,
    val appDatabase: AppDatabase
) : ViewModel() {
    val groupeRepositorysProtoAvJuin3= a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin

    val b_ClientDataBaseRepository= a_MasterRepositorysGrpProtoJuin3.b_ClientInfosProtoJuin3Repository

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val secteurRepo = groupeRepositorysProtoAvJuin3.repositorys_Model
        .e1SecteurDeClientsRepository

    val secteurList = secteurRepo.listCollected

    val c3_BonAchate_List = groupeRepositorysProtoAvJuin3.repositorys_Model
        .c3TransactionCommercialRepository.modelDatasSnapList

    val bProto_ClientsDataBase = uiState.value.b_ClientInfosProtoJuin3List

    var auClickeCaUpdateClientPar by mutableStateOf(B_ClientInfosProtoJuin3.TypeDeSonMagasine.ATAYAT_MOUKASSARAT)
    var mapReloadTigger by mutableIntStateOf(0)

    var afficheLesJoursAuNoms by mutableStateOf(true)
    var filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList by mutableStateOf<List<String>>(
        emptyList()
    )

    // State for sectors
    private val _secteurs = MutableStateFlow<List<E1SecteurDeClients>>(emptyList())
    val secteurs: StateFlow<List<E1SecteurDeClients>> = _secteurs

    // Current active sector for polygon creation
    private var _currentActiveSectorId = MutableStateFlow<Long?>(null)
    val currentActiveSectorId: StateFlow<Long?> = _currentActiveSectorId

    // Dialog states
    val showSecteurDialog = mutableStateOf(false)
    val showAddSecteurDialog = mutableStateOf(false)

    init {
        viewModelScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        b_ClientInfosProtoJuin3List = model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList(),
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }

        viewModelScope.launch {
            snapshotFlow {
                a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
                    .repositorys_Model
                    .c3TransactionCommercialRepository
                    .modelDatasSnapList
                    .toList() // Convert to regular list
            }.collect { transactionList ->
                _uiState.value = _uiState.value.copy(
                    c3_TransactionCommercialList = transactionList
                )
            }
        }

        viewModelScope.launch {
            snapshotFlow {
                a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
                    .repositorys_Model
                    .repository_1_5_Vendeur
                    .modelDatasSnapList
                    .toList()
            }.collect { list ->
                _uiState.value = _uiState.value.copy(
                    activeCompt = _1_5_Vendeur.getActiveComptPourCeTelephone(list)
                )
            }
        }
    }

    fun getLastTransaction(
        client: B_ClientInfosProtoJuin3
    ): C3_TransactionCommercial? {
        val historicalData = groupeRepositorysProtoAvJuin3
            .repositorys_Model
            .c3TransactionCommercialRepository
            .modelDatasSnapList
        val lastTransaction = historicalData
            .filter { it.clientAcheteurID == client.id }
            .maxByOrNull { it.timestamps }
        return lastTransaction
    }

    init {
        loadSecteurs()
    }

    fun updatedStateFabGroupVisibility(updatedState: PanelsGroupeButton) {
        viewModelScope.launch {
            // Create a new list with the updated panel
            val currentList = _uiState.value.paneleGroupeButtonList
            val index = currentList.indexOfFirst { it.key == updatedState.key }

            // Create a new list with the updated item
            val updatedList = currentList.toMutableList()
            updatedList[index] = updatedState

            // Emit the new state with the updated list
            _uiState.emit(_uiState.value.copy(paneleGroupeButtonList = updatedList))
        }
    }

    val polygonDao = appDatabase.polygonGeoLimiteDaoDao()

    private fun loadSecteurs() {
        viewModelScope.launch {
            _secteurs.value = secteurList
        }
    }

    fun showSecteurDialog() {
        showSecteurDialog.value = true
    }

    fun hideSecteurDialog() {
        showSecteurDialog.value = false
    }

    fun showAddSecteurDialog() {
        showAddSecteurDialog.value = true
    }

    fun hideAddSecteurDialog() {
        showAddSecteurDialog.value = false
    }

    suspend fun updateSecteurActive(secteurId: Long, active: Boolean) {
        val secteur = _secteurs.value.find { it.vid == secteurId } ?: return
        val updatedSecteur = secteur.copy(ouvert = active)

        secteurRepo.insert(updatedSecteur)
        loadSecteurs() // Refresh the list
    }

    suspend fun addNewSector(name: String, color: String) {
        val newSector = E1SecteurDeClients(
            vid = 0, // Auto-generated
            nom = name,
            ouvert = true,
            sonPolygonOnModeDessine = false,
            couleur = color
        )

        val newSectorId = _secteurs.value.maxOf { it.vid } +1

        _currentActiveSectorId.value = newSectorId
        loadSecteurs() // Refresh the list
        mapReloadTigger++ // Add this line to trigger map refresh

    }

    fun addPointToCurrentSector(mapCenter: IGeoPoint) {
        val sectorId = _currentActiveSectorId.value

        viewModelScope.launch {
            try {
                if (sectorId == null) {
                    Log.e(
                        "PolygonCreator",
                        "No active sector selected. Please select or create a sector first."
                    )
                    return@launch
                }

                val sector = _secteurs.value.find { it.vid == sectorId }
                if (sector == null) {
                    Log.e("PolygonCreator", "Cannot find sector with ID: $sectorId")
                    return@launch
                }

                // Log coordinates being added
                val latMicroDegrees = (mapCenter.latitude * 1E6).toInt()
                val lonMicroDegrees = (mapCenter.longitude * 1E6).toInt()
                Log.d(
                    "PolygonCreator",
                    "Adding point to sector ${sector.nom} (ID: $sectorId): lat=$latMicroDegrees, lon=$lonMicroDegrees"
                )

                // Create a new point for the polygon
                val newPoint = PolygonGeoLimite(
                    parentSecteurDeClientsId = sectorId,
                    parentE1SecteurDeClientsKey = "E1SecteurDeClients.$sectorId(${sector.nom})",
                    aLatitude = latMicroDegrees,
                    aLongitude = lonMicroDegrees
                )

                // Insert the point into the database
                val pointId = polygonDao.insertAvecRetureNewVid(newPoint)
                Log.d(
                    "PolygonCreator",
                    "Successfully added point with ID: $pointId to sector $sectorId"
                )

                // Let's also verify if the point was correctly saved
                val allPointsForSector =
                    polygonDao.getAll().filter { it.parentSecteurDeClientsId == sectorId }
                Log.d(
                    "PolygonCreator",
                    "Total points for sector $sectorId after adding: ${allPointsForSector.size}"
                )

                // Refresh the map
                mapReloadTigger++

                // Log success
                Log.d(
                    "PolygonCreator",
                    "Map refresh triggered. New mapReloadTrigger value: $mapReloadTigger"
                )
            } catch (e: Exception) {
                // Log the error
                Log.e("PolygonCreator", "Error adding point to sector: ${e.message}", e)
            }
        }
    }

    fun closeCurrentSector() {
        val sectorId = _currentActiveSectorId.value ?: return

        viewModelScope.launch {
            val sector = _secteurs.value.find { it.vid == sectorId } ?: return@launch

            // Update the sector to mark it as closed
            val updatedSector = sector.copy(sonPolygonOnModeDessine = true, ouvert = false)
            secteurRepo.insert(updatedSector)

            // Clear the current active sector
            _currentActiveSectorId.value = null

            // Refresh the sectors list
            loadSecteurs()

            // Refresh the map
            mapReloadTigger++
        }
    }

    fun updateData(client: B_ClientInfosProtoJuin3): Unit {
        viewModelScope.launch {
            b_ClientDataBaseRepository.addOrUpdateData(client)
        }

        mapReloadTigger++
    }

    fun onClickAddMarkerButton(mapView: MapView) {
        val center = mapView.mapCenter
        if (center.latitude == 0.0) {
            return
        }

        try {
            val newID = if (bProto_ClientsDataBase.isEmpty()) {
                1L
            } else {
                bProto_ClientsDataBase.maxOf { it.id } + 1
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
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    fun updateLongAppSetting(
        value: Long,
        name: String = "clientBuyerNowId",
    ) {
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

    enum class VisibleClientsNow(val icon: Any, val couleur: Color = Color.White) {
        AFFICHE_CIBLE_POUR_VENDEUR(Icons.Default.Map, Color.Red),
        CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX(Icons.Default.SettingsBackupRestore, Color.Blue),

        showNonAbsentClientsOnly(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl),
        affichePourCollecteurCommendes(LottieJsonGetterR_Raw_Icons.afficheFenetre),
        showAtayClients(LottieJsonGetterR_Raw_Icons.atay),
        showClientsOnlyAcEtateCIBLE_POUR_2(Icons.Default.CheckCircleOutline),
        showAlimentionlients(LottieJsonGetterR_Raw_Icons.alimentation),
        showClientsWithConfirmedProducts(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl), // New filter mode
        showAll(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl);
    }

    fun cleanupResources() {
        viewModelScope.launch {
            try {
                // Cancel any ongoing operations first
                cancelActiveOperations()

                // Clear any stored map data
                mapReloadTigger = 0
                filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList = emptyList()
                _currentActiveSectorId.value = null

                // Log cleanup completion
                println("Map resources cleaned up successfully")
            } catch (e: Exception) {
                println("Error during resource cleanup: ${e.message}")
            }
        }
    }

    fun cancelActiveOperations() {
        try {
            mapReloadTigger++
            afficheLesJoursAuNoms = true
            _currentActiveSectorId.value = null

            println("Active operations canceled successfully")
        } catch (e: Exception) {
            println("Error canceling active operations: ${e.message}")
        }
    }

    fun deleteUnSeulData(data: B_ClientInfosProtoJuin3) { b_ClientDataBaseRepository.deleteData(data) }
    fun startRecordIfNot(): Unit { recordingHandler.startRecordIfNot() }

    fun updateActiveComptIdClientOuSonMarqueMapEstOuvert(data: Long) {
        val currentActiveCompt = _uiState.value.activeCompt ?: return
        _uiState.value = _uiState.value.copy(
            activeCompt = currentActiveCompt.copy(idClientOuvertPoutCeCompt = data)
        )
    }
}                       */
