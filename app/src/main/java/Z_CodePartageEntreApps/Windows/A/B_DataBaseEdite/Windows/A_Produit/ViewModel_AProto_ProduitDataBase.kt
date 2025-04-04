package Z_CodePartageEntreApps.Windows.A.B_DataBaseEdite.Windows.A_Produit

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.Z.Archive.A_ProduitAncienModelStructure
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.ref_HeadOfModels
import android.annotation.SuppressLint
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewModel_AProto_ProduitDataBase(
    private val A_ProduitRepository: A_ProduitRepository
) : ViewModel() {
    val TAG = "ViewModel_AProto_ProduitDataBase"

    // Progress tracking
    private val _migrationProgress = MutableStateFlow(0f)
    val migrationProgress = _migrationProgress.asStateFlow()

    // References to the old database structure
    private val sonAncienReference = ref_HeadOfModels.child("A_Produit")

    /**
     * Retrieves data from the old database structure
     */
    @SuppressLint("RestrictedApi")
    private suspend fun getAncienDataBase(): List<A_ProduitAncienModelStructure> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val snapshot = sonAncienReference.get().await()

                if (!snapshot.exists()) {
                    return@withContext emptyList()
                }

                val result = snapshot.children.mapNotNull {
                    try {
                        it.getValue(A_ProduitAncienModelStructure::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }

                result
            } catch (e: Exception) {
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
                    _migrationProgress.value = 0f
                    return@launch
                }

                _migrationProgress.value = 0.3f

                // Transform old data structure to new A_Produit structure
                val newDataList = ancienDataList.mapIndexed { index, ancienData ->
                    try {
                        // Update progress during mapping (from 30% to 60%)
                        if (ancienDataList.size > 1) {
                            val progressIncrement = 0.3f / ancienDataList.size
                            _migrationProgress.value = 0.3f + (progressIncrement * index)
                        }

                        val newItem = A_Produit(
                            id = ancienData.id,
                            nom = ancienData.nom,
                            emballageCartone = ancienData.statuesBase.characterProduit.emballageCartone,
                            itsTempProduit = ancienData.itsTempProduit,
                            besoinToBeUpdated = ancienData.besoinToBeUpdated,
                            non_Trouve = ancienData.non_Trouve,
                            isVisible = ancienData.isVisible,
                            imageGlidReloadTigger = ancienData.statuesBase.imageGlidReloadTigger,
                            prePourCameraCapture = ancienData.statuesBase.prePourCameraCapture,
                            diponibilityEtate = ancienData.etatesMutable.diponibilityEtate,
                            probablementNonDispo = ancienData.etatesMutable.porbableNonDispo,
                            enumVarNonDispoPourClients = getDepuitenumVarNonDispoPourClients(ancienData.etatesMutable.nonDispoPourClientsString),
                            parentCategoryId = ancienData.parentCategoryId,
                            indexInParentCategorie = ancienData.indexInParentCategorie,
                            monPrixAchat = ancienData.statuesBase.infosCoutes.monPrixAchat,
                            monPrixVent = ancienData.statuesBase.infosCoutes.monPrixVent
                        )
                        newItem
                    } catch (e: Exception) {
                        // Return a default item to maintain consistency
                        A_Produit(
                            id = ancienData.id,
                            nom = "Error: ${e.message}",
                            parentCategoryId = 0,
                            indexInParentCategorie = 0
                        )
                    }
                }

                _migrationProgress.value = 0.6f

                // Create a SnapshotStateList from the converted data
                val snapshotList = SnapshotStateList<A_Produit>()
                snapshotList.addAll(newDataList)

                // Update repository with new data structure
                try {
                    // Update progress to 80% before database write
                    _migrationProgress.value = 0.8f
                    A_ProduitRepository.updateMultiDatas(snapshotList)
                } catch (e: Exception) {
                    throw e
                }

                _migrationProgress.value = 1.0f
            } catch (e: Exception) {
                _migrationProgress.value = 0f
            }
        }
    }

    private fun getDepuitenumVarNonDispoPourClients(nonDispoPourClientsString: String?): A_Produit.NON_DISPO_POUR_CLIENTS {
        return when (nonDispoPourClientsString) {
            "DISPONIBLE_POUR_TOUT" -> A_Produit.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT
            "TOUT" -> A_Produit.NON_DISPO_POUR_CLIENTS.TOUT
            "NEVEAU" -> A_Produit.NON_DISPO_POUR_CLIENTS.NEVEAU
            "DEFINIE" -> A_Produit.NON_DISPO_POUR_CLIENTS.DEFINIE
            else -> A_Produit.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT // Default value
        }
    }
    fun updateData(categorieProduit: A_Produit) {
        A_ProduitRepository.updateUnSeulData(categorieProduit)
    }

    suspend fun updateMultiDatas(data: SnapshotStateList<A_Produit>) {
        A_ProduitRepository.updateMultiDatas(data)
    }
}
