package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.DebugTestsPerformInitialSearch
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainFastSearchProduitPourVent(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMainFastSearchProduitPourVent = koinViewModel(),
    sourceLenceurDeCetteFragment: ViewModelMainFastSearchProduitPourVent.RoleDefinieParSourceACetteFragment? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    val bProduitInfosRepository = uiState.bProduitInfosRepository
    val products = bProduitInfosRepository.datasValue
    val categories = viewModel.getter.b3CategoriesCompoRepository.datasValue

    // Fixed: Properly handle the sealed class to extract the product name
    val startTextSearchM1Produit = when (sourceLenceurDeCetteFragment) {
        is ViewModelMainFastSearchProduitPourVent.RoleDefinieParSourceACetteFragment.SearchProduit -> {
            sourceLenceurDeCetteFragment.produit.nom
        }
        is ViewModelMainFastSearchProduitPourVent.RoleDefinieParSourceACetteFragment.AfficheSearchAllProduits -> {
            ""
        }
        null -> ""
    }

    val focusRequester = remember { FocusRequester() }
    var localSearchText by remember { mutableStateOf(startTextSearchM1Produit) }

    // Only request focus if the OutlinedTextField will be shown
    val shouldShowTextField = sourceLenceurDeCetteFragment !is ViewModelMainFastSearchProduitPourVent.RoleDefinieParSourceACetteFragment.SearchProduit

    LaunchedEffect(Unit) {
        if (shouldShowTextField && startTextSearchM1Produit.isEmpty()) {
            delay(100)
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(localSearchText) {
        if (localSearchText.isNotEmpty()) {
            delay(500)
            viewModel.onSearchTextChange(localSearchText)
        } else {
            viewModel.onSearchTextChange("")
        }
    }

    var shouldPerformInitialSearch by remember { mutableStateOf(true) }

    DebugTestsPerformInitialSearch(
        enabled = shouldPerformInitialSearch,
        focusRequester = focusRequester,
        { searchText ->
            localSearchText = searchText
            shouldPerformInitialSearch = false
        },
        "liy"
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
                if (shouldShowTextField) {
                    OutlinedTextField(
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
                            val handleBonVentSelection =  {
                                val newCouleurP = M3CouleurProduitInfos(
                                    parentBProduitOldID = newProduit.id,
                                    parentBProduitInfosKeyID = newProduit.keyID,
                                    parentId1ProduitInfosDebugName = newProduit.nom,
                                    processPositioningInFactory = M3CouleurProduitInfos.ProcessPositioningInFactory.CreeDepuitRechercheRapid
                                )
                                viewModel.aCentralFacade.repositorysMainGetter.repo3CouleurProduitInfos.addOrUpdateData(
                                    newCouleurP
                                )
                            }

                            IconButton(
                                onClick = {
                                    uiState.bProduitInfosRepository.upsert(newProduit)
                                    handleBonVentSelection()
                                },
                                modifier = Modifier
                                    .getSemanticsTag(newProduit, "newProduit")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Créer nouveau produit",
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
                }

                MainFilterT1(
                    viewModel,
                    products, categories, uiState.searchText, Modifier.fillMaxSize(),sourceLenceurDeCetteFragment
                )
            }
        }
    }
}
