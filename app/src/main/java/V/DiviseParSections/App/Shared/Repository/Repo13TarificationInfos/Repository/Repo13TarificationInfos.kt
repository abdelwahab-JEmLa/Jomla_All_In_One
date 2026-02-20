package V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt.Companion.getPushFireBase
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
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
    val repoScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M13TarificationInfos>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }


    init {
        repoScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { newData ->
                _datas.value = newData
                // Clean up duplicates after data is loaded
                if (newData.isNotEmpty() && M18CentralParametresOfAllApps().au_Lence_Diminue_DatasFB) {
                    cleanupDuplicateTariffs(this@Repo13TarificationInfos, newData)
                }

                M18CentralParametresOfAllApps().time_tamp_all_tariffs.ifTrue {
                    updateTariffsWithZeroTimestamps(newData)
                }
            }
        }
    }

    private fun updateTariffsWithZeroTimestamps(tariffs: List<M13TarificationInfos>) {
        repoScope.launch {
            try {
                val currentTimestamp = System.currentTimeMillis()

                // Filter tariffs that need timestamp updates
                val tariffsToUpdate = tariffs.filter { it.creationTimestamps == 0L }

                if (tariffsToUpdate.isNotEmpty()) {
                    // Update each tariff with current timestamp
                    tariffsToUpdate.forEach { tariff ->
                        val updatedTariff = tariff.copy(
                            creationTimestamps = currentTimestamp,
                            dernierTimeTampsSynchronisationAvecFireBase = currentTimestamp
                        )

                        // Save to database and Firebase
                        dataBaseCreationFactory.set(updatedTariff)
                    }

                    // Update local state
                    withContext(Dispatchers.Main.immediate) {
                        _datas.value = _datas.value.map { tariff ->
                            if (tariff.creationTimestamps == 0L) {
                                tariff.copy(
                                    creationTimestamps = currentTimestamp,
                                    dernierTimeTampsSynchronisationAvecFireBase = currentTimestamp
                                )
                            } else {
                                tariff
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Log error if needed
            }
        }
    }
    fun upsert(data: M13TarificationInfos) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        val existingIndex = datasValue.indexOfFirst { it.keyID == dataUpdate.keyID }

        repoScope.launch {
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

    fun refresh_Datas() {
        repoScope.launch {
            try {
                dataBaseCreationFactory.dao.deleteAll()

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = emptyList()
                }

                val freshDataFromFirebase = dataBaseCreationFactory.fetchDataFromFirebase()

                dataBaseCreationFactory.dao.insertAll(freshDataFromFirebase)

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = freshDataFromFirebase
                }

            } catch (e: Exception) {
            }
        }
    }


    fun add(data: M13TarificationInfos) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        repoScope.launch {
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
        repoScope.launch {
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
    var creationTimestamps: Long =  System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var defaultNonSaved_Entre: Boolean = true,

    var its_From_CalculeParNewBenifice: Boolean = true,

    var laisse_Au_Gerant: Boolean = false,

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
            Color.Black,
            0,
            "قديم",
            its_gro_app = false
        ),

        LeMaxPrixArrive(
            Icons.Filled.ArrowUpward,
            Color(0xFFFF9800),
            "اعلى سعر وصل له",
            Color.Black,
            5,
            "أعلى",
            its_gro_app = false ,
            ignore_affiche=true
        ),

        Historique(
            Icons.Filled.History,
            Color(0xFF9C27B0),
            "السعر الأخير",
            Color.White,
            5,
            "أخير",
            its_gro_app = false ,
            ignore_affiche = true  // Only shown in grossist app
        ),

        Prix_SupperGro_Et_PresentationService(
            Icons.Filled.Warning,
            Color(0xFFF44336),
            "عرض + السوبر جملة",
            Color.White,
            2,
            "عرض",
            its_gro_app = false
        ),

        Edited_Pour_Client(
            Icons.Filled.Edit,
            Color(0xFFFFEB3B),
            "سعر انتقالي",
            Color.Black,
            3,
            "انتقالي",
            its_gro_app = false
        ),

        Prix_Progressive_Editable(
            Icons.Filled.Edit,
            Color(0xFFFFEB3B),
            "سعر انتقالي",
            Color.Black,
            3,
            "انتقالي",
            its_gro_app = false,
            ignore_affiche = true
        ),

        Prix_Detaille(
            Icons.Filled.Person,
            Color(0xFF1A711F),
            "سعر التجزئة",
            Color.White,
            4,
            "تجزئة",
            its_gro_app = false
        );

        fun getNextProfitableType(): TypeChoisi? {
            return when (this) {
                // Grossist app progression
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

    companion object {
        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/DataBase13TarificationInfos"
        )

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
            val modifier = Modifier.getSemanticsTag(
                nomVal = "m13TarificationInfos",
                data = m13TarificationInfos
            )
            return Pair(m13TarificationInfos, modifier)
        }

        fun get_default(): M13TarificationInfos {
            return M13TarificationInfos()
        }

        /**
         * Calculates the Edited_Pour_Client price by interpolating between
         * Prix_SupperGro and Prix_Detaille using pourcentage_Prix_Progressive.
         *
         *   result = SupperGro + (Detaille - SupperGro) * (pourcentage / 100)
         *
         * pourcentage == 0   → full SupperGro price
         * pourcentage == 100 → full Detaille price
         * pourcentage == 60  → 60 % of the way from SupperGro to Detaille (default)
         *
         * Falls back gracefully when only one price is available:
         *   - Only SupperGro  → uses SupperGro directly (no interpolation)
         *   - Only Detaille   → uses Detaille directly (no interpolation)
         *   - Both missing    → returns null
         */
        fun remembered_calculated_progressive_changement_tariff(
            relative_Prix_Detaille: Double?,
            relative_Prix_SupperGro_Et_PresentationService: Double?,
            relative_produit: M01Produit,
        ): M13TarificationInfos? {
            // Need at least one price to produce anything
            if (relative_Prix_SupperGro_Et_PresentationService == null && relative_Prix_Detaille == null) {
                return null
            }

            val calculatedPrice = when {
                // Both available → interpolate with pourcentage_Prix_Progressive
                relative_Prix_SupperGro_Et_PresentationService != null && relative_Prix_Detaille != null -> {
                    val weight = relative_produit.pourcentage_Prix_Progressive.coerceIn(0, 100) / 100.0
                    relative_Prix_SupperGro_Et_PresentationService +
                            (relative_Prix_Detaille - relative_Prix_SupperGro_Et_PresentationService) * weight
                }
                // Only SupperGro → use it directly
                relative_Prix_SupperGro_Et_PresentationService != null -> relative_Prix_SupperGro_Et_PresentationService
                // Only Detaille → use it directly
                else -> relative_Prix_Detaille!!
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
