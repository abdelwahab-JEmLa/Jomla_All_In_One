package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Model.ProduitNoSqlDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun PreviewTest() {
    FragmentMain(
        selectedProductId = 1,
        selectedClientId = 1
    )
}

@Composable
private fun FragmentMain(
    viewModel: TarificationViewModel = koinViewModel(),
    selectedProductId: Long,
    selectedClientId: Long
) {
    val uiState by viewModel.uiState

    ClientJetPackTheme(darkTheme = true) {
        FilterMainScreen(
            viewModel = viewModel,
            noSqlData = uiState.outputModel,
            selectedProductId = selectedProductId,
            selectedClientId = selectedClientId,
        )
    }
}

@Composable
fun FilterMainScreen(
    noSqlData: ProduitNoSqlDataBase,
    selectedProductId: Long,
    selectedClientId: Long,
    modifier: Modifier = Modifier,
    viewModel: TarificationViewModel,
) {
    var showOnlyLatestPrices by remember {
        mutableStateOf(false)
    }
    val selectedProduct = noSqlData.produits.find { it.infosId == selectedProductId }
    val selectedClient = selectedProduct?.clientAchteurs?.find { it.infosId == selectedClientId }
    val typeTarificationsList = selectedClient?.typeTarification ?: emptyList()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (selectedProduct != null && selectedClient != null) {
                ProductClientInfoCard(
                    viewModel = viewModel,
                    produit = selectedProduct,
                    client = selectedClient
                )
            }

            MainList(
                viewModel = viewModel,
                typeTarificationsList = typeTarificationsList,
                showOnlyLatestPrices = showOnlyLatestPrices,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    showOnlyLatestPrices = !showOnlyLatestPrices
                },
                modifier = Modifier
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = "Toggle Latest Prices"
                )
            }
        }
    }
}

@Composable
fun MainList(
    viewModel: TarificationViewModel,
    typeTarificationsList: List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification>,
    showOnlyLatestPrices: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(typeTarificationsList) { typeTarification ->
            TarificationTypeSection(
                viewModel = viewModel,
                typeTarification = typeTarification,
                showOnlyLatestPrices = showOnlyLatestPrices,
            )
        }
    }
}
