package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._4_CouleurOperationCommand._4_CouleurOperationCommand
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UiState_APP2_ID_3(
    var _4_CouleurOperationCommand: SnapshotStateList<_4_CouleurOperationCommand> = mutableStateListOf(),
    var errorMessage: String? = null,
    var isDataLoading: Boolean = true,
)

class ViewModelFragment_APP2_ID_3(
    val _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository,
) : ViewModel() {
    private val TAG = "ViewModelFragment_APP2_ID_3"

    private val _uiStateFlow = MutableStateFlow(UiState_APP2_ID_3())
    val uiStateFlow: StateFlow<UiState_APP2_ID_3> = _uiStateFlow.asStateFlow()

    val repositorys_Model = _0_0_HeadOfRepositorys_Repository.repositorys_Model

    val _1_1_CouleurAcheteOperation =
        repositorys_Model._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList

    val _1_2_ProduitAcheteOperation =
        repositorys_Model._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList

    val _2_1_ProduitsDataBase =
        repositorys_Model._2_1_ProduitsDataBase_Repository.modelDatasSnapList

    fun groupeAchatsParIdCouleurEtAddAu_4_CouleurOperationCommand() {
        _uiStateFlow.value = _uiStateFlow.value.copy(isDataLoading = true)

        // Clear the UI state list
        _0_0_HeadOfRepositorys_Repository.repositorys_Model._4_CouleurOperationCommand_Repository.deleteAllEtRestartSequenceces()
        _uiStateFlow.value._4_CouleurOperationCommand.clear()

        // Check if there's any data to process
        if (_1_1_CouleurAcheteOperation.isEmpty()) {
            _uiStateFlow.value = _uiStateFlow.value.copy(isDataLoading = false)
            return
        }

        // Create a product ID mapping from _2_1_ProduitsDataBase for efficient lookups
        val produitIdMap = _2_1_ProduitsDataBase.associateBy { it.vid }

        // Create a mapping from _1_2_ProduitAcheteOperation to _2_1_ProduitsDataBase product IDs
        val produitAcheteToRealProductMap = _1_2_ProduitAcheteOperation.associateBy(
            { it.vid },
            { it.produitAcheterID }
        )

        // Group _1_1_CouleurAcheteOperation by both couleurIndex_ParentVID and produitVID
        val groupedByColorAndProduct = _1_1_CouleurAcheteOperation.groupBy { operation ->
            // Find the corresponding product in _1_2_ProduitAcheteOperation
            val parentProduitAchateOperationVID = operation.parentProduitAchateOperationVID

            // Get the actual product ID from the produitAcheteToRealProductMap
            val realProductID = if (parentProduitAchateOperationVID != null) {
                produitAcheteToRealProductMap[parentProduitAchateOperationVID]
            } else {
                null
            }

            // Find the corresponding product in _2_1_ProduitsDataBase
            val validProduitVID = if (realProductID != null && produitIdMap.containsKey(realProductID)) {
                realProductID
            } else {
                parentProduitAchateOperationVID
            }

            // Create the grouping key pair
            Pair(operation.couleurIndex_ParentVID, validProduitVID)
        }

        // Prepare a list to hold all the commands
        val allCommands = mutableListOf<_4_CouleurOperationCommand>()

        // For each group, create a _4_CouleurOperationCommand
        groupedByColorAndProduct.forEach { (keyPair, operations) ->
            val (couleurIndex, produitVID) = keyPair

            // Calculate the total quantity for this color-product combination
            val totalQuantity = operations.sumOf { it.totaleQuantity }

            // Create the _4_CouleurOperationCommand with the grouped data
            val couleurOperationCommand = _4_CouleurOperationCommand(
                couleurIndex_ParentVID = couleurIndex,
                produitVID_ParentKey = produitVID,
                totaleQuantity = totalQuantity
            )

            // Add to our collection
            allCommands.add(couleurOperationCommand)
        }

        // Only proceed if we have commands to add
        if (allCommands.isNotEmpty()) {
            // Add all commands at once
            repositorys_Model._4_CouleurOperationCommand_Repository.addMultiDATAsEtReturnVIDsList(
                allCommands
            ) { vidsList ->
                // Update the VIDs in our objects
                allCommands.forEachIndexed { index, command ->
                    if (index < vidsList.size) {
                        command.vid = vidsList[index]
                    }
                }

                // Add all to the UI state list
                _uiStateFlow.value = _uiStateFlow.value.copy(
                    _4_CouleurOperationCommand = mutableStateListOf<_4_CouleurOperationCommand>().apply {
                        addAll(allCommands)
                    }
                )
            }
        }

        // Update loading state once processing is complete
        _uiStateFlow.value = _uiStateFlow.value.copy(isDataLoading = false)
    }
}
