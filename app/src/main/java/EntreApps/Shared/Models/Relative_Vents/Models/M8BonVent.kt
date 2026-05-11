package EntreApps.Shared.Models.Relative_Vents.Models

//noinspection SuspiciousImport,SuspiciousImport
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.central_Developing_Test
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.central_MainDataBases_RefProduction
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.io.File
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
    var vala_supp: Int = 0,
    var a_etai_imprime_au_moi_ne_foit: Boolean = false,

    var cUn_Versement_duBonVentKey: String = "",
    var versement_fait: Double = 0.0,
    var ancien_credit: Double = 0.0,
    var cUn_Credit_duBonVentKey: String = "",
    var new_credit_apre_tout_fait: Double = 0.0,
    var demande_Versemet_si_Type: Double = 0.0,
    var demande_Versemet_si_Type_est_regle: Boolean = false,
    var affiche_le_verssement_au_prochen_print: Boolean = false,
    var sum_De_Credit_Fait: Double = 0.0,
    var versement: Double = 0.0,
    var credit_fait: Double = 0.0,
    var montant_principale_du_type: Double = 0.0,


    var sum_De_Totale_Vents: Double = 0.0,
    var position_Don_Lis_Cible_Clients_au_VentPeriod: Int = 0,
    var cLeDataOuvertDuParentList: Boolean? = null,
    var cActive: Boolean = false,
    val parentID8C2TypeTransactionKeyByParent: String = "",
    var vid: Long = 0L,
    var moulahada: String = "",    //06_27
    var new_situation: Double = 0.0, //06_21
) {
    fun to_Map(): Map<String, Any?> {
        return mapOf(
            "keyID" to keyID,
            "creationTimestamps" to creationTimestamps,
            "dernierTimeTampsSynchronisationAvecFireBase" to dernierTimeTampsSynchronisationAvecFireBase,
            "confirmeCommande_TimeTamp" to confirmeCommande_TimeTamp,
            "pourcentage_AffichageDuCatalogue_Conficerie" to pourcentage_AffichageDuCatalogue_Conficerie,
            "pourcentage_AffichageDuCatalogue_Cosmitiques" to pourcentage_AffichageDuCatalogue_Cosmitiques,
            "pourcentage_AffichageDuCatalogue_tebnage" to pourcentage_AffichageDuCatalogue_tebnage,
            "nombre_produits_don_dernier_pdf_stoked" to nombre_produits_don_dernier_pdf_stoked,
            "last_sort_pdf_locale_totale_a_paye" to last_sort_pdf_locale_totale_a_paye,
            "path_pdf_bon_file" to path_pdf_bon_file,
            "parent_M9AppCompt_KeyID" to parent_M9AppCompt_KeyID,
            "parent_M9AppCompt_DebugInfos" to parent_M9AppCompt_DebugInfos,
            "parent_M14VentPeriod_KeyId" to parent_M14VentPeriod_KeyId,
            "parent_M14VentPeriod_DebugInfos" to parent_M14VentPeriod_DebugInfos,
            "parent_M2Client_KeyID" to parent_M2Client_KeyID,
            "parent_M2Client_DebugInfos" to parent_M2Client_DebugInfos,
            "parent_M2Client_OldLongID" to parent_M2Client_OldLongID,
            "parent_M17Message_KeyID" to parent_M17Message_KeyID,
            "parent_M17Message_DebugInfos" to parent_M17Message_DebugInfos,
            "its_Confirmation_de_TransactionKeyId" to its_Confirmation_de_TransactionKeyId,
            "heurDebutInString" to heurDebutInString,
            "heurFinInString" to heurFinInString,
            "its_working_for_wholesaler" to its_working_for_wholesaler,

            "etateActuellementEst" to etateActuellementEst.name,
            "vocaleKeyID" to vocaleKeyID,
            "sonVocaleEstEcoute" to sonVocaleEstEcoute,
            "sonEcoutementEstFaitAutimestamps" to sonEcoutementEstFaitAutimestamps,
            "totale_saved" to totale_saved,
            "vala_supp" to vala_supp,
            "a_etai_imprime_au_moi_ne_foit" to a_etai_imprime_au_moi_ne_foit,
            "cUn_Versement_duBonVentKey" to cUn_Versement_duBonVentKey,
            "versement_fait" to versement_fait,
            "ancien_credit" to ancien_credit,
            "cUn_Credit_duBonVentKey" to cUn_Credit_duBonVentKey,
            "new_credit_apre_tout_fait" to new_credit_apre_tout_fait,
            "demande_Versemet_si_Type" to demande_Versemet_si_Type,
            "demande_Versemet_si_Type_est_regle" to demande_Versemet_si_Type_est_regle,
            "affiche_le_verssement_au_prochen_print" to affiche_le_verssement_au_prochen_print,
            "sum_De_Credit_Fait" to sum_De_Credit_Fait,
            "versement" to versement,
            "credit_fait" to credit_fait,
            "montant_principale_du_type" to montant_principale_du_type,
            "sum_De_Totale_Vents" to sum_De_Totale_Vents,
            "position_Don_Lis_Cible_Clients_au_VentPeriod" to position_Don_Lis_Cible_Clients_au_VentPeriod,
            "cLeDataOuvertDuParentList" to cLeDataOuvertDuParentList,
            "cActive" to cActive,
            "parentID8C2TypeTransactionKeyByParent" to parentID8C2TypeTransactionKeyByParent,
            "vid" to vid,
            "moulahada" to moulahada,
            "new_situation" to new_situation,
        )
    }

    fun fun_calculative_du_main_val(allBons: List<M8BonVent>): Double {
        val samePeriodClientBons = allBons.filter {
            it.parent_M2Client_KeyID == this.parent_M2Client_KeyID &&
                    it.parent_M14VentPeriod_KeyId == this.parent_M14VentPeriod_KeyId
        }
        return when (etateActuellementEst) {
            EtateActuellementEst.New_Situation_Credit -> {
                val sumCredits = samePeriodClientBons
                    .filter {
                        it.etateActuellementEst == EtateActuellementEst.Credit ||
                                it.etateActuellementEst == EtateActuellementEst.Cette_Transaction_Type_Est_Credit
                    }
                    .sumOf { it.credit_fait }
                val sumVersements = samePeriodClientBons
                    .filter { it.etateActuellementEst == EtateActuellementEst.Versemment }
                    .sumOf { it.versement_fait }
                sumCredits - sumVersements
            }

            EtateActuellementEst.Versemment -> versement_fait
            EtateActuellementEst.Credit,
            EtateActuellementEst.Cette_Transaction_Type_Est_Credit -> credit_fait

            EtateActuellementEst.Demande_Versemet -> demande_Versemet_si_Type
            else -> 0.0
        }
    }


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
    enum class EtateActuellementEst(
        val color: Color,
        val nomArabe: String,
        val credit_type: Boolean = false,
        val nonDeletable: Boolean = false,
        val text_color: Color = Color(0xFFFFFFFF)
    ) {
        CreeMaisNonDefinie(Color(0xFFFFFFFF), "غير محدد"),
        ON_MODE_COMMEND_ACTUELLEMENT(
            Color(0xFF99CC00),
            " تنفيذ المطلوب في تحسين الوضع معه"
        ),
        Rapport_Entre_On_Etate_De_Bloquage(
            Color(0xFFFF4444),
            ":تقرير الدخول معه في حالة انسداد في التجارة بسبب"
        ),
        Bloque_Probleme(Color(0xFFCC0000), "حدث مشكل معه"),
        Ordre_Gerant(Color(0xFFCC0000), "توجيه المسير"),
        A_COMMANDE_CONFIRME(Color(0xFF9933CC), "تم تاكيد الطلبية",nonDeletable=true),
        COMMANDE_LIVRAI(Color(0xFF0099CC), "تم أيصال منتجاته"),

        ACHETEUR_NON_DISPO(Color(0xFFCC0000), "الشاري غائب"),
        AVEC_MARCHANDISE(Color(0xFFCC0000), "عندو سلعة"),
        FERME(Color(0xFF444444), "مغلق"),
        Cible(Color(0xFFFF6700), "معين من المسير"),
        CIBLE_PRIORITE_2(Color(0xFFFF6700), "CIBLE_PRIORITE_2"),
        CIBLE_PRIORITE_3(Color(0xFF99CC00), "CIBLE_PRIORITE_3"),
        CIBLE_POUR_2(Color(0xFF0099CC), "CIBLE_POUR_2"),
        PourVoirPanie(Color(0xFFFF4444), "للنظر"),
        RAPPORT_AU_ENREGESTREMENT_VOCALE(Color(0xFF000000), "التقرير قي التسجيل الصوتي "),
        ON_MODE_VOIRE_PANIE_ARTICLES(Color(0xFF0099CC), "في معاينة السلة"),
        A_EVITE(Color(0xFF99CC00), "اقترح ان يتجنب لمدة اسبوعين"),
        PASSE(Color(0xFFCC0000), "اقترح ان يؤجل الى مدة قادمة"),
        CommantaireSpeciale(Color(0xFFCC0000), "ملاحظة خاصة بالطلبية"),
        Passed_Sans_Livre(Color(0xFF444444), "Passed_Sans_Livre"),

        //Credits
        Credit(Color(0xFFFF5722), " ", credit_type = true,nonDeletable=true),
        Cette_Transaction_Type_Est_Credit(Color(0xFFFF5722), "تم اقراضه  ", credit_type = true,nonDeletable=true),
        Versemment(Color(0xFF4CAF50), "", credit_type = true,nonDeletable=true),
        Demande_Versemet(
            Color(0xFFCDDC39), "المبلغ المرجو تحظيره", credit_type = true,
            text_color = Color(0xFF000000)     ,nonDeletable=true
        ),
        New_Situation_Credit(Color(0xFFD2180D), "الحالة الجديدة للدين", credit_type = true,nonDeletable=true),
        ;

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
        const val nam_Model_Str = "M08BonVent"
        val ref = central_MainDataBases_RefProduction.child(nam_Model_Str)
        val ref_Non_Active_Datas = M00CentralParametresOfAllApps.centralRef_Non_Active_Datas_PourLightApp.child(nam_Model_Str)
        val ref_Test = central_Developing_Test.child(nam_Model_Str)

        fun remove_ref() {
            ref.removeValue()
        }

        val csv_test = File(
            M00CentralParametresOfAllApps.central_Local_Csv,
            "TestDatas/$nam_Model_Str.csv"
        )

        fun generePushKey() = M00CentralParametresOfAllApps.genereUnPushKeyFireBase(
            if (M00CentralParametresOfAllApps.get_Default().chose_ref_test_For_Datas_Car_C_DevMode) ref_Test else ref
        )

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

        fun to_Map(map: Map<String, String?>): M8BonVent {
            return M8BonVent(
                keyID = map["keyID"] ?: generePushKey(),
                creationTimestamps = map["creationTimestamps"]?.toLongOrNull()
                    ?: System.currentTimeMillis(),
                dernierTimeTampsSynchronisationAvecFireBase = map["dernierTimeTampsSynchronisationAvecFireBase"]?.toLongOrNull()
                    ?: System.currentTimeMillis(),
                confirmeCommande_TimeTamp = map["confirmeCommande_TimeTamp"]?.toLongOrNull() ?: 0L,
                pourcentage_AffichageDuCatalogue_Conficerie = map["pourcentage_AffichageDuCatalogue_Conficerie"]?.toDoubleOrNull()
                    ?: 0.0,
                pourcentage_AffichageDuCatalogue_Cosmitiques = map["pourcentage_AffichageDuCatalogue_Cosmitiques"]?.toDoubleOrNull()
                    ?: 0.0,
                pourcentage_AffichageDuCatalogue_tebnage = map["pourcentage_AffichageDuCatalogue_tebnage"]?.toDoubleOrNull()
                    ?: 0.0,
                nombre_produits_don_dernier_pdf_stoked = map["nombre_produits_don_dernier_pdf_stoked"]?.toIntOrNull()
                    ?: 0,
                last_sort_pdf_locale_totale_a_paye = map["last_sort_pdf_locale_totale_a_paye"]?.toDoubleOrNull()
                    ?: 0.0,
                path_pdf_bon_file = map["path_pdf_bon_file"] ?: "",
                parent_M9AppCompt_KeyID = map["parent_M9AppCompt_KeyID"] ?: "null",
                parent_M9AppCompt_DebugInfos = map["parent_M9AppCompt_DebugInfos"] ?: "null",
                parent_M14VentPeriod_KeyId = map["parent_M14VentPeriod_KeyId"] ?: "null",
                parent_M14VentPeriod_DebugInfos = map["parent_M14VentPeriod_DebugInfos"] ?: "null",
                parent_M2Client_KeyID = map["parent_M2Client_KeyID"] ?: "null",
                parent_M2Client_DebugInfos = map["parent_M2Client_DebugInfos"] ?: "null",
                parent_M2Client_OldLongID = map["parent_M2Client_OldLongID"]?.toLongOrNull() ?: 0L,
                parent_M17Message_KeyID = map["parent_M17Message_KeyID"] ?: "null",
                parent_M17Message_DebugInfos = map["parent_M17Message_DebugInfos"] ?: "null",
                its_Confirmation_de_TransactionKeyId = map["its_Confirmation_de_TransactionKeyId"]
                    ?: "",
                heurDebutInString = map["heurDebutInString"] ?: SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                ).format(Date()),
                heurFinInString = map["heurFinInString"] ?: "Non Defini",
                its_working_for_wholesaler = map["its_working_for_wholesaler"]?.equals(
                    "true",
                    ignoreCase = true
                ) ?: false,
                etateActuellementEst = map["etateActuellementEst"]?.let {
                    runCatching { EtateActuellementEst.valueOf(it) }.getOrDefault(
                        EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                    )
                } ?: EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                vocaleKeyID = map["vocaleKeyID"] ?: "",
                sonVocaleEstEcoute = map["sonVocaleEstEcoute"]?.equals("true", ignoreCase = true)
                    ?: false,
                sonEcoutementEstFaitAutimestamps = map["sonEcoutementEstFaitAutimestamps"]?.toLongOrNull()
                    ?: 0L,
                totale_saved = map["totale_saved"]?.toDoubleOrNull() ?: 0.0,
                vala_supp = map["vala_supp"]?.toIntOrNull() ?: 0,
                a_etai_imprime_au_moi_ne_foit = map["a_etai_imprime_au_moi_ne_foit"]?.equals(
                    "true",
                    ignoreCase = true
                ) ?: false,
                cUn_Versement_duBonVentKey = map["cUn_Versement_duBonVentKey"] ?: "",
                versement_fait = map["versement_fait"]?.toDoubleOrNull() ?: 0.0,
                ancien_credit = map["ancien_credit"]?.toDoubleOrNull() ?: 0.0,
                cUn_Credit_duBonVentKey = map["cUn_Credit_duBonVentKey"] ?: "",
                new_credit_apre_tout_fait = map["new_credit_apre_tout_fait"]?.toDoubleOrNull()
                    ?: 0.0,
                demande_Versemet_si_Type = map["demande_Versemet_si_Type"]?.toDoubleOrNull() ?: 0.0,
                demande_Versemet_si_Type_est_regle = map["demande_Versemet_si_Type_est_regle"]?.equals(
                    "true",
                    ignoreCase = true
                ) ?: false,
                affiche_le_verssement_au_prochen_print = map["affiche_le_verssement_au_prochen_print"]?.equals(
                    "true",
                    ignoreCase = true
                ) ?: false,
                sum_De_Credit_Fait = map["sum_De_Credit_Fait"]?.toDoubleOrNull() ?: 0.0,
                versement = map["versement"]?.toDoubleOrNull() ?: 0.0,
                credit_fait = map["credit_fait"]?.toDoubleOrNull() ?: 0.0,
                montant_principale_du_type = map["montant_principale_du_type"]?.toDoubleOrNull()
                    ?: 0.0,
                sum_De_Totale_Vents = map["sum_De_Totale_Vents"]?.toDoubleOrNull() ?: 0.0,
                position_Don_Lis_Cible_Clients_au_VentPeriod = map["position_Don_Lis_Cible_Clients_au_VentPeriod"]?.toIntOrNull()
                    ?: 0,
                cLeDataOuvertDuParentList = map["cLeDataOuvertDuParentList"]?.let {
                    if (it.isBlank()) null else it.equals("true", ignoreCase = true)
                },
                cActive = map["cActive"]?.equals("true", ignoreCase = true) ?: false,
                parentID8C2TypeTransactionKeyByParent = map["parentID8C2TypeTransactionKeyByParent"]
                    ?: "",
                vid = map["vid"]?.toLongOrNull() ?: 0L,
                moulahada = map["moulahada"] ?: "",
                new_situation = map["new_situation"]?.toDoubleOrNull() ?: 0.0,
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
