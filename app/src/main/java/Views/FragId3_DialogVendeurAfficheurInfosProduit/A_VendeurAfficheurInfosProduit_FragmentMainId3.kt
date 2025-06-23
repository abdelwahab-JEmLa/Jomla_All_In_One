package Views.FragId3_DialogVendeurAfficheurInfosProduit

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.A_MainListFragId3
import Views.FragId3_DialogVendeurAfficheurInfosProduit.C_PrixInfosProduit.Details
import Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects.ProductNameSection3
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.VendeurAfficheurInfosProduitViewModel
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.UiState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun A_VendeurAfficheurInfosProduit_FragmentMainId3(
    uiState: UiState,
    viewModel: VendeurAfficheurInfosProduitViewModel = koinViewModel(),
    viewModelHeadViewModel: HeadViewModel,
    viewModelInitApp: ViewModelInitApp,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier, lockExpandedPrices: Boolean,
    onToggleLockExpandedPricex: () -> Unit,
    currentClient: B_ClientInfosProtoJuin3?,
    clickedCouleurIndex: Int,
    onFermDialoge: () -> Unit,
) {
    val currentSale by viewModelHeadViewModel.currentSaleInWindows.collectAsState()
    val articlesBaseStats = currentSale?.let { sale ->
        uiState.articlesBasesStatTables.find { it.id.toLong() == sale.idArticle }
    }

    var isDetailsVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { isDetailsVisible = true }


    currentSale?.let {
        MainUi(
            viewModel=viewModel,
            viewModelHeadViewModel = viewModelHeadViewModel,
            viewModelInitApp = viewModelInitApp,
            currentSale = it,
            currentClient = currentClient,
            modifier = modifier,
            articlesBaseStats = articlesBaseStats,
            isDetailsVisible = isDetailsVisible,
            onDismiss = onDismiss,
            uiState = uiState,
            lockExpandedPrices = lockExpandedPrices,
            onToggleLockExpandedPricex = onToggleLockExpandedPricex,
            colorsArticlesTabelleModele = viewModelHeadViewModel._uiState.value.colorsArticlesTabelleModel,
            clickedCouleurIndex = clickedCouleurIndex,
            onPourFermeWindows = onFermDialoge,
        )
    }
}

@Composable
fun MainUi(
    viewModel: VendeurAfficheurInfosProduitViewModel,
    currentSale: SoldArticlesTabelle,
    currentClient: B_ClientInfosProtoJuin3?,
    viewModelInitApp: ViewModelInitApp,
    modifier: Modifier = Modifier,
    articlesBaseStats: ArticlesBasesStatsTable?,
    viewModelHeadViewModel: HeadViewModel,
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
    val centralDatasHandler = viewModel.centralDatasHandler
    val ouvertTransactionalCommercial = centralDatasHandler.ouvertTransactionCommercial

    if (ouvertTransactionalCommercial == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aucune transaction ouverte",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    // Safe null handling for progress
    val progressValue = centralDatasHandler.loadingProgress
    val isLoading = (progressValue ?: 0f) < 1.0f

    // Safe handling for client ID
    val clientOuSonMarqueMapEstOuvert = centralDatasHandler.clientOuSonMarqueMapEstOuvert
    val idClientActuelleDepui1_3 = clientOuSonMarqueMapEstOuvert?.id
    if (idClientActuelleDepui1_3 == null) {
        // Handle case where there's no client
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aucun client sélectionné",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    val repositorysModel = _0_0_HeadSQLRepositorys.repositorys_Model
    var parentCompose_1_2_ProduitAcheteOperationVid by remember { mutableLongStateOf(0L) }

    LaunchedEffect(
        key1 = currentSale.idArticle,
        key2 = idClientActuelleDepui1_3
    ) {
        val produitActuelle = currentSale.idArticle
        val existing_1_2_ProduitAcheteOperation =
            repositorysModel.repositoryC2_ProduitAcheteOperation.modelDatasSnapList.find {
                it.produitAcheterID == produitActuelle &&
                        it.parent_1_3_TransactionCommercial == ouvertTransactionalCommercial.vid
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
                    repositorysModel.repositoryC2_ProduitAcheteOperation.modelDatasSnapList
                        .maxOfOrNull { it.vid }?.plus(1) ?: 1L

                val newOperation = _1_2_ProduitAcheteOperation(
                    vid = newVid,
                    produitAcheterID = produitActuelle,
                    parentIdClient = idClientActuelleDepui1_3,
                    provisoireMonPrix = articlesBaseStats?.prixVent ?: 0.0,
                    parent_1_3_TransactionCommercial = ouvertTransactionalCommercial.vid
                )

                repositorysModel.repositoryC2_ProduitAcheteOperation.addDataAndReturneItVID(
                    newOperation
                )
                newVid
            }
    }

    Dialog(
        onDismissRequest = { onDismiss() },
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
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Chargement... ${((progressValue ?: 0f) * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        articlesBaseStats?.let { stats ->
                            item {
                                ProductNameSection3(
                                    stats, onToggleLockExpandedPricex
                                )
                            }

                            item {
                                // Debug log
                                android.util.Log.d(
                                    "DEBUG_VID",
                                    "Passing to A_MainListFragId3: $parentCompose_1_2_ProduitAcheteOperationVid"
                                )

                                A_MainListFragId3(
                                    viewModel= viewModel,
                                    viewModelHeadViewModel = viewModelHeadViewModel,
                                    viewModelInitApp = viewModelInitApp,
                                    currentSale = currentSale,
                                    stats = stats,
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
                                    viewModel = viewModelHeadViewModel,
                                    lockExpandedPrices = lockExpandedPrices,
                                    onToggleLockExpandedPricex = onToggleLockExpandedPricex
                                )
                            }
                        } ?: item {
                            // Handle case where articlesBaseStats is null
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Informations du produit non disponibles",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Action buttons
                PressistatntMainActivityButtons_Sec8FWinID1(
                    cLenceDepuitDialogeAchate = true,
                    onPourFermeWindows = { buttonResult ->
                        updateState(
                            viewModelInitApp = viewModelInitApp,
                            parentCompose_1_2_ProduitAcheteOperationVid = parentCompose_1_2_ProduitAcheteOperationVid,
                            neveauEtateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME,
                            newProvisoirePrix = buttonResult.prixCurrency
                        )

                        viewModelHeadViewModel.saveSaleTransactionToSoldAriclesList()
                        onDismiss()
                        onPourFermeWindows()
                    },
                    idProduitActuelle = idProduitActuelle,
                    onClickAnulationButton = {
                        updateState(
                            viewModelInitApp = viewModelInitApp,
                            parentCompose_1_2_ProduitAcheteOperationVid = parentCompose_1_2_ProduitAcheteOperationVid,
                            neveauEtateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK,
                        )
                        onPourFermeWindows()
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
