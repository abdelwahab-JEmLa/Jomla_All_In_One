package Application4.App.Fragment.View.ViewS

import Application4.App.Fragment.ID1.Fragment.ViewModel.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Compact_Header_FragID3(
    relative_M1produit: M01Produit,
    isExpanded: Boolean,
    shouldShowButtons: Boolean = false,
    onUpdateTariffContext: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, ViewModel_NewProtoPatterns>
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel
    val centralValues = uiState.active_Central_Values

    // Sourced from uiState — no koinInject needed
    val isAdmin = remember(centralValues.currentApp_Est_Admin) {
        centralValues.currentApp_Est_Admin
    }

    // Dynamic text sizes based on expansion state
    val nameTextSize = if (isExpanded) 14.sp else 10.sp
    val arabicTextSize = if (isExpanded) 12.sp else 9.sp
    val labelTextSize = if (isExpanded) 10.sp else 7.sp
    val valueTextSize = if (isExpanded) 12.sp else 9.sp
    val iconSize = if (isExpanded) 14.dp else 10.dp
    val cardPadding = if (isExpanded) 6.dp else 3.dp
    val itemPadding = if (isExpanded) 4.dp else 2.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (isExpanded) 8.dp else 6.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 2.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(itemPadding)
        ) {
            // Delete button - only visible for admin users
            if (shouldShowButtons && isAdmin) {
                DeleteProductHeader(
                    productName = relative_M1produit.nom,
                    onDelete = {
                        viewModel.update_m1Produit(
                            relative_M1produit.copy(
                                dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                            )
                        )
                        // Delegate actual deletion to ViewModel
                        viewModel.delete_m1Produit(relative_M1produit)
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = relative_M1produit.nom,
                        fontSize = nameTextSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = if (isExpanded) 16.sp else 12.sp
                    )

                    if (relative_M1produit.nomArab.isNotBlank() && isExpanded) {
                        Text(
                            text = relative_M1produit.nomArab,
                            fontSize = arabicTextSize,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Second row: Info cards in FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(itemPadding),
                verticalArrangement = Arrangement.spacedBy(itemPadding)
            ) {
                // Update tariff context button — shown first if available, admin only
                if (isAdmin) {
                    if (onUpdateTariffContext == null) {
                        Log.d(
                            "Compact_Header_FragID3",
                            "ClickableInfoCard 'Tarif' non affiché : onUpdateTariffContext est null " +
                                    "(currentApp_Est_Admin=false ou activeCompt=null)"
                        )
                    }
                    ClickableInfoCard(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Update Tariff",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(iconSize)
                            )
                        },
                        value = "↻",
                        label = "Tarif",
                        labelTextSize = labelTextSize,
                        valueTextSize = valueTextSize,
                        itemPadding = itemPadding,
                        onClick = onUpdateTariffContext
                    )
                }

                // Number of units card - only show if > 1
                if (relative_M1produit.nombreUniteInt > 1) {
                    InfoCard(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ViewModule,
                                contentDescription = "Units",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(iconSize)
                            )
                        },
                        value = "${relative_M1produit.nombreUniteInt}",
                        label = "U",
                        labelTextSize = labelTextSize,
                        valueTextSize = valueTextSize,
                        itemPadding = itemPadding
                    )
                }

                // Carton quantity card - only show if > 1
                if (relative_M1produit.quantite_Boit_Par_Carton > 1) {
                    InfoCard(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "Carton",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(iconSize)
                            )
                        },
                        value = "${relative_M1produit.quantite_Boit_Par_Carton}",
                        label = "C",
                        labelTextSize = labelTextSize,
                        valueTextSize = valueTextSize,
                        itemPadding = itemPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    labelTextSize: TextUnit,
    valueTextSize: TextUnit,
    itemPadding: Dp,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = itemPadding + 2.dp, vertical = itemPadding),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    fontSize = labelTextSize,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                    lineHeight = labelTextSize
                )
                Text(
                    text = value,
                    fontSize = valueTextSize,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    lineHeight = valueTextSize
                )
            }
        }
    }
}

@Composable
private fun ClickableInfoCard(
    icon: @Composable (() -> Unit),
    value: String,
    label: String,
    labelTextSize: TextUnit,
    valueTextSize: TextUnit,
    itemPadding: Dp,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val isEnabled = onClick != null
    Card(
        modifier = modifier
            .then(if (isEnabled) Modifier.clickable(onClick = onClick!!) else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled)
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isEnabled) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = itemPadding + 2.dp, vertical = itemPadding),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    fontSize = labelTextSize,
                    color = if (isEnabled)
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                    fontWeight = FontWeight.Medium,
                    lineHeight = labelTextSize
                )
                Text(
                    text = value,
                    fontSize = valueTextSize,
                    color = if (isEnabled)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                    fontWeight = FontWeight.Bold,
                    lineHeight = valueTextSize
                )
            }
        }
    }
}
