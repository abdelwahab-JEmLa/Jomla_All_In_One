package Z_CodePartageEntreApps.Proto.B.ParSections.Fragment.A.AchatsManager.App._1.Shared.Module

import Z_CodePartageEntreApps.Proto.B.ParSections.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package.A_MainScreen_APP2_FragID3
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
           // A_MainScreen_APP2_ID_2()
          //  A_MainScreenApp2FragID_1()
            A_MainScreen_APP2_FragID3()
        }
    }
}
