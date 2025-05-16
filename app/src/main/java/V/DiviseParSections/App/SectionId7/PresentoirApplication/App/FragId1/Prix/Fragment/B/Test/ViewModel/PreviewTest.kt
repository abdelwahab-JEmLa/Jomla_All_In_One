package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.A.Test.formatTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.B.NoSQL.Model.ProduitNoSqlDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.B.NoSQL.Model.testDatasProduitNoSqlDataBase
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import org.koin.androidx.compose.koinViewModel

private const val TAG = "PreviewTest"

@Preview
@Composable
fun PreviewTest() {
    // Log at the start of the function to track when composition begins
    Log.d(TAG, "PreviewTest: Starting composition")
    Fragment()
}

@Composable
private fun Fragment(
    viewModel: TarificationViewModel = koinViewModel()
) {
    // Track when Fragment composition begins
    val startTime = System.currentTimeMillis()
    Log.d(TAG, "Fragment: Starting composition at $startTime")

    val uiState by viewModel.uiState

    // Log whenever uiState changes
    SideEffect {
        Log.d(TAG, "Fragment: UI State updated, isLoading=${uiState.isLoading}, " +
                "produits size=${uiState.outputModel.produits.size}, " +
                "time elapsed=${System.currentTimeMillis() - startTime}ms")
    }

    // Track lifecycle for debugging UI rendering
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> Log.d(TAG, "Fragment lifecycle: ON_CREATE")
                Lifecycle.Event.ON_START -> Log.d(TAG, "Fragment lifecycle: ON_START")
                Lifecycle.Event.ON_RESUME -> Log.d(TAG, "Fragment lifecycle: ON_RESUME, UI is visible")
                Lifecycle.Event.ON_PAUSE -> Log.d(TAG, "Fragment lifecycle: ON_PAUSE")
                Lifecycle.Event.ON_STOP -> Log.d(TAG, "Fragment lifecycle: ON_STOP")
                Lifecycle.Event.ON_DESTROY -> Log.d(TAG, "Fragment lifecycle: ON_DESTROY")
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Handle potential empty initial state with fallback data for preview
    var noSqlData by remember {
        mutableStateOf(
            if (uiState.outputModel.produits.isEmpty()) {
                Log.d(TAG, "Fragment: Using test data because uiState.outputModel is empty")
                testDatasProduitNoSqlDataBase()
            } else {
                Log.d(TAG, "Fragment: Using real data from uiState.outputModel")
                uiState.outputModel
            }
        )
    }

    // Update noSqlData when uiState.outputModel changes and is not empty
    LaunchedEffect(uiState.outputModel) {
        if (uiState.outputModel.produits.isNotEmpty()) {
            Log.d(TAG, "Fragment: LaunchedEffect - Updating noSqlData with ${uiState.outputModel.produits.size} products")
            noSqlData = uiState.outputModel
        }
    }

    var showOnlyLatestPrices by remember { mutableStateOf(false) }
    val selectedProductId by remember { mutableLongStateOf(1L) }
    val selectedClientId by remember { mutableLongStateOf(1L) }

    ClientJetPackTheme(darkTheme = true) {
        // Log right before MainScreen is called to track rendering time
        Log.d(TAG, "Fragment: About to render MainScreen with ${noSqlData.produits.size} products")

        MainScreen(
            noSqlData = noSqlData,
            selectedProductId = selectedProductId,
            selectedClientId = selectedClientId,
            showOnlyLatestPrices = showOnlyLatestPrices,
            onToggleLatestPrices = {
                showOnlyLatestPrices = !showOnlyLatestPrices
                Log.d(TAG, "Fragment: Toggled showOnlyLatestPrices to $showOnlyLatestPrices")
            }
        )

        // Log after MainScreen has been composed
        SideEffect {
            val renderTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "Fragment: MainScreen composed in $renderTime ms")
        }
    }
}

@Composable
fun MainScreen(
    noSqlData: ProduitNoSqlDataBase,
    selectedProductId: Long,
    selectedClientId: Long,
    showOnlyLatestPrices: Boolean,
    modifier: Modifier = Modifier,
    onToggleLatestPrices: () -> Unit,
) {
    // Log when MainScreen is composed
    Log.d(TAG, "MainScreen: Starting composition with ${noSqlData.produits.size} products")

    // Find the selected product and client
    val selectedProduct = noSqlData.produits.find { it.infosId == selectedProductId }
    val selectedClient = selectedProduct?.clientAchteurs?.find { it.infosId == selectedClientId }

    // Log selected product and client details for debugging
    if (selectedProduct == null) {
        Log.d(TAG, "MainScreen: Could not find product with ID $selectedProductId")
    } else {
        Log.d(TAG, "MainScreen: Found product with ID ${selectedProduct.infosId}, " +
                "has ${selectedProduct.clientAchteurs.size} clients")
    }

    if (selectedClient == null) {
        Log.d(TAG, "MainScreen: Could not find client with ID $selectedClientId for product ID $selectedProductId")
    } else {
        Log.d(TAG, "MainScreen: Found client with ID ${selectedClient.infosId}, " +
                "has ${selectedClient.typeTarification.size} tarification types")
    }

    // Extract all type tarifications for the selected product and client
    val typeTarificationsList = selectedClient?.typeTarification ?: emptyList()
    Log.d(TAG, "MainScreen: Using ${typeTarificationsList.size} tarification types for the UI")

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (selectedProduct != null && selectedClient != null) {
                NoSqlProductClientInfoCard(
                    produit = selectedProduct,
                    client = selectedClient
                )
            }

            MainList(
                typeTarificationsList = typeTarificationsList,
                selectedProductId = selectedProductId,
                selectedClientId = selectedClientId,
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
                onClick = onToggleLatestPrices,
                modifier = Modifier
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = "Toggle Latest Prices"
                )
            }
        }
    }

    // Log when MainScreen composition completes
    SideEffect {
        Log.d(TAG, "MainScreen: Composition complete")
    }
}

@Composable
fun MainList(
    typeTarificationsList: List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification>,
    selectedProductId: Long,
    selectedClientId: Long,
    showOnlyLatestPrices: Boolean,
    modifier: Modifier = Modifier
) {
    // Log when MainList is composed
    Log.d(TAG, "MainList: Starting composition with ${typeTarificationsList.size} tarification types")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(typeTarificationsList) { typeTarification ->
            NoSqlTarificationTypeSection(
                typeTarification = typeTarification,
                selectedProductId = selectedProductId,
                selectedClientId = selectedClientId,
                showOnlyLatestPrices = showOnlyLatestPrices,
            )
        }
    }

    // Log when MainList composition completes
    SideEffect {
        Log.d(TAG, "MainList: Composition complete")
    }
}

@Composable
fun NoSqlProductClientInfoCard(
    produit: ProduitNoSqlDataBase.Produit,
    client: ProduitNoSqlDataBase.Produit.ClientAchteur,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "Produit ID: ${produit.infosId}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Client ID: ${client.infosId}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val (date, time) = formatTimestamp(produit.vidTimestamp)
        Text(
            text = "Date: $date $time",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun NoSqlTarificationTypeSection(
    typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification,
    selectedProductId: Long,
    selectedClientId: Long,
    showOnlyLatestPrices: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(typeTarification.vidTimestamp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Type ID: ${typeTarification.infosId}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$date $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val pricesToShow = if (showOnlyLatestPrices) {
            // Get only the most recent price by timestamp
            typeTarification.PrixsCurrency
                .maxByOrNull { it.vidTimestamp }
                ?.let { listOf(it) } ?: emptyList()
        } else {
            // Show all prices sorted by timestamp
            typeTarification.PrixsCurrency
                .sortedByDescending { it.vidTimestamp }
        }

        pricesToShow.forEach { prix ->
            NoSqlTarificationItem(prix = prix)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun NoSqlTarificationItem(
    prix: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(prix.vidTimestamp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Prix: ${prix.valeur}€",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$date $time",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
