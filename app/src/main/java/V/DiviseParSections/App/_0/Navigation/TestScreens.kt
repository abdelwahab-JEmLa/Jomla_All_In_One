package V.DiviseParSections.App._0.Navigation

import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.R

data class NavigationItem(
    val title: String,
    val onClick: () -> Unit,
    val isImageItem: Boolean = false,
    val imageRes: Int? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val containerColor: androidx.compose.ui.graphics.Color? = null,
    val contentColor: androidx.compose.ui.graphics.Color? = null
)

@Composable
fun TestScreens(
    onDismiss: () -> Unit,
    fragmentNavigationHandler: FragmentNavigationHandler
) {
    // Create navigation items list
    val navigationItems = listOf(
        NavigationItem(
            title = "Panie AV 29Juin Proto  ",
            onClick = {
                fragmentNavigationHandler.navigateToTestDataScreen()
                onDismiss()
            },
            isImageItem = true,
            imageRes = R.drawable.panier_scree_shoot
        ),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Navigation Test Dialog",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Choose a navigation option:",
                    style = MaterialTheme.typography.bodyMedium
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(navigationItems) { item ->
                        NavigationItemCard(item = item)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NavigationItemCard(item: NavigationItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image handling for the Test Data item
        Card(
            modifier = Modifier
                .size(150.dp)
                .clickable { item.onClick() },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            if (true) {
                Icons.Default.TravelExplore
            } else {
                Image(
                    painter = painterResource(id = item.imageRes!!),
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Text(
            item.title,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
