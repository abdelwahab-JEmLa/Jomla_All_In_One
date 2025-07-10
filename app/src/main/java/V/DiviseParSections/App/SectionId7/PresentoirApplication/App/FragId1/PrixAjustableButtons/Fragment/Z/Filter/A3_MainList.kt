package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
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
    onClickPrixButton: (TypeChoisi, M13TarificationInfos, Context) -> Unit,
    onClickAnulationButton: (() -> Unit)? = null
) {
    val currentM9AppCompt =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentM9AppCompt
    val travailleChezGrossisst3Ali = currentM9AppCompt?.travailleChezGrossisst3Ali

    val tariffs = viewModel.aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
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
                        typeChoisi = TypeChoisi.LeMaxPrixArrive,
                        prixCurrency = maxPrixArriveDuProduit,
                    )
                )
            }

            if (clientLastHistoricalPrice != 0.0 &&
                clientLastHistoricalPrice != produit.prixVent
            ) {

                add(
                    M13TarificationInfos(
                        typeChoisi = TypeChoisi.Historique,
                        prixCurrency = clientLastHistoricalPrice,
                    )
                )
            }

            if (!travailleChezGrossisst3Ali!!) {
                add(
                    M13TarificationInfos(
                        typeChoisi = TypeChoisi.PRIX_BASE,
                        prixCurrency = produit.prixVent,
                    )
                )
            }
        }
    }

    val existingDefiniParGerant2Tariff = M13TarificationInfos.findTariff(
        tariffs,
        produit,
        TypeChoisi.DefiniParGerant2
    )

    val generatedTariffDefiniParGerant2 = remember(
        existingDefiniParGerant2Tariff,
        produit,
        tariffs
    ) {
        existingDefiniParGerant2Tariff ?: M13TarificationInfos(
            id = if (tariffs.isNotEmpty()) tariffs.maxOf { it.id } + 1 else 1L,
            parentM1ProduitDebugInfos = produit.nom,
            parentM1ProduitInfosKeyId = produit.keyID,
            prixCurrency = produit.prixAchat,
            typeChoisi = TypeChoisi.DefiniParGerant2
        )
    }

    val allTariffsGroupedAndSorted = remember(
        clientDefiniTariffs,
        standardTariffs,
        generatedTariffDefiniParGerant2
    ) {
        val shouldAddGeneratedTariff = !clientDefiniTariffs.any {
            it.typeChoisi == TypeChoisi.DefiniParGerant2 &&
                    it.parentM1ProduitInfosKeyId == produit.keyID
        }

        val allTariffs = if (shouldAddGeneratedTariff) {
            clientDefiniTariffs + standardTariffs + generatedTariffDefiniParGerant2
        } else {
            clientDefiniTariffs + standardTariffs
        }

        allTariffs
            .groupBy { it.typeChoisi }
            .toSortedMap(compareBy { it.ordinal })
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = modifier
                .getSemanticsTag(currentM9AppCompt?.nom, "currentM9AppCompt")
        ) {
            allTariffsGroupedAndSorted.forEach { (type, typeTariffs) ->
                TariffButtonItem(
                    produit=produit,
                    viewModel=viewModel,
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
            TypeChoisi.LeMaxPrixArrive
        } else {
            TypeChoisi.PRIX_BASE
        }

        val tarificationInfo = M13TarificationInfos(
            typeChoisi = typeToUse,
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
