package EntreApps.Shared.Models.Relative_Vents.Models

//noinspection SuspiciousImport
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import android.R
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Entity
data class M8BonVent(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var creationTimestamps: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var confirmeCommande_TimeTamp: Long = 0,
    var pourcentage_AffichageDuCatalogue_Conficerie: Double = 0.0,
    var pourcentage_AffichageDuCatalogue_Cosmitiques: Double = 0.0,
    var pourcentage_AffichageDuCatalogue_tebnage: Double = 0.0,

    val nombre_produits_don_dernier_pdf_stoked: Int = 0,
    val last_sort_pdf_locale_totale_a_paye: Double = 0.0,
    val path_pdf_bon_file: String = "",

    var parent_M9AppCompt_KeyID: String = "null",
    var parent_M9AppCompt_DebugInfos: String = "null",
    var parent_M14VentPeriod_KeyId: String = "null",
    var parent_M14VentPeriod_DebugInfos: String = "null",
    var parent_M2Client_KeyID: String = "null",
    var parent_M2Client_DebugInfos: String = "null",
    var parent_M2Client_OldLongID: Long = 0L,
    var parent_M17Message_KeyID: String = "null",
    var parent_M17Message_DebugInfos: String = "null",
    var its_Confirmation_de_TransactionKeyId: String = "",
    var heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    var heurFinInString: String = "Non Defini",
    var its_working_for_wholesaler: Boolean = false,
    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
    var vocaleKeyID: String = "",
    var sonVocaleEstEcoute: Boolean = false,
    var sonEcoutementEstFaitAutimestamps: Long = 0,
    var totale_saved: Double = 0.0,
    var cUn_Versement_duBonVentKey: String = "",
    var vala_supp: Int = 0,
    var a_etai_imprime_au_moi_ne_foit: Boolean = false,
    var versement_fait: Double = 0.0,
    var ancien_credit: Double = 0.0,
    var cUn_Credit_duBonVentKey: String = "",
    var new_credit_apre_tout_fait: Double = 0.0,
    var affiche_le_verssement_au_prochen_print: Boolean = false,
    var demande_Versemet_si_Type: Double = 0.0,
    var demande_Versemet_si_Type_est_regle: Boolean = false,
    var credit_fait: Double = 0.0,
    var sum_De_Totale_Vents: Double = 0.0,
    var sum_De_Credit_Fait: Double = 0.0,
    var versement: Double = 0.0,
    var position_Don_Lis_Cible_Clients_au_VentPeriod: Int = 0,
    var cLeDataOuvertDuParentList: Boolean? = null,
    var cActive: Boolean = false,
    val parentID8C2TypeTransactionKeyByParent: String = "",
    var vid: Long = 0L,
) {

    fun get_DebugInfos(): String {
        return buildString {
            append("Bon")
            append("[")
            append("p.cli->")
            append(parent_M2Client_DebugInfos)
            append(") ")
            append("[")
            append(keyID.takeLast(4))
            append("])")
        }
    }

    @IgnoreExtraProperties
    enum class EtateActuellementEst(val color: Int, val nomArabe: String) {
        CreeMaisNonDefinie(R.color.white, "غير محدد"),
        ON_MODE_COMMEND_ACTUELLEMENT(
            R.color.holo_green_light,
            " تنفيذ المطلوب في تحسين الوضع معه"
        ),
        Rapport_Entre_On_Etate_De_Bloquage(
            R.color.holo_red_light,
            ":تقرير الدخول معه في حالة انسداد في التجارة بسبب"
        ),
        Bloque_Probleme(com.example.clientjetpack.R.color.c3, "حدث مشكل معه"),
        Ordre_Gerant(com.example.clientjetpack.R.color.c4, "توجيه المسير"),
        A_COMMANDE_CONFIRME(
            R.color.holo_purple, "تم تاكيد الطلبية"
        ),
        COMMANDE_LIVRAI(R.color.holo_blue_dark, "تم أيصال منتجاته"),
        Cette_Transaction_Type_Est_Credit(R.color.holo_red_dark, "تم اقراضه  "),
        Versemment(com.example.clientjetpack.R.color.c5, ""),
        Demande_Versemet(R.color.holo_red_dark, "طلب تحظير الدين القديم عند احظار الطلبية"),
        ACHETEUR_NON_DISPO(com.example.clientjetpack.R.color.c2, "الشاري غائب"),
        AVEC_MARCHANDISE(com.example.clientjetpack.R.color.c5, "عندو سلعة"),

        New_Situation_Credit(R.color.holo_red_dark, "الحالة الجديدة للدين"),

        FERME(R.color.darker_gray, "مغلق"),
        Cible(R.color.holo_orange_dark, "معين من المسير"),
        CIBLE_PRIORITE_2(R.color.holo_orange_dark, "CIBLE_PRIORITE_2"),
        CIBLE_PRIORITE_3(R.color.holo_green_light, "CIBLE_PRIORITE_3"),
        CIBLE_POUR_2(R.color.holo_blue_dark, "CIBLE_POUR_2"),
        PourVoirPanie(
            R.color.holo_red_light, "للنظر"
        ),
        RAPPORT_AU_ENREGESTREMENT_VOCALE(R.color.black, "التقرير قي التسجيل الصوتي "),
        ON_MODE_VOIRE_PANIE_ARTICLES(R.color.holo_blue_dark, "في معاينة السلة"),
        A_EVITE(R.color.holo_green_light, "اقترح ان يتجنب لمدة اسبوعين"),
        PASSE(com.example.clientjetpack.R.color.c6, "اقترح ان يؤجل الى مدة قادمة"),
        CommantaireSpeciale(com.example.clientjetpack.R.color.c7, "ملاحظة خاصة بالطلبية"),
        Passed_Sans_Livre(R.color.darker_gray, "Passed_Sans_Livre"),
        Credit(R.color.holo_red_dark, " "), ;

        companion object {
            const val keyModel = "ID8C2"
        }
    }

    fun isSameEntity(other: M8BonVent) =
        keyID == other.keyID && parent_M9AppCompt_KeyID == other.parent_M9AppCompt_KeyID && parent_M14VentPeriod_KeyId == other.parent_M14VentPeriod_KeyId

    override fun equals(other: Any?) =
        this === other || (other is M8BonVent && isSameEntity(other))

    override fun hashCode() = Objects.hash(
        keyID, parent_M9AppCompt_DebugInfos, parent_M14VentPeriod_KeyId
    )

    companion object {
        const val keyModel = "ID8"

        fun remove_ref() {
            ref.removeValue()
        }

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases"
        ).child("Datas08BonVent")

        fun generePushKey() = RepositorysMainSetter.Companion.genereUnPushKeyFireBase(ref)

        fun get_default2(): M8BonVent {
            return M8BonVent()
        }

        fun get_default(
            parent_M9AppCompt_KeyID: String,
            parent_M9AppCompt_DebugInfos: String,
            parent_M14VentPeriod_DebugInfos: String,
            parent_M14VentPeriod_KeyId: String,
            parent_M2Client_KeyID: String,
            parent_M2Client_DebugInfos: String,
            etateActuellementEst: EtateActuellementEst? = null,
        ): M8BonVent {
            return M8BonVent(
                parent_M9AppCompt_DebugInfos = parent_M9AppCompt_DebugInfos,
                parent_M9AppCompt_KeyID = parent_M9AppCompt_KeyID,
                parent_M14VentPeriod_DebugInfos = parent_M14VentPeriod_DebugInfos,
                parent_M14VentPeriod_KeyId = parent_M14VentPeriod_KeyId,
                parent_M2Client_KeyID = parent_M2Client_KeyID,
                parent_M2Client_DebugInfos = parent_M2Client_DebugInfos,
                etateActuellementEst = etateActuellementEst
                    ?: EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            )
        }

        fun find_By_MainValuesKeys_Depuit_List(
            data_List: List<M8BonVent>,
            parent_M14VentPeriod_KeyId: String,
            parent_M2Client_KeyID: String,
            relative_Etate: EtateActuellementEst? = null,
        ) = data_List
            .find { data ->
                val match_MainValuesKeys =
                    data.parent_M14VentPeriod_KeyId == parent_M14VentPeriod_KeyId
                            && data.parent_M2Client_KeyID == parent_M2Client_KeyID
                            && data.etateActuellementEst == relative_Etate
                match_MainValuesKeys
            }


        fun M8BonVent.sum_totale_et_benifice(
            vents: List<M10OperationVentCouleur>,
            tariffs: List<M13TarificationInfos>,
        ): Sums_Bons {
            var totale = 0.0
            var benifices = 0.0

            vents
                .filter { it.parent_M8BonVent_KeyId == this.keyID }
                .forEach { op ->
                    val prixAchat: Double? = tariffs
                        .filter {
                            it.parent_M1Produit_KeyId == op.parent_M1Produit_KeyId &&
                                    it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
                        }
                        .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }
                        ?.prixCurrency

                    totale += op.prix_de_Vent_entre_directement_NewProto * op.quantity
                    // Skip benefit calculation if purchase price is null or zero
                    if (prixAchat != null && prixAchat != 0.0) {
                        benifices += (op.prix_de_Vent_entre_directement_NewProto - prixAchat) * op.quantity
                    }
                }

            return Sums_Bons(totale_vents = totale, benifices_vents = benifices)
        }

        fun M8BonVent.benifice(
            vents: List<M10OperationVentCouleur>,
            tariffs: List<M13TarificationInfos>,
        ): Double = sum_totale_et_benifice(vents, tariffs).benifices_vents

        fun M8BonVent.sum_totale_vents(
            vents: List<M10OperationVentCouleur>,
            tariffs: List<M13TarificationInfos>,
        ): Double = sum_totale_et_benifice(vents, tariffs).totale_vents
    }
}

data class Sums_Bons(
    val totale_vents: Double,
    val benifices_vents: Double,
)
