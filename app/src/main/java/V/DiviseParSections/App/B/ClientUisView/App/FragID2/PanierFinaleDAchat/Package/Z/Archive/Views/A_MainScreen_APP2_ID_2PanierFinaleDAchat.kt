package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Z.Archive.Views
     /*
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Z.Archive.ViewModel.PanierFinaleDAchatViewModel
import Z_CodePartageEntreApps.Proto.B.Par.App.A.AchatsManager.App._1.Shared.Views.LoadingContent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun A_MainScreen_APP2_ID_2PanierFinaleDAchat(
    viewModel: PanierFinaleDAchatViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val eGroupeddatabasesrepositoryprotoavant3juin = viewModel.a_MasterRepositorysGrpProtoJuin3
        .e_GroupedDataBasesRepositoryProtoAvant3Juin

    val groupeRepositorysProtoAvJuin3Repositorys_Model = eGroupeddatabasesrepositoryprotoavant3juin.repositorys_Model

    val progressValue = viewModel.a_CentralDatasHandlerProtoJuin9.loadingProgress

    val ouvertc3Transactioncommercial = viewModel.a_CentralDatasHandlerProtoJuin9.ouvertTransactionCommercial

    val idOuvertC3_TransactionCommercial = ouvertc3Transactioncommercial?.vid

    val produitsBonAchatIDs = groupeRepositorysProtoAvJuin3Repositorys_Model
        .repositoryC2_ProduitAcheteOperation
        .modelDatasSnapList
        .filter { produitOpe ->
            produitOpe.parent_1_3_TransactionCommercial == idOuvertC3_TransactionCommercial
                    && produitOpe.etateActuellementEst == _1_2_ProduitAcheteOperation
                .EtateActuellementEst
                .CONFIRME
                    && groupeRepositorysProtoAvJuin3Repositorys_Model._1_1_CouleurAcheteOperation_Repository
                .modelDatasSnapList
                .any {
                    it.parentProduitAchateOperationVID == produitOpe.vid &&
                            it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                }
        }

    val itemCount = groupeRepositorysProtoAvJuin3Repositorys_Model
        ._1_1_CouleurAcheteOperation_Repository
        .modelDatasSnapList
        .filter { couleurOpe ->
            produitsBonAchatIDs.any { it.vid == couleurOpe.parentProduitAchateOperationVID } &&
                    couleurOpe.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
        }
        .sumOf { it.totaleQuantity }

    val totalPrice = calcule_totalPrice(produitsBonAchatIDs, groupeRepositorysProtoAvJuin3Repositorys_Model)

    val formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE)
    val formattedTotalPrice = formatter.format(totalPrice).replace("€", "دج")

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var showOrderSuccess by remember { mutableStateOf(false) }

            ouvertc3Transactioncommercial?.let {
                BonVentInfos(
                    viewModel=viewModel,
                    _0_0_HeadSQLRepositorys = eGroupeddatabasesrepositoryprotoavant3juin,
                    relativeBonAchate = ouvertc3Transactioncommercial,
                    itemCount = itemCount,
                    formattedTotalPrice = formattedTotalPrice,
                    showOrderSuccess = showOrderSuccess,
                )

                Column(
                    modifier = modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        // Show loading indicator while data is being loaded
                        if (progressValue != null) {
                            if (progressValue < 1.0f) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingContent(message = "Loading data...")
                                }
                            } else {
                                B_MainList_APP2_ID_2(
                                    viewModel = viewModel,
                                    composeKeyVID = idOuvertC3_TransactionCommercial,
                                    _0_HeadOfRepositorys_Repository_Model = eGroupeddatabasesrepositoryprotoavant3juin
                                        .repositorys_Model,
                                    onDoneUpdatePrice = { colorOperations ->
                                        groupeRepositorysProtoAvJuin3Repositorys_Model._1_1_CouleurAcheteOperation_Repository.notifyDataChanged()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun calcule_totalPrice(
    produitsBonAchatIDs: List<_1_2_ProduitAcheteOperation>,
    _0_HeadOfRepositorys_Repository_Model: GroupeRepositorysProtoAvJuin3Model,
    colorOperations: List<_1_1_CouleurAcheteOperation>? = null
) = produitsBonAchatIDs.sumOf { produitOpe ->
    // Check if there's add provisional price, use it instead of the default if available
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
                      */
