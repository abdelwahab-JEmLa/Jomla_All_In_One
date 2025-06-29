package Z_CodePartageEntreApps.Proto.B.Par.App.A.AchatsManager.App._1.Shared.Module

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Screen_GrossistAchatSec12FragID1
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
            Screen_GrossistAchatSec12FragID1()
        }
    }
}
