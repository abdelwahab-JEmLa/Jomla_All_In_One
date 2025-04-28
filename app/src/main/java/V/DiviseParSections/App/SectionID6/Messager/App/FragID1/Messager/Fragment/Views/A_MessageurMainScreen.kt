package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_MessageurMainScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur = koinViewModel()
) {
    Box(modifier = modifier) {
        // Collect messages from ViewModel
        val uiState by viewModel.uiState.collectAsState()
        val messagesList by uiState.noSqlMessageVocaleList.collectAsState(initial = emptyList())
        val etatesList by uiState.etateMessageVocaleList.collectAsState(initial = emptyList())
        val messageVocaleList by uiState.messageVocaleList.collectAsState(initial = emptyList())

        // Create a map of messages for easier access
        val messagesMap = messageVocaleList.associateBy { it.keyID }

        // Display messages in LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp) // Add padding for the FAB
        ) {
            items(messagesList) { message ->
                // Find the message details from the map
                val messageDetails = messagesMap[message.keyIDMessageVocale]

                if (messageDetails != null) {
                    MessagesVocaleItem(
                        uiState=uiState,
                        messageDetails = messageDetails,
                        etates = etatesList.filter { etate ->
                            message.keyIDsChildListEtateMessageVocale.contains(etate.fireBaseKeyID)
                        },
                        viewModel = viewModel
                    )
                }
            }
        }

        // Display FAB buttons at the bottom
        FabButtonsMessageurMainScreen(viewModel)
    }
}


