package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainList(
    tariffs: List<D_TarificationInfos>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    onClickPrixButton: () -> (TypeTarificationEnumT2, D_TarificationInfos, Context) -> () -> Unit,
    filteredProduit: _2_1_ProduitsDataBase,
) {
    val tariffsGroupedByType = remember(tariffs) {
        tariffs.groupBy { it.typeTarificationEnumT2Correspond }
            .toSortedMap(compareBy { it.ordinal })
    }

    val gerantButtonEntry = tariffsGroupedByType.entries.find {
        it.key == TypeTarificationEnumT2.AU_GERANT
    }

    val gerantButtonTariffs = gerantButtonEntry?.value ?: emptyList()

    val autres = tariffsGroupedByType.filter {
        it.key != TypeTarificationEnumT2.AU_GERANT
    }

    val finalGerantTariffs = gerantButtonTariffs.ifEmpty {
        listOf(
            D_TarificationInfos(
                idParentProduit = filteredProduit.vid,
                idParentBonAchat = tariffs.firstOrNull()?.idParentBonAchat ?: 0L,
                typeTarificationEnumT2Correspond = TypeTarificationEnumT2.AU_GERANT,
                prixCurrency = filteredProduit.monPrixVent,
                timestamps = System.currentTimeMillis()
            )
        )
    }

    val finalGerantType = TypeTarificationEnumT2.AU_GERANT

    // Calculate dynamic height based on the count of elements in each group
    // Each element contributes 20dp to the height
    val maxElementsInGroup = if (autres.isNotEmpty()) {
        autres.maxOf { it.value.size }
    } else {
        1 // Minimum height for at least one element
    }
    val gerantButtonHeight = remember(autres) {
        // Base height + (number of elements * 20dp)
        // Adding some base height (40dp) for padding and minimum size
        val calculatedHeight = 5 + (tariffsGroupedByType.size * 40)
        calculatedHeight.dp
    }

    Text("${tariffsGroupedByType.size}")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = modifier) {
            // Display other tariff types (excluding AU_GERANT)
            autres.forEach { (type, typeTariffs) ->
                TariffButtonItem(
                    typeTarification = type,
                    tariffs = typeTariffs,
                    showLabels = showLabels,
                    onClickPrixButton = onClickPrixButton(),
                    gerantButtonHeight = gerantButtonHeight
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Always display AU_GERANT button (either existing or created)
        TariffButtonItem(
            typeTarification = finalGerantType,
            tariffs = finalGerantTariffs,
            showLabels = showLabels,
            onClickPrixButton = onClickPrixButton(),
            gerantButtonHeight = gerantButtonHeight
        )
    }
}
