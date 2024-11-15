package P3_DisplayeProductInfosToSeller.Main

import P2_EStorePresentationToClient.Ui.ProductNameSection
import P3_DisplayeProductInfosToSeller.Ui.ActionsButtonRow
import P3_DisplayeProductInfosToSeller.Ui.ColorsCardsP3
import P3_DisplayeProductInfosToSeller.Ui.Details
import P3_DisplayeProductInfosToSeller.Ui.confirmExitDialog
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
    modifier: Modifier = Modifier,
) {
    val currentSale = viewModel.currentSaleInWindows.collectAsState().value
    val articlesBaseStats = currentSale?.let { sale ->
        uiState.articlesBasesStatTables.find {
            it.idArticle.toLong() == sale.idArticle
        }
    }

    var isDetailsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isDetailsVisible = true
    }


    if (currentSale != null) {
        MainUi(
            modifier,
            articlesBaseStats,
            currentSale,
            viewModel,
            reloadTrigger,
            uiState,
            isDetailsVisible,
            onDismiss
        )
    }
}

@Composable
private fun MainUi(
    modifier: Modifier,
    articlesBaseStats: ArticlesBasesStatsTable?,
    currentSale: SoldArticlesTabelle,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    uiState: UiState,
    isDetailsVisible: Boolean,
    onDismiss: () -> Unit
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                articlesBaseStats?.let { stats ->
                    ProductNameSection(stats)

                    // Visual Divider with Label
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 3.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Text(
                            text = "اختر اللون",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    // Colors Selection with Animation
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically()
                    ) {
                        ColorsCardsP3(
                            currentSale = currentSale,
                            articlesBasesStatsTable = stats,
                            viewModel = viewModel,
                            relodeTigger = reloadTrigger,
                            uiState = uiState,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }

                    // Details Card with Animation
                    Details(isDetailsVisible, stats)
                }

                ActionsButtonRow(
                    // TODO:  fait que ca soit s affiche toutjoure au base du dialoge
                    //comme button bar et fait anime infenie du button طلب
                    //comme    LaunchedEffect(Unit) {
                    //        while(true) {
                    //            if(isRed) {
                    //                delay(700)
                    //                isRed = false
                    //            }  else {
                    //                delay(6000)
                    //                isRed = true
                    //            }
                    //        }
                    //    }
                    //
                    //    Card(
                    //        modifier = Modifier
                    //            .fillMaxSize()
                    //            .wrapContentHeight(),
                    //        elevation = CardDefaults.cardElevation(4.dp),
                    //        colors = CardDefaults.cardColors(
                    //            containerColor = animateColorAsState(
                    //                if (isRed) Color.Red else Color.White,
                    //                label = "backgroundColor"
                    //            ).value
                    //        )
                    onConfirm = {
                        viewModel.saveSaleTransactionToSoldAriclesList()
                        onDismiss()
                    },
                    onCancel = {
                        viewModel.deleteSoldArticle(currentSale.vid)
                        onDismiss()
                    },
                )
            }
        }
    }
}











