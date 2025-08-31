package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.PrixsVents_Handler

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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import java.util.SortedMap

@SuppressLint("DefaultLocale")
@Composable
fun PrixsVents_Handler(
    relative_Produit: ArticlesBasesStatsTable,
    relative_Tariff: M13TarificationInfos,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,
) {
    val typeTarification = relative_Tariff.typeChoisi
    val currentApp_Est_Admin = focusedValuesGetter.currentApp_Est_Admin

    var editablePurchasePriceText by remember(relative_Produit) { mutableStateOf("") }
    var isEditingPurchasePrice by remember(relative_Produit) { mutableStateOf(false) }
    var isEditingUnitPrice by remember(relative_Produit) { mutableStateOf(false) }

    var currentTariffPrice by remember(relative_Tariff.prixCurrency) {
        mutableStateOf(relative_Tariff.prixCurrency)
    }

    val purchasePriceFocusRequester = remember { FocusRequester() }

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

    fun toggleUnitPriceMode() {
        isEditingUnitPrice = !isEditingUnitPrice
        editablePurchasePriceText = ""
    }

    // FIXED: Updated to match new signature with shouldCreateNew parameter
    fun handel_Add_Diminue_Prix(newPrix: Double, shouldCreateNew: Boolean) {
        val currentTime = System.currentTimeMillis()

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

    fun handlePurchasePriceEditDone() {
        val newPurchasePrice = editablePurchasePriceText.toDoubleOrNull()
        if (newPurchasePrice != null && newPurchasePrice >= 0) {
            val finalPrice = if (isEditingUnitPrice) {
                newPurchasePrice * relative_Produit.nombreUniteInt
            } else {
                newPurchasePrice
            }
            // Always create new for manual edits
            handel_Add_Diminue_Prix(finalPrice, true)
        }
        isEditingPurchasePrice = false
        isEditingUnitPrice = false
    }

    Column {
        Row(
            modifier = Modifier
                .semantics(mergeDescendants = true) {
                    set(value = relative_Tariff, key = SemanticsPropertyKey("relative_Tariff"))
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val couleurButton = typeTarification.couleur
            val textColor = typeTarification.couleur_Text
            val typeName = typeTarification.nomArabe

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (currentApp_Est_Admin) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BenificeAdjustmentButtons(
                            allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                            relative_Produit = relative_Produit,
                            relative_Tariff = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                            onPriceChange = { newPrice, shouldCreateNew ->
                                handel_Add_Diminue_Prix(newPrice, shouldCreateNew)
                            }
                        )
                    }
                }

                PriceAdjustmentButtons(
                    currentApp_Est_Admin = currentApp_Est_Admin,
                    relative_Tariff = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                    couleurButton = couleurButton,
                    textColor = textColor,
                    relative_Produit = relative_Produit,
                    onPriceChange = { newPrice, shouldCreateNew ->
                        handel_Add_Diminue_Prix(newPrice, shouldCreateNew)
                    }
                )
            }

            // FIXED: Changed icon to show type name instead of icon
            FloatingActionButton(
                modifier = Modifier.width(80.dp),
                onClick = ::handelClick,
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
