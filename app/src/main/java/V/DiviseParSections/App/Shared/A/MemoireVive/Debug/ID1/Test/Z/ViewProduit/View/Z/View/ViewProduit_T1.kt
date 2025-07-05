package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.W.Components.ProductHeader_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.W.Components.ViewDisponibilityEtates
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.ListCouleurs
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
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
    val b1CouleurOuGoutProduitDataBaseRepository =
        viewModel.b1CouleurOuGoutProduitDataBaseRepository
    val productKeyId = product.keyID
    val produit =
        bProduitDataBase_SubClassFunctionality.datasValue.find { it.keyID == productKeyId }

    val onVentM8BonVent = viewModel.getterFocusedVarsHandlerFacade.onVentM8BonVent

    val produitWithColors by remember(
        product.id,
        b1CouleurOuGoutProduitDataBaseRepository.datasValue
    ) {
        derivedStateOf {
            val relatedColors = b1CouleurOuGoutProduitDataBaseRepository.datasValue
                .filter { it.parentBProduitOldID == product.id }
                .sortedBy { it.indexCouleurDansAncienProto }

            Pair(product, relatedColors)
        }
    }

    val relatedVents by remember {
        derivedStateOf {
            getter.repo10OperationVentCouleur.datasValue
                .filter { it.parentM1ProduitInfosKeyId == productKeyId }
        }
    }

    val haptic = LocalHapticFeedback.current

    val showDialog = remember(viewModel.getterFocusedVarsHandlerFacade.onVentM3CouleurProduitInfos, productKeyId) {
        val onVentM3 = viewModel.getterFocusedVarsHandlerFacade.onVentM3CouleurProduitInfos
        onVentM3?.parentM1ProduitInfosKeyId == (productKeyId ?: false)
    }

    val totalQuantity = viewModel.getTotalQuantity(relatedVents)
    val productName = viewModel.getProductName(produit, productKeyId)
    val allNonTrouve = viewModel.allNonTrouve(relatedVents)

    Card(
        modifier = modifier
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
                onVentM8BonVent=onVentM8BonVent,
                produit = product,
                viewModel = viewModel,
                productName = productName,
                allNonTrouve = allNonTrouve,
                onQuantityClickToHaptic = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ListCouleurs(produitWithColors, viewModel)

            Spacer(modifier = Modifier.height(12.dp))

            if (produit != null) {
                ViewDisponibilityEtates(product = produit)
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))


}
