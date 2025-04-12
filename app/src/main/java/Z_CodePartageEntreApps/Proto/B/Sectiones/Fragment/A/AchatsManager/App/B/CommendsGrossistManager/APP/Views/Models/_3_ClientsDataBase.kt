package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views.Models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@Entity
data class _3_ClientsDataBase(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    var nom: String = "Non Defini",

    // Section Etates Mutable
    var numTelephone: String = "",
    var couleur: String = "#FFFFFF",
    var bonDuClientsSu: String = "",
    var currentCreditBalance: Double = 0.0,
    var positionDonClientsList: Int = 0,
    var cUnClientTemporaire: Boolean = true,
    var auFilterFAB: Boolean = false,
    var typeDeSonMagasine: TypeDeSonMagasine = TypeDeSonMagasine.ATAYAT_MOUKASSARAT,
    var clientTypeMode: ClientTypeMode = ClientTypeMode.NEVEAU,

    // Section GpsLocation
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var title: String = "",
    var snippet: String = "",
    var actuelleEtat: DernierEtatAAffiche = DernierEtatAAffiche.NON_DEFINI
) {
    // Keep the enum classes as they are
    @IgnoreExtraProperties
    enum class DernierEtatAAffiche(val color: Int, val nomArabe: String) {
        NON_DEFINI(android.R.color.white, "غير محدد"),
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

    // Add no-arg constructor for Firebase
    constructor() : this(1L)
}
