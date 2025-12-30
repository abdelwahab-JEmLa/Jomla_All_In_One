package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.AppBar.Settings.TopAppBar_With_DropDownMenu
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_Client.Dialog_Filter_Client
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_VentPeriod.Dialog_Filter_VentPeriod
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.F.Dialog_Filter_Product
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.Dialog_Choisire_Grossist_Modularized
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.List_GroupeAchatProduit
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun Screen_GrossistAchatSec12FragID1(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel = koinViewModel(),
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
) {
    val active_M2Client_AuFilterAchats =
        focusedValuesGetter.active_Central_Values.active_M2Client_AuFilterAchats
    val active_M1Produit_AuFilterAchats =
        focusedValuesGetter.active_Central_Values.active_M1Produit_AuFilterAchats

    val uiState by viewModel.uiState.collectAsState()

    // FIXED: Check if no filters are active
    val noFiltersActive by remember {
        derivedStateOf {
            val centralValues = focusedValuesGetter.active_Central_Values
            centralValues.active_M14VentPeriode_AuFilterAchats == null &&
                    centralValues.active_M15Grossist_AuFilterAchats == null &&
                    centralValues.active_M2Client_AuFilterAchats == null &&
                    centralValues.active_M1Produit_AuFilterAchats == null &&
                    centralValues.outlined_filter_searcher_floating_abouve_all.isNullOrBlank()
        }
    }

    // FIXED: Get products with pending orders from wholesaler
    val productsWithPendingOrders by remember {
        derivedStateOf {
            if (noFiltersActive) {
                val repo03 = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos
                val repo01 = aCentralFacade.repositorysMainGetter.repo1ProduitInfos

                // Find all colors with positive a_cammende_depuit_grossist
                val colorsWithOrders = repo03.datasValue.filter {
                    it.a_cammende_depuit_grossist > 0
                }

                // Group by product and get product details
                colorsWithOrders
                    .groupBy { it.parentBProduitInfosKeyID }
                    .mapNotNull { (productKeyId, colors) ->
                        val product = repo01.datasValue.find { it.keyID == productKeyId }
                        if (product != null) {
                            Triple(
                                product,
                                colors,
                                colors.sumOf { it.a_cammende_depuit_grossist }
                            )
                        } else null
                    }
                    .sortedByDescending { it.third } // Sort by total orders descending
            } else {
                emptyList()
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar_With_DropDownMenu(viewModel, uiState = uiState)

        if (noFiltersActive && productsWithPendingOrders.isNotEmpty()) {
            List_PendingOrdersFromWholesaler(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                viewModel = viewModel,
                productsWithOrders = productsWithPendingOrders
            )
        } else {
            List_GroupeAchatProduit(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                viewModel = viewModel
            )
        }
    }

    if (uiState.dialog_Filter_VentPeriod_showDialog) {
        Dialog_Filter_VentPeriod(viewModel) { period ->
            viewModel.update_dialog_Filter_VentPeriod_showDialog(false)
        }
    }

    if (uiState.dialog_Choisire_Grossist_Modularized_showDialog_Pour_MainScreen) {
        Dialog_Choisire_Grossist_Modularized(
            titel = "Choisir un Grossiste",
            viewModel = viewModel,
            list_M11AchatOperation = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
        ) { grossist ->
            if (grossist != null) {
                focusedValuesGetter.addGrossistFilter(grossist)
            } else {
                focusedValuesGetter.removeGrossistFilter()
            }
            viewModel.update_dialog_Choisire_Grossist_Modularized_showDialog(pour_MainScreen = false)
        }
    }

    if (uiState.show_Dialog_filter_AChats_Par_Client_Acheteur) {
        Dialog_Filter_Client(
            viewModel,
            onDismiss = {
                viewModel.update_show_Dialog_filter_AChats_Par_Client_Acheteur(false)
            },
            active_M14VentPeriode_AuFilterAchats = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.active_Central_Values.active_M14VentPeriode_AuFilterAchats
        )
    }

    if (uiState.show_Dialog_filter_Products_Par_Client) {
        Dialog_Filter_Product(
            viewModel = viewModel,
            activeClient = active_M2Client_AuFilterAchats,
            onDismiss = { product ->
                if (product != null) {
                    focusedValuesGetter.addProductFilter(product)
                } else {
                    focusedValuesGetter.removeProductFilter()
                }
                viewModel.update_show_Dialog_filter_Products_Par_Client(false)
            }
        )
    }
}

