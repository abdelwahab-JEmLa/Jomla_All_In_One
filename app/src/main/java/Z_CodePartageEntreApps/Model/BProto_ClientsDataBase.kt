package Z_CodePartageEntreApps.Model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.IgnoreExtraProperties

class BProto_ClientsDataBase {
    var id by mutableStateOf(1L)

    //Section InfosBase
    var nom by mutableStateOf("Non Defini")

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
}
