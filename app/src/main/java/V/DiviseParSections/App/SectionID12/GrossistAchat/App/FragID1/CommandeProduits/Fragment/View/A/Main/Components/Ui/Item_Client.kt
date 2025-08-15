package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Item_Client(
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    relative_client: M2Client,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    activePeriod: M14VentPeriode?,
    activeGrossist: M15Grossist?,
    on_Pour_Dissmiss: () -> Unit
) {
    val clientPurchaseInfo = remember(
        relative_client.keyID,
        viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activePeriod?.keyID,
        activeGrossist?.keyID
    ) {
        val allBonVents = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
        val allVentOperations =
            viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
        var allAchatOperations =
            viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue

        // FIXED: Filter achat operations by active period AND grossist if they are selected
        activePeriod?.let { period ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M14VentPeriod_KeyID == period.keyID
            }
        }

        activeGrossist?.let { grossist ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M15Grossist_KeyID == grossist.keyID
            }
        }

        // Get all BonVents for this client
        val clientBonVents =
            allBonVents.filter { it.parent_M2Client_KeyID == relative_client.keyID }
        val clientBonVentIds = clientBonVents.map { it.keyID }.toSet()

        // Get all vent operations for this client
        val clientVentOperations = allVentOperations.filter {
            it.parent_M8BonVent_KeyId in clientBonVentIds
        }

        // Get unique products (M1Produit) purchased by this client (considering period and grossist)
        val relatedAchatOperations = allAchatOperations.filter { achatOperation ->
            val relatedVentOperations =
                achatOperation.get_list_v_Depuit_joinedStringKeys(clientVentOperations)
            relatedVentOperations.isNotEmpty()
        }

        val uniqueProducts = relatedAchatOperations.map {
            it.parent_M1Produit_KeyID
        }.toSet()

        // Calculate total quantity and total sales amount for this client (considering period and grossist)
        val relevantVentOperations = clientVentOperations.filter { ventOperation ->
            allAchatOperations.any { achatOperation ->
                achatOperation.get_list_v_Depuit_joinedStringKeys(listOf(ventOperation))
                    .isNotEmpty()
            }
        }

        val totalQuantity = relevantVentOperations.sumOf { it.quantity }

        val totalSalesValue = relevantVentOperations.filter {
            it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
        }.sumOf {
            val parentM13TarificationPrix =
                repositorysMainGetter.find_M13Tarification_By_KeyID(it.parentM13TarificationKeyID)?.prixCurrency
                    ?: 0.0

            it.quantity * parentM13TarificationPrix
        }

        Triple(uniqueProducts.size, totalQuantity, totalSalesValue)
    }
    val active_Central_Values = focusedValuesGetter.active_Central_Values
    val updatedValues = active_Central_Values.copy(
        active_M2Client_AuFilterAchats = relative_client
    )

    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(value = updatedValues, key = SemanticsPropertyKey("updatedValues"))
            }
            .clickable {
                focusedValuesGetter.update_activeCentralValues(updatedValues)
                on_Pour_Dissmiss()
            }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = relative_client.nom.take(2).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Client info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = relative_client.nom,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Show purchase statistics
                Text(
                    text = "${clientPurchaseInfo.first} produits • ${clientPurchaseInfo.second} articles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Show total sales amount with context
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (clientPurchaseInfo.third > 0) Icons.Default.TrendingUp else Icons.Default.AccountBalance,
                        contentDescription = "Ventes totales",
                        modifier = Modifier.size(14.dp),
                        tint = if (clientPurchaseInfo.third > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Ventes: ${String.format("%.2f", clientPurchaseInfo.third)} DA",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (clientPurchaseInfo.third > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        fontWeight = if (clientPurchaseInfo.third > 0) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // FIXED: Show context based on active filters
                    when {
                        activePeriod != null && activeGrossist != null -> {
                            Text(
                                text = "(période + grossiste actifs)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        activePeriod != null -> {
                            Text(
                                text = "(période active)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        activeGrossist != null -> {
                            Text(
                                text = "(grossiste actif)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
