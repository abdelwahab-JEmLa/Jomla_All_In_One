package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows.I_CategorieProduits

import Z_CodePartageEntreApps.Model.I_CategorieProduits.A.Repository.I_CategorieProduitsRepository
import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Model.I_CategoriesProduits
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.ref_HeadOfModels
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewModel_I_CategorieProduits(
    private val I_CategorieProduitsRepository: I_CategorieProduitsRepository
) : ViewModel() {
    val TAG = "ViewModel_I_CategorieProduits"

    // Progress tracking
    private val _migrationProgress = MutableStateFlow(0f)
    val migrationProgress = _migrationProgress.asStateFlow()

    // References to the old database structure
    private val sonAncienReference = ref_HeadOfModels.child("I_CategoriesProduits")

    /**
     * Retrieves data from the old database structure
     */
    private suspend fun getAncienDataBase(): List<I_CategoriesProduits> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                sonAncienReference
                    .get()
                    .await()
                    .children
                    .mapNotNull {
                        it.getValue(I_CategoriesProduits::class.java)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving ancien produits: ${e.message}", e)
                emptyList()
            }
        }

    /**
     * Migrates data from the old database structure to the new one
     */
    fun populateModelearSonAncien() {
        viewModelScope.launch {
            try {
                _migrationProgress.value = 0.1f

                // Get data from old database structure
                val ancienDataList = getAncienDataBase()

                if (ancienDataList.isEmpty()) {
                    Log.d(TAG, "No ancien data found to migrate")
                    _migrationProgress.value = 0f
                    return@launch
                }

                _migrationProgress.value = 0.3f

                // Transform old data structure to new I_CategorieProduits structure
                val newDataList = ancienDataList.map { ancienData ->
                    I_CategorieProduits(
                        id = ancienData.id,
                        nom = ancienData.infosDeBase.nom,
                        groupeParentId = ancienData.infosDeBase.groupeParentId,
                        indexDonsParentList = ancienData.statuesMutable.indexDonsParentList,
                        afficheSonHeader = ancienData.statuesMutable.afficheSonHeader
                    )
                }

                _migrationProgress.value = 0.6f

                // Create a SnapshotStateList from the converted data
                val snapshotList = SnapshotStateList<I_CategorieProduits>()
                snapshotList.addAll(newDataList)

                // Update repository with new data structure
                I_CategorieProduitsRepository.updateMultiDatas(snapshotList)

                _migrationProgress.value = 1.0f

                Log.d(TAG, "Successfully migrated ${newDataList.size} category products from ancien database")
            } catch (e: Exception) {
                Log.e(TAG, "Error migrating ancien database: ${e.message}", e)
                _migrationProgress.value = 0f
            }
        }
    }

    /**
     * Updates a single product category
     */
    fun updateData(categorieProduit: I_CategorieProduits) {
        I_CategorieProduitsRepository.updateUnSeulData(categorieProduit)
    }

    /**
     * Updates multiple product categories at once
     */
    suspend fun updateMultiDatas(data: SnapshotStateList<I_CategorieProduits>) {
        I_CategorieProduitsRepository.updateMultiDatas(data)
    }


}
