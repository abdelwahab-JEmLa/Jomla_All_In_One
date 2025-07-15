package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
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
    modifier: Modifier = Modifier,
    relative_M1Produit: ArticlesBasesStatsTable,
    viewModel: TariffsButtonsViewModelSec7ID2,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    list_M13TarificationInfos: List<M13TarificationInfos> = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue,
    context: Context = LocalContext.current,
    showLabels: Boolean,
    maxPrixArriveDuProduit: Double?,
    clientDefiniTariffs: List<M13TarificationInfos>,
    onClickPrixButton: (TypeChoisi, M13TarificationInfos, Context) -> Unit,
    onClickAnulationButton: (() -> Unit)? = null
) {
    val relative_M2Client =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M2Client
    val currentM9AppCompt =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentM9AppCompt

    val travailleChezGrossisst3Ali = currentM9AppCompt?.travailleChezGrossisst3Ali

    val last_list_M13TarificationInfos = list_M13TarificationInfos.lastOrNull {
        it.parent_M2Client_KeyId == relative_M2Client?.keyID
    }

    val relative_Tariff_Historique =
        last_list_M13TarificationInfos?.let {
            M13TarificationInfos.get_default().copy(
                prixCurrency = it.prixCurrency,
                typeChoisi = TypeChoisi.Historique,
                parent_M1Produit_KeyId = relative_M1Produit.keyID,
                parent_M1Produit_DebugInfos = relative_M1Produit.getDebugInfos(),
                parent_M2Client_KeyId = relative_M2Client?.keyID ?: "null",
                parent_M2Client_DebugInfos = relative_M2Client?.get_DebugInfos() ?: "null",
            )
        }

    val standardTariffs = remember(
        relative_M1Produit,
        maxPrixArriveDuProduit,
        clientDefiniTariffs,
        travailleChezGrossisst3Ali
    ) {
        buildList {

            if (relative_Tariff_Historique != null) {
                add(relative_Tariff_Historique)
            }

            if (maxPrixArriveDuProduit != null && maxPrixArriveDuProduit != 0.0 && maxPrixArriveDuProduit != relative_M1Produit.prixVent) {
                add(
                    M13TarificationInfos(
                        typeChoisi = TypeChoisi.LeMaxPrixArrive,
                        prixCurrency = maxPrixArriveDuProduit,
                    )
                )
            }
            if (!travailleChezGrossisst3Ali!!) {
                add(
                    M13TarificationInfos(
                        typeChoisi = TypeChoisi.PRIX_BASE,
                        prixCurrency = relative_M1Produit.prixVent,
                    )
                )
            }
            if (relative_M1Produit.prixAchat != 0.0) {
                add(
                    M13TarificationInfos(
                        typeChoisi = TypeChoisi.Tariff_Achat_Depuit_Grossisst,
                        prixCurrency = relative_M1Produit.prixAchat,
                        parent_M1Produit_KeyId = relative_M1Produit.keyID,
                        parent_M1Produit_DebugInfos = relative_M1Produit.nom,
                    )
                )
            }

        }
    }

    val existingDefiniParGerant2Tariff = M13TarificationInfos.findTariff(
        list_M13TarificationInfos,
        relative_M1Produit,
        TypeChoisi.DefiniParGerant
    )

    val generatedTariffDefiniParGerant2 = remember(
        existingDefiniParGerant2Tariff,
        relative_M1Produit,
        list_M13TarificationInfos
    ) {
        existingDefiniParGerant2Tariff ?: M13TarificationInfos(
            parent_M1Produit_DebugInfos = relative_M1Produit.nom,
            parent_M1Produit_KeyId = relative_M1Produit.keyID,
            prixCurrency = relative_M1Produit.prixAchat,
            typeChoisi = TypeChoisi.DefiniParGerant
        )
    }

    val allTariffsGroupedAndSorted = remember(
        clientDefiniTariffs,
        standardTariffs,
        generatedTariffDefiniParGerant2
    ) {
        val shouldAddGeneratedTariff = !clientDefiniTariffs.any {
            it.typeChoisi == TypeChoisi.DefiniParGerant &&
                    it.parent_M1Produit_KeyId == relative_M1Produit.keyID
        }

        val allTariffs = if (shouldAddGeneratedTariff) {
            clientDefiniTariffs +
                    standardTariffs +
                    generatedTariffDefiniParGerant2
        } else {
            clientDefiniTariffs + standardTariffs
        }

        allTariffs
            .groupBy { it.typeChoisi }
            .toSortedMap(compareBy { it.ordinal })
    }


    Row(
        modifier = Modifier
            .getSemanticsTag(last_list_M13TarificationInfos, "")
            .getSemanticsTag(relative_M2Client?.keyID, "", 1)
            .getSemanticsTag(list_M13TarificationInfos, "", 2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = modifier
        ) {
            allTariffsGroupedAndSorted.forEach { (type, typeTariffs) ->
                TariffButtonItem(
                    produit = relative_M1Produit,
                    viewModel = viewModel,
                    typeTarification = type,
                    tariffs = typeTariffs,
                    showLabels = showLabels,
                    onClickPrixButton = onClickPrixButton,
                    context = context,
                    nombreUnite = relative_M1Produit.nombreUniteInt
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        val priceToUse = if (maxPrixArriveDuProduit != null && maxPrixArriveDuProduit != 0.0) {
            maxPrixArriveDuProduit
        } else {
            relative_M1Produit.prixVent
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
