package Application4.App.Fragment.View.ViewS

import Application4.App.Fragment.ID1.Fragment.ViewModel.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import Application4.App.Fragment.View.ViewS.Views.Image_Displaye
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS.DeleteProductHeader
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS.FastInit_Outlined_Int_Edite_Modulable_Proto4
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Compact_Header_FragID4(
    modifier: Modifier = Modifier,
    relative_M1produit: M01Produit,
    isExpanded: Boolean,
    onUpdateTariff: () -> Unit,
    onUpdateProduit: (M01Produit) -> Unit,
    affiche_ProduitDataBaseEdites_ComposableViews: Boolean,
    shouldShowButtons: Boolean = affiche_ProduitDataBaseEdites_ComposableViews,
    onDelete: (M01Produit) -> Unit
) {
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
                // Delete button - only visible for admin users
                if (shouldShowButtons && affiche_ProduitDataBaseEdites_ComposableViews) {
                    DeleteProductHeader(
                        productName = relative_M1produit.nom,
                        onDelete = {
                            onDelete(relative_M1produit)
                        }
                    )
                }

                // FIXED: Update tariff context button as InfoCard - shown first if available
                if (shouldShowButtons && affiche_ProduitDataBaseEdites_ComposableViews) {
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
                        onClick = onUpdateTariff
                    )
                }

                // Number of units card - admin: InfoCard pill that opens FastInit on click
                if (relative_M1produit.nombreUniteInt > 1 || affiche_ProduitDataBaseEdites_ComposableViews) {
                    if (affiche_ProduitDataBaseEdites_ComposableViews) {
                        EditableInfoCard(
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
                            itemPadding = itemPadding,
                            startCount = relative_M1produit.nombreUniteInt,
                            isExpanded = isExpanded,
                            onUpdate = { new ->
                                onUpdateProduit(
                                    relative_M1produit
                                        .copy(
                                            nombreUniteInt = new
                                        )
                                )
                            }
                        )
                    } else {
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
                }

                // Carton quantity card - admin: InfoCard pill that opens FastInit on click
                if (relative_M1produit.quantite_Boit_Par_Carton > 1 || affiche_ProduitDataBaseEdites_ComposableViews) {
                    if (affiche_ProduitDataBaseEdites_ComposableViews) {
                        EditableInfoCard(
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
                            itemPadding = itemPadding,
                            startCount = relative_M1produit.quantite_Boit_Par_Carton,
                            isExpanded = isExpanded,
                            onUpdate = { new ->
                                onUpdateProduit(
                                    relative_M1produit
                                        .copy(
                                            quantite_Boit_Par_Carton = new
                                        )
                                )
                            }
                        )
                    } else {
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
}

/**
 * Affiche une InfoCard pill normale.
 * Au clic, se remplace directement par le OutlinedTextField de FastInit
 * (force_edit_mode_on_start = true — pas de pill intermédiaire FastInit).
 * Dès que l'utilisateur confirme (Done), repasse en mode pill.
 */
@Composable
private fun EditableInfoCard(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    labelTextSize: TextUnit,
    valueTextSize: TextUnit,
    itemPadding: Dp,
    startCount: Int,
    isExpanded: Boolean,
    onUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }

    if (isEditing) {
        FastInit_Outlined_Int_Edite_Modulable_Proto4(
            start_count = startCount,
            standard_count = 1,
            force_edit_mode_on_start = true,
            isAvailable = true,
            compact_taille = !isExpanded,
            is_admin = true,
            modifier = modifier,
            on_Data_Update = { newValue ->
                onUpdate(newValue)
                isEditing = false
            }
        )
    } else {
        Card(
            modifier = modifier.clickable { isEditing = true },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = itemPadding + 2.dp,
                    vertical = itemPadding
                ),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    labelTextSize: TextUnit,
    valueTextSize: TextUnit,
    itemPadding: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    lineHeight = labelTextSize
                )
                Text(
                    text = value,
                    fontSize = valueTextSize,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    lineHeight = valueTextSize
                )
            }
        }
    }
}

@Composable
fun ColorImageCard_FragID3(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    isSelected: Boolean,
    on_pour_send_data: (String, String) -> Unit,
    modifier: Modifier = Modifier.Companion,
    roundedCorners: RoundedCornerShape = RoundedCornerShape(12.dp), // Default: all corners rounded
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, ViewModel_NewProtoPatterns>
) {
    val elevation = if (isSelected) 4.dp else 2.dp

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = roundedCorners
    ) {
        Box(
            modifier = if (isSelected) {
                // Selected (main color): use fixed aspect ratio
                Modifier.Companion
                    .fillMaxWidth()
                    .aspectRatio(370.dp / 500.dp)
            } else {
                // Sub-color: wrap to content height
                Modifier.Companion
                    .fillMaxWidth()
                    .wrapContentHeight()
            }
        ) {
            // Image always clickable
            Image_Displaye(
                uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                contentScale = if (isSelected) ContentScale.Companion.Fit else ContentScale.Companion.Crop,
                modifier = Modifier.Companion,
                on_pour_send_data = on_pour_send_data
            )
        }
    }
}

/**
 * Header component with delete button that requires double-click with 4-second countdown
 *
 * Features:
 * - First click: Starts 4-second countdown
 * - Second click: Confirms deletion
 * - Right-side click during countdown: Cancels operation
 * - Auto-resets if countdown expires
 *
 * Usage:
 * DeleteProductHeader(
 *     productName = product.nom,
 *     onDelete = {
 *         // Your delete logic here
 *         repositorysMainGetter.repoM1Produit.deleteData(product)
 *     }
 * )
 */
@Composable
fun DeleteProductHeader(
    productName: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCountdownActive by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableIntStateOf(4) }
    var progress by remember { mutableFloatStateOf(1f) }

    // Smooth countdown animation
    LaunchedEffect(isCountdownActive) {
        if (isCountdownActive) {
            countdownSeconds = 4
            progress = 1f
            val totalMillis = 4000f
            val step = 50L // Update every 50ms for smooth animation
            var elapsed = 0L

            while (elapsed < totalMillis && isCountdownActive) {
                delay(step)
                elapsed += step
                progress = 1f - (elapsed / totalMillis)
                countdownSeconds = ((totalMillis - elapsed) / 1000).toInt() + 1
            }

            // Reset if countdown expires
            if (elapsed >= totalMillis) {
                isCountdownActive = false
                progress = 1f
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    !isCountdownActive -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                }
            )
            .clickable {
                if (!isCountdownActive) {
                    // First click: Start countdown
                    isCountdownActive = true
                } else {
                    // Second click during countdown: Execute deletion
                    isCountdownActive = false
                    onDelete()
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = when {
                !isCountdownActive -> "🗑️ Supprimer \"$productName\""
                else -> "👆 Cliquer pour confirmer"
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = when {
                !isCountdownActive -> MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.colorScheme.onError
            },
            modifier = Modifier.weight(1f)
        )

        if (isCountdownActive) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular countdown indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .drawBehind {
                            val strokeWidth = 4.dp.toPx()
                            // Background circle
                            drawCircle(
                                color = Color.Companion.White.copy(alpha = 0.3f),
                                style = Stroke(width = strokeWidth)
                            )
                            // Progress arc
                            drawArc(
                                color = Color.Companion.White,
                                startAngle = -90f,
                                sweepAngle = 360f * progress,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Companion.Round)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$countdownSeconds",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onError
                    )
                }

                // Cancel button - more appealing
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onError.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { isCountdownActive = false }
                        .background(MaterialTheme.colorScheme.onError.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}
