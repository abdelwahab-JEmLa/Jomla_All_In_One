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

    // Track all screens that have been pushed as "active" overlays
    private val _activeFragments = mutableSetOf<Screen>()

    fun setNavController(navController: NavController) {
        _navController = navController
    }

    fun updateCurrentFragment(screen: Screen?) {
        _currentFragment.value = screen
        if (screen != null) _activeFragments.add(screen)
    }

    fun updateCurrentFragmentByRoute(route: String?) {
        val screen = route?.let { getAllScreens().find { it.route == route } }
        _currentFragment.value = screen
        if (screen != null) _activeFragments.add(screen)
    }

    fun setStartupScreen(startupScreen: Screen) {
        _currentFragment.value = startupScreen
        _activeFragments.add(startupScreen)
    }

    /**
     * Clears all tracked active fragments and resets current fragment to null.
     * Use this when entering a focused/isolated screen that should own all resources.
     */
    fun closeAllActiveFragments() {
        _activeFragments.clear()
        _currentFragment.value = null
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

        val screenEnum = when (screen) {
            is Screen -> screen
            is String -> getAllScreens().find { it.route == screen }
            else -> null
        }
        _currentFragment.value = screenEnum
        if (screenEnum != null) _activeFragments.add(screenEnum)

        _navController?.navigate(route) {
            if (config.launchSingleTop) {
                launchSingleTop = true
            }

            if (config.popUpToStart) {
                val startDestinationId = config.popUpToRoute?.let { customRoute ->
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

    fun navigateToMainDataBaseInitFactory1Produit() {
        navigateTo(Screen.Main_DataBaseInitFactory_1Produit, DEFAULT_CONFIG)
    }

    fun navigateToEducationFragment() {
        navigateTo(Screen.EducationFragment, DEFAULT_CONFIG)
    }

    private fun getAllScreens(): List<Screen> {
        return listOf(
            Screen.Fragment_Compact_Presentoir_Echantilliants,
            Screen.Compact_Presentoire_App_Produits_FragID5,
            Screen.A_Clients_LocationGps,
            Screen.EditDatabaseWithCreateNewArticles,
            Screen.Screen1PanieVentsFinale,
            Screen.Achats_Produits_Chez_Grossists,
            Screen.TravailleTempRecorder,
            Screen.NewFragTest,
            Screen.DialogTests,
            Screen.ToggleFab,
            Screen.FragmentProduitFastSearchDialog,
            Screen.Main_DataBaseInitFactory_1Produit,
            Screen.EducationFragment
        )
    }
}
