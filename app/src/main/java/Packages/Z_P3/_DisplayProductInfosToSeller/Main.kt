package Packages.Z_P3._DisplayProductInfosToSeller

import Packages.Z_P3.Ui.Main.ColorSelectionSection
import Packages.Z_P3.Ui.Main.Details
import Packages.Z_P3.Ui.Objects.ActionsButtonRow
import Packages.Z_P3.Ui.Objects.ProductNameSection3
import Packages.Z_P3.Ui.Objects.confirmExitDialog
import Z_MasterOfApps.Kotlin.ViewModel.Actions.F3_DisplayProductInfosToSeller._F3_DisplayeProductInfosToSeller
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.clientjetpack.Models.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel

@Composable
fun P3DisplayeProductInfosToSeller(
    uiState: UiState,
    viewModel: HeadViewModel,
    onDismiss: () -> Unit,
    reloadTrigger: Int,
    modifier: Modifier = Modifier, lockExpandedPrices: Boolean,
    onToggleLockExpandedPricex: () -> Unit, viewModelInitApp: ViewModelInitApp,
    currentClient: ClientsModel?,
) {
    val currentSale by viewModel.currentSaleInWindows.collectAsState()
    val articlesBaseStats = currentSale?.let { sale ->
        uiState.articlesBasesStatTables.find { it.idArticle.toLong() == sale.idArticle }
    }

    var isDetailsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isDetailsVisible = true }

    currentSale?.let {
        MainUi(
            modifier = modifier,
            articlesBaseStats = articlesBaseStats,
            colorsArticlesTabelleModel = uiState.colorsArticlesTabelleModel,
            currentSale = it,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            isDetailsVisible = isDetailsVisible,
            onDismiss = onDismiss,
            uiState = uiState,
            lockExpandedPrices = lockExpandedPrices,
            onToggleLockExpandedPricex = onToggleLockExpandedPricex,
            viewModelInitApp = viewModelInitApp,
            currentClient = currentClient,
            colorsArticlesTabelleModele = viewModel._uiState.value.colorsArticlesTabelleModel
        )

    }
}

@Composable
fun MainUi(
    modifier: Modifier = Modifier,
    articlesBaseStats: ArticlesBasesStatsTable?,
    colorsArticlesTabelleModel: List<ColorsArticlesTabelle>,
    currentSale: SoldArticlesTabelle,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    isDetailsVisible: Boolean,
    onDismiss: () -> Unit,
    uiState: UiState,
    lockExpandedPrices: Boolean,
    onToggleLockExpandedPricex: () -> Unit,
    viewModelInitApp: ViewModelInitApp,
    currentClient: ClientsModel?,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    showConfirmDialog = confirmExitDialog(showConfirmDialog, viewModel, onDismiss)

    Dialog(
        onDismissRequest = { showConfirmDialog = true },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 80.dp) // Add padding for the action buttons
                ) {
                    articlesBaseStats?.let { stats ->
                        ProductNameSection3(stats)
                        ColorSelectionSection(
                            currentSale = currentSale,
                            stats = stats,
                            colorsArticlesTabelleModel = colorsArticlesTabelleModel,
                            viewModel = viewModel,
                            reloadTrigger = reloadTrigger, viewModelInitApp = viewModelInitApp,
                            currentClient = currentClient,
                            colorsArticlesTabelleModele = colorsArticlesTabelleModele,
                        )
                        Details(
                            isDetailsVisible,
                            stats,
                            uiState,
                            viewModel,
                            lockExpandedPrices,
                            onToggleLockExpandedPricex
                        )
                    }
                }

                // Updated action buttons section with fixed cancel logic
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    ActionsButtonRow(
                        modifier = Modifier.padding(8.dp),
                        onConfirm = {
                            viewModel.saveSaleTransactionToSoldAriclesList()
                            onDismiss()
                        },
                        onCancel = {
                            viewModel.deleteSoldArticle(currentSale.vid)
                            onDismiss()
                            _F3_DisplayeProductInfosToSeller(viewModelInitApp).onClickOnMain(
                                viewModelInitApp,
                                currentSale,
                                currentClient
                            )
                        }
                    )
                }
            }
        }
    }
}



