// FICHIER 1: View_Downer_Bar.kt - FIXED
package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.A.DownerBar.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@SuppressLint("UnrememberedMutableState")
@Composable
fun Downer_Bar_SemiModularized_Searcher(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    related_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    onShowColorsClick: (() -> Unit)? = null,
    isExpanded: Boolean = true,
    on_Pour_FocuceAfficheClavieSearcherProduit: () -> Unit = {},
    onToggleExpand: () -> Unit = {},
    focusRequester: FocusRequester? = null,  // NEW: Pass FocusRequester
    on_PourEntre_EditeMode: (Boolean) -> Unit = {},  // FIXED: Accept Boolean parameter
    isCartonEditMode: Boolean
) {
    val onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent = viewModel.getterFocusedVarsHandlerFacade
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent

    val ventOperationsForProduct by derivedStateOf {
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.get_ListFiltered_M10OperationVentCouleurs_By_M1Produit(
            produit
        )
    }
    val allNonTrouve =
        related_ListM10OperationVentCouleur.isNotEmpty() && related_ListM10OperationVentCouleur.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }

    // State for edit modes
    var isEditMode by remember { mutableStateOf(false) }

    // Animation for rotate icon
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (allNonTrouve) {
                        listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    } else {
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                )
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .getSemanticsTag(
                    nomVal = "onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent",
                    data = onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent
                )
                .getSemanticsTag(
                    nomVal = "ventOperationsForProduct",
                    data = ventOperationsForProduct
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (allNonTrouve)
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {

                    focusedValuesGetter.currentApp_ItsWorkChezGrossisst.ifTrue {
                        CartonQuantityDisplay_Mo_F_(
                            produit = produit,
                            aCentralFacade = viewModel.aCentralFacade,
                            allNonTrouve = allNonTrouve,
                            isEditMode = isCartonEditMode,
                            focusRequester = focusRequester,  // FIXED: Renamed parameter
                            onEditModeChange = { newMode ->
                                // FIXED: Propagate the mode change upward
                                on_PourEntre_EditeMode(newMode)
                            },
                            onRequestSearchFocus = on_Pour_FocuceAfficheClavieSearcherProduit
                        )
                    }

                    QuantityDisplay_Mo_F_(
                        produit = produit,
                        aCentralFacade = viewModel.aCentralFacade,
                        allNonTrouve = allNonTrouve,
                        onShowColorsClick = onShowColorsClick,
                        isEditMode = isEditMode,
                        onEditModeChange = {
                            if (!it) on_Pour_FocuceAfficheClavieSearcherProduit()
                            isEditMode = it
                        },
                        onRequestSearchFocus = on_Pour_FocuceAfficheClavieSearcherProduit
                    )

                }
            }

            IconButton(
                onClick = onToggleExpand,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationAngle),
                    tint = if (allNonTrouve)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
