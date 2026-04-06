package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Items

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Genere_Tariffs_currentApp_ItsWorkChezGrossisst
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Items.Components.PrixVentAdjustmentButtonsItsWorkChezGrossisst_Handler
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Items.Components.ProgressivePercentageAdjustmentCardItsWorkChezGrossisst_Handler
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import java.util.SortedMap
@SuppressLint("DefaultLocale")
@Composable
fun Prixs_currentApp_ItsWorkChezGrossisst_Handler(
    relative_Produit: M01Produit,
    relative_Tariff: M13TarificationInfos,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,
) {
    val active_Central_Values = focusedValuesGetter.active_Central_Values

    val typeTarification = relative_Tariff.typeChoisi
    fun focusedValuesGetter() = focusedValuesGetter

    val currentApp_Est_Admin = focusedValuesGetter.currentApp_Est_Admin

    val tariffGenerator = remember { Genere_Tariffs_currentApp_ItsWorkChezGrossisst() }

    val isGrossistTariffType = typeTarification in setOf(
        M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Achat,
        M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
        M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro
    )

    if (!isGrossistTariffType) {
        return
    }

    var currentTariffPrice by remember(relative_Tariff.prixCurrency) {
        mutableStateOf(relative_Tariff.prixCurrency)
    }

    val m10OperationVentCouleurs =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .focused_ListM10OpeVentCouleur_Par_PD_M1Produit

    fun save_Tariff_au_relative_vent_et_ferm_fabs_tariffs() {
        focusedValuesGetter().update_activeCentralValues(
            focusedValuesGetter.active_Central_Values.copy(
                fastSearchProduitPourVent = "",
            )
        )

        repositorysMainSetter
            .saveTariff_Et_RelateIt_Au_Vents_Correspond(
                m13TarificationInfos_Pour_Produit = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                m10OperationVentCouleurs = m10OperationVentCouleurs,
                aCentralFacade = aCentralFacade
            )

        aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
            .dismisses_By_toggle_CurrentApp_activeDialogSearchM1Produit()
    }

    fun getCurrentBenefitForTariffType(tariffType: M13TarificationInfos.TypeChoisi): Double {
        return when (tariffType) {
            M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros -> 5.0
            M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro -> 10.0
            else -> 0.0
        }
    }

    fun updatePurchasePriceIfNeeded(newSellingPrice: Double) {
        if (typeTarification !in setOf(
                M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro
            )
        ) {
            return
        }

        val currentPurchaseTariff =
            allTariffsGroupedAndSorted[M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Achat]
                ?.maxByOrNull { it.creationTimestamps }

        val currentBenefit = getCurrentBenefitForTariffType(typeTarification)
        val newPurchasePrice = newSellingPrice - currentBenefit

        if (currentPurchaseTariff != null) {
            val updatedPurchaseTariff = currentPurchaseTariff.copy(
                prixCurrency = newPurchasePrice,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            repositorysMainSetter.upsert_M13TarificationInfos(updatedPurchaseTariff)
        } else {
            val newPurchaseTariff = M13TarificationInfos(
                parent_M14VentPeriod_KeyId = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
                    ?: "",
                typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Achat,
                prixCurrency = newPurchasePrice,
                parent_M1Produit_KeyId = relative_Produit.keyID,
                parent_M1Produit_DebugInfos = relative_Produit.nom,
                creationTimestamps = System.currentTimeMillis()
            )
            repositorysMainSetter.upsert_M13TarificationInfos(newPurchaseTariff)
        }

        val updatedProduit = relative_Produit.copy(prixAchat = newPurchasePrice)
        repositorysMainSetter.upsert_M1Produit(updatedProduit)
    }

    fun updateRelatedGrossistTariffs(newPurchasePrice: Double) {
        val currentTime = System.currentTimeMillis()
        val oneMinuteAgo = currentTime - (60 * 1000)

        val relatedTariffs = allTariffsGroupedAndSorted.values.flatten().filter { tariff ->
            tariff.parent_M1Produit_KeyId == relative_Produit.keyID &&
                    tariff.typeChoisi in setOf(
                M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro
            ) &&
                    tariff.creationTimestamps >= oneMinuteAgo
        }

        relatedTariffs.forEach { tariff ->
            val oldPurchasePrice = relative_Produit.prixAchat
            val currentSellingPrice = tariff.prixCurrency
            val existingBenefit = currentSellingPrice - oldPurchasePrice

            val newSellingPrice = existingBenefit + newPurchasePrice

            val updatedTariff = tariff.copy(
                prixCurrency = newSellingPrice,
                dernierTimeTampsSynchronisationAvecFireBase = currentTime
            )

            repositorysMainSetter.upsert_M13TarificationInfos(updatedTariff)
        }
    }

    fun updateProgressiveTariffIfNeeded() {
        if (typeTarification in setOf(
                M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro
            )
        ) {
            tariffGenerator.updateProgressiveTariffOnRelatedChange(
                aCentralFacade = aCentralFacade,
                relative_M1Produit = relative_Produit,
                focusedValuesGetter = focusedValuesGetter
            )
        }
    }

    fun handel_Add_Diminue_Prix(newPrix: Double, shouldCreateNew: Boolean) {
        val currentTime = System.currentTimeMillis()
        updatePurchasePriceIfNeeded(newPrix)

        if (typeTarification == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Achat) {
            updateRelatedGrossistTariffs(newPrix)

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

        updateProgressiveTariffIfNeeded()
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
            val textColor = if (focusedValuesGetter.currentApp_ItsWorkChezGrossisst) Color.Black else  typeTarification.couleur_Text

            focusedValuesGetter.currentApp_ItsWorkChezGrossisst.ifTrue {
                FloatingActionButton(
                    modifier = Modifier.width(50.dp),
                    onClick = ::save_Tariff_au_relative_vent_et_ferm_fabs_tariffs,
                    containerColor = couleurButton
                ) {
                    val text = if (focusedValuesGetter.currentApp_ItsWorkChezGrossisst

                        ) "كانتيتي" else  typeTarification.abrgNom
                    Text(
                        text = text,
                        color = textColor,
                        fontSize = 7.sp,
                        maxLines = 2
                    )
                }
            }

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
                                    val prixDetaille =
                                        allTariffsGroupedAndSorted[M13TarificationInfos.TypeChoisi.Prix_Detaille]
                                            ?.maxByOrNull { it.creationTimestamps }?.prixCurrency
                                            ?: relative_Produit.prixVent

                                    val prixVent = relative_Produit.prixVent
                                    val priceDifference = prixDetaille - prixVent
                                    val adjustedPercentage =
                                        if (newPercentage == 50) 60 else newPercentage
                                    val progressiveAdjustment =
                                        priceDifference * (adjustedPercentage / 100.0)
                                    val newProgressivePrice = prixVent + progressiveAdjustment

                                    handel_Add_Diminue_Prix(newProgressivePrice, false)
                                }
                            )
                        }

                        else -> {}
                    }
                }

                PrixVentAdjustmentButtonsItsWorkChezGrossisst_Handler(
                    allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                    relative_Produit = relative_Produit,
                    relative_Tariff = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                    onPriceChange = { newPrice, shouldCreateNew ->
                        handel_Add_Diminue_Prix(newPrice, shouldCreateNew)
                    },
                    executeClickLogic = { save_Tariff_au_relative_vent_et_ferm_fabs_tariffs() }
                )
            }
            focusedValuesGetter.currentApp_ItsWorkChezGrossisst.ifFalse {
                FloatingActionButton(
                    modifier = Modifier.width(80.dp),
                    onClick = ::save_Tariff_au_relative_vent_et_ferm_fabs_tariffs,
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
}
