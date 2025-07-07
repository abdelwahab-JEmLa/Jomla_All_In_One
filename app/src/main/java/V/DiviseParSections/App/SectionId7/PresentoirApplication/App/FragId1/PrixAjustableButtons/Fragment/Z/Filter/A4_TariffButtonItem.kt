package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetFocusedVars.Companion.getSemanticsTagFocucedVars
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import android.content.Context
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

@Composable
fun TariffButtonItem(
    produit: ArticlesBasesStatsTable,
    viewModel: TariffsButtonsViewModelSec7ID2,
    typeTarification: TypeChoisi,
    tariffs: List<M13TarificationInfos>,
    showLabels: Boolean,
    nombreUnite: Int = 10,
    context: Context,
    onClickPrixButton: (TypeChoisi, M13TarificationInfos, Context) -> Unit,
) {
    val latestTariff = tariffs.maxByOrNull { it.id }
    if (latestTariff == null) return

    // Fix: Add produit as a key to remember so it recomposes when product changes
    var latestTariffLocalData by remember(produit, latestTariff) {
        mutableStateOf(
            latestTariff.copy(
                parentM1ProduitInfosKeyId = produit.keyID,
                parentM1ProduitDebugInfos = produit.nom
            )
        )
    }

    // Also reset editing state when product changes
    var isEditingPrice by remember(produit) { mutableStateOf(false) }
    var editablePriceText by remember(produit) { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val isEditableTariff = typeTarification == TypeChoisi.DEFINI ||
            typeTarification == TypeChoisi.DefiniParGerant2

    fun handelClick() =
        viewModel.aCentralFacade.mainRepositorysSetterFacade
            .saveTariff_Et_RelateIt_Au_Vents_Correspond(
                focused_M13TarificationInfos_Pour_Produit = latestTariff,
                m10OperationVentCouleurs = viewModel.aCentralFacade.focusedVarsHandlerFacade.get
                    .focused_ListM10OpeVentCouleur_Par_PD_M1Produit
            )

    fun handlePriceEditDone() {
        val newPrice = editablePriceText.toDoubleOrNull()
        if (newPrice != null && newPrice >= 0) {
            latestTariffLocalData = latestTariffLocalData.copy(
                prixCurrency = newPrice
            )
            handelClick()
        }
        isEditingPrice = false
    }

    Row(
        modifier = Modifier
            .getSemanticsTag(produit.nom, "produit"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),

        ) {
        val couleurButton = typeTarification.couleur

        if (showLabels) {
            val typeName = typeTarification.nomArabe
            val prixCurrency = "${latestTariffLocalData.prixCurrency} "

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ElevatedCard {
                    val labelBackgroundColor = if (isEditableTariff) {
                        Color.Yellow
                    } else {
                        couleurButton
                    }

                    val labelTextColor = if (isEditableTariff) {
                        Color.Black
                    } else {
                        Color.White
                    }

                    if (isEditingPrice && isEditableTariff) {
                        OutlinedTextField(
                            value = editablePriceText,
                            onValueChange = { editablePriceText = it },
                            label = { Text("${latestTariffLocalData.prixCurrency}") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { handlePriceEditDone() }
                            ),
                            modifier = Modifier
                                .width(100.dp)
                                .focusRequester(focusRequester)
                        )

                        LaunchedEffect(isEditingPrice) {
                            if (isEditingPrice) {
                                focusRequester.requestFocus()
                            }
                        }
                    } else {
                        Text(
                            typeName,
                            modifier = Modifier
                                .width(100.dp)
                                .background(labelBackgroundColor)
                                .padding(4.dp)
                                .then(
                                    if (isEditableTariff) {
                                        Modifier.clickable {
                                            editablePriceText = ""
                                            isEditingPrice = true
                                        }
                                    } else {
                                        Modifier
                                    }
                                ),
                            color = labelTextColor,
                            fontSize = 14.sp,
                            maxLines = 2
                        )
                    }
                }

                // Show decrease button for both editable tariff types
                if (isEditableTariff) {
                    IconButton(
                        onClick = {
                            val newPrice =
                                (latestTariffLocalData.prixCurrency - 5.0).coerceAtLeast(0.0)
                            latestTariffLocalData = latestTariffLocalData.copy(
                                prixCurrency = newPrice
                            )
                        },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Diminuer le prix",
                            tint = Color.Black
                        )
                    }
                }

                ElevatedCard(
                    onClick = {
                        // Allow price increase for both editable tariff types
                        if (isEditableTariff) {
                            latestTariffLocalData = latestTariffLocalData.copy(
                                prixCurrency = latestTariffLocalData.prixCurrency + 5.0
                            )
                        }
                    }
                ) {
                    // Show plus sign for both editable tariff types
                    val pls = if (isEditableTariff) " +" else ""

                    val priceBackgroundColor = if (isEditableTariff) {
                        Color.Yellow
                    } else {
                        couleurButton
                    }

                    val priceTextColor = if (isEditableTariff) {
                        Color.Black
                    } else {
                        Color.White
                    }

                    Column {
                        Text(
                            "$prixCurrency$pls",
                            modifier = Modifier
                                .background(priceBackgroundColor)
                                .padding(4.dp),
                            color = priceTextColor
                        )

                        val unitPrice = latestTariffLocalData.prixCurrency / nombreUnite
                        Text(
                            "س.و: ${String.format("%.2f", unitPrice)}",
                            modifier = Modifier
                                .background(priceBackgroundColor.copy(alpha = 0.6f))
                                .padding(2.dp),
                            color = priceTextColor,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        val buttonBackgroundColor = if (isEditableTariff) {
            Color.Yellow
        } else {
            couleurButton
        }
        val getter = viewModel.aCentralFacade.focusedVarsHandlerFacade.get
        val listFocusedM10OpeVentCouleurParPrixDifineur =
            getter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit.toMutableList()

        FloatingActionButton(
            modifier = Modifier
                .size(40.dp)
                .getSemanticsTag(latestTariffLocalData, "latestTariffLocalData")
                .getSemanticsTagFocucedVars(getter),
            onClick = {

                handelClick()


                onClickPrixButton(typeTarification, latestTariffLocalData, context)
            },
            containerColor = buttonBackgroundColor
        ) {
            typeTarification.iconVector?.let { iconVector ->
                val iconColor = if (isEditableTariff) {
                    Color.Black
                } else {
                    Color.White
                }

                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconColor
                )
            }
        }
    }
}
