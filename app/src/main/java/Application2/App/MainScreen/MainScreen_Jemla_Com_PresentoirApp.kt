package Application2.App.MainScreen

import Application2.App.App.ViewModel.ViewModel_MainFragment
import Application2.App.Base.Modules.ConnexionCard_App2
import Application2.App.Fragment.Compact_Presentoire_App_Produits_App2
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_Jemla_Com_PresentoirApp(
    modifier: Modifier = Modifier,
    vm: ViewModel_MainFragment = koinViewModel(),
) {
    val wifiState by vm.wifiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        if (!wifiState.isConnected) {
            ConnexionCard_App2(vm = vm)
        }

        Compact_Presentoire_App_Produits_App2(
            modifier = Modifier
                .weight(1f)
            ,
            productDisplayController = wifiState,
            viewModel = vm,
        )
    }
}
