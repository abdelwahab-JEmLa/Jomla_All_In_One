package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Z.Archive.Views
    /*
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Z.Archive.ViewModel.PanierFinaleDAchatViewModel
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun B_MainList_APP2_ID_2(
    viewModel: PanierFinaleDAchatViewModel,
    composeKeyVID: Long?,
    modifier: Modifier = Modifier,
    _0_HeadOfRepositorys_Repository_Model: GroupeRepositorysProtoAvJuin3Model,
    onDoneUpdatePrice: (SnapshotStateList<_1_1_CouleurAcheteOperation>) -> Unit,
) {
    val validColorOperations =
        _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository
            .modelDatasSnapList
            .filter {
                it.parentProduitAchateOperationVID != null &&
                        it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
            }

    // Get the product operations and sort them by VID in descending order
    val produitOperations = produitsBonAchatIDs(
        _0_HeadOfRepositorys_Repository_Model,
        composeKeyVID,
        validColorOperations
    ).sortedByDescending { it.vid } // Sort by VID in descending order

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(produitOperations) { produitOperation ->
            // Pass the product operation ID to the item component
            C_MainItem_APP2_ID_2(
                viewModel=viewModel,
                composeKeyVID = produitOperation.vid,
                _0_HeadOfRepositorys_Repository_Model = _0_HeadOfRepositorys_Repository_Model,
                onDoneUpdatePrice = { newPrice ->
                    onDoneUpdatePrice(_0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList)

                    val price = newPrice.toDoubleOrNull() ?: 0.0
                    if (price > 0) {
                        val updatedProduct = produitOperation.copy(
                            provisoireMonPrix = price
                        )
                        _0_HeadOfRepositorys_Repository_Model
                            .repositoryC2_ProduitAcheteOperation
                            .updateUnSeulData(updatedProduct)


                        onDoneUpdatePrice(_0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList)
                        _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository.notifyDataChanged()
                    }
                }
            )
        }
    }
}

private fun produitsBonAchatIDs(
    _0_HeadOfRepositorys_Repository_Model: GroupeRepositorysProtoAvJuin3Model,
    composeKeyVID: Long?,
    validColorOperations: A_MainListView<_1_1_CouleurAcheteOperation>,
) = _0_HeadOfRepositorys_Repository_Model
    .repositoryC2_ProduitAcheteOperation
    .modelDatasSnapList
    .filter { produitOpe ->
        val condition1 = produitOpe.parent_1_3_TransactionCommercial == composeKeyVID
        val condition2 = produitOpe.etateActuellementEst == _1_2_ProduitAcheteOperation
            .EtateActuellementEst
            .CONFIRME
        val condition3 = validColorOperations
            .any { it.parentProduitAchateOperationVID == produitOpe.vid }

        condition1 && condition2 && condition3
    }
                            */
