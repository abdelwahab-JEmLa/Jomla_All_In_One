package V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository

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
        val dataUpdate = data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

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

    //Forging IDs

    //---------------------------------Parent M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parentM1ProduitInfosKeyId: String = "null",
    val parentM1ProduitDebugInfos:String="null",
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------

    val idParentProduit: Long = 0L,
    val typeChoisi: TypeChoisi =
        TypeChoisi.DefiniParGerant2,

    val parentIdClient: Long = 0L,

    //Base Infos
    val prixCurrency: Double = 0.0,
    val timestamps: Long = System.currentTimeMillis(),
    val nom: String = "",

    //Etates Mutable
    val needUpdate: Boolean = true,

) {
    fun getDebugInfos(): String {
        return "$parentM1ProduitDebugInfos $typeChoisi"
    }

    enum class TypeChoisi(
        val iconVector: ImageVector? = null,
        val couleur: Color = Color.White,
        val nomArabe: String ="",
    ) {
        LeMaxPrixArrive(Icons.Filled.ArrowUpward, Color(0xFFFF9800),"فائدة محققة مع لاضا كثير من الزيناء"),
        DefiniParGerant2(Icons.Filled.ArrowUpward, Color(0xFFFFEB3B),"محدد من عمي علي"),
        DEFINI(Icons.Filled.Edit, Color(0xFFFFEB3B),"المحدد من المدير بنصرف "),
        Historique(Icons.Filled.History, Color(0xFF2196F3),"السعر الذي وصلنا له"),
        PRIX_BASE(Icons.Filled.EditOff, Color(0xFFF44336),"الفايدة ابتداءا تكاد تكون معدومة ")
    }

    fun withProperDefaults(): M13TarificationInfos {
        return this
    }

    companion object{
        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/DataBase13TarificationInfos"
        )
        fun findTariff(
            datasValue: List<M13TarificationInfos>,
            produit: ArticlesBasesStatsTable,
            typeChoisi: TypeChoisi =TypeChoisi.DefiniParGerant2
        ) = datasValue
            .lastOrNull { tariff ->
                val match =
                    tariff.typeChoisi == typeChoisi &&
                            tariff.parentM1ProduitInfosKeyId == produit.keyID
                match
            }

    }
}
