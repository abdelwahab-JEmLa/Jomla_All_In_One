package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.PanieAchates.APP.Views

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun B_MainList_APP2_ID_2(
    composeKeyVID: Long?,
    modifier: Modifier = Modifier,
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
    onQuantitySelected: (Int) -> Unit,
) {
    // First filter color operations to find valid ones
    val validColorOperations = _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository
        .modelDatasSnapList
        .filter {
            it.parentProduitAchateOperationVID != null &&
                    it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
        }

    // Then filter products that match our criteria
    val produitsBonAchatIDs = _0_HeadOfRepositorys_Repository_Model
        ._1_2_ProduitAcheteOperation_Repository
        .modelDatasSnapList
        .filter { produitOpe ->
            val condition1 = produitOpe.parent_1_3_BonAchat == composeKeyVID
            val condition2 = produitOpe.etateActuellementEst == _1_2_ProduitAcheteOperation
                .EtateActuellementEst
                .CONFIRME
            val condition3 = validColorOperations
                .any { it.parentProduitAchateOperationVID == produitOpe.vid }

            condition1 && condition2 && condition3
        }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(produitsBonAchatIDs) { produitItem ->
            C_MainItem_APP2_ID_2(
                composeKeyVID = produitItem.vid,
                _0_HeadOfRepositorys_Repository_Model = _0_HeadOfRepositorys_Repository_Model,
                onQuantitySelected = onQuantitySelected,
            )
        }
    }
}
