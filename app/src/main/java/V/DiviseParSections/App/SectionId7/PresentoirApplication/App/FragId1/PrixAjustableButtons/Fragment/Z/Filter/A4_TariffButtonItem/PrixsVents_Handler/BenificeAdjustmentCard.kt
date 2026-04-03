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
    val prixAchatTariff =
        allTariffsGroupedAndSorted[M13TarificationInfos.TypeChoisi.Tariff_Achat_Depuit_Grossisst]
            ?.maxByOrNull { it.creationTimestamps }

    val prixAchat = list_M13TarificationInfos
        .filter {
            it.parent_M1Produit_KeyId == relative_Produit.keyID &&
                    it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService
        }
        .maxByOrNull { it.creationTimestamps }?.prixCurrency
        ?: prixAchatTariff?.prixCurrency
        ?: relative_Produit.prixAchat

    val prixVente    = relative_Tariff.prixCurrency
    val benefice     = prixVente - prixAchat
    val nombreUnite  = relative_Produit.nombreUniteInt
    val beneficeUnitaire = if (nombreUnite > 0) benefice / nombreUnite else 0.0

    var isEditingUnitBenefit  by remember { mutableStateOf(false) }
    var unitBenefitText       by remember { mutableStateOf("") }
    var isEditingTotalBenefit by remember { mutableStateOf(false) }
    var totalBenefitText      by remember { mutableStateOf("") }
    val focusRequester             = remember { FocusRequester() }
    val totalBenefitFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingUnitBenefit)  { if (isEditingUnitBenefit)  focusRequester.requestFocus() }
    LaunchedEffect(isEditingTotalBenefit) { if (isEditingTotalBenefit) totalBenefitFocusRequester.requestFocus() }

    fun shouldCreateNewTariff(): Boolean {
        val diff = (System.currentTimeMillis() - relative_Tariff.creationTimestamps) / 1000
        return diff > 20
    }

    fun updateBenefitImmediately(newBenefit: Double) {
        onPriceChange(prixAchat + newBenefit, shouldCreateNewTariff())
    }

    fun updateUnitBenefitImmediately(newUnitBenefit: Double) {
        onPriceChange(prixAchat + newUnitBenefit * nombreUnite, shouldCreateNewTariff())
    }

    fun handleUnitBenefitEditDone() {
        unitBenefitText.toDoubleOrNull()?.takeIf { it >= 0 }?.let { updateUnitBenefitImmediately(it) }
        isEditingUnitBenefit = false
    }

    fun handleTotalBenefitEditDone() {
        totalBenefitText.toDoubleOrNull()?.takeIf { it >= 0 }?.let { updateBenefitImmediately(it) }
        isEditingTotalBenefit = false
    }

    val colorUnite = Color(0xD8D9C3DC)

    ElevatedCard(modifier = Modifier.wrapContentWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Header ──────────────────────────────────────────────────────
            Text(
                text = "Bénéfice",
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9C27B0),
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // ── Bénéfice total ──────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditingTotalBenefit) {
                    OutlinedTextField(
                        value = totalBenefitText,
                        onValueChange = { totalBenefitText = it },
                        modifier = Modifier
                            .width(70.dp)
                            .focusRequester(totalBenefitFocusRequester),
                        label = { Text(String.format("%.0f", benefice), fontSize = 8.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { handleTotalBenefitEditDone() }),
                        singleLine = true
                    )
                } else {
                    Text(
                        text = String.format("%.0f", benefice),
                        modifier = Modifier
                            .background(Color(0xFF9C27B0))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .clickable { isEditingTotalBenefit = true },
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }

            // ── Bénéfice unitaire ───────────────────────────────────────────
            Box(contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isEditingUnitBenefit) {
                        OutlinedTextField(
                            value = unitBenefitText,
                            onValueChange = { unitBenefitText = it },
                            modifier = Modifier
                                .width(60.dp)
                                .focusRequester(focusRequester),
                            label = { Text(String.format("%.2f", beneficeUnitaire), fontSize = 8.sp) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { handleUnitBenefitEditDone() }),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = String.format("%.2f", beneficeUnitaire),
                            modifier = Modifier
                                .background(colorUnite)
                                .padding(2.dp)
                                .clickable { isEditingUnitBenefit = true },
                            color = Color.White,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
