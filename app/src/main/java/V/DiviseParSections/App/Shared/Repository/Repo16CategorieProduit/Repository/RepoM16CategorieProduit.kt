package V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase16.Factory.DataBaseInitFactory_16CategorieProduit
import android.content.Context
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Stable
class RepoM16CategorieProduit(
    val context: Context,
    val dataBaseCreationFactory: DataBaseInitFactory_16CategorieProduit,
) {
    val TAG = "RepoM16CategorieProduit"
    private val repoScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<CategoriesTabelle>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val tigerDataRecompose by derivedStateOf { _datas.value.map { it.dernierTimeTampsSynchronisationAvecFireBase } }

    init {
        repoScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun deleteAddMultiDatas(
        datas: List<CategoriesTabelle>,
    ) {
        repoScope.launch {
            try {
                val preparedDatas =
                    datas.map { it.withDernierTimeTampsSynchronisationAvecFireBase() }

                // Use the new bulk replace method with transaction
                dataBaseCreationFactory.bulkReplaceAll(preparedDatas)

                // Update UI state after successful database operation
                withContext(Dispatchers.Main.immediate) {
                    _datas.value = preparedDatas
                }

                // Clear Firebase and batch update
                CategoriesTabelle.safeRemoveRef()
                batchFireBaseUpdate(preparedDatas)

                Log.d(TAG, "Successfully replaced all ${preparedDatas.size} categories")
            } catch (e: Exception) {
                Log.e(TAG, "Error in deleteAddMultiDatas: ${e.message}")
            }
        }
    }

    fun reorderCategories(reorderedCategories: List<CategoriesTabelle>) {
        repoScope.launch {
            val processedDatas = reorderedCategories.map {
                it.withDernierTimeTampsSynchronisationAvecFireBase()
            }

            withContext(Dispatchers.Main.immediate) {
                _datas.value = processedDatas
            }
        }
    }


    private suspend fun batchFireBaseUpdate(datas: List<CategoriesTabelle>) {
        try {
            val updates = mutableMapOf<String, Any>()
            datas.forEach { data ->
                updates[data.keyID] = data
            }
            CategoriesTabelle.ref.updateChildren(updates).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error in batchFireBaseUpdate: ${e.message}")
        }
    }

    fun addOrUpdateData(data: CategoriesTabelle) {
        data.let { dataSansProper ->
            val newData = dataSansProper.withDernierTimeTampsSynchronisationAvecFireBase()

            // Find existing index in current list
            val existingIndex = _datas.value.indexOfFirst { it.id == newData.id }

            val updatedList = if (existingIndex >= 0) {
                // Update existing item
                _datas.value.map {
                    if (it.id == newData.id) newData else it
                }
            } else {
                // Add new item
                _datas.value + newData
            }

            CategoriesTabelle.logCategory(newData, TAG)
            _datas.value = updatedList

            repoScope.launch {
                dataBaseCreationFactory.addOrUpdatedAncienRepo(existingIndex, newData)
            }
        }
    }


    fun add_New(data: CategoriesTabelle) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        dataBaseCreationFactory.addOrUpdatedAncienRepo(-1, dataUpdate)
    }

    fun addOrUpdateDatas(
        datas: List<CategoriesTabelle>,
        avec_BatchFireBase: Boolean = false
    ) {
        val processedDatas = datas.map { it.withDernierTimeTampsSynchronisationAvecFireBase() }

        // Update UI state immediately
        _datas.value = processedDatas

        // Handle database updates in background with transaction
        repoScope.launch {
            try {
                dataBaseCreationFactory.dao.transaction {
                    if (datas.size > 10) {
                        // For bulk operations like reordering - use transaction for consistency
                        Log.d(
                            TAG,
                            "Performing bulk update with transaction for ${datas.size} items"
                        )
                        deleteAll()
                        insertAll(processedDatas)
                    } else {
                        // For smaller updates, use upsert which handles insert/update automatically
                        Log.d(TAG, "Performing individual upserts for ${datas.size} items")
                        processedDatas.forEach { processedData ->
                            upsert(processedData)
                        }
                    }
                }

                Log.d(TAG, "Database transaction completed successfully")

                if (avec_BatchFireBase) {
                    batchFireBaseUpdate(processedDatas)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in addOrUpdateDatas transaction: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    fun delete(data: CategoriesTabelle) {
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
data class CategoriesTabelle(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    var bsonObjectId: String = getPushFireBase(Z_AppCompt.ref),
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    val catalogueParentId: Long = 0,
    val parentCatalogueIdObject: String = "",

    var nom: String = "",

    var position: Int = 0,

    var positionDouble: Double = 0.0,

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
        val ref =
            Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/C_CategorieProduitInfos")

        fun safeRemoveRef(): Unit {
            ref.removeValue()
        }

        fun generePushKey() =
            ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")

        fun get_default(
        ): CategoriesTabelle {
            val data = CategoriesTabelle()
            return data
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
