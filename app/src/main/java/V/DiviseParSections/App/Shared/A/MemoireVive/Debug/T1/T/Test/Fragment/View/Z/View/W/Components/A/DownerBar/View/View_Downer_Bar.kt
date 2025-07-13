package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.W.Components.A.DownerBar.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.W.Components.QuantityDisplay
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@SuppressLint("UnrememberedMutableState")
@Composable
fun Downer_Bar_SemiModularized_Searcher(
    related_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
) {
    val onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent = viewModel.getterFocusedVarsHandlerFacade
        .onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent

    val ventOperationsForProduct by derivedStateOf {
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.get_ListFiltered_M10OperationVentCouleurs_By_M1Produit(
            produit
        )
    }
    val allNonTrouve =
        related_ListM10OperationVentCouleur.isNotEmpty() && related_ListM10OperationVentCouleur.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }

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
                    QuantityDisplay(
                        produit = produit,
                        viewModel = viewModel,
                        allNonTrouve = allNonTrouve,
                    ) {}
                }
            }
        }
    }
}
