package P0_MainScreen.Main.Main.Settings.Windows.g

import EntreApps.Shared.Models.M8BonVent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickStatsRowWithFilter(
    bons: List<M8BonVent>,
    selectedFilter: M8BonVent.EtateActuellementEst?,
    onFilterSelected: (M8BonVent.EtateActuellementEst) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusCounts = bons.groupBy { it.etateActuellementEst }.mapValues { it.value.size }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        M8BonVent.EtateActuellementEst.values().forEach { status ->
            val count = statusCounts[status] ?: 0
            if (count > 0) {
                StatusChipClickable(
                    status = status,
                    count = count,
                    isSelected = selectedFilter == status,
                    onClick = { onFilterSelected(status) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
