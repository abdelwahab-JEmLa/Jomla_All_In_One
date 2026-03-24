package Application4.App.Fragment.View.Components.A_Header.View

import EntreApps.Shared.Models.M01Produit
import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.Prioriter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Label
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Section_ToggleButton_TagPreiorities(
    produit: M01Produit,
    affiche_ProduitDataBaseEdites_ComposableViews: Boolean,
    start_Colapssed: Boolean = false,
    onAddDeleteTag_ToUpdate: (M01Produit) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    if (!affiche_ProduitDataBaseEdites_ComposableViews) return

    var expanded by remember { mutableStateOf(!start_Colapssed) }
    val activeTags = remember(produit.tag_prioriter_str) { produit.produit_set_Tag_Priorite() }
    val hasAnyTag = activeTags.isNotEmpty()

    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.Companion.clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(
                containerColor = if (hasAnyTag)
                    MaterialTheme.colorScheme.tertiaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.Companion.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = "Tags priorité",
                    tint = if (hasAnyTag)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.Companion.size(12.dp)
                )
                Text(
                    text = if (hasAnyTag)
                        activeTags.joinToString(" · ") { it.label() }
                    else
                        "Tags priorité",
                    fontSize = 8.sp,
                    fontWeight = if (hasAnyTag) FontWeight.Companion.SemiBold else FontWeight.Companion.Normal,
                    color = if (hasAnyTag)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Companion.Ellipsis
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Prioriter.entries.forEach { prioriter ->
                    val isSelected = prioriter in activeTags
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newTags = activeTags.toMutableSet().apply {
                                if (isSelected) remove(prioriter) else add(prioriter)
                            }
                            onAddDeleteTag_ToUpdate(
                                produit.setReturn_Produit_Ac_tag_prioriter_str(
                                    produit_set_Tag_Priorite = newTags,
                                    produit = produit
                                )
                            )
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
