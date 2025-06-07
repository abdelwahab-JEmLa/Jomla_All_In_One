package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options.FabButtonsMessageurMainScreen
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_MessageurMainScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur = koinViewModel(),
    onDismiss: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    // Show dialog at start
    LaunchedEffect(Unit) {
        showDialog = true
    }

    // Container Box
    Box(modifier = modifier.fillMaxSize()) {

        // Dialog implementation
        if (showDialog) {
            Dialog(
                onDismissRequest = {
                    showDialog = false
                    onDismiss()
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = false
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        MessageurDialogContent(
                            viewModel = viewModel,
                            onDismiss = {
                                showDialog = false
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageurDialogContent(
    viewModel: ViewModelMessageur,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box {
        MainFilter(uiState = uiState, viewModel = viewModel)
        // Display FAB buttons at the bottom
        FabButtonsMessageurMainScreen(viewModel)
    }
}

@Composable
fun MainFilter(
    modifier: Modifier = Modifier,
    uiState: UiState,
    viewModel: ViewModelMessageur
) {
    MainList(uiState = uiState, viewModel = viewModel)
}

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    uiState: UiState,
    viewModel: ViewModelMessageur
) {
    val groupedD_EtateMessageVocaleParParentMessage by remember(uiState.d_EtateMessageVocaleList) {
        derivedStateOf {
            uiState.d_EtateMessageVocaleList.groupBy { it.parentMessageVID }
        }
    }

    // FIXED: Create a list that shows only the latest state for each message
    val latestStatesForEachMessage by remember(groupedD_EtateMessageVocaleParParentMessage) {
        derivedStateOf {
            groupedD_EtateMessageVocaleParParentMessage.mapNotNull { (parentMessageVID, etatesList) ->
                // Sort by timestamp to get the latest state
                val sortedEtates = etatesList.sortedByDescending { it.timestamps }
                val latestEtate = sortedEtates.firstOrNull()

                if (latestEtate != null) {
                    // Return a pair of the latest state and all states for this message
                    Pair(latestEtate, etatesList)
                } else null
            }.sortedByDescending { it.first.timestamps } // Sort messages by latest activity
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 80.dp) // Add padding for the FAB
    ) {
        items(latestStatesForEachMessage) { (latestEtate, allEtatesForMessage) ->
            // Show the item based on the latest state, but pass all states for status checking
            B_ItemMessagesVocale(
                uiState=uiState,
                parentD_EtateMessageVocale = latestEtate,
                etatesChildKeyIDsList = allEtatesForMessage,
                viewModel = viewModel
            )
        }
    }
}
