package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.W.Components

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProductHeader_T1(
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    productName: String,
    allNonTrouve: Boolean,
    onQuantityClickToHaptic: () -> Unit
) {
    val onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent = viewModel.getterFocusedVarsHandlerFacade
        .onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent

    // Fixed: Using derivedStateOf instead of remember for computed values
    val ventOperationsForProduct by derivedStateOf {
        onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent
            .filter { ventOperation ->
                ventOperation.parentM1ProduitInfosKeyId == produit.keyID
            }
    }

    val currentQuantityUnit = ventOperationsForProduct.firstOrNull()?.setIN_VentQuantity_Actuellement_Va_Remplire
        ?: M10OperationVentCouleur.SetIN_VentQuantity_Actuellement_Va_Remplire.quantity_Par_Boit

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
                .getSemanticsTag(onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent,"onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent")
                .getSemanticsTag(ventOperationsForProduct,"ventOperationsForProduct")
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (allNonTrouve) {
                    Text(
                        text = "Non disponible",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Toggle button for quantity unit
                if (ventOperationsForProduct.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            ventOperationsForProduct.forEach { ventOperation ->
                                val toggledOperation = ventOperation.toggleQuantityUnit()
                                viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur
                                    .addOrUpdateData(toggledOperation)
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (currentQuantityUnit == M10OperationVentCouleur.SetIN_VentQuantity_Actuellement_Va_Remplire.quantity_Par_Carton)
                                Icons.Default.Inventory2 // Box icon for carton
                            else
                                Icons.Default.ViewModule, // Unit/grid icon for boîte
                            contentDescription = null,
                            tint = when {
                                allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                currentQuantityUnit == M10OperationVentCouleur.SetIN_VentQuantity_Actuellement_Va_Remplire.quantity_Par_Boit ->
                                    MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.secondary
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                QuantityDisplay(
                    produit = produit,
                    viewModel = viewModel,
                    allNonTrouve = allNonTrouve,
                ) {
                    onQuantityClickToHaptic()
                }
            }
        }
    }
}
