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
import androidx.compose.ui.tooling.preview.Preview
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
    val clientId = viewModel.ancienRepoOuvertClientId

    var initialSetupComplete by remember { mutableStateOf(false) }

    LaunchedEffect(productId, clientId) {
        if (!initialSetupComplete) {
            viewModel.ajouteSiExistePas_A_ProduitInfos(
                productId,
                produitSelectioneDuAncienDataBase,
            )

            viewModel.ajouteSiExistePas_B_ClientsDataBase()

            viewModel.convertiseurNoSqlToSqlRepository.refreshNoSqlData()

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

    LaunchedEffect(Unit) {
        val previewData = getPreviewTypeTarificationsList()

        // Use a single log statement to reduce overhead
        val logBuilder = StringBuilder()
        logBuilder.append("DATA COMPARISON REPORT\n")
        logBuilder.append("Real data size: ${typeTarificationsList.size}, Preview data size: ${previewData.size}\n")

        // Compare IDs and prices
        val realIds = typeTarificationsList.map { it.infosId }
        val previewIds = previewData.map { it.infosId }

        logBuilder.append("Real data IDs: $realIds\n")
        logBuilder.append("Preview data IDs: $previewIds\n")

        // Log differences between real and preview data
        val missingInPreview = realIds.filter { it !in previewIds }
        val missingInReal = previewIds.filter { it !in realIds }

        if (missingInPreview.isNotEmpty()) {
            logBuilder.append("IDs in real data but missing in preview: $missingInPreview\n")
        }

        if (missingInReal.isNotEmpty()) {
            logBuilder.append("IDs in preview data but missing in real: $missingInReal\n")
        }

        // Compare prices (only once)
        typeTarificationsList.forEach { type ->
            val prices = type.PrixsCurrency.map { it.valeur }
            logBuilder.append("Real data ID ${type.infosId} prices: $prices\n")
        }

        Log.d(TAG, logBuilder.toString())
    }

    var tarificationTypesProcessed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selectedClientId, key2 = selectedProductId) {
        if (!tarificationTypesProcessed) {
            val sqlDataList = viewModel.convertiseurNoSqlToSqlRepository.sqlRepository.modelListFlow.value

            val existingTarifications = if (sqlDataList.isNotEmpty()) {
                val sqlData = sqlDataList.first()
                sqlData.d_TarificationInfos.filter {
                    it.idClient == selectedClientId && it.idProduit == selectedProductId
                }
            } else emptyList()

            if (existingTarifications.isNotEmpty()) {

                viewModel.verifierAddNew_C_TypeTarificationInfos(typeTarificationsList)

                viewModel.convertiseurNoSqlToSqlRepository.refreshNoSqlData()
            }
            else if (typeTarificationsList.isNotEmpty()) {
                viewModel.verifierAddNew_C_TypeTarificationInfos(typeTarificationsList)

                viewModel.convertiseurNoSqlToSqlRepository.refreshNoSqlData()

                typeTarificationsList.forEach { typeTarification ->
                    viewModel.verifierAdd_D_TarificationInfos(typeTarification)
                }

                viewModel.convertiseurNoSqlToSqlRepository.refreshNoSqlData()
            }
            else if (selectedClientId > 0 && existingTarifications.isEmpty()) {
                viewModel.createDefaultTarificationIfNeeded(selectedClientId)
            } else if (selectedClientId <= 0) {
                // Invalid client ID
            } else {
                viewModel.convertiseurNoSqlToSqlRepository.refreshNoSqlData()
            }

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
                    // Using real data from database which now matches updated preview data
                    showOnlyLatestPrices = showOnlyLatestPrices,
                    modifier = Modifier.weight(1f)
                )
            } else if (selectedProduct != null) {
                Text(
                    text = "Product information available, attempting to load client data...",
                    modifier = Modifier.padding(16.dp)
                )

                LaunchedEffect(Unit) {
                    if (!tarificationTypesProcessed) {
                        viewModel.ajouteSiExistePas_B_ClientsDataBase()
                        tarificationTypesProcessed = true
                    }
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

// Extract the preview data creation to a separate function so it can be reused for logging comparison
fun getPreviewTypeTarificationsList(): List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification> {
    return listOf(
        ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
            vidTimestamp = System.currentTimeMillis(),
            infosId = 4, // Matches the real data ID
            PrixsCurrency = listOf(
                ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                    vidTimestamp = System.currentTimeMillis(),
                    valeur = 10.0 // Matches the real data price
                )
            )
        )
    )
}

@Preview
@Composable
private fun prevMainList() {
    // Use the extracted function to get preview data
    val previewTypeTarificationsList = getPreviewTypeTarificationsList()

    MainList(
        typeTarificationsList = previewTypeTarificationsList,
        showOnlyLatestPrices = false
    )
}

@Composable
fun MainList(
    viewModel: TarificationViewModel = koinViewModel(),
    typeTarificationsList: List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification>,
    showOnlyLatestPrices: Boolean,
    modifier: Modifier = Modifier
) {
    Text("")
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
