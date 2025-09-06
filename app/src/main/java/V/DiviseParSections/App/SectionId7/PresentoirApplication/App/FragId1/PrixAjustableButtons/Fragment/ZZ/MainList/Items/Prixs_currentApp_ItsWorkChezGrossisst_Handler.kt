package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Items

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Items.Components.BenificeAdjustmentButtonsItsWorkChezGrossisst_Handler
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Items.Components.PrixVentAdjustmentButtonsItsWorkChezGrossisst_Handler
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Items.Components.ProgressivePercentageAdjustmentCardItsWorkChezGrossisst_Handler
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import java.util.SortedMap

@SuppressLint("DefaultLocale")
@Composable
fun Prixs_currentApp_ItsWorkChezGrossisst_Handler(
    relative_Produit: ArticlesBasesStatsTable,
    relative_Tariff: M13TarificationInfos,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,
) {
    val typeTarification = relative_Tariff.typeChoisi
    val currentApp_Est_Admin = focusedValuesGetter.currentApp_Est_Admin

    // Utilise seulement les types de tarifs spécifiés
    val isGrossistTariffType = typeTarification in setOf(
        M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Achat,
        M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
        M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive,
        M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro
    )

    // Don't render if not a grossist tariff type
    if (!isGrossistTariffType) {
        return
    }

    var currentTariffPrice by remember(relative_Tariff.prixCurrency) {
        mutableStateOf(relative_Tariff.prixCurrency)
    }

    val m10OperationVentCouleurs =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .focused_ListM10OpeVentCouleur_Par_PD_M1Produit

    fun executeClickLogic() {
        repositorysMainSetter
            .saveTariff_Et_RelateIt_Au_Vents_Correspond(
                m13TarificationInfos_Pour_Produit = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                m10OperationVentCouleurs = m10OperationVentCouleurs
            )

        aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
            .dismisses_By_toggle_CurrentApp_activeDialogSearchM1Produit()
    }

    fun handelClick() {
        executeClickLogic()
    }

    /**
     * Updates related grossist tariffs when the purchase price (Tariff_ItsWorkInGrossist_Achat) changes
     */
    fun updateRelatedGrossistTariffs(newPurchasePrice: Double) {
        val currentTime = System.currentTimeMillis()
        val oneMinuteAgo = currentTime - (60 * 1000) // 1 minute in milliseconds

        // Get all related grossist tariffs for this product that were created within the last minute
        val relatedTariffs = allTariffsGroupedAndSorted.values.flatten().filter { tariff ->
            tariff.parent_M1Produit_KeyId == relative_Produit.keyID &&
                    tariff.typeChoisi in setOf(
                M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro
            ) &&
                    tariff.creationTimestamps >= oneMinuteAgo
        }

        // Update each related tariff by recalculating price = benefit + new purchase price
        relatedTariffs.forEach { tariff ->
            // Get the current purchase price from the tariff to calculate existing benefit
            val oldPurchasePrice = relative_Produit.prixAchat
            val currentSellingPrice = tariff.prixCurrency
            val existingBenefit = currentSellingPrice - oldPurchasePrice

            // Calculate new selling price = existing benefit + new purchase price
            val newSellingPrice = existingBenefit + newPurchasePrice

            // Update the tariff with new price
            val updatedTariff = tariff.copy(
                prixCurrency = newSellingPrice.coerceAtLeast(newPurchasePrice), // Ensure selling price >= purchase price
                dernierTimeTampsSynchronisationAvecFireBase = currentTime
            )

            repositorysMainSetter.upsert_M13TarificationInfos(updatedTariff)
        }
    }

    fun handel_Add_Diminue_Prix(newPrix: Double, shouldCreateNew: Boolean) {
        //<--
        //TODO(1): fait que si le type  Tariff_ItsWorkInGrossist_SuperGros de par temp add ou update une tariff de Tariff_Achat_Depuit_Grossisst prix = Tariff_ItsWorkInGrossist_SuperGros 
        val currentTime = System.currentTimeMillis()

        // If this is a purchase price tariff being updated, also update related grossist tariffs
        if (typeTarification == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Achat) {
            updateRelatedGrossistTariffs(newPrix)

            // Also update the product's base purchase price
            val updatedProduit = relative_Produit.copy(prixAchat = newPrix)
            repositorysMainSetter.upsert_M1Produit(updatedProduit)
        }

        if (shouldCreateNew) {
            val newTariff = relative_Tariff.copy(
                prixCurrency = newPrix,
                creationTimestamps = currentTime,
                dernierTimeTampsSynchronisationAvecFireBase = currentTime
            )
            repositorysMainSetter.upsert_M13TarificationInfos(newTariff)
            currentTariffPrice = newPrix
        } else {
            currentTariffPrice = newPrix
            repositorysMainSetter.upsert_M13TarificationInfos(
                relative_Tariff.copy(
                    prixCurrency = newPrix,
                    dernierTimeTampsSynchronisationAvecFireBase = currentTime
                )
            )
        }
    }

    Column {
        Row(
            modifier = Modifier.semantics(mergeDescendants = true) {
                set(value = relative_Tariff, key = SemanticsPropertyKey("relative_Tariff"))
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val couleurButton = typeTarification.couleur
            val textColor = typeTarification.couleur_Text

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (currentApp_Est_Admin) {
                    when (typeTarification) {
                        M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive -> {
                            ProgressivePercentageAdjustmentCardItsWorkChezGrossisst_Handler(
                                currentApp_Est_Admin = currentApp_Est_Admin,
                                allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                                relative_Produit = relative_Produit,
                                currentTariffPrice = currentTariffPrice,
                                onPriceChange = { newPrice, shouldCreateNew ->
                                    handel_Add_Diminue_Prix(newPrice, shouldCreateNew)
                                },
                                produit = relative_Produit,
                                typeTarification = typeTarification,
                                repositorysMainSetter = repositorysMainSetter,
                                onPercentageChange = { newPercentage ->
                                    val prixDetaille = allTariffsGroupedAndSorted[M13TarificationInfos.TypeChoisi.Prix_Detaille]
                                        ?.maxByOrNull { it.creationTimestamps }?.prixCurrency
                                        ?: relative_Produit.prixVent

                                    val prixVent = relative_Produit.prixVent
                                    val priceDifference = prixDetaille - prixVent
                                    val adjustedPercentage = if (newPercentage == 50) 60 else newPercentage
                                    val progressiveAdjustment = priceDifference * (adjustedPercentage / 100.0)
                                    val newProgressivePrice = prixVent + progressiveAdjustment

                                    handel_Add_Diminue_Prix(newProgressivePrice, false)
                                }
                            )
                        }
                        M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                        M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro -> {
                            BenificeAdjustmentButtonsItsWorkChezGrossisst_Handler(
                                allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                                relative_Produit = relative_Produit,
                                relative_Tariff = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                                onPriceChange = { newPrice, shouldCreateNew ->
                                    handel_Add_Diminue_Prix(newPrice, shouldCreateNew)
                                }
                            )
                        }
                        else->{}
                    }
                }

                PrixVentAdjustmentButtonsItsWorkChezGrossisst_Handler(
                    allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                    relative_Produit = relative_Produit,
                    relative_Tariff = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                    onPriceChange = { newPrice, shouldCreateNew ->
                        handel_Add_Diminue_Prix(newPrice, shouldCreateNew)
                    }
                )
            }

            FloatingActionButton(
                modifier = Modifier.width(80.dp),
                onClick = ::executeClickLogic,
                containerColor = couleurButton
            ) {
                Text(
                    text = typeTarification.nomArabe,
                    color = textColor,
                    fontSize = 10.sp,
                    maxLines = 2
                )
            }
        }
    }
}
