package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainList(
    tariffs: List<D_TarificationInfosT2>,
    showLabels: Boolean,
    modifier: Modifier = Modifier
) {
    // Group tariffs by their type
    val tariffsGroupedByType = remember(tariffs) {
        tariffs.groupBy { it.typeTarificationEnumT2Correspond }
    }

    Column(modifier = modifier) {
        // Process each tarification type individually
        tariffsGroupedByType.forEach { (type, typeTariffs) ->
            TariffButtonItem(
                typeTarification = type,
                tariffs = typeTariffs,
                showLabels = showLabels
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
