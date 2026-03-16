package Application4.App.Main.A.Navigation.Component

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "FragNavHandler"

class FragmentNavigationHandler_NewProto {

    // -------------------------------------------------------------------------
    // NavController
    // -------------------------------------------------------------------------

    private var _navController: NavController? = null
    val navController: NavHostController? get() = _navController as? NavHostController

    fun setNavController(navController: NavController) {
        Log.d(TAG, "🎛️ setNavController — NavController initialisé: ${navController::class.simpleName}")
        _navController = navController
        Log.d(TAG, "✅ setNavController — NavController prêt")
    }

    // -------------------------------------------------------------------------
    // Current fragment state
    // -------------------------------------------------------------------------

    private val _currentFragment = MutableStateFlow<Screen_NewProtoPattern?>(null)
    val currentFragment: StateFlow<Screen_NewProtoPattern?> = _currentFragment.asStateFlow()

    private val _activeFragments = mutableSetOf<Screen_NewProtoPattern>()

    fun updateCurrentFragment(screen: Screen_NewProtoPattern?) {
        val previousFragment = _currentFragment.value
        Log.d(TAG, "🔄 updateCurrentFragment — transition: ${previousFragment?.route ?: "null"} → ${screen?.route ?: "null"}")

        _currentFragment.value = screen
        if (screen != null) {
            _activeFragments.add(screen)
            Log.d(TAG, "✅ updateCurrentFragment — fragment activé: ${screen.route} | fragments actifs: ${_activeFragments.map { it.route }}")
        } else {
            Log.w(TAG, "⚠️ updateCurrentFragment — appelé avec screen=null, aucun fragment actif.")
        }
    }

    fun updateCurrentFragmentByRoute(route: String?) {
        val previousFragment = _currentFragment.value
        Log.d(TAG, "🔄 updateCurrentFragmentByRoute — route demandée: '$route' | fragment actuel: ${previousFragment?.route ?: "null"}")

        val screen = route?.let { r -> getAllScreens().find { it.route == r } }
        _currentFragment.value = screen

        if (screen != null) {
            _activeFragments.add(screen)
            Log.d(TAG, "✅ updateCurrentFragmentByRoute — fragment trouvé et activé: ${screen.route} | fragments actifs: ${_activeFragments.map { it.route }}")
        } else {
            Log.w(
                TAG, "⚠️ updateCurrentFragmentByRoute — aucun fragment sélectionné. " +
                        "route reçue='$route'. " +
                        "Routes connues=${getAllScreens().map { it.route }}"
            )
        }
    }

    fun setStartupScreen(screen: Screen_NewProtoPattern) {
        Log.d(TAG, "🚀 setStartupScreen — initialisation: ${screen.route}")
        _currentFragment.value = screen
        _activeFragments.add(screen)
        Log.d(TAG, "✅ setStartupScreen — fragment de démarrage activé: ${screen.route} | fragments actifs: ${_activeFragments.map { it.route }}")
    }

    // ✅ FIX: closeAllActiveFragments() ne remet plus _currentFragment à null.
    // Elle vide seulement le set interne des fragments actifs.
    // Appeler cette méthode ne désynchronise plus la navigation.
    fun closeAllActiveFragments() {
        Log.d(TAG, "🧹 closeAllActiveFragments — vidage du set de fragments actifs: ${_activeFragments.map { it.route }}")
        _activeFragments.clear()
        // ✅ NE PAS remettre _currentFragment.value = null ici
        // Cela causait une désynchronisation entre le state de navigation
        // et le NavController au 2ème lancement.
        Log.d(TAG, "✅ closeAllActiveFragments — set vidé | _currentFragment conservé: ${_currentFragment.value?.route ?: "null"}")
    }

    // -------------------------------------------------------------------------
    // Resource cleanup callback for Compact_Presentoire
    // Wired to ViewModel_NewProtoPatterns.releaseResources() from MainScreen ONLY.
    // NOTE: This is NOT onCleared() — onCleared() must only be called by the Android framework.
    // This callback is invoked when navigating away from Compact_Presentoire to allow
    // the ViewModel to release resources cleanly before the navigation completes.
    // -------------------------------------------------------------------------

    private var onLeaveCompactPresentoire: (() -> Unit)? = null
    private var onEnterCompactPresentoire: (() -> Unit)? = null

    fun setOnLeaveCompactPresentoireCallback(callback: () -> Unit) {
        Log.d(TAG, "📝 setOnLeaveCompactPresentoireCallback — callback enregistré")
        onLeaveCompactPresentoire = callback
    }

    fun setOnEnterCompactPresentoireCallback(callback: () -> Unit) {
        Log.d(TAG, "📝 setOnEnterCompactPresentoireCallback — callback enregistré")
        onEnterCompactPresentoire = callback
    }

    // -------------------------------------------------------------------------
    // Navigation config
    // -------------------------------------------------------------------------

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
        val OVERLAY_CONFIG = NavigationConfig(
            popUpToStart = false,
            saveState = false,
            restoreState = false
        )
        val CART_CONFIG = NavigationConfig(
            popUpToStart = true,
            inclusive = false
        )
    }

    // -------------------------------------------------------------------------
    // Core navigateTo — all typed helpers delegate here
    // -------------------------------------------------------------------------

    fun navigateTo(screen: Any, config: NavigationConfig = DEFAULT_CONFIG) {
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d(TAG, "🧭 navigateTo — DÉBUT | destination: $screen")

        val route = when (screen) {
            is Screen_NewProtoPattern -> screen.route
            is String -> screen
            else -> screen.toString()
        }

        val screenEnum = when (screen) {
            is Screen_NewProtoPattern -> screen
            is String -> getAllScreens().find { it.route == screen }
            else -> null
        }

        val currentScreen = _currentFragment.value
        Log.d(TAG, "📍 navigateTo — état actuel: ${currentScreen?.route ?: "null"} → nouvelle destination: ${screenEnum?.route ?: route}")
        Log.d(TAG, "⚙️ navigateTo — config: popUpToStart=${config.popUpToStart}, saveState=${config.saveState}, restoreState=${config.restoreState}, launchSingleTop=${config.launchSingleTop}")

        // Detect departure from Compact_Presentoire BEFORE updating state.
        val leavingCompactPresentoire =
            currentScreen == Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4
                    && screenEnum != Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4

        val enteringCompactPresentoire =
            currentScreen != Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4
                    && screenEnum == Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4

        // Phase 1: Cleanup resources if leaving Compact_Presentoire
        if (leavingCompactPresentoire) {
            Log.d(TAG, "🔴 navigateTo — PHASE 1: Départ de Compact_Presentoire détecté")
            Log.d(TAG, "🧹 navigateTo — invocation de releaseResources() AVANT mise à jour d'état")
            onLeaveCompactPresentoire?.invoke()
            Log.d(TAG, "✅ navigateTo — releaseResources() terminé")
        }

        // Phase 2: Update state
        Log.d(TAG, "📝 navigateTo — PHASE 2: Mise à jour de l'état")
        _currentFragment.value = screenEnum
        if (screenEnum != null) {
            _activeFragments.add(screenEnum)
            Log.d(TAG, "✅ navigateTo — fragment ajouté aux actifs: ${screenEnum.route}")
        }
        Log.d(TAG, "📊 navigateTo — fragments actifs: ${_activeFragments.map { it.route }}")

        // Phase 3: Perform navigation
        Log.d(TAG, "🚀 navigateTo — PHASE 3: Navigation vers '$route'")
        _navController?.navigate(route) {
            if (config.launchSingleTop) {
                launchSingleTop = true
                Log.d(TAG, "   ↪ launchSingleTop = true")
            }
            if (config.popUpToStart) {
                val startDestinationId = config.popUpToRoute?.let {
                    _navController!!.graph.findNode(it)?.id
                } ?: _navController!!.graph.findStartDestination().id
                popUpTo(startDestinationId) {
                    saveState = config.saveState
                    inclusive = config.inclusive
                }
                Log.d(TAG, "   ↪ popUpTo startDestination | saveState=${config.saveState}, inclusive=${config.inclusive}")
            }
            if (config.restoreState) {
                restoreState = true
                Log.d(TAG, "   ↪ restoreState = true")
            }
        }
        Log.d(TAG, "✅ navigateTo — navigation système effectuée")

        // Phase 4: Initialize data if entering Compact_Presentoire
        if (enteringCompactPresentoire) {
            Log.d(TAG, "🟢 navigateTo — PHASE 4: Entrée dans Compact_Presentoire détectée")
            Log.d(TAG, "🔄 navigateTo — invocation de initializeData() APRÈS navigation")
            onEnterCompactPresentoire?.invoke()
            Log.d(TAG, "✅ navigateTo — initializeData() terminé")
        }

        Log.d(TAG, "🎯 navigateTo — FIN | fragment actuel: ${_currentFragment.value?.route ?: "null"}")
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }

    // -------------------------------------------------------------------------
    // Typed navigate helpers — one per fragment
    // -------------------------------------------------------------------------

    fun navigateTo_Compact_Presentoire(config: NavigationConfig = DEFAULT_CONFIG) {
        Log.d(TAG, "🎬 navigateTo_Compact_Presentoire — demande de navigation")
        navigateTo(Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4, config)
    }

    fun navigateTo_Panier(config: NavigationConfig = DEFAULT_CONFIG) {
        Log.d(TAG, "🛒 navigateTo_Panier — demande de navigation")
        navigateTo(Screen_NewProtoPattern.Panier, config)
    }

    fun navigateTo_A_Clients_LocationGps(config: NavigationConfig = DEFAULT_CONFIG) {
        Log.d(TAG, "📍 navigateTo_A_Clients_LocationGps — demande de navigation")
        navigateTo(Screen_NewProtoPattern.A_Clients_LocationGps, config)
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private fun getAllScreens() = listOf(
        Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4,
        Screen_NewProtoPattern.A_Clients_LocationGps,
        Screen_NewProtoPattern.Panier
    )
}

// -------------------------------------------------------------------------
// Composable extension — call once at the NavHost level
// -------------------------------------------------------------------------

@Composable
fun FragmentNavigationHandler_NewProto.rememberAndSetNavController(): NavHostController {
    Log.d(TAG, "🎨 rememberAndSetNavController — création du NavController")
    val navController = rememberNavController()
    setNavController(navController)
    Log.d(TAG, "✅ rememberAndSetNavController — NavController créé et configuré")
    return navController
}
