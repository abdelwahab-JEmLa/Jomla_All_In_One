package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.PrixsVents_Handler

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import org.koin.compose.koinInject
import java.util.SortedMap

@Composable
fun BenificeAdjustmentButtons(
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,
    relative_Produit: M01Produit,
    relative_Tariff: M13TarificationInfos,
    onPriceChange: (Double, Boolean) -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    list_M13TarificationInfos: List<M13TarificationInfos> = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue,
) {
    val prixAchat = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
        .filter {
            it.parent_M1Produit_KeyId == relative_Produit.keyID &&
                    it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService
        }
        .maxByOrNull { it.creationTimestamps }?.prixCurrency
        ?: 0.0
    val prixVente = relative_Tariff.prixCurrency
    val benefice = prixVente - prixAchat
    val nombreUnite = relative_Produit.nombreUniteInt

    val beneficeUnitaire = if (nombreUnite > 0) benefice / nombreUnite else 0.0

    var isEditingUnitBenefit by remember { mutableStateOf(false) }
    var unitBenefitText by remember { mutableStateOf("") }
    var isEditingTotalBenefit by remember { mutableStateOf(false) }
    var totalBenefitText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val totalBenefitFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingUnitBenefit) {
        if (isEditingUnitBenefit) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingTotalBenefit) {
        if (isEditingTotalBenefit) {
            totalBenefitFocusRequester.requestFocus()
        }
    }

    val benefitAdjustmentValue = when {
        benefice < 10.0 -> 1.0
        benefice < 50.0 -> 5.0
        benefice < 200.0 -> 5.0
        benefice < 1200.0 -> 25.0
        else -> 50.0
    }

    val unitBenefitAdjustmentValue = when {
        beneficeUnitaire < 1.0 -> 1.0
        beneficeUnitaire < 10.0 -> 2.0
        beneficeUnitaire < 50.0 -> 1.0
        else -> 5.0
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
        onPriceChange(newSellingPrice, shouldCreateNew)
    }

    fun updateUnitBenefitImmediately(newUnitBenefit: Double) {
        val totalBenefit = newUnitBenefit * nombreUnite
        val newSellingPrice = prixAchat + totalBenefit
        val shouldCreateNew = shouldCreateNewTariff()
        onPriceChange(newSellingPrice, shouldCreateNew)
    }

    fun handleUnitBenefitEditDone() {
        val newUnitBenefit = unitBenefitText.toDoubleOrNull()
        if (newUnitBenefit != null && newUnitBenefit >= 0) {
            updateUnitBenefitImmediately(newUnitBenefit)
        }
        isEditingUnitBenefit = false
    }

    fun handleTotalBenefitEditDone() {
        val newTotalBenefit = totalBenefitText.toDoubleOrNull()
        if (newTotalBenefit != null && newTotalBenefit >= 0) {
            updateBenefitImmediately(newTotalBenefit)
        }
        isEditingTotalBenefit = false
    }

    // Card with both benefits in column
    val colorUnite = Color(0xD8D9C3DC)
    ElevatedCard {
        Column {
            // Total benefit section at top
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Total benefit display/edit
                if (isEditingTotalBenefit) {
                    OutlinedTextField(
                        value = totalBenefitText,
                        onValueChange = { totalBenefitText = it },
                        modifier = Modifier
                            .width(80.dp)
                            .focusRequester(totalBenefitFocusRequester),
                        label = { Text("${String.format("%.0f", benefice)}", fontSize = 8.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleTotalBenefitEditDone() }
                        ),
                        singleLine = true
                    )
                } else {
                    Text(
                        String.format("%.0f", benefice),
                        modifier = Modifier
                            .background(Color(0xFF9C27B0))
                            .padding(4.dp)
                            .clickable {
                                isEditingTotalBenefit = true
                            },
                        color = Color.White
                    )
                }
            }

            // Unit benefit section - centered in Box
            Box(
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Unit benefit display/edit
                    if (isEditingUnitBenefit) {
                        OutlinedTextField(
                            value = unitBenefitText,
                            onValueChange = { unitBenefitText = it },
                            modifier = Modifier
                                .width(70.dp)
                                .focusRequester(focusRequester),
                            label = {
                                Text(
                                    "${String.format("%.2f", beneficeUnitaire)}",
                                    fontSize = 8.sp
                                )
                            },
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
                                .background(colorUnite)
                                .padding(2.dp)
                                .clickable {
                                    isEditingUnitBenefit = true
                                },
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }

                }
            }
        }
    }
}
