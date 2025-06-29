package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.View.W.Filter

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.View.Z.List.View_MainList
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@SuppressLint("DefaultLocale")
@Composable
fun MainFilter(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val datasGBonVentRepository = viewModel.getter.gBonVentRepository.datasValue
    val listGBonVentFilteredByClientKeySorted=
        datasGBonVentRepository
            .filter { it.parentHClientKeyID ==uiState.comptOnAfficheHistoriquesClientKeyId}
            .sortedByDescending { it.creationTimestamps }

    View_MainList(
        viewModel ,
                listGBonVentFilteredByClientKeySorted,
    )
}
