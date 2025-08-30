package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ItsLancedDepuit
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.PrixAchatHandler
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.f.PrixsVents_Handler
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.TariffButtonItem
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
    onClickAnulationButton: (() -> Unit)? = null,
    itsLancedDepuitComposeParent: ItsLancedDepuit?
) {
    val relative_M2Client =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M2Client
    val currentM9AppCompt =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt

    val itsLancedDepuit_EditeBaseDonne =
        itsLancedDepuitComposeParent is ItsLancedDepuit.EditeBaseDonne
    val travailleChezGrossisst3Ali = currentM9AppCompt?.travailleChezGrossisst3Ali

    fun getOrSet_TariffPrix_SupperGro_Et_PresentationService(): M13TarificationInfos {
        return list_M13TarificationInfos
            .lastOrNull {
                (it.parent_M1Produit_KeyId == relative_M1Produit.keyID
                        && it.typeChoisi == TypeChoisi.Prix_SupperGro_Et_PresentationService
                        && it.parent_M14VentPeriod_KeyId == (focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
                    ?: ""))
            } ?: M13TarificationInfos(
            parent_M14VentPeriod_KeyId = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
                ?: "",
            typeChoisi = TypeChoisi.Prix_SupperGro_Et_PresentationService,
            prixCurrency = relative_M1Produit.prixVent,
            creationTimestamps = System.currentTimeMillis()
        )
    }

    val tariffPrix_SupperGro_Et_PresentationService by remember(list_M13TarificationInfos.map { it.dernierTimeTampsSynchronisationAvecFireBase }) {
        derivedStateOf {
            getOrSet_TariffPrix_SupperGro_Et_PresentationService()
        }
    }

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

    val existing_Prix_Detaille = find_existing_Prix_Detaille(
        aCentralFacade,
        relative_M1Produit,
    )

    val relative_Tariff_Prix_Detaille =
        M13TarificationInfos.get_default().copy(
            typeChoisi = TypeChoisi.Prix_Detaille,
            parent_M1Produit_DebugInfos = relative_M1Produit.nom,
            parent_M1Produit_KeyId = relative_M1Produit.keyID,
            prixCurrency = existing_Prix_Detaille?.prixCurrency
                ?: relative_Tariff_Historique?.prixCurrency ?: relative_M1Produit.prixVent
        )

    val relative_Tariff_Prix_Progressive = createProgressiveTariff(
        relative_M1Produit = relative_M1Produit,
        relative_Tariff_Prix_Detaille = relative_Tariff_Prix_Detaille
    )

    val standardTariffs = remember(
        relative_M1Produit,
        max_Prix,
        clientDefiniTariffs,
        travailleChezGrossisst3Ali,
        repositorysMainGetter.repo9AppCompt.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        list_M13TarificationInfos.map { it.dernierTimeTampsSynchronisationAvecFireBase }
    ) {
        buildList {
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

            add(
                tariffPrix_SupperGro_Et_PresentationService
            )

            add(relative_Tariff_Prix_Progressive)

            if (relative_M1Produit.prixVent != relative_Tariff_Prix_Detaille.prixCurrency) {
                add(relative_Tariff_Prix_Detaille)
            }

            if (relative_Tariff_Historique != null && !itsLancedDepuit_EditeBaseDonne) {
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
                    set(value = existing_Prix_Detaille, key = SemanticsPropertyKey(""))
                }
                .semantics(mergeDescendants = true) {
                    set(value = list_M13TarificationInfos.filter { tariff ->
                        tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                                tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
                    }, key = SemanticsPropertyKey("filter"))
                }
        ) {
            allTariffsGroupedAndSorted.forEach { (centralType, relativeList_Tariff) ->
                when (centralType) {
                    TypeChoisi.Tariff_Achat_Depuit_Grossisst -> {
                        val relative_Tariff =
                            relativeList_Tariff.maxByOrNull { it.creationTimestamps }

                        if (relative_Tariff != null) {
                            PrixAchatHandler(
                                relative_Produit = relative_M1Produit,
                                relative_Tariff = relative_Tariff,
                                showLabels = showLabels,
                                nombreUnite = relative_M1Produit.nombreUniteInt,
                            )
                        }
                    }

                    TypeChoisi.Prix_SupperGro_Et_PresentationService,
                    TypeChoisi.Prix_Detaille -> {
                        val relative_Tariff =
                            relativeList_Tariff.maxByOrNull { it.creationTimestamps }

                        if (relative_Tariff != null) {
                            PrixsVents_Handler(
                                allTariffsGroupedAndSorted=allTariffsGroupedAndSorted,
                                relative_Produit = relative_M1Produit,
                                relative_Tariff = relative_Tariff,
                            )
                        }
                    }

                    else -> TariffButtonItem(
                        produit = relative_M1Produit,
                        viewModel = viewModel,
                        typeTarification = centralType,
                        tariffs = relativeList_Tariff,
                        showLabels = showLabels,
                        onClickPrixButton = onClickPrixButton,
                        context = context,
                        nombreUnite = relative_M1Produit.nombreUniteInt,
                    )
                }
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

        itsLancedDepuit_EditeBaseDonne.ifFalse {
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
}

fun find_existing_Prix_Detaille(
    aCentralFacade: ACentralFacade,
    relative_M1Produit: ArticlesBasesStatsTable,
) = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
    .lastOrNull { tariff ->
        tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
    }

private fun createProgressiveTariff(
    relative_M1Produit: ArticlesBasesStatsTable,
    relative_Tariff_Prix_Detaille: M13TarificationInfos
): M13TarificationInfos {
    return M13TarificationInfos.get_default().copy(
        typeChoisi = TypeChoisi.Edited_Pour_Client,
        parent_M1Produit_DebugInfos = relative_M1Produit.nom,
        parent_M1Produit_KeyId = relative_M1Produit.keyID,
        prixCurrency = calculateProgressivePrice(
            prixDetaille = relative_Tariff_Prix_Detaille.prixCurrency,
            prixVent = relative_M1Produit.prixVent,
            pourcentageProgressive = relative_M1Produit.pourcentage_Prix_Progressive
        )
    )
}

private fun calculateProgressivePrice(
    prixDetaille: Double,
    prixVent: Double,
    pourcentageProgressive: Int
): Double {
    val priceDifference = prixDetaille - prixVent

    val pourcentageProgressive1 = if (pourcentageProgressive == 50) 60 else pourcentageProgressive

    val progressiveAdjustment = priceDifference * (pourcentageProgressive1 / 100.0)

    return prixVent + progressiveAdjustment
}
