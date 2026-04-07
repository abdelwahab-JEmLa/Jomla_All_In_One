package A_Main.Shared.Views.Dialogs.B.Dialoge

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class ProductDisplayMode {
    AllProducts,
    Echantillons,
    Panie,
}

// ─────────────────────────────────────────────────────────────────────────────
// Search FAB
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A floating search button that expands into a text-field when toggled.
 * [searchText] / [onSearchTextChange] are owned by the caller (ViewModel state).
 */
@Composable
fun But_4_FloatingSearchFAB(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showField by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = {
                showField = !showField
                if (!showField) onSearchTextChange("") // clear on collapse
            },
            containerColor = MaterialTheme.colorScheme.tertiary,
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rechercher un produit",
                tint = Color.White
            )
        }

        if (showField) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                placeholder = {
                    Text(
                        "Rechercher...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchTextChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Effacer",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main floating button cluster
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PressistatntMainActivityButtons_App4(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns
) {

    val currentMode by remember {
        derivedStateOf {
            val datas = viewModelNewProtoPatterns.active_Datas
            when {
                datas.its_Panie_Mode     -> ProductDisplayMode.Panie
                datas.isEchatillantsMode -> ProductDisplayMode.Echantillons
                else                     -> ProductDisplayMode.AllProducts
            }
        }
    }

    fun applyMode(mode: ProductDisplayMode) {
        val isPanie = mode == ProductDisplayMode.Panie
        viewModelNewProtoPatterns.active_Datas.its_Panie_Mode     = isPanie
        viewModelNewProtoPatterns.active_Datas.isEchatillantsMode = mode == ProductDisplayMode.Echantillons
        // Clear search when switching mode so stale query does not hide items
        viewModelNewProtoPatterns.active_Datas.filter_echatilaten = ""
        viewModelNewProtoPatterns.active_Datas.active_M9Compt?.let { compt ->
            viewModelNewProtoPatterns.update_active_Compt(
                compt.copy(its_Panie_Mode_Au_Lence_Boutique = isPanie)
            )
        }
    }

    var showDropdown by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val fabColor = when (currentMode) {
        ProductDisplayMode.AllProducts  -> MaterialTheme.colorScheme.surfaceVariant
        ProductDisplayMode.Echantillons -> MaterialTheme.colorScheme.primary
        ProductDisplayMode.Panie        -> MaterialTheme.colorScheme.tertiary
    }
    val fabIcon: ImageVector = when (currentMode) {
        ProductDisplayMode.AllProducts  -> Icons.Default.FilterList
        ProductDisplayMode.Echantillons -> Icons.Default.Check
        ProductDisplayMode.Panie        -> Icons.Default.ShoppingCart
    }
    val fabTint = when (currentMode) {
        ProductDisplayMode.AllProducts -> MaterialTheme.colorScheme.onSurfaceVariant
        else                           -> Color.White
    }
    val labelText = when (currentMode) {
        ProductDisplayMode.AllProducts  -> "Tous les produits"
        ProductDisplayMode.Echantillons -> "Échantillons"
        ProductDisplayMode.Panie        -> "Panier"
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // ── Mode selector FAB ────────────────────────────────────────────
            Box {
                FloatingActionButton(
                    modifier = Modifier.padding(16.dp).size(56.dp),
                    onClick = { showDropdown = true },
                    containerColor = fabColor,
                ) {
                    Icon(imageVector = fabIcon, contentDescription = null, tint = fabTint)
                }

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false }
                ) {
                    ModeMenuItem(
                        label = "Tous les produits",
                        icon = Icons.Default.FilterList,
                        isSelected = currentMode == ProductDisplayMode.AllProducts,
                        onClick = { applyMode(ProductDisplayMode.AllProducts); showDropdown = false }
                    )
                    ModeMenuItem(
                        label = "Échantillons",
                        icon = Icons.Default.Check,
                        isSelected = currentMode == ProductDisplayMode.Echantillons,
                        onClick = { applyMode(ProductDisplayMode.Echantillons); showDropdown = false }
                    )
                    ModeMenuItem(
                        label = "Panier",
                        icon = Icons.Default.ShoppingCart,
                        isSelected = currentMode == ProductDisplayMode.Panie,
                        onClick = { applyMode(ProductDisplayMode.Panie); showDropdown = false }
                    )
                }
            }

            But_4_FloatingSearchFAB(
                searchText = viewModelNewProtoPatterns.active_Datas.filter_echatilaten,
                onSearchTextChange = { viewModelNewProtoPatterns.active_Datas.filter_echatilaten = it }
            )

            // ── Mode label ───────────────────────────────────────────────────
            Text(
                text = labelText,
                modifier = Modifier
                    .background(color = fabColor, shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                color = fabTint,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ModeMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (isSelected) Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        onClick = onClick
    )
}
