package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp

@Composable
fun TariffButtonItem(
    typeTarification: TypeTarificationEnumT2,
    tariffs: List<D_TarificationInfosT2>,
    showLabels: Boolean
) {
    val latestTariff = tariffs.maxByOrNull { it.id }
    if (latestTariff == null) return

    val typeTarificationCompos by remember { mutableStateOf(typeTarification) }
    val latestTariffCompos by remember { mutableStateOf(latestTariff) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val context = LocalContext.current
        val couleurButton = typeTarificationCompos.couleur


        if (showLabels) {
            val typeName = typeTarificationCompos.nomArabe

            val prixCurrency = "${latestTariff.prixCurrency} "
            Row {
                ElevatedCard {
                    Text(
                        typeName,
                        modifier = Modifier
                            .background(couleurButton)
                            .padding(4.dp),
                        color = Color.White
                    )
                }
                if (typeTarificationCompos!=TypeTarificationEnumT2.AU_GERANT ) {
                    ElevatedCard {
                        Text(
                            prixCurrency,
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
            onClick = {
                val typeName = typeTarificationCompos.name
                val message = "$typeName: ${latestTariff.prixCurrency}"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(40.dp),
            containerColor = couleurButton
        ) {
            typeTarificationCompos.iconVector?.let { iconVector ->
                Icon(
                    imageVector = iconVector,
                    contentDescription = null
                )
            }
        }
    }
}
