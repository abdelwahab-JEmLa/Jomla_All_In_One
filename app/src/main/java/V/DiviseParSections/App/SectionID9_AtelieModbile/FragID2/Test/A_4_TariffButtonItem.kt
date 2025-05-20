package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun TariffButtonItem(
    typeTarification: TypeTarificationEnumT2,
    tariffs: List<D_TarificationInfosT2>,
    showLabels: Boolean
) {
    val latestTariff = tariffs.maxByOrNull { it.id }
    if (latestTariff == null) return

    var latestTariffLocalData by remember { mutableStateOf(latestTariff) }
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val couleurButton = typeTarification.couleur

        if (showLabels) {
            val typeName = typeTarification.nomArabe
            val prixCurrency = "${latestTariffLocalData.prixCurrency} "

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ElevatedCard {
                    Text(
                        typeName,
                        modifier = Modifier
                            .background(couleurButton)
                            .padding(4.dp),
                        color = Color.White
                    )
                }

                // Only show price and adjustment buttons for types that aren't AU_GERANT
                if (typeTarification != TypeTarificationEnumT2.AU_GERANT) {
                    // Add '+' button for DEFINI type
                    if (typeTarification == TypeTarificationEnumT2.DEFINI) {
                        IconButton(
                            onClick = {
                                // Add 5.0 to the price
                                latestTariffLocalData = latestTariffLocalData.copy(
                                    prixCurrency = latestTariffLocalData.prixCurrency + 5.0
                                )
                                Toast.makeText(context, "Prix augmenté: ${latestTariffLocalData.prixCurrency}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Augmenter le prix",
                                tint = couleurButton
                            )
                        }
                    }

                    // Price display for all non-AU_GERANT types
                    ElevatedCard {
                        Text(
                            prixCurrency,
                            modifier = Modifier
                                .background(couleurButton)
                                .padding(4.dp),
                            color = Color.White
                        )
                    }

                    // Add '-' button for DEFINI type
                    if (typeTarification == TypeTarificationEnumT2.DEFINI) {
                        IconButton(
                            onClick = {
                                // Subtract 5.0 from the price, but don't go below 0
                                val newPrice = (latestTariffLocalData.prixCurrency - 5.0).coerceAtLeast(0.0)
                                latestTariffLocalData = latestTariffLocalData.copy(
                                    prixCurrency = newPrice
                                )
                                Toast.makeText(context, "Prix diminué: ${latestTariffLocalData.prixCurrency}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = "Diminuer le prix",
                                tint = couleurButton
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                val typeName = typeTarification.name
                val message = "$typeName: ${latestTariffLocalData.prixCurrency}"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(40.dp),
            containerColor = couleurButton
        ) {
            typeTarification.iconVector?.let { iconVector ->
                Icon(
                    imageVector = iconVector,
                    contentDescription = null
                )
            }
        }
    }
}
