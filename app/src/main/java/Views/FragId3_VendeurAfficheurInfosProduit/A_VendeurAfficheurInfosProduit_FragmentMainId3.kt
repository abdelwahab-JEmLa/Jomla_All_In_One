package Views.FragId3_VendeurAfficheurInfosProduit

import Views.FragId3_VendeurAfficheurInfosProduit.B_CouleursAfficheur.B_CouleursAfficheur_MainListFid3
import Views.FragId3_VendeurAfficheurInfosProduit.Ui.Main.Details
import Views.FragId3_VendeurAfficheurInfosProduit.Ui.Objects.ActionsButtonRow
import Views.FragId3_VendeurAfficheurInfosProduit.Ui.Objects.ProductNameSection3
import Views.FragId3_VendeurAfficheurInfosProduit.Ui.Objects.confirmExitDialog
import Z_MasterOfApps.Kotlin.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.SoldArticlesTabelle
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_3._DisplayeProductInfosToSeller
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
fun A_VendeurAfficheurInfosProduit_FragmentMainId3(
    uiState: UiState,
    viewModel: HeadViewModel,
    onDismiss: () -> Unit,
    reloadTrigger: Int,
    modifier: Modifier = Modifier, lockExpandedPrices: Boolean,
    onToggleLockExpandedPricex: () -> Unit, viewModelInitApp: ViewModelInitApp,
    currentClient: B_ClientsDataBase?,
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
    currentClient: B_ClientsDataBase?,
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
                        B_CouleursAfficheur_MainListFid3(
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

                            _DisplayeProductInfosToSeller(viewModelInitApp)
                                .onClickOnMain(
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



