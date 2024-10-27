package com.example.clientjetpack

import a_RoomDB.ArticlesBasesStatsTabelle
import a_RoomDB.ClientsModel
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.ContentAlpha
import b_StartupAppDisplayerOfNewArticles.StartUpNewArticlesViewModels
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
    val startUpNewArticlesViewModels: StartUpNewArticlesViewModels
)

class MainActivity : ComponentActivity() {
    private val database by lazy { Objects.getInstance(this) }
    private val permissionHandler by lazy { PermissionHandler(this) }
    private val startUpNewArticlesViewModels: StartUpNewArticlesViewModels by viewModels {
        ViewModelFactory(database)
    }
    private val appViewModels by lazy { AppViewModels(startUpNewArticlesViewModels) }

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
            modelClass.isAssignableFrom(StartUpNewArticlesViewModels::class.java) ->
                StartUpNewArticlesViewModels(database) as T
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

// Add this composable for client selection
@Composable
fun ClientSelectionDialog(
    clients: List<ClientsModel>,
    onClientSelected: (ClientsModel) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredClients = remember(searchQuery, clients) {
        if (searchQuery.length >= 3) {
            clients.filter { it.nomClientsSu.contains(searchQuery, ignoreCase = true) }
        } else {
            emptyList()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Select Client",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Client") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (searchQuery.length >= 3) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        items(filteredClients) { client ->
                            TextButton(
                                onClick = {
                                    onClientSelected(client)
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(client.nomClientsSu ?: "Unknown")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    appViewModels: AppViewModels,
    navController: NavHostController,
    onToggleNavBar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by appViewModels.startUpNewArticlesViewModels.uiState.collectAsState()

    var windosBuyAndDesplayeArticleStats by remember { mutableStateOf<ArticlesBasesStatsTabelle?>(null) }
    var clientBuyerNow by rememberSaveable() { mutableStateOf<ClientsModel?>(null) }
    var showClientSelection by remember { mutableStateOf(false) }
    var pendingArticle by remember { mutableStateOf<ArticlesBasesStatsTabelle?>(null) }
    var pendingIndexColor by remember { mutableIntStateOf(0) }

    var indexColorStat by remember { mutableIntStateOf(0) }
    var reloadTrigger by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.EditDatabaseWithCreateNewArticles.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.EditDatabaseWithCreateNewArticles.route) {
                Box(modifier = Modifier.fillMaxSize()) {
                    StartupAppDisplayerOfNewArticles(
                        viewModel = appViewModels.startUpNewArticlesViewModels,
                        onToggleNavBar = onToggleNavBar,
                        reloadTrigger = reloadTrigger,
                        onClickToOpenWindos = { article, indexColor ->
                            pendingArticle = article
                            pendingIndexColor = indexColor
                            if (clientBuyerNow == null) {
                                showClientSelection = true
                            } else {
                                // Si client déjà sélectionné, ouvre directement la fenêtre
                                windosBuyAndDesplayeArticleStats = article
                                indexColorStat = indexColor
                            }
                        }
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

        // N'ouvre le dialogue que si pas de client ET showClientSelection est true
        if (showClientSelection && clientBuyerNow == null) {
            ClientSelectionDialog(
                clients = uiState.clientsModel,
                onClientSelected = { client ->
                    clientBuyerNow = client
                    windosBuyAndDesplayeArticleStats = pendingArticle
                    indexColorStat = pendingIndexColor
                    showClientSelection = false  // Ferme le dialogue après sélection
                },
                onDismiss = { showClientSelection = false }
            )
        }

        // Ouvre la fenêtre stats seulement si on a un article ET un client
        windosBuyAndDesplayeArticleStats?.let { article ->
            WindosBuyAndDesplayeArticleStats(
                modifier = Modifier.padding(horizontal = 3.dp),
                article = article,
                clientBuyerNow = clientBuyerNow,
                viewModel = appViewModels.startUpNewArticlesViewModels,
                onDismiss = {
                    windosBuyAndDesplayeArticleStats = null
                },
                onReloadTrigger = { reloadTrigger += 1 },
                reloadTrigger = reloadTrigger,
                uiState = uiState,
                indexColorStat = indexColorStat
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
