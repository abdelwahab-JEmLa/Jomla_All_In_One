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
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
                    centralValues.outlined_filter_searcher_achat.isNullOrBlank()
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

    // FIXED: Auto-show pending orders info when no filters active
    LaunchedEffect(noFiltersActive, productsWithPendingOrders) {
        if (noFiltersActive && productsWithPendingOrders.isNotEmpty()) {
            // Optional: You could show a toast or info message here
            // Toast.makeText(context, "${productsWithPendingOrders.size} produits avec commandes en attente", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar_With_DropDownMenu(viewModel, uiState = uiState)

        // FIXED: Show pending orders view when no filters, otherwise show normal list
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

// FIXED: New composable to display pending orders grouped by product
@Composable
fun List_PendingOrdersFromWholesaler(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    productsWithOrders: List<Triple<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>, Int>>
) {

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Commandes en attente",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Commandes en attente du grossiste",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        items(productsWithOrders) { (product, colors, totalOrders) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.nom,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "$totalOrders à commander",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Display colors with pending orders
                    colors.forEach { color ->
                        if (color.a_cammende_depuit_grossist > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "• ${color.nomCouleurStrSiSonImageDispo}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${color.a_cammende_depuit_grossist} unités",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
