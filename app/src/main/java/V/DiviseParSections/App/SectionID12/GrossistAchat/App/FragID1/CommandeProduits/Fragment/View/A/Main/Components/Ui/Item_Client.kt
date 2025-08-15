package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
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
    val TAG = "Item_Client"

    // Log current state before any interaction
    LaunchedEffect(Unit) {
        Log.d(TAG, "=== Item_Client Initialized ===")
        Log.d(TAG, "Client: ${relative_client.nom} (ID: ${relative_client.keyID})")
        Log.d(TAG, "Current active client filter: ${focusedValuesGetter.active_Central_Values.active_M2Client_AuFilterAchats?.nom ?: "NONE"}")
        Log.d(TAG, "Active period: ${activePeriod?.let { "ID: ${it.keyID}" } ?: "NONE"}")
        Log.d(TAG, "Active grossist: ${activeGrossist?.let { "ID: ${it.keyID}" } ?: "NONE"}")
        Log.d(TAG, "FocusedValuesGetter instance: ${focusedValuesGetter.hashCode()}")
    }

    // Monitor state changes
    val currentClientFilter = focusedValuesGetter.active_Central_Values.active_M2Client_AuFilterAchats
    LaunchedEffect(currentClientFilter) {
        Log.d(TAG, ">>> Client filter changed to: ${currentClientFilter?.nom ?: "NULL"}")
        if (currentClientFilter != null) {
            Log.d(TAG, "    - Client ID: ${currentClientFilter.keyID}")
            Log.d(TAG, "    - Is same as current client? ${currentClientFilter.keyID == relative_client.keyID}")
        }
    }

    val clientPurchaseInfo = remember(
        relative_client.keyID,
        viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activePeriod?.keyID,
        activeGrossist?.keyID
    ) {
        Log.d(TAG, "Recalculating purchase info for client: ${relative_client.nom}")

        val allBonVents = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
        val allVentOperations =
            viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
        var allAchatOperations =
            viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue

        Log.d(TAG, "Initial data counts:")
        Log.d(TAG, "  - BonVents: ${allBonVents.size}")
        Log.d(TAG, "  - VentOperations: ${allVentOperations.size}")
        Log.d(TAG, "  - AchatOperations: ${allAchatOperations.size}")

        activePeriod?.let { period ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M14VentPeriod_KeyID == period.keyID
            }
            Log.d(TAG, "  - AchatOperations after period filter: ${allAchatOperations.size}")
        }

        activeGrossist?.let { grossist ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M15Grossist_KeyID == grossist.keyID
            }
            Log.d(TAG, "  - AchatOperations after grossist filter: ${allAchatOperations.size}")
        }

        // Get all BonVents for this client
        val clientBonVents =
            allBonVents.filter { it.parent_M2Client_KeyID == relative_client.keyID }
        val clientBonVentIds = clientBonVents.map { it.keyID }.toSet()
        Log.d(TAG, "  - Client BonVents: ${clientBonVents.size}")

        // Get all vent operations for this client
        val clientVentOperations = allVentOperations.filter {
            it.parent_M8BonVent_KeyId in clientBonVentIds
        }
        Log.d(TAG, "  - Client VentOperations: ${clientVentOperations.size}")

        // Get unique products (M1Produit) purchased by this client (considering period and grossist)
        val relatedAchatOperations = allAchatOperations.filter { achatOperation ->
            val relatedVentOperations =
                achatOperation.get_list_v_Depuit_joinedStringKeys(clientVentOperations)
            relatedVentOperations.isNotEmpty()
        }

        val uniqueProducts = relatedAchatOperations.map {
            it.parent_M1Produit_KeyID
        }.toSet()
        Log.d(TAG, "  - Unique products: ${uniqueProducts.size}")

        // Calculate total quantity and total sales amount for this client (considering period and grossist)
        val relevantVentOperations = clientVentOperations.filter { ventOperation ->
            allAchatOperations.any { achatOperation ->
                achatOperation.get_list_v_Depuit_joinedStringKeys(listOf(ventOperation))
                    .isNotEmpty()
            }
        }

        val totalQuantity = relevantVentOperations.sumOf { it.quantity }
        Log.d(TAG, "  - Total quantity: $totalQuantity")

        val filter_relevantVentOperations = relevantVentOperations.filter {
            it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
        }
        val totalSalesValue = filter_relevantVentOperations.sumOf {
            val parentM13TarificationPrix =
                repositorysMainGetter.find_M13Tarification_By_KeyID(it.parentM13TarificationKeyID)?.prixCurrency
                    ?: 0.0

            it.quantity * parentM13TarificationPrix
        }
        Log.d(TAG, "  - Total sales value: $totalSalesValue")

        Triple(uniqueProducts.size, totalQuantity, totalSalesValue)
    }
    val client_Vents = remember(
        relative_client.keyID,
        viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activePeriod?.keyID,
    ) {
        Log.d(TAG, "Recalculating purchase info for client: ${relative_client.nom}")

        val allBonVents = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
        val allVentOperations =
            viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
        var allAchatOperations =
            viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue

        Log.d(TAG, "Initial data counts:")
        Log.d(TAG, "  - BonVents: ${allBonVents.size}")
        Log.d(TAG, "  - VentOperations: ${allVentOperations.size}")
        Log.d(TAG, "  - AchatOperations: ${allAchatOperations.size}")

        activePeriod?.let { period ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M14VentPeriod_KeyID == period.keyID
            }
            Log.d(TAG, "  - AchatOperations after period filter: ${allAchatOperations.size}")
        }

        activeGrossist?.let { grossist ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M15Grossist_KeyID == grossist.keyID
            }
            Log.d(TAG, "  - AchatOperations after grossist filter: ${allAchatOperations.size}")
        }

        val clientBonVents =
            allBonVents.filter { it.parent_M2Client_KeyID == relative_client.keyID }
        val clientBonVentIds = clientBonVents.map { it.keyID }.toSet()

        val clientVentOperations = allVentOperations.filter {
            it.parent_M8BonVent_KeyId in clientBonVentIds
        }

        val relevantVentOperations = clientVentOperations.filter { ventOperation ->
            allAchatOperations.any { achatOperation ->
                achatOperation.get_list_v_Depuit_joinedStringKeys(listOf(ventOperation))
                    .isNotEmpty()
            }
        }

        val filter_relevantVentOperations = relevantVentOperations.filter {
            it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
        }

        filter_relevantVentOperations
    }

    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(value = client_Vents, key = SemanticsPropertyKey("client_Vents"))
            }
            .clickable {
                Log.d(TAG, "=== CLICK EVENT TRIGGERED ===")
                Log.d(TAG, "About to add client filter for: ${relative_client.nom}")
                Log.d(TAG, "Client ID: ${relative_client.keyID}")
                Log.d(TAG, "Current state BEFORE update:")
                Log.d(TAG, "  - Current client filter: ${focusedValuesGetter.active_Central_Values.active_M2Client_AuFilterAchats?.nom ?: "NULL"}")
                Log.d(TAG, "  - Current period filter: ${focusedValuesGetter.active_Central_Values.active_M14VentPeriode_AuFilterAchats?.keyID ?: "NULL"}")
                Log.d(TAG, "  - Current grossist filter: ${focusedValuesGetter.active_Central_Values.active_M15Grossist_AuFilterAchats?.keyID ?: "NULL"}")

                try {
                    // Test if the method exists and is accessible
                    Log.d(TAG, "Calling focusedValuesGetter.addClientFilter()...")
                    focusedValuesGetter.addClientFilter(relative_client)
                    Log.d(TAG, "✓ addClientFilter() called successfully")

                    // Check state immediately after call
                    Log.d(TAG, "Current state AFTER update:")
                    Log.d(TAG, "  - Current client filter: ${focusedValuesGetter.active_Central_Values.active_M2Client_AuFilterAchats?.nom ?: "NULL"}")
                    Log.d(TAG, "  - Current client filter ID: ${focusedValuesGetter.active_Central_Values.active_M2Client_AuFilterAchats?.keyID ?: "NULL"}")

                    // Verify the update worked
                    val wasUpdated = focusedValuesGetter.active_Central_Values.active_M2Client_AuFilterAchats?.keyID == relative_client.keyID
                    Log.d(TAG, "Update verification: ${if (wasUpdated) "SUCCESS" else "FAILED"}")

                    if (!wasUpdated) {
                        Log.e(TAG, "⚠️ CLIENT FILTER NOT UPDATED PROPERLY!")
                        Log.e(TAG, "Expected: ${relative_client.keyID}")
                        Log.e(TAG, "Actual: ${focusedValuesGetter.active_Central_Values.active_M2Client_AuFilterAchats?.keyID}")
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "❌ Exception during addClientFilter(): ${e.message}")
                    Log.e(TAG, "Exception details: ", e)
                }

                Log.d(TAG, "Calling on_Pour_Dissmiss()...")
                on_Pour_Dissmiss()
                Log.d(TAG, "=== CLICK EVENT COMPLETED ===")
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

                    // Show context based on active filters
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
