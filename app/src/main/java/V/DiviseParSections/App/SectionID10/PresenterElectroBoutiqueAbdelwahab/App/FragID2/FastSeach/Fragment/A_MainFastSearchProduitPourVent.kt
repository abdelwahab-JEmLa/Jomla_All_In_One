package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.MarkerStatusDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.DebugTestsPerformInitialSearch
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
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
    val catalogues = get_ListM21CataloguesCategorie().sortedBy { it.position }
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
    var isCartonEditMode by remember { mutableStateOf(false) }

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
    var isEditMode by remember { mutableStateOf(false) }


    var cartonEditModeProductId by remember { mutableStateOf<String?>(null) }
    var boitEditModeProductId by remember { mutableStateOf<String?>(null) }  // NEW STATE

    val focusRequester = remember { FocusRequester() }
    val fastSearchProduitPourVent = active_Central_Values.fastSearchProduitPourVent

    var isTextFieldReady by remember { mutableStateOf(false) }

    // Debouncing implementation with proper state management
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var lastSearchText by remember { mutableStateOf("") }

    val shouldShowTextField =
        sourceLenceurDeCetteFragment !is ActiveCentralValues.RoleDefinieParSourceACetteFragment.SearchProduit

    /*LaunchedEffect(Unit) {
        if (M18CentralParametresOfAllApps.get_Default().its_AppType == AppType.GrossistRealSeller) {
            runCatching {
                GlobalContext.get().unloadModules(listOf(appModule))
            }
        }
    }         */
    // DEBOUNCED SEARCH IMPLEMENTATION (300ms delay)
    LaunchedEffect(fastSearchProduitPourVent) {
        // Cancel any pending search job
        searchJob?.cancel()

        when {
            // Clear search immediately when text is empty
            fastSearchProduitPourVent.isEmpty() -> {
                lastSearchText = ""
                viewModel.onSearchTextChange("")
            }

            // Start filtering immediately after reaching 3 characters
            fastSearchProduitPourVent.length == 3 -> {
                lastSearchText = fastSearchProduitPourVent
                viewModel.onSearchTextChange(fastSearchProduitPourVent)
            }

            // For 4+ characters, apply 300ms debounce for smooth rapid typing
            fastSearchProduitPourVent.length >= 4 -> {
                searchJob = launch {
                    delay(200) // 300ms debounce delay
                    if (fastSearchProduitPourVent != lastSearchText) {
                        lastSearchText = fastSearchProduitPourVent
                        viewModel.onSearchTextChange(fastSearchProduitPourVent)
                    }
                }
            }

            // Less than 3 characters - clear results but keep the text
            else -> {
                lastSearchText = ""
                viewModel.onSearchTextChange("")
            }
        }
    }

    var shouldPerformInitialSearch by remember { mutableStateOf(M18CentralParametresOfAllApps().itsDevMode) }

    LaunchedEffect(isTextFieldReady) {
        if (isTextFieldReady && shouldPerformInitialSearch) {
            DebugTestsPerformInitialSearch(
                enabled = shouldPerformInitialSearch,
                focusRequester = focusRequester,
                { searchText ->
                    update_activeCentralValuesfastSearchProduitPourVent(searchText)
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
                    .padding(petitePaddine)
            ) {
                if (shouldShowTextField) {
                    OutlinedTextField(
                        value = fastSearchProduitPourVent,
                        onValueChange = { newText ->
                            // Capitalize each word
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
                            .focusRequester(focusRequester),
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
                                update_activeCentralValuesfastSearchProduitPourVent("")
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
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Effacer le texte"
                                    )
                                }
                            }
                        }
                    )
                    Spacer(Modifier.height(petitePaddine))

                    LaunchedEffect(Unit) {
                        delay(100)
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
                    sourceLenceurDeCetteFragment,
                    searchFieldFocusRequester = focusRequester,
                    on_Pour_FocuceAfficheClavieSearcherProduit = {
                        coroutineScope.launch {
                            update_activeCentralValuesfastSearchProduitPourVent("")
                            delay(200)
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    },
                    cartonEditModeProductId = cartonEditModeProductId,
                    boitEditModeProductId = boitEditModeProductId,  // NEW PARAMETER
                    on_PourEntre_CartonEditeMode = { productId ->
                        cartonEditModeProductId = productId
                    },
                    on_PourEntre_BoitEditeMode = { productId ->  // NEW CALLBACK
                        boitEditModeProductId = productId
                    }
                )
            }
            focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie.ifTrue {
                Dialog_Fast_Affiche_Panie()
            }

            val currentValues = focusedValuesGetter.active_Central_Values
            val markerStatusDialogActiveM2Client = currentValues.markerStatusDialogActiveM2Client

            val shouldShowMarkerDialog = run {
                markerStatusDialogActiveM2Client != null
            }

            if (shouldShowMarkerDialog) {
                MarkerStatusDialog(
                    relative_M2Client = markerStatusDialogActiveM2Client,
                    markerStatusDialogActiveM2Client= markerStatusDialogActiveM2Client,
                    on_dissmiss_dialog_avec_enleve_focuse_bon={
                        focusedValuesGetter.update_activeCentralValues(
                            currentValues.copy(markerStatusDialogActiveM2Client = null)
                        )
                    }
                )
            }
        }
    }
}
