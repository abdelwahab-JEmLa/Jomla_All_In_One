package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.Component.LabelEtShowButtonsButtons
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.AncienProto.C_CategorieProduitInfosAncienProto.ProtoB2.Companion.updateCategoriePositionDepuitProto2
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.CategoriesTabelle
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class AfficheElements { APP_BAR, DOCUMENTATION_TEXT }

@Composable
fun OptionsFragmentButtons(
    viewModelScope: CoroutineScope,
    function: (List<CategoriesTabelle>) -> Unit,
    initCategories: List<CategoriesTabelle>,
    onToggleMasque: (Set<AfficheElements>) -> Unit = {},
    selectedProducts: Set<ArticlesBasesStatsTable> = emptySet(),
    onShowBulkMoveDialog: () -> Unit = {}
) {
    var showButtons by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }

    // Get screen configuration to position at the right edge
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    // Initialize offset to start at the right edge of the screen
    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 150f)) } // 80f accounts for button size + padding
    var offsetY by remember { mutableFloatStateOf(screenWidth.value + 500f) }
    var maskedElements by remember { mutableStateOf(setOf<AfficheElements>()) }
    var showDialog by remember { mutableStateOf(false) }

    val onToggle = {
        maskedElements = if (maskedElements.contains(AfficheElements.APP_BAR)) {
            maskedElements - AfficheElements.APP_BAR
        } else {
            maskedElements + AfficheElements.APP_BAR
        }
        onToggleMasque(maskedElements)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Update categories? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModelScope.launch { function(updateCategoriePositionDepuitProto2(initCategories)) }
                }) { Text("Confirm") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (showButtons) {
                    // But1 - Update Categories
                    But1(showDialog = showDialog, showLabels = showLabels) { showDialog = true }

                    // But2 - Bulk Move Products
                    But2(
                        showLabels = showLabels,
                        selectedCount = selectedProducts.size,
                        onBulkMove = onShowBulkMoveDialog
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (showLabels) Text("AfficheElements")
                        FloatingActionButton(
                            onClick = { viewModelScope.launch { onToggle() } },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color.Red
                        ) {
                            Icon(
                                if (maskedElements.contains(AfficheElements.APP_BAR)) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                "Toggle Mask Elements",
                                tint = Color.Black
                            )
                        }
                    }
                }

                LabelEtShowButtonsButtons(
                    showLabels = showLabels,
                    showButtons = showButtons,
                    onShowLabelsToggle = { showLabels = !showLabels },
                    onShowButtonsToggle = { showButtons = !showButtons },
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun But1(showDialog: Boolean, showLabels: Boolean, onShowDialog: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabels) Text("Update")
        FloatingActionButton(
            onClick = onShowDialog,
            modifier = Modifier.size(40.dp),
            containerColor = Color.Yellow
        ) {
            Icon(Icons.Default.Refresh, "Update Categories", tint = Color.Black)
        }
    }
}

@Composable
private fun But2(showLabels: Boolean, selectedCount: Int, onBulkMove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabels) Text("Move ($selectedCount)")
        FloatingActionButton(
            onClick = onBulkMove,
            modifier = Modifier.size(40.dp),
            containerColor = if (selectedCount > 0) Color.Green else Color.Gray
        ) {
            Icon(Icons.Default.SwapHoriz, "Bulk Move Products", tint = Color.Black)
        }
    }
}
