package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.A.DownerBar.View.Downer_Bar_SemiModularized_Searcher
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.ProductHeader_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.VentProduitQuantityDialog_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.ViewDisponibilityEtates
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.ListCouleurs
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun ViewProduit_T1(
    modifier: Modifier = Modifier,
    product: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1 = koinViewModel(),
) {
    val getter = viewModel.aCentralFacade.repositorysMainGetter
    val bProduitDataBase_SubClassFunctionality =
        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos
    val b1CouleurOuGoutProduitDataBaseRepository =
        viewModel.b1CouleurOuGoutProduitDataBaseRepository
    val productKeyId = product.keyID
    val produit =
        bProduitDataBase_SubClassFunctionality.datasValue.find { it.keyID == productKeyId }

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


    val haptic = LocalHapticFeedback.current
    val relatedVents = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
        .get_ListFiltered_M10OperationVentCouleurs_By_M1Produit(
            product
        )

    val allNonTrouve =
        relatedVents.isNotEmpty() && relatedVents.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }

    Card(
        modifier = modifier
            .getSemanticsTag(produit,"produit")
            .getSemanticsTag(produit?.getDebugInfos(),"getDebugInfos")
            .getSemanticsTag(produit?.quantite_Boit_Par_Carton?:"","quantite_boit_par_carton",2)
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
                produit = product,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(12.dp))

            ListCouleurs(produitWithColors, viewModel)

            Spacer(modifier = Modifier.height(12.dp))

            if (produit != null) {
                ViewDisponibilityEtates(product = produit)
            }
            Downer_Bar_SemiModularized_Searcher(
                related_ListM10OperationVentCouleur=relatedVents,
                produit = product,
                viewModel = viewModel,
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    val getterFocusedVarsHandlerFacade = viewModel.getterFocusedVarsHandlerFacade
    val ouvertDialogProduit = getterFocusedVarsHandlerFacade.active_M1ProduitInfos_In_CurCompt_DialogQantity_Defineur

    if (produit != null && ouvertDialogProduit?.keyID == produit.keyID) {
        val operationForDialog = relatedVents.firstOrNull()
            ?: getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()?.copy(
                parentM1ProduitInfosKeyId = produit.keyID,
                parentM1ProduitDebugInfos = produit.nom,
                parentM3CouleurProduitDebugInfos = "Default Color",
                quantity = 0
            )

        operationForDialog?.let { operation ->
            VentProduitQuantityDialog_T1(
                produit = produit,
                viewModel = viewModel,
                colorName = operation.parentM3CouleurProduitDebugInfos,
                currentQuantity = operation.quantity,
                onDismiss = {
                    viewModel.setterFocusedVarsHandlerFacade.fermeFocucePourPrixDeM1ProduitDialogChoisireQuantityFacade(
                        produit
                    )
                }
            )
        }
    }
}
