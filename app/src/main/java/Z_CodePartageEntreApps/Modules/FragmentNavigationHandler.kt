package Z_CodePartageEntreApps.Modules

import V.DiviseParSections.App._0.Navigation.Screen
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FragmentNavigationHandler {
    private var _navController: NavController? = null

    // Track the current active fragment
    private val _currentFragment = MutableStateFlow<Screen?>(null)
    val currentFragment: StateFlow<Screen?> = _currentFragment.asStateFlow()

    fun setNavController(navController: NavController) {
        _navController = navController
    }

    // Update current fragment from external sources (like AppNavHost)
    fun updateCurrentFragment(screen: Screen?) {
        _currentFragment.value = screen
    }

    // Update current fragment by route string
    fun updateCurrentFragmentByRoute(route: String?) {
        val screen = route?.let { getAllScreens().find { it.route == route } }
        _currentFragment.value = screen
    }

    // RepositorysMainSetter startup screen as current
    fun setStartupScreen(startupScreen: Screen) {
        _currentFragment.value = startupScreen
    }

    data class NavigationConfig(
        val popUpToStart: Boolean = true,
        val saveState: Boolean = true,
        val restoreState: Boolean = true,
        val launchSingleTop: Boolean = true,
        val inclusive: Boolean = false,
        val popUpToRoute: String? = null
    )

    companion object {
        val DEFAULT_CONFIG = NavigationConfig()
        val OVERLAY_CONFIG = NavigationConfig(popUpToStart = false, saveState = false, restoreState = false)
        val CART_CONFIG = NavigationConfig(popUpToStart = true, inclusive = false)
    }

    fun navigateTo(screen: Any, config: NavigationConfig = DEFAULT_CONFIG) {
        val route = when (screen) {
            is Screen -> screen.route
            is String -> screen
            else -> screen.toString()
        }

        // Update current fragment state
        val screenEnum = when (screen) {
            is Screen -> screen
            is String -> getAllScreens().find { it.route == screen }
            else -> null
        }
        _currentFragment.value = screenEnum

        _navController?.navigate(route) {
            if (config.launchSingleTop) {
                launchSingleTop = true
            }

            if (config.popUpToStart) {
                val startDestinationId = config.popUpToRoute?.let { customRoute ->
                    // If custom route specified, find it in the graph
                    _navController!!.graph.findNode(customRoute)?.id
                } ?: _navController!!.graph.findStartDestination().id

                popUpTo(startDestinationId) {
                    saveState = config.saveState
                    inclusive = config.inclusive
                }
            }

            if (config.restoreState) {
                restoreState = true
            }
        }
    }

    fun navigateToCartScreen() {
        navigateTo(Screen.Screen1PanieVentsFinale, CART_CONFIG)
    }

    fun navigateToTestDataScreen() {
        navigateTo(Screen.NewFragTest, CART_CONFIG)
    }

    fun navigateToTestProduitFastSearchDialog() {
        navigateTo(Screen.FragmentProduitFastSearchDialog, OVERLAY_CONFIG)
    }

    // Helper function to get all screen instances
    private fun getAllScreens(): List<Screen> {
        return listOf(
            Screen.A_ClientsLocationGps,
            Screen.FacadePresentoireProduits,
            Screen.EditDatabaseWithCreateNewArticles,
            Screen.Screen1PanieVentsFinale,
            Screen.Achats_Produits_Chez_Grossists,
            Screen.TravailleTempRecorder,
            Screen.NewFragTest,
            Screen.DialogTests,
            Screen.ToggleFab,
            Screen.FragmentProduitFastSearchDialog
        )
    }
}
