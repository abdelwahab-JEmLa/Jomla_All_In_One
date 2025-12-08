package V.DiviseParSections.App._0.Navigation

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.FabButton_When_ItsEditeBaseDonne
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.FabDropdownMenu_BaseDonneEdite
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.Floating_Separated_FragMap_Button_1_SelectCategorieEtAddNewProduit
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.Floating_Separated_FragMap_Button_5
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.FabDropdownMenu_WhenItsAchatsFragment
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.FloatingItems.Views.FabButton_When_Its_Achats
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.FloatingItems.Views.FragAchats_FloatingOutlinedSearcher_4
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.FabDropdownMenu_WhenIts_FragFastVent
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.FloatingItems.Views.CheckList_ChoisiseurActiveFilter
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.FabDropdownMenu_WhenIts_FragmentEducation
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ContentAlpha

private const val TAG = "NavigationBarWithFab"

@Composable
fun NavigationBarWithFab(
    viewModelInitApp: ViewModelInitApp,
    aCentralFacade: ACentralFacade = viewModelInitApp.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repo8BonVent: Repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent,
    fragmentNavigationHandler: FragmentNavigationHandler = aCentralFacade.modulesCentral.fragmentNavigationHandler,
    items: List<Screen>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isFabVisible: Boolean,
    onToggleFabVisibility: () -> Unit,
    onCatalogSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    showWarningState: Boolean = true
) {
    var showCatalogDialog by remember { mutableStateOf(false) }
    var showDialogTests by remember { mutableStateOf(false) }
    val activeFragment by fragmentNavigationHandler.currentFragment.collectAsState()
    var showFabDropdown by remember { mutableStateOf(false) }
    var showFabDropdownBaseDonne by remember { mutableStateOf(false) }
    var showFabDropdownAchats by remember { mutableStateOf(false) }
    var showFabDropdownFastVent by remember { mutableStateOf(false) }
    var showFabDropdownEducation by remember { mutableStateOf(false) }

    // Get current focused values to check floating button visibility
    val currentValues = focusedValuesGetter.active_Central_Values
    val shouldShowFloatingButton = currentValues.affiche_Floating_Button_SelecteCategorieEtAddNewProduit

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            // Calculate middle index
            val middleIndex = items.size / 2

            items.forEachIndexed { index, screen ->
                if (index == middleIndex) {
                    // Add empty space for FAB
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Box(modifier = Modifier.size(48.dp)) },
                        enabled = false
                    )
                }
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (currentRoute == screen.route) screen.color
                            else LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                        )
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        // FIXED: Safe navigation with try-catch to prevent crashes
                        try {
                            if (screen.route == Screen.DialogTests.route) {
                                showDialogTests = true
                            } else {
                                onNavigate(screen.route)
                            }
                        } catch (e: IllegalStateException) {
                            // Navigation graph not ready yet, ignore
                        }
                    }
                )
            }
        }

        val its_Targeted_Frag = activeFragment == Screen.A_Clients_LocationGps
        val its_EditDatabaseWithCreateNewArticles = activeFragment == Screen.EditDatabaseWithCreateNewArticles
        val its_Achats_Produits_Chez_Grossists = activeFragment == Screen.Achats_Produits_Chez_Grossists
        val its_FragmentProduitFastSearchDialog = activeFragment == Screen.FragmentProduitFastSearchDialog
        val its_EducationFragment = activeFragment == Screen.EducationFragment

        when {
            its_EditDatabaseWithCreateNewArticles -> {
                FabButton_When_ItsEditeBaseDonne(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = true,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = {
                        showFabDropdownBaseDonne = true
                    }
                )
            }
            its_Achats_Produits_Chez_Grossists -> {
                FabButton_When_Its_Achats(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = true,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = {
                        showFabDropdownAchats = true
                    }
                )
            }

            its_FragmentProduitFastSearchDialog -> {
                FabButton(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = true,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = { showFabDropdownFastVent = true }
                )
            }

            its_EducationFragment -> {
                FabButton(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = true,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = { showFabDropdownEducation = true }
                )
            }

            else -> {
                FabButton(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = its_Targeted_Frag,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = { showFabDropdown = true }
                )
            }
        }

        if (showCatalogDialog) {
            CatalogSelectionDialog(
                onDismiss = { showCatalogDialog = false },
                onCatalogSelected = { categoryId ->
                    onCatalogSelected(categoryId)
                    showCatalogDialog = false
                    onNavigate(Screen.FacadePresentoireProduits.route)
                },
                viewModelInitApp = viewModelInitApp
            )
        }

        if (showDialogTests) {
            TestScreens(
                onDismiss = { showDialogTests = false },
                fragmentNavigationHandler = fragmentNavigationHandler
            )
        }

        if (showFabDropdown && !its_EditDatabaseWithCreateNewArticles && !its_Achats_Produits_Chez_Grossists && !its_FragmentProduitFastSearchDialog && !its_EducationFragment) {
            FabDropdownMenu(
                showFabDropdown = showFabDropdown,
                onDismissDropdown = { showFabDropdown = false },
                repo8BonVent = repo8BonVent
            )
        }

        if (showFabDropdownBaseDonne && its_EditDatabaseWithCreateNewArticles) {
            FabDropdownMenu_BaseDonneEdite(
                onDismissDropdown = {
                    showFabDropdownBaseDonne = false
                }
            )
        }

        if (showFabDropdownAchats && its_Achats_Produits_Chez_Grossists) {
            FabDropdownMenu_WhenItsAchatsFragment(
                onDismissDropdown = {
                    showFabDropdownAchats = false
                }
            )
        }

        if (showFabDropdownEducation && its_EducationFragment) {
            FabDropdownMenu_WhenIts_FragmentEducation(
                onDismissDropdown = { showFabDropdownEducation = false }
            )
        }

        if (showFabDropdownFastVent && its_FragmentProduitFastSearchDialog) {
            FabDropdownMenu_WhenIts_FragFastVent(
                onDismissDropdown = { showFabDropdownFastVent = false }
            )
        }

        if (focusedValuesGetter.active_Central_Values.affiche_Floating_Button_SelecteCategorieEtAddNewProduit) {
            Floating_Separated_FragMap_Button_1_SelectCategorieEtAddNewProduit(
                aCentralFacade = aCentralFacade,
                repositorysMainGetter = aCentralFacade.repositorysMainGetter,
                focusedValuesGetter = focusedValuesGetter
            )
        }
        if (focusedValuesGetter.active_Central_Values.affiche_Floating_Button_FABsModeEditesProduit) {
            Floating_Separated_FragMap_Button_5(
                aCentralFacade = aCentralFacade,
                repositorysMainGetter = aCentralFacade.repositorysMainGetter,
                focusedValuesGetter = focusedValuesGetter
            )
        }

        if (focusedValuesGetter.active_Central_Values.afficheFloatingOutlinedSearcher_of_Achat) {
            FragAchats_FloatingOutlinedSearcher_4(
                aCentralFacade = aCentralFacade,
                focusedValuesGetter = focusedValuesGetter
            )
        }
        if (focusedValuesGetter.active_Central_Values.affiche_CheckList_ChoisiseurActiveFilter) {
            CheckList_ChoisiseurActiveFilter(
                aCentralFacade = aCentralFacade,
                focusedValuesGetter = focusedValuesGetter
            )
        }
    }
}

data class Item_States(
    val function_noms_separatedStrings: String = ",",
    val avec_Premier_Click_Jane: Boolean = true,
    val time_pressing_millis: Int = 1000,
    val icon_imageVector: ImageVector = Icons.Default.Close,
) {
    companion object {
        fun get_Arab_Nom(function_noms_separatedStrings: String): String {
            return extract_Noms(function_noms_separatedStrings).getOrNull(1) ?: ""
        }

        fun get_English_Nom(function_noms_separatedStrings: String): String {
            return extract_Noms(function_noms_separatedStrings).getOrNull(0) ?: ""
        }

        fun extract_Noms(function_noms_separatedStrings: String): List<String> {
            return function_noms_separatedStrings.split(",").map { it.trim() }
        }

        fun get_Default(): Item_States {
            return Item_States()
        }
    }
}
