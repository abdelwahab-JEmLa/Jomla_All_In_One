package Views.FragId3_DialogVendeurAfficheurInfosProduit

import V.DiviseParSections.App.SectionID8.FloatingButtons.App.FragID1.Windows.PressistatntMainActivityButtons
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository._1_2_ProduitAcheteOperation
import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.A_MainListFragId3
import Views.FragId3_DialogVendeurAfficheurInfosProduit.C_PrixInfosProduit.Details
import Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects.ProductNameSection3
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.clientjetpack.Repositorys.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.compose.koinInject

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
    clickedCouleurIndex: Int,
    onFermDialoge: () -> Unit,
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
            clickedCouleurIndex = clickedCouleurIndex,
            onPourFermeWindows = onFermDialoge,
        )
    }
}

@Composable
fun MainUi(
    currentSale: SoldArticlesTabelle,
    currentClient: B_ClientsDataBase?,
    viewModelInitApp: ViewModelInitApp,
    modifier: Modifier = Modifier,
    articlesBaseStats: ArticlesBasesStatsTable?,
    colorsArticlesTabelleModel: List<ColorsArticlesTabelle>,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    isDetailsVisible: Boolean,
    onDismiss: () -> Unit,
    uiState: UiState,
    lockExpandedPrices: Boolean,
    onToggleLockExpandedPricex: () -> Unit,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>,
    _0_0_HeadSQLRepositorys: _0_0_HeadSQLRepositorys = koinInject(),
    clickedCouleurIndex: Int,
    onPourFermeWindows: () -> Unit,
) {
    val idProduitActuelle = currentSale.idArticle
    val parentCompose_1_3_BonAchatVid by
    _0_0_HeadSQLRepositorys.repositorys_Model.activeVId_1_3_TransactionCommercial.collectAsState()

    // Fixed access to progress value
    val progressValue by _0_0_HeadSQLRepositorys.progressRepo.collectAsState()
    val isLoading = progressValue < 1.0f

    val repositorysModel = _0_0_HeadSQLRepositorys.repositorys_Model

    val find = repositorysModel.repository_1_3_TransactionCommercial
        .modelDatasSnapList.find {
            it.vid == parentCompose_1_3_BonAchatVid
        }

    val idClientActuelleDepui1_3 =
        find?.clientAcheteurID

    var parentCompose_1_2_ProduitAcheteOperationVid by remember { mutableLongStateOf(0L) }

    LaunchedEffect(
        key1 = currentSale.idArticle,
        idClientActuelleDepui1_3
    ) {
        val produitActuelle = currentSale.idArticle
        val existing_1_2_ProduitAcheteOperation =
            repositorysModel.repositoryC2_ProduitAcheteOperation.modelDatasSnapList.find {
                it.produitAcheterID == produitActuelle
                        && it.parent_1_3_TransactionCommercial == parentCompose_1_3_BonAchatVid
            }
        parentCompose_1_2_ProduitAcheteOperationVid =
            if (existing_1_2_ProduitAcheteOperation != null) {
                repositorysModel.repositoryC2_ProduitAcheteOperation.updateUnSeulData(
                    existing_1_2_ProduitAcheteOperation.apply {
                        etateActuellementEst =
                            _1_2_ProduitAcheteOperation.EtateActuellementEst.PRESENTATION
                    }
                )
                existing_1_2_ProduitAcheteOperation.vid
            } else {
                val newVid =
                    repositorysModel.repositoryC2_ProduitAcheteOperation.modelDatasSnapList.maxOfOrNull { it.vid }
                        ?.plus(1) ?: 1

                // No need for null check since we're using 'by' to unwrap the State
                _1_2_ProduitAcheteOperation(
                    vid = newVid,
                    produitAcheterID = produitActuelle,
                    parentIdClient=idClientActuelleDepui1_3?:0,
                    provisoireMonPrix =articlesBaseStats?.monPrixVent ?:0.0,
                    parent_1_3_TransactionCommercial = parentCompose_1_3_BonAchatVid
                ).let {
                    repositorysModel.repositoryC2_ProduitAcheteOperation.addDataAndReturneItVID(
                        it
                    )
                }
                newVid
            }
    }

    Dialog(
        onDismissRequest = {  onDismiss() }, properties = DialogProperties(
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
                    ) {
                        articlesBaseStats?.let { stats ->
                            item {
                            //    Text(parentCompose_1_3_BonAchatVid.toString())
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
                                    clickedCouleurIndex = clickedCouleurIndex,
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

                }

                PressistatntMainActivityButtons(
                    cLenceDepuitDialogeAchate=  true,
                    onPourFermeWindows = {
                        updateState(
                            viewModelInitApp,
                            parentCompose_1_2_ProduitAcheteOperationVid,
                            _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME,
                            newProvisoirePrix =it.prixCurrency
                        )

                        viewModel.saveSaleTransactionToSoldAriclesList()
                        onDismiss()

                        onPourFermeWindows()
                    },
                    idProduitActuelle = idProduitActuelle,
                    parentCompose_1_3_BonAchatVid= parentCompose_1_3_BonAchatVid,
                    onClickAnulationButton = {
                        updateState(
                            viewModelInitApp,
                            parentCompose_1_2_ProduitAcheteOperationVid,
                            _1_2_ProduitAcheteOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK,
                        )
                    }
                )
            }
        }
    }
}

fun updateState(
    viewModelInitApp: ViewModelInitApp,
    parentCompose_1_2_ProduitAcheteOperationVid: Long,
    neveauEtateActuellementEst: _1_2_ProduitAcheteOperation.EtateActuellementEst,
    newProvisoirePrix: Double=0.0,
) {
    val rep = viewModelInitApp._1_2_ProduitAcheteOperation_Repository
    rep.modelDatasSnapList.find {
        it.vid == parentCompose_1_2_ProduitAcheteOperationVid
    }?.apply {
        etateActuellementEst = neveauEtateActuellementEst
        provisoireMonPrix=newProvisoirePrix
    }?.let { rep.updateUnSeulData(it) }
}
