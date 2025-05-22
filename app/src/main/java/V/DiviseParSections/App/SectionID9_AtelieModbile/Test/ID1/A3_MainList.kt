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
import androidx.compose.ui.platform.LocalContext
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

    val context = LocalContext.current

    val PRIX_BASETariffe =
        listOf(
            D_TarificationInfos(
                idParentProduit = filteredProduit.vid,
                idParentBonAchat = tariffs.firstOrNull()?.idParentBonAchat ?: 0L,
                typeTarificationEnumT2Correspond = TypeTarificationEnumT2.PRIX_BASE,
                prixCurrency = filteredProduit.monPrixVent,
                timestamps = System.currentTimeMillis()
            )
        )

    Text("${filteredProduit.monPrixVent}")

    val gerantButtonHeight = remember(tariffsGroupedByType) {
        val calculatedHeight = 5 + (tariffsGroupedByType.size * 40)
        calculatedHeight.dp
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = modifier) {
            tariffsGroupedByType.forEach { (type, typeTariffs) ->
                TariffButtonItem(
                    typeTarification = type,
                    tariffs = typeTariffs,
                    showLabels = showLabels,
                    onClickPrixButton = onClickPrixButton(),
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            TariffButtonItem(
                typeTarification = TypeTarificationEnumT2.PRIX_BASE,
                tariffs = PRIX_BASETariffe,
                showLabels = showLabels,
                onClickPrixButton = onClickPrixButton(),
            )
        }

        GerantButton(
            latestTariffLocalData = PRIX_BASETariffe.first(),
            showLabels = showLabels,
            gerantButtonHeight = gerantButtonHeight,
            onClickPrixButton = onClickPrixButton(),
            context = context
        )
    }
}
