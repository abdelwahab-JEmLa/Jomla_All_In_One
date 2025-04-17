package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject


@Composable
fun Main(
    modifier: Modifier = Modifier,
    viewModel: PeriodesViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    PeriodesContent(
        uiState = uiState,
     
    )
}

@Composable
fun MainScreen(
    uiState: PeriodesUiState,
  
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            MainList(
                uiState,
             
            )
        }
    }
}

@Composable
private fun MainList(
    uiState: PeriodesUiState,

) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
           

        }

        items(uiState) { vendeur ->
            
        }



    }
}
