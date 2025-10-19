package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.DebugTestsPerformInitialSearch
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

fun get_New_Datas(
    searchQuery: String,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
): Pair<ArticlesBasesStatsTable?, M3CouleurProduitInfos?> {
    val catalogues = B4CatalogueCategoriesRepository().sortedBy { it.position }
    val newOldId = repositorysMainGetter.repo1ProduitInfos.datasValue.maxOf { it.id } + 1
    val idParentCategorie = catalogues.find {
        it.keyID == "t1"
    }?.premierCategorieId

    val keyIDM3CouleurProduitInfos = getPushFireBase(M3CouleurProduitInfos.ref)
    val keyID = getPushFireBase(ArticlesBasesStatsTable.ref)

    val newProduit = idParentCategorie?.let {
        ArticlesBasesStatsTable.get_Default().copy(
            keyID = keyID,
            id = newOldId,
            creationTimestamp = System.currentTimeMillis(),
            nom = searchQuery,
            couleur1 = keyIDM3CouleurProduitInfos,
            idParentCategorie = it,
            disponibilityEtates = DisponibilityEtates.NON_DISPO
        )
    }

    val newCouleurP = newProduit?.let {
        M3CouleurProduitInfos.get_default().copy(
            keyID = keyIDM3CouleurProduitInfos,
            creationTimestamp = System.currentTimeMillis(),
            parentBProduitInfosKeyID = it.keyID,
            parentId1ProduitInfosDebugName = newProduit.nom,
            parentBProduitOldID = newProduit.id,
        )
    }

    return Pair(newProduit, newCouleurP)
}

@Composable
fun MainFastSearchProduitPourVent(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMainFastSearchProduitPourVent = koinViewModel(),
    sourceLenceurDeCetteFragment: ActiveCentralValues.RoleDefinieParSourceACetteFragment? = null,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val active_Central_Values = focusedValuesGetter.active_Central_Values
    val uiState by viewModel.uiState.collectAsState()
    val bProduitInfosRepository = uiState.bProduitInfosRepository
    val products = bProduitInfosRepository.datasValue
    val categories = viewModel.getter.repoM16CategorieProduit.datasValue
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val startTextSearchM1Produit = when (sourceLenceurDeCetteFragment) {
        is ActiveCentralValues.RoleDefinieParSourceACetteFragment.SearchProduit -> {
            sourceLenceurDeCetteFragment.produit.nom
        }
        is ActiveCentralValues.RoleDefinieParSourceACetteFragment.AfficheSearchAllProduits -> {
            ""
        }
        null -> ""
    }

    fun update_activeCentralValuesfastSearchProduitPourVent(capitalizedText: String) {
        focusedValuesGetter.update_activeCentralValues(
            active_Central_Values.copy(
                fastSearchProduitPourVent = capitalizedText
            )
        )
    }

    val focusRequester = remember { FocusRequester() }
    val fastSearchProduitPourVent = active_Central_Values.fastSearchProduitPourVent

    var isTextFieldReady by remember { mutableStateOf(false) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    var searchJob by remember { mutableStateOf<Job?>(null) }
    var lastSearchText by remember { mutableStateOf("") }

    val shouldShowTextField =
        sourceLenceurDeCetteFragment !is ActiveCentralValues.RoleDefinieParSourceACetteFragment.SearchProduit

    // Expose focus and keyboard control to FocusedValuesGetter
    LaunchedEffect(Unit) {
        focusedValuesGetter.setSearchFieldControls(
            focusRequester = focusRequester,
            keyboardController = keyboardController
        )
    }

    // FIXED: Monitor clearAndFocusTrigger with proper keyboard showing logic
    LaunchedEffect(active_Central_Values.clearAndFocusTrigger) {
        val trigger = active_Central_Values.clearAndFocusTrigger
        if (trigger > 0 && isTextFieldReady) {
            // Clear the text first
            update_activeCentralValuesfastSearchProduitPourVent("")

            // Small delay for UI to update
            delay(100)

            // Request focus - this will trigger onFocusChanged
            focusRequester.requestFocus()

            // Wait for focus to be acquired
            delay(150)

            // Now show keyboard - only if field is focused
            if (isTextFieldFocused) {
                keyboardController?.show()
            }
        }
    }

    // DEBOUNCED SEARCH IMPLEMENTATION (300ms delay)
    LaunchedEffect(fastSearchProduitPourVent) {
        searchJob?.cancel()

        when {
            fastSearchProduitPourVent.isEmpty() -> {
                lastSearchText = ""
                viewModel.onSearchTextChange("")
            }

            fastSearchProduitPourVent.length == 3 -> {
                lastSearchText = fastSearchProduitPourVent
                viewModel.onSearchTextChange(fastSearchProduitPourVent)
            }

            fastSearchProduitPourVent.length >= 4 -> {
                searchJob = launch {
                    delay(200)
                    if (fastSearchProduitPourVent != lastSearchText) {
                        lastSearchText = fastSearchProduitPourVent
                        viewModel.onSearchTextChange(fastSearchProduitPourVent)
                    }
                }
            }

            else -> {
                lastSearchText = ""
                viewModel.onSearchTextChange("")
            }
        }
    }

    var shouldPerformInitialSearch by remember { mutableStateOf(true) }

    LaunchedEffect(isTextFieldReady) {
        if (isTextFieldReady && shouldPerformInitialSearch) {
            DebugTestsPerformInitialSearch(
                enabled = shouldPerformInitialSearch,
                focusRequester = focusRequester,
                { searchText ->
                    update_activeCentralValuesfastSearchProduitPourVent(searchText)
                    shouldPerformInitialSearch = false
                },
                "liy"
            )
        }
    }

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Box {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (shouldShowTextField) {
                    OutlinedTextField(        //<--
                    //TODO(1): pk le focuse ce fait mais le text ne s efface pas et le clavie ne s affiche apre qe la quantity est choisi
                        value = fastSearchProduitPourVent,
                        onValueChange = { newText ->
                            val capitalizedText = newText.split(" ").joinToString(" ") { word ->
                                if (word.isNotEmpty()) {
                                    word.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase() else it.toString()
                                    }
                                } else {
                                    word
                                }
                            }
                            update_activeCentralValuesfastSearchProduitPourVent(capitalizedText)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                isTextFieldFocused = focusState.isFocused
                                // Show keyboard when focused
                                if (focusState.isFocused) {
                                    coroutineScope.launch {
                                        delay(100)
                                        keyboardController?.show()
                                    }
                                }
                            },
                        placeholder = {
                            Text("Rechercher un produit... (min. 3 caractères)")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                searchJob?.cancel()
                                keyboardController?.hide()
                            }
                        ),
                        leadingIcon = {
                            val newDatas = get_New_Datas(
                                searchQuery = fastSearchProduitPourVent,
                                aCentralFacade = aCentralFacade,
                            )
                            IconButton(
                                modifier = Modifier
                                    .semantics(mergeDescendants = true) {
                                        set(
                                            value = newDatas,
                                            key = SemanticsPropertyKey("newDatas")
                                        )
                                    },
                                onClick = {
                                    val newProduit = newDatas.first

                                    if (newProduit != null) {
                                        aCentralFacade.repositorysMainSetter.upsert_M1Produit(newProduit)
                                    }

                                    newDatas.second?.let {
                                        aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(
                                            it
                                        )
                                    }

                                    val statusMessage =
                                        if (aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.active_Central_Values.active_EtateDispoNonDifinieAuAddNew) {
                                            "Produit créé (état non défini): ${newProduit?.nom}"
                                        } else {
                                            "Produit WebP créé: ${newProduit?.nom}"
                                        }

                                    Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT)
                                        .show()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Créer nouveau produit",
                                )
                            }
                        },
                        trailingIcon = {
                            if (fastSearchProduitPourVent.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchJob?.cancel()
                                        update_activeCentralValuesfastSearchProduitPourVent("")
                                        // Refocus after clearing
                                        coroutineScope.launch {
                                            delay(100)
                                            focusRequester.requestFocus()
                                            delay(100)
                                            keyboardController?.show()
                                        }
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Effacer le texte"
                                    )
                                }
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))

                    LaunchedEffect(Unit) {
                        delay(100)
                        isTextFieldReady = true
                        if (shouldShowTextField && startTextSearchM1Produit.isEmpty()) {
                            focusRequester.requestFocus()
                            delay(150)
                            keyboardController?.show()
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
            focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie.ifTrue {
                Dialog_Fast_Affiche_Panie()
            }
        }
    }
}
