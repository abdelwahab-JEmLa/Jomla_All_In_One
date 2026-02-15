package com.example.clientjetpack.App2.App.A.Main.MainScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.B.Fragment.Compact_Presentoire_App_Produits_App2
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_Jemla_Com_PresentoirApp(
    modifier: Modifier = Modifier,
    focusedvaluesgetter: FocusedValuesGetter_app2 = koinInject(),
) {
    val initProgress = focusedvaluesgetter.active_Central_Values.mainInitDataBaseProgressEtate
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
            Compact_Presentoire_App_Produits_App2()
        }
    }
}
