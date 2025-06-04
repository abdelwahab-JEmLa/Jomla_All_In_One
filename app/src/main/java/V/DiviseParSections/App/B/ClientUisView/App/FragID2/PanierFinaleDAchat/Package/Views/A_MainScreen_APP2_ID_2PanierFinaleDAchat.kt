package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.B.Models.C3_BonAchate
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Proto.B.Par.App.A.AchatsManager.App._1.Shared.Views.LoadingContent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
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
fun A_MainScreen_APP2_ID_2PanierFinaleDAchat(
    modifier: Modifier = Modifier,
    onConfirmOrder: () -> Unit,
    _0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3 = koinInject(),
) {
    val progressValue by _0_0_HeadSQLRepositorys.progressRepo.collectAsState()
    val _0_HeadOfRepositorys_Repository_Model = _0_0_HeadSQLRepositorys
        .repositorys_Model

    val composeKeyVID by _0_0_HeadSQLRepositorys.repositorys_Model.activeVId_C3_BonAchate_Repository.collectAsState()

    // Fix 1: Using the collected value directly, which is now a Long
    val relativeBonAchate = _0_HeadOfRepositorys_Repository_Model
        .c3_BonAchate_Repository
        .modelDatasSnapList.find { it.vid == composeKeyVID }

    val produitsBonAchatIDs = _0_HeadOfRepositorys_Repository_Model
        .repositoryC2_ProduitAcheteOperation
        .modelDatasSnapList
        .filter { produitOpe ->
            produitOpe.parent_1_3_TransactionCommercial == composeKeyVID
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

    val totalPrice = calcule_totalPrice(produitsBonAchatIDs, _0_HeadOfRepositorys_Repository_Model)


    // Format the total price
    val formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE)
    val formattedTotalPrice = formatter.format(totalPrice).replace("€", "دج")

    // Check if the BonAchat is in COMMANDE_LENCE state
    val isOrderMode =
        relativeBonAchate?.etateActuellementEst ==
                C3_BonAchate.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                ||       relativeBonAchate?.etateActuellementEst ==
                C3_BonAchate.EtateActuellementEst.PourVoirPanie


    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var showOrderSuccess by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            // Only show the order form if in COMMANDE_LENCE state
            if (isOrderMode) {
                BonAchatInfos(
                    relativeBonAchate?.clientAcheteurID,
                    _0_0_HeadSQLRepositorys,
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
                                _0_HeadOfRepositorys_Repository_Model = _0_0_HeadSQLRepositorys
                                    .repositorys_Model,
                                onQuantitySelected = {
                                },
                                // FIX: In A_MainScreen_APP2_ID_2PanierFinaleDAchat.kt - properly upsert_1_3_TransactionCommercial totalPrice when price changes
                                onDoneupdatePrice = { colorOperations ->
                                    _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository.notifyDataChanged()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun calcule_totalPrice(
    produitsBonAchatIDs: List<_1_2_ProduitAcheteOperation>,
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
    colorOperations: List<_1_1_CouleurAcheteOperation>? = null
) = produitsBonAchatIDs.sumOf { produitOpe ->
    // Check if there's a provisional price, use it instead of the default if available
    val productPrice = if (produitOpe.provisoireMonPrix > 0) {
        produitOpe.provisoireMonPrix
    } else {
        _0_HeadOfRepositorys_Repository_Model
            ._2_1_ProduitsDataBase_Repository
            .modelDatasSnapList
            .find { it.vid == produitOpe.produitAcheterID }
            ?.monPrixVent ?: 0.0
    }

    // If colorOperations is provided, filter from that list, otherwise use repository data
    val productTotalQuantity = colorOperations?.filter {
        it.parentProduitAchateOperationVID == produitOpe.vid &&
                it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
    }?.sumOf { it.totaleQuantity }
        ?: _0_HeadOfRepositorys_Repository_Model
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
