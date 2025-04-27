package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.PolygonGeoLimite
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.SecteurDeClients
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
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
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.api.IGeoPoint
import org.osmdroid.views.MapView
import java.util.Date

class ViewModel_MapClients_App2FragID1(
    val appDatabase: AppDatabase,
    val mainRepositery: B_ClientDataBaseRepository,
    val repo_0_0_HeadSQLRepositorys:_0_0_HeadSQLRepositorys,
    val repo_01_VentsHistoriquesDataBase : _01_VentsHistoriquesDataBase_Repository
) : ViewModel() {
    val secteurDao = appDatabase.secteurDeClientsDao()
    val polygonDao = appDatabase.polygonGeoLimiteDaoDao()

    val modelDatasSnapList_1_3_BonAchat=repo_0_0_HeadSQLRepositorys.repositorys_Model
        .repository_1_3_TransactionCommercial.modelDatasSnapList

    val bProto_ClientsDataBase = mainRepositery.modelDatas
    val newClientDataBase = repo_0_0_HeadSQLRepositorys.repositorys_Model
        .repository_3_ClientsDataBase
        .modelDatasSnapList

    var auClickeCaUpdateClientPar by mutableStateOf(B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT)
    var mapReloadTigger by mutableIntStateOf(0)

    var afficheLesJoursAuNoms by mutableStateOf(true)
    var filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList by mutableStateOf<List<String>>(emptyList())

    // State for sectors
    private val _secteurs = MutableStateFlow<List<SecteurDeClients>>(emptyList())
    val secteurs: StateFlow<List<SecteurDeClients>> = _secteurs

    // Current active sector for polygon creation
    private var _currentActiveSectorId = MutableStateFlow<Long?>(null)
    val currentActiveSectorId: StateFlow<Long?> = _currentActiveSectorId

    // Dialog states
    val showSecteurDialog = mutableStateOf(false)
    val showAddSecteurDialog = mutableStateOf(false)

    init {
        loadSecteurs()
    }

    private fun loadSecteurs() {
        viewModelScope.launch {
            _secteurs.value = secteurDao.getAll()
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
        secteurDao.insert(updatedSecteur)
        loadSecteurs() // Refresh the list
    }

    suspend fun addNewSector(name: String, color: String) {
        val newSector = SecteurDeClients(
            vid = 0, // Auto-generated
            nom = name,
            ouvert = true,
            polygonEstFerme = false,
            couleur = color
        )
        val sectorId = secteurDao.insertAvecRetureNewVid(newSector)
        _currentActiveSectorId.value = sectorId
        loadSecteurs() // Refresh the list
        mapReloadTigger++ // Add this line to trigger map refresh

    }

    fun startNewPolygon() {
        viewModelScope.launch {
            // Check if there are any sectors marked as open
            val openSector = _secteurs.value.find { it.ouvert }

            if (openSector != null) {
                // Use existing open sector
                _currentActiveSectorId.value = openSector.vid
            } else {
                // Create a new sector if none are open
                showAddSecteurDialog()
            }
        }
    }

    fun addPointToCurrentSector(mapCenter: IGeoPoint) {
        val sectorId = _currentActiveSectorId.value

        viewModelScope.launch {
            try {
                if (sectorId == null) {
                    Log.e("PolygonCreator", "No active sector selected. Please select or create a sector first.")
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
                Log.d("PolygonCreator", "Adding point to sector ${sector.nom} (ID: $sectorId): lat=$latMicroDegrees, lon=$lonMicroDegrees")

                // Create a new point for the polygon
                val newPoint = PolygonGeoLimite(
                    parentSecteurDeClientsId = sectorId,
                    parentSecteurDeClientsKey = "SecteurDeClients.$sectorId(${sector.nom})",
                    aLatitude = latMicroDegrees,
                    aLongitude = lonMicroDegrees
                )

                // Insert the point into the database
                val pointId = polygonDao.insertAvecRetureNewVid(newPoint)
                Log.d("PolygonCreator", "Successfully added point with ID: $pointId to sector $sectorId")

                // Let's also verify if the point was correctly saved
                val allPointsForSector = polygonDao.getAll().filter { it.parentSecteurDeClientsId == sectorId }
                Log.d("PolygonCreator", "Total points for sector $sectorId after adding: ${allPointsForSector.size}")

                // Refresh the map
                mapReloadTigger++

                // Log success
                Log.d("PolygonCreator", "Map refresh triggered. New mapReloadTrigger value: $mapReloadTigger")
            } catch (e: Exception) {
                // Log the error
                Log.e("PolygonCreator", "Error adding point to sector: ${e.message}", e)
            }
        }
    }
    // Add this function to your ViewModel
    fun refreshMapData() {
        viewModelScope.launch {
            Log.d("PolygonCreator", "Refreshing map data")
            // This will trigger a re-fetch of all the data and redraw the map
            loadSecteurs() // Make sure all sectors are up to date
            mapReloadTigger++ // Increment to trigger map redraw
        }
    }
    fun closeCurrentSector() {
        val sectorId = _currentActiveSectorId.value ?: return

        viewModelScope.launch {
            val sector = _secteurs.value.find { it.vid == sectorId } ?: return@launch

            // Update the sector to mark it as closed
            val updatedSector = sector.copy(polygonEstFerme = true, ouvert = false)
            secteurDao.insert(updatedSector)

            // Clear the current active sector
            _currentActiveSectorId.value = null

            // Refresh the sectors list
            loadSecteurs()

            // Refresh the map
            mapReloadTigger++
        }
    }

    fun updateData(client: B_ClientDataBase): Unit {
        viewModelScope.launch {
            mainRepositery.updateUnSeulData(client)
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

            val newClient = B_ClientDataBase().apply {
                id = newID
                nom = newnom
                cUnClientTemporaire = true
                typeDeSonMagasine = auClickeCaUpdateClientPar
                latitude = center.latitude
                longitude = center.longitude
                title = newnom
                snippet = "Client temporaire"
            }

            viewModelScope.launch {
                mainRepositery.addData(newClient)
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

    fun deleteUnSeulData(data: B_ClientDataBase) {
        mainRepositery.deleteUnSeulData(data)
    }

    enum class VisibleClientsNow(val icon: Any,val couleur :Color = Color.White) {
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

    /**
     * Cleans up all resources used by the ViewModel to prevent memory leaks
     * and ensure proper resource management when the view is destroyed.
     */
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
}
