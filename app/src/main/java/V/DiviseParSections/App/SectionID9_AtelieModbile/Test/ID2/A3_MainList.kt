package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID2

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainList(
    tariffs: List<D_TarificationInfosT2>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    onClickPrixButton: () -> (TypeTarificationEnumT2, D_TarificationInfosT2, Context) -> () -> Unit,
) {
    val tariffsGroupedByType = remember(tariffs) {
        tariffs.groupBy { it.typeTarificationEnumT2Correspond }
            .toSortedMap(compareBy { it.ordinal })
    }

    // Fix: Using Map.entries.find() instead of Map.find()
    val gerantButtonEntry = tariffsGroupedByType.entries.find {
        it.key == TypeTarificationEnumT2.AU_GERANT
    }

    // Safely accessing value from the entry
    val gerantButtonTariffs = gerantButtonEntry?.value ?: emptyList()
    val gerantButtonType = gerantButtonEntry?.key

    val autres = tariffsGroupedByType.filter {
        it.key != TypeTarificationEnumT2.AU_GERANT
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = modifier) {
            autres.forEach { (type, typeTariffs) ->
                TariffButtonItem(
                    typeTarification = type,
                    tariffs = typeTariffs,
                    showLabels = showLabels,
                    onClickPrixButton = onClickPrixButton()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Only render gerant button if the type is found
        if (gerantButtonType != null) {
            TariffButtonItem(
                typeTarification = gerantButtonType,
                tariffs = gerantButtonTariffs,
                showLabels = showLabels,
                onClickPrixButton = onClickPrixButton()
            )
        }
    }
}
