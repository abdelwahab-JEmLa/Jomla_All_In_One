package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.V1ProductHeader_T1.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnrememberedMutableState")
@Composable
fun Card_Produit_Nombre_Unites(
    allNonTrouve: Boolean,
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    onClick_PourOuvrireDialog: () -> Unit
) {      
    // Track toggle state for afficheUniteAuPrint
    var toggleState by remember { mutableStateOf(produit.afficheUniteAuPrint) }

    // Track dialog state for vent operations
    var shouldShowVentOperationsDialog by remember { mutableStateOf(false) }

    val list_M10OperationVentCouleurs_By_M1Produit by derivedStateOf {
        viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { ventOperation ->
            ventOperation.parent_M1Produit_KeyId == produit.keyID
        }
    }

    fun clickHandel() {
        // Toggle the state
        toggleState = !toggleState

        // Update the product with the new state
        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(
            produit.copy(
                afficheUniteAuPrint = toggleState
            )
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allNonTrouve) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(start = petitePaddine)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(petitePaddine),
            modifier = Modifier.padding(petitePaddine)
        ) {
            // Vent operations button - shows count badge if operations exist
            if (list_M10OperationVentCouleurs_By_M1Produit.isNotEmpty()) {
                IconButton(
                    onClick = { shouldShowVentOperationsDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Badge(
                        containerColor = if (allNonTrouve)
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "${list_M10OperationVentCouleurs_By_M1Produit.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Original quantity display button
            IconButton(
                onClick = { onClick_PourOuvrireDialog() },
                modifier = Modifier
                    .width(50.dp)
                    .height(36.dp)
            ) {
                Row {
                    Text(
                        text = "Nbr.U ",
                        fontSize = 8.sp,
                        color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f
                        )
                        else MaterialTheme.colorScheme.tertiary,
                    )
                    Text(
                        text = "${produit.nombreUniteInt}",
                        fontSize = 15.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Toggle button for afficheUniteAuPrint - only show when not in grid mode
            ActiveCentralValues.get_Default().affiche_Produit_OnGrid.ifFalse {
                IconButton(
                    onClick = { clickHandel() },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (toggleState) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    Icon(
                        imageVector = if (toggleState) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = if (toggleState) "Print units enabled" else "Print units disabled",
                        tint = if (toggleState) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // Dialog showing vent operations
    if (shouldShowVentOperationsDialog) {
        Dialog_VentOperations_ForProduct(
            produit = produit,
            ventOperations = list_M10OperationVentCouleurs_By_M1Produit,
            viewModel = viewModel,
            onDismiss = { shouldShowVentOperationsDialog = false }
        )
    }
}
@Composable
fun Dialog_VentOperations_ForProduct(
    produit: ArticlesBasesStatsTable,
    ventOperations: List<M10OperationVentCouleur>,
    viewModel: ViewModelsProduit_T1,
    onDismiss: () -> Unit
) {
    val repositorysMainGetter = viewModel.aCentralFacade.repositorysMainGetter

    // Sort vent operations by creation timestamp - most recent first
    val sortedVentOperations = remember(ventOperations) {
        ventOperations.sortedByDescending { it.creationTimestamps }
    }

    // Get bon vents map for looking up parent bon vent info
    val bonVentsMap = repositorysMainGetter.repo8BonVent.datasValue
        .associateBy { it.keyID }

    // Calculate total quantities by delivery status
    val totalTrouve = ventOperations
        .filter { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve }
        .sumOf { it.quantity }

    val totalNonTrouve = ventOperations
        .filter { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
        .sumOf { it.quantity }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Vent operations",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )

                    Text(
                        text = "${ventOperations.size} opération${if (ventOperations.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = produit.nom,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Summary row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Trouvé summary
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Trouvé",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$totalTrouve",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Non trouvé summary
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Non trouvé",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "$totalNonTrouve",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sortedVentOperations) { operation ->
                    val bonVent = bonVentsMap[operation.parent_M8BonVent_KeyId]

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when (operation.etateDelivery) {
                                M10OperationVentCouleur.EtateDelivery.Trouve ->
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                M10OperationVentCouleur.EtateDelivery.NonTrouve ->
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Header row with client and quantity
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bonVent?.parent_M2Client_DebugInfos ?: "Client non trouvé",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Bon: ${operation.parent_M8BonVent_KeyId.takeLast(4).uppercase()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Quantity display
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = when (operation.etateDelivery) {
                                            M10OperationVentCouleur.EtateDelivery.Trouve ->
                                                MaterialTheme.colorScheme.primary
                                            M10OperationVentCouleur.EtateDelivery.NonTrouve ->
                                                MaterialTheme.colorScheme.error
                                        }
                                    )
                                ) {
                                    Text(
                                        text = "${operation.quantity}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )

                            // Details section
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // State
                                DetailRow(
                                    label = "État:",
                                    value = operation.etateActuellementEst.name.replace("_", " ")
                                )

                                // Quantity type
                                DetailRow(
                                    label = "Type:",
                                    value = when (operation.setIN_Vent_Its_Quantity_Represent) {
                                        M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit ->
                                            "Par Boîte"
                                        M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton ->
                                            "Par Carton"
                                    }
                                )

                                // Creation date
                                DetailRow(
                                    label = "Créé:",
                                    value = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                        .format(Date(operation.creationTimestamps))
                                )

                                // Color info if available
                                if (operation.parent_M3CouleurProduit_DebugInfos.isNotBlank() &&
                                    operation.parent_M3CouleurProduit_DebugInfos != "null") {
                                    DetailRow(
                                        label = "Couleur:",
                                        value = operation.parent_M3CouleurProduit_DebugInfos
                                    )
                                }
                            }

                            // Delivery status badge
                            if (operation.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "⚠️ Non trouvé",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onError,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Fermer",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}
