package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable.Companion.ref
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Fonctions.Main.getKeyFireBase
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Entity
data class B_ClientInfosProtoJuin3(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    //Infos De Base
    var nom: String = "Non Defini",
    var cretionTimestamps: Long = DatesHandler().getCurrentTimestamps(),
    //Forging Keys

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
    var caMarqueGpsEstOuvert: Boolean = false,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var title: String = "",
    var snippet: String = "",
    var actuelleEtat: DernierEtatAAffiche = DernierEtatAAffiche.NON_DEFINI,
    //Etates Mutable

    // Section Centralization Valeurs Pour Injection a TOu modules
    var tagCeBonEstOuvertPourComptsIds: String = "",

    // Section keyFireBase et dernierFireBaseUpdateTimestamps
    var keyFireBase: String = "",
    var dernierTimeTampsSynchronisationAvecFireBase: Long = 0,
) {
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
        A_EVITE(android.R.color.black, "يتجنب"),
        CLIENT_ABSENT(android.R.color.darker_gray, "عميل غائب") // Add this line
    }

    enum class TypeDeSonMagasine(val color: Int, val nomArabe: String) {
        ATAYAT_MOUKASSARAT(android.R.color.holo_green_light, "عطارة ومكسرات"),
        AlIMENTATION_GENERALE(android.R.color.holo_purple, "مواد غذائية")
    }

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

    fun withProperKeyFireBaseAndTimeTamp(): B_ClientInfosProtoJuin3 {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(id, nom) }
        return this.copy(
            keyFireBase = safeKey,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        fun createTestInstance(): List<B_ClientInfosProtoJuin3> {
            return emptyList()
        }

        val parent = Firebase.database.getReference(
            "00_DataPrototype-04-02" +
                    "/_1_developingRef" +
                    "/C_InfosSqlDataBases"
        )

        val caRef = parent.child("B_ClientInfosProtoJuin3")

        fun removeRef(
            preparedData: B_ClientInfosProtoJuin3
        ) {
            ref.child(preparedData.keyFireBase).removeValue()
        }
    }
}
