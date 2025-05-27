package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.B.Models.TypeTarificationEnumT2
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TariffButtonItem(
    typeTarification: TypeTarificationEnumT2,
    tariffs: List<D_TarificationInfos>,
    showLabels: Boolean,
    onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit,
    nombreUnite: Int= 10,
    context: Context,
) {
    val latestTariff = tariffs.maxByOrNull { it.id }
    if (latestTariff == null) return

    var latestTariffLocalData by remember { mutableStateOf(latestTariff) }

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
                    val labelBackgroundColor = if (typeTarification == TypeTarificationEnumT2.DEFINI) {
                        Color.Yellow
                    } else {
                        couleurButton
                    }

                    val labelTextColor = if (typeTarification == TypeTarificationEnumT2.DEFINI) {
                        Color.Black
                    } else {
                        Color.White
                    }

                    Text(
                        typeName,
                        modifier = Modifier
                            .width(100.dp)
                            .background(labelBackgroundColor)
                            .padding(4.dp),
                        color = labelTextColor,
                        fontSize = 14.sp,
                        maxLines = 2
                    )
                }

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
                            tint = Color.Black
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

                    val priceBackgroundColor = if (typeTarification == TypeTarificationEnumT2.DEFINI) {
                        Color.Yellow
                    } else {
                        couleurButton
                    }

                    val priceTextColor = if (typeTarification == TypeTarificationEnumT2.DEFINI) {
                        Color.Black
                    } else {
                        Color.White
                    }

                    Column {
                        Text(
                            "$prixCurrency$pls",
                            modifier = Modifier
                                .background(priceBackgroundColor)
                                .padding(4.dp),
                            color = priceTextColor
                        )

                            val unitPrice = latestTariffLocalData.prixCurrency / nombreUnite
                            Text(
                                "س.و: ${String.format("%.2f", unitPrice)}",
                                modifier = Modifier
                                    .background(priceBackgroundColor.copy(alpha = 0.6f))
                                    .padding(2.dp),
                                color = priceTextColor,
                                fontSize = 10.sp
                            )
                    }

                }
            }
        }

        val buttonBackgroundColor = if (typeTarification == TypeTarificationEnumT2.DEFINI) {
            Color.Yellow
        } else {
            couleurButton
        }

        FloatingActionButton(
            onClick = {
                onClickPrixButton(typeTarification, latestTariffLocalData, context)
            },
            modifier = Modifier.size(40.dp),
            containerColor = buttonBackgroundColor
        ) {
            typeTarification.iconVector?.let { iconVector ->
                val iconColor = if (typeTarification == TypeTarificationEnumT2.DEFINI) {
                    Color.Black
                } else {
                    Color.White
                }

                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconColor
                )
            }
        }
    }
}
