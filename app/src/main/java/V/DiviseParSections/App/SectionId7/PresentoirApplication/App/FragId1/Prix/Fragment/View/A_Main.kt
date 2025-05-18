package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitNoSqlDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun FragmentMain(
    viewModel: TarificationViewModel = koinViewModel(),
    produitSelectioneDuAncienDataBase: ArticlesBasesStatsTable,
) {
    val uiState by viewModel.uiState
    val productId = produitSelectioneDuAncienDataBase.idArticle.toLong()
    val clientId = viewModel.ancienRepoOuverClientId

    // Move verification operations to LaunchedEffect here
    LaunchedEffect(productId, clientId) {
        // Step 1: Add product info if not exists
        viewModel.ajouteSiExistePas_A_ProduitInfos(
            productId,
            produitSelectioneDuAncienDataBase.nomArticleFinale,
        )

        // Step 2: Add client info if not exists
        viewModel.ajouteSiExistePas_B_ClientsDataBase()

        // Step 3: Refresh data
        viewModel.convertiseurNoSqlToSqlRepository.refreshNoSqlData()
    }

    FilterMainScreen(
        viewModel = viewModel,
        noSqlData = uiState.outputModel,
        selectedProductId = productId,
        selectedClientId = clientId ?: 0L,
    )
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

    // Move the verification for type tarifications here
    LaunchedEffect(typeTarificationsList) {
        if (typeTarificationsList.isNotEmpty()) {
            // Add typeTarification info if not exists
            viewModel.verifierAddNew_C_TypeTarificationInfos(typeTarificationsList)

            // Add tarification data for each type
            typeTarificationsList.forEach { typeTarification ->
                viewModel.verifierAdd_D_TarificationInfos(typeTarification)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (selectedProduct != null && selectedClient != null) {
                ProductClientInfoCard(
                    viewModel = viewModel,
                    produit = selectedProduct,
                    client = selectedClient
                )

                MainList(
                    viewModel = viewModel,
                    typeTarificationsList = typeTarificationsList,
                    showOnlyLatestPrices = showOnlyLatestPrices,
                    modifier = Modifier.weight(1f)
                )
            } else if (selectedProduct != null) {
                Text(
                    text = "Product information available, attempting to load client data...",
                    modifier = Modifier.padding(16.dp)
                )

                // Add this to trigger client data loading
                LaunchedEffect(Unit) {
                    viewModel.ajouteSiExistePas_B_ClientsDataBase()
                }
            } else {
                Text(
                    text = "Product information not found. ID: $selectedProductId",
                    modifier = Modifier.padding(16.dp)
                )
            }
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
