package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.ViewList_M14VentPeriod
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import org.koin.compose.koinInject

open class ViewModel_M14VentPeriod(val aCentralFacade: ACentralFacade) : ViewModel()

@Composable
fun ScreenM14VentPeriod(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_M14VentPeriod = koinInject(),
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    list_M14VentPeriode: List<M14VentPeriode> = aCentralFacade.repositorysMainGetter.repo14VentPeriode.datasValue,
    relative_M9AppCompt: Z_AppCompt? = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentM9AppCompt,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Périodes de Vente de ${relative_M9AppCompt?.get_DebugInfos()}",
                        style = MaterialTheme.typography.titleLarge
                    )

                    // Add debug info to see what's happening
                    Text(
                        text = "Count: ${list_M14VentPeriode.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    ViewList_M14VentPeriod(
                        viewModel,
                        relative_M9AppCompt
                    )
                }
            }
        }
    }
}
