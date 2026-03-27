package EntreApps.Shared.Compose_Injectable_Sepecialise.Kotlin.ID1.EditeBaseDonne.Package.M16Categorie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryBadge(
    catalogueName: String?,
    categoryName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    val hasNoCategory = catalogueName.isNullOrBlank() && categoryName.isNullOrBlank()
    val displayedCategoryName = when {
        hasNoCategory -> "Non Définie"
        else -> categoryName ?: "Sans Catégorie"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (hasNoCategory)
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                else
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Companion.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = null,
                tint = if (hasNoCategory)
                    MaterialTheme.colorScheme.onErrorContainer
                else
                    MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.Companion.size(16.dp)
            )
            Column(modifier = Modifier.Companion.weight(1f)) {
                if (!catalogueName.isNullOrBlank()) {
                    Text(
                        text = catalogueName,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                }
                Text(
                    text = displayedCategoryName,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Companion.SemiBold,
                    color = if (hasNoCategory)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Companion.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Changer catégorie",
                tint = if (hasNoCategory)
                    MaterialTheme.colorScheme.onErrorContainer
                else
                    MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.Companion.size(14.dp)
            )
        }
    }
}
