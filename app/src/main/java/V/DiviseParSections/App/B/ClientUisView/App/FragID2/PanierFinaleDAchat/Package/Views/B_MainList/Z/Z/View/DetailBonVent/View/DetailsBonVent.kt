package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.Z.View.DetailBonVent.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.ZViewModel_Sec1Frag3
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Preview
@Composable
fun DetailsBonVentPrev() {
    DetailsBonVent()
}

@Composable
fun DetailsBonVent(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinInject()
) {
    var isMinimized by remember { mutableStateOf(false) }

    val zAppComptRepositoryComposable =
        viewModel.uiStateCentralRepositorys.zAppComptRepositoryComposable
    val comptAppActuelle = zAppComptRepositoryComposable.ouvertData
    val clientsRepository = viewModel.uiStateCentralRepositorys.bClientsStateCompoRepository
    val achatsRepository =
        viewModel.uiStateCentralRepositorys.fCouleurAchatOperationRepositoryComposable

    val ouvertF2BonVentId = comptAppActuelle?.ouvertF2BonVentId ?: ""
    val ouvertClientId = comptAppActuelle?.ouvertClientOnVentAncienId.toString() ?: ""
    val ouvertClientNom = comptAppActuelle?.ouvertClientOnVentNom ?: ""
    val ouvertPeriodId = comptAppActuelle?.ouvertF1PeriodVentId ?: ""
    val ouvertPeriodStartTime = comptAppActuelle?.ouvertF1PeriodVentStartTimesTamp ?: 0L

    val clientDetails by remember(ouvertClientId) {
        derivedStateOf {
            if (ouvertClientId.isNotEmpty()) {
                ouvertClientId.toLongOrNull()?.let { clientId ->
                    clientsRepository.findClientById(clientId)
                }
            } else null
        }
    }

    val cartSummary by remember {
        derivedStateOf {
            val achats = achatsRepository.filteredDatasValue.filter {
                it.parentBonVentId == ouvertF2BonVentId
            }
            CartSummary(
                totalItems = achats.sumOf { it.quantityAchete },
                totalProducts = achats.groupBy { it.parentProduitId }.size,
                totalValue = achats.sumOf { it.quantityAchete * it.provisoireMonPrix },
                itemsCount = achats.size
            )
        }
    }

    if (comptAppActuelle != null) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isMinimized = !isMinimized },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = if (isMinimized) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isMinimized) "Afficher détails" else "Masquer détails"
                    )
                }
            }
        ) { paddingValues ->
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(paddingValues),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isMinimized) {
                        // Header - Only when expanded
                        BonVentHeader(ouvertF2BonVentId)

                        // Summary - Only when expanded

                    }
                    if (!isMinimized) {

                        PeriodDetailsSection(
                            ouvertPeriodId = ouvertPeriodId,
                            ouvertPeriodStartTime = ouvertPeriodStartTime
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Client details - Always visible
                    ClientDetailsSection(
                        ouvertClientNom = ouvertClientNom,
                        ouvertClientId = ouvertClientId,
                        clientDetails = clientDetails
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    CartSummarySection(cartSummary)


                }
            }
        }
    } else {
        ErrorCard(modifier = modifier)
    }
}

@Composable
fun ClientDetailsSection(
    ouvertClientNom: String,
    ouvertClientId: String,
    clientDetails: B_ClientInfosProtoJuin3? // Replace with actual client type
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Client",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Client",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = ouvertClientNom.ifEmpty { "Client non sélectionné" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            val client = clientDetails
            if (client != null) {
                // Client details display code - replace with actual client properties
                Text(
                    text = "ID: ${ouvertClientId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Add other client details as needed based on your client data class

            } else if (ouvertClientId.isNotEmpty()) {
                Text(
                    text = "ID: $ouvertClientId",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

