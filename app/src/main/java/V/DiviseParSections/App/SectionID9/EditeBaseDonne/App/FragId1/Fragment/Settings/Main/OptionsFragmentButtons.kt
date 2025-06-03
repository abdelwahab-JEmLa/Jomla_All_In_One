package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.Component.LabelEtShowButtonsButtons
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.*
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.AncienProto.C_CategorieProduitInfosAncienProto.ProtoB2.Companion.updateCategoriePositionDepuitProto2
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.CategoriesTabelle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class AfficheElements { APP_BAR, DOCUMENTATION_TEXT }

@Composable
fun OptionsFragmentButtons(
    viewModelScope: CoroutineScope,
    function: (List<CategoriesTabelle>) -> Unit,
    initCategories: List<CategoriesTabelle>,
    onToggleMasque: (Set<AfficheElements>) -> Unit = {}
) {
    var showButtons by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
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
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showButtons) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FloatingActionButton(
                            onClick = { showDialog = true },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color.Yellow
                        ) {
                            Icon(Icons.Default.Refresh, "Update Categories", tint = Color.Black)
                        }
                        if (showLabels) Text("Update")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                        if (showLabels) Text("AfficheElements")
                    }
                }

                LabelEtShowButtonsButtons(
                    showLabels = showLabels,
                    showButtons = showButtons,
                    onShowLabelsToggle = { showLabels = !showLabels },
                    onShowButtonsToggle = { showButtons = !showButtons }
                )
            }
        }
    }
}
