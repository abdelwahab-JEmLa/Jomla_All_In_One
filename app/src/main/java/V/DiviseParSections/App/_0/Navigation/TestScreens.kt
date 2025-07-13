package V.DiviseParSections.App._0.Navigation

import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.clientjetpack.R

data class NavigationItem(
    val title: String,
    val onClick: () -> Unit,
    val isImageItem: Boolean = false,
    val imageRes: Int? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val containerColor: Color? = null,
    val contentColor: Color? = null
)

@Composable
fun TestScreens(
    onDismiss: () -> Unit,
    fragmentNavigationHandler: FragmentNavigationHandler
) {
    // Create navigation items list
    val navigationItems = listOf(
        NavigationItem(
            title = "Panie AV 29Juin Proto",
            onClick = {
                fragmentNavigationHandler.navigateToTestDataScreen()
                onDismiss()
            },
            isImageItem = false,
            icon = Icons.Default.ShoppingCart,
            contentColor = Color.Red,
            imageRes = R.drawable.panier_scree_shoot
        ),
        NavigationItem(
            title = "Test Produit Fast Search",
            onClick = {
                fragmentNavigationHandler.navigateToTestProduitFastSearchDialog()
                onDismiss()
            },
            isImageItem = false,
            icon = Icons.Default.Search,
            contentColor = Color.Blue,
            containerColor = Color(0xFFE3F2FD)
        )
    )

    // Using full-screen dialog instead of AlertDialog
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Close button at top-right
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .zIndex(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }

                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Navigation Test Dialog",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Text(
                        "Choose a navigation option:",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(navigationItems) { item ->
                            NavigationItemCard(item = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationItemCard(item: NavigationItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(150.dp)
                .clickable { item.onClick() },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = item.containerColor ?: CardDefaults.cardColors().containerColor
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (item.isImageItem && item.imageRes != null) {
                    // Show image when isImageItem is true
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else if (item.icon != null) {
                    // Show icon when isImageItem is false
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(64.dp),
                        tint = item.contentColor ?: MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Text(
            item.title,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
