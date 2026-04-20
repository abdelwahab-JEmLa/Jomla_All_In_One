package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Items.Components

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.SortedMap

@Composable
fun PrixVentAdjustmentButtonsItsWorkChezGrossisst_Handler(
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,
    relative_Produit: M01Produit,
    relative_Tariff: M13TarificationInfos,
    onPriceChange: (Double, Boolean,) -> Unit,
    executeClickLogic: () -> Unit
) {
    val prixAchat = relative_Produit.prixAchat
    val prixVente = relative_Tariff.prixCurrency
    val nombreUnite = relative_Produit.nombreUniteInt

    val prixVenteUnitaire = if (nombreUnite > 0) prixVente / nombreUnite else 0.0

    var isEditingUnitPrice by remember { mutableStateOf(false) }
    var unitPriceText by remember { mutableStateOf("") }
    var isEditingTotalPrice by remember { mutableStateOf(false) }
    var totalPriceText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val totalPriceFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isEditingUnitPrice) {
        if (isEditingUnitPrice) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingTotalPrice) {
        if (isEditingTotalPrice) {
            totalPriceFocusRequester.requestFocus()
        }
    }

    val totalPriceAdjustmentValue = when {
        prixVente < 10.0 -> 1.0
        prixVente < 50.0 -> 5.0
        prixVente < 1000.0 -> 10.0
        prixVente < 2000.0 -> 25.0
        else -> 50.0
    }

    val unitPriceAdjustmentValue = when {
        prixVenteUnitaire < 1.0 -> 0.1
        prixVenteUnitaire < 5.0 -> 0.5
        prixVenteUnitaire < 20.0 -> 1.0
        prixVenteUnitaire < 50.0 -> 2.0
        else -> 5.0
    }

    val tariffColor = relative_Tariff.typeChoisi.couleur
    val tariffTextColor = relative_Tariff.typeChoisi.couleur_Text

    fun shouldCreateNewTariff(): Boolean {
        val currentTime = System.currentTimeMillis()
        val tariffCreationTime = relative_Tariff.creationTimestamps
        val timeDifferenceSeconds = (currentTime - tariffCreationTime) / 1000
        return timeDifferenceSeconds > 20
    }

    fun updateTotalPriceImmediately(newTotalPrice: Double) {
        val shouldCreateNew = shouldCreateNewTariff()
        onPriceChange(newTotalPrice, shouldCreateNew)
    }

    fun updateUnitPriceImmediately(newUnitPrice: Double) {
        val totalPrice = newUnitPrice * nombreUnite
        val shouldCreateNew = shouldCreateNewTariff()
        onPriceChange(totalPrice, shouldCreateNew)
    }

    fun handleUnitPriceEditDone() {
        val newUnitPrice = unitPriceText.toDoubleOrNull()
        if (newUnitPrice != null && newUnitPrice >= 0) {
            updateUnitPriceImmediately(newUnitPrice)
        }
        isEditingUnitPrice = false
        focusManager.clearFocus()
    }

    fun handleTotalPriceEditDone() {
        val newTotalPrice = totalPriceText.toDoubleOrNull()
        if (newTotalPrice != null && newTotalPrice >= 0) {
            updateTotalPriceImmediately(newTotalPrice)
            executeClickLogic()
        }
        isEditingTotalPrice = false
        focusManager.clearFocus()
    }

    // Card with both selling prices in column
    ElevatedCard(
        modifier = Modifier
            .width(100.dp)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            // Total selling price section at top
            Row(
                modifier = Modifier
                    .fillMaxWidth() ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrease total price button
                IconButton(
                    onClick = {
                        val newPrice =
                            (prixVente - totalPriceAdjustmentValue)
                        updateTotalPriceImmediately(newPrice)
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = tariffColor.copy(alpha = 0.8f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "تقليل السعر الإجمالي",
                        tint = tariffTextColor,
                        modifier = Modifier.size(12.dp)
                    )
                }

                if (isEditingTotalPrice) {
                    OutlinedTextField(
                        value = totalPriceText,
                        onValueChange = { totalPriceText = it },
                        modifier = Modifier
                            .width(80.dp)
                            .focusRequester(totalPriceFocusRequester),
                        label = { Text("${String.format("%.0f", prixVente)}", fontSize = 8.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                handleTotalPriceEditDone()

                            }
                        ),
                        singleLine = true
                    )
                } else {
                    Text(
                        String.format("%.0f", prixVente),
                        modifier = Modifier
                            .background(tariffColor)
                            .padding(4.dp)
                            .clickable {
                                isEditingTotalPrice = true
                                totalPriceText = "" // <-- Champ vide
                            },
                        color = tariffTextColor
                    )
                }

                // Increase total price button
                IconButton(
                    onClick = {
                        val newPrice = prixVente + totalPriceAdjustmentValue
                        updateTotalPriceImmediately(newPrice)
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = tariffColor.copy(alpha = 0.8f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "زيادة السعر الإجمالي",
                        tint = tariffTextColor,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Unit price section - centered
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Decrease unit price button
                    IconButton(
                        onClick = {
                            val newUnitPrice =
                                (prixVenteUnitaire - unitPriceAdjustmentValue).coerceAtLeast(0.0)
                            updateUnitPriceImmediately(newUnitPrice)
                        },
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = tariffColor.copy(alpha = 0.7f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "تقليل السعر الوحدة",
                            tint = tariffTextColor,
                            modifier = Modifier.size(10.dp)
                        )
                    }

                    // Unit price display/edit
                    if (isEditingUnitPrice) {
                        OutlinedTextField(
                            value = unitPriceText,
                            onValueChange = { unitPriceText = it },
                            modifier = Modifier
                                .width(70.dp)
                                .focusRequester(focusRequester),
                            label = {
                                Text(
                                    "${String.format("%.2f", prixVenteUnitaire)}",
                                    fontSize = 8.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    handleUnitPriceEditDone()
                                }
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            "${String.format("%.2f", prixVenteUnitaire)}",
                            modifier = Modifier
                                .background(tariffColor.copy(alpha = 0.3f))
                                .padding(2.dp)
                                .clickable {
                                    isEditingUnitPrice = true
                                    unitPriceText = "" // <-- Champ vide
                                },
                            color = tariffTextColor,
                            fontSize = 10.sp
                        )
                    }

                    // Increase unit price button
                    IconButton(
                        onClick = {
                            val newUnitPrice = prixVenteUnitaire + unitPriceAdjustmentValue
                            updateUnitPriceImmediately(newUnitPrice)
                        },
                        modifier = Modifier
                            .size(25.dp)
                            .background(
                                color = tariffColor.copy(alpha = 0.7f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "زيادة السعر الوحدة",
                            tint = tariffTextColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
