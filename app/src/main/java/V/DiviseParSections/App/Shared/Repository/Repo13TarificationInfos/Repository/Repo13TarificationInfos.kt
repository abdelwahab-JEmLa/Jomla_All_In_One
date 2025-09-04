package V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt.Companion.getPushFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.DataBaseCreationFactory13TarificationInfos
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo13TarificationInfos(
    val dataBaseCreationFactory: DataBaseCreationFactory13TarificationInfos,
    val zAppComptRepositoryComposable: Repo9AppCompt,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M13TarificationInfos>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun upsert(data: M13TarificationInfos) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        val existingIndex = datasValue.indexOfFirst { it.keyID == dataUpdate.keyID }

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    if (existingIndex >= 0) {
                        this[existingIndex] = dataUpdate
                    } else {
                        add(dataUpdate)
                    }
                }
            }
        }
        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    fun add(data: M13TarificationInfos) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    private fun ancienRepoUpsertUneDataEtReturnVID(dataUpdate: M13TarificationInfos) {
        dataBaseCreationFactory.set(dataUpdate)
    }

    fun delete(data: M13TarificationInfos) {
        composScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
            }
        }
    }
}

@Entity
data class M13TarificationInfos(
    @PrimaryKey
    val keyID: String = getPushFireBase(ref),

    val id: Long = 0L,
    var creationTimestamps: Long = 0,
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var defaultNonSaved_Entre: Boolean = true,

    val typeChoisi: TypeChoisi = TypeChoisi.Historique,
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
        val couleur: Color = Color.White,
        val nomArabe: String = "",
        val couleur_Text: Color = Color.White,
        val profitabilityScore: Int = 0,
    ) {
        //---------------------------------------currentApp_ItsWorkChezGrossisst  ------------------------------------------------------------------------------------------------

        Tariff_Grossist_Achat(
            Icons.Filled.History,
            Color(0xFF000000),
            "",
            Color(0xFF2196F3),
            1
        ),

        Tariff_Grossist_SuperGros(
            Icons.Filled.History,
            Color(0xFF000000),
            "",
            Color(0xFFF44336),
            1
        ),
        Tariff_Grossist_Progressive(
            Icons.Filled.History,
            Color(0xFFEEEEEE),
            "",
            Color(0xFF000000),
            1
        ),
        Tariff_Grossist_Gro(
            Icons.Filled.History,
            Color(0xFFCDDC39),
            "",
            Color(0xFF2196F3),
            1
        ),

        //--------------------------------------------------------------------------------------------------------------------------------
        Tariff_Achat_Depuit_Grossisst(
            Icons.Filled.History,
            Color(0xFF000000),
            "سعر الشراء",
            Color(0xFF2196F3),
            1
        ),

        DEFIN_OLd(Icons.Filled.Edit, Color(0xFFFFEB3B), "قديم", Color.Black, 0),

        LeMaxPrixArrive(
            Icons.Filled.ArrowUpward,
            Color(0xFFFF9800),
            "فائدة محققة مع الحصول على كثير من الزبناء",
            Color.Black,
            5
        ),

        Historique(
            Icons.Filled.History,
            Color(0xFF2196F3),
            "السعر الأخير - يمكن تحسينه للربح الأكثر",
            Color.White,
            2
        ),

        Prix_SupperGro_Et_PresentationService(
            Icons.Filled.Warning,
            Color(0xFF000000),
            "سعر السوبر جملة - ربح عند الكمية",
            Color.Red,
            1
        ),

        Edited_Pour_Client(
            Icons.Filled.Edit,
            Color(0xFFEEEEEE),
            " سعر انتقالي",
            Color.Black,
            3
        ),

        Prix_Detaille(
            Icons.Filled.Person,
            Color(0xFFCDDC39),
            "سعر التجزئة - ربح جيد",
            Color.Black,
            4
        );

        fun getNextProfitableType(): TypeChoisi? {
            return when (this) {
                Tariff_Achat_Depuit_Grossisst -> Prix_SupperGro_Et_PresentationService
                Prix_SupperGro_Et_PresentationService -> Historique
                Historique -> Edited_Pour_Client
                Edited_Pour_Client -> Prix_Detaille
                Prix_Detaille -> LeMaxPrixArrive
                LeMaxPrixArrive -> null
                DEFIN_OLd -> Historique

                else  -> null
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

    companion object {
        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/DataBase13TarificationInfos"
        )

        fun get_default_P0(
            parentM1ProduitInfos: ArticlesBasesStatsTable,
            start_Prix_Depuit_Ancient: Double,
        ): Pair<M13TarificationInfos, Modifier> {
            val m13TarificationInfos = M13TarificationInfos(
                parent_M1Produit_KeyId = parentM1ProduitInfos.keyID,
                parent_M1Produit_DebugInfos = parentM1ProduitInfos.getDebugInfos(),
                prixCurrency = start_Prix_Depuit_Ancient,
                suggestedUpgrade = TypeChoisi.Historique.getNextProfitableType()
            )
            val modifier = Modifier.getSemanticsTag(
                nomVal = "m13TarificationInfos",
                data = m13TarificationInfos
            )
            return Pair(m13TarificationInfos, modifier)
        }

        fun get_default(): M13TarificationInfos {
            return M13TarificationInfos()
        }


        fun analyzeSalesDistribution(
            groupedSales: List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>>
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
