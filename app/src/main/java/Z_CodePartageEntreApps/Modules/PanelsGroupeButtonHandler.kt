package Z_CodePartageEntreApps.Modules

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class PanelsGroupeButtonHandler {
    // Added a TAG for logs
    private val TAG = "PanelsButtonHandler"

    private val classeScope = CoroutineScope(Dispatchers.IO)

    // Changed to mutableStateOf to make it observable in Compose
    private var _showDialogeControleFabs = mutableStateOf(false)
    val showDialogeControleFabs: Boolean get() = _showDialogeControleFabs.value

    // Make the list a mutableStateOf to ensure updates trigger recomposition
    private var _paneleGroupeButtonList = mutableStateOf(
        listOf(
            PanelsGroupeButtonDeClasse(
                PanelsGroupeButtonDeClasse.Keys.MapSecteursPolygenHandelButtons,
                isVisible = false
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

            Log.d(TAG, "Found panel at index $index with current visibility: ${currentList[index].isVisible}")

            // Create a new list with the updated item
            val updatedList = currentList.toMutableList()
            updatedList[index] = updatedState

            // Update the state (directly on the main thread for UI updates)
            _paneleGroupeButtonList.value = updatedList

            Log.d(TAG, "Successfully updated panel list. New state: ${updatedList.map { "${it.key}:${it.isVisible}" }}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating panel visibility: ${e.message}", e)
        }
    }

    data class PanelsGroupeButtonDeClasse(
        val key: Keys,
        val isVisible: Boolean = false,
    ) {
        enum class Keys {
            A_OptionsControlsButtons_A1FragID_3,
            MapSecteursPolygenHandelButtons,
            autres,
        }
    }

    fun setShowDialogControleFabs(show: Boolean) {
        Log.d(TAG, "Setting dialog visibility to: $show")
        _showDialogeControleFabs.value = show
    }

    @Composable
    fun ButtonActiveWindow() {
        val couleurButton2 = Color(0xFF3F51B5)
        FloatingActionButton(
            onClick = {
                Log.d(TAG, "Button clicked, attempting to show dialog")
                setShowDialogControleFabs(true)
            },
            modifier = Modifier.size(40.dp),
            containerColor = couleurButton2
        ) {
            // Change icon to indicate polygon creation
            Icon(Icons.Filled.Shop, "setShowDialogControleFabs")
        }
    }

    @Composable
    fun DialogPanelButtons() {
        // Get the current values from state
        val showDialog = _showDialogeControleFabs.value
        val panelsList = _paneleGroupeButtonList.value

        Log.d(TAG, "DialogPanelButtons composable called, showDialog: $showDialog, panels: ${panelsList.size}")

        if (showDialog) {
            Log.d(TAG, "Showing dialog with ${panelsList.size} panel options: ${panelsList.map { "${it.key}:${it.isVisible}" }}")
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
                            "Control Panel",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        panelsList.forEach { fabHandler ->
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        Log.d(TAG, "Clicked on panel item: ${fabHandler.key}, current state: ${fabHandler.isVisible}")
                                        val updatedState = fabHandler.copy(
                                            isVisible = !fabHandler.isVisible
                                        )
                                        Log.d(TAG, "Creating updated state with new visibility: ${updatedState.isVisible}")
                                        updatedStateFabGroupVisibility(updatedState)
                                    },
                                verticalAlignment = Alignment.CenterVertically
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
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}
