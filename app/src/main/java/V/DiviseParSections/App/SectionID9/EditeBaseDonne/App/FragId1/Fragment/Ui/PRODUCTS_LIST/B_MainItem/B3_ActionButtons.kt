package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ActionButtons(
    modifier: Modifier,
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit,
    viewModel: Sec9FragId1ViewId2ViewModel,
    uiState: Sec9FragId1ViewId2ViewModel.UiState,
) {
    // Get individual product expansion state
    val isIndividuallyExpanded = viewModel.isProductDetailsExpanded(produit.bsonObjectId)

    // Final expansion state: global setting AND individual setting
    val shouldShowExpanded = uiState.showDetailsExpandedPourTout && isIndividuallyExpanded

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Bouton Priorité (à gauche - plus important)
            FilledTonalButton(
                onClick = { updateProduct(produit.copy(heldPrioriteDemandAuGrossist = !produit.heldPrioriteDemandAuGrossist)) },
                modifier = Modifier.weight(0.4f),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (produit.heldPrioriteDemandAuGrossist) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = if (produit.heldPrioriteDemandAuGrossist) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Priorité",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            // Bouton Afficher/Masquer détails (à droite - plus large)
            FilledTonalButton(
                onClick = { viewModel.toggleProductDetailsVisibility(produit.bsonObjectId) },
                modifier = Modifier.weight(0.6f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (shouldShowExpanded) "Masquer" else "Détails",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (shouldShowExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
