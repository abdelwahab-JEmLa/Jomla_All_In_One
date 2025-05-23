package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
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
    showLabels: Boolean,
    tariffsGroupedByType: SortedMap<TypeTarificationEnumT2, List<D_TarificationInfos>>
) {            //<--
//TODO(1): enleve commentaires
    val color = Color(0xFF4CAF50)

    // Dynamic height calculation based on actual layout structure
    val gerantButtonHeight = remember(tariffsGroupedByType) {
        // Constants from the actual layout structure in MainList.kt and TariffButtonItem.kt
        val fabButtonSize = 40 // FloatingActionButton size from TariffButtonItem
        val spacerBetweenItems = 4 // Spacer height between items in MainList

        // Calculate total height based on actual number of tariff types
        val numberOfTariffTypes = tariffsGroupedByType.size

        // Each tariff item contributes: FAB button + spacer (except last item)
        val totalItemsHeight = numberOfTariffTypes * fabButtonSize
        val totalSpacersHeight = if (numberOfTariffTypes > 1) {
            (numberOfTariffTypes - 1) * spacerBetweenItems
        } else 0

        // Add some extra padding for visual balance and container margins
        val extraPadding = 16

        val calculatedHeight = totalItemsHeight + totalSpacersHeight + extraPadding

        // Ensure minimum height for visual consistency
        val minHeight = 60
        maxOf(calculatedHeight, minHeight).dp
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
    }
}
