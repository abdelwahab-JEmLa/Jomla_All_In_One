package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    relative_M1Produit: ArticlesBasesStatsTable,
    viewModel: TariffsButtonsViewModelSec7ID2,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    list_M13TarificationInfos: List<M13TarificationInfos> = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue,
    context: Context = LocalContext.current,
    showLabels: Boolean,
    clientDefiniTariffs: List<M13TarificationInfos>,
    onClickPrixButton: (TypeChoisi, M13TarificationInfos, Context) -> Unit,
    onClickAnulationButton: (() -> Unit)? = null
) {
    val relative_M2Client =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M2Client
    val currentM9AppCompt =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt

    val travailleChezGrossisst3Ali = currentM9AppCompt?.travailleChezGrossisst3Ali

    val max_Prix = list_M13TarificationInfos
        .filter {
            it.parent_M1Produit_KeyId == relative_M1Produit.keyID
                    && it.typeChoisi != TypeChoisi.Prix_SupperGro_Et_PresentationService

        }
        .maxOfOrNull { it.prixCurrency } ?: 0.0

    val last_list_M13TarificationInfos = list_M13TarificationInfos
        .lastOrNull {
            it.parent_M1Produit_KeyId == relative_M1Produit.keyID &&
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

    val existingDefiniParGerant2Tariff = list_M13TarificationInfos
        .lastOrNull { tariff ->
            tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                    tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
        }

    val relative_Tariff_Prix_Detaille =
        M13TarificationInfos.get_default().copy(
            typeChoisi = TypeChoisi.Prix_Detaille,
            parent_M1Produit_DebugInfos = relative_M1Produit.nom,
            parent_M1Produit_KeyId = relative_M1Produit.keyID,
            prixCurrency = existingDefiniParGerant2Tariff?.prixCurrency
                ?: relative_Tariff_Historique?.prixCurrency ?: relative_M1Produit.prixVent
        )

    val relative_Tariff_Edited_Pour_Client =
        M13TarificationInfos.get_default().copy(
            typeChoisi = TypeChoisi.Edited_Pour_Client,
            parent_M1Produit_DebugInfos = relative_M1Produit.nom,
            parent_M1Produit_KeyId = relative_M1Produit.keyID,
            prixCurrency = relative_Tariff_Historique?.prixCurrency ?: relative_M1Produit.prixVent
        )

    val standardTariffs = remember(
        relative_M1Produit,
        max_Prix,
        clientDefiniTariffs,
        travailleChezGrossisst3Ali,
        repositorysMainGetter.repo9AppCompt.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase }
    ) {
        buildList {
            add(relative_Tariff_Prix_Detaille)

            if (relative_Tariff_Historique == null || focusedValuesGetter.currentApp_Est_Admin) {
                add(relative_Tariff_Edited_Pour_Client)
            }

            if (relative_Tariff_Historique != null) {
                add(relative_Tariff_Historique)
            }

            if (
                max_Prix != 0.0
                && max_Prix > relative_M1Produit.prixVent
                && max_Prix > relative_Tariff_Prix_Detaille.prixCurrency
            ) {
                add(
                    M13TarificationInfos(
                        typeChoisi = TypeChoisi.LeMaxPrixArrive,
                        prixCurrency = max_Prix,
                    )
                )
            }

            add(
                M13TarificationInfos(
                    typeChoisi = TypeChoisi.Prix_SupperGro_Et_PresentationService,
                    prixCurrency = relative_M1Produit.prixVent,
                )
            )

            if (relative_M1Produit.prixAchat != 0.0 || focusedValuesGetter.currentApp_Est_Admin) {
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

    val allTariffsGroupedAndSorted = remember(
        clientDefiniTariffs,
        standardTariffs,
    ) {
        val allTariffs = clientDefiniTariffs + standardTariffs

        allTariffs
            .groupBy { it.typeChoisi }
            .toSortedMap(compareBy { it.ordinal })
    }

    Row(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(SemanticsPropertyKey("allTariffsGroupedAndSorted"), allTariffsGroupedAndSorted)
            }
            .getSemanticsTag(last_list_M13TarificationInfos, "")
            .getSemanticsTag(relative_M2Client?.keyID, "", 1)
            .getSemanticsTag(list_M13TarificationInfos, "", 2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = modifier
                .semantics(mergeDescendants = true) {
                    set(value = existingDefiniParGerant2Tariff, key = SemanticsPropertyKey(""))
                }
                .semantics(mergeDescendants = true) {
                    set(value = list_M13TarificationInfos.filter { tariff ->
                        tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                                tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
                    }, key = SemanticsPropertyKey("filter"))
                }
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
                    nombreUnite = relative_M1Produit.nombreUniteInt,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        val priceToUse = if (max_Prix != 0.0) {
            max_Prix
        } else {
            relative_M1Produit.prixVent
        }

        val typeToUse = if (max_Prix != 0.0) {
            TypeChoisi.LeMaxPrixArrive
        } else {
            TypeChoisi.Prix_SupperGro_Et_PresentationService
        }

        val tarificationInfo = M13TarificationInfos(
            typeChoisi = typeToUse,
            prixCurrency = priceToUse,
        )

        GerantButton(
            relative_M1Produit = relative_M1Produit,
            relative_Tariff = standardTariffs.find { it.typeChoisi == TypeChoisi.LeMaxPrixArrive },
            viewModel = viewModel,
            showLabels = showLabels,
            tariffsGroupedByType = allTariffsGroupedAndSorted,
            onClickPrixButton = {
                onClickPrixButton(typeToUse, tarificationInfo, context)
            },
            onClickAnulationButton = onClickAnulationButton
        )
    }
}
