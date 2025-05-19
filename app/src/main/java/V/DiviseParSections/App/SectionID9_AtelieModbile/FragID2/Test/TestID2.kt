package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun Test2Prev() {
    TariffsButtons_TestID2()
}

@Composable
private fun LoadingTariffItem(isLoading: Boolean = true) {
    if (!isLoading) return

    ElevatedCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.size(40.dp),
                containerColor = Color.Gray
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            }
            Text(
                "Loading...",
                modifier = Modifier
                    .background(Color.Gray)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}

data class UiState(
    var bonAchatList: List<BonAchatT2> = emptyList(),
    var produitInfosList: List<ArticlesBasesStatsTable> = emptyList(),
    var tarificationList: List<D_TarificationInfosT2> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isInitialSetupComplete: Boolean = false,
)

class TariffsButtonsViewModel_TestID2(
    private val appDatabase: AppDatabase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadTariffs()
    }

    private fun loadTariffs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val tariffs = testD_TarificationInfosT2()
                val produitInfos = testDataArticlesBasesStatsTable2()
                val bonAchat = testBonAchatT2()

                _uiState.update {
                    it.copy(
                        bonAchatList = bonAchat,
                        produitInfosList = produitInfos,
                        tarificationList = tariffs,
                        isLoading = false,
                        isInitialSetupComplete = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error loading tariffs",
                        isLoading = false
                    )
                }
            }
        }
    }
}

@Composable
fun TariffsButtons_TestID2(
    showLabels: Boolean = true,
    viewModel: TariffsButtonsViewModel_TestID2 = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val tarificationList = uiState.tarificationList
    val bonAchatList = uiState.bonAchatList
    val produitInfosList = uiState.produitInfosList

    val shouldShowLoading = uiState.isLoading && tarificationList.isEmpty()

    Box {
        if (shouldShowLoading) {
            LoadingTariffItem(isLoading = true)
        } else if (bonAchatList.isNotEmpty() && produitInfosList.isNotEmpty()) {
            Column {
                MainFilter(
                    tarificationList = tarificationList,
                    bonAchatList = bonAchatList,
                    produitInfosList = produitInfosList,
                    showLabels = showLabels
                )
            }
        }
    }
}

@Composable
fun MainFilter(
    tarificationList: List<D_TarificationInfosT2>,
    bonAchatList: List<BonAchatT2>,
    produitInfosList: List<ArticlesBasesStatsTable>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
) {
    // Fix TODO(1): Using filterBon = 1L instead of 2L since the first example in testBonAchatT2() has vid = 1
    val filterBonID = 1L
    val filterProduiID = 1

    // Fix for TODO(FIXME): Using proper state handling to fix the delegate issue
    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: BonAchatT2()
    }

    val filteredProduit = remember(produitInfosList, filterProduiID) {
        produitInfosList.find { it.idArticle == filterProduiID } ?: ArticlesBasesStatsTable()
    }

    val filteredTariffs = remember(tarificationList, filteredProduit, filteredBonAchat) {
        tarificationList.filter { tariff ->
            tariff.idProduit.toInt() == filteredProduit.idArticle &&
                    tariff.idParentBonAchat == filteredBonAchat.vid
        }
    }

    Column(modifier = modifier) {
        MainList(
            tariffs = filteredTariffs,
            showLabels = showLabels
        )
    }
}

@Composable
fun MainList(
    tariffs: List<D_TarificationInfosT2>,
    showLabels: Boolean,
    modifier: Modifier = Modifier
) {
    // Group tariffs by their type
    val tariffsGroupedByType = remember(tariffs) {
        tariffs.groupBy { it.typeTarificationEnumT2Correspond }
    }

    Column(modifier = modifier) {
        // Process each tarification type individually
        tariffsGroupedByType.forEach { (type, typeTariffs) ->
            TariffButtonItem(
                typeTarification = type,
                tariffs = typeTariffs,
                showLabels = showLabels
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun TariffButtonItem(
    typeTarification: TypeTarificationEnumT2,
    tariffs: List<D_TarificationInfosT2>,
    showLabels: Boolean
) {
    // Get the most recent tariff
    val latestTariff = tariffs.maxByOrNull { it.vidTimestamp }

    if (latestTariff == null) return

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val context = LocalContext.current
        val couleurButton = typeTarification.couleur

        FloatingActionButton(
            onClick = {
                // Show toast with the tariff value
                val typeName = typeTarification.name
                val message = "$typeName: ${latestTariff.prixCurrency}"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(40.dp),
            containerColor = couleurButton
        ) {
            typeTarification.iconVector?.let { iconVector ->
                Icon(
                    imageVector = iconVector,
                    contentDescription = null
                )
            }
        }

        if (showLabels) {
            ElevatedCard {
                // Get the name of the tariff type
                val typeName = typeTarification.name

                Text(
                    "${latestTariff.prixCurrency} $typeName",
                    modifier = Modifier
                        .background(couleurButton)
                        .padding(4.dp),
                    color = Color.White
                )

                // Debug logging for this specific tariff
                Log.d(
                    "TariffDisplay",
                    "Displaying tariff: type=$typeName, value=${latestTariff.prixCurrency}"
                )
            }
        }
    }
}
