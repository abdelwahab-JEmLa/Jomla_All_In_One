package P0_MainScreen.Ui.Main.AppNavHost

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material.icons.filled.MapsHomeWork
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.ContentAlpha
import com.example.clientjetpack.R

private const val TAG = "NavigationBarWithFab"

@Composable
fun NavigationBarWithFab(
    items: List<Screen>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isFabVisible: Boolean,
    onToggleFabVisibility: () -> Unit,
    navController: NavController,
    onCatalogSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp,
) {
    // Dialog state
    var showCatalogDialog by remember { mutableStateOf(false) }

    // Track first navigation state
    var isFirstNavigation by remember { mutableStateOf(true) }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            // Calculate middle index
            val middleIndex = items.size / 2

            items.forEachIndexed { index, screen ->
                if (index == middleIndex) {
                    // Add empty space for FAB
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Box(modifier = Modifier.size(48.dp)) },
                        enabled = false
                    )
                }
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (currentRoute == screen.route) screen.color
                            else LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                        )
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        onNavigate(screen.route)
                    }
                )
            }
        }

        // Image FAB positioned above the navigation bar
        Surface(
            modifier = Modifier
                .offset(y = (-28).dp)
                .size(56.dp),
            shape = CircleShape,
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onToggleFabVisibility),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = if (isFabVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle FAB",
                    modifier = Modifier.align(Alignment.Center),
                    tint = Color.White
                )
            }
        }

        if (showCatalogDialog) {
            CatalogSelectionDialog(
                onDismiss = {
                    showCatalogDialog = false
                },
                onCatalogSelected = { categoryId ->
                    onCatalogSelected(categoryId)
                    showCatalogDialog = false
                    // Navigate to the selected screen after selecting a catalog
                    onNavigate(Screen.EditDatabaseWithCreateNewArticles.route)
                }  ,
                viewModelInitApp=viewModelInitApp
            )
        }
    }
}

@Composable
fun CatalogSelectionDialog(
    onDismiss: () -> Unit,
    onCatalogSelected: (Long) -> Unit,
    viewModelInitApp: ViewModelInitApp
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sélectionner un catalogue") },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Catalog options in the dialog
                androidx.compose.foundation.layout.Column {
                    TextButton(
                        onClick = { onCatalogSelected(148L)

                        }
                    ) {
                        Text("Catalogue Cosmétiques")
                    }

                    TextButton(
                        onClick = { onCatalogSelected(149L) }
                    ) {
                        Text("Catalogue Confiseries")
                    }

                    TextButton(
                        onClick = { onCatalogSelected(150L) }
                    ) {
                        Text("Catalogue Téléphones")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Annuler")
            }
        }
    )
}

// Add this to your project if it's missing
object NavigationItems {
    fun getItems() = listOf(
        ScreensApp2.A_ClientsLocationGps,
        Screen.EditDatabaseWithCreateNewArticles,
        Screen.SoldCart,
        Screen.TravailleTempRecorder,
        Screen.CommandeProduits,
        Screen.ToggleFab,
        Screen.NewFragTest
    )
}

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
) {
    data object A_ClientsLocationGps : Screen(
        route = "A_ClientsLocationGps",
        icon = Icons.Default.MapsHomeWork,
        title = "A_ClientsLocationGps",
        color = Color(0xFFFF5722)
    )
    data object EditDatabaseWithCreateNewArticles : Screen(
        route = "main_fragment_edit_database_with_create_new_articles",
        icon = Icons.Default.EditRoad,
        title = "Create New Articles",
        color = Color(0xFF151414)
    )

    data object SoldCart : Screen(
        route = "sold_cart",
        icon = Icons.Default.ShoppingCart,
        title = "Panier Sold",
        color = Color(0xFFF44336)
    )

    data object CommandeProduits : Screen(
        route = "CommandeProduits",
        icon = Icons.Default.Receipt,
        title = "CommandeProduits",
        color = Color(0xFF009688)
    )

    data object TravailleTempRecorder : Screen(
        route = "TravailleTempRecorder",
        icon = Icons.Default.Work,
        title = "TravailleTempRecorder",
        color = Color(0xFF9C27B0)
    )

    data object NewFragTest : Screen(
        route = "NewFragTest",
        icon = Icons.Default.TravelExplore,
        title = "NewFragTest",
        color = Color(0xFF4CAF50)
    )

    data object ToggleFab : Screen(
        route = "toggle_fab",
        icon = Icons.Default.Visibility,
        title = "Toggle FAB",
        color = Color(0xFF2196F3)
    )
}
