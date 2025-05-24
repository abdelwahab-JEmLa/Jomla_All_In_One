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
    filteredProduit: _2_1_ProduitsDataBase,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit,
    clientLastHistoricalPrice: Double,
    maxPrixArriveDuProduit: Double?,
    clientDefiniTariffs: List<D_TarificationInfos>,
    onClickAnulationButton: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val standardTariffs = remember(
        filteredProduit,
        maxPrixArriveDuProduit,
        clientLastHistoricalPrice,
        clientDefiniTariffs
    ) {
        buildList {
            if (maxPrixArriveDuProduit != null &&
                maxPrixArriveDuProduit != 0.0 &&
                maxPrixArriveDuProduit != filteredProduit.monPrixVent &&
                maxPrixArriveDuProduit > clientLastHistoricalPrice) {

                add(
                    D_TarificationInfos(
                        typeTarificationEnumT2Correspond = TypeTarificationEnumT2.LeMaxPrixArrive,
                        prixCurrency = maxPrixArriveDuProduit,
                    )
                )
            }

            if (clientLastHistoricalPrice != 0.0 &&
                clientLastHistoricalPrice != filteredProduit.monPrixVent) {

                add(
                    D_TarificationInfos(
                        typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
                        prixCurrency = clientLastHistoricalPrice,
                    )
                )
            }

            add(
                D_TarificationInfos(
                    typeTarificationEnumT2Correspond = TypeTarificationEnumT2.PRIX_BASE,
                    prixCurrency = filteredProduit.monPrixVent,
                )
            )
        }
    }

    val allTariffsGroupedAndSorted = remember(clientDefiniTariffs, standardTariffs) {
        (clientDefiniTariffs + standardTariffs)
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
                    onClickPrixButton = onClickPrixButton,
                    context = context,
                    nombreUnite = 10
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        GerantButton(
            showLabels = showLabels,
            tariffsGroupedByType = allTariffsGroupedAndSorted,
            onClickPrixButton = {
                // Fixed: Use maxPrixArriveDuProduit if available and valid, otherwise use base price
                val priceToUse = if (maxPrixArriveDuProduit != null && maxPrixArriveDuProduit != 0.0) {
                    maxPrixArriveDuProduit
                } else {
                    filteredProduit.monPrixVent
                }

                val typeToUse = if (maxPrixArriveDuProduit != null && maxPrixArriveDuProduit != 0.0) {
                    TypeTarificationEnumT2.LeMaxPrixArrive
                } else {
                    TypeTarificationEnumT2.PRIX_BASE
                }

                val tarificationInfo = D_TarificationInfos(
                    typeTarificationEnumT2Correspond = typeToUse,
                    prixCurrency = priceToUse,
                )

                onClickPrixButton(typeToUse, tarificationInfo, context)
            },
            onClickAnulationButton = onClickAnulationButton // Pass the cancellation callback
        )
    }
}
