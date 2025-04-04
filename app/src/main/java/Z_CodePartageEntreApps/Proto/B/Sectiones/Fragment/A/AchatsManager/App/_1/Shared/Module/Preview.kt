package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Module

import W.Fragments.A.PanierFinaleDAchat.APP.A_MainScreenApp2FragID_1
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Preview()
@Composable
fun MainScreenPreview_APP2() {
    MaterialTheme {

        KoinAndroidContext {
           // MainScreen_APP2_ID_2()
            A_MainScreenApp2FragID_1()
        }
    }
}
