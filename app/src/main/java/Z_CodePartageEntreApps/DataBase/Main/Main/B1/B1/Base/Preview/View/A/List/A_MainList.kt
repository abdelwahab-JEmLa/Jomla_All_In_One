package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List

import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.B1CouleurOuGoutProduitDataBase
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.B1CouleurOuGoutProduitDataBaseRepository
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.CouleurDisplayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MainList(
    b1CouleurOuGoutProduitDataBaseRepository: B1CouleurOuGoutProduitDataBaseRepository,
) {
    val items = b1CouleurOuGoutProduitDataBaseRepository.datasValueFiltred

    val groupedItems by remember(items) {
        derivedStateOf {
            items.groupBy { it.parentBProduitOldID }
                .toList()
                .sortedBy { (parentId, _) -> parentId ?: Long.MAX_VALUE }
        }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(groupedItems) { (parentId, groupItems) ->
            LazyRowProduitGroup(
                parentId = parentId,
                items = groupItems
            )
        }
    }
}

@Composable
private fun LazyRowProduitGroup(
    parentId: Long?,
    items: List<B1CouleurOuGoutProduitDataBase>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Group header
            Text(
                text = if (parentId != null) {
                    "Product Group: ${items.firstOrNull()?.parentBProduitNom ?: "Unknown"} (ID: $parentId)"
                } else {
                    "Ungrouped Items"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "${items.size} item${if (items.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Group items in horizontal scrollable row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { data ->
                    CouleurDisplayer(
                        keyCouleur = data.key,
                    )
                }
            }
        }
    }
}
