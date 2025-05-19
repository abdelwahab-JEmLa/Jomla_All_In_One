package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.View

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitsNoSqlDataBase
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.TarificationViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun FragmentMain(
    viewModel: TarificationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState
    val produitsNoSqlDataBase by remember {
        mutableStateOf(
            uiState.produitsNoSqlDataBase
        )
    }

    if (produitsNoSqlDataBase.produits.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Loading product data...")
        }
    } else {
        FilterMainScreen(
            produitsNoSqlDataBase = produitsNoSqlDataBase,
            viewModel = viewModel,
        )
    }
}

@Composable
fun FilterMainScreen(
    modifier: Modifier = Modifier,
    viewModel: TarificationViewModel,
    produitsNoSqlDataBase: ProduitsNoSqlDataBase,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MainList(
                produitsNoSqlDataBase=produitsNoSqlDataBase,
                viewModel = viewModel,
                showOnlyLatestPrices = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    viewModel: TarificationViewModel,
    showOnlyLatestPrices: Boolean,
    produitsNoSqlDataBase: ProduitsNoSqlDataBase,
) {
    val typeTarificationList by remember {
        mutableStateOf(
            viewModel.gettypeActiveTarifications(produitsNoSqlDataBase)
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(typeTarificationList) { typeTarification ->
            TarificationTypeSection(
                viewModel = viewModel,
                typeTarification = typeTarification,
                showOnlyLatestPrices = showOnlyLatestPrices,
            )
        }
    }
}
