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
    var _0_0_HeadOfRepositorys_RepositoryProgress: Float = 0f,
    var errorMessage: String? = null,
    var isDataLoading: Boolean = true,
)

class ViewModelFragment_APP2_ID_3(
    private val _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository,
) : ViewModel() {
    private val TAG = "ViewModelFragment_APP2_ID_3"

    private val _uiStateFlow = MutableStateFlow(UiState_APP2_ID_3())
    val uiStateFlow: StateFlow<UiState_APP2_ID_3> = _uiStateFlow.asStateFlow()

    val repositorys_Model = _0_0_HeadOfRepositorys_Repository.repositorys_Model

    val _1_1_CouleurAcheteOperation =
        repositorys_Model._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList

    init {
        // Initialize with progress from repository
        updateProgress(_0_0_HeadOfRepositorys_Repository.progressRepo.value)
    }

    /**
     * Updates the UI state progress
     */
    private fun updateProgress(progress: Float) {
        _uiStateFlow.value = _uiStateFlow.value.copy(
            _0_0_HeadOfRepositorys_RepositoryProgress = progress,
            isDataLoading = progress < 1.0f
        )
    }

    /**
     * Groups purchase operations by color ID and adds them to _4_CouleurOperationCommand
     */
    /**
     * Groups purchase operations by color ID and product ID, then adds them to _4_CouleurOperationCommand
     */
    fun groupeAchatsParIdCouleurEtAddAu_4_CouleurOperationCommand() {
        // Set loading state
        _uiStateFlow.value = _uiStateFlow.value.copy(isDataLoading = true)

        // Also clear the UI state list
        _uiStateFlow.value._4_CouleurOperationCommand.clear()

        // Group _1_1_CouleurAcheteOperation by both couleurIndex_ParentVID and parentProduitAchateOperationVID
        val groupedByColorAndProduct = _1_1_CouleurAcheteOperation
            .groupBy { Pair(it.couleurIndex_ParentVID, it.parentProduitAchateOperationVID) }

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
