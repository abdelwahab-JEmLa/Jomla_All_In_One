package Z_MasterOfApps.Kotlin.Model

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Objects

class ClientsDataBase(
    var id: Long = 1,
    var nom: String = "Non Defini"
) {
    var statueDeBase by mutableStateOf(StatueDeBase())
    @IgnoreExtraProperties
    class StatueDeBase {
        var couleur: String = "#FFFFFF"
        var caRefDonAncienDataBase by mutableStateOf("G_Clients")
        var cUnClientTemporaire: Boolean by mutableStateOf(true)
        var auFilterFAB: Boolean by mutableStateOf(false)
        var positionDonClientsList: Int by mutableIntStateOf(0)
    }

    var gpsLocation by mutableStateOf(GpsLocation())
    @IgnoreExtraProperties
    class GpsLocation {
        var latitude by mutableStateOf(0.0)
        var longitude by mutableStateOf(0.0)
        var title by mutableStateOf("")
        var snippet by mutableStateOf("")

        var actuelleEtat: DernierEtatAAffiche? by mutableStateOf(null)
        enum class DernierEtatAAffiche(val color: Int, val nomArabe: String) {
            Cible(android.R.color.holo_red_light, "Cible"),
            ON_MODE_COMMEND_ACTUELLEMENT(android.R.color.holo_green_light, "نشط / متصل"),
            CLIENT_ABSENT(android.R.color.darker_gray, "غائب الشاري"),
            AVEC_MARCHANDISE(android.R.color.holo_blue_light, "عندو سلعة"),
            FERME(android.R.color.darker_gray, "مغلق")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClientsDataBase) return false
        return id == other.id &&
                nom == other.nom
    }

    override fun hashCode(): Int {
        return Objects.hash(id, nom)
    }

    companion object {
        val refClientsDataBase = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("ClientsDataBase")

        fun updateClientsDataBase(
            client :ClientsDataBase,
            viewModelProduits: ViewModelInitApp
        ) {
            viewModelProduits.viewModelScope.launch {
                try {
                    // Update _produitsAvecBonsGrossist
                    val index =
                        viewModelProduits._modelAppsFather.clientDataBaseSnapList.indexOfFirst { it.id == client.id }
                    if (index != -1) {
                        // Direct update of the SnapshotStateList
                        viewModelProduits._modelAppsFather.clientDataBaseSnapList[index] = client
                    }

                    refClientsDataBase.child(client.id.toString()).setValue(client).await()

                } catch (e: Exception) {
                }
            }
        }
    }
    }
}
