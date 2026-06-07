package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.PrixsVents_Handler

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedCard
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

@Composable
fun BeneficeClientAdjustmentCard(
    relative_Produit: M01Produit,
    relative_Tariff: M13TarificationInfos,
    onPriceChange_To_Change_Tariff_Prix: (Double, Boolean) -> Unit
) {
    val prixVente = relative_Tariff.prixCurrency
    val nombreUnite = relative_Produit.nombreUniteInt
    val tekherej = nombreUnite * relative_Produit.clientPrixVentUnite

    val beneficeClient = tekherej - prixVente
    val beneficeClientUnitaire = if (nombreUnite > 0) beneficeClient / nombreUnite else 0.0

    var isEditingTotal by remember { mutableStateOf(false) }
    var totalText by remember { mutableStateOf("") }
    var isEditingUnit by remember { mutableStateOf(false) }
    var unitText by remember { mutableStateOf("") }

    val totalFocusRequester = remember { FocusRequester() }
    val unitFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingTotal) { if (isEditingTotal) totalFocusRequester.requestFocus() }
    LaunchedEffect(isEditingUnit) { if (isEditingUnit) unitFocusRequester.requestFocus() }

    fun shouldCreateNew(): Boolean {
        val diff = (System.currentTimeMillis() - relative_Tariff.creationTimestamps) / 1000
        return diff > 20
    }

    fun applyTotalBenefit(newBenefit_Client: Double) {
        // new selling price = what client pays in total − the benefit we grant him
        onPriceChange_To_Change_Tariff_Prix(tekherej - newBenefit_Client, shouldCreateNew())
    }

    fun applyUnitBenefit(newBenefit_Client_Uniter: Double) {
        val newTotalBenefit = newBenefit_Client_Uniter * nombreUnite
        onPriceChange_To_Change_Tariff_Prix(tekherej - newTotalBenefit, shouldCreateNew())
    }

    val colorTotal = Color(0xFF1976D2)          // bleu = client
    val colorUnit = colorTotal.copy(alpha = 0.5f)

    ElevatedCard {
        Column(modifier = Modifier.padding(2.dp)) {

            // ── Bénéfice total ──────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditingTotal) {
                    OutlinedTextField(
                        value = totalText,
                        onValueChange = { totalText = it },
                        modifier = Modifier
                            .width(80.dp)
                            .focusRequester(totalFocusRequester),
                        label = { Text("%.0f".format(beneficeClient), fontSize = 8.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            totalText.toDoubleOrNull()
                                ?.takeIf { it >= 0 }
                                ?.let { applyTotalBenefit(it) }
                            isEditingTotal = false
                        }),
                        singleLine = true
                    )
                } else {
                    Text(
                        text = "%.0f".format(beneficeClient),
                        modifier = Modifier
                            .background(colorTotal)
                            .padding(4.dp)
                            .clickable { isEditingTotal = true; totalText = "" },
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }

            // ── Bénéfice unitaire ───────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditingUnit) {
                    OutlinedTextField(
                        value = unitText,
                        onValueChange = { unitText = it },
                        modifier = Modifier
                            .width(70.dp)
                            .focusRequester(unitFocusRequester),
                        label = { Text("%.2f".format(beneficeClientUnitaire), fontSize = 8.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            unitText.toDoubleOrNull()
                                ?.takeIf { it >= 0 }
                                ?.let { applyUnitBenefit(it) }
                            isEditingUnit = false
                        }),
                        singleLine = true
                    )
                } else {
                    Text(
                        text = "%.2f".format(beneficeClientUnitaire),
                        modifier = Modifier
                            .background(colorUnit)
                            .padding(2.dp)
                            .clickable { isEditingUnit = true; unitText = "" },
                        color = Color.White,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}
