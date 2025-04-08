package Views.Package_4.SoldCartScreen

import Views.Package_4.SoldCartScreen.Views.CartSummaryCard
import Views.Package_4.SoldCartScreen.Views.OrderSuccessMessage
import Views.Package_4.SoldCartScreen.Views.getTotalQuantity
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views.MainScreen_APP2_ID_2
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.Models.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SoldCartScreen(
    viewModel: HeadViewModel,
    modifier: Modifier = Modifier,
    clientBuyerNow: B_ClientsDataBase? = null,
    uiState: UiState,
    onConfirmOrder: () -> Unit, viewModelInitApp: ViewModelInitApp
) {
    var showOrderSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Filter articles logic
    val filteredSoldArticles = uiState.soldArticlesModel
        .filterNotNull()
        .filter { soldArticle ->
            val hasQuantity = (
                    soldArticle.color1SoldQuantity +
                            soldArticle.color2SoldQuantity +
                            soldArticle.color3SoldQuantity +
                            soldArticle.color4SoldQuantity
                    ) > 0

            clientBuyerNow?.let { client ->
                soldArticle.clientSoldToItId == client.id && hasQuantity
            } ?: false
        }

    val totalPrice = filteredSoldArticles.sumOf { soldArticle ->
        uiState.articlesBasesStatTables
            .find { it.idArticle.toLong() == soldArticle.idArticle }
            ?.monPrixVent?.times(
                soldArticle.color1SoldQuantity +
                        soldArticle.color2SoldQuantity +
                        soldArticle.color3SoldQuantity +
                        soldArticle.color4SoldQuantity
            ) ?: 0.0
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CartSummaryCard(
                client = clientBuyerNow,
                itemCount = filteredSoldArticles.sumOf { it.getTotalQuantity() },
                totalPrice = totalPrice,
                onConfirmOrder = {
                    viewModelInitApp.viewModelScope.launch {
                        if (clientBuyerNow != null) {
                            viewModelInitApp.updateStatueClientParID(
                                clientBuyerNow.id,
                                B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.VENDU_A_LUI
                            )
                        }
                    }
                    scope.launch {
                        showOrderSuccess = true
                        delay(3000)
                        showOrderSuccess = false
                    }
                    onConfirmOrder()
                }
            )

            MainScreen_APP2_ID_2(
                modifier = Modifier.fillMaxSize()
            )
        }

        // Success Animation Overlay
        AnimatedVisibility(
            visible = showOrderSuccess,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            OrderSuccessMessage()
        }
    }
}
