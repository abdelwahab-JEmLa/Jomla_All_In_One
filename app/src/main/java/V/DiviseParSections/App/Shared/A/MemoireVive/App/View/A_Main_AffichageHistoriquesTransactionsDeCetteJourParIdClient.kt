package V.DiviseParSections.App.Shared.A.MemoireVive.App.View

import V.DiviseParSections.App.Shared.A.MemoireVive.App.View.W.Filter.MainFilter
import V.DiviseParSections.App.Shared.A.MemoireVive.App.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    modifier: Modifier = Modifier,
    viewModel: E0AfficheHistoriqueTransactionsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    MainFilter(
        viewModel,
    )
}

