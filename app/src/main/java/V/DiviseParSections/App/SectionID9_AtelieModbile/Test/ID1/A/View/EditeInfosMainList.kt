package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A.View

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditeInfosMainList(
    modifier: Modifier = Modifier,
    produitList: List<A_ProduitInfosTest> = emptyList(),
    onPrixUpdate: (A_ProduitInfosTest) -> Unit = {}
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
                onPrixUpdate = onPrixUpdate
            )
        }
    }
}
