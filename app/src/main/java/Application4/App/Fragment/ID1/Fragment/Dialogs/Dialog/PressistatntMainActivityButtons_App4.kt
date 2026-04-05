package Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

// ---------------------------------------------------------------------------
// Display-mode enum — single source of truth for all three mutually-exclusive
// filter modes exposed by PressistatntMainActivityButtons_App4.
// ---------------------------------------------------------------------------
enum class ProductDisplayMode {
    AllProducts,   // normal view — hides échantillons
    Echantillons,  // only échantillon colours
    Panie,         // only colours that have an active-bon-vent operation
}

@Composable
fun PressistatntMainActivityButtons_App4(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns
) {
    // ── Derive current mode from the two boolean flags ──────────────────────
    val currentMode by remember {
        derivedStateOf {
            val datas = viewModelNewProtoPatterns.active_Datas
            when {
                datas.its_Panie_Mode       -> ProductDisplayMode.Panie
                datas.isEchatillantsMode   -> ProductDisplayMode.Echantillons
                else                       -> ProductDisplayMode.AllProducts
            }
        }
    }

    // Helper that applies a mode and resets the other flags
    fun applyMode(mode: ProductDisplayMode) {
        viewModelNewProtoPatterns.active_Datas.its_Panie_Mode     = mode == ProductDisplayMode.Panie
        viewModelNewProtoPatterns.active_Datas.isEchatillantsMode = mode == ProductDisplayMode.Echantillons
    }

    var showDropdown by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // ── Visual properties for the active mode ───────────────────────────────
    val fabColor = when (currentMode) {
        ProductDisplayMode.AllProducts -> MaterialTheme.colorScheme.surfaceVariant
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
            // ── FAB — tap opens the dropdown ─────────────────────────────────
            Box {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(56.dp),
                    onClick = { showDropdown = true },
                    containerColor = fabColor,
                ) {
                    Icon(
                        imageVector = fabIcon,
                        contentDescription = "Sélectionner le mode d'affichage",
                        tint = fabTint
                    )
                }

                // ── Dropdown menu ────────────────────────────────────────────
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false }
                ) {
                    ModeMenuItem(
                        label = "Tous les produits",
                        icon = Icons.Default.FilterList,
                        isSelected = currentMode == ProductDisplayMode.AllProducts,
                        onClick = {
                            applyMode(ProductDisplayMode.AllProducts)
                            showDropdown = false
                        }
                    )
                    ModeMenuItem(
                        label = "Échantillons",
                        icon = Icons.Default.Check,
                        isSelected = currentMode == ProductDisplayMode.Echantillons,
                        onClick = {
                            applyMode(ProductDisplayMode.Echantillons)
                            showDropdown = false
                        }
                    )
                    ModeMenuItem(
                        label = "Panier",
                        icon = Icons.Default.ShoppingCart,
                        isSelected = currentMode == ProductDisplayMode.Panie,
                        onClick = {
                            applyMode(ProductDisplayMode.Panie)
                            showDropdown = false
                        }
                    )
                }
            }

            // ── Active-mode label ────────────────────────────────────────────
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

// ---------------------------------------------------------------------------
// Reusable menu item with a leading icon and a checkmark when selected
// ---------------------------------------------------------------------------
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
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Actif",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        onClick = onClick
    )
}
