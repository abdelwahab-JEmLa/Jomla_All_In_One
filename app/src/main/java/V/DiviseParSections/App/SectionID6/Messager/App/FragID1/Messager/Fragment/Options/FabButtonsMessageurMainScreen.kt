package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.LabelsButton
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.MenuButton
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Models.EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Models.MessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun FabButtonsMessageurMainScreen(
    viewModel: ViewModelMessageur,
) {

    var showMenu by remember { mutableStateOf(true) }
    var showLabels by remember { mutableStateOf(true) }

    // États pour le drag
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showMenu) {
                    ButtonEnregestrementMessageVocaleEtLeMetreAuStorageGoogle(
                        viewModel = viewModel
                    ) { fileId ->
                        // Implement the insertion logic here
                        viewModel.viewModelScope.launch {
                            // Create a new MessageVocale with the uploaded file ID
                            val newMessage = MessageVocale(
                                vid = System.currentTimeMillis(),
                                vocaleKeyID = fileId,
                                nomClientConcerned = "Client"
                            )

                            // Insert the message into the database
                            viewModel.appDatabase.messageVocaleDao().insert(newMessage)

                            // Create an initial CREE state for the message
                            val newEtate = EtateMessageVocale(
                                parentMessageVID = newMessage.vid,
                                parentMessageKeyID = newMessage.fireBaseKeyID,
                                nom = EtateMessageVocale.Nom.CREE,
                                timestamps = DatesHandler().getCurrentTimestamps()
                            )

                            // Insert the state into the database
                            viewModel.appDatabase.etateMessageVocaleDao().insert(newEtate)
                        }
                    }

                }

                LabelsButton(
                    showLabels = showLabels,
                    onShowLabelsChange = { showLabels = it }
                )

                MenuButton(
                    showLabels = showLabels,
                    showMenu = showMenu,
                    onShowMenuChange = { showMenu = it }
                )
            }
        }
    }
}
