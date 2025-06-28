package Z_CodePartageEntreApps.Proto.B.Par.App.A.AchatsManager.App._1.Shared.Module

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.GrossistAchatSec12FragID1_Main
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
            GrossistAchatSec12FragID1_Main()
        }
    }
}
