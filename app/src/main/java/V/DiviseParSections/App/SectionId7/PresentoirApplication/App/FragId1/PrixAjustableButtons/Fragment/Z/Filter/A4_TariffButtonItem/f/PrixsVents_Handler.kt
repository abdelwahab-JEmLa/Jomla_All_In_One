package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.f

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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

    // Add state to track the current tariff price, allowing BenificeAdjustmentButtons to update it
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

    fun handel_Add_Diminue_Prix(newPrix: Double) {
        val currentTime = System.currentTimeMillis()
        val timeDifferenceSeconds = (currentTime - relative_Tariff.creationTimestamps) / 1000

        if (timeDifferenceSeconds > 20) {
            // Créer un nouveau tarif si plus de 20 secondes
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
            handel_Add_Diminue_Prix(finalPrice)
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
                ElevatedCard {
                    if (isEditingPurchasePrice) {
                        OutlinedTextField(
                            modifier = Modifier
                                .width(100.dp)
                                .focusRequester(purchasePriceFocusRequester),
                            value = editablePurchasePriceText,
                            onValueChange = { newInput ->
                                editablePurchasePriceText = newInput
                            },
                            label = {
                                Text(
                                    if (isEditingUnitPrice) {
                                        "Prix unitaire: ${
                                            String.format(
                                                "%.2f",
                                                currentTariffPrice / relative_Produit.nombreUniteInt
                                            )
                                        }"
                                    } else {
                                        "Prix de vente: ${currentTariffPrice}"
                                    }
                                )
                            },
                            leadingIcon = {
                                IconButton(
                                    onClick = {
                                        toggleUnitPriceMode()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Calculate,
                                        contentDescription = if (isEditingUnitPrice) "Éditer prix total" else "Éditer prix unitaire",
                                        tint = if (isEditingUnitPrice) Color.Blue else Color.Gray
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { handlePurchasePriceEditDone() }
                            ),
                        )

                        LaunchedEffect(isEditingPurchasePrice) {
                            if (isEditingPurchasePrice) {
                                purchasePriceFocusRequester.requestFocus()
                            }
                        }
                    } else {
                        Text(
                            typeName,
                            modifier = Modifier
                                .width(100.dp)
                                .background(couleurButton)
                                .padding(4.dp)
                                .then(
                                    if (currentApp_Est_Admin) {
                                        Modifier.clickable {
                                            editablePurchasePriceText = ""
                                            isEditingPurchasePrice = true
                                            isEditingUnitPrice = false
                                        }
                                    } else {
                                        Modifier
                                    }
                                ),
                            color = textColor,
                            fontSize = 14.sp,
                            maxLines = 2
                        )
                    }
                }

                if (currentApp_Est_Admin) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
// Dans PrixsVents_Handler.kt, mettez à jour l'appel à BenificeAdjustmentButtons:

                        BenificeAdjustmentButtons(
                            allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                            relative_Produit = relative_Produit,
                            relative_Tariff = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                            onPriceChange = { newPrice, shouldCreateNew ->
                                val currentTime = System.currentTimeMillis()

                                if (shouldCreateNew) {
                                    // Créer un nouveau tarif
                                    val newTariff = relative_Tariff.copy(
                                        prixCurrency = newPrice,
                                        creationTimestamps = currentTime,
                                        dernierTimeTampsSynchronisationAvecFireBase = currentTime
                                    )
                                    repositorysMainSetter.upsert_M13TarificationInfos(newTariff)
                                } else {
                                    // Mettre à jour le tarif existant
                                    repositorysMainSetter.upsert_M13TarificationInfos(
                                        relative_Tariff.copy(
                                            prixCurrency = newPrice,
                                            dernierTimeTampsSynchronisationAvecFireBase = currentTime
                                        )
                                    )
                                }

                                currentTariffPrice = newPrice
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
                    onPriceChange = ::handel_Add_Diminue_Prix
                )
            }

            FloatingActionButton(
                modifier = Modifier.size(40.dp),
                onClick = ::handelClick,
                containerColor = couleurButton
            ) {
                typeTarification.iconVector?.let { iconVector ->
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = textColor
                    )
                }
            }
        }
    }
}


@Composable
private fun PriceAdjustmentButtons(
    currentApp_Est_Admin: Boolean,
    relative_Tariff: M13TarificationInfos,
    couleurButton: Color,
    textColor: Color,
    relative_Produit: ArticlesBasesStatsTable,
    onPriceChange: (Double) -> Unit
) {
    val decrease_Value = if (relative_Tariff.prixCurrency < 200.0) 1.0 else 5.0

    // Decrease button
    if (currentApp_Est_Admin) {
        IconButton(
            onClick = {
                val newPrice = (relative_Tariff.prixCurrency - decrease_Value).coerceAtLeast(0.0)
                onPriceChange(newPrice)
            },
            modifier = Modifier.size(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Diminuer le prix de vente",
                tint = Color.Black
            )
        }
    }

    // Price display and increase button
    ElevatedCard(
        onClick = {
            if (currentApp_Est_Admin) {
                val newPrice = relative_Tariff.prixCurrency + decrease_Value
                onPriceChange(newPrice)
            }
        }
    ) {
        val pls = if (currentApp_Est_Admin) " +" else ""

        Column {
            Text(
                "${relative_Tariff.prixCurrency}$pls",
                modifier = Modifier
                    .background(couleurButton)
                    .padding(4.dp),
                color = textColor
            )

            val unitPrice = relative_Tariff.prixCurrency / relative_Produit.nombreUniteInt
            Text(
                "س.و: ${String.format("%.2f", unitPrice)}",
                modifier = Modifier
                    .background(couleurButton.copy(alpha = 0.6f))
                    .padding(2.dp),
                color = textColor,
                fontSize = 10.sp
            )
        }
    }
}
