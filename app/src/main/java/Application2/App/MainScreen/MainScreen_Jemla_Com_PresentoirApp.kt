package Application2.App.MainScreen

import Application2.App.App.ViewModel.ViewModel_MainFragment
import Application2.App.Base.Repository.RepositorysMainGetter_app2
import Application2.App.Fragment.Compact_Presentoire_App_Produits_App2
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import EntreApps.Shared.Modules.Base.AppDatabase
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_Jemla_Com_PresentoirApp(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase = koinInject(),
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto = koinInject(),
    repositorysMainGetter_app2: RepositorysMainGetter_app2 = koinInject()
) {
    val context = LocalContext.current
    val vewModel_MainFragment: ViewModel_MainFragment = viewModel(
        factory = viewModelFactory {
            initializer {
                ViewModel_MainFragment(
                    context = context.applicationContext,
                    appDatabase = appDatabase,
                    repositorysMainGetter_app2 = repositorysMainGetter_app2,
                )
            }
        }
    )
    val mainInitDataBaseProgressEtate =
        repositorysMainGetter_app2.active_Central_Values.mainInitDataBaseProgressEtate

    val wifiState by vewModel_MainFragment.wifiState.collectAsState()
    val isConnectedAsClient = wifiState.isConnected && !wifiState.isHostPhone

    val isInitDone = isConnectedAsClient || mainInitDataBaseProgressEtate >= 1f

    if (!isInitDone) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { mainInitDataBaseProgressEtate },
                modifier = Modifier.size(48.dp),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Compact_Presentoire_App_Produits_App2(
            modifier = Modifier,
            vm = vewModel_MainFragment,
        )
    }
}
