package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.B_ProductGroup

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.C.MainItem.UI.ViewVentCouleur_Module
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun View_Vent_M1Produit(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel(),
    aCentralFacade: ACentralFacade= koinInject(),
    repositorysMainSetter: RepositorysMainSetter =aCentralFacade.repositorysMainSetter,
    productKeyId: String,
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>,
) {
    val bProduitDataBase_SubClassFunctionality =
        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos

    val relative_M1Produit =
        bProduitDataBase_SubClassFunctionality.datasValue.find { it.keyID == productKeyId }

    val allNonTrouve =
        relative_List_M10OperationVentCouleur.isNotEmpty() && relative_List_M10OperationVentCouleur.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }

    // State for delete button activation
    var isDeleteButtonActivated by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    fun delete_list_Vents(datas: List<M10OperationVentCouleur>): Unit {
        repositorysMainSetter.delete_ListM10OperationVentCouleur(datas)
        isDeleteButtonActivated = false // Reset after deletion
    }

    Box {
        Card(
            modifier = modifier
                .semantics(mergeDescendants = true) {
                    set(value = relative_M1Produit, key = SemanticsPropertyKey("relative_M1Produit"))
                }
                .semantics(mergeDescendants = true) {
                    set(value = productKeyId, key = SemanticsPropertyKey("productKeyId"))
                }
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
                    .semantics(mergeDescendants = true) {
                        set(value = relative_M1Produit, key = SemanticsPropertyKey(""))
                    }
                    .padding(16.dp)
                    .graphicsLayer(alpha = if (allNonTrouve) 0.4f else 1.0f)
            ) {
                //----------------------------Header---------------------------------------------------------------------------------------------------------------------------------------------------------
                if (relative_M1Produit != null) {
                    ProductHeader_SemiModularized(relative_M1Produit, viewModel)
                }
                //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(relative_List_M10OperationVentCouleur) { vent ->
                        viewModel.uiStateCentralRepositorys.repo03CouleurProduitInfos.datasValue
                            .find { it.keyID == vent.parent_M3CouleurProduit_KeyID }?.let {
                                val relative_M3CouleurProduit = viewModel.aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.datasValue
                                    .find { it.keyID == vent.parent_M3CouleurProduit_KeyID }
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
                                    if (relative_M3CouleurProduit != null) {
                                        if (relative_M1Produit != null) {
                                            ViewVentCouleur_Module(
                                                modifier = Modifier.padding(4.dp),
                                                relative_M1Produit =relative_M1Produit,
                                                relative_M3CouleurProduit = relative_M3CouleurProduit
                                            )
                                        }
                                    }
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

        // Floating Action Button for Delete with Long Press Security
        if (relative_List_M10OperationVentCouleur.isNotEmpty()) {
            FloatingActionButton(
                onClick = {
                    if (isDeleteButtonActivated) {
                        delete_list_Vents(relative_List_M10OperationVentCouleur)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 8.dp, y = 8.dp)
                    .size(40.dp)
                    .combinedClickable(
                        onClick = {
                            if (isDeleteButtonActivated) {
                                delete_list_Vents(relative_List_M10OperationVentCouleur)
                            }
                        },
                        onLongClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            isDeleteButtonActivated = !isDeleteButtonActivated
                        }
                    ),
                shape = CircleShape,
                containerColor = if (isDeleteButtonActivated)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                contentColor = if (isDeleteButtonActivated)
                    MaterialTheme.colorScheme.onError
                else
                    MaterialTheme.colorScheme.onErrorContainer,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = if (isDeleteButtonActivated) 6.dp else 2.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = if (isDeleteButtonActivated)
                        "Delete items (activated)"
                    else
                        "Long press to activate delete",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
