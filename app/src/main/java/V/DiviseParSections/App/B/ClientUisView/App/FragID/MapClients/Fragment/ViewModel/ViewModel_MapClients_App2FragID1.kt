package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel

import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.Parent.AppSettingsSaverModel
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.views.MapView
import java.util.Date

class ViewModel_MapClients_App2FragID1(
    val mainRepositery: B_ClientDataBaseRepository,
    val repo_0_0_HeadOfRepositorys_Repository:_0_0_HeadOfRepositorys_Repository,
    val repo_01_VentsHistoriquesDataBase : _01_VentsHistoriquesDataBase_Repository
) : ViewModel() {
    val modelDatasSnapList_1_3_BonAchat=repo_0_0_HeadOfRepositorys_Repository.repositorys_Model
        .repository_1_3_TransactionCommercial.modelDatasSnapList

    val bProto_ClientsDataBase = mainRepositery.modelDatas
    val newClientDataBase = repo_0_0_HeadOfRepositorys_Repository.repositorys_Model
        .repository_3_ClientsDataBase
        .modelDatasSnapList

    var auClickeCaUpdateClientPar by mutableStateOf(B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT)
    var mapReloadTigger by mutableIntStateOf(0)

    var afficheLesJoursAuNoms by mutableStateOf(true)
    var filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList by mutableStateOf<List<String>>(emptyList())

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
}
