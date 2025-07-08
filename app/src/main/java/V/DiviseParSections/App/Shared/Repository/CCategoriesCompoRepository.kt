package V.DiviseParSections.App.Shared.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.Get.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt.Companion.ref
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update.addOrUpdateDatas
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update.deleteAddMultiDatas
import android.util.Log
import androidx.compose.runtime.Stable
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
class CCategoriesCompoRepository(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3
) {
    val TAG = "CCategoriesCompoRepository"
    val parentRepo = a_MasterRepositorysGrpProtoJuin3.repoC_CategorieProduitInfos
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<CategoriesTabelle>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val tigerDataRecompose by derivedStateOf { _datas.value.map { it.dernierTimeTampsSynchronisationAvecFireBase } }

    init {
        composScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    model.repoStateC_CategorieProduitInfos?.modelListFlow
                    val newDataList =
                        model.repoStateC_CategorieProduitInfos?.modelListFlow ?: emptyList()
                    _datas.value = newDataList
                }
            }
        }
    }

    fun addOrUpdateData(data: CategoriesTabelle) {
        data.let { dataSansProper ->
            val newData = dataSansProper.withDernierTimeTampsSynchronisationAvecFireBase()

            val updatedList = _datas.value.map {
                if (it.id == newData.id)
                    newData
                else it
            }
            CategoriesTabelle.logCategory(newData, TAG)
            _datas.value = updatedList

            composScope.launch {
                updateSonRepositoryProtoJuin3(newData)
            }
        }
    }

    fun updateSonRepositoryProtoJuin3(newData: CategoriesTabelle) {
        parentRepo.addOrUpdateData(newData)
    }

    fun addOrUpdateDatas(datas: List<CategoriesTabelle>) {
        val processedDatas = datas.map { it.withDernierTimeTampsSynchronisationAvecFireBase() }

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

        composScope.launch {
            updateDatasDonSonRepositoryProtoJuin3(processedDatas)
        }
    }

    fun updateDatasDonSonRepositoryProtoJuin3(newDatas: List<CategoriesTabelle>) {
        parentRepo.addOrUpdateDatas(newDatas)
    }
    fun deleteAddMultiDatas(newDatas: List<CategoriesTabelle>) {
        parentRepo.deleteAddMultiDatas(newDatas)
    }
}

@Entity
data class CategoriesTabelle(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    var bsonObjectId: String = getPushFireBase(ref),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    val catalogueParentId: Long = 0,
    val parentCatalogueIdObject: String = "",

    var nom: String = "",

    var position: Int = 0,

    var displayedHeader: Boolean = false,

    val itsHeldPourDeplacement: Boolean = false,

    var cSelectionePourDeplace: Boolean = false,
) {
    fun withDernierTimeTampsSynchronisationAvecFireBase(): CategoriesTabelle {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        val caRef =
            Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/C_CategorieProduitInfos")
        fun safeRemoveRef(): Unit {
            caRef.removeValue()
        }
        fun logCategory(category: CategoriesTabelle, TAG: String) {
            Log.d(
                TAG, "Category selected for displacement processed: " +
                        "ID=${category.id}, Name='${category.nom}', " +
                        "CatalogueParentId=${category.catalogueParentId}, " +
                        "Position=${category.position}, " +
                        "${category.cSelectionePourDeplace}, " +
                        "Timestamp=${category.dernierTimeTampsSynchronisationAvecFireBase}"
            )
        }
    }
}
