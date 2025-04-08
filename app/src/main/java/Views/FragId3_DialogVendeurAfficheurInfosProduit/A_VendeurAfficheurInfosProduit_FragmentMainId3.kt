package Views.FragId3_DialogVendeurAfficheurInfosProduit

import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.A_MainListFragId3
import Views.FragId3_DialogVendeurAfficheurInfosProduit.C_PrixInfosProduit.Details
import Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects.ActionsButtonRow
import Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects.ConfirmExitDialog
import Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects.ProductNameSection3
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Model._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Model._1_3_BonAchat
import Z_CodePartageEntreApps.Model._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_3._DisplayeProductInfosToSeller
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun A_VendeurAfficheurInfosProduit_FragmentMainId3(
    uiState: UiState,
    viewModel: HeadViewModel,
    onDismiss: () -> Unit,
    reloadTrigger: Int,
    modifier: Modifier = Modifier, lockExpandedPrices: Boolean,
    onToggleLockExpandedPricex: () -> Unit,
    viewModelInitApp: ViewModelInitApp,
    currentClient: B_ClientsDataBase?,
) {
    val currentSale by viewModel.currentSaleInWindows.collectAsState()
    val articlesBaseStats = currentSale?.let { sale ->
        uiState.articlesBasesStatTables.find { it.idArticle.toLong() == sale.idArticle }
    }

    var isDetailsVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { isDetailsVisible = true }


    currentSale?.let {
        MainUi(
            viewModelInitApp = viewModelInitApp,
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
            currentClient = currentClient,
            colorsArticlesTabelleModele = viewModel._uiState.value.colorsArticlesTabelleModel,
        )
    }
}

@Composable
fun MainUi(
    viewModelInitApp: ViewModelInitApp,
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
    currentClient: B_ClientsDataBase?,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject()
) {
    // Fixed access to progress value
    val progressValue by _0_0_HeadOfRepositorys_Repository.progressRepo.collectAsState()
    val isLoading = progressValue < 1.0f

    val parentCompose_1_5_VendeurId by _0_0_HeadOfRepositorys_Repository.repositorys_Model._1_5_Vendeur_Repository.active_1_5_VendeurId.collectAsState()
    val parentCompose_1_5_VendeurId by _0_0_HeadOfRepositorys_Repository.repositorys_Model._1_5_Vendeur_Repository.active_1_5_VendeurId.collectAsState()
    val parentCompose_1_5_VendeurId by _0_0_HeadOfRepositorys_Repository.repositorys_Model._1_5_Vendeur_Repository.active_1_5_VendeurId.collectAsState()

    var parentCompose_1_4_PeriodeVentVid by remember { mutableLongStateOf(0L) }
    var parentCompose_1_3_BonAchatVid by remember { mutableLongStateOf(0L) }
    var parentCompose_1_2_ProduitAcheteOperationVid by remember { mutableLongStateOf(0L) }

    LaunchedEffect(
        key1 = currentSale.idArticle
    ) {
        val produitActuelle = currentSale.idArticle
        val existing_1_2_ProduitAcheteOperation =
            _0_0_HeadOfRepositorys_Repository.repositorys_Model._1_2_ProduitAcheteOperation_Repository?.modelDatasSnapList?.find {
                it.produitAcheterID == produitActuelle && it.parent_1_3_BonAchat == parentCompose_1_3_BonAchatVid
            }
        parentCompose_1_2_ProduitAcheteOperationVid =
            if (existing_1_2_ProduitAcheteOperation != null) {
                _0_0_HeadOfRepositorys_Repository.repositorys_Model._1_2_ProduitAcheteOperation_Repository?.updateUnSeulData(
                    existing_1_2_ProduitAcheteOperation.apply {
                        etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.PRESENTATION
                    }
                )
                existing_1_2_ProduitAcheteOperation.vid
            } else {
                val newVid =
                    _0_0_HeadOfRepositorys_Repository.repositorys_Model._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList?.maxOfOrNull { it.vid }
                        ?.plus(1) ?: 1
                _0_0_HeadOfRepositorys_Repository.repositorys_Model._1_2_ProduitAcheteOperation_Repository.add(
                    _1_2_ProduitAcheteOperation(
                        vid = newVid,
                        produitAcheterID = produitActuelle,
                        parent_1_3_BonAchat = parentCompose_1_3_BonAchatVid
                    )
                )
                newVid
            }
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    showConfirmDialog = ConfirmExitDialog(
        viewModelInitApp,
        parentCompose_1_2_ProduitAcheteOperationVid = parentCompose_1_2_ProduitAcheteOperationVid,
        showConfirmDialog = showConfirmDialog,
        viewModel = viewModel,
    ) {
        onDismiss()
        _DisplayeProductInfosToSeller(viewModelInitApp).onClickOnMain(
            viewModelInitApp, currentSale, currentClient
        )
    }

    Dialog(
        onDismissRequest = { showConfirmDialog = true }, properties = DialogProperties(
            usePlatformDefaultWidth = false, decorFitsSystemWindows = true
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
                // Show loading indicator when data is still loading
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp)
                    ) {
                        articlesBaseStats?.let { stats ->
                            item {
                                ProductNameSection3(
                                    stats, onToggleLockExpandedPricex
                                )
                            }

                            item {
                                // Add this debug log before passing the value
                                android.util.Log.d(
                                    "DEBUG_VID",
                                    "Passing to A_MainListFragId3: $parentCompose_1_2_ProduitAcheteOperationVid"
                                )

                                A_MainListFragId3(
                                    currentSale = currentSale,
                                    stats = stats,
                                    colorsArticlesTabelleModel = colorsArticlesTabelleModel,
                                    viewModel = viewModel,
                                    reloadTrigger = reloadTrigger,
                                    viewModelInitApp = viewModelInitApp,
                                    currentClient = currentClient,
                                    colorsArticlesTabelleModele = colorsArticlesTabelleModele,
                                    parentCompose_1_2_ProduitAcheteOperationVid = parentCompose_1_2_ProduitAcheteOperationVid,
                                )
                            }

                            item {
                                Details(
                                    isDetailsVisible = isDetailsVisible,
                                    article = stats,
                                    uiState = uiState,
                                    viewModel = viewModel,
                                    lockExpandedPrices = lockExpandedPrices,
                                    onToggleLockExpandedPricex = onToggleLockExpandedPricex
                                )
                            }
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
                            parentCompose_1_2_ProduitAcheteOperationVid = parentCompose_1_2_ProduitAcheteOperationVid,
                            modifier = Modifier.padding(8.dp),
                            currentSale = currentSale,
                            currentClient = currentClient,
                            onConfirm = {
                                viewModel.saveSaleTransactionToSoldAriclesList()
                                onDismiss()
                            },
                            onDismiss = onDismiss,
                            viewModel = viewModel,
                            viewModelInitApp = viewModelInitApp
                        )
                    }
                }
            }
        }
    }
}

fun updateState(
    viewModelInitApp: ViewModelInitApp,
    parentCompose_1_2_ProduitAcheteOperationVid: Long,
    neveauEtateActuellementEst: _1_2_ProduitAcheteOperation.EtateActuellementEst,
) {
    val rep = viewModelInitApp._1_2_ProduitAcheteOperation_Repository
    rep.modelDatasSnapList.find {
        it.vid == parentCompose_1_2_ProduitAcheteOperationVid
    }?.apply {
        etateActuellementEst = neveauEtateActuellementEst
    }?.let { rep.updateUnSeulData(it) }
}
