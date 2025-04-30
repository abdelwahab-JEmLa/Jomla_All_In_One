package Z_CodePartageEntreApps.Modules

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.A_APP4FragID1_MainScreen
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin.Partage.Views.AnimatedIconLottieJsonFile
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlin.math.roundToInt

class PanelsGroupeButtonHandler {
    private val TAG = "PanelsButtonHandler"

    // Changed to mutableStateOf to make it observable in Compose
    private var _showDialogeControleFabs = mutableStateOf(false)
    val showDialogeControleFabs: Boolean get() = _showDialogeControleFabs.value

    private var _showVendeursDialog = mutableStateOf(false)

    // Make the list a mutableStateOf to ensure updates trigger recomposition
    private var _paneleGroupeButtonList = mutableStateOf(
        listOf(
            PanelsGroupeButtonDeClasse(
                PanelsGroupeButtonDeClasse.Keys.A_OptionsControlsButtons_A1FragID_3,
            ),
            PanelsGroupeButtonDeClasse(
                PanelsGroupeButtonDeClasse.Keys.MapSecteursPolygenHandelButtons, isVisible = false
            ),
            PanelsGroupeButtonDeClasse(PanelsGroupeButtonDeClasse.Keys.autres, isVisible = false),
        )
    )

    // Property to expose the state value
    var paneleGroupeButtonList: List<PanelsGroupeButtonDeClasse>
        get() = _paneleGroupeButtonList.value
        set(value) {
            Log.d(TAG, "Setting new panel list with ${value.size} items")
            _paneleGroupeButtonList.value = value
        }

    @Composable
    fun GroupeButtonsActivePanelsWindows() {
        val TAG = "A_OptionsControlsButtons_Main"

        var showMenu by remember { mutableStateOf(false) }
        var showLabels by remember { mutableStateOf(true) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }


        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {

            Box(modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .padding(16.dp)) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showMenu) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val couleur = Color(0xFF9C27B0)
                            FloatingActionButton(
                                onClick = {
                                    _showVendeursDialog.value=true
                                }, modifier = Modifier.size(40.dp), containerColor = couleur
                            ) {
                                Icon(
                                    Icons.Filled.PhoneAndroid, contentDescription = "View Vendeurs"
                                )
                            }

                            val couleurButton2 = Color(0xFF3F51B5)
                            FloatingActionButton(
                                onClick = {
                                    Log.d(
                                        TAG, "Button clicked, attempting to show dialog"
                                    )
                                    setShowDialogControleFabs(true)
                                }, modifier = Modifier.size(40.dp), containerColor = couleurButton2
                            ) {
                                // Change icon to indicate polygon creation
                                Icon(Icons.Filled.Shop, "setShowDialogControleFabs")
                            }

                            if (showLabels) {
                                Text(
                                    "View Vendeurs", modifier = Modifier
                                        .background(
                                            Color(0xFF009688)
                                        )
                                        .padding(4.dp), color = Color.White
                                )
                            }

                            ComptsManager()

                        }

                        ControlButton(
                            onClick = { showLabels = !showLabels },
                            icon = Icons.Default.Info,
                            contentDescription = if (showLabels) "Hide labels" else "Show labels",
                            showLabels = showLabels,
                            labelText = if (showLabels) "Hide labels" else "Show labels",
                            containerColor = Color(0xFF3F51B5)
                        )
                    }

                    // Menu Button - Inlined from MenuButton function
                    ControlButton(
                        onClick = { showMenu = !showMenu },
                        icon = if (showMenu) Icons.Default.ExpandLess else Icons.Default.Warning,
                        contentDescription = if (showMenu) "Hide menu" else "Show menu",
                        showLabels = showLabels,
                        labelText = if (showMenu) "Hide" else "خاص بمدير التطبيق",
                        containerColor = Color(0xFFF44336)
                    )
                }
            }
        }
    }
    @Composable
    fun ControlButton(
        onClick: () -> Unit,
        icon: Any,
        contentDescription: String,
        showLabels: Boolean,
        labelText: String,
        containerColor: Color,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
    ) {
        Log.d(
            TAG,
            "ControlButton called with icon type: ${icon.javaClass.simpleName}"
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (icon) {
                is ImageVector -> {
                    Log.d(
                        TAG,
                        "Rendering ImageVector icon"
                    )
                    FloatingActionButton(
                        onClick = {
                            if (enabled) {
                                Log.d(
                                    TAG,
                                    "ImageVector FAB clicked"
                                )
                                onClick()
                            }
                        },
                        modifier = modifier.size(40.dp),
                        containerColor = containerColor,

                        ) {
                        Icon(icon, contentDescription)
                    }
                }

                is LottieJsonGetterR_Raw_Icons -> {
                    Log.d(
                        TAG,
                        "Rendering LottieJsonGetterR_Raw_Icons"
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(enabled = enabled) {
                                Log.d(
                                    TAG,
                                    "LottieJson Box clicked"
                                )
                                onClick()
                            }
                            .background(
                                color = if (enabled) containerColor else Color.Gray,
                                shape = CircleShape
                            )
                            .also {
                                Log.d(
                                    TAG,
                                    "Box modifiers applied successfully"
                                )
                            }, contentAlignment = Alignment.Center
                    ) {
                        AnimatedIconLottieJsonFile(
                            ressourceXml = icon, onClick = if (enabled) onClick else ({})
                        )
                    }
                }

                is Int -> {
                    // Support for direct resource IDs like R.raw.categ
                    Log.d(
                        TAG,
                        "Rendering direct resource ID: $icon"
                    )
                    Box(modifier = Modifier
                        .size(40.dp)
                        .clickable(enabled = enabled) {
                            Log.d(
                                TAG,
                                "Resource ID Box clicked"
                            )
                            onClick()
                        }
                        .background(
                            color = if (enabled) containerColor else Color.Gray, shape = CircleShape
                        ), contentAlignment = Alignment.Center) {
                    }
                }

                else -> {
                    Log.e(
                        TAG,
                        "Unsupported icon type: ${icon.javaClass.simpleName}"
                    )
                    throw IllegalArgumentException("Unsupported icon type")
                }
            }

            if (showLabels) {
                Text(
                    labelText,
                    modifier = Modifier
                        .background(if (enabled) containerColor else Color.Gray)
                        .padding(4.dp),
                    color = Color.White
                )
            }
        }
    }

    @Composable
    private fun ComptsManager() {
        if (_showVendeursDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    _showVendeursDialog.value = false
                },
                title = { Text("Manage Vendeurs") },
                text = {
                    A_APP4FragID1_MainScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        _showVendeursDialog.value = false
                    }) {
                        Text("Close")
                    }
                }
            )
        }
    }

    @Composable
    fun AfficheDialogesHeadApps() {
        // Get the current values from state
        if (_showDialogeControleFabs.value) {
            Dialog(
                onDismissRequest = {
                    Log.d(TAG, "Dialog dismiss requested")
                    setShowDialogControleFabs(false)
                },
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            "Control Panel", modifier = Modifier.padding(bottom = 16.dp)
                        )

                        _paneleGroupeButtonList.value.forEach { fabHandler ->
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        Log.d(
                                            TAG,
                                            "Clicked on panel item: ${fabHandler.key}, current state: ${fabHandler.isVisible}"
                                        )
                                        val updatedState = fabHandler.copy(
                                            isVisible = !fabHandler.isVisible
                                        )
                                        Log.d(
                                            TAG,
                                            "Creating updated state with new visibility: ${updatedState.isVisible}"
                                        )
                                        updatedStateFabGroupVisibility(updatedState)
                                    }, verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = if (fabHandler.isVisible) Color.Green else Color.Gray,
                                            shape = CircleShape
                                        )
                                        .padding(4.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(fabHandler.key.name)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = {
                                Log.d(TAG, "Close button clicked")
                                setShowDialogControleFabs(false)
                            }, modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }

    fun updatedStateFabGroupVisibility(updatedState: PanelsGroupeButtonDeClasse) {
        Log.d(TAG, "Updating visibility for ${updatedState.key} to ${updatedState.isVisible}")

        try {
            // Get current list value
            val currentList = _paneleGroupeButtonList.value
            val index = currentList.indexOfFirst { it.key == updatedState.key }

            if (index == -1) {
                Log.e(TAG, "Failed to find panel with key ${updatedState.key} in the list")
                return
            }

            Log.d(
                TAG,
                "Found panel at index $index with current visibility: ${currentList[index].isVisible}"
            )

            // Create a new list with the updated item
            val updatedList = currentList.toMutableList()
            updatedList[index] = updatedState

            // Update the state (directly on the main thread for UI updates)
            _paneleGroupeButtonList.value = updatedList

            Log.d(
                TAG,
                "Successfully updated panel list. New state: ${updatedList.map { "${it.key}:${it.isVisible}" }}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error updating panel visibility: ${e.message}", e)
        }
    }

    data class PanelsGroupeButtonDeClasse(
        val key: Keys,
        val isVisible: Boolean = false,
    ) {
        enum class Keys {
            A_OptionsControlsButtons_A1FragID_3, MapSecteursPolygenHandelButtons, autres,
        }
    }

    fun setShowDialogControleFabs(show: Boolean) {
        Log.d(TAG, "Setting dialog visibility to: $show")
        _showDialogeControleFabs.value = show
    }


}
