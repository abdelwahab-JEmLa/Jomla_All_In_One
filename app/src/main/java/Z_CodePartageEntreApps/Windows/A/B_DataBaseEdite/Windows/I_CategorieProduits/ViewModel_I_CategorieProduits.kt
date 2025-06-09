package Z_CodePartageEntreApps.Windows.A.B_DataBaseEdite.Windows.I_CategorieProduits

import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Model.I_CategorieProduits.Z.Repository.I_CategorieProduitsRepository
import Z_CodePartageEntreApps.Model.Z.Archive.ApreAlleAuSql.I_CategoriesProduitsAncien
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.ref_HeadOfModels
import android.annotation.SuppressLint
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
    @SuppressLint("RestrictedApi")
    private suspend fun getAncienDataBase(): List<I_CategoriesProduitsAncien> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Log.d(TAG, "Starting retrieval from old database at path: ${sonAncienReference.path}")
                val snapshot = sonAncienReference.get().await()

                if (!snapshot.exists()) {
                    Log.e(TAG, "Error: Old database reference doesn't exist at path: ${sonAncienReference.path}")
                    return@withContext emptyList()
                }

                Log.d(TAG, "Successfully fetched data snapshot with ${snapshot.childrenCount} items")

                val result = snapshot.children.mapNotNull {
                    try {
                        val item = it.getValue(I_CategoriesProduitsAncien::class.java)
                        if (item == null) {
                            Log.w(TAG, "Warning: Failed to parse item with key: ${it.key}")
                        } else {
                            Log.v(TAG, "Successfully parsed item: ${item.id} - ${item.infosDeBase.nom}")
                        }
                        item
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing item with key ${it.key}: ${e.message}", e)
                        null
                    }
                }

                Log.d(TAG, "Successfully parsed ${result.size}/${snapshot.childrenCount} items from old database")
                result
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving ancien produits: ${e.message}", e)
                Log.e(TAG, "Database path attempted: ${sonAncienReference.path}")
                Log.e(TAG, "Stack trace:", e)
                emptyList()
            }
        }

    /**
     * Migrates data from the old database structure to the new one
     */
    fun populateModelearSonAncien() {
        Log.d(TAG, "Starting migration from old database structure to new one")
        viewModelScope.launch {
            try {
                _migrationProgress.value = 0.1f
                Log.d(TAG, "Migration progress: 10% - Starting data retrieval")

                // Get data from old database structure
                val ancienDataList = getAncienDataBase()

                if (ancienDataList.isEmpty()) {
                    Log.d(TAG, "No ancien data found to migrate. Either database is empty or there was an error retrieving data")
                    _migrationProgress.value = 0f
                    return@launch
                }

                Log.d(TAG, "Migration progress: 30% - Retrieved ${ancienDataList.size} items from old database")
                _migrationProgress.value = 0.3f

                // Transform old data structure to new I_CategorieProduits structure
                Log.d(TAG, "Starting data transformation from old to new structure")
                val newDataList = ancienDataList.mapIndexed { index, ancienData ->
                    try {
                        val newItem = I_CategorieProduits(
                            id = ancienData.id,
                            nom = ancienData.infosDeBase.nom,
                            groupeParentId = ancienData.infosDeBase.groupeParentId,
                            indexDonsParentList = ancienData.statuesMutable.indexDonsParentList,
                            afficheSonHeader = ancienData.statuesMutable.afficheSonHeader
                        )
                        Log.v(TAG, "Transformed item ${index+1}/${ancienDataList.size}: ${newItem.id} - ${newItem.nom}")
                        newItem
                    } catch (e: Exception) {
                        Log.e(TAG, "Error transforming item ${ancienData.id}: ${e.message}", e)
                        // Return a default item to maintain consistency
                        I_CategorieProduits(
                            id = ancienData.id,
                            nom = "Error: ${e.message}",
                            groupeParentId = 0,
                            indexDonsParentList = 0,
                            afficheSonHeader = false
                        )
                    }
                }

                Log.d(TAG, "Migration progress: 60% - Transformed ${newDataList.size} items to new structure")
                _migrationProgress.value = 0.6f

                // Create a SnapshotStateList from the converted data
                val snapshotList = SnapshotStateList<I_CategorieProduits>()
                snapshotList.addAll(newDataList)
                Log.d(TAG, "Created SnapshotStateList with ${snapshotList.size} items")

                // Update repository with new data structure
                Log.d(TAG, "Starting repository upsertLenceCommandeRepoGroupedProtoAvantJuin3 with transformed data")
                try {
                    I_CategorieProduitsRepository.updateMultiDatas(snapshotList)
                    Log.d(TAG, "Repository upsertLenceCommandeRepoGroupedProtoAvantJuin3 successful")
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating repository: ${e.message}", e)
                    throw e
                }

                _migrationProgress.value = 1.0f
                Log.d(TAG, "Migration progress: 100% - Migration complete")
                Log.d(TAG, "Successfully migrated ${newDataList.size} category products from ancien database")
            } catch (e: Exception) {
                Log.e(TAG, "Error migrating ancien database: ${e.message}", e)
                Log.e(TAG, "Stack trace:", e)
                _migrationProgress.value = 0f
            }
        }
    }

    /**
     * Updates a single product category
     */
    fun updateData(categorieProduit: I_CategorieProduits) {
        Log.d(TAG, "Updating single product category: ${categorieProduit.id} - ${categorieProduit.nom}")
        I_CategorieProduitsRepository.updateUnSeulData(categorieProduit)
    }

    /**
     * Updates multiple product categories at once
     */
    suspend fun updateMultiDatas(data: SnapshotStateList<I_CategorieProduits>) {
        Log.d(TAG, "Updating multiple product categories: ${data.size} items")
        I_CategorieProduitsRepository.updateMultiDatas(data)
    }
}
