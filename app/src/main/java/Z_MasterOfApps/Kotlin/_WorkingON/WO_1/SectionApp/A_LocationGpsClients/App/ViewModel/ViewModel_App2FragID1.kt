package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel

import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase.TypeDeSonMagasine
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.BProto_ClientsDataBaseRepository
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.Parent.AppSettingsSaverModel
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.runtime.getValue
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
    val mainRepositery: BProto_ClientsDataBaseRepository,
) : ViewModel() {
    private val TAG = "ViewModel_App2FragID1"
    val bProto_ClientsDataBase = mainRepositery.modelDatas

    var auClickeCaUpdateClientPar by mutableStateOf(TypeDeSonMagasine.ATAYAT_MOUKASSARAT)
    var mapReloadTigger by mutableStateOf(0)
    // In ViewModel_App2FragID1.kt

    fun updateData(client: BProto_ClientsDataBase): Unit {
        Log.d(TAG, "updateData called for client: ${client.id}, state: ${client.actuelleEtat?.name}, mapReloadTrigger before: $mapReloadTigger")

        viewModelScope.launch {
            mainRepositery.updateData(client)
        }

        mapReloadTigger++
        Log.d(TAG, "mapReloadTrigger after increment: $mapReloadTigger")
    }

    fun onClickAddMarkerButton(mapView: MapView) {
        val center = mapView.mapCenter
        if (center.latitude == 0.0) {
            Log.e(TAG, "Invalid latitude value")
            return
        }

        try {
            // Calculate new ID safely
            val newID = if (bProto_ClientsDataBase.isEmpty()) {
                1L
            } else {
                bProto_ClientsDataBase.maxOf { it.id } + 1
            }

            val newnom = "ز.$newID"

            val newClient = BProto_ClientsDataBase().apply {
                id = newID
                nom = newnom
                cUnClientTemporaire = true
                typeDeSonMagasine = auClickeCaUpdateClientPar
                latitude = center.latitude
                longitude = center.longitude
                title = newnom
                snippet = "Client temporaire"
                actuelleEtat = BProto_ClientsDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2
            }

            Log.d(TAG, "Adding new client with ID: ${newClient.id}")

            // Use viewModelScope to ensure proper threading
            viewModelScope.launch {
                mainRepositery.addData(newClient)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding new marker", e)
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
                Log.e(TAG, "Error updating app setting", e)
            }
        }
    }

    fun deleteUnSeulData(data: BProto_ClientsDataBase) {
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
