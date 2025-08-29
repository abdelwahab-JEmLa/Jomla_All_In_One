package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@SuppressLint("DefaultLocale")
@Composable
fun PrixAchatHandler(
    relative_Produit: ArticlesBasesStatsTable,
    relative_Tariff: M13TarificationInfos,

    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,

    showLabels: Boolean,
    nombreUnite: Int = 1,
) {
    val typeTarification = relative_Tariff.typeChoisi
    val currentApp_Est_Admin = focusedValuesGetter.currentApp_Est_Admin

    var editablePurchasePriceText by remember(relative_Produit) { mutableStateOf("") }
    var isEditingPurchasePrice by remember(relative_Produit) { mutableStateOf(false) }

    var isEditingUnitPrice by remember(relative_Produit) { mutableStateOf(false) }

    val purchasePriceFocusRequester = remember { FocusRequester() }

    val m10OperationVentCouleurs =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .focused_ListM10OpeVentCouleur_Par_PD_M1Produit

    fun toggleUnitPriceMode() {
        isEditingUnitPrice = !isEditingUnitPrice
        editablePurchasePriceText = ""

        // Set the appropriate initial value based on the mode
        if (isEditingUnitPrice) {
            // Show unit price
            val unitPrice = relative_Produit.prixAchat / nombreUnite
            editablePurchasePriceText = String.format("%.2f", unitPrice)
        } else {
            // Show total price
            editablePurchasePriceText = relative_Produit.prixAchat.toString()
        }
    }

    fun handel_Add_Diminue_Prix(newPrix: Double) {
        repositorysMainSetter.update_M1Produit(
            relative_Produit.copy(
                prixAchat = newPrix,
                prixAchatDernierTimeTempUpdate = System.currentTimeMillis()
            )
        )
    }

    fun handlePurchasePriceEditDone() {
        val newPurchasePrice = editablePurchasePriceText.toDoubleOrNull()
        if (newPurchasePrice != null && newPurchasePrice >= 0) {
            val finalPrice = if (isEditingUnitPrice) {
                newPurchasePrice * nombreUnite
            } else {
                newPurchasePrice
            }
            handel_Add_Diminue_Prix(finalPrice)
        }
        isEditingPurchasePrice = false
        isEditingUnitPrice = false
    }

    Row(
        modifier = Modifier
            .getSemanticsTag(nomVal = "produit", data = relative_Produit.nom),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val couleurButton = Color.Cyan // Purchase price specific color

        if (showLabels) {
            val typeName = typeTarification.nomArabe

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ElevatedCard {
                    if (isEditingPurchasePrice) {
                        OutlinedTextField(
                            modifier = Modifier
                                .getSemanticsTag(
                                    nomVal = "editablePurchasePriceText",
                                    data = editablePurchasePriceText
                                )
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
                                                relative_Produit.prixAchat / nombreUnite
                                            )
                                        }"
                                    } else {
                                        "Prix d'achat: ${relative_Produit.prixAchat}"
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
                            color = Color.Black,
                            fontSize = 14.sp,
                            maxLines = 2
                        )
                    }
                }

                // Decrease button
                val decrease_Value = if (relative_Produit.prixAchat < 200.0) 1.0 else 5.0

                if (currentApp_Est_Admin) {
                    IconButton(
                        onClick = {
                            val newPrice = (relative_Produit.prixAchat - decrease_Value).coerceAtLeast(0.0)
                            handel_Add_Diminue_Prix(newPrice)
                        },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Diminuer le prix d'achat",
                            tint = Color.Black
                        )
                    }
                }

                ElevatedCard(
                    onClick = {
                        if (currentApp_Est_Admin) {
                            val newPrice = relative_Produit.prixAchat + decrease_Value
                            handel_Add_Diminue_Prix(newPrice)
                        }
                    }
                ) {
                    val pls = if (currentApp_Est_Admin) " +" else ""

                    Column {
                        Text(
                            "${relative_Produit.prixAchat}$pls",
                            modifier = Modifier
                                .background(couleurButton)
                                .padding(4.dp),
                            color = Color.Black
                        )

                        val unitPrice = relative_Produit.prixAchat / nombreUnite
                        Text(
                            "س.و: ${String.format("%.2f", unitPrice)}",
                            modifier = Modifier
                                .background(couleurButton.copy(alpha = 0.6f))
                                .padding(2.dp),
                            color = Color.Black,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = {},
            containerColor = couleurButton
        ) {
            typeTarification.iconVector?.let { iconVector ->
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }
    }
}
