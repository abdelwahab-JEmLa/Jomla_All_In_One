package Z_CodePartageEntreApps.Modules

import V.DiviseParSections.App._0.Navigation.Screen
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

class FragmentNavigationHandler {
    // Use late initialization for the NavController
    private var _navController: NavController? = null

    // Method to set the nav controller when it's available
    fun setNavController(navController: NavController) {
        _navController = navController
    }

    fun navigateToMainScreen() {
        _navController?.navigate(Screen.EditDatabaseWithCreateNewArticles.route) {
            popUpTo(_navController!!.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToCartScreen() {
        _navController?.navigate(Screen.SoldCart.route) {
            // Keep existing flag
            launchSingleTop = true
            // Don't pop from back stack so we can return with back button
            popUpTo(_navController!!.graph.findStartDestination().id) {
                saveState = true
                inclusive = false
            }
            // Restore state when navigating back
            restoreState = true
        }
    }
    fun navigateToClientMapScreen() {
        _navController?.navigate(Screen.A_ClientsLocationGps.route) {
            launchSingleTop = true
        }
    }

    fun navigateToCommandeProduits() {
        _navController?.navigate(Screen.CommandeProduits.route) {
            launchSingleTop = true
        }
    }

    fun navigateToTravailleTempRecorder() {
        _navController?.navigate(Screen.TravailleTempRecorder.route) {
            launchSingleTop = true
        }
    }
}
