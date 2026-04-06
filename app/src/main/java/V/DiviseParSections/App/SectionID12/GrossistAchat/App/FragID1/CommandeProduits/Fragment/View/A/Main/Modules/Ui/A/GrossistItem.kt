package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.W_AchatProduitOperation.View.updated_Achats
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import EntreApps.Shared.Models.Relative_Vents.Models.M15Grossist
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

@Composable
fun GrossistItem(
    grossist: M15Grossist,
    purchaseCount: Int,
    grossistCredit: Double,
    isLoadingCredit: Boolean,
    activePeriodId: String?,
    onSelect: () -> Unit,
    list_M11AchatOperation: List<M11AchatOperation> = emptyList()
) {
    val datas = updated_Achats(list_M11AchatOperation, grossist)
    var showTransactionDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .getSemanticsTag(datas, "datas")
            .clickable { onSelect() }
            .fillMaxWidth()
            .height(80.dp), // Fixed height limit as requested
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Ensure consistent height
                .padding(12.dp), // Reduced padding to fit within height constraint
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgedBox(
                badge = {
                    if (purchaseCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                purchaseCount.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp) // Slightly reduced size to fit better
                        .clip(CircleShape)
                        .background(
                            try {
                                Color(grossist.couleur_In_Str.toColorInt())
                            } catch (e: Exception) {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp) // Adjusted icon size
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp)) // Reduced spacing

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center // Center content vertically
            ) {
                Text(
                    text = grossist.nom,
                    style = MaterialTheme.typography.bodyMedium, // Slightly smaller text
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp), // Reduced spacing
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = grossist.get_DebugInfos(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false) // Allow flexible width
                    )
                    if (purchaseCount > 0) {
                        Text(
                            text = "• $purchaseCount achats${if (activePeriodId != null) " (p)" else ""}", // Shortened text
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Display total credit for this grossist
                if (isLoadingCredit) {
                    Text(
                        text = "Chargement...", // Shortened loading text
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp) // Reduced spacing
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = "Crédit",
                            modifier = Modifier.size(12.dp), // Smaller icon
                            tint = if (grossistCredit > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "${String.format("%.0f", grossistCredit)} DA", // Removed decimal places to save space
                            style = MaterialTheme.typography.bodySmall,
                            color = if (grossistCredit > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            fontWeight = if (grossistCredit > 0) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            IconButton(
                onClick = { showTransactionDialog = true },
                modifier = Modifier.size(36.dp) // Smaller button to fit better
            ) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = "Voir les transactions",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showTransactionDialog) {
        TransactionDialog(
            grossist = grossist,
            onDismiss = { showTransactionDialog = false }
        )
    }
}
