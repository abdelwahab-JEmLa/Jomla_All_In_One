package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun TariffsButtons_TestID2(
    showLabels: Boolean = true,
    viewModel: TariffsButtonsViewModel_TestID2 = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val tarificationList = uiState.tarificationList
    val bonAchatList = uiState.bonAchatList
    val produitInfosList = uiState.produitInfosList

    // Add auto-refresh if no products are loaded but repository shows they exist
    LaunchedEffect(produitInfosList.size) {
        if (produitInfosList.isEmpty()) {
            Log.d("TariffsButtons", "No products found, waiting 500ms before retry")
            delay(500)
            viewModel.refreshTariffs()
        }
    }

    // Get first available product ID and bon ID from the data
    val firstProductId = if (produitInfosList.isNotEmpty()) produitInfosList[0].vid.toInt() else 1
    val firstBonId = if (bonAchatList.isNotEmpty()) bonAchatList[0].vid else 1L

    // Add logging to help debug data availability
    Log.d("TariffsButtons", "Product count: ${produitInfosList.size}, Bon count: ${bonAchatList.size}")
    Log.d("TariffsButtons", "Using productID: $firstProductId, bonID: $firstBonId")

    val shouldShowLoading = uiState.loadingProgress < 1f

    Box(modifier = Modifier.fillMaxWidth()) {
        if (shouldShowLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    progress = { uiState.loadingProgress },
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                )
                Text(
                    text = "Loading tariffs... ${(uiState.loadingProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else if (bonAchatList.isNotEmpty() && produitInfosList.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Debug information
                Text(
                    text = "Products: ${produitInfosList.size}, Bons: ${bonAchatList.size}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Main filter component rendering
                MainFilter(
                    produitInfosList = produitInfosList,
                    tarificationList = tarificationList,
                    bonAchatList = bonAchatList,
                    showLabels = showLabels,
                    filterProduiID = firstProductId,
                    filterBonID = firstBonId
                )
            }
        } else {
            // Show an informative message when data is not available
            Text(
                text = "Waiting for data... Products: ${produitInfosList.size}, Bons: ${bonAchatList.size}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp).align(Alignment.Center),
                textAlign = TextAlign.Center
            )

            // Log the error for debugging
            Log.e("TariffsButtons", "No data available to display. bonAchatList: ${bonAchatList.size}, produitInfosList: ${produitInfosList.size}")
        }
    }
}


