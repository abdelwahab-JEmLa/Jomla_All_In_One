package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.database.IgnoreExtraProperties

class BProto_ClientsDataBase {
    var id by mutableStateOf(1L)

    //Section InfosBase
    var nom by mutableStateOf("Non Defini")

    //Section Etates Mutable
    var numTelephone by mutableStateOf("")
    var couleur by mutableStateOf("#FFFFFF")
    var bonDuClientsSu by mutableStateOf("")
    var currentCreditBalance by mutableStateOf(0.0)
    var positionDonClientsList by mutableStateOf(0)
    var cUnClientTemporaire by mutableStateOf(true)
    var auFilterFAB by mutableStateOf(false)
    var typeDeSonMagasine by mutableStateOf(TypeDeSonMagasine.ATAYAT_MOUKASSARAT)
    var clientTypeMode by mutableStateOf(ClientTypeMode.NEVEAU)

    //Section GpsLocation
    var latitude by mutableStateOf(0.0)
    var longitude by mutableStateOf(0.0)
    var title by mutableStateOf("")
    var snippet by mutableStateOf("")
    var actuelleEtat by mutableStateOf(DernierEtatAAffiche.آNON_DEFINI)

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

    @IgnoreExtraProperties
    enum class TypeDeSonMagasine(val color: Int, val nomArabe: String) {
        ATAYAT_MOUKASSARAT(android.R.color.holo_green_light, "عطارة ومكسرات"),
        AlIMENTATION_GENERALE(android.R.color.holo_purple, "مواد غذائية")
    }

    @IgnoreExtraProperties
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

