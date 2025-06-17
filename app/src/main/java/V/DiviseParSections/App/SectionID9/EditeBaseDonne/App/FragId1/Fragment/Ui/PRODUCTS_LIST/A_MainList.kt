package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditeInfosMainList(
    modifier: Modifier = Modifier,
    produitList: List<ArticlesBasesStatsTable> = emptyList(),
    onPrixUpdate: (ArticlesBasesStatsTable) -> Unit = {}
) {


    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = produitList,
            key = { it.id }
        ) { produit ->
            ProductItem(
                produitInit = produit,
                onUpdate = onPrixUpdate
            )
        }
    }
}
