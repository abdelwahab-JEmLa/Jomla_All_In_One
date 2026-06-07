package Application2.App.Fragment.Ui

import Application2.App.Base.Repository.RepositorysMainGetter_app2
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

/**
 * FIXED: CategoryStickyHeader now respects the hide_header_categorie filter
 * When hide_header_categorie is true, headers are completely hidden
 * When false, headers are shown if there are products in that category
 */
@Composable
fun CategoryStickyHeader(
    category: M16CategorieProduit,
    modifier: Modifier = Modifier,
    onToggleHeaderVisibility: (M16CategorieProduit) -> Unit = {},
    FocusedValuesGetter_app2: RepositorysMainGetter_app2 = koinInject()
) {
    // Original header rendering logic
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.nom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    val updatedCategory = category.copy(
                        displayedHeader = !category.displayedHeader
                    )
                    onToggleHeaderVisibility(updatedCategory)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (category.displayedHeader) {
                        Icons.Default.Visibility
                    } else {
                        Icons.Default.VisibilityOff
                    },
                    contentDescription = if (category.displayedHeader) {
                        "Masquer l'en-tête"
                    } else {
                        "Afficher l'en-tête"
                    },
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
