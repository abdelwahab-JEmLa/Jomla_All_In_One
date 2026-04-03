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

    val prixAchat = prixAchatTariff?.prixCurrency ?: relative_Produit.prixAchat
    val prixVente = relative_Tariff.prixCurrency
    val nombreUnite = relative_Produit.nombreUniteInt

    // Calculate unit selling price
    val prixVenteUnitaire = if (nombreUnite > 0) prixVente / nombreUnite else 0.0

    var isEditingUnitPrice by remember { mutableStateOf(false) }
    var unitPriceText by remember { mutableStateOf("") }
    var isEditingTotalPrice by remember { mutableStateOf(false) }
    var totalPriceText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val totalPriceFocusRequester = remember { FocusRequester() }

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

    // Get tariff colors from the TypeChoisi enum
    val tariffColor = relative_Tariff.typeChoisi.couleur
    val tariffTextColor = relative_Tariff.typeChoisi.couleur_Text

    fun shouldCreateNewTariff(): Boolean {
        val currentTime = System.currentTimeMillis()
        val tariffCreationTime = relative_Tariff.creationTimestamps
        val timeDifferenceSeconds = (currentTime - tariffCreationTime) / 1000
        return true
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
    }

    fun handleTotalPriceEditDone() {
        val newTotalPrice = totalPriceText.toDoubleOrNull()
        if (newTotalPrice != null && newTotalPrice >= 0) {
            updateTotalPriceImmediately(newTotalPrice)
        }
        isEditingTotalPrice = false
    }

    // Card with both selling prices in column
    val its_Pour_Abdelwahab = (!currentApp_ItsNotWorkChezGrossisst_And_NotAdmin
            || relative_Tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client)
    val width =if (isEditingTotalPrice)100.dp else 60.dp
    ElevatedCard(
        modifier = Modifier
            .width(width)
            .padding(4.dp)
            .background(tariffColor)
    ) {
        Column(
            modifier = Modifier.padding(4.dp)

        ) {
            // Total selling price section at top
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tariffColor)
            ) {


                // Total price display/edit
                if (isEditingTotalPrice) {
                    OutlinedTextField(
                        value = totalPriceText,
                        onValueChange = { totalPriceText = it },
                        modifier = Modifier
                            .width(width)
                            .focusRequester(totalPriceFocusRequester),
                        label = { Text("${String.format("%.0f", prixVente)}", fontSize = 8.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (relative_Tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService) {
                                    val tariff_Prix_SupperGro_Et_PresentationService =
                                        aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
                                            .sortedByDescending {
                                                it.creationTimestamps
                                            }
                                            .findLast {
                                                it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService
                                                        && it.parent_M1Produit_KeyId == relative_Produit.keyID
                                            }
                                    val newPrice = totalPriceText.toDoubleOrNull() ?: prixVente

                                    val toastMsg: String
                                    if (tariff_Prix_SupperGro_Et_PresentationService != null) {
                                        aCentralFacade.repositorysMainSetter.update_M13TarificationInfos(
                                            tariff_Prix_SupperGro_Et_PresentationService.copy(
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

                            }
                        ),
                        singleLine = true
                    )
                } else {
                    Text(
                        String.format("%.0f", prixVente),
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                its_Pour_Abdelwahab.ifTrue {
                                    isEditingTotalPrice = true
                                    totalPriceText = ""
                                }
                            }
                            .background(tariffColor)
                        ,
                        color = tariffTextColor
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


                    // Unit price display/edit
                    if (isEditingUnitPrice) {
                        OutlinedTextField(
                            value = unitPriceText,
                            onValueChange = { unitPriceText = it },
                            modifier = Modifier
                                .width(width)
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
                                onDone = { handleUnitPriceEditDone() }
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            "${String.format("%.2f", prixVenteUnitaire)}",
                            modifier = Modifier
                                .padding(2.dp)
                                .clickable {
                                    its_Pour_Abdelwahab.ifTrue {
                                        isEditingUnitPrice = true
                                        unitPriceText = ""
                                    }
                                }
                                .background(tariffColor)
                            ,
                            color = tariffTextColor,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
