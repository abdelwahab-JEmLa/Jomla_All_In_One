package EntreApps.Shared.Models.Relative_Vents.Models

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.central_MainDataBases_RefProduction
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class M13TarificationInfos(

    @PrimaryKey
    val keyID: String = M09AppCompt.Companion.getPushFireBase(ref),

    val id: Long = 0L,
    var creationTimestamps: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var defaultNonSaved_Entre: Boolean = true,

    var its_From_CalculeParNewBenifice: Boolean = true,

    var laisse_Au_Gerant: Boolean = false,

    val typeChoisi: TypeChoisi = TypeChoisi.Prix_SupperGro_Et_PresentationService,
    val prixCurrency: Double = 0.0,

    val profitMargin: Double = 0.0,
    val suggestedUpgrade: TypeChoisi? = null,
    //---------------------------------Parent.M14VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parent_M14VentPeriod_KeyId: String = "",
    var parent_M14VentPeriod_DebugInfos: String = "",

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    var parent_M1Produit_KeyId: String = "null",
    val parent_M1Produit_DebugInfos: String = "null",
    var parent_M8BonVent_KeyId: String = "null",
    val parent_M8BonVent_DebugInfos: String = "null",
    var parent_M2Client_KeyId: String = "null",
    val parent_M2Client_DebugInfos: String = "null",
) {
    enum class TypeChoisi(
        val iconVector: ImageVector? = null,
        val couleur: Color = Color.Companion.White,
        val nomArabe: String = "",
        val couleur_Text: Color = Color.Companion.White,
        val profitabilityScore: Int = 0,
        val abrgNom: String = "",
        val its_gro_app: Boolean = false,
        val ignore_affiche: Boolean = false,

        ) {
        //------- Grossist App Specific Tariffs (its_gro_app = true) --------
        Tariff_ItsWorkInGrossist_Achat(
            Icons.Filled.History,
            Color(0xFFEEEEEE),
            "سعر شراء الجملة",
            Color(0xFF2196F3),
            1,
            "س.ش",
            its_gro_app = true  // Only shown in grossist app
        ),

        Tariff_ItsWorkInGrossist_SuperGros(
            Icons.Filled.Warning,
            Color(0xFF000000),
            "سعر السوبر جملة عند الكمية",
            Color(0xFFF44336),
            2,
            "كمية",
            its_gro_app = true
        ),

        Tariff_ItsWorkInGrossist_Progressive(
            Icons.Filled.Edit,
            Color(0xFFEEEEEE),
            "سعر انتقالي للجملة",
            Color(0xFF000000),
            3,
            "انتقالي",
            its_gro_app = true  // Only shown in grossist app
        ),

        Tariff_ItsWorkInGrossist_Gro(
            Icons.Filled.Person,
            Color(0xFFFF5722),
            "سعر النصف جملة",
            Color(0xFFE6E8EA),
            4,
            "نصف",
            its_gro_app = true  // Only shown in grossist app
        ),

        //------- Regular App Tariffs (its_gro_app = false) --------
        Tariff_Achat_Depuit_Grossisst(
            Icons.Filled.History,
            Color(0xFF000000),
            "سعر الشراء",
            Color(0xFF2196F3),
            1,
            "ش",
            its_gro_app = false
        ),

        DEFIN_OLd(
            Icons.Filled.Edit,
            Color(0xFF03A9F4),
            "قديم",
            Color.Companion.Black,
            0,
            "قديم",
            its_gro_app = false
        ),

        LeMaxPrixArrive(
            Icons.Filled.ArrowUpward,
            Color(0xFFFF9800),
            "اعلى سعر وصل له",
            Color.Companion.Black,
            5,
            "أعلى",
            its_gro_app = false,
            ignore_affiche = true
        ),

        Historique(
            Icons.Filled.History,
            Color(0xFF9C27B0),
            "السعر الأخير",
            Color.Companion.White,
            5,
            "أخير",
            its_gro_app = false,
            ignore_affiche = true  // Only shown in grossist app
        ),

        Prix_SupperGro_Et_PresentationService(
            Icons.Filled.Warning,
            Color(0xFFF44336),
            "عرض + السوبر جملة",
            Color.Companion.White,
            2,
            "عرض",
            its_gro_app = false
        ),

        Edited_Pour_Client(
            Icons.Filled.Edit,
            Color(0xFFFFEB3B),
            "سعر انتقالي",
            Color.Companion.Black,
            3,
            "انتقالي",
            its_gro_app = false
        ),

        Prix_Progressive_Editable(
            Icons.Filled.Edit,
            Color(0xFFFFEB3B),
            "سعر انتقالي",
            Color.Companion.Black,
            3,
            "انتقالي",
            its_gro_app = false,
            ignore_affiche = true
        ),

        Prix_Detaille(
            Icons.Filled.Person,
            Color(0xFF1A711F),
            "سعر التجزئة",
            Color.Companion.White,
            4,
            "تجزئة",
            its_gro_app = false
        );

        fun getNextProfitableType(): TypeChoisi? {
            return when (this) {
                Tariff_ItsWorkInGrossist_Achat -> Tariff_ItsWorkInGrossist_SuperGros
                Tariff_ItsWorkInGrossist_SuperGros -> Tariff_ItsWorkInGrossist_Progressive
                Tariff_ItsWorkInGrossist_Progressive -> Tariff_ItsWorkInGrossist_Gro
                Tariff_ItsWorkInGrossist_Gro -> null  // Top level for grossist

                // Regular app progression
                Tariff_Achat_Depuit_Grossisst -> Prix_SupperGro_Et_PresentationService
                Prix_SupperGro_Et_PresentationService -> Historique
                Historique -> Edited_Pour_Client
                Edited_Pour_Client -> Prix_Progressive_Editable
                Prix_Progressive_Editable -> Prix_Detaille
                Prix_Detaille -> LeMaxPrixArrive
                LeMaxPrixArrive -> null
                DEFIN_OLd -> Historique
            }
        }

        fun isTopProfitable(): Boolean {
            return profitabilityScore >= 3
        }

        fun getImprovementSuggestion(): String {
            val nextType = getNextProfitableType()
            return if (nextType != null) {
                "اقتراح: انتقل إلى ${nextType.nomArabe} لربح أكثر"
            } else {
                "ممتاز! أنت في أعلى مستوى ربح"
            }
        }
    }

    fun getDebugInfos(): String {
        return "$parent_M1Produit_DebugInfos $typeChoisi"
    }

    fun withProperDefaults(): M13TarificationInfos {
        return this.copy(
            suggestedUpgrade = typeChoisi.getNextProfitableType()
        )
    }

    fun toFirebaseMap(): Map<String, Any?> = mapOf(
        "keyID" to keyID,
        "id" to id,
        "creationTimestamps" to creationTimestamps,
        "dernierTimeTampsSynchronisationAvecFireBase" to dernierTimeTampsSynchronisationAvecFireBase,
        "defaultNonSaved_Entre" to defaultNonSaved_Entre,
        "its_From_CalculeParNewBenifice" to its_From_CalculeParNewBenifice,
        "laisse_Au_Gerant" to laisse_Au_Gerant,
        "typeChoisi" to typeChoisi.name,
        "prixCurrency" to prixCurrency,
        "profitMargin" to profitMargin,
        "suggestedUpgrade" to suggestedUpgrade?.name,
        "parent_M14VentPeriod_KeyId" to parent_M14VentPeriod_KeyId,
        "parent_M14VentPeriod_DebugInfos" to parent_M14VentPeriod_DebugInfos,
        "parent_M1Produit_KeyId" to parent_M1Produit_KeyId,
        "parent_M1Produit_DebugInfos" to parent_M1Produit_DebugInfos,
        "parent_M8BonVent_KeyId" to parent_M8BonVent_KeyId,
        "parent_M8BonVent_DebugInfos" to parent_M8BonVent_DebugInfos,
        "parent_M2Client_KeyId" to parent_M2Client_KeyId,
        "parent_M2Client_DebugInfos" to parent_M2Client_DebugInfos,
    )

    companion object {
        val ref = central_MainDataBases_RefProduction
            .child("M13Tariffication")

        /*  val ref = Firebase.database.getReference(
              "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/DataBase13TarificationInfos"
          )                   */

        val ref_NonActiveDatas =
            M00CentralParametresOfAllApps.Companion.centralRef_Non_Active_Datas_PourLightApp
                .child("M13Tariffication")


        fun get_default_P0(
            parentM1ProduitInfos: M01Produit,
            start_Prix_Depuit_Ancient: Double,
        ): Pair<M13TarificationInfos, Modifier> {
            val m13TarificationInfos = M13TarificationInfos(
                parent_M1Produit_KeyId = parentM1ProduitInfos.keyID,
                parent_M1Produit_DebugInfos = parentM1ProduitInfos.getDebugInfos(),
                prixCurrency = start_Prix_Depuit_Ancient,
                suggestedUpgrade = TypeChoisi.Historique.getNextProfitableType()
            )
            val modifier = Modifier.Companion.getSemanticsTag(
                nomVal = "m13TarificationInfos",
                data = m13TarificationInfos
            )
            return Pair(m13TarificationInfos, modifier)
        }

        fun get_default(): M13TarificationInfos {
            return M13TarificationInfos()
        }


        fun remembered_calculated_progressive_changement_tariff(
            relative_Prix_Detaille: Double?,
            relative_Prix_SupperGro_Et_PresentationService: Double?,
            relative_produit: M01Produit,
        ): M13TarificationInfos? {
            val supperGro = relative_Prix_SupperGro_Et_PresentationService
                ?.takeIf { it != 0.0 }
            val detaille = relative_Prix_Detaille
                ?.takeIf { it != 0.0 }

            // Both missing or zero → no progressive price
            if (supperGro == null && detaille == null) return null

            val calculatedPrice = when {
                // Both available → average
                supperGro != null && detaille != null ->
                    (supperGro + detaille) / 2.0
                // Only one available → use it directly
                supperGro != null -> supperGro
                else              -> detaille!!
            }

            return M13TarificationInfos(
                parent_M1Produit_KeyId = relative_produit.keyID,
                parent_M1Produit_DebugInfos = relative_produit.getDebugInfos(),
                typeChoisi = TypeChoisi.Edited_Pour_Client,
                prixCurrency = calculatedPrice,
                creationTimestamps = System.currentTimeMillis(),
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        }


        fun analyzeSalesDistribution(
            groupedSales: List<Map.Entry<TypeChoisi, List<M01Produit>>>
        ): String {
            val totalProducts = groupedSales.sumOf { it.value.size }
            val topProfitableCount = groupedSales
                .filter { it.key.isTopProfitable() }
                .sumOf { it.value.size }

            val profitablePercentage = if (totalProducts > 0) {
                (topProfitableCount * 100) / totalProducts
            } else 0

            return when {
                profitablePercentage >= 80 -> "ممتاز! ${profitablePercentage}% من مبيعاتك بأسعار مربحة"
                profitablePercentage >= 60 -> "جيد! ${profitablePercentage}% مربح، حاول رفع النسبة"
                profitablePercentage >= 40 -> "يمكن التحسين! ${profitablePercentage}% مربح، ركز على الأسعار العالية"
                else -> "تحتاج تحسين! ${profitablePercentage}% فقط مربح، ارفع أسعارك"
            }
        }
    }
}

