package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CartSummarySection(viewModel: ZViewModel_Sec1Frag3) {
    val repo = viewModel.uiStateCentralRepositorys.repo10OperationVentCouleur

    val summaryByDeliveryStatus by remember {
        derivedStateOf {
            val allVents = repo.onVentFilteredDatas

            val ventsTrouve = allVents.filter {
                it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
            }
            val ventsNonTrouve = allVents.filter {
                it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve
            }

            DeliveryStatusSummary(
                trouveSummary = CartSummary(
                    totalItems = ventsTrouve.sumOf { it.quantityAchete },
                    totalProducts = ventsTrouve.groupBy { it.parentM1ProduitInfosKeyId }.size,
                    totalValue = ventsTrouve.sumOf { it.quantityAchete * it.provisoireMonPrix },
                    itemsCount = ventsTrouve.size
                ),
                nonTrouveSummary = CartSummary(
                    totalItems = ventsNonTrouve.sumOf { it.quantityAchete },
                    totalProducts = ventsNonTrouve.groupBy { it.parentM1ProduitInfosKeyId }.size,
                    totalValue = ventsNonTrouve.sumOf { it.quantityAchete * it.provisoireMonPrix },
                    itemsCount = ventsNonTrouve.size
                )
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Found items section
        if (summaryByDeliveryStatus.trouveSummary.itemsCount > 0) {
            SummarySection(
                title = "Articles Trouvés",
                summary = summaryByDeliveryStatus.trouveSummary,
                icon = Icons.Default.CheckCircle,
                iconTint = Color(0xFF4CAF50), // Green
                showTotal = true,
                viewModel = viewModel
            )
        }

        // Not found items section
        if (summaryByDeliveryStatus.nonTrouveSummary.itemsCount > 0) {
            SummarySection(
                title = "Articles Non Trouvés",
                summary = summaryByDeliveryStatus.nonTrouveSummary,
                icon = Icons.Default.Error,
                iconTint = Color(0xFFFF5722), // Orange/Red
                showTotal = false,
                viewModel = viewModel
            )
        }

        // Overall total (only for found items) - FIXED: Increased text size
        if (summaryByDeliveryStatus.trouveSummary.itemsCount > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total à Payer:",
                    style = MaterialTheme.typography.headlineSmall, // Increased from titleMedium
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format("%.2f", summaryByDeliveryStatus.trouveSummary.totalValue)} DA",
                    style = MaterialTheme.typography.headlineSmall, // Increased from titleMedium
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@Composable
private fun SummarySection(
    title: String,
    summary: CartSummary,
    icon: ImageVector,
    iconTint: Color,
    showTotal: Boolean,
    viewModel: ZViewModel_Sec1Frag3
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Section header
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = iconTint
            )
        }

        // Details
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "  Produits:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${summary.totalProducts} produits",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

        }
    }
}

data class DeliveryStatusSummary(
    val trouveSummary: CartSummary,
    val nonTrouveSummary: CartSummary
)

