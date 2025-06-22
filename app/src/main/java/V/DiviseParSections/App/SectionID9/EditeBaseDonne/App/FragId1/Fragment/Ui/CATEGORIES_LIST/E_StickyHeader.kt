package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.CategoriesTabelle
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun E_StickyHeader(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    groupedProducts: Map<Long, List<ArticlesBasesStatsTable>>,
    categoryId: Long?,
    category: CategoriesTabelle? = null,
) {
    val isUncategorized = categoryId == 0L
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isUncategorized -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation( 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                if (isUncategorized) Icons.Default.FolderOpen else Icons.Default.Category,
                null,
                tint = when {
                    isUncategorized -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                }
            )

            Text(
                when {
                    isUncategorized -> "Sans Catégorie"
                    category != null -> {
                        val text = "${category.nom} ${category.position}"
                        text
                    }
                    else -> "Catégorie $categoryId"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    isUncategorized -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                },
                modifier = Modifier.weight(1f)
            )

            category?.let {
                    Text(
                    "#${it.position}",
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isUncategorized -> MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                        else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
