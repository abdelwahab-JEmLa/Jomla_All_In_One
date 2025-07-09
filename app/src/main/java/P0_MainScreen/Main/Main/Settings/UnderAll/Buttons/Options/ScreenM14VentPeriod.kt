package P0_MainScreen.Main.Main.Settings.UnderAll.Buttons.Options

import P0_MainScreen.Main.Main.Settings.UnderAll.Buttons.Options.List.ViewListM14
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

open class ViewModel_M14VentPeriod(val aCentralFacade: ACentralFacade) : ViewModel()

@Composable
fun ScreenM14VentPeriod(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_M14VentPeriod = koinInject(),
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
                ViewListM14(
                    viewModel,
                )
            }
        }
    }
}
