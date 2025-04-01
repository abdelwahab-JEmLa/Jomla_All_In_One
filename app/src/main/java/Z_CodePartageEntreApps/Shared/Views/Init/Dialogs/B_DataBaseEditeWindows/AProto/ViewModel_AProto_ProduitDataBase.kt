package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows.AProto

import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.AProto_ProduitDataBase
import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.Z.Repository.AProto_ProduitDataBaseRepository
import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.ref_HeadOfModels
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
    private val AProto_ProduitDataBaseRepository: AProto_ProduitDataBaseRepository
) : ViewModel() {
    val TAG = "ViewModel_AProto_ProduitDataBase"

    // Progress tracking
    private val _migrationProgress = MutableStateFlow(0f)
    val migrationProgress = _migrationProgress.asStateFlow()

    // References to the old database structure
    private val sonAncienReference = ref_HeadOfModels.child("A_ProduitModel")

    /**
     * Retrieves data from the old database structure
     */
    @SuppressLint("RestrictedApi")
    private suspend fun getAncienDataBase(): List<A_ProduitModel> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val snapshot = sonAncienReference.get().await()

                if (!snapshot.exists()) {
                    return@withContext emptyList()
                }

                val result = snapshot.children.mapNotNull {
                    try {
                        it.getValue(A_ProduitModel::class.java)
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

                // Transform old data structure to new AProto_ProduitDataBase structure
                val newDataList = ancienDataList.mapIndexed { index, ancienData ->
                    try {
                        // Update progress during mapping (from 30% to 60%)
                        if (ancienDataList.size > 1) {
                            val progressIncrement = 0.3f / ancienDataList.size
                            _migrationProgress.value = 0.3f + (progressIncrement * index)
                        }

                        val newItem = AProto_ProduitDataBase(
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
                            porbableNonDispo = ancienData.etatesMutable.porbableNonDispo,
                            enumVarNonDispoPourClients = mapNonDispoPourClients(ancienData.etatesMutable.enumVarNonDispoPourClients),
                            parentCategoryId = ancienData.parentCategoryId,
                            indexInParentCategorie = ancienData.indexInParentCategorie,
                            monPrixAchat = ancienData.statuesBase.infosCoutes.monPrixAchat,
                            monPrixVent = ancienData.statuesBase.infosCoutes.monPrixVent
                        )
                        newItem
                    } catch (e: Exception) {
                        // Return a default item to maintain consistency
                        AProto_ProduitDataBase(
                            id = ancienData.id,
                            nom = "Error: ${e.message}",
                            parentCategoryId = 0,
                            indexInParentCategorie = 0
                        )
                    }
                }

                _migrationProgress.value = 0.6f

                // Create a SnapshotStateList from the converted data
                val snapshotList = SnapshotStateList<AProto_ProduitDataBase>()
                snapshotList.addAll(newDataList)

                // Update repository with new data structure
                try {
                    // Update progress to 80% before database write
                    _migrationProgress.value = 0.8f
                    AProto_ProduitDataBaseRepository.updateMultiDatas(snapshotList)
                } catch (e: Exception) {
                    throw e
                }

                _migrationProgress.value = 1.0f
            } catch (e: Exception) {
                _migrationProgress.value = 0f
            }
        }
    }

    /**
     * Maps the old enum to the new enum type
     */
    private fun mapNonDispoPourClients(oldEnum: A_ProduitModel.EtatesMutable.NON_DISPO_POUR_CLIENTS): AProto_ProduitDataBase.NON_DISPO_POUR_CLIENTS {
        return when (oldEnum) {
            A_ProduitModel.EtatesMutable.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT -> AProto_ProduitDataBase.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT
            A_ProduitModel.EtatesMutable.NON_DISPO_POUR_CLIENTS.TOUT -> AProto_ProduitDataBase.NON_DISPO_POUR_CLIENTS.TOUT
            A_ProduitModel.EtatesMutable.NON_DISPO_POUR_CLIENTS.NEVEAU -> AProto_ProduitDataBase.NON_DISPO_POUR_CLIENTS.NEVEAU
            A_ProduitModel.EtatesMutable.NON_DISPO_POUR_CLIENTS.DEFINIE -> AProto_ProduitDataBase.NON_DISPO_POUR_CLIENTS.DEFINIE
        }
    }

    /**
     * Updates a single product category
     */
    fun updateData(categorieProduit: AProto_ProduitDataBase) {
        AProto_ProduitDataBaseRepository.updateUnSeulData(categorieProduit)
    }

    /**
     * Updates multiple product categories at once
     */
    suspend fun updateMultiDatas(data: SnapshotStateList<AProto_ProduitDataBase>) {
        AProto_ProduitDataBaseRepository.updateMultiDatas(data)
    }
}
