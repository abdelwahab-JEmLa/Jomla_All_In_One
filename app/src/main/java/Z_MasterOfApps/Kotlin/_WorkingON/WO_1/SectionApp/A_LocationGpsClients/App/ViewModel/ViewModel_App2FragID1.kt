package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel

import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.Parent.AppSettingsSaverModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.views.MapView
import java.util.Date

class ViewModel_App2FragID1(
    val mainRepositery: B_ClientDataBaseRepository,
) : ViewModel() {
    val bProto_ClientsDataBase = mainRepositery.modelDatas

    var auClickeCaUpdateClientPar by mutableStateOf(B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT)
    var mapReloadTigger by mutableIntStateOf(0)

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
                actuelleEtat = B_ClientDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2
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

    enum class VisbleClientsNow(val icon: Any) {
        showNonAbsentClientsOnly(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl),
        affichePourCollecteurCommendes(LottieJsonGetterR_Raw_Icons.afficheFenetre),
        showAtayClients(LottieJsonGetterR_Raw_Icons.atay),
        showClientsOnlyAcEtateCIBLE_POUR_2(Icons.Default.CheckCircleOutline),
        showAlimentionlients(LottieJsonGetterR_Raw_Icons.alimentation),
        showAll(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl);
    }
}
