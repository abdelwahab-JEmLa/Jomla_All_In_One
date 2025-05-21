package V.DiviseParSections.App.SectionID8.FloatingButtons.App.FragID1.Windows

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitsNoSqlDataBase
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.TarificationViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
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
    val typeTarifications by remember(activeClient) {
        mutableStateOf(activeClient?.typeTarification ?: emptyList())
    }

    val shouldShowLoading = uiState.isLoading && typeTarifications.isEmpty()

    // Log all the tariff types for debugging
    if (typeTarifications.isNotEmpty()) {
        typeTarifications.forEach { typeTarif ->
            val typeInfo = tarificationViewModel.getByID_C_TypeTarificationInfos(typeTarif.infosId)
            val typeName = typeInfo?.nom ?: "Unknown Type"
            val latestValue = typeTarif.tariffsList.maxByOrNull { it.vidTimestamp }?.valeur ?: 0.0
            Log.d("TariffsDebug", "Type ID: ${typeTarif.infosId}, Name: $typeName, Value: $latestValue")
        }
    }

    Column {
        LoadingTariffItem(isLoading = shouldShowLoading)

        // Debug text for development
        if (typeTarifications.isNotEmpty()) {
            Text(
                "Available tariffs: ${typeTarifications.size}",
                Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }

        // Process each tarification type individually
        typeTarifications.forEach { typeTarification ->
            TariffButtonItem(
                typeTarification = typeTarification,
                showLabels = showLabels,
                tarificationViewModel = tarificationViewModel
            )
            Spacer(modifier = Modifier.height(4.dp)) // Add spacing between tariff buttons
        }
    }
}

@Composable
private fun TariffButtonItem(
    typeTarification: ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification,
    showLabels: Boolean,
    tarificationViewModel: TarificationViewModel
) {
    val relatedTypeInfos = tarificationViewModel.getByID_C_TypeTarificationInfos(
        typeTarification.infosId
    )

    // Find the most recent tariff value
    val latestTariff = typeTarification.tariffsList.maxByOrNull { it.vidTimestamp }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val couleurButton = relatedTypeInfos?.entityCorrespond?.couleur ?: Color(0xFFF44336)
        val context = LocalContext.current

        FloatingActionButton(
            onClick = {
                // Show toast with the tariff value
                latestTariff?.let {
                    val typeName = relatedTypeInfos?.nom ?: "Tarif"
                    val message = "$typeName: ${it.valeur}"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.size(40.dp),
            containerColor = couleurButton
        ) {
            relatedTypeInfos?.entityCorrespond?.iconVector?.let { iconVector ->
                Icon(
                    imageVector = iconVector,
                    contentDescription = null
                )
            }
        }

        if (showLabels && latestTariff != null) {
            ElevatedCard {
                // Determine the correct type name based on infosId
                val typeName = relatedTypeInfos?.nom ?: "Tarif"

                Text(
                    "${latestTariff.valeur} $typeName",
                    modifier = Modifier
                        .background(couleurButton)
                        .padding(4.dp),
                    color = Color.White
                )

                // Debug logging for this specific tariff
                Log.d("TariffDisplay", "Displaying tariff: infosId=${typeTarification.infosId}, type=$typeName, value=${latestTariff.valeur}")
            }
        }
    }
}
