package com.example.clientjetpack.App2.App.A.Main.MainScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.ConnexionCard_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiConexiontLuncher
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.B.Fragment.Compact_Presentoire_App_Produits_App2
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_Jemla_Com_PresentoirApp(
    modifier: Modifier = Modifier,
    WifiConexiontLuncher: WifiConexiontLuncher = koinViewModel(),
    focusedvaluesgetter: FocusedValuesGetter_app2 = koinInject(),
) {
    var isDisplayedConnexionWifiVisible by remember { mutableStateOf(false) }

    val uiState by WifiConexiontLuncher.uiState.collectAsState()
    val productDisplayController = uiState.productDisplayController
    val isWifiClientConnected_1 =
        !productDisplayController.isHostPhone && productDisplayController.isConnected

    val initProgress = focusedvaluesgetter.currentActive_M9AppCompt?.mainInitDataBaseProgressEtate  ?: 0f
    val isInitDone = initProgress >= 1f

    Box() {
        if (!isInitDone) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { initProgress },
                    modifier = Modifier.size(48.dp),
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            AnimatedVisibility(
                visible = isDisplayedConnexionWifiVisible || (!productDisplayController.isConnected
                        && !focusedvaluesgetter.currentApp_ItsWorkChezGrossisst)
            ) {
                ConnexionCard_app2(
                    headViewModel = WifiConexiontLuncher,
                    productDisplayController = productDisplayController,
                )
            }
            Compact_Presentoire_App_Produits_App2(
                isWifiClientConnected_1 = isWifiClientConnected_1,
                viewModelWifiConexiontLuncher = WifiConexiontLuncher,
                on_pour_send_data = { it1, it2 ->
                    WifiConexiontLuncher.sendOrderToClientDisplayer(
                        it1,
                        it2
                    )
                }
            )
        }
    }
}
