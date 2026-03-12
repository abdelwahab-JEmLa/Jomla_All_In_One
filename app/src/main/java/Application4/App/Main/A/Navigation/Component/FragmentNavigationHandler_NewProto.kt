package Application4.App.Main.A.Navigation.Component

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "FragNavHandler"

class FragmentNavigationHandler_NewProto {
    private var _navController: NavController? = null
    val navController: NavHostController? get() = _navController as? NavHostController

    private val _currentFragment = MutableStateFlow<Screen_NewProtoPattern?>(null)
    val currentFragment: StateFlow<Screen_NewProtoPattern?> = _currentFragment.asStateFlow()

    private val _activeFragments = mutableSetOf<Screen_NewProtoPattern>()

    fun setNavController(navController: NavController) { _navController = navController }

    fun updateCurrentFragment(screen: Screen_NewProtoPattern?) {
        _currentFragment.value = screen
        if (screen != null) {
            _activeFragments.add(screen)
        } else {
            Log.w(TAG, "updateCurrentFragment — appelé avec screen=null, aucun fragment actif.")
        }
    }

    fun updateCurrentFragmentByRoute(route: String?) {
        val screen = route?.let { getAllScreen_NewProtoPatterns().find { it.route == route } }
        _currentFragment.value = screen
        if (screen != null) {
            _activeFragments.add(screen)
        } else {
            Log.w(TAG, "updateCurrentFragmentByRoute — aucun fragment sélectionné. " +
                    "route reçue='$route'. " +
                    "Routes connues=${getAllScreen_NewProtoPatterns().map { it.route }}"
            )
        }
    }

    fun setStartupScreen_NewProtoPattern(startupScreen_NewProtoPattern: Screen_NewProtoPattern) {
        _currentFragment.value = startupScreen_NewProtoPattern
        _activeFragments.add(startupScreen_NewProtoPattern)
    }

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
            is Screen_NewProtoPattern -> screen.route
            is String -> screen
            else -> screen.toString()
        }
        val screenEnum = when (screen) {
            is Screen_NewProtoPattern -> screen
            is String -> getAllScreen_NewProtoPatterns().find { it.route == screen }
            else -> null
        }
        _currentFragment.value = screenEnum
        if (screenEnum != null) _activeFragments.add(screenEnum)

        _navController?.navigate(route) {
            if (config.launchSingleTop) launchSingleTop = true
            if (config.popUpToStart) {
                val startDestinationId = config.popUpToRoute?.let {
                    _navController!!.graph.findNode(it)?.id
                } ?: _navController!!.graph.findStartDestination().id
                popUpTo(startDestinationId) {
                    saveState = config.saveState
                    inclusive = config.inclusive
                }
            }
            if (config.restoreState) restoreState = true
        }
    }

    private fun getAllScreen_NewProtoPatterns() = listOf(
        Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4,
        Screen_NewProtoPattern.A_Clients_LocationGps,
        Screen_NewProtoPattern.Panier
    )
}

@Composable
fun FragmentNavigationHandler_NewProto.rememberNavController(): NavHostController {
    val navController = rememberNavController()
    setNavController(navController)
    return navController
}
