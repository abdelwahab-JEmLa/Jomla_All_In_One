package Z_CodePartageEntreApps.Windows.A.B_DataBaseEdite.Windows.A_Produit

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.Z.Archive.A_ProduitAncienModelStructure
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.ref_HeadOfModels
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
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
    private val A_ProduitRepository: A_ProduitRepository,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository,
) : ViewModel() {
    val TAG = "ViewModel_AProto_ProduitDataBase"
    val _2_1_ProduitsDataBase_Repository =_0_0_HeadOfRepositorys_Repository.repositorys_Model._2_1_ProduitsDataBase_Repository
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

                val convertedData = snapshotList.map { produit ->
                    convertAProduitToProduitsDataBase(produit)
                }

                // Update _2_1_ProduitsDataBase_Repository with the converted data
                _2_1_ProduitsDataBase_Repository.modelDatasSnapList.clear()
                _2_1_ProduitsDataBase_Repository.modelDatasSnapList.addAll(convertedData)

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

    private fun convertAProduitToProduitsDataBase(produit: A_Produit): _2_1_ProduitsDataBase {
        return _2_1_ProduitsDataBase(
            vid = produit.id,
            nom = produit.nom,
            emballageCartone = produit.emballageCartone,
            itsTempProduit = produit.itsTempProduit,
            besoinToBeUpdated = produit.besoinToBeUpdated,
            non_Trouve = produit.non_Trouve,
            isVisible = produit.isVisible,
            imageGlidReloadTigger = produit.imageGlidReloadTigger,
            prePourCameraCapture = produit.prePourCameraCapture,
            diponibilityEtate = produit.diponibilityEtate,
            probablementNonDispo = produit.probablementNonDispo,
            enumVarNonDispoPourClients = convertNonDispoPourClients(produit.enumVarNonDispoPourClients),
            parentCategoryId = produit.parentCategoryId,
            indexInParentCategorie = produit.indexInParentCategorie,
            monPrixAchat = produit.monPrixAchat,
            monPrixVent = produit.monPrixVent
        )
    }

    private fun convertNonDispoPourClients(source: A_Produit.NON_DISPO_POUR_CLIENTS): _2_1_ProduitsDataBase.NON_DISPO_POUR_CLIENTS {
        return when (source) {
            A_Produit.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT -> _2_1_ProduitsDataBase.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT
            A_Produit.NON_DISPO_POUR_CLIENTS.TOUT -> _2_1_ProduitsDataBase.NON_DISPO_POUR_CLIENTS.TOUT
            A_Produit.NON_DISPO_POUR_CLIENTS.NEVEAU -> _2_1_ProduitsDataBase.NON_DISPO_POUR_CLIENTS.NEVEAU
            A_Produit.NON_DISPO_POUR_CLIENTS.DEFINIE -> _2_1_ProduitsDataBase.NON_DISPO_POUR_CLIENTS.DEFINIE
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
