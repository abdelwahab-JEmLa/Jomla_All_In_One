package V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase16.Factory.DataBaseInitFactory_16CategorieProduit
import android.content.Context
import android.util.Log
import android.widget.Toast
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

    fun addOrUpdateDatas(
        datas: List<CategoriesTabelle> ,
        avec_BatchFireBase :Boolean=false
    ) {
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

        repoScope.launch {
            processedDatas.forEach { processedData ->
                val existingIndex = currentList.indexOfFirst { it.id == processedData.id }
                dataBaseCreationFactory.addOrUpdatedAncienRepo(existingIndex, processedData,avec_BatchFireBase)
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

    fun update_If_Exist(data: CategoriesTabelle) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            repoScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Item not found, cannot update", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            return
        }

        val updatedItem = data.copy(
            keyID = datasValue[existingIndex].keyID,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply {
                    this[existingIndex] = updatedItem
                }
            }
        }

        dataBaseCreationFactory.addOrUpdatedAncienRepo(existingIndex, updatedItem)
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

    fun deleteAddMultiDatas(
        datas: List<CategoriesTabelle>,
    ) {
        repoScope.launch {
            try {
                val preparedDatas =
                    datas.map { it.withDernierTimeTampsSynchronisationAvecFireBase() }

                dataBaseCreationFactory.dao.deleteAll()
                dataBaseCreationFactory.dao.insertAll(preparedDatas)

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = preparedDatas
                }

                CategoriesTabelle.safeRemoveRef()

                batchFireBaseUpdate(preparedDatas)
            } catch (e: Exception) {
                Log.e(TAG, "Error in deleteAddMultiDatas: ${e.message}")
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
