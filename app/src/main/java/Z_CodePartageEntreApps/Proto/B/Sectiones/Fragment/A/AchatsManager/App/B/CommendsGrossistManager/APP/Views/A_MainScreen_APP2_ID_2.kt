package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views.Models._1_3_BonAchat
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Views.LoadingContent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.text.NumberFormat
import java.util.Locale

@Composable
fun A_MainScreen_APP2_ID_2(
    modifier: Modifier = Modifier,
    onConfirmOrder: () -> Unit,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject(),
) {
    val progressValue by _0_0_HeadOfRepositorys_Repository.progressRepo.collectAsState()
    val _0_HeadOfRepositorys_Repository_Model = _0_0_HeadOfRepositorys_Repository
        .repositorys_Model

    val composeKeyVID =
        _0_0_HeadOfRepositorys_Repository.activeVID_1_3_BonAchat

    val relativeBonAchate = _0_HeadOfRepositorys_Repository_Model
        ._1_3_BonAchat_Repository
        .modelDatasSnapList.find { it.vid == composeKeyVID }

    val produitsBonAchatIDs = _0_HeadOfRepositorys_Repository_Model
        ._1_2_ProduitAcheteOperation_Repository
        .modelDatasSnapList
        .filter { produitOpe ->
            produitOpe.parent_1_3_BonAchat == composeKeyVID
                    && produitOpe.etateActuellementEst == _1_2_ProduitAcheteOperation
                .EtateActuellementEst
                .CONFIRME
                    && _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository
                .modelDatasSnapList
                .any {
                    it.parentProduitAchateOperationVID == produitOpe.vid &&
                            it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                }
        }


    // Calculate total items count
    val itemCount = _0_HeadOfRepositorys_Repository_Model
        ._1_1_CouleurAcheteOperation_Repository
        .modelDatasSnapList
        .filter { couleurOpe ->
            produitsBonAchatIDs.any { it.vid == couleurOpe.parentProduitAchateOperationVID } &&
                    couleurOpe.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
        }
        .sumOf { it.totaleQuantity }


    val totalPrice = produitsBonAchatIDs.sumOf { produitOpe ->
        val productPrice = _0_HeadOfRepositorys_Repository_Model
            ._2_1_ProduitsDataBase_Repository
            .modelDatasSnapList
            .find { it.vid == produitOpe.produitAcheterID }
            ?.monPrixVent ?: 0.0

        // Get all color operations for this product and sum their quantities
        val productTotalQuantity = _0_HeadOfRepositorys_Repository_Model
            ._1_1_CouleurAcheteOperation_Repository
            .modelDatasSnapList
            .filter {
                it.parentProduitAchateOperationVID == produitOpe.vid &&
                        it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
            }
            .sumOf { it.totaleQuantity }

        // Multiply the quantity by the price for this product
        productTotalQuantity * productPrice
    }

    // Format the total price
    val formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE)
    val formattedTotalPrice = formatter.format(totalPrice).replace("€", "دج")

    // Check if the BonAchat is in ON_MODE_COMMEND_ACTUELLEMENT state
    val isOrderMode =
        relativeBonAchate?.etateActuellementEst == _1_3_BonAchat.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var showOrderSuccess by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            // Only show the order form if in ON_MODE_COMMEND_ACTUELLEMENT state
            if (isOrderMode) {
                BonAchatInfos(
                    relativeBonAchate?.clientAcheteurID,
                    _0_0_HeadOfRepositorys_Repository,
                    relativeBonAchate,
                    itemCount,
                    formattedTotalPrice,
                    showOrderSuccess,
                    scope,
                    onConfirmOrder,
                    onShowOrderSuccessChange = { showOrderSuccess = it }
                )

                Column(
                    modifier = modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        // Show loading indicator while data is being loaded
                        if (progressValue < 1.0f) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingContent(message = "Loading data...")
                            }
                        } else {
                            // Always show the list of items, regardless of order mode
                            B_MainList_APP2_ID_2(
                                composeKeyVID = composeKeyVID,
                                _0_HeadOfRepositorys_Repository_Model = _0_0_HeadOfRepositorys_Repository
                                    .repositorys_Model
                            )
                        }
                    }
                }
            }
        }
    }
}
