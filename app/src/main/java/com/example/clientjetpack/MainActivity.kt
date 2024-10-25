package com.example.clientjetpack

import a_RoomDB.AppDatabase
import a_RoomDB.DataBaseArticles
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import b_StartupEcommerceApp.HeadOfViewModelFactory
import b_StartupEcommerceApp.HeadOfViewModels
import com.example.clientjetpack.ui.theme.ClientJetPackTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.ContentAlpha


// MainActivity.kt
class MainActivity : ComponentActivity() {
    private lateinit var permissionHandler: PermissionHandler
    private val database by lazy { AppDatabase.getInstance(this) }
    private val headOfViewModels: HeadOfViewModels by viewModels {
        HeadOfViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHandler = PermissionHandler(this)
        permissionHandler.checkAndRequestPermissions()

        setContent {
            AbdelwahabJeMLaJetPackTheme {
                MainScreen(
                    database = database,
                    viewModel = headOfViewModels
                )
            }
        }
    }
}
@Composable
private fun MainScreen(
    database: AppDatabase,
    viewModel: HeadOfViewModels
) {
    val navController = rememberNavController()
    val items = NavigationItems.getItems()
    var isNavBarVisible by remember { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val textProgress by viewModel.textProgress.collectAsState()

    Scaffold(
        bottomBar = {
            if (isNavBarVisible) {
                Column {
                    ProgressBarWithAnimation(uploadProgress, textProgress)
                    CustomNavigationBar(
                        items = items,
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AppNavHost(
                navController = navController,
                database = database,
                headOfViewModels = viewModel,
                onToggleNavBar = { isNavBarVisible = !isNavBarVisible }
            )
        }
    }
}

// AppNavigation.kt

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
) {
    data object EditDatabaseWithCreateNewArticles : Screen(
        route = "main_fragment_edit_database_with_create_new_articles",
        icon = Icons.Default.EditRoad,
        title = "Create New Articles",
        color = Color(0xFFE30E0E)
    )
}

object NavigationItems {
    fun getItems() = listOf(Screen.EditDatabaseWithCreateNewArticles)
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    database: AppDatabase,
    headOfViewModels: HeadOfViewModels,
    onToggleNavBar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by headOfViewModels.uiState.collectAsState()
    val uploadProgress by headOfViewModels.uploadProgress.collectAsState()
    val currentEditedArticle by headOfViewModels.currentEditedArticle.collectAsState()

    var dialogArticle by remember { mutableStateOf<DataBaseArticles?>(null) }
    var reloadTrigger by remember { mutableIntStateOf(0) }

    // Update dialog article when currentEditedArticle changes
    LaunchedEffect(currentEditedArticle) {
        dialogArticle = currentEditedArticle
    }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.EditDatabaseWithCreateNewArticles.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.EditDatabaseWithCreateNewArticles.route) {
                MainFragmentEditDatabaseWithCreateNewArticles(
                    viewModel = headOfViewModels,
                    onToggleNavBar = onToggleNavBar,
                    onNewArticleAdded = { dialogArticle = it },
                    reloadTrigger = reloadTrigger
                )

                // Show upload progress indicator
                if (uploadProgress in 0f..100f) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { uploadProgress / 100f },
                            trackColor = ProgressIndicatorDefaults.circularTrackColor,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
        }

        // Article Detail Dialog
        dialogArticle?.let { article ->
            ArticleDetailWindow(
                article = article,
                uiState = uiState,
                onDismiss = { dialogArticle = null },
                viewModel = headOfViewModels,
                modifier = Modifier.padding(horizontal = 3.dp),
                onReloadTrigger = { reloadTrigger += 1 },
                reloadTrigger = reloadTrigger
            )
        }
    }
}

@Composable
fun CustomNavigationBar(
    items: List<Screen>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        tint = screen.color
                    )
                },
                selected = currentRoute == screen.route,
                onClick = { onNavigate(screen.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = screen.color,
                    unselectedIconColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                )
            )
        }
    }
}
