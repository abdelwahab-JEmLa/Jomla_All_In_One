package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.Filter.MainFilter
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    modifier: Modifier = Modifier,
    viewModel: E0AfficheHistoriqueTransactionsViewModel = koinViewModel(),
    repo8BonVent: Repo8BonVent = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent,
    relative_Client: M2Client? = null,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
) {
    Column(
        modifier =
            Modifier
                .getSemanticsTag(repo8BonVent.datasValue, "")
    ) {
        Text(
            modifier =
                Modifier

                    .getSemanticsTag(repo8BonVent.datasValue, "")
                    .padding(vertical = 8.dp),
            text = buildString {
                append((relative_Client?.nom ?: "null"))
                append(" ")
                append("سجل المعاملات")
            },
            style = MaterialTheme.typography.titleMedium
        )

        MainFilter(
            relative_Client,
            viewModel,
            Modifier,
            fragmentNavigationHandler_NewProto=fragmentNavigationHandler_NewProto,
        )
    }
}
