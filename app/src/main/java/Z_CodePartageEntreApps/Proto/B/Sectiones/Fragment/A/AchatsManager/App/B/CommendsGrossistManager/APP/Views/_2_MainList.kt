package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainList_APP2_ID_2(
    composeKeyVID: Long?,
    modifier: Modifier = Modifier,
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
) {
    val produitBonAchatIDs = _0_HeadOfRepositorys_Repository_Model
        ._1_2_ProduitAcheteOperation_Repository
        .modelDatasSnapList
        .filter { produitOpe ->
            produitOpe.parent_1_3_BonAchat == composeKeyVID
        }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(produitBonAchatIDs.size) { index ->
            MainItem_APP2_ID_2(
                composeKeyVID = produitBonAchatIDs[index].vid,
                _0_HeadOfRepositorys_Repository_Model = _0_HeadOfRepositorys_Repository_Model
            )
        }
    }
}
