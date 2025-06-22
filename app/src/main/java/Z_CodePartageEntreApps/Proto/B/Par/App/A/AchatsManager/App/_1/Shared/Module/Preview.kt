package Z_CodePartageEntreApps.Proto.B.Par.App.A.AchatsManager.App._1.Shared.Module

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Old.Proto.A_APP1FragID3_MainScreen
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
           // A_MainScreen_APP2_ID_2PanierFinaleDAchat()
          //  A_MainScreenApp2FragID_1()
            A_APP1FragID3_MainScreen()
        }
    }
}
