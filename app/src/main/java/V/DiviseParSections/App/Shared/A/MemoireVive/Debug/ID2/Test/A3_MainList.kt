package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeTarificationEnumT2
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
    viewModel: TariffsButtonsViewModelSec7ID2,
    produit: ArticlesBasesStatsTable,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    clientLastHistoricalPrice: Double,
    maxPrixArriveDuProduit: Double?,
    clientDefiniTariffs: List<M13TarificationInfos>,
    onClickPrixButton: (TypeTarificationEnumT2, M13TarificationInfos, Context) -> Unit,
    onClickAnulationButton: (() -> Unit)? = null
) {
    val currentM9AppCompt =
        viewModel.aCentralFacade.focusedVarsHandlerFacade.getter.currentM9AppCompt
    val travailleChezGrossisst3Ali = currentM9AppCompt?.travailleChezGrossisst3Ali

    val tariffs = viewModel.aCentralFacade.getter.repo13TarificationInfos.datasValue
    val context = LocalContext.current

    val standardTariffs = remember(
        produit,
        maxPrixArriveDuProduit,
        clientLastHistoricalPrice,
        clientDefiniTariffs,
        travailleChezGrossisst3Ali
    ) {
        buildList {
            if (maxPrixArriveDuProduit != null &&
                maxPrixArriveDuProduit != 0.0 &&
                maxPrixArriveDuProduit != produit.prixVent &&
                maxPrixArriveDuProduit > clientLastHistoricalPrice
            ) {

                add(
                    M13TarificationInfos(
                        typeTarificationEnumT2Correspond = TypeTarificationEnumT2.LeMaxPrixArrive,
                        prixCurrency = maxPrixArriveDuProduit,
                    )
                )
            }

            if (clientLastHistoricalPrice != 0.0 &&
                clientLastHistoricalPrice != produit.prixVent
            ) {

                add(
                    M13TarificationInfos(
                        typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
                        prixCurrency = clientLastHistoricalPrice,
                    )
                )
            }

            // Only add base price tariff if not working at wholesale (TravailleChezGrossisst3Ali)
            if (!travailleChezGrossisst3Ali!!) {
                add(
                    M13TarificationInfos(
                        typeTarificationEnumT2Correspond = TypeTarificationEnumT2.PRIX_BASE,
                        prixCurrency = produit.prixVent,
                    )
                )
            }
        }
    }

    val generatedTariffDefiniParGerant2 = M13TarificationInfos(
        id = if (tariffs.isNotEmpty()) tariffs.maxOf { it.id } + 1 else 1L,
        parentM1ProduitDebugInfos = produit.nom,
        parentM1ProduitInfosKeyId = produit.keyID,
        prixCurrency = produit.prixAchat
    )

    val allTariffsGroupedAndSorted = remember(clientDefiniTariffs, standardTariffs) {
        (clientDefiniTariffs + standardTariffs + generatedTariffDefiniParGerant2)
            .groupBy { it.typeTarificationEnumT2Correspond }
            .toSortedMap(compareBy { it.ordinal })
    }


    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = modifier
                .getSemanticsTag("currentM9AppCompt", currentM9AppCompt?.nom)
        ) {
            allTariffsGroupedAndSorted.forEach { (type, typeTariffs) ->
                TariffButtonItem(
                    typeTarification = type,
                    tariffs = typeTariffs,
                    showLabels = showLabels,
                    onClickPrixButton = onClickPrixButton,
                    context = context,
                    nombreUnite = produit.nombreUniteInt
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        val priceToUse = if (maxPrixArriveDuProduit != null && maxPrixArriveDuProduit != 0.0) {
            maxPrixArriveDuProduit
        } else {
            produit.prixVent
        }

        val typeToUse = if (maxPrixArriveDuProduit != null && maxPrixArriveDuProduit != 0.0) {
            TypeTarificationEnumT2.LeMaxPrixArrive
        } else {
            TypeTarificationEnumT2.PRIX_BASE
        }

        val tarificationInfo = M13TarificationInfos(
            typeTarificationEnumT2Correspond = typeToUse,
            prixCurrency = priceToUse,
        )

        GerantButton(
            viewModel = viewModel,
            tarificationInfo = tarificationInfo,
            showLabels = showLabels,
            tariffsGroupedByType = allTariffsGroupedAndSorted,
            onClickPrixButton = {

                onClickPrixButton(typeToUse, tarificationInfo, context)
            },
            onClickAnulationButton = onClickAnulationButton
        )
    }
}
