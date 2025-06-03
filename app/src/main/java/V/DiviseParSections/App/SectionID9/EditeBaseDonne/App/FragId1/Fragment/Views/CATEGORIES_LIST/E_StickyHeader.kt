package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.CATEGORIES_LIST

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.C_CategorieProduitInfos
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.outlined.PanTool
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun E_StickyHeader(
    categoryId: Long?,
    category: C_CategorieProduitInfos? = null,
    onHeldPourDeplacement: (Boolean) -> Unit = {},
    onClickPourChangeDeplaceApre: (Boolean) -> Unit = {}
) {
    val isHeld = category?.itsHeldPourDeplacement ?: false
    val isUncategorized = categoryId == 0L

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isHeld -> MaterialTheme.colorScheme.tertiaryContainer
                isUncategorized -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(if (isHeld) 8.dp else 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isHeld && !isUncategorized) {
                RepositionBtn(true) { onClickPourChangeDeplaceApre(false) }
            }

            Icon(
                if (isUncategorized) Icons.Default.FolderOpen else Icons.Default.Category,
                null,
                tint = when {
                    isHeld -> MaterialTheme.colorScheme.onTertiaryContainer
                    isUncategorized -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                }
            )

            Text(
                when {
                    isUncategorized -> "Sans Catégorie"
                    category != null -> category.nom
                    else -> "Catégorie $categoryId"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    isHeld -> MaterialTheme.colorScheme.onTertiaryContainer
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
                        isHeld -> MaterialTheme.colorScheme.onTertiaryContainer.copy(0.7f)
                        isUncategorized -> MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                        else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                    },
                    fontWeight = FontWeight.Medium
                )
                 if(it.id==89L){
                     Log.d("Test","cate ${it.nom}id ${it.id} pos = ${it.position}")
                 }
            }

            if (!isHeld && !isUncategorized) {
                RepositionBtn(false) { onClickPourChangeDeplaceApre(true) }
            }

            if (!isUncategorized) {
                IconButton({ onHeldPourDeplacement(!isHeld) }) {
                    Icon(
                        if (isHeld) Icons.Filled.PanTool else Icons.Outlined.PanTool,
                        if (isHeld) "Relâcher" else "Tenir pour déplacement",
                        tint = when {
                            isHeld -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RepositionBtn(isBefore: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.size(24.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(0.7f))
            .clickable { onClick() },
        Alignment.Center
    ) {
        Text(
            if (isBefore) "↑" else "↓",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold
        )
    }
}
