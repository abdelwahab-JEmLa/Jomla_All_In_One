package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.annotation.SuppressLint
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

@SuppressLint("DefaultLocale")
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

            val totalValue = ventsTrouve.sumOf {
                val provisoireMonPrix =
                    viewModel.aCentralFacade.repoMainGetter.m13Tarification_By_KeyID(it.keyID)
                        ?.prixCurrency
                        ?: 0.0

                it.quantity * provisoireMonPrix
            }


            DeliveryStatusSummary(
                trouveSummary = CartSummary(
                    totalItems = ventsTrouve.sumOf { it.quantity },
                    totalProducts = ventsTrouve.groupBy { it.parent_M1Produit_KeyId }.size,
                    totalValue = totalValue,
                    itemsCount = ventsTrouve.size
                ),
                nonTrouveSummary = CartSummary(
                    totalItems = ventsNonTrouve.sumOf { it.quantity },
                    totalProducts = ventsNonTrouve.groupBy { it.parent_M1Produit_KeyId }.size,
                    totalValue = ventsNonTrouve.sumOf { it.quantity * it.provisoireMonPrix },
                    itemsCount = ventsNonTrouve.size
                )
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if (summaryByDeliveryStatus.trouveSummary.itemsCount > 0) {
            SummarySection(
                title = "Articles Trouvés",
                summary = summaryByDeliveryStatus.trouveSummary,
                icon = Icons.Default.CheckCircle,
                iconTint = Color(0xFF4CAF50) // Green
            )
        }

        if (summaryByDeliveryStatus.nonTrouveSummary.itemsCount > 0) {
            SummarySection(
                title = "Articles Non Trouvés",
                summary = summaryByDeliveryStatus.nonTrouveSummary,
                icon = Icons.Default.Error,
                iconTint = Color(0xFFFF5722) // Orange/Red
            )
        }

        if (summaryByDeliveryStatus.trouveSummary.itemsCount > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total à Payer:",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                val ventsTrouve = repo.onVentFilteredDatas.filter {
                    it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
                }
                val totalValue = ventsTrouve.sumOf {
                    val provisoireMonPrix =
                        viewModel.aCentralFacade.repoMainGetter.m13Tarification_By_KeyID(it.parentM13TarificationKeyID)
                            ?.prixCurrency
                            ?: 0.0

                    it.quantity * provisoireMonPrix
                }

                Text(
                    modifier = Modifier.getSemanticsTag(ventsTrouve, "ventsTrouve"),
                    text = "${
                        String.format(
                            "%.2f",
                            totalValue
                        )
                    } DA",
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

