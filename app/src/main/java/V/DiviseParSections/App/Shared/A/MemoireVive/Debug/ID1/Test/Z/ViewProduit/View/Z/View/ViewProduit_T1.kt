package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ClickUpdate
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.Modules.PriceEditor_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.W.Components.ProductHeader_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.W.Components.ViewDisponibilityEtates
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.ListCouleurs
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.ModernQuantityDialog_T1
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B1CouleurOuGoutProduitDataBase
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun ViewProduit_T1(
    modifier: Modifier = Modifier,
    product: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1 = koinViewModel(),
) {
    val getter = viewModel.aCentral.getter
    val bProduitDataBase_SubClassFunctionality = viewModel.aCentral.getter.bProduitInfosRepository
    val b1CouleurOuGoutProduitDataBaseRepository = viewModel.b1CouleurOuGoutProduitDataBaseRepository
    val productKeyId = product.keyID
    val produit = bProduitDataBase_SubClassFunctionality.datasValue.find { it.keyID == productKeyId }

    // Fixed: Changed from List<Map.Entry<...>> to Pair<ArticlesBasesStatsTable, List<B1CouleurOuGoutProduitDataBase>>
    val produitWithColors by remember(product.id, b1CouleurOuGoutProduitDataBaseRepository.datasValue) {
        derivedStateOf {
            val relatedColors = b1CouleurOuGoutProduitDataBaseRepository.datasValue
                .filter { it.parentBProduitOldID == product.id }
                .sortedBy { it.indexCouleurDansAncienProto }

            Pair(product, relatedColors)
        }
    }

    val relatedVents by remember {
        derivedStateOf {
            getter.fVentCouleurOperationRepository.datasValue
                .filter { it.parentBProduitInfosKeyId == productKeyId }
        }
    }

    val haptic = LocalHapticFeedback.current

    val dialogStates by viewModel.dialogStates.collectAsState()
    val showDialog = dialogStates.productDialogStates[productKeyId] ?: false

    val totalQuantity = viewModel.getTotalQuantity(relatedVents)
    val productName = viewModel.getProductName(produit, productKeyId)
    val currentPrice = viewModel.getCurrentPrice(relatedVents)
    val hasNonTrouve = viewModel.hasNonTrouve(relatedVents)
    val allNonTrouve = viewModel.allNonTrouve(relatedVents)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
            ProductHeader_T1(
                productName = productName,
                allNonTrouve = allNonTrouve,
                hasNonTrouve = hasNonTrouve,
                totalQuantity = totalQuantity,
                onToggleDelivery = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    produit?.keyID?.let { viewModel.toggleEtateDeliveryNonTrouveVentOu(it) }
                },
                onQuantityClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.showProductDialog(productKeyId)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PriceEditor_T1(
                currentPrice = currentPrice,
                label = "Prix unitaire (toutes variantes)",
                onPriceUpdate = { price ->
                    if (!allNonTrouve) {
                        relatedVents.forEach { vent ->
                            viewModel.getter.fVentCouleurOperationRepository
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

            // Updated call to ListCouleurs with the new structure
            ListCouleurs(produitWithColors, viewModel)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (produit != null) {
        ViewDisponibilityEtates(product = produit)
    }

    if (showDialog && relatedVents.isNotEmpty() && !allNonTrouve) {
        ModernQuantityDialog_T1(
            clickUpdate = ClickUpdate.TotalQua,
            colorName = "Total - $productName",
            currentQuantity = totalQuantity,
            onDissmiss_showQuantityDialog = { viewModel.hideProductDialog(productKeyId) },
            onDismiss = { viewModel.hideProductDialog(productKeyId) },
            viewModel = viewModel,
            vent = relatedVents.first().copy(quantityAchete = totalQuantity)
        )
    }
}
