package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import android.content.Context
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun TariffsButtons_TestID2(
    viewModel: TariffsButtonsViewModel_TestID2 = koinViewModel(),
    showLabels: Boolean = true,
    filterProductId: Long = 0,
    filterBonId: Long = 0,
    fermeDialog: () -> Unit,
    cLenceDepuitDialogeAchate: Boolean = false,
) {
    var afficheButtons by remember { mutableStateOf(cLenceDepuitDialogeAchate) }
    val uiState by viewModel.uiState.collectAsState()

    val tarificationList = uiState.tariffsList
    val bonAchatList = uiState.bonAchatList
    val produitInfosList = uiState.produitInfosList
    val produitAcheteOperationList = uiState.produitAcheteOperationList
    Text("${produitAcheteOperationList.size}")
    LaunchedEffect(produitInfosList.size, suspendFunction1(produitInfosList, viewModel))

    val shouldShowLoading = uiState.isDataSyncing ||
            (uiState.loadingProgress > 0f && uiState.loadingProgress < 1f) ||
            (bonAchatList.isEmpty() && produitInfosList.isEmpty() && uiState.loadingProgress == 0f)

    val onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit = { typeTarification, latestTariffLocalData, context ->
        val typeName = typeTarification.name
        val message = "$typeName: ${latestTariffLocalData.prixCurrency}"
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        afficheButtons = false
        fermeDialog()
    }

    if (afficheButtons) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (shouldShowLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.loadingProgress > 0f) {
                        CircularProgressIndicator(
                            progress = { uiState.loadingProgress },
                            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                        )

                        val progressPercentage = (uiState.loadingProgress * 100).toInt()
                        val syncStatus = viewModel.getSyncStatus()

                        Text(
                            text = "$syncStatus $progressPercentage%",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        CircularProgressIndicator()
                        Text(
                            text = "Initializing...",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (bonAchatList.isNotEmpty() && produitInfosList.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MainFilter(
                        tarificationList = tarificationList,
                        bonAchatList = bonAchatList,
                        produitInfosList = produitInfosList,
                        showLabels = showLabels,
                        filterProduitID = filterProductId.toInt(),
                        filterBonID = filterBonId,
                        onClickPrixButton = onClickPrixButton
                    )
                }
            } else {
                val dataStatus = when {
                    produitInfosList.isEmpty() && bonAchatList.isEmpty() -> "Loading initial data..."
                    produitInfosList.isEmpty() -> "Loading products..."
                    bonAchatList.isEmpty() -> "Loading purchase orders..."
                    else -> "Preparing data..."
                }

                Text(
                    text = "$dataStatus\nProducts: ${produitInfosList.size}, Orders: ${bonAchatList.size}",
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

@Composable
private fun suspendFunction1(
    produitInfosList: SnapshotStateList<_2_1_ProduitsDataBase>,
    viewModel: TariffsButtonsViewModel_TestID2
): suspend CoroutineScope.() -> Unit = {
    if (produitInfosList.isEmpty()) {
        delay(500)
        viewModel.refreshTariffs()
    }
}
