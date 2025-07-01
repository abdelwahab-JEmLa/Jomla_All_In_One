package Z_CodePartageEntreApps.Modules

import V.DiviseParSections.App._0.Navigation.Screen
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

class FragmentNavigationHandler {
    // Use late initialization for the NavController
    private var _navController: NavController? = null

    // Method to upsert the nav controller when it's available
    fun setNavController(navController: NavController) {
        _navController = navController
    }

    // Navigation configuration data class
    data class NavigationConfig(
        val popUpToStart: Boolean = true,
        val saveState: Boolean = true,
        val restoreState: Boolean = true,
        val launchSingleTop: Boolean = true,
        val inclusive: Boolean = false,
        val popUpToRoute: String? = null
    )

    // Default navigation configurations
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
        navigateTo(Screen.SoldCart, CART_CONFIG)
    }

    fun navigateToTestDataScreen() {
        navigateTo(Screen.NewFragTest, CART_CONFIG)
    }

    // Added missing method for TestProduitFastSearchDialog
    fun navigateToTestProduitFastSearchDialog() {
        navigateTo(Screen.TestProduitFastSearchDialog, OVERLAY_CONFIG)
    }
}
