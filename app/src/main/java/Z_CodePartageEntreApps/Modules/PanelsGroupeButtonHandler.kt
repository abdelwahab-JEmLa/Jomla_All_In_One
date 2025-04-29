package Z_CodePartageEntreApps.Modules

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PanelsGroupeButtonHandler {
    private val classeScope = CoroutineScope(Dispatchers.IO)

    private var showDialogeControleFabs: Boolean = false

    var paneleGroupeButtonList: List<PanelsGroupeButtonDeClasse> =
        listOf(
            PanelsGroupeButtonDeClasse(
                PanelsGroupeButtonDeClasse.Keys.MapSecteursPolygenHandelButtons,
                isVisible = false
            ),
            PanelsGroupeButtonDeClasse(PanelsGroupeButtonDeClasse.Keys.autres, isVisible = false),
        )

    private fun updatedStateFabGroupVisibility(updatedState: PanelsGroupeButtonDeClasse) {
        classeScope.launch {
            // Create a new list with the updated panel
            val currentList = paneleGroupeButtonList
            val index = currentList.indexOfFirst { it.key == updatedState.key }

            // Create a new list with the updated item
            val updatedList = currentList.toMutableList()
            updatedList[index] = updatedState

            paneleGroupeButtonList = updatedList
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

    private fun setShowDialogControleFabs(show: Boolean) {
        showDialogeControleFabs = show
    }

    @Composable
     fun ButtonActiveWindow() {
        val couleurButton2 = Color(0xFF3F51B5)
        FloatingActionButton(
            onClick = {
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
     fun DialogPanelButtons(
    ) {
        if (showDialogeControleFabs) {
            Dialog(
                onDismissRequest = { setShowDialogControleFabs(false) },
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

                        paneleGroupeButtonList.forEach { fabHandler ->
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        val updatedState = fabHandler.copy(
                                            isVisible = !fabHandler.isVisible
                                        )
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
                            onClick = { setShowDialogControleFabs(false) },
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
