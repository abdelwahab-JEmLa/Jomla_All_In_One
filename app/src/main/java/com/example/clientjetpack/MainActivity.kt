package com.example.clientjetpack

import a_RoomDB.AppDatabase
import a_RoomDB.ArticlesBasesStatsTable
import android.app.Application
import android.content.Context
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditScore
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
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
import b_StartupAppDisplayerOfNewArticles.StartUpNewArticlesViewModels
import b_StartupAppDisplayerOfNewArticles.StartupAppDisplayerOfNewArticles
import b_StartupAppDisplayerOfNewArticles.UiState
import c_WindosBuyAndDesplayeArticleStats.SaleWindows
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import d_SoldCartScreen.SoldCartScreen
import e_AiGroupeForSupplier.GenerativeAiScreen
import e_AiGroupeForSupplier.GenerativeAiViewModel
import g_DialogeClientsEditer.ClientSelectionDialog

// Application.kt
class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.DatabaseModule.getDatabase(this)
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}

data class AppViewModels(
    val startUpNewArticlesViewModels: StartUpNewArticlesViewModels,
    val generativeAiViewModel: GenerativeAiViewModel,
)

// ViewModelFactory.kt
class ViewModelFactory(
    private val context: Context,
    private val database: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StartUpNewArticlesViewModels::class.java) ->
                StartUpNewArticlesViewModels(context.applicationContext, database) as T
            modelClass.isAssignableFrom(GenerativeAiViewModel::class.java) ->
                GenerativeAiViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

// MainActivity.kt
class MainActivity : ComponentActivity() {
    private val database by lazy { (application as MyApplication).database }
    private val permissionHandler by lazy { PermissionHandler(this) }

    private val viewModelFactory by lazy {
        ViewModelFactory(applicationContext, database)
    }

    private val startUpNewArticlesViewModels: StartUpNewArticlesViewModels by viewModels {
        viewModelFactory
    }

    private val generativeAiViewModel: GenerativeAiViewModel by viewModels {
        viewModelFactory
    }

    private val appViewModels by lazy {
        AppViewModels(
            startUpNewArticlesViewModels = startUpNewArticlesViewModels,
            generativeAiViewModel = generativeAiViewModel
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                // Initialize your Nearby Connections here
                setContent {
                    MainScreenWrapper(appViewModels)
                }
            }

            override fun onPermissionsDenied() {
                // Handle the case where permissions are denied
                // You might want to show a message or disable certain features
                setContent {
                    MainScreenWrapper(appViewModels)
                }
            }
        })
    }
}

// MainScreen.kt
@Composable
private fun MainScreenWrapper(appViewModels: AppViewModels) {
    val startUpViewModel = appViewModels.startUpNewArticlesViewModels
    val uiState by startUpViewModel.uiState.collectAsState()
    val isServer by startUpViewModel.appIsInstalledInHostPhone.collectAsState()

    MainScreen(
        appViewModels = appViewModels,
        uiState = uiState,
        isServer = isServer,
        onToggleServerMode = startUpViewModel::toggleServerMode  //Unresolved reference: toggleServerMode
    )
}

@Composable
private fun MainScreen(
    appViewModels: AppViewModels,
    uiState: UiState,
    isServer: Boolean,
    onToggleServerMode: () -> Unit
) {
    val navController = rememberNavController()
    val items = NavigationItems.getItems()
    var isNavBarVisible by remember { mutableStateOf(true) }
    var isFabVisible by remember { mutableStateOf(true  ) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AnimatedVisibility(visible = isNavBarVisible) {
                        NavigationBarWithFab(
                            items = items.filter { it != Screen.ToggleFab },
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            },
                            isFabVisible = isFabVisible,
                            onToggleFabVisibility = { isFabVisible = !isFabVisible }
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ConnectionStatusSection(uiState = uiState)
                    ServerClientSection(
                        uiState = uiState,
                        isServer = isServer,
                        startUpViewModel = appViewModels.startUpNewArticlesViewModels
                    )

                    AppNavHost(
                        appViewModels = appViewModels,
                        navController = navController,
                        onToggleNavBar = { isNavBarVisible = !isNavBarVisible },
                        isFabVisible = isFabVisible,
                        onClickDonne = { isFabVisible = false },
                        onToggleitsWifiServerAppOrClient = onToggleServerMode
                    )
                }
            }
        }
    }
}
@Composable
private fun ConnectionStatusSection(uiState: UiState) {
    Text(
        text = uiState.connectionStatus,
        style = MaterialTheme.typography.titleMedium,
        color = when {
            uiState.isConnected -> Color.Green
            uiState.connectionStatus.startsWith("Erreur") -> Color.Red
            else -> Color.Gray
        }
    )
}

@Composable
private fun ServerClientSection(
    uiState: UiState,
    isServer: Boolean,
    startUpViewModel: StartUpNewArticlesViewModels
) {
    if (isServer) {
        Text("Mode Serveur")

    } else {
        Text("Mode Client")
        Text("État du test WiFi: ${if (uiState.wifiTestDisplayer) "Actif" else "Inactif"}")
    }
}

@Composable
fun AppNavHost(
    appViewModels: AppViewModels,
    navController: NavHostController,
    onToggleNavBar: () -> Unit,
    modifier: Modifier = Modifier,
    isFabVisible: Boolean, onClickDonne: () -> Unit, onToggleitsWifiServerAppOrClient: () -> Unit
) {
    val uiState by appViewModels.startUpNewArticlesViewModels.uiState.collectAsState()

    // Get current client from settings
    val currentClientId = uiState.appSettingsSaverModel
        .find { it.name == "clientBuyerNowId" }?.valueLong ?: 0
    val currentClient = uiState.clientsModel.find { it.idClientsSu == currentClientId }

    // Existing state management
    var opnerSaleWindows by rememberSaveable { mutableStateOf(false) }
    var showClientSelection by rememberSaveable { mutableStateOf(false) }
    var showClientSelectionWithoutCondition by rememberSaveable { mutableStateOf(false) }
    var relatedArticleBaseStats by rememberSaveable { mutableStateOf<ArticlesBasesStatsTable?>(null) }
    var pendingIndexColor by rememberSaveable { mutableIntStateOf(0) }
    val reloadTrigger by rememberSaveable { mutableIntStateOf(0) }

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
                        onClickToOpenWindos = { articleDataBase, indexColor ->
                            relatedArticleBaseStats = articleDataBase
                            pendingIndexColor = indexColor

                            if (currentClientId == 0L) {
                                showClientSelection = true
                            } else {
                                appViewModels.startUpNewArticlesViewModels.openWindowsNewSaleWithUpdateCurrent(
                                    relatedArticleBaseStats!!.idArticle.toLong(),
                                    currentClientId,
                                    pendingIndexColor)
                                opnerSaleWindows=true
                            }
                        },
                        onClickToOpenClientsW = {
                            showClientSelectionWithoutCondition=true
                        },
                        isFabVisible=isFabVisible, onClickDonne = onClickDonne,
                        onToggleitsWifiServerAppOrClient = onToggleitsWifiServerAppOrClient
                    )

                    if (uiState.isLoading) {
                        LoadingOverlay(
                            progress = uiState.loadingProgress / 100f,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }
            }

            composable(Screen.SoldCart.route) {
                Box(modifier = Modifier.fillMaxSize()) {
                    SoldCartScreen(
                        viewModel = appViewModels.startUpNewArticlesViewModels,
                        clientBuyerNow = currentClient,
                        uiState = uiState,
                        onConfirmOrder = {
                            appViewModels.startUpNewArticlesViewModels.updateLongAppSetting("clientBuyerNowId",0)
                        }
                    )
                }
            }
            composable(Screen.BakingScreen.route) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GenerativeAiScreen(
                        generativeAiViewModel = appViewModels.generativeAiViewModel,
                    )
                }
            }
        }

        // Overlay dialogs and windows
        if (showClientSelectionWithoutCondition ||(showClientSelection && currentClientId == 0L)) {
            ClientSelectionDialog(
                soldArticle = uiState.soldArticlesModel,
                viewModel = appViewModels.startUpNewArticlesViewModels,
                clients = uiState.clientsModel,
                onClientSelected = { client ->
                    appViewModels.startUpNewArticlesViewModels.updateLongAppSetting("clientBuyerNowId",client.idClientsSu)
                    if (!showClientSelectionWithoutCondition) {
                        appViewModels.startUpNewArticlesViewModels.openWindowsNewSaleWithUpdateCurrent(
                            relatedArticleBaseStats!!.idArticle.toLong(),
                            client.idClientsSu,
                            pendingIndexColor
                        )
                        opnerSaleWindows = true
                    }
                    showClientSelection = false
                    showClientSelectionWithoutCondition= false
                },
                onDismiss = {
                    showClientSelection = false
                    showClientSelectionWithoutCondition= false

                }
            )
        }

        if (opnerSaleWindows) {
            SaleWindows(
                modifier = Modifier.padding(horizontal = 3.dp),
                uiState = uiState,
                viewModel = appViewModels.startUpNewArticlesViewModels,
                onDismiss = {
                    appViewModels.startUpNewArticlesViewModels.clearCurrentSale()
                    opnerSaleWindows=false
                },
                reloadTrigger = reloadTrigger,
            )
        }
    }
}



@Composable
fun NavigationBarWithFab(
    items: List<Screen>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isFabVisible: Boolean,
    onToggleFabVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    onClick = { onNavigate(screen.route) }
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
                    tint = Color.White // Vous pouvez ajuster la couleur de l'icône pour qu'elle soit bien visible sur votre image
                )
            }
        }
    }
}
// Add this to your project if it's missing
object NavigationItems {
    fun getItems() = listOf(
        Screen.EditDatabaseWithCreateNewArticles,
        Screen.SoldCart,
        Screen.BakingScreen,
        Screen.ToggleFab
    )
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

    data object SoldCart : Screen(
        route = "sold_cart",
        icon = Icons.Default.ShoppingCart,
        title = "Panier Sold",
        color = Color(0xFF4CAF50)
    )

    data object BakingScreen : Screen(
        route = "baking_Screen",
        icon = Icons.Default.CreditScore,
        title = "baking Screen",
        color = Color(0xFFE91E63)
    )

    data object ToggleFab : Screen(
        route = "toggle_fab",
        icon = Icons.Default.Visibility,
        title = "Toggle FAB",
        color = Color(0xFF2196F3)
    )
}


// In AppNavHost.kt update the client selection handling:

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
