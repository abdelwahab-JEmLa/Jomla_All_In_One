package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun TariffsButtons_TestID2(
    viewModel: TariffsButtonsViewModel_TestID2 = koinViewModel(),
    showLabels: Boolean = true,
    filterProductId: Int = 4,
    filterBonId: Long = 1,
    fermeDialog: () -> Unit,
) {
    var afficheButtons by remember { mutableStateOf(true) }

    val uiState by viewModel.uiState.collectAsState()

    val tarificationList =
        testD_TarificationInfosT2()
    // uiState.tariffsList

    val bonAchatList = uiState.bonAchatList
    val produitInfosList = uiState.produitInfosList

    LaunchedEffect(produitInfosList.size) {
        if (produitInfosList.isEmpty()) {
            delay(500)
            viewModel.refreshTariffs()
        }
    }

    val shouldShowLoading = uiState.loadingProgress < 1f

    Text("${tarificationList.size}")
    if (afficheButtons) {
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

                    MainFilter(
                        tarificationList = tarificationList,
                        bonAchatList = bonAchatList,
                        produitInfosList = produitInfosList,
                        showLabels = showLabels,
                        filterProduitID = filterProductId,
                        filterBonID = filterBonId,
                        onClickPrixButton = {
                            {
                                { typeTarification, latestTariffLocalData, context ->
                                    {
                                        val typeName = typeTarification.name
                                        val message =
                                            "$typeName: ${latestTariffLocalData.prixCurrency}"
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        afficheButtons = false
                                        fermeDialog()
                                    }
                                }
                            }
                        }
                    )
                }
            } else {
                Text(
                    text = "Waiting for data... Products: ${produitInfosList.size}, Bons: ${bonAchatList.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
