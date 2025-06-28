package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.View.B.List.B.Main.VIEW

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.A.ViewModel.CommandeProduitsViewModel
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.W.Test.LoadingScreenB_ClientProtoJuin3
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun AcheteursDeCetteProduit(
    viewModel: CommandeProduitsViewModel = koinViewModel(),
    clientQuantities: Map<Long, Long>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        val uiState by viewModel.uiState.collectAsState()
        val progress = uiState.mainLoadingProgress
        val datasB_ClientInfosProtoJuin3List by remember(uiState.B_ClientInfosProtoJuin3List) {
            mutableStateOf(
                uiState.B_ClientInfosProtoJuin3List
            )
        }
        val clientDataBaseSnapList = datasB_ClientInfosProtoJuin3List

        if (progress < 1.0f) {
            LoadingScreenB_ClientProtoJuin3(progress)
        } else {
            // Display each client with their quantity
            clientQuantities.forEach { (clientId, quantity) ->
                // Skip clientAchteurs with zero quantity
                if (quantity <= 0) return@forEach


                val clientAchteurName = clientDataBaseSnapList.find {
                    it.id == clientId
                }?.nom ?: "ClientAchteur #$clientId"

                Text(
                    text = "$clientAchteurName ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Qté: $quantity",
                    fontWeight = FontWeight.Bold
                )

                // Add spacing between clientAchteurs
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
