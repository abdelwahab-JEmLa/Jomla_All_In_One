package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitNoSqlDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "FragmentMain"

@Composable
fun FragmentMain(
    viewModel: TarificationViewModel = koinViewModel(),
    produitSelectioneDuAncienDataBase: ArticlesBasesStatsTable,
) {
    Log.d(TAG, "FragmentMain: Starting with product ID: ${produitSelectioneDuAncienDataBase.idArticle}")

    val uiState by viewModel.uiState
    val productId = produitSelectioneDuAncienDataBase.idArticle.toLong()
    val clientId = viewModel.ancienRepoOuvertClientId

    Log.d(TAG, "FragmentMain: Product ID: $productId, Client ID: $clientId")

    var initialSetupComplete by remember { mutableStateOf(false) }

    LaunchedEffect(productId, clientId) {
        if (!initialSetupComplete) {
            Log.d(TAG, "FragmentMain: Starting initial setup")

            viewModel.ajouteSiExistePas_A_ProduitInfos(
                productId,
                produitSelectioneDuAncienDataBase,
            )
            Log.d(TAG, "FragmentMain: Product info added")

            viewModel.ajouteSiExistePas_B_ClientsDataBase()
            Log.d(TAG, "FragmentMain: Client database setup complete")

            viewModel.convertiseurNoSqlToSqlRepository.refreshNoSqlData()
            Log.d(TAG, "FragmentMain: NoSQL data refreshed")

            initialSetupComplete = true
            Log.d(TAG, "FragmentMain: Initial setup complete")
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

        // Use a single log statement to reduce overhead
        val logBuilder = StringBuilder()
        logBuilder.append("DATA COMPARISON REPORT\n")

        // Compare IDs and prices
        val realIds = typeTarificationsList.map { it.infosId }

        logBuilder.append("Real data IDs: $realIds\n")

        // Log differences between real and preview data



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

@Composable
fun MainList(
    viewModel: TarificationViewModel = koinViewModel(),
    typeTarificationsList: List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification>,
    showOnlyLatestPrices: Boolean,
    modifier: Modifier = Modifier
) {
    val sortedTypes = remember(typeTarificationsList) {
        typeTarificationsList.sortedBy { it.infosId }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        if (sortedTypes.isEmpty()) {
            EmptyStateMessage()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedTypes) { typeTarification ->
                    TarificationTypeCard(
                        viewModel = viewModel,
                        typeTarification = typeTarification,
                        showOnlyLatestPrices = showOnlyLatestPrices
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No pricing information available",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TarificationTypeCard(
    viewModel: TarificationViewModel,
    typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification,
    showOnlyLatestPrices: Boolean
) {
    val typeInfo = remember(typeTarification) {
        viewModel.get_C_TypeTarificationInfos(typeTarification)
    }

    val prices = remember(typeTarification, showOnlyLatestPrices) {
        if (showOnlyLatestPrices && typeTarification.PrixsCurrency.isNotEmpty()) {
            listOf(typeTarification.PrixsCurrency.maxByOrNull { it.vidTimestamp } ?: typeTarification.PrixsCurrency.first())
        } else {
            typeTarification.PrixsCurrency.sortedByDescending { it.vidTimestamp }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = typeInfo?.nom ?: "Type ${typeTarification.infosId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = { /* Add functionality for adding new price */ },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Add Price")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (prices.isNotEmpty()) {
                PricesList(prices = prices)
            } else {
                Text(
                    text = "No prices available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PricesList(prices: List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        prices.forEachIndexed { index, price ->
            PriceItem(
                price = price,
                isLatest = index == 0
            )

            if (index < prices.size - 1) {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
private fun PriceItem(
    price: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix,
    isLatest: Boolean
) {
    val formattedDate = remember(price.vidTimestamp) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(price.vidTimestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (isLatest) "Current Price" else "Historical Price",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isLatest)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "$${String.format("%.2f", price.valeur)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isLatest) FontWeight.Bold else FontWeight.Normal,
            color = if (isLatest)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}
