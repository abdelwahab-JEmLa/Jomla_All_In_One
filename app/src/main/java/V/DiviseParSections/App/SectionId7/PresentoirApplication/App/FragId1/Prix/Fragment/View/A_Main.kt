package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitNoSqlDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import android.util.Log
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
private const val TAG = "FragmentMain"

@Composable
fun FragmentMain(
    viewModel: TarificationViewModel = koinViewModel(),
    produitSelectioneDuAncienDataBase: ArticlesBasesStatsTable,
) {
    val uiState by viewModel.uiState
    val productId = produitSelectioneDuAncienDataBase.idArticle.toLong()
    val clientId = viewModel.ancienRepoOuverClientId

    // Use remember to track whether initial setup has been completed
    var initialSetupComplete by remember { mutableStateOf(false) }

    // First LaunchedEffect to set up the basic data - runs only once
    LaunchedEffect(productId, clientId) {
        if (!initialSetupComplete) {
            Log.d(TAG, "Initial setup - ProductID: $productId, ClientID: $clientId")

            viewModel.ajouteSiExistePas_A_ProduitInfos(
                productId,
                produitSelectioneDuAncienDataBase.nomArticleFinale,
            )

            viewModel.ajouteSiExistePas_B_ClientsDataBase()

            // Force refresh of NoSQL data after setting up basic data
            viewModel.convertiseurNoSqlToSqlRepository.refreshNoSqlData()

            Log.d(TAG, "Initial setup complete - Refreshed NoSQL data")
            initialSetupComplete = true
        }
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

    // Track if we've processed tarification types to avoid redundant processing
    var tarificationTypesProcessed by remember { mutableStateOf(false) }

    // Only process tarification types once per client and product
    LaunchedEffect(key1 = selectedClientId, key2 = selectedProductId) {
        // Skip if already processed for this client/product combo
        if (!tarificationTypesProcessed) {
            Log.d(TAG, "Processing tarifications for clientID: $selectedClientId, product: $selectedProductId")

            if (typeTarificationsList.isNotEmpty()) {
                Log.d(TAG, "Processing ${typeTarificationsList.size} tarification types for client $selectedClientId")

                // Log each tarification type ID for debugging
                typeTarificationsList.forEachIndexed { index, type ->
                    Log.d(TAG, "Type[$index]: ID=${type.infosId}")
                }

                // Add logic for type tarifications
                viewModel.verifierAddNew_C_TypeTarificationInfos(typeTarificationsList)

                Log.d(TAG, "Refreshing NoSQL data after type tarification verification")
                viewModel.convertiseurNoSqlToSqlRepository.refreshNoSqlData()

                // Process each tarification type for pricing data
                typeTarificationsList.forEachIndexed { index, typeTarification ->
                    Log.d(TAG, "Processing tarification data for type[$index]: ID=${typeTarification.infosId}")
                    viewModel.verifierAdd_D_TarificationInfos(typeTarification)
                }

                Log.d(TAG, "Final NoSQL refresh after tarification data processing")
                viewModel.convertiseurNoSqlToSqlRepository.refreshNoSqlData()
            } else if (selectedClientId > 0) {
                Log.d(TAG, "No tarification types found for client $selectedClientId, creating default")
                viewModel.createDefaultTarificationIfNeeded(selectedClientId)
            } else {
                Log.w(TAG, "Cannot create default tarification: Invalid client ID: $selectedClientId")
            }

            // Mark as processed to prevent reprocessing
            tarificationTypesProcessed = true
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

                // Add this to trigger client data loading - only once
                LaunchedEffect(Unit) {
                    if (!tarificationTypesProcessed) {
                        Log.d(TAG, "Attempting to load client data for product: $selectedProductId")
                        viewModel.ajouteSiExistePas_B_ClientsDataBase()
                        tarificationTypesProcessed = true
                    }
                }
            } else {
                Text(
                    text = "Product information not found. ID: $selectedProductId",
                    modifier = Modifier.padding(16.dp)
                )

                Log.e(TAG, "Product information not found in NoSQL database. ID: $selectedProductId")
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
                    Log.d(TAG, "Toggle latest prices: $showOnlyLatestPrices")
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
