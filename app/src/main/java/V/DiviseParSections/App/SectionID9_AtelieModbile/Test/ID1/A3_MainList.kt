package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun MainList(
    produitTariffs: List<D_TarificationInfos>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    onClickPrixButton: () -> (TypeTarificationEnumT2, D_TarificationInfos, Context) -> () -> Unit,
    filteredProduit: _2_1_ProduitsDataBase,
) {
    val context = LocalContext.current

    val hasHistoricalTariffs = remember(produitTariffs) {
        produitTariffs.any { it.typeTarificationEnumT2Correspond == TypeTarificationEnumT2.Historique }
    }

    val maxHistoricalPrice = remember(produitTariffs) {
        produitTariffs
            .filter { it.typeTarificationEnumT2Correspond == TypeTarificationEnumT2.Historique }
            .maxOfOrNull { it.prixCurrency } ?: 0.0
    }

    val standardTariffs = remember(filteredProduit, produitTariffs, hasHistoricalTariffs, maxHistoricalPrice) {
        buildList {
            add(
                D_TarificationInfos(
                    idParentProduit = filteredProduit.vid,
                    parentIdClient = produitTariffs.firstOrNull()?.parentIdClient ?: 0L,
                    typeTarificationEnumT2Correspond = TypeTarificationEnumT2.PRIX_BASE,
                    prixCurrency = filteredProduit.monPrixVent,
                )
            )

            if (hasHistoricalTariffs && maxHistoricalPrice > 0.0) {
                add(
                    D_TarificationInfos(
                        idParentProduit = filteredProduit.vid,
                        parentIdClient = produitTariffs.firstOrNull()?.parentIdClient ?: 0L,
                        typeTarificationEnumT2Correspond = TypeTarificationEnumT2.LeMaxPrixArrive,
                        prixCurrency = maxHistoricalPrice,
                    )
                )
            }
        }
    }

    val allTariffsGroupedAndSorted = remember(produitTariffs, standardTariffs) {
        val combinedTariffs = produitTariffs + standardTariffs

        combinedTariffs
            .groupBy { it.typeTarificationEnumT2Correspond }
            .toSortedMap(compareBy { it.ordinal })
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = modifier) {
            allTariffsGroupedAndSorted.forEach { (type, typeTariffs) ->
                TariffButtonItem(
                    typeTarification = type,
                    tariffs = typeTariffs,
                    showLabels = showLabels,
                    onClickPrixButton = onClickPrixButton(),
                    context = context,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        GerantButton(
            showLabels = showLabels,
            tariffsGroupedByType = allTariffsGroupedAndSorted
        )
    }
}
