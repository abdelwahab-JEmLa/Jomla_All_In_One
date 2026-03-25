package Application2.App.Fragment

import Application2.App.App.ViewModel.ViewModel_MainFragment
import Application2.App.Base.Modules.ConnexionCard_App2
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.ifTrue
import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Compact_Presentoire_App_Produits_App2(
    vm: ViewModel_MainFragment = koinViewModel(),
    modifier: Modifier,
) {
    val uiState by vm.uiState.collectAsState()
    val wifiState by vm.wifiState.collectAsState()
    val isInitDone = uiState.initDatasProgressEtate >= 1f

    val context = LocalContext.current
    val view = LocalView.current
    val window = (context as? Activity)?.window

    LaunchedEffect(wifiState.isConnected) {
        window?.let { w ->
            if (wifiState.isConnected && !wifiState.isHostPhone) {
                WindowCompat.setDecorFitsSystemWindows(w, false)
                w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                WindowInsetsControllerCompat(w, view).apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                WindowCompat.setDecorFitsSystemWindows(w, true)
                w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                WindowInsetsControllerCompat(w, view).show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    if (!isInitDone) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { uiState.initDatasProgressEtate },
                modifier = Modifier.size(48.dp),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            (!wifiState.isConnected).ifTrue {
                ConnexionCard_App2(vm = vm)
            }
            Etager_LazyColumn_App2(
                cataloguesWithCategoriesAndProducts = uiState.grpList_cataloguesWithCategoriesAndProducts,
                viewModel = vm,
            )
        }
    }
}


