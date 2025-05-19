package P0_MainScreen.Main.Windows

import V.DiviseParSections.App.C_AtelieModbile.Fragment.ViewModel.TarificationViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun TarriffesButtons(
    showLabels: Boolean,
    tarificationViewModel: TarificationViewModel = koinViewModel(),
) {
    val uiState = tarificationViewModel.uiState.value
    val activeProduit = uiState.produitsNoSqlDataBase.produits.find { it.itsActiveOne }
    val activeClient = activeProduit?.clientAchteurs?.find { it.itsActiveOne }
    val activeTypeTarification = activeClient?.typeTarification?.find { it.itsActiveOne }
    val tariffsList = activeTypeTarification?.tariffsList ?: emptyList()

    tariffsList.forEach { tariff ->
        // Find the parent TypeTarification
        val parentTypeTarificationId = activeTypeTarification?.infosId ?: 0L
        val relatedTypeInfos = tarificationViewModel.getByID_C_TypeTarificationInfos(
            parentTypeTarificationId
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Use the icon and color from the related TypeTarification
            val couleurButton = relatedTypeInfos?.entityCorrespond?.couleur ?: Color(0xFFF44336)
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.size(40.dp),
                containerColor = couleurButton
            ) {
                relatedTypeInfos?.entityCorrespond?.icon?.let { iconVector ->
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null
                    )
                }
            }
            if (showLabels) {
                Text(
                    "${tariff.valeur}",
                    modifier = Modifier
                        .background(couleurButton)
                        .padding(4.dp),
                    color = Color.White
                )
            }
        }
    }
}
