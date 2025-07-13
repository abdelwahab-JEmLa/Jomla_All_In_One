package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.B_ProductGroup

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ClickUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.C.MainItem.UI.Quantity.Ui.A.Screen.ModernQuantityDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.C.MainItem.UI.VentDisplayer_Sec2FragId2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.UI.PriceEditorFragID2
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProductGroup(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel(),
    productKeyId: String,
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>,
) {
    val bProduitDataBase_SubClassFunctionality =
        viewModel.aCentralFacade.repositorysMainGetter.repoM1ProduitInfos

    val relative_M1Produit =
        bProduitDataBase_SubClassFunctionality.datasValue.find { it.keyID == productKeyId }
    val relative_First_OF_ListM13Tariffication =
        viewModel.aCentralFacade.repositorysMainGetter.m13Tarification_By_KeyID(
            relative_List_M10OperationVentCouleur.first().parentM13TarificationKeyID
        )


    val haptic = LocalHapticFeedback.current
    var showDialog by remember { mutableStateOf(false) }

    val totalQuantity = relative_List_M10OperationVentCouleur.sumOf { it.quantity }
    val productName = relative_M1Produit?.nom?.takeIf { it.isNotBlank() }
        ?: relative_M1Produit?.nomMutable?.takeIf { it.isNotBlank() }
        ?: "Product #$productKeyId"


    val currentPrice = relative_First_OF_ListM13Tariffication?.prixCurrency ?: 0.0
    val hasNonTrouve =
        relative_List_M10OperationVentCouleur.any { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
    val allNonTrouve =
        relative_List_M10OperationVentCouleur.isNotEmpty() && relative_List_M10OperationVentCouleur.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(if (allNonTrouve) 2.dp else 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allNonTrouve) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .graphicsLayer(alpha = if (allNonTrouve) 0.4f else 1.0f)
        ) {
            //----------------------------Header---------------------------------------------------------------------------------------------------------------------------------------------------------
            if (relative_M1Produit != null) {
                ProductHeader_SemiModularized(relative_M1Produit, viewModel)
            }
            ProductHeader(
                viewModel=viewModel,
                productName = productName,
                allNonTrouve = allNonTrouve,
                hasNonTrouve = hasNonTrouve,
                totalQuantity = totalQuantity,
                onToggleDelivery = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    relative_M1Produit?.keyID?.let { viewModel.aCentralFacade.repositorysMainSetter.toggleEtateDeliveryNonTrouveVentOuFacade(it)  }
                },
                onQuantityClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showDialog = true
                },
                relative_M1Produit =relative_M1Produit,
            )

            //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

            Spacer(modifier = Modifier.height(12.dp))

            PriceEditorFragID2(
                currentPrice = currentPrice,
                label = "Prix Boit (toutes variantes)",
                onPriceUpdate = { price ->
                    if (!allNonTrouve) {
                        relative_List_M10OperationVentCouleur.forEach { vent ->
                            viewModel.uiStateCentralRepositorys.repo10OperationVentCouleur
                                .addOrUpdateData(
                                    vent.copy(
                                        provisoireMonPrix = price,
                                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                    )
                                )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textColor = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(relative_List_M10OperationVentCouleur) { vent ->
                    viewModel.uiStateCentralRepositorys.repo3CouleurProduitInfos.datasValue
                        .find { it.keyID == vent.parentM3CouleurProduitInfosKeyID }?.let {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(if (allNonTrouve) 1.dp else 2.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier
                                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                    .graphicsLayer(
                                        alpha = if (vent.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve) 0.5f else 1.0f
                                    )
                            ) {
                                VentDisplayer_Sec2FragId2(
                                    modifier = Modifier.padding(4.dp),
                                    ventKey = vent.keyID,
                                    size = 120.dp,
                                    purchasedQuantity = vent.quantity,
                                    viewModel = viewModel
                                )
                            }
                        }
                }
            }
            if (relative_M1Produit != null) {
                Downer_Bar_SemiModularized_panie(
                    relative_List_M10OperationVentCouleur =relative_List_M10OperationVentCouleur ,
                    viewModel = viewModel,
                    relative_M1Produit = relative_M1Produit
                )
            }
        }
    }

    if (showDialog && relative_List_M10OperationVentCouleur.isNotEmpty() && !allNonTrouve) {
        ModernQuantityDialog(
            clickUpdate = ClickUpdate.TotalQua,
            colorName = "Total - $productName",
            currentQuantity = totalQuantity,
            onDissmiss_showQuantityDialog = { showDialog = false },
            onDismiss = { showDialog = false },
            viewModel = viewModel,
            vent = relative_List_M10OperationVentCouleur.first().copy(quantity = totalQuantity)
        )
    }
}

