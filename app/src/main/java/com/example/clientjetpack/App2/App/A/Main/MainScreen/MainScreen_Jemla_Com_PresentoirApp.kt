package com.example.clientjetpack.App2.App.A.Main.MainScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.ConnexionCard_App2
import com.example.clientjetpack.App2.App.B.Fragment.Compact_Presentoire_App_Produits_App2
import com.example.clientjetpack.App2.App.B.Fragment.ViewModel.ViewModel_MainFragment
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_Jemla_Com_PresentoirApp(
    modifier: Modifier = Modifier,
    vm: ViewModel_MainFragment = koinViewModel(),
) {
    val wifiState by vm.wifiState.collectAsState()

    if (!wifiState.isConnected) {
        ConnexionCard_App2(vm = vm)
    }

    Compact_Presentoire_App_Produits_App2(
        productDisplayController = wifiState,
        viewModel = vm,
    )
}
