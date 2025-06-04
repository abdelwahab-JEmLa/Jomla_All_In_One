package Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package

import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Proto.B.Par.App.A.AchatsManager.App._1.Shared.Views.LoadingContent
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.UI.BottonsActions
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.UI.SearchField_A4F1
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.UI.handleCategoryClick_F1
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel.ViewModel_A4FragID1
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.Windows.A_OptionsControlsButtons_App4FragId_4
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_MainScreen_SectionID4_FragmentID1(
    viewModel: ViewModel_A4FragID1 = koinViewModel()
) {
    val i_Categories = viewModel.i_CategoriesProduits.sortedBy { it.indexDonsParentList }

    var multiSelectionMode by remember { mutableStateOf(false) }
    var renameOrFusionMode by remember { mutableStateOf(false) }
    var selectedCategories by remember { mutableStateOf<List<I_CategorieProduits>>(emptyList()) }
    var movingCategory by remember { mutableStateOf<I_CategorieProduits?>(null) }
    var heldCategory by remember { mutableStateOf<I_CategorieProduits?>(null) }
    var filterText by remember { mutableStateOf("") }
    var reorderMode by remember { mutableStateOf(false) }

    val onMultiSelectionModeChange: (Boolean) -> Unit = { newMode ->
        multiSelectionMode = newMode
        if (!newMode) {
            selectedCategories = emptyList()
            movingCategory = null
            renameOrFusionMode = false
            heldCategory = null
            reorderMode = false
        }
    }

    val onRenameOrFusionModeChange: (Boolean) -> Unit = { newMode ->
        renameOrFusionMode = newMode
        if (!newMode) {
            heldCategory = null
            multiSelectionMode = false
            selectedCategories = emptyList()
            movingCategory = null
            reorderMode = false
        }
    }


    val onReorderModeActivate: () -> Unit = {
        reorderMode = true
    }

    val onCancelMove: () -> Unit = {
        movingCategory = null
        heldCategory = null
        renameOrFusionMode = false
        reorderMode = false
    }
    val loadingProgress by viewModel.a_ProduitModelRepository.progressRepo.collectAsState()

    // Removed Dialog wrapper - now a normal Fragment
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = MaterialTheme.shapes.large
    ) {
        if (loadingProgress < 1f) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                LoadingContent(
                    message = "Chargement des données..."
                )
            }
        } else {
            Box() {
                Column(modifier = Modifier.padding(4.dp)) {
                    SearchField_A4F1(
                        filterText = filterText,
                        onFilterTextChange = { filterText = it }
                    )

                    val filteredCategories = remember(i_Categories, filterText) {
                        if (filterText.isBlank()) {
                            i_Categories
                        } else {
                            i_Categories.filter {
                                it.nom.contains(
                                    filterText,
                                    ignoreCase = true
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        B_MainList_A4FragID_1(
                            viewModel = viewModel,
                            categories = filteredCategories,
                            selectedCategories = selectedCategories,
                            movingCategory = movingCategory,
                            heldCategory = heldCategory,
                            reorderMode = reorderMode,
                            onCategoryClick = { category ->
                                handleCategoryClick_F1(
                                    category = category,
                                    filterText = filterText,
                                    viewModel = viewModel,
                                    renameOrFusionMode = renameOrFusionMode,
                                    multiSelectionMode = multiSelectionMode,
                                    reorderMode = reorderMode,
                                    heldCategory = heldCategory,
                                    selectedCategories = selectedCategories,
                                    movingCategory = movingCategory,
                                    onHeldCategoryChange = { heldCategory = it },
                                    onSelectedCategoriesChange = { selectedCategories = it },
                                    onRenameOrFusionModeChange = { renameOrFusionMode = it },
                                    onMovingCategoryChange = { movingCategory = it },
                                    onReorderModeChange = { reorderMode = it },
                                    onCategorySelected = {},
                                    onDismiss = {}
                                )
                            }
                        )
                    }

                    BottonsActions(
                        viewModel = viewModel,
                        multiSelectionMode = multiSelectionMode,
                        renameOrFusionMode = renameOrFusionMode,
                        selectedCategories = selectedCategories,
                        movingCategory = movingCategory,
                        reorderMode = reorderMode,
                        onMultiSelectionModeChange = onMultiSelectionModeChange,
                        onRenameOrFusionModeChange = onRenameOrFusionModeChange,
                        onReorderModeActivate = onReorderModeActivate,
                        onCancelMove = onCancelMove
                    )
                }
            }
        }
        A_OptionsControlsButtons_App4FragId_4(viewModel)
    }
}
