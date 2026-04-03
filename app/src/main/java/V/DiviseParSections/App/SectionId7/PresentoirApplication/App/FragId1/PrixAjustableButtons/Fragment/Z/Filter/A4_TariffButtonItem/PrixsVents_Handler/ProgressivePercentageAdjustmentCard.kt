package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.PrixsVents_Handler

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.text.font.FontWeight
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
    onPriceChange: (newPrix: Double, shouldCreateNew: Boolean) -> Unit = { _, _ -> },
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>? = null,
    relative_Produit: M01Produit? = null,
    currentTariffPrice: Double = 0.0,
    currentApp_Est_Admin: Boolean = false
) {
    var isEditingPercentage by remember { mutableStateOf(false) }
    var percentageText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingPercentage) {
        if (isEditingPercentage) focusRequester.requestFocus()
    }

    fun updateProgressivePercentage(newPercentage: Int) {
        val coercedPercentage = newPercentage.coerceIn(0, 100)
        repositorysMainSetter.upsert_M1Produit(
            produit.copy(pourcentage_Prix_Progressive = coercedPercentage)
        )
        onPercentageChange(coercedPercentage)
    }

    fun handlePercentageEditDone() {
        val newPercentage = percentageText.toIntOrNull()
        if (newPercentage != null && newPercentage in 0..100) {
            updateProgressivePercentage(newPercentage)
        }
        isEditingPercentage = false
    }

    val percentageColor = Color(0xFF4CAF50)

    ElevatedCard(modifier = Modifier.wrapContentWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {

            // ── Header ──────────────────────────────────────────────────────
            Text(
                text = "% Prog",
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = percentageColor,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // ── Percentage display/edit (no +/- buttons) ─────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditingPercentage) {
                    OutlinedTextField(
                        value = percentageText,
                        onValueChange = { percentageText = it },
                        modifier = Modifier
                            .width(60.dp)
                            .focusRequester(focusRequester),
                        label = { Text("${produit.pourcentage_Prix_Progressive}%", fontSize = 8.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { handlePercentageEditDone() }),
                        singleLine = true
                    )
                } else {
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

            // ── Benefit row for Edited_Pour_Client ───────────────────────
            if (currentApp_Est_Admin &&
                typeTarification == M13TarificationInfos.TypeChoisi.Edited_Pour_Client &&
                allTariffsGroupedAndSorted != null &&
                relative_Produit != null
            ) {
                BenefitDisplayRow(
                    allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                    relative_Produit = relative_Produit,
                    currentTariffPrice = currentTariffPrice,
                    onPriceChange = { newPrice, shouldCreateNew ->
                        onPriceChange(newPrice, shouldCreateNew)
                    }
                )
            }
        }
    }
}
