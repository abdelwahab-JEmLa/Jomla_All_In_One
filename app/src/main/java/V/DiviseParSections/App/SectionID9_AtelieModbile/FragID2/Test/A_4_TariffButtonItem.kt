package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TariffButtonItem(
    typeTarification: TypeTarificationEnumT2,
    tariffs: List<D_TarificationInfosT2>,
    showLabels: Boolean,
    onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfosT2, Context) -> () -> Unit
) {
    val latestTariff = tariffs.maxByOrNull { it.id }
    if (latestTariff == null) return

    var latestTariffLocalData by remember { mutableStateOf(latestTariff) }
    val context = LocalContext.current

    // Special layout for GERANT type
    if (gerantButton(typeTarification, showLabels)) return

    // Regular layout for other types
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

                if (typeTarification != TypeTarificationEnumT2.AU_GERANT) {
                    if (typeTarification == TypeTarificationEnumT2.DEFINI) {
                        IconButton(
                            onClick = {
                                val newPrice = (latestTariffLocalData.prixCurrency - 5.0).coerceAtLeast(0.0)
                                latestTariffLocalData = latestTariffLocalData.copy(
                                    prixCurrency = newPrice
                                )
                            },
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = "Diminuer le prix",
                                tint = couleurButton
                            )
                        }
                    }

                    ElevatedCard(
                        onClick = {
                            if (typeTarification == TypeTarificationEnumT2.DEFINI) {
                                latestTariffLocalData = latestTariffLocalData.copy(
                                    prixCurrency = latestTariffLocalData.prixCurrency + 5.0
                                )
                            }
                        }
                    ) {
                        val pls = if (typeTarification == TypeTarificationEnumT2.DEFINI)
                            " +" else ""
                        Text(
                            "$prixCurrency$pls",
                            modifier = Modifier
                                .background(couleurButton)
                                .padding(4.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onClickPrixButton(typeTarification, latestTariffLocalData, context),
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

@Composable
private fun gerantButton(
    typeTarification: TypeTarificationEnumT2,
    showLabels: Boolean
): Boolean {
    if (typeTarification == TypeTarificationEnumT2.AU_GERANT) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Vertical text for AU_GERANT
            if (showLabels) {
                ElevatedCard {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(typeTarification.couleur)
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                            .height(185.dp)
                            .width(30.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val fontSize = 12.sp

                            Text(
                                text = "التقدير",
                                maxLines = 1,
                                fontSize = fontSize,
                                modifier = Modifier.rotate(-90f),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "للمدير",
                                maxLines = 1,
                                fontSize = fontSize,
                                modifier = Modifier.rotate(-90f),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
        return true
    }
    return false
}
