package V.DiviseParSections.App.SectionID8.FloatingButtons.App.FragID1.Windows

import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.TarificationViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun TarriffesButtonsP() {
    TariffsButtons()
}

@Composable
private fun LoadingTariffItem(isLoading: Boolean = true) {
    if (!isLoading) return

    ElevatedCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.size(40.dp),
                containerColor = Color.Gray
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            }
            Text(
                "Loading...",
                modifier = Modifier
                    .background(Color.Gray)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}

@Composable
fun TariffsButtons(
    showLabels: Boolean = true,
    tarificationViewModel: TarificationViewModel = koinViewModel(),
) {
    val uiState = tarificationViewModel.uiState.value
    val activeProduit = uiState.produitsNoSqlDataBase.produits.find { it.itsActiveOne }
    val activeClient = activeProduit?.clientAchteurs?.find { it.itsActiveOne }
    val activeTypeTarification = activeClient?.typeTarification?.find { it.itsActiveOne }

    val tariffsList by remember(activeTypeTarification) {
        mutableStateOf(activeTypeTarification?.tariffsList ?: emptyList())
    }

    val shouldShowLoading = uiState.isLoading && tariffsList.isEmpty()
    LoadingTariffItem(isLoading = shouldShowLoading)

    if (tariffsList.isNotEmpty()) {
        tariffsList.forEach { tariff ->
            val parentTypeTarificationId = activeTypeTarification?.infosId ?: 0L
            val relatedTypeInfos = tarificationViewModel.getByID_C_TypeTarificationInfos(
                parentTypeTarificationId
            )

            ElevatedCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
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
    }
}
