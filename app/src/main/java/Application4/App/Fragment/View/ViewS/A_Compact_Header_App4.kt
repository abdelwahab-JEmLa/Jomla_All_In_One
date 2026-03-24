package Application4.App.Fragment.View.ViewS

import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.Prioriter
import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import Application4.App.Fragment.View.ViewS.Views.Image_Displaye
import EntreApps.Shared.Compose_Injectable_Sepecialise.Kotlin.ID1.EditeBaseDonne.Package.M16Categorie.CategoryBadge
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS.DeleteProductHeader
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS.FastInit_Outlined_Int_Edite_Modulable_Proto4
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun A_Compact_Header_App4(
    modifier: Modifier = Modifier,
    relative_M1produit: M01Produit,
    isExpanded: Boolean,
    onUpdateTariff: () -> Unit,
    onUpdateProduit: (M01Produit) -> Unit,
    affiche_ProduitDataBaseEdites_ComposableViews: Boolean,
    shouldShowButtons: Boolean = affiche_ProduitDataBaseEdites_ComposableViews,
    onDelete: (M01Produit) -> Unit,
    // TODO(1) FIXED: CategoryBadge moved here from A_Item_Produit_App4.
    // Pass nulls (default) to hide the badge entirely (e.g. non-admin callers).
    catalogueName: String? = null,
    categoryName: String? = null,
    onCategoryClick: (() -> Unit)? = null,
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
            // ── Priorité tag toggles (admin only) ─────────────────────────
            if (affiche_ProduitDataBaseEdites_ComposableViews) {
                Section_ToggleButton_TagPreiorities(
                    produit = relative_M1produit,
                    affiche_ProduitDataBaseEdites_ComposableViews = affiche_ProduitDataBaseEdites_ComposableViews,
                    onAddDeleteTag_ToUpdate = { updatedProduit -> onUpdateProduit(updatedProduit) }
                )
            }

            // ── TODO(1) FIXED: CategoryBadge now lives here ───────────────
            // Shown only when the caller supplies an onCategoryClick handler
            // (admin context). The badge itself handles the "Non Définie" case.
            if (onCategoryClick != null) {
                CategoryBadge(
                    catalogueName = catalogueName,
                    categoryName = categoryName,
                    onClick = onCategoryClick,
                    modifier = Modifier.padding(bottom = 4.dp)
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

// =============================================================================
// Section_ToggleButton_TagPreiorities
// =============================================================================

/**
 * A compact pill button that expands on click to show one [FilterChip] per [Prioriter] value.
 * Active tags are read from [produit.produit_set_Tag_Priorite()].
 * Each chip click adds/removes the tag and calls [onAddDeleteTag_ToUpdate] with the updated product.
 * Hidden entirely when [affiche_ProduitDataBaseEdites_ComposableViews] is false.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Section_ToggleButton_TagPreiorities(
    produit: M01Produit,
    affiche_ProduitDataBaseEdites_ComposableViews: Boolean,
    onAddDeleteTag_ToUpdate: (M01Produit) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!affiche_ProduitDataBaseEdites_ComposableViews) return

    var expanded by remember { mutableStateOf(false) }
    val activeTags = remember(produit.tag_prioriter_str) { produit.produit_set_Tag_Priorite() }
    val hasAnyTag = activeTags.isNotEmpty()

    Column(modifier = modifier.fillMaxWidth()) {
        // ── Pill trigger button ───────────────────────────────────────────
        Card(
            modifier = Modifier.clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(
                containerColor = if (hasAnyTag)
                    MaterialTheme.colorScheme.tertiaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = "Tags priorité",
                    tint = if (hasAnyTag)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = if (hasAnyTag)
                        activeTags.joinToString(" · ") { it.label() }
                    else
                        "Tags priorité",
                    fontSize = 8.sp,
                    fontWeight = if (hasAnyTag) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (hasAnyTag)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ── Expanded chip row ─────────────────────────────────────────────
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Prioriter.entries.forEach { prioriter ->
                    val isSelected = prioriter in activeTags
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            // Toggle tag in the set, serialize back into product, propagate
                            val newTags = activeTags.toMutableSet().apply {
                                if (isSelected) remove(prioriter) else add(prioriter)
                            }
                            val updatedProduit = produit.setReturn_Produit_Ac_tag_prioriter_str(
                                produit_set_Tag_Priorite = newTags,
                                produit = produit
                            )
                            onAddDeleteTag_ToUpdate(updatedProduit)
                        },
                        label = {
                            Text(
                                text = prioriter.label(),
                                fontSize = 8.sp,
                                lineHeight = 10.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                }
            }
        }
    }
}

/** Short human-readable label for each Prioriter value. */
private fun Prioriter.label(): String = when (this) {
    Prioriter.Dernier_VentAchat_Est_Moin_Mois  -> "< Mois"
    Prioriter.Dernier_VentAchat_Est_Moin_Semain -> "< Sem"
    Prioriter.PlusDe80P_Ne_Le_Voit_Pas          -> "80%"
    else -> {""}
}

// =============================================================================
// Private card helpers (unchanged)
// =============================================================================

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
    roundedCorners: RoundedCornerShape = RoundedCornerShape(12.dp),
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
                Modifier.Companion
                    .fillMaxWidth()
                    .aspectRatio(370.dp / 500.dp)
            } else {
                Modifier.Companion
                    .fillMaxWidth()
                    .wrapContentHeight()
            }
        ) {
            Image_Displaye(
                uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                contentScale = if (isSelected) ContentScale.Companion.Fit else ContentScale.Companion.Crop,
                modifier = Modifier.Companion,
                list_M1Produit = uiState_NewProtoPatterns_viewModel.second.active_Datas
                    .list_M1Produit,
                on_pour_send_data = on_pour_send_data
            )
        }
    }
}
