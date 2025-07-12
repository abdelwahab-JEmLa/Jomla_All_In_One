// Updated NavigationBarWithFab.kt
package V.DiviseParSections.App._0.Navigation

import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ContentAlpha
import com.example.clientjetpack.R
import org.koin.compose.koinInject

private const val TAG = "NavigationBarWithFab"

@Composable
fun NavigationBarWithFab(
    items: List<Screen>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isFabVisible: Boolean,
    onToggleFabVisibility: () -> Unit,
    onCatalogSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp,
) {
    var showCatalogDialog by remember { mutableStateOf(false) }
    var showDialogTests by remember { mutableStateOf(false) }
    val fragmentNavigationHandler: FragmentNavigationHandler = koinInject()

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
                        // Handle DialogTests specially - show dialog instead of navigating
                        if (screen.route == Screen.DialogTests.route) {
                            showDialogTests = true
                        } else {
                            onNavigate(screen.route)
                        }
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

        // Catalog selection dialog
        if (showCatalogDialog) {
            CatalogSelectionDialog(
                onDismiss = {
                    showCatalogDialog = false
                },
                onCatalogSelected = { categoryId ->
                    onCatalogSelected(categoryId)
                    showCatalogDialog = false
                    onNavigate(Screen.FacadePresentoireProduits.route)
                },
                viewModelInitApp = viewModelInitApp
            )
        }

        // Dialog Tests - Show as dialog instead of navigation
        if (showDialogTests) {
            TestScreens(
                onDismiss = { showDialogTests = false },
                fragmentNavigationHandler = fragmentNavigationHandler
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
                Column {
                    TextButton(
                        onClick = { onCatalogSelected(148L) }
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
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

// Navigation Items
object NavigationItems {
    fun getItems() = listOf(
        ScreensApp2.A_ClientsLocationGps,
        Screen.FacadePresentoireProduits,
        Screen.FragmentProduitFastSearchDialog,
        Screen.Screen1PanieVentsFinale,
        Screen.TravailleTempRecorder,
        Screen.Achats_Produits_Chez_Grossists,
        Screen.ToggleFab,
        Screen.EditDatabaseWithCreateNewArticles,
        Screen.DialogTests
    )
}

