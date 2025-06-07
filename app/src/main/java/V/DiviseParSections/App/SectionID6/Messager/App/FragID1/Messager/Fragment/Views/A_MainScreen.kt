package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options.FabButtonsMessageurMainScreen
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_MessageurMainScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier) {
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
                parentD_EtateMessageVocale = latestEtate,
                etatesChildKeyIDsList = allEtatesForMessage,
                viewModel = viewModel
            )
        }
    }
}
