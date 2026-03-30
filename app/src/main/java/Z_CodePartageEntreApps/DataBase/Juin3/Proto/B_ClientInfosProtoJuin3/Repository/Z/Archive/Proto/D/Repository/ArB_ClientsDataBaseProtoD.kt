package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.D.Repository

import EntreApps.Shared.Models.M2Client
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@IgnoreExtraProperties
data class ArB_ClientsDataBaseProtoD(
    var id: Long = 1,
    var nom: String = "Non Defini",
    var statueDeBase: StatueDeBase = StatueDeBase(),
    var gpsLocation: GpsLocation = GpsLocation(),
) {
    @IgnoreExtraProperties
    data class StatueDeBase(
        var numTelephone: String = "",
        var couleur: String = "#FFFFFF",
        var bonDuClientsSu: String = "",
        var currentCreditBalance: Double = 0.0, // New field for current credit balance
        var positionDonClientsList: Int = 0,
        var caRefDonAncienDataBase: String = "G_Clients",
        var cUnClientTemporaire: Boolean = true,
        var auFilterFAB: Boolean = false ,
        var typeDeSonMagasine: TypeDeSonMagasine? = TypeDeSonMagasine.ATAYAT_MOUKASSARAT
    )  {
        @IgnoreExtraProperties
        enum class TypeDeSonMagasine(val color: Int, val nomArabe: String) {
            ATAYAT_MOUKASSARAT(android.R.color.holo_green_light, ""),
            AlIMENTATION_GENERALE(android.R.color.holo_purple, ""),
        }
    }

    var etatesMutable by mutableStateOf(EtatesMutable())
    @IgnoreExtraProperties
    class EtatesMutable {
        var clientTypeMode by mutableStateOf(ClientTypeMode.NEVEAU)

        // New enum to represent client type modes
        enum class ClientTypeMode(
            val icon: ImageVector,
            val color: Color
        ) {
            NEVEAU(
                icon = Icons.Default.Add,
                color = Color.Red
            ),
            ANCIEN(
                icon = Icons.Default.MonetizationOn,
                color = Color.Blue
            ),
            EVITE(
                icon = Icons.Default.Lock,
                color = Color.Gray
            )
        }


    }

    @IgnoreExtraProperties
    data class GpsLocation(
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var title: String = "",
        var snippet: String = "",
        var actuelleEtat: DernierEtatAAffiche? = null,
    ) {
        @IgnoreExtraProperties
        enum class DernierEtatAAffiche(val color: Int, val nomArabe: String) {
            آNON_DEFINI(android.R.color.white, "غير محدد"),
            ON_MODE_COMMEND_ACTUELLEMENT(android.R.color.holo_green_light, "نشط / متصل"),
            VENDU_A_LUI(android.R.color.holo_purple, ""),
            Cible(android.R.color.holo_red_light, "Cible"),
            CIBLE_PRIORITE_2(android.R.color.holo_orange_light, "CIBLE_PRIORITE_2"),
            CIBLE_POUR_2(android.R.color.holo_blue_dark, "CIBLE_POUR_2"),
            CLIENT_ABSENT(android.R.color.darker_gray, "غائب الشاري"),
            AVEC_MARCHANDISE(android.R.color.holo_blue_light, "عندو سلعة"),
            FERME(android.R.color.darker_gray, "مغلق"),
            A_EVITE(android.R.color.black, "يتجنب")
        }
    }

    // M2Client.kt - Updated companion object
    companion object {
        val refClientsDataBase = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("M2Client")


        fun M2Client.updateClientsDataBase(
            viewModel: ViewModelInitApp
        ) {
            viewModel.viewModelScope.launch {
                try {
                    // Create add_New snapshot of the current state
                    val currentState = this@updateClientsDataBase.copy()

                    // Update local state using clear and addAll
                    val clientsList = viewModel._modelAppsFather.clientDataBase
                    val updatedList = clientsList.toMutableList()
                    val index = updatedList.indexOfFirst { it.id == currentState.id }

                    if (index != -1) {
                        updatedList[index] = currentState
                    } else {
                        // If client doesn't exist, upsert them
                        updatedList.add(currentState)
                    }

                    // Replace entire list
                    clientsList.clear()
                    clientsList.addAll(updatedList)

                    // Update Firebase with error handling
                    try {
                        refClientsDataBase.child(currentState.id.toString())
                            .setValue(currentState)
                            .await()
                    } catch (e: Exception) {
                        // Revert local state if Firebase upsertLenceCommandeRepoGroupedProtoAvantJuin3 fails
                        clientsList.clear()
                        clientsList.addAll(
                            if (index != -1) updatedList.toMutableList().apply { this[index] = this@updateClientsDataBase }
                            else updatedList.dropLast(1)
                        )
                        throw e
                    }

                } catch (e: Exception) {
                    Log.e("M2Client", "Failed to upsertLenceCommandeRepoGroupedProtoAvantJuin3 client", e)
                }
            }
        }
    }


    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + nom.hashCode()
        result = 31 * result + statueDeBase.hashCode()
        result = 31 * result + gpsLocation.hashCode()
        return result
    }
}
