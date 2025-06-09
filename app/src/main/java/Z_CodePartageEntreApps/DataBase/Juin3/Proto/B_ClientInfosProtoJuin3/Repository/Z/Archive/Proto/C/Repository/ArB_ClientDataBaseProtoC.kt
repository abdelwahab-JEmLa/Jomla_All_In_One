package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.C.Repository

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
data class ArB_ClientDataBaseProtoC(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

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
    var actuelleEtat: DernierEtatAAffiche = DernierEtatAAffiche.NON_DEFINI,

    // Section Centralization Valeurs Pour Injection a TOu modules
    var tagCeBonEstOuvertPourComptsIds: String ="",

    ) {
    @IgnoreExtraProperties
    enum class DernierEtatAAffiche(val color: Int, val nomArabe: String) {
        NON_DEFINI(android.R.color.holo_orange_light, "غير محدد"),
        ON_MODE_COMMEND_ACTUELLEMENT(android.R.color.holo_green_light, "نشط / متصل"),
        VENDU_A_LUI(android.R.color.holo_purple, ""),
        Cible(android.R.color.holo_red_light, "Cible"),
        CIBLE_PRIORITE_2(android.R.color.holo_orange_dark, "CIBLE_PRIORITE_2"),
        CIBLE_PRIORITE_3(android.R.color.holo_green_light, "CIBLE_PRIORITE_3"),
        CIBLE_POUR_2(android.R.color.holo_blue_dark, "CIBLE_POUR_2"),
        ACHETEUR_NON_DISPO(android.R.color.darker_gray, "الشاري غائب"),
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
