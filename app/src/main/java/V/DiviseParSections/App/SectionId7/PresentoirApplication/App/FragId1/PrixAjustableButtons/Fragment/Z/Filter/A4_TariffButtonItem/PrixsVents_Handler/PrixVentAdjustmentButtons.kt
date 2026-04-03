package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.PrixsVents_Handler

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import java.util.SortedMap

@SuppressLint("DefaultLocale")
@Composable
fun PrixVentAdjustmentButtons(
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,
    relative_Produit: M01Produit,
    relative_Tariff: M13TarificationInfos,
    onPriceChange: (Double, Boolean) -> Unit,
    currentApp_ItsNotWorkChezGrossisst_And_NotAdmin: Boolean,
    aCentralFacade: ACentralFacade = koinInject()
) {
    val context = LocalContext.current

    val prixAchatTariff =
        allTariffsGroupedAndSorted[M13TarificationInfos.TypeChoisi.Tariff_Achat_Depuit_Grossisst]
            ?.maxByOrNull { it.creationTimestamps }

    val prixAchat   = prixAchatTariff?.prixCurrency ?: relative_Produit.prixAchat
    val prixVente   = relative_Tariff.prixCurrency
    val nombreUnite = relative_Produit.nombreUniteInt

    val prixVenteUnitaire = if (nombreUnite > 0) prixVente / nombreUnite else 0.0

    var isEditingUnitPrice  by remember { mutableStateOf(false) }
    var unitPriceText       by remember { mutableStateOf("") }
    var isEditingTotalPrice by remember { mutableStateOf(false) }
    var totalPriceText      by remember { mutableStateOf("") }
    val focusRequester           = remember { FocusRequester() }
    val totalPriceFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingUnitPrice)  { if (isEditingUnitPrice)  focusRequester.requestFocus() }
    LaunchedEffect(isEditingTotalPrice) { if (isEditingTotalPrice) totalPriceFocusRequester.requestFocus() }

    val tariffColor     = relative_Tariff.typeChoisi.couleur
    val tariffTextColor = relative_Tariff.typeChoisi.couleur_Text

    fun shouldCreateNewTariff() = true

    fun updateTotalPriceImmediately(newTotalPrice: Double) {
        onPriceChange(newTotalPrice, shouldCreateNewTariff())
    }

    fun updateUnitPriceImmediately(newUnitPrice: Double) {
        onPriceChange(newUnitPrice * nombreUnite, shouldCreateNewTariff())
    }

    fun handleUnitPriceEditDone() {
        unitPriceText.toDoubleOrNull()?.takeIf { it >= 0 }?.let { updateUnitPriceImmediately(it) }
        isEditingUnitPrice = false
    }

    fun handleTotalPriceEditDone() {
        totalPriceText.toDoubleOrNull()?.takeIf { it >= 0 }?.let { updateTotalPriceImmediately(it) }
        isEditingTotalPrice = false
    }

    val its_Pour_Abdelwahab = (!currentApp_ItsNotWorkChezGrossisst_And_NotAdmin
            || relative_Tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client)

    ElevatedCard(
        modifier = Modifier
            .width(90.dp)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Header ──────────────────────────────────────────────────────
            Text(
                text = "Prix vente",
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = tariffColor,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // ── Prix total ───────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditingTotalPrice) {
                    OutlinedTextField(
                        value = totalPriceText,
                        onValueChange = { totalPriceText = it },
                        modifier = Modifier
                            .width(75.dp)
                            .focusRequester(totalPriceFocusRequester),
                        label = { Text(String.format("%.0f", prixVente), fontSize = 8.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            if (relative_Tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService) {
                                val tariff_Prix_SupperGro =
                                    aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
                                        .sortedByDescending { it.creationTimestamps }
                                        .findLast {
                                            it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService
                                                    && it.parent_M1Produit_KeyId == relative_Produit.keyID
                                        }
                                val newPrice = totalPriceText.toDoubleOrNull() ?: prixVente
                                val toastMsg: String
                                if (tariff_Prix_SupperGro != null) {
                                    aCentralFacade.repositorysMainSetter.update_M13TarificationInfos(
                                        tariff_Prix_SupperGro.copy(
                                            prixCurrency = newPrice,
                                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                        )
                                    )
                                    toastMsg = "تم تحديث سعر السوبر جملة"
                                } else {
                                    aCentralFacade.repositorysMainSetter.add_M13TarificationInfos(
                                        M13TarificationInfos(
                                            parent_M1Produit_KeyId = relative_Produit.keyID,
                                            parent_M1Produit_DebugInfos = relative_Produit.getDebugInfos(),
                                            typeChoisi = M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService,
                                            prixCurrency = newPrice,
                                            creationTimestamps = System.currentTimeMillis(),
                                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                        )
                                    )
                                    toastMsg = "تم إضافة سعر السوبر جملة جديد"
                                }
                                Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                                isEditingTotalPrice = false
                            } else {
                                handleTotalPriceEditDone()
                            }
                        }),
                        singleLine = true
                    )
                } else {
                    Text(
                        text = String.format("%.0f", prixVente),
                        modifier = Modifier
                            .background(tariffColor)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .clickable {
                                its_Pour_Abdelwahab.ifTrue {
                                    isEditingTotalPrice = true
                                    totalPriceText = ""
                                }
                            },
                        color = tariffTextColor,
                        fontSize = 10.sp
                    )
                }
            }

            // ── Prix unitaire ────────────────────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isEditingUnitPrice) {
                        OutlinedTextField(
                            value = unitPriceText,
                            onValueChange = { unitPriceText = it },
                            modifier = Modifier
                                .width(65.dp)
                                .focusRequester(focusRequester),
                            label = { Text(String.format("%.2f", prixVenteUnitaire), fontSize = 8.sp) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { handleUnitPriceEditDone() }),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = String.format("%.2f", prixVenteUnitaire),
                            modifier = Modifier
                                .background(tariffColor.copy(alpha = 0.3f))
                                .padding(2.dp)
                                .clickable {
                                    its_Pour_Abdelwahab.ifTrue {
                                        isEditingUnitPrice = true
                                        unitPriceText = ""
                                    }
                                },
                            color = tariffTextColor,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
