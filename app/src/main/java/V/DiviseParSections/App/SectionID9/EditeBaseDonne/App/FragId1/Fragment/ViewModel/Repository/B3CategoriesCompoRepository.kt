package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update.addOrUpdateDatas
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class B3CategoriesCompoRepository(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3
) {
    val parentRepo = a_MasterRepositorysGrpProtoJuin3.repoC_CategorieProduitInfos
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<CategoriesTabelle>>(emptyList())
    val datasState: State<List<CategoriesTabelle>> = _datas
    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    model.repoStateC_CategorieProduitInfos?.modelListFlow
                    _datas.value =
                        model.repoStateC_CategorieProduitInfos?.modelListFlow ?: emptyList()
                }
            }
        }
    }

    fun addOrUpdateData(data: CategoriesTabelle) {
        data.let { dataSansProper ->
            val newData = dataSansProper.withDernierTimeTampsSynchronisationAvecFireBase()

            // Log tracking for categories selected for displacement
            logCategorySelectionForDisplacementIfNeeded(newData)

            _datas.value = _datas.value.map {
                if (it.id == newData.id)
                    newData
                else it
            }.let { list ->
                if (list.none { it.id == newData.id }) list + newData else list
            }
            updateSonRepositoryProtoJuin3(newData)
        }
    }

    fun updateSonRepositoryProtoJuin3(newData: CategoriesTabelle) { parentRepo.addOrUpdateData(newData) }

    fun addOrUpdateDatas(datas: List<CategoriesTabelle>) {
        val processedDatas = datas.map { it.withDernierTimeTampsSynchronisationAvecFireBase() }

        // Log tracking for categories selected for displacement in batch operations
        logCategoriesSelectionForDisplacementIfNeeded(processedDatas)

        val currentList = _datas.value.toMutableList()
        processedDatas.forEach { newData ->
            val existingIndex = currentList.indexOfFirst { it.id == newData.id }
            if (existingIndex >= 0) {
                currentList[existingIndex] = newData
            } else {
                currentList.add(newData)
            }
        }
        _datas.value = currentList

        updateDatasDonSonRepositoryProtoJuin3(processedDatas)
    }

    fun updateDatasDonSonRepositoryProtoJuin3(newDatas: List<CategoriesTabelle>) {
        parentRepo.addOrUpdateDatas(newDatas)
    }

    /**
     * Logs when a category with cSelectionePourDeplace = true is processed
     */
    private fun logCategorySelectionForDisplacementIfNeeded(category: CategoriesTabelle) {
        if (category.cSelectionePourDeplace) {
            Log.d(TAG, "Category selected for displacement processed: " +
                    "ID=${category.id}, Name='${category.nom}', " +
                    "CatalogueParentId=${category.catalogueParentId}, " +
                    "Position=${category.position}, " +
                    "Timestamp=${category.dernierTimeTampsSynchronisationAvecFireBase}")
        }
    }

    /**
     * Logs when categories with cSelectionePourDeplace = true are processed in batch
     */
     fun logCategoriesSelectionForDisplacementIfNeeded(
        categories: List<CategoriesTabelle>,
        cLenceDepuitViemModel:Boolean=false
    ) {
        val selectedCategories = categories.filter { it.cSelectionePourDeplace }
        if (selectedCategories.isNotEmpty()) {
            val cLenceDepuitViemModelTag=if(cLenceDepuitViemModel)"cLenceDepuitViemModel"  else ""
            Log.d(TAG, "$cLenceDepuitViemModelTag Batch operation: ${selectedCategories.size} categories selected for displacement processed")
            selectedCategories.forEach { category ->
                Log.d(TAG, "  - Category: ID=${category.id}, Name='${category.nom}', " +
                        "CatalogueParentId=${category.catalogueParentId}, Position=${category.position}")
            }
        }
    }

    companion object {
        private const val TAG = "B3CategoriesCompoRepository"
    }
}

@Entity
data class CategoriesTabelle(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),

    //Parent Forging Ids
    val catalogueParentId: Long = 0,

    var nom: String = "",

    var position: Int = 0,

    var displayedHeader: Boolean = false,

    val itsHeldPourDeplacement: Boolean = false,

    // Section Etates Mutable

    // Section Centralization Valeurs Pour Injection a TOu modules
    var cSelectionePourDeplace: Boolean = false,

    // Section dernierFireBaseUpdateTimestamps
    var dernierTimeTampsSynchronisationAvecFireBase: Long = DatesHandler().getCurrentTimestamps(),
) {
    fun withDernierTimeTampsSynchronisationAvecFireBase(): CategoriesTabelle {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        val caRef =
            Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/C_CategorieProduitInfos")
    }
}
