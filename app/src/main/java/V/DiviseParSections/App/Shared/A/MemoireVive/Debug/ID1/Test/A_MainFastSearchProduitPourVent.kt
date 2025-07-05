package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.A.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable.ProcessPositioningInFactoryID1
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainFastSearchProduitPourVent(
    viewModel: ViewModelMainFastSearchProduitPourVent = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val bProduitInfosRepository= uiState.bProduitInfosRepository
    val products = bProduitInfosRepository.datasValue
    val categories = viewModel.getter.b3CategoriesCompoRepository.datasValue

    val focusRequester = remember { FocusRequester() }

    // Local state for immediate UI updates
    var localSearchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    // Debounce effect - triggers search after user stops typing
    LaunchedEffect(localSearchText) {
        if (localSearchText.isNotEmpty()) {
            delay(500) // 500ms debounce delay
            // Only update the viewModel's search text after the delay
            viewModel.onSearchTextChange(localSearchText)
        } else {
            // Clear search immediately when text is empty
            viewModel.onSearchTextChange("")
        }
    }

    // Control for initial search
    var shouldPerformInitialSearch by remember { mutableStateOf(true) }

    @Composable
    fun debugTestsPerformInitialSearch(
        enabled: Boolean,
        onSearchQueryChange: (String) -> Unit,
        focusRequester: FocusRequester
    ) {
        LaunchedEffect(enabled) {
            if (enabled) {
                delay(2000) // Wait 2 seconds after component loads
                onSearchQueryChange("sor")
                focusRequester.requestFocus()
                shouldPerformInitialSearch = false // Disable after first execution
            }
        }
    }

    // Use the separated performInitialSearch
    debugTestsPerformInitialSearch(
        enabled = shouldPerformInitialSearch,
        onSearchQueryChange = { searchText ->
            localSearchText = searchText
        },
        focusRequester = focusRequester
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    // Use local state for immediate UI feedback
                    value = localSearchText,
                    onValueChange = { newText ->
                        localSearchText = newText
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = { Text("Rechercher un produit...") },
                    singleLine = true,
                    leadingIcon = {
                        val searchQuery = localSearchText
                        val newProduit = ArticlesBasesStatsTable(
                            id = if (uiState.bProduitInfosRepository.datasValue.isNotEmpty()) {
                                uiState.bProduitInfosRepository.datasValue.maxOf { it.id } + 1
                            } else {
                                1L
                            },
                            nom = searchQuery.ifEmpty { "Err definition" },
                            processPositioningInFactory = ProcessPositioningInFactoryID1.CreeDepuitRechercheRapid
                        )

                        val newCouleurP = M3CouleurProduitInfos(
                            parentBProduitOldID = newProduit.id,
                            parentBProduitInfosKeyID = newProduit.keyID,
                            parentId1ProduitInfosDebugName = newProduit.nom,
                            processPositioningInFactory = M3CouleurProduitInfos.ProcessPositioningInFactory.CreeDepuitRechercheRapid
                        )

                        IconButton(
                            onClick = {
                                // Add new product to repository
                                uiState.bProduitInfosRepository.upsert(newProduit)

                                // Add corresponding color data
                                uiState.b1CouleurOuGoutProduitDataBaseRepository.addOrUpdateData(newCouleurP)

                                // Update current app account if available
                                uiState.zAppComptRepositoryComposable.currentAppCompt?.let { appCompt ->
                                    val updatedAppCompt = appCompt.copy(
                                        onVentM1ProduitInfosKeyID = newProduit.keyID,
                                        onVentM1ProduitInfosDebugName = newProduit.nom
                                    )
                                    uiState.zAppComptRepositoryComposable.upsert(updatedAppCompt)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Créer nouveau produit",
                                modifier = Modifier.semantics(mergeDescendants = true) {
                                    set(SemanticsPropertyKey("DebugID1"), newProduit)
                                    set(SemanticsPropertyKey("DebugID1C2"), newCouleurP)
                                }
                            )
                        }
                    },
                    trailingIcon = {
                        if (localSearchText.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    localSearchText = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Effacer le texte"
                                )
                            }
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                MainFilterT1(
                    viewModel,
                    products, categories, uiState.searchText, Modifier.fillMaxSize(),
                )
            }
        }
    }
}
