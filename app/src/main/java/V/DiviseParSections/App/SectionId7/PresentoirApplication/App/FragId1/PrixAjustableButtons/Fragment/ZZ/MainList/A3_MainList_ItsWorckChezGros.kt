package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ItsLancedDepuit
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.GerantButton
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Items.Prixs_currentApp_ItsWorkChezGrossisst_Handler
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
fun A3_MainList_ItsWorckChezGros(
    modifier: Modifier = Modifier,
    relative_M1Produit: ArticlesBasesStatsTable,
    viewModel: TariffsButtonsViewModelSec7ID2,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    list_M13TarificationInfos: List<M13TarificationInfos> = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue,
    clientDefiniTariffs: List<M13TarificationInfos>,
    itsLancedDepuitComposeParent: ItsLancedDepuit?,
    showLabels: Boolean = true,
    onClickPrixButton: (TypeChoisi, M13TarificationInfos, Context) -> Unit = { _, _, _ -> },
    onClickAnulationButton: (() -> Unit)? = null,
    context: Context = LocalContext.current
) {
    val relative_M2Client =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M2Client

    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    val itsLancedDepuit_EditeBaseDonne =
        itsLancedDepuitComposeParent is ItsLancedDepuit.EditeBaseDonne

    // Create instance of tariff generator
    val tariffGenerator = remember { Genere_Tariffs_currentApp_ItsWorkChezGrossisst() }

    // Generate default grossist tariffs
    val tariff_Grossist_Achat by remember(list_M13TarificationInfos.map { it.dernierTimeTampsSynchronisationAvecFireBase }) {
        derivedStateOf {
            tariffGenerator.getOrCreate_Tariff_Grossist_Achat(
                aCentralFacade,
                relative_M1Produit,
                focusedValuesGetter
            )
        }
    }

    val tariff_Grossist_SuperGros by remember(list_M13TarificationInfos.map { it.dernierTimeTampsSynchronisationAvecFireBase }) {
        derivedStateOf {
            tariffGenerator.getOrCreate_Tariff_Grossist_SuperGros(
                aCentralFacade,
                relative_M1Produit,
                focusedValuesGetter
            )
        }
    }

    val tariff_Grossist_Progressive by remember(list_M13TarificationInfos.map { it.dernierTimeTampsSynchronisationAvecFireBase }) {
        derivedStateOf {
            tariffGenerator.getOrCreate_Tariff_Grossist_Progressive(
                aCentralFacade,
                relative_M1Produit,
                focusedValuesGetter
            )
        }
    }

    val tariff_Grossist_Gro by remember(list_M13TarificationInfos.map { it.dernierTimeTampsSynchronisationAvecFireBase }) {
        derivedStateOf {
            tariffGenerator.getOrCreate_Tariff_Grossist_Gro(
                aCentralFacade,
                relative_M1Produit,
                focusedValuesGetter
            )
        }
    }


    val last_Client_Tariff = list_M13TarificationInfos
        .filter {
            it.parent_M1Produit_KeyId == relative_M1Produit.keyID &&
                    it.parent_M2Client_KeyId == relative_M2Client?.keyID &&
                    it.typeChoisi in setOf(
                TypeChoisi.Tariff_ItsWorkInGrossist_Achat,
                TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                TypeChoisi.Tariff_ItsWorkInGrossist_Progressive,
                TypeChoisi.Tariff_ItsWorkInGrossist_Gro
            )
        }
        .maxByOrNull { it.creationTimestamps }

    val standardTariffs = remember(
        relative_M1Produit,
        clientDefiniTariffs,
        currentApp_ItsWorkChezGrossisst,
        repositorysMainGetter.repo9AppCompt.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        list_M13TarificationInfos.map { it.dernierTimeTampsSynchronisationAvecFireBase }
    ) {
        buildList {
            if (currentApp_ItsWorkChezGrossisst) {
                // Add purchase price if available or if admin
                if (relative_M1Produit.prixAchat != 0.0 || focusedValuesGetter.currentApp_Est_Admin) {
                    add(tariff_Grossist_Achat)
                }

                // Add grossist tariffs
                add(tariff_Grossist_SuperGros)
                add(tariff_Grossist_Progressive)
                add(tariff_Grossist_Gro)

                if (last_Client_Tariff != null) {
                        add(last_Client_Tariff)
                }
            }
        }
    }

    val allTariffsGroupedAndSorted = remember(
        clientDefiniTariffs,
        standardTariffs,
    ) {
        val allTariffs = if (currentApp_ItsWorkChezGrossisst) {
            // Filter out non-grossist tariffs when in grossist mode
            val filteredClientTariffs = clientDefiniTariffs.filter { tariff ->
                tariff.typeChoisi in setOf(
                    TypeChoisi.Tariff_ItsWorkInGrossist_Achat,
                    TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                    TypeChoisi.Tariff_ItsWorkInGrossist_Progressive,
                    TypeChoisi.Tariff_ItsWorkInGrossist_Gro,
                    TypeChoisi.LeMaxPrixArrive
                )
            }
            filteredClientTariffs + standardTariffs
        } else {
            clientDefiniTariffs + standardTariffs
        }

        allTariffs
            .groupBy { it.typeChoisi }
            .toSortedMap(compareBy { it.ordinal })
    }

    Column(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(SemanticsPropertyKey("allTariffsGroupedAndSorted"), allTariffsGroupedAndSorted)
            }
            .getSemanticsTag(last_Client_Tariff, "last_client_tariff")
            .getSemanticsTag(relative_M2Client?.keyID, "client_id", 1)
            .getSemanticsTag(list_M13TarificationInfos, "all_tariffs", 2)
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column {
                allTariffsGroupedAndSorted.forEach { (centralType, relativeList_Tariff) ->
                    val relative_Tariff =
                        relativeList_Tariff.maxByOrNull { it.creationTimestamps }

                    if (relative_Tariff != null) {
                        Prixs_currentApp_ItsWorkChezGrossisst_Handler(
                            allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                            relative_Produit = relative_M1Produit,
                            relative_Tariff = relative_Tariff,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Add GerantButton for grossist mode
        itsLancedDepuit_EditeBaseDonne.ifFalse {
            GerantButton(
                relative_Tariff = standardTariffs.lastOrNull { it.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_Gro },
                relative_M1Produit = relative_M1Produit,
                viewModel = viewModel,
                showLabels = showLabels,
                onClickAnulationButton = onClickAnulationButton
            )
        }
    }
}
