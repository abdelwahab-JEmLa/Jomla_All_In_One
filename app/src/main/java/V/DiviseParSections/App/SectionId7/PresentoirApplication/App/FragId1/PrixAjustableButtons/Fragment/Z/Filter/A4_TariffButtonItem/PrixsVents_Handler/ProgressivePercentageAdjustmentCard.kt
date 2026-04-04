package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.PrixsVents_Handler

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.SortedMap

@Composable
fun ProgressivePercentageAdjustmentCard(
    produit: M01Produit,
    typeTarification: M13TarificationInfos.TypeChoisi,
    repositorysMainSetter: RepositorysMainSetter,
    onPercentageChange: (Int) -> Unit,
    onPriceChange: (newPrix: Double, shouldCreateNew: Boolean) -> Unit ={_,_->},
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>> ?=null,
    relative_Produit: M01Produit ?=null,
    currentTariffPrice: Double=0.0,
    currentApp_Est_Admin: Boolean =false
) {
    var isEditingPercentage by remember { mutableStateOf(false) }
    var percentageText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingPercentage) {
        if (isEditingPercentage) {
            focusRequester.requestFocus()
        }
    }

    fun updateProgressivePercentage(newPercentage: Int) {
        val coercedPercentage = newPercentage.coerceIn(0, 100)

        val updatedProduit = produit.copy(
            pourcentage_Prix_Progressive = coercedPercentage
        )

        repositorysMainSetter.upsert_M1Produit(updatedProduit)
        onPercentageChange(coercedPercentage)
    }

    fun handlePercentageEditDone() {
        val newPercentage = percentageText.toIntOrNull()
        if (newPercentage != null && newPercentage in 0..100) {
            updateProgressivePercentage(newPercentage)
        }
        isEditingPercentage = false
    }

    val percentageColor = Color(0xFF4CAF50) // Green color for percentage
    val adjustmentValue = 5

    ElevatedCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(4.dp)
        ) {
            // Percentage display section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Decrease percentage button
                IconButton(
                    onClick = {
                        val newPercentage = (produit.pourcentage_Prix_Progressive - adjustmentValue).coerceAtLeast(0)
                        updateProgressivePercentage(newPercentage)
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = percentageColor,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "تقليل النسبة المئوية",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }

                // Percentage display/edit
                if (isEditingPercentage) {
                    OutlinedTextField(
                        value = percentageText,
                        onValueChange = { percentageText = it },
                        modifier = Modifier
                            .width(60.dp)
                            .focusRequester(focusRequester),
                        label = {
                            Text("${produit.pourcentage_Prix_Progressive}%", fontSize = 8.sp)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handlePercentageEditDone() }
                        ),
                        singleLine = true
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${produit.pourcentage_Prix_Progressive}%",
                            fontSize = 12.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .background(percentageColor)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable {
                                    percentageText = produit.pourcentage_Prix_Progressive.toString()
                                    isEditingPercentage = true
                                }
                        )
                    }
                }

                // Increase percentage button
                IconButton(
                    onClick = {
                        val newPercentage = (produit.pourcentage_Prix_Progressive + adjustmentValue).coerceAtMost(100)
                        updateProgressivePercentage(newPercentage)
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = percentageColor,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "زيادة النسبة المئوية",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            // BenefitDisplayRow always rendered — no conditions
            BenefitDisplayRow(
                allTariffsGroupedAndSorted = allTariffsGroupedAndSorted ?: sortedMapOf(),
                relative_Produit = relative_Produit ?: produit,
                currentTariffPrice = currentTariffPrice,
                onPriceChange = { newPrice, shouldCreateNew ->
                    onPriceChange(newPrice, shouldCreateNew)
                }
            )
        }
    }
}
