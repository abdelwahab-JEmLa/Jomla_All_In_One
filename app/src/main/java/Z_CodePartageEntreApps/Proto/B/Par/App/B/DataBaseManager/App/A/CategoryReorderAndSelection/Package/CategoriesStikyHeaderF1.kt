package Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package

import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel.ViewModel_A4FragID1
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun CategoriesStikyHeaderF1(
    viewModel: ViewModel_A4FragID1,
    category: I_CategorieProduits,
    isSelected: Boolean,
    isMoving: Boolean,
    isHeld: Boolean,
    isReorderTarget: Boolean,
    selectionOrder: Int,
    onClick: () -> Unit
) {
    val itsAddNewCatItem = category.nom == "Add New Category"

    val backgroundColor = when {
        isHeld -> MaterialTheme.colorScheme.primaryContainer
        isSelected -> MaterialTheme.colorScheme.secondaryContainer
        isMoving -> MaterialTheme.colorScheme.tertiaryContainer
        isReorderTarget -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        itsAddNewCatItem -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val height = 50.dp
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(height)
            .clickable(onClick = onClick)
            .border(
                BorderStroke(
                    width = if (isSelected || isHeld || isMoving) 2.dp else 1.dp,
                    color = when {
                        isHeld -> MaterialTheme.colorScheme.primary
                        isSelected -> MaterialTheme.colorScheme.secondary
                        isMoving -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.outline
                    }
                ),
                shape = MaterialTheme.shapes.medium
            ),
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (selectionOrder > 0) {
                Text(
                    text = selectionOrder.toString(),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.shapes.small
                        )
                        .padding(4.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = category.nom,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Added toggle button with background color
                if (!itsAddNewCatItem) {
                    // RepositorysMainGetter background color based on the toggle state
                    val toggleBackgroundColor = if (category.afficheSonHeader)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant

                    // RepositorysMainGetter icon color based on the toggle state
                    val iconColor = if (category.afficheSonHeader)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant

                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(toggleBackgroundColor)
                            .clickable {
                                category.afficheSonHeader = !category.afficheSonHeader
                                viewModel.updateUneSeulDataDeCategories(category)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (category.afficheSonHeader)
                                Icons.Default.KeyboardArrowDown
                            else
                                Icons.Default.KeyboardArrowUp,
                            contentDescription = "Toggle Header Visibility",
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (itsAddNewCatItem) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
