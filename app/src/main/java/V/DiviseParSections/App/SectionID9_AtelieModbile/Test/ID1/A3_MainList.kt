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
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit,
    filteredProduit: _2_1_ProduitsDataBase,
    produitHistoriquesTariffs: List<D_TarificationInfos>,
    produitTariffs: List<D_TarificationInfos>,
    clientProduitTariffs: List<D_TarificationInfos>,
    clientLastHistoricalPrice: Double
) {
    val context = LocalContext.current

    val maxHistoricalPrice = remember(produitHistoriquesTariffs) {
        produitHistoriquesTariffs.maxOfOrNull { it.prixCurrency } ?: 0.0
    }

    val standardTariffs = remember(
        filteredProduit,
        produitTariffs,
        maxHistoricalPrice,
        clientLastHistoricalPrice,
        clientProduitTariffs
    ) {
        buildList {
            // Prix de base du produit
            add(
                D_TarificationInfos(
                    typeTarificationEnumT2Correspond = TypeTarificationEnumT2.PRIX_BASE,
                    prixCurrency = filteredProduit.monPrixVent,
                )
            )

            // Ajout du prix historique seulement si différent de 0
            if (clientLastHistoricalPrice != 0.0) {
                add(
                    D_TarificationInfos(
                        typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
                        prixCurrency = clientLastHistoricalPrice,
                    )
                )
            }

            // Ajout du prix max si nécessaire
            if (clientProduitTariffs.isNotEmpty() && maxHistoricalPrice != 0.0) {
                val clientMaxPrice = clientProduitTariffs.maxOfOrNull { it.prixCurrency } ?: 0.0
                if (maxHistoricalPrice > clientMaxPrice) {
                    add(
                        D_TarificationInfos(
                            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.LeMaxPrixArrive,
                            prixCurrency = maxHistoricalPrice,
                        )
                    )
                }
            }
        }
    }

    val allTariffsGroupedAndSorted = remember(clientProduitTariffs, standardTariffs) {
        (clientProduitTariffs + standardTariffs)
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
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        GerantButton(
            showLabels = showLabels,
            tariffsGroupedByType = allTariffsGroupedAndSorted,
            onClickPrixButton = {
                onClickPrixButton(
                    TypeTarificationEnumT2.LeMaxPrixArrive,
                    D_TarificationInfos(
                        typeTarificationEnumT2Correspond = TypeTarificationEnumT2.LeMaxPrixArrive,
                        prixCurrency = maxHistoricalPrice,
                    ),
                    context
                )
            }
        )
    }
}
