package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options.FabButtonsMessageurMainScreen
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp) // Add padding for the FAB
        ) {
            items(uiState.noSqlMessageVocaleList) { noSqlMessageVocale ->
                val parentMessage = uiState.messageVocaleList.find { it.keyID == noSqlMessageVocale.keyIDMessageVocale }

                val relevantEtates = uiState.etateMessageVocaleList.filter { etate ->
                    noSqlMessageVocale.keyIDsChildListEtateMessageVocale.contains(etate.keyID)
                }

                if (parentMessage != null) {
                    B_ItemMessagesVocale(
                        parentMessageVocale = parentMessage,
                        etatesChildKeyIDsList = relevantEtates,
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }
        }

        // Display FAB buttons at the bottom
        FabButtonsMessageurMainScreen(viewModel)
    }
}
