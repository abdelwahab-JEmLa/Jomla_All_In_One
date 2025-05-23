package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.SortedMap

@Composable
fun GerantButton(
    latestTariffLocalData: D_TarificationInfos,
    showLabels: Boolean,
    onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> () -> Unit,
    context: Context,
    tariffsGroupedByType: SortedMap<TypeTarificationEnumT2, List<D_TarificationInfos>>
) {
    val color = Color(0xFF4CAF50)

 //   Text("tariffsGroupedByType.size==${tariffsGroupedByType.size}")

    val gerantButtonHeight = remember(tariffsGroupedByType) {
        val calculatedHeight = ((tariffsGroupedByType.size + 1) * (40+5))
        calculatedHeight.dp
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Vertical text label for AU_GERANT
        if (showLabels) {
            ElevatedCard {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(color)
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                        .height(gerantButtonHeight)
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

        // Floating action button for AU_GERANT
        FloatingActionButton(
            onClick = onClickPrixButton(
                latestTariffLocalData.typeTarificationEnumT2Correspond
                , latestTariffLocalData
                , context),
            modifier = Modifier.size(40.dp),
            containerColor = color
        ) {
            Icons.Filled.Done.let { iconVector ->
                Icon(
                    imageVector = iconVector,
                    contentDescription = null
                )
            }
        }
    }
}
