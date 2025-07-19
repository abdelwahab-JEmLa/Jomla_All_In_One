package V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt.Companion.getPushFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.DataBaseCreationFactory13TarificationInfos
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.History
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

    val typeChoisi: TypeChoisi = TypeChoisi.Historique,
    val prixCurrency: Double = 0.0,

    //---------------------------------ForgingIDsParent.M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parent_M1Produit_KeyId: String = "null",
    val parent_M1Produit_DebugInfos: String = "null",
    //---------------------------------M8BonVent----------------------------------------------------------------------------------------------------------------------------------
    var parent_M8BonVent_KeyId: String = "null",
    val parent_M8BonVent_DebugInfos: String = "null",

    //---------------------------------M2Client----------------------------------------------------------------------------------------------------------------------------------
    var parent_M2Client_KeyId: String = "null",
    val parent_M2Client_DebugInfos: String = "null",

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------
) {
    fun getDebugInfos(): String {
        return "$parent_M1Produit_DebugInfos $typeChoisi"
    }

    enum class TypeChoisi(
        val iconVector: ImageVector? = null,
        val couleur: Color = Color.White,
        val nomArabe: String = "",
        val couleur_Text: Color = Color.White,
    ) {
        LeMaxPrixArrive(
            Icons.Filled.ArrowUpward,
            Color(0xFFFF9800),
            "فائدة محققة مع لاضا كثير من الزيناء"
        ),
        DefiniParGerant(Icons.Filled.ArrowUpward, Color(0xFFFFEB3B), "محدد من المحل",Color.Black),
        Historique(Icons.Filled.History, Color(0xFF9C27B0), "السعر الذي وصلنا له"),
        PRIX_BASE(Icons.Filled.EditOff, Color(0xFFF44336), "الفايدة ابتداءا تكاد تكون معدومة "),
        Tariff_Achat_Depuit_Grossisst(
            Icons.Filled.History,
            Color(0xFF000000),
            "سعر الشراء",
            Color(0xFF2196F3)
        ),

        DEFINI(Icons.Filled.Edit, Color(0xFFFFEB3B), "fd "),
    }

    fun withProperDefaults(): M13TarificationInfos {
        return this
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
                prixCurrency = start_Prix_Depuit_Ancient
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

        fun findTariff(
            datasValue: List<M13TarificationInfos>,
            produit: ArticlesBasesStatsTable,
            typeChoisi: TypeChoisi = TypeChoisi.DefiniParGerant
        ) = datasValue
            .lastOrNull { tariff ->
                val match =
                    tariff.typeChoisi == typeChoisi &&
                            tariff.parent_M1Produit_KeyId == produit.keyID
                match
            }
    }
}
