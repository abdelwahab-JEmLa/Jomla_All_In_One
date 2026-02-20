package Application2.App.MainScreen

import Application2.App.App.ViewModel.ViewModel_MainFragment
import Application2.App.Base.Repository.RepositorysMainGetter_app2
import Application2.App.Fragment.Compact_Presentoire_App_Produits_App2
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
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_Jemla_Com_PresentoirApp(
    modifier: Modifier = Modifier,
    vm: ViewModel_MainFragment = koinViewModel(),
    repositorysMainGetter_app2: RepositorysMainGetter_app2 = koinInject()
) {
    val mainInitDataBaseProgressEtate =
        repositorysMainGetter_app2.active_Central_Values.mainInitDataBaseProgressEtate
    val isInitDone = mainInitDataBaseProgressEtate >= 1f

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
            vm = vm,
        )
    }
}
