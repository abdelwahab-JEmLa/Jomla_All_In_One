package Views.FragId3_DialogVendeurAfficheurInfosProduit

import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.A_MainListFragId3
import Views.FragId3_DialogVendeurAfficheurInfosProduit.C_PrixInfosProduit.Details
import Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects.ActionsButtonRow
import Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects.ProductNameSection3
import Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects.confirmExitDialog
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Model._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Model._1_3_BonAchat
import Z_CodePartageEntreApps.Model._1_4_PeriodeVent
import Z_CodePartageEntreApps.Model._1_5_Vendeur
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_3._DisplayeProductInfosToSeller
import android.os.Build
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
            colorsArticlesTabelleModele = viewModel._uiState.value.colorsArticlesTabelleModel
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
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>
) {
    // Check loading status from all repositories
    val progress1 by viewModelInitApp._1_1_CouleurAcheteOperation_Repository.progressRepo.collectAsState()
    val progress2 by viewModelInitApp._1_2_ProduitAcheteOperation_Repository.progressRepo.collectAsState()
    val progress3 by viewModelInitApp._1_3_BonAchat_Repository.progressRepo.collectAsState()
    val progress4 by viewModelInitApp._1_4_PeriodeVent_Repository.progressRepo.collectAsState()
    val progress5 by viewModelInitApp._1_5_Vendeur_Repository.progressRepo.collectAsState()

    val isLoading = progress1 < 1.0f || progress2 < 1.0f || progress3 < 1.0f ||
            progress4 < 1.0f || progress5 < 1.0f

    var parentCompose_1_5_VendeurId by remember { mutableLongStateOf(0) }
    var parentCompose_1_4_PeriodeVentVid by remember { mutableLongStateOf(0) }
    var parentCompose_1_3_BonAchatVid by remember { mutableLongStateOf(0) }
    var parentCompose_1_2_ProduitAcheteOperationVid by remember { mutableLongStateOf(0) }

    LaunchedEffect(Unit) {

        val deviceModelNom = Build.MODEL
        val existingVendor = viewModelInitApp._1_5_Vendeur_Repository
            .modelDatasSnapList.find { it.deviceModelNom == deviceModelNom }
        parentCompose_1_5_VendeurId = if (existingVendor != null) {
            existingVendor.vid
        } else {
            val newVid = viewModelInitApp._1_5_Vendeur_Repository.modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1
            viewModelInitApp._1_5_Vendeur_Repository.addData(
                _1_5_Vendeur(
                    vid = newVid,
                    deviceModelNom = deviceModelNom
                )
            )
            newVid
        }

        val currenteDateInString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val existing_1_4_PeriodeVent = viewModelInitApp._1_4_PeriodeVent_Repository.modelDatasSnapList
            .find {
                it.endDateInString == ""
            }
        parentCompose_1_4_PeriodeVentVid = if (existing_1_4_PeriodeVent != null) {
            existing_1_4_PeriodeVent.vid
        } else {
            val newVid = viewModelInitApp._1_4_PeriodeVent_Repository
                .modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1
            viewModelInitApp._1_4_PeriodeVent_Repository.addData(
                _1_4_PeriodeVent(
                    vid = newVid,
                    startDateInString = currenteDateInString,
                    vendeur_ParentVID=parentCompose_1_5_VendeurId
                )
            )
            newVid
        }

        val currentClientId = currentClient?.id ?: 1
        val existing_1_3_BonAchat = viewModelInitApp._1_3_BonAchat_Repository.modelDatasSnapList.find {
            it.clientAcheteurID == currentClientId
                    && it.parent_1_4_PeriodeVentVid == parentCompose_1_4_PeriodeVentVid
        }
        parentCompose_1_3_BonAchatVid = if (existing_1_3_BonAchat != null) {
            existing_1_3_BonAchat.vid
        } else {
            val newVid = viewModelInitApp._1_3_BonAchat_Repository
                .modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1
            viewModelInitApp._1_3_BonAchat_Repository.addData(
                _1_3_BonAchat(
                    vid = newVid,
                    clientAcheteurID = currentClientId ,
                    parent_1_4_PeriodeVentVid=parentCompose_1_4_PeriodeVentVid
                )
            )
            newVid
        }

        val produitActuelle = currentSale.idArticle
        val existing_1_2_ProduitAcheteOperation = viewModelInitApp._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList.find {
            it.produitAcheterID == produitActuelle
                    && it.parent_1_3_BonAchat == parentCompose_1_3_BonAchatVid
        }
        parentCompose_1_2_ProduitAcheteOperationVid = if (existing_1_2_ProduitAcheteOperation != null) {
            existing_1_2_ProduitAcheteOperation.vid
        } else {
            val newVid = viewModelInitApp._1_2_ProduitAcheteOperation_Repository
                .modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1
            viewModelInitApp._1_2_ProduitAcheteOperation_Repository.add(
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
    showConfirmDialog = confirmExitDialog(
        viewModelInitApp, showConfirmDialog, viewModel,
    ) {
        onDismiss()
        _DisplayeProductInfosToSeller(viewModelInitApp)
            .onClickOnMain(
                viewModelInitApp,
                currentSale,
                currentClient
            )
    }

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
                // Show loading indicator when data is still loading
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
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
                                    stats,
                                    onToggleLockExpandedPricex
                                )
                            }

                            item {
                                A_MainListFragId3(
                                    currentSale = currentSale,
                                    stats = stats,
                                    colorsArticlesTabelleModel = colorsArticlesTabelleModel,
                                    viewModel = viewModel,
                                    reloadTrigger = reloadTrigger,
                                    viewModelInitApp = viewModelInitApp,
                                    currentClient = currentClient,
                                    colorsArticlesTabelleModele = colorsArticlesTabelleModele,
                                    parentCompose_1_2_ProduitAcheteOperationVid=parentCompose_1_2_ProduitAcheteOperationVid,
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
                            modifier = Modifier.padding(8.dp),
                            currentSale=currentSale,
                            currentClient=currentClient,
                            onConfirm = {
                                viewModel.saveSaleTransactionToSoldAriclesList()
                                onDismiss()
                            },
                            onDismiss = onDismiss,
                            viewModel=viewModel,
                            viewModelInitApp=viewModelInitApp
                        )
                    }
                }
            }
        }
    }
}
