package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.f

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.SortedMap

@Composable
fun BenificeAdjustmentButtons(
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,
    relative_Produit: ArticlesBasesStatsTable,
    relative_Tariff: M13TarificationInfos,
    onPriceChange: (Double, Boolean) -> Unit // Boolean indicates if should create new tariff
) {
    // Find purchase price from tariffs
    val prixAchatTariff =
        allTariffsGroupedAndSorted[M13TarificationInfos.TypeChoisi.Tariff_Achat_Depuit_Grossisst]
            ?.maxByOrNull { it.creationTimestamps }

    val prixAchat = prixAchatTariff?.prixCurrency ?: relative_Produit.prixAchat
    val prixVente = relative_Tariff.prixCurrency
    val benefice = prixVente - prixAchat
    val nombreUnite = relative_Produit.nombreUniteInt

    // Calculate unit benefit
    val beneficeUnitaire = if (nombreUnite > 0) benefice / nombreUnite else 0.0

    // State for unit benefit editing
    var isEditingUnitBenefit by remember { mutableStateOf(false) }
    var unitBenefitText by remember { mutableStateOf("") }

    val benefitAdjustmentValue = if (benefice < 50.0) 5.0 else 10.0
    val unitBenefitAdjustmentValue = if (beneficeUnitaire < 5.0) 0.5 else 1.0

    val benefitColor = when {
        benefice < 0 -> Color.Red
        benefice < 20 -> Color(0xFFFF9800) // Orange
        benefice < 50 -> Color(0xFF4CAF50) // Green
        else -> Color(0xFF2196F3) // Blue
    }

    fun shouldCreateNewTariff(): Boolean {
        val currentTime = System.currentTimeMillis()
        val tariffCreationTime = relative_Tariff.creationTimestamps
        val timeDifferenceSeconds = (currentTime - tariffCreationTime) / 1000
        return timeDifferenceSeconds > 20
    }

    fun updateBenefitImmediately(newBenefit: Double) {
        val newSellingPrice = prixAchat + newBenefit
        val shouldCreateNew = shouldCreateNewTariff()
        onPriceChange(newSellingPrice.coerceAtLeast(prixAchat), shouldCreateNew)
    }

    fun updateUnitBenefitImmediately(newUnitBenefit: Double) {
        val totalBenefit = newUnitBenefit * nombreUnite
        val newSellingPrice = prixAchat + totalBenefit
        val shouldCreateNew = shouldCreateNewTariff()
        onPriceChange(newSellingPrice.coerceAtLeast(prixAchat), shouldCreateNew)
    }



    fun handleUnitBenefitEditDone() {
        val newUnitBenefit = unitBenefitText.toDoubleOrNull()
        if (newUnitBenefit != null && newUnitBenefit >= 0) {
            updateUnitBenefitImmediately(newUnitBenefit)
        }
        isEditingUnitBenefit = false
    }

    // Card with both benefits in column
    ElevatedCard {
        Column {
            // Total benefit section at top
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrease total benefit button (left side like unit benefit)
                IconButton(
                    onClick = {
                        val newBenefit = (benefice - benefitAdjustmentValue).coerceAtLeast(0.0)
                        updateBenefitImmediately(newBenefit)
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color(0xFF9C27B0), // MÃªme bleu clair que les autres boutons
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "ØªÙ‚Ù„ÙŠÙ„ Ø§Ù„ÙØ§Ø¦Ø¯Ø©",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Text(
                    String.format("%.0f", benefice),
                    modifier = Modifier
                        .background(Color(0xFF9C27B0)) // ArriÃ¨re-plan plus foncÃ©
                        .padding(4.dp),
                    color = Color.White
                )

                // Increase total benefit button (separate circular button)
                IconButton(
                    onClick = {
                        val newBenefit = benefice + benefitAdjustmentValue
                        updateBenefitImmediately(newBenefit)
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color(0xFF9C27B0), // MÃªme bleu clair que les boutons unitaires
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Ø²ÙŠØ§Ø¯Ø© Ø§Ù„ÙØ§Ø¦Ø¯Ø©",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Unit benefit section below total benefit
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrease unit benefit button
                IconButton(
                    onClick = {
                        val newUnitBenefit = (beneficeUnitaire - unitBenefitAdjustmentValue).coerceAtLeast(0.0)
                        updateUnitBenefitImmediately(newUnitBenefit)
                    },
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = Color(0xFF64B5F6),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "ØªÙ‚Ù„ÙŠÙ„ Ø§Ù„ÙØ§Ø¦Ø¯Ø© Ø§Ù„ÙˆØ­Ø¯Ø©",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }

                // Unit benefit display/edit
                if (isEditingUnitBenefit) {
                    OutlinedTextField(
                        value = unitBenefitText,
                        onValueChange = { unitBenefitText = it },
                        modifier = Modifier.width(70.dp),
                        label = { Text("Ù.ÙˆØ­Ø¯Ø©", fontSize = 8.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleUnitBenefitEditDone() }
                        ),
                        singleLine = true
                    )
                } else {
                    Text(
                        "${String.format("%.2f", beneficeUnitaire)}",
                        modifier = Modifier
                            .background(benefitColor.copy(alpha = 0.8f))
                            .padding(2.dp)
                            .clickable {
                                unitBenefitText = String.format("%.2f", beneficeUnitaire)
                                isEditingUnitBenefit = true
                            },
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }

                // Increase unit benefit button
                IconButton(
                    onClick = {
                        val newUnitBenefit = beneficeUnitaire + unitBenefitAdjustmentValue
                        updateUnitBenefitImmediately(newUnitBenefit)
                    },
                    modifier = Modifier
                        .size(25.dp)
                        .background(
                            color = Color(0xFF64B5F6),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Ø²ÙŠØ§Ø¯Ø© Ø§Ù„ÙØ§Ø¦Ø¯Ø© Ø§Ù„ÙˆØ­Ø¯Ø©",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
