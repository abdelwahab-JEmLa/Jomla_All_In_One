package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem

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

@SuppressLint("DefaultLocale")
@Composable
fun PrixsVents_Handler(
    relative_Produit: ArticlesBasesStatsTable,
    relative_Tariff: M13TarificationInfos,

    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {    //<--
    val typeTarification = relative_Tariff.typeChoisi
    val currentApp_Est_Admin = focusedValuesGetter.currentApp_Est_Admin

    var editablePurchasePriceText by remember(relative_Produit) { mutableStateOf("") }
    var isEditingPurchasePrice by remember(relative_Produit) { mutableStateOf(false) }

    var isEditingUnitPrice by remember(relative_Produit) { mutableStateOf(false) }

    val purchasePriceFocusRequester = remember { FocusRequester() }

    val m10OperationVentCouleurs =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .focused_ListM10OpeVentCouleur_Par_PD_M1Produit

    fun executeClickLogic() {
        repositorysMainSetter
            .saveTariff_Et_RelateIt_Au_Vents_Correspond(
                m13TarificationInfos_Pour_Produit = relative_Tariff,
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
        repositorysMainSetter.upsert_M13TarificationInfos(
            relative_Tariff.copy(
                prixCurrency = newPrix
            )
        )
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
            // Use the tariff type's color instead of hardcoded Cyan
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
                                                relative_Tariff.prixCurrency / relative_Produit.nombreUniteInt
                                            )
                                        }"
                                    } else {
                                        "Prix de vente: ${relative_Tariff.prixCurrency}"
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

                // Decrease button
                val decrease_Value = if (relative_Tariff.prixCurrency < 200.0) 1.0 else 5.0

                if (currentApp_Est_Admin) {
                    IconButton(
                        onClick = {
                            val newPrice =
                                (relative_Tariff.prixCurrency - decrease_Value).coerceAtLeast(0.0)
                            handel_Add_Diminue_Prix(newPrice)
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

                ElevatedCard(
                    onClick = {
                        if (currentApp_Est_Admin) {
                            val newPrice = relative_Tariff.prixCurrency + decrease_Value
                            handel_Add_Diminue_Prix(newPrice)
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
