package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun TariffsButtons_TestID2(
    showLabels: Boolean = true,
    viewModel: TariffsButtonsViewModel_TestID2 = koinViewModel(),
) {
    val filterProduitID = 1
    val filterBonID = 1L

    val uiState by viewModel.uiState.collectAsState()

    val tarificationList = uiState.tarificationList
    val bonAchatList = uiState.bonAchatList
    val produitInfosList = uiState.produitInfosList

    val shouldShowLoading = uiState.loadingProgress < 1f && tarificationList.isEmpty()


    Box {
        if (shouldShowLoading) {
            LoadingTariffItem(uiState.loadingProgress)
        } else if (bonAchatList.isNotEmpty() && produitInfosList.isNotEmpty()) {
            Column {

                MainFilter(
                    produitInfosList = produitInfosList,
                    tarificationList = tarificationList,
                    bonAchatList = bonAchatList,
                    showLabels = showLabels,
                    filterProduiID = filterProduitID,
                    filterBonID = filterBonID
                )
            }
        }
    }
}
