package P3_DisplayeProductInfosToSeller.Main

import P2_EStorePresentationToClient.Ui.ProductNameSection
import P3_DisplayeProductInfosToSeller.Ui.Objects.ActionsButtonRow
import P3_DisplayeProductInfosToSeller.Ui.ColorSelectionSection
import P3_DisplayeProductInfosToSeller.Ui.Objects.Details
import P3_DisplayeProductInfosToSeller.Ui.Objects.DividerWithLabel
import P3_DisplayeProductInfosToSeller.Ui.Objects.confirmExitDialog
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
    modifier: Modifier = Modifier,
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
            onDismiss = onDismiss
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
    onDismiss: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    showConfirmDialog = confirmExitDialog(showConfirmDialog, viewModel, onDismiss)

    Dialog(
        onDismissRequest = { showConfirmDialog = true },
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = true)
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                articlesBaseStats?.let { stats ->
                    ProductNameSection(stats)
                    DividerWithLabel()
                    ColorSelectionSection(
                        currentSale = currentSale,
                        stats = stats,
                        colorsArticlesTabelleModel = colorsArticlesTabelleModel,
                        viewModel = viewModel,
                        reloadTrigger = reloadTrigger
                    )
                    Details(isDetailsVisible, stats)
                }

                ActionsButtonRow(  // TODO: fait que ca soit comme
                    //a ne pase ce cache par le scroll tou s affiche on flotantn
                    onConfirm = {
                        viewModel.saveSaleTransactionToSoldAriclesList()
                        onDismiss()
                    },
                    onCancel = {
                        viewModel.deleteSoldArticle(currentSale.vid)
                        onDismiss()
                    }
                )
            }
        }
    }
}






