package com.example.clientjetpack

import a_RoomDB.ArticlesBasesStatsModel
import a_RoomDB.Objects
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.ContentAlpha
import b_StartupAppDisplayerOfNewArticles.HeadOfViewModels
import b_StartupAppDisplayerOfNewArticles.StartupAppDisplayerOfNewArticles
import c_WindosBuyAndDesplayeArticleStats.WindosBuyAndDesplayeArticleStats
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

// Application.kt
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}

data class AppViewModels(
    val headOfViewModels: HeadOfViewModels
)

class MainActivity : ComponentActivity() {
    private val database by lazy { Objects.getInstance(this) }
    private val permissionHandler by lazy { PermissionHandler(this) }
    private val headOfViewModels: HeadOfViewModels by viewModels {
        ViewModelFactory(database)
    }
    private val appViewModels by lazy { AppViewModels(headOfViewModels) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHandler.checkAndRequestPermissions()
        setContent {
            MainScreen(appViewModels)
        }
    }
}

// ViewModelFactory.kt
class ViewModelFactory(
    private val database: Objects
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HeadOfViewModels::class.java) ->
                HeadOfViewModels(database) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

// MainScreen.kt
@Composable
private fun MainScreen(appViewModels: AppViewModels) {
    val navController = rememberNavController()
    val items = NavigationItems.getItems()
    var isNavBarVisible by remember { mutableStateOf(true) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            AnimatedVisibility(visible = isNavBarVisible) {
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AppNavHost(
                appViewModels = appViewModels,
                navController = navController,
                onToggleNavBar = { isNavBarVisible = !isNavBarVisible }
            )
        }
    }
}

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

// AppNavHost.kt
@Composable
fun AppNavHost(
    appViewModels: AppViewModels,
    navController: NavHostController,
    onToggleNavBar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by appViewModels.headOfViewModels.uiState.collectAsState()
    val currentEditedArticle by appViewModels.headOfViewModels.currentArticle.collectAsState()

    var windosBuyAndDesplayeArticleStats by remember { mutableStateOf<ArticlesBasesStatsModel?>(null) }
    var reloadTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentEditedArticle) {
        windosBuyAndDesplayeArticleStats = currentEditedArticle
    }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.EditDatabaseWithCreateNewArticles.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.EditDatabaseWithCreateNewArticles.route) {
                Box(modifier = Modifier.fillMaxSize()) {
                    StartupAppDisplayerOfNewArticles(
                        viewModel = appViewModels.headOfViewModels,
                        onToggleNavBar = onToggleNavBar,
                        reloadTrigger = reloadTrigger
                    )

                    if (uiState.isLoading) {
                        LoadingOverlay(
                            progress = uiState.loadingProgress / 100f,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }
            }
        }

        windosBuyAndDesplayeArticleStats?.let { article ->
            WindosBuyAndDesplayeArticleStats(
                article = article,
                uiState = uiState,
                onDismiss = { windosBuyAndDesplayeArticleStats = null },
                viewModel = appViewModels.headOfViewModels,
                modifier = Modifier.padding(horizontal = 3.dp),
                onReloadTrigger = { reloadTrigger += 1 },
                reloadTrigger = reloadTrigger
            )
        }
    }
}

@Composable
fun LoadingOverlay(
    progress: Float,
    modifier: Modifier = Modifier
) {
    // État pour l'animation de clignotement
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // État pour l'animation de rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        // Votre image qui tourne et clignote
        Image(
            painter = painterResource(id = R.drawable.baked_goods_1), // Mettez votre image ici
            contentDescription = "Loading",
            modifier = Modifier
                .size(64.dp)
                .rotate(rotation)
                .alpha(alpha)
        )

        // Indicateur de progression circulaire
        CircularProgressIndicator(
            progress = { progress },
            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            modifier = Modifier.size(64.dp)
        )
    }
}
