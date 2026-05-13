package Views.FragId3_DialogVendeurAfficheurInfosProduit

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import V.DiviseParSections.App.Shared.ViewModel.UiState
import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.A_MainListFragId3
import Views.FragId3_DialogVendeurAfficheurInfosProduit.C_PrixInfosProduit.Details
import Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects.ProductNameSection3
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.VendeurAfficheurInfosProduitViewModel
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun A_VendeurAfficheurInfosProduit_FragmentMainId3(
    uiState: UiState,
    viewModel: VendeurAfficheurInfosProduitViewModel = koinViewModel(),
    viewModelHeadViewModel: HeadViewModel,
    viewModelInitApp: ViewModelInitApp,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    lockExpandedPrices: Boolean,
    onToggleLockExpandedPricex: () -> Unit,
    currentClient: M2Client?,
    clickedCouleurIndex: Int,
    onFermDialoge: () -> Unit,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
) {
    val currentSale by viewModelHeadViewModel.currentSaleInWindows.collectAsState()
    val articlesBaseStats = currentSale?.let { sale ->
        uiState.articlesBasesStatTables.find { it.id.toLong() == sale.idArticle }
    }

    var isDetailsVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { isDetailsVisible = true }

    currentSale?.let {
        MainUi(
            viewModel = viewModel,
            currentSale = it,
            currentClient = currentClient,
            viewModelInitApp = viewModelInitApp,
            modifier = modifier,
            articlesBaseStats = articlesBaseStats,
            viewModelHeadViewModel = viewModelHeadViewModel,
            isDetailsVisible = isDetailsVisible,
            onDismiss = onDismiss,
            uiState = uiState,
            lockExpandedPrices = lockExpandedPrices,
            onToggleLockExpandedPricex = onToggleLockExpandedPricex,
            colorsArticlesTabelleModele = viewModelHeadViewModel._uiState.value.colorsArticlesTabelleModel,
            clickedCouleurIndex = clickedCouleurIndex,
            onPourFermeWindows = onFermDialoge,viewModelNewProtoPatterns=viewModelNewProtoPatterns
        )
    }
}

@Composable
fun ModernToastMessageLo(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(3000) // Auto dismiss after 3 seconds
        onDismiss()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEF4444) // Modern red color
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "تحذير",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MainUi(
    viewModel: VendeurAfficheurInfosProduitViewModel,
    currentSale: SoldArticlesTabelle,
    currentClient: M2Client?,
    viewModelInitApp: ViewModelInitApp,
    modifier: Modifier = Modifier,
    articlesBaseStats: M01Produit?,
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
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns
) {
    // Toast state management inside MainUi
    var showToast by remember { mutableStateOf(false) }

    val idProduitActuelle = currentSale.idArticle
    val getter = viewModel.getter
    val onVentBonVent =  viewModel.aCentral.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M8BonVent

    if (onVentBonVent == null) {
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
    val progressValue = getter.loadingProgress
    val isLoading = (progressValue ?: 0f) < 1.0f

    // Safe handling for client ID
    val clientOuSonMarqueMapEstOuvert = viewModel.aCentral.focusedActiveValuesFacade.focusedValuesGetter.activeOnVentM2ClientInfos
    if (clientOuSonMarqueMapEstOuvert == null) {
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
        key2 = clientOuSonMarqueMapEstOuvert
    ) {
        val produitActuelle = currentSale.idArticle
        val existing_1_2_ProduitAcheteOperation =
            repositorysModel.repositoryC2_ProduitAcheteOperation.modelDatasSnapList.find {
                it.produitAcheterID == produitActuelle &&
                        it.parent_1_3_TransactionCommercial == onVentBonVent.vid
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
                    parentIdClient = clientOuSonMarqueMapEstOuvert.id,
                    provisoireMonPrix = articlesBaseStats?.prixVent ?: 0.0,
                    parent_1_3_TransactionCommercial = onVentBonVent.vid
                )

                repositorysModel.repositoryC2_ProduitAcheteOperation.addDataAndReturneItVID(
                    newOperation
                )
                newVid
            }
    }

    Dialog(
        onDismissRequest = {
            // Show toast when user tries to dismiss improperly
            showToast = true
        },
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
                // Toast Animation Component - Must be first to appear on top
                AnimatedVisibility(
                    visible = showToast,
                    enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                            androidx.compose.animation.scaleIn(
                                initialScale = 0.8f,
                                animationSpec = tween(durationMillis = 300)
                            ),
                    exit = fadeOut(animationSpec = tween(durationMillis = 300)) +
                            androidx.compose.animation.scaleOut(
                                targetScale = 0.8f,
                                animationSpec = tween(durationMillis = 300)
                            ),
                    modifier = Modifier.zIndex(999f)
                ) {
                    ModernToastMessageLo(
                        message = "يرجى استخدام الأزرار لتحديد السعر",
                        onDismiss = { showToast = false }
                    )
                }

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
                                    viewModel = viewModel,
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
                    its_Affiche_InfoProduit_Dialog = true,
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
                    onClickAnulationButton = {
                        updateState(
                            viewModelInitApp = viewModelInitApp,
                            parentCompose_1_2_ProduitAcheteOperationVid = parentCompose_1_2_ProduitAcheteOperationVid,
                            neveauEtateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK,
                        )
                        onPourFermeWindows()
                    },
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
