package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.A.MemoireVive.ID2.Test.View.W.Filter.MainFilter
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

