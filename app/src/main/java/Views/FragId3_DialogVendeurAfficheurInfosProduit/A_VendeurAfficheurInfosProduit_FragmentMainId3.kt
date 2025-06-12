package Views.FragId3_DialogVendeurAfficheurInfosProduit

import P0_MainScreen.Main.Main.Settings.Windows.PressistatntMainActivityButtons
import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.A_MainListFragId3
import Views.FragId3_DialogVendeurAfficheurInfosProduit.C_PrixInfosProduit.Details
import Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects.ProductNameSection3
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.VendeurAfficheurInfosProduitViewModel
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
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
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun A_VendeurAfficheurInfosProduit_FragmentMainId3(
    uiState: UiState,
    viewModelFragment: VendeurAfficheurInfosProduitViewModel = koinViewModel(),
    viewModel: HeadViewModel,
    onDismiss: () -> Unit,
    reloadTrigger: Int,
    modifier: Modifier = Modifier, lockExpandedPrices: Boolean,
    onToggleLockExpandedPricex: () -> Unit,
    viewModelInitApp: ViewModelInitApp,
    currentClient: B_ClientInfosProtoJuin3?,
    clickedCouleurIndex: Int,
    onFermDialoge: () -> Unit,
) {
    val currentSale by viewModel.currentSaleInWindows.collectAsState()
    val articlesBaseStats = currentSale?.let { sale ->
        uiState.articlesBasesStatTables.find { it.id.toLong() == sale.idArticle }
    }

    var isDetailsVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { isDetailsVisible = true }


    currentSale?.let {
        MainUi(
            viewModelFragment=viewModelFragment,
            currentSale = it,
            currentClient = currentClient,
            viewModelInitApp = viewModelInitApp,
            modifier = modifier,
            articlesBaseStats = articlesBaseStats,
            colorsArticlesTabelleModel = uiState.colorsArticlesTabelleModel,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            isDetailsVisible = isDetailsVisible,
            onDismiss = onDismiss,
            uiState = uiState,
            lockExpandedPrices = lockExpandedPrices,
            onToggleLockExpandedPricex = onToggleLockExpandedPricex,
            colorsArticlesTabelleModele = viewModel._uiState.value.colorsArticlesTabelleModel,
            clickedCouleurIndex = clickedCouleurIndex,
            onPourFermeWindows = onFermDialoge,
        )
    }
}

@Composable
fun MainUi(
    viewModelFragment: VendeurAfficheurInfosProduitViewModel,
    currentSale: SoldArticlesTabelle,
    currentClient: B_ClientInfosProtoJuin3?,
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
    _0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3 = koinInject(),
    clickedCouleurIndex: Int,
    onPourFermeWindows: () -> Unit,
) {
    val idProduitActuelle = currentSale.idArticle
    val centralDatasHandler = viewModelFragment.centralDatasHandler
    val ouvertTransactionalCommercial = centralDatasHandler.ouvertTransactionCommercial
    val idOuvertTransactionalCommercial = centralDatasHandler.ouvertTransactionCommercial!!.vid

    val progressValue = centralDatasHandler.loadingProgress

    val isLoading = progressValue!! < 1.0f
    val idClientActuelleDepui1_3 = centralDatasHandler.clientOuSonMarqueMapEstOuvert!!.id

    val repositorysModel = _0_0_HeadSQLRepositorys.repositorys_Model
    var parentCompose_1_2_ProduitAcheteOperationVid by remember { mutableLongStateOf(0L) }


    LaunchedEffect(
        key1 = currentSale.idArticle,
        idClientActuelleDepui1_3
    ) {
        val produitActuelle = currentSale.idArticle
        val existing_1_2_ProduitAcheteOperation =
            repositorysModel.repositoryC2_ProduitAcheteOperation.modelDatasSnapList.find {
                it.produitAcheterID == produitActuelle
                        && it.parent_1_3_TransactionCommercial == idOuvertTransactionalCommercial
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

                _1_2_ProduitAcheteOperation(
                    vid = newVid,
                    produitAcheterID = produitActuelle,
                    parentIdClient = idClientActuelleDepui1_3 ?: 0,
                    provisoireMonPrix = articlesBaseStats?.prixVent ?: 0.0,
                    parent_1_3_TransactionCommercial = idOuvertTransactionalCommercial
                ).let {
                    repositorysModel.repositoryC2_ProduitAcheteOperation.addDataAndReturneItVID(
                        it
                    )
                }
                newVid
            }
    }

    Dialog(
        onDismissRequest = { onDismiss() }, properties = DialogProperties(
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
                                    viewModel = viewModel,
                                    currentSale = currentSale,
                                    stats = stats,
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
                    cLenceDepuitDialogeAchate = true,
                    onPourFermeWindows = {
                        updateState(
                            viewModelInitApp,
                            parentCompose_1_2_ProduitAcheteOperationVid,
                            _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME,
                            newProvisoirePrix = it.prixCurrency
                        )

                        viewModel.saveSaleTransactionToSoldAriclesList()
                        onDismiss()

                        onPourFermeWindows()
                    },
                    idProduitActuelle = idProduitActuelle,
                    parentCompose_1_3_BonAchatVid = idOuvertTransactionalCommercial,
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
    newProvisoirePrix: Double = 0.0,
) {
    val rep = viewModelInitApp._1_2_ProduitAcheteOperation_Repository
    rep.modelDatasSnapList.find {
        it.vid == parentCompose_1_2_ProduitAcheteOperationVid
    }?.apply {
        etateActuellementEst = neveauEtateActuellementEst
        provisoireMonPrix = newProvisoirePrix
    }?.let { rep.updateUnSeulData(it) }
}
