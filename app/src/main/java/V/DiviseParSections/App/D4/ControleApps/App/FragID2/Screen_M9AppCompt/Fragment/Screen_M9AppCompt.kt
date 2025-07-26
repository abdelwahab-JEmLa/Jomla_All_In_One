package V.DiviseParSections.App.D4.ControleApps.App.FragID2.Screen_M9AppCompt.Fragment

import V.DiviseParSections.App.D4.ControleApps.App.FragID2.Screen_M9AppCompt.Fragment.Main.List.ViewList_M9AppCompt
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import org.koin.compose.koinInject

open class ViewModel_M9AppCompt(val aCentralFacade: ACentralFacade) : ViewModel()

@Composable
fun Screen_M9AppCompt(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_M9AppCompt = koinInject(),
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
                ViewList_M9AppCompt(
                    viewModel,
                )
            }
        }
    }
}
