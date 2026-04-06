package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.A.DownerBar.View.Downer_Bar_SemiModularized_Searcher
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.V1ProductHeader_T1.View.ProductHeader_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.VentProduitQuantityDialog_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.ViewDisponibilityEtates
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.ListCouleurs
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import EntreApps.Shared.Models.Home.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
@Composable
fun ViewProduit_T1(
    modifier: Modifier = Modifier,
    product: M01Produit,
    viewModel: ViewModelsProduit_T1 = koinViewModel(),
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    searchFieldFocusRequester: FocusRequester? = null,
    on_Pour_FocuceAfficheClavieSearcherProduit: () -> Unit = {},
    isCartonEditMode: Boolean,
    isBoitEditMode: Boolean,
    on_PourEntre_CartonEditeMode: (Boolean) -> Unit = {},
    on_PourEntre_BoitEditeMode: (Boolean) -> Unit = {},
) {
    val currentActive_M9AppCompt = focusedValuesGetter.currentActive_M9AppCompt
    val image_detail_produit_s_affiche = currentActive_M9AppCompt?.image_detail_produit_s_affiche ?: false

    // Initial expansion state based on user role and settings
    var isExpanded by remember {
        mutableStateOf(
            focusedValuesGetter.currentApp_Its_Vendeur ||
                    !focusedValuesGetter.currentApp_ItsWorkChezGrossisst ||
                    M00CentralParametresOfAllApps.get_Default().itsDevMode
        )
    }

    // Track changes to image_detail_produit_s_affiche and update isExpanded accordingly
    LaunchedEffect(image_detail_produit_s_affiche) {
        // When image detail is active, expand the view
        // When image detail is inactive, keep current expansion state or collapse based on user preferences
        if (image_detail_produit_s_affiche) {
            isExpanded = true
        }
    }

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
            .getSemanticsTag(produit, "produit")
            .getSemanticsTag(produit?.getDebugInfos(), "get_DebugsInfos")
            .getSemanticsTag(produit?.quantite_Boit_Par_Carton ?: "", "quantite_boit_par_carton", 2)
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
                .padding(petitePaddine)
                .graphicsLayer(alpha = if (allNonTrouve) 0.4f else 1.0f)
        ) {
            ProductHeader_T1(
                relative_Produit = product,
                viewModel = viewModel,
                isExpanded = isExpanded
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    ListCouleurs(produitWithColors, viewModel)
                    if (produit != null &&   !ActiveCentralValues.get_Default().affiche_Produit_OnGrid) {
                        ViewDisponibilityEtates(product = produit)
                    }
                }
            }

            Downer_Bar_SemiModularized_Searcher(
                related_ListM10OperationVentCouleur = relatedVents,
                produit = product,
                viewModel = viewModel,
                searchFieldFocusRequester = searchFieldFocusRequester,
                isExpanded = isExpanded,
                onToggleExpand = { isExpanded = !isExpanded },
                isCartonEditMode = isCartonEditMode,
                isBoitEditMode = isBoitEditMode,
                on_PourEntre_CartonEditeMode = on_PourEntre_CartonEditeMode,
                on_PourEntre_BoitEditeMode = on_PourEntre_BoitEditeMode,
                on_Pour_FocuceAfficheClavieSearcherProduit = on_Pour_FocuceAfficheClavieSearcherProduit
            )
        }
    }

    val getterFocusedVarsHandlerFacade = viewModel.getterFocusedVarsHandlerFacade
    val ouvertDialogProduit =
        getterFocusedVarsHandlerFacade.active_M1ProduitInfos_In_CurCompt_DialogQantity_Defineur

    if (produit != null && ouvertDialogProduit?.keyID == produit.keyID) {
        val operationForDialog = relatedVents.firstOrNull()
            ?: getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()?.copy(
                parent_M1Produit_KeyId = produit.keyID,
                parent_M1Produit_DebugInfos = produit.nom,
                parent_M3CouleurProduit_DebugInfos = "Default Color",
                quantity = 0
            )

        operationForDialog?.let { operation ->
            VentProduitQuantityDialog_T1(
                produit = produit,
                viewModel = viewModel,
                colorName = operation.parent_M3CouleurProduit_DebugInfos,
                currentQuantity = operation.quantity,
                onDismiss = {
                    viewModel.setterFocusedVarsHandlerFacade.fermeFocucePourPrixDeM1ProduitDialogChoisireQuantityFacade(
                        produit
                    )
                    on_Pour_FocuceAfficheClavieSearcherProduit()
                },
            )
        }
    }
}
