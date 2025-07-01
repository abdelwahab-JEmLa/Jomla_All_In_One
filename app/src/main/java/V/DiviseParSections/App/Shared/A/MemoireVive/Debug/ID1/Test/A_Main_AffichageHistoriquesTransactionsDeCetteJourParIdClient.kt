package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Filter.MainFilter
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    modifier: Modifier = Modifier,
    viewModel: E0AfficheHistoriqueTransactionsViewModel = koinViewModel(),
    parentTestTag_ClientKey: String = "",
) {
    Column {
        Text(
            text = "سجل المعاملات",
            style = MaterialTheme.typography.titleMedium,
            modifier =
                Modifier
                    .padding(vertical = 8.dp)
                    .testTag(parentTestTag_ClientKey)
        )

        MainFilter(
            viewModel,
        )
    }
}

