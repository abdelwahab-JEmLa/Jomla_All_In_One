package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel

import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase.TypeDeSonMagasine
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.BProto_ClientsDataBaseRepository
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.Parent.AppSettingsSaverModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
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
    val bProto_ClientsDataBase = mainRepositery.modelDatas

    init {
        mainRepositery.importDeFireBaseAuRoom(viewModelScope)
    }

    fun updateClient(client: BProto_ClientsDataBase): Unit {
        mainRepositery.updateData(client)
    }

    fun updateDataTiggerreRelode(client: BProto_ClientsDataBase): Unit {
        val currentList = bProto_ClientsDataBase.toList()
        val updatedClients = mutableStateListOf<BProto_ClientsDataBase>()

        for (existingClient in currentList) {
            if (existingClient.id == client.id) {
                val updatedClient = BProto_ClientsDataBase().apply {
                    id = client.id
                    nom = client.nom
                    numTelephone = client.numTelephone
                    couleur = client.couleur
                    bonDuClientsSu = client.bonDuClientsSu
                    currentCreditBalance = client.currentCreditBalance
                    positionDonClientsList = client.positionDonClientsList
                    cUnClientTemporaire = client.cUnClientTemporaire
                    auFilterFAB = client.auFilterFAB
                    typeDeSonMagasine = client.typeDeSonMagasine
                    clientTypeMode = client.clientTypeMode
                    latitude = client.latitude
                    longitude = client.longitude
                    title = client.title
                    snippet = client.snippet
                    actuelleEtat = client.actuelleEtat
                }
                updatedClients.add(updatedClient)
            } else {
                updatedClients.add(existingClient)
            }
        }

        viewModelScope.launch {
            mainRepositery.updateDatas(updatedClients.toMutableStateList())
        }
    }

    var auClickeCaUpdateClientPar by mutableStateOf(TypeDeSonMagasine.ATAYAT_MOUKASSARAT)

    fun onClickAddMarkerButton(
        mapView: MapView,
    ) {
        val center = mapView.mapCenter
        require(center.latitude != 0.0) { "Invalid latitude value" }

        val newID = if (bProto_ClientsDataBase.isEmpty()) {
            1L
        } else {
            bProto_ClientsDataBase.maxOf { it.id } + 1
        }
        val newnom = "ز.$newID"

        val newClient = BProto_ClientsDataBase().apply {
            nom = newnom
            cUnClientTemporaire = true
            typeDeSonMagasine = auClickeCaUpdateClientPar
            latitude = center.latitude
            longitude = center.longitude
            title = newnom
            snippet = "Client temporaire"
            actuelleEtat = BProto_ClientsDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2
        }

        mainRepositery.addData(newClient)

        B_ClientsDataBase.refClientsDataBase
            .child(newClient.id.toString())
            .setValue(newClient)
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
            } catch (_: Exception) {
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
