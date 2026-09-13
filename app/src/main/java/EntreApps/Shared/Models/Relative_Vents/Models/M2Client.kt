package EntreApps.Shared.Models.Relative_Vents.Models

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.central_MainDataBases_RefProduction
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client.Companion.calculateCreditsMap
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.mongodb.kbson.BsonObjectId


@Entity
data class M2Client(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = 0,
    var creationTimestamps: Long = System.currentTimeMillis(),
    //Infos De Base

    var nom: String = "Non Defini",
    var cretionTimestamps: Long = DatesHandler().getCurrentTimestamps(),
    //Forging Keys
    var its_Fournisseur: Boolean = false,

    var its_non_deletable_client_et_trxs: Boolean = false,

    var its_Client_De_Jamale: Boolean = false,
    var its_Fournisseur_Grossisst_A_Jomla: Boolean = false,
    var ces_credits_son_a_long_term: Boolean = false,


    var cUnClientTemporaire: Boolean = true,

    var parentComptCreateurKEyID: String = "",

    // Section Etates Mutable
    var numTelephone: String = "",
    var couleur: String = "#FFFFFF",
    var bonDuClientsSu: String = "",
    var currentCreditBalance: Double = 0.0,
    var positionDonClientsList: Int = 0,
    var auFilterFAB: Boolean = false,
    var typeDeSonMagasine: TypeDeSonMagasine = TypeDeSonMagasine.ATAYAT_MOUKASSARAT,
    var clientTypeMode: ClientTypeMode = ClientTypeMode.NEVEAU,
    var caMarqueGpsEstOuvert: Boolean = false,
    var latitude: Double = getCurrentDefaultLatitude(),
    var longitude: Double = getCurrentDefaultLongitude(),
    var title: String = "",
    var snippet: String = "",
    var actuelleEtat: DernierEtatAAffiche = DernierEtatAAffiche.NON_DEFINI,
    //Etates Mutable
    var edite_Exact_Gps_est_fait: Boolean = false,
    // Section Centralization Valeurs Pour Injection add_New TOu modules
    var tagCeBonEstOuvertPourComptsIds: String = "",
    // Section keyFireBase et dernierFireBaseUpdateTimestamps
    var id: Long = 0L,
    var keyByParent: String = "",
    var bsonObjectId: String = BsonObjectId.Companion().toHexString(),

    val nomPrenomArabe: String = "حمنيش عبد الوهاب",
    val register_Commerce_Nm: String = "16/00 – 5138424 D20",
    val nif_Num: String = "16291403036",
    var secteur: String = "",

) {

    fun toFirebaseMap(): Map<String, Any?> = mapOf(
        "secteur" to secteur,
        "its_non_deletable_client_et_trxs" to its_non_deletable_client_et_trxs,

        "keyID" to keyID,
        "dernierTimeTampsSynchronisationAvecFireBase" to dernierTimeTampsSynchronisationAvecFireBase,
        "creationTimestamps" to creationTimestamps,
        "nom" to nom,
        "cretionTimestamps" to cretionTimestamps,
        "its_Fournisseur" to its_Fournisseur,
        "its_Client_De_Jamale" to its_Client_De_Jamale,
        "its_Fournisseur_Grossisst_A_Jomla" to its_Fournisseur_Grossisst_A_Jomla,
        "ces_credits_son_a_long_term" to ces_credits_son_a_long_term,
        "parentComptCreateurKEyID" to parentComptCreateurKEyID,
        "numTelephone" to numTelephone,
        "couleur" to couleur,
        "bonDuClientsSu" to bonDuClientsSu,
        "currentCreditBalance" to currentCreditBalance,
        "positionDonClientsList" to positionDonClientsList,
        "cUnClientTemporaire" to cUnClientTemporaire,
        "auFilterFAB" to auFilterFAB,
        "typeDeSonMagasine" to typeDeSonMagasine.name,
        "clientTypeMode" to clientTypeMode.name,
        "caMarqueGpsEstOuvert" to caMarqueGpsEstOuvert,
        "latitude" to latitude,
        "longitude" to longitude,
        "title" to title,
        "snippet" to snippet,
        "actuelleEtat" to actuelleEtat.name,
        "edite_Exact_Gps_est_fait" to edite_Exact_Gps_est_fait,
        "tagCeBonEstOuvertPourComptsIds" to tagCeBonEstOuvertPourComptsIds,
        "id" to id,
        "keyByParent" to keyByParent,
        "bsonObjectId" to bsonObjectId,
        "nomPrenomArabe" to nomPrenomArabe,
        "register_Commerce_Nm" to register_Commerce_Nm,
        "nif_Num" to nif_Num,
    )

    /**
     * Get Arabic name with fallback to French name
     */
    fun getNomAffichage(): String {
        return nomPrenomArabe.takeIf { it.isNotBlank() } ?: nom
    }

    /**
     * Get full display name with both French and Arabic if available
     */
    fun getNomComplet(): String {
        return if (nomPrenomArabe.isBlank()) {
            nom
        } else {
            "$nom ($nomPrenomArabe)"
        }
    }

    fun get_DebugInfos(): String {
        return buildString {
            append("(M2=")
            append(nom)
            append("[")
            append(keyID.takeLast(3).uppercase())
            append("])")
        }
    }

    fun getTempKeyByParent(): String {
        return this.nom.withOutFireBaseInvalidCharacters()
    }

    enum class DernierEtatAAffiche(val color: Int, val nomArabe: String) {
        NON_DEFINI(R.color.holo_orange_light, "غير محدد"),
        ON_MODE_COMMEND_ACTUELLEMENT(R.color.holo_green_light, "نشط / متصل"),
        VENDU_A_LUI(R.color.holo_purple, ""),
        Cible(R.color.holo_red_light, "Cible"),
        CIBLE_PRIORITE_2(R.color.holo_orange_dark, "CIBLE_PRIORITE_2"),
        CIBLE_PRIORITE_3(R.color.holo_green_light, "CIBLE_PRIORITE_3"),
        CIBLE_POUR_2(R.color.holo_blue_dark, "CIBLE_POUR_2"),
        ACHETEUR_NON_DISPO(R.color.darker_gray, "الشاري غائب"),
        AVEC_MARCHANDISE(R.color.holo_blue_light, "عندو سلعة"),
        FERME(R.color.darker_gray, "مغلق"),
        A_EVITE(R.color.black, "يتجنب"),
        CLIENT_ABSENT(R.color.darker_gray, "عميل غائب"),
    }

    enum class TypeDeSonMagasine(val color: Int, val nomArabe: String) {
        ATAYAT_MOUKASSARAT(R.color.holo_green_light, "عطارة ومكسرات"),
        AlIMENTATION_GENERALE(R.color.holo_purple, "مواد غذائية")
    }

    enum class ClientTypeMode(
        val icon: ImageVector,
        val color: Color
    ) {
        NEVEAU(
            icon = Icons.Default.Add,
            color = Color.Companion.Red
        ),
        ANCIEN(
            icon = Icons.Default.MonetizationOn,
            color = Color.Companion.Blue
        ),
        EVITE(
            icon = Icons.Default.Lock,
            color = Color.Companion.Gray
        )
    }

    fun with_Trigger_RealTime(): M2Client {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        const val pathString = "M02Client"

        val ref = central_MainDataBases_RefProduction
            .child(pathString)

        val ref_Non_Active_Datas =
            M00CentralParametresOfAllApps.Companion.centralRef_Non_Active_Datas_PourLightApp
                .child(pathString)
//
//        val parent = Firebase.database.getReference(
//            "00_DataPrototype-04-02" +
//                    "/_1_developingRef" +
//                    "/C_InfosSqlDataBases"
//        )
//        val ref = parent.child("B_ClientInfosProtoJuin3")

        fun generePushKey() = RepositorysMainSetter.genereUnPushKeyFireBase(ref)

        const val keyModel = "ID2"

        fun getCurrentDefaultLatitude(): Double {
            return 0.0
        }

        fun extractClientNamePrefix(clientName: String): String {
            return clientName.substringBefore(".", clientName).trim()
        }

        fun getCurrentDefaultLongitude(): Double {
            return 0.0
        }

        fun safe_Remove_MainDatas_Ref(onDone: () -> Unit = {}) {
            ref.removeValue().addOnSuccessListener { onDone() }
        }

        fun removeRef(preparedData: M2Client) {
            ref.child(preparedData.keyID).removeValue()
        }

        fun calculateCreditsMap(
            clients: List<M2Client>,
            bons: List<M8BonVent>,
            forFournisseurs: Boolean = false,
        ): Map<String, Double> {
            val bonsByClient = bons
                .filter { it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit }
                .groupBy { it.parent_M2Client_KeyID }

            return clients
                .filter { !it.ces_credits_son_a_long_term && !it.its_Client_De_Jamale }
                .filter { if (forFournisseurs) it.its_Fournisseur_Grossisst_A_Jomla else !it.its_Fournisseur_Grossisst_A_Jomla }
                .mapNotNull { client ->
                    val lastSituation =
                        bonsByClient[client.keyID]?.maxByOrNull { it.creationTimestamps }
                            ?: return@mapNotNull null
                    val brutMontant = lastSituation.montant_principale_du_type
                    // montant_principale_du_type = sumCredits - sumVersements.
                    // Pour un client normal, positif = il nous doit de l'argent.
                    // Pour un fournisseur, c'est l'inverse : les versements
                    // dépassant les crédits (donc un montant négatif) signifient
                    // qu'on lui doit de l'argent — c'est ça le crédit fournisseur
                    // à afficher, donc on inverse le signe dans ce cas.
                    val montant = if (forFournisseurs) -brutMontant else brutMontant
                    if (montant != 0.0) client.keyID to montant else null
                }.toMap()
        }

        fun calculateTotalCredit(
            clients: List<M2Client>,
            bons: List<M8BonVent>,
            forFournisseurs: Boolean = false,
        ): Double {
            return calculateCreditsMap(clients, bons, forFournisseurs).values.sum()
        }

        /**
         * Same as [calculateCreditsMap] but for clients whose credit is currently
         * being ignored (ignore_sont_credit == true), based on their last
         * New_Situation_Credit bon. Lets the UI surface "ignored credits" totals
         * separately from the normally-tracked credit total.
         */
        fun calculateIgnoredCreditsMap(
            clients: List<M2Client>,
            bons: List<M8BonVent>,
            forFournisseurs: Boolean = false,
        ): Map<String, Double> {
            val bonsByClient = bons
                .filter { it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit }
                .groupBy { it.parent_M2Client_KeyID }

            return clients
                .filter { it.ces_credits_son_a_long_term }
                .filter { if (forFournisseurs) it.its_Fournisseur_Grossisst_A_Jomla else !it.its_Fournisseur_Grossisst_A_Jomla }
                .mapNotNull { client ->
                    val lastSituation =
                        bonsByClient[client.keyID]?.maxByOrNull { it.creationTimestamps }
                            ?: return@mapNotNull null
                    val brutMontant = lastSituation.montant_principale_du_type
                    // Même inversion de signe que calculateCreditsMap pour les
                    // fournisseurs — voir le commentaire là-bas.
                    val montant = if (forFournisseurs) -brutMontant else brutMontant
                    if (montant != 0.0) client.keyID to montant else null
                }.toMap()
        }

        fun calculateTotalIgnoredCredit(
            clients: List<M2Client>,
            bons: List<M8BonVent>,
            forFournisseurs: Boolean = false,
        ): Double {
            return calculateIgnoredCreditsMap(clients, bons, forFournisseurs).values.sum()
        }

        fun get_default(): M2Client {
            return M2Client()
        }
    }
}

object Jomla_Clients {
    val ECHATILLANTS_KEY_ID =
        AbdelwahabJomla_Client_Speciale.AbdelwahabJomla_ECHATILLANTS_Ditha_MarqueSel3a.keyID
}

enum class AbdelwahabJomla_Client_Speciale(
    val keyID: String = "",
    val autre_nom: String = "",
    val moulahada: String = "",
) {
    Abdelwahab_Depo_Echant(
        "-OV9dYujH9cA3yEx8AY2",
    ),
    AbdelwahabJomla_ECHATILLANTS_Ditha_MarqueSel3a(
        "-Oh4W0-igT_bXGOo-LC_",
        autre_nom = "AbdelwahabJomla Marke Wach Dina Échantillon"
    ),
    AbdelwahabJomla_Marque_Sel3a_Au_Depot(
        "-OoK4WklxDWe_o19oc2F"
    ),
    Jomla_Marque_Sel3a_Ditha_Pour_Vendre(
        "-OfYtzn5JtD6Ne7gCOLu",
        autre_nom = " Abdelwahab mark sel3a ta3 Commande ",
        moulahada = "non supprime l ami jamel"
    ),
    AbdelwahabJomla_Promo_Sel3a(
        "-Op4u9T7KSOL5x5PSYa0",
        autre_nom = "Abdelwahab Jomla Promo Sel3a "
    ),
}
