package Application4.App.Fragment.ID2.Fragment

import EntreApps.Shared.Models.Home.ActiveCentralValues
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.MarkerStatusDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.MainFilterT1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MainScreen_FragID2() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MainFastSearchProduitPourVent_App4()
            PressistatntMainActivityButtons_Sec8FWinID1()
        }
    }
}

@Composable
fun MainFastSearchProduitPourVent_App4(
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

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Box {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(petitePaddine)
            ) { //<--
                if (shouldShowTextField) {
                    Text(
                        text = fastSearchProduitPourVent.ifEmpty { "Rechercher un produit... (min. 3 caractères)" },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(petitePaddine),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (fastSearchProduitPourVent.isEmpty())
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.onSurface
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
