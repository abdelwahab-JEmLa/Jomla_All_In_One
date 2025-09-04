package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.DebugTestsPerformInitialSearch
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

fun addNewFastSearch(
    searchQuery: String,
    context: Context,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val catalogues =
        B4CatalogueCategoriesRepository().sortedBy { it.position }
    val newOldId = repositorysMainGetter.repo1ProduitInfos.datasValue.maxOf { it.id } + 1
    val idParentCategorie = catalogues.find {
        it.keyID == "t1"
    }?.premierCategorieId

    val keyIDM3CouleurProduitInfos = getPushFireBase(M3CouleurProduitInfos.ref)
    val keyID = getPushFireBase(ArticlesBasesStatsTable.ref)

    val currentValues = focusedValuesGetter.active_Central_Values

    val newProduit = ArticlesBasesStatsTable
        .get_Default()
        .copy(
            keyID = keyID,
            id = newOldId,
            creationTimestamp = System.currentTimeMillis(),
            nom = searchQuery,
            couleur1 = keyIDM3CouleurProduitInfos,
            idParentCategorie = idParentCategorie
        )

    aCentralFacade.repositorysMainSetter.update_M1Produit(newProduit)

    val newCouleurP = M3CouleurProduitInfos
        .get_default()
        .copy(
            keyID = keyIDM3CouleurProduitInfos,
            creationTimestamp = System.currentTimeMillis(),
            parentBProduitInfosKeyID = newProduit.keyID,
            parentId1ProduitInfosDebugName = newProduit.nom,
            parentBProduitOldID = newProduit.id,
        )

    aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(
        newCouleurP
    )

    val statusMessage = if (currentValues.active_EtateDispoNonDifinieAuAddNew) {
        "Produit créé (état non défini): ${newProduit.nom}"
    } else {
        "Produit WebP créé: ${newProduit.nom}"
    }

    Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show()
}

@Composable
fun MainFastSearchProduitPourVent(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMainFastSearchProduitPourVent = koinViewModel(),
    sourceLenceurDeCetteFragment: ActiveCentralValues.RoleDefinieParSourceACetteFragment? = null,
    aCentralFacade: ACentralFacade = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val bProduitInfosRepository = uiState.bProduitInfosRepository
    val products = bProduitInfosRepository.datasValue
    val categories = viewModel.getter.repoM16CategorieProduit.datasValue
    val context = LocalContext.current

    // Fixed: Properly handle the sealed class to extract the product name
    val startTextSearchM1Produit = when (sourceLenceurDeCetteFragment) {
        is ActiveCentralValues.RoleDefinieParSourceACetteFragment.SearchProduit -> {
            sourceLenceurDeCetteFragment.produit.nom
        }

        is ActiveCentralValues.RoleDefinieParSourceACetteFragment.AfficheSearchAllProduits -> {
            ""
        }

        null -> ""
    }

    val focusRequester = remember { FocusRequester() }
    var localSearchText by remember { mutableStateOf(startTextSearchM1Produit) }
    var isTextFieldReady by remember { mutableStateOf(false) }

    // Only request focus if the OutlinedTextField will be shown
    val shouldShowTextField =
        sourceLenceurDeCetteFragment !is ActiveCentralValues.RoleDefinieParSourceACetteFragment.SearchProduit

    LaunchedEffect(localSearchText) {
        if (localSearchText.isNotEmpty()) {
            delay(500)
            viewModel.onSearchTextChange(localSearchText)
        } else {
            viewModel.onSearchTextChange("")
        }
    }

    var shouldPerformInitialSearch by remember { mutableStateOf(false) }

    // FIXED: Only call DebugTestsPerformInitialSearch after TextField is ready
    LaunchedEffect(isTextFieldReady) {
        if (isTextFieldReady && shouldPerformInitialSearch) {
            DebugTestsPerformInitialSearch(
                enabled = shouldPerformInitialSearch,
                focusRequester = focusRequester,
                { searchText ->
                    localSearchText = searchText
                    shouldPerformInitialSearch = false
                },
                "liy"
            )
        }
    }
    fun clickHandel(localSearchText: String): Unit {
        addNewFastSearch(
            searchQuery = localSearchText,
            aCentralFacade = aCentralFacade,
            context = context
        )
    }
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
                            IconButton(
                                modifier = Modifier
                                    .semantics(mergeDescendants = true) {
                                        set(value = clickHandel(localSearchText), key = SemanticsPropertyKey("onClick()"))
                                    },
                                onClick = {
                                    clickHandel(localSearchText)
                                },
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

                    // FIXED: Mark TextField as ready after it's composed
                    LaunchedEffect(Unit) {
                        delay(100) // Small delay to ensure TextField is fully composed
                        isTextFieldReady = true
                        if (shouldShowTextField && startTextSearchM1Produit.isEmpty()) {
                            focusRequester.requestFocus()
                        }
                    }
                }

                MainFilterT1(
                    viewModel,
                    products,
                    categories,
                    uiState.searchText,
                    Modifier.fillMaxSize(),
                    sourceLenceurDeCetteFragment
                )
            }
        }
    }
}
