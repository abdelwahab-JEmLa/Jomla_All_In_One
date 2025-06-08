package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient = koinViewModel(),
    idClient: Long = 4
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateStringName = DatesHandler()
    MainFilter(uiState=uiState,
        dateStringName=dateStringName,
        idClient=idClient ,
        viewModel
        )
   
}

