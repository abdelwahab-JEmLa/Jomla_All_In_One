package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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

@SuppressLint("DefaultLocale")
@Composable
fun TariffButtonItem(
    produit: ArticlesBasesStatsTable,
    viewModel: TariffsButtonsViewModelSec7ID2,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    typeTarification: TypeChoisi,
    tariffs: List<M13TarificationInfos>,
    showLabels: Boolean,
    nombreUnite: Int = 1,
    context: Context,
    onClickPrixButton: (TypeChoisi, M13TarificationInfos, Context) -> Unit,
) {
    val currentApp_Est_Admin = focusedValuesGetter.currentApp_Est_Admin
    val latestTariff = tariffs.maxByOrNull { it.creationTimestamps }
    if (latestTariff == null) return

    var latestTariffLocalData by remember(
        produit,
        latestTariff,
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .activeOnVent_M2Client,
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .activeonVent_M8BonVent
    ) {
        mutableStateOf(
            latestTariff.copy(
                parent_M1Produit_KeyId = produit.keyID,
                parent_M1Produit_DebugInfos = produit.nom,
                parent_M2Client_KeyId = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                    .activeOnVent_M2Client?.keyID ?: "null",
                parent_M2Client_DebugInfos = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                    .activeOnVent_M2Client?.get_DebugInfos() ?: "null",
                parent_M8BonVent_KeyId = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                    .activeonVent_M8BonVent?.keyID ?: "null",
                parent_M8BonVent_DebugInfos = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                    .activeonVent_M8BonVent?.get_DebugInfos() ?: "null",
            )
        )
    }

    val m10OperationVentCouleurs =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .focused_ListM10OpeVentCouleur_Par_PD_M1Produit

    var editablePriceText by remember(produit) { mutableStateOf("") }
    var isEditingPrice by remember(produit) { mutableStateOf(false) }
    var isEditingUnitPrice by remember(produit) { mutableStateOf(false) }

    var editablePurchasePriceText by remember(produit) { mutableStateOf("") }
    var isEditingPurchasePrice by remember(produit) { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val purchasePriceFocusRequester = remember { FocusRequester() }

    val isEditableTariff = typeTarification == TypeChoisi.DEFIN_OLd ||
            typeTarification == TypeChoisi.DefiniParGerant

    val isPurchasePriceTariff = typeTarification == TypeChoisi.Tariff_Achat_Depuit_Grossisst

    // Hide purchase price tariff if user is not admin
    if (isPurchasePriceTariff && !currentApp_Est_Admin) {
        return
    }

    fun handelClick() {
        viewModel.aCentralFacade.repositorysMainSetter
            .saveTariff_Et_RelateIt_Au_Vents_Correspond(
                m13TarificationInfos_Pour_Produit = latestTariffLocalData,
                m10OperationVentCouleurs = m10OperationVentCouleurs
            )

        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
            .dismisses_By_toggle_CurrentApp_activeDialogSearchM1Produit()
    }

    fun handlePriceEditDone() {
        val newPrice = editablePriceText.toDoubleOrNull()
        if (newPrice != null && newPrice >= 0) {
            val finalPrice = if (isEditingUnitPrice) {
                newPrice * nombreUnite
            } else {
                newPrice
            }
            latestTariffLocalData = latestTariffLocalData.copy(
                prixCurrency = finalPrice
            )
            handelClick()
        }
        isEditingPrice = false
        isEditingUnitPrice = false
    }

    fun handlePurchasePriceEditDone() {
        val newPurchasePrice = editablePurchasePriceText.toDoubleOrNull()
        if (newPurchasePrice != null && newPurchasePrice >= 0) {
            // Update the product's purchase price
            val updatedProduit = produit.copy(
                prixAchat = newPurchasePrice,
                prixAchatDernierTimeTempUpdate = System.currentTimeMillis()
            )

            // Update the product in the repository
            viewModel.aCentralFacade.repositorysMainSetter.update_M1Produit(updatedProduit)

            latestTariffLocalData = latestTariffLocalData.copy(
                prixCurrency = newPurchasePrice
            )
            handelClick()
        }
        isEditingPurchasePrice = false
    }
    Row(
        modifier = Modifier
            .getSemanticsTag(nomVal = "produit", data = produit.nom),
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
                    } else if (isPurchasePriceTariff) {
                        Color.Cyan // Different color for purchase price
                    } else {
                        couleurButton
                    }

                    val labelTextColor = if (isEditableTariff || isPurchasePriceTariff) {
                        Color.Black
                    } else {
                        Color.White
                    }

                    // Handle purchase price editing
                    if (isEditingPurchasePrice && isPurchasePriceTariff) {
                        OutlinedTextField(
                            modifier = Modifier
                                .getSemanticsTag(
                                    nomVal = "editablePurchasePriceText",
                                    data = editablePurchasePriceText
                                )
                                .width(100.dp)
                                .focusRequester(purchasePriceFocusRequester),
                            value = editablePurchasePriceText,
                            onValueChange = { newInput ->
                                editablePurchasePriceText = newInput
                            },
                            label = {
                                Text("Prix d'achat: ${produit.prixAchat}")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingCart,
                                    contentDescription = "Prix d'achat",
                                    tint = Color.Blue
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { handlePurchasePriceEditDone() }
                            ),
                        )

                        LaunchedEffect(isEditingPurchasePrice) {
                            if (isEditingPurchasePrice) {
                                purchasePriceFocusRequester.requestFocus()
                            }
                        }
                    }
                    // Handle selling price editing
                    else if (isEditingPrice && isEditableTariff) {
                        val nombreUniteInt = produit.nombreUniteInt

                        OutlinedTextField(
                            modifier = Modifier
                                .getSemanticsTag(
                                    nomVal = "editablePriceText",
                                    data = editablePriceText
                                )
                                .width(100.dp)
                                .focusRequester(focusRequester),
                            value = editablePriceText,
                            onValueChange = { newInput ->
                                // Fixed: Direct assignment of user input
                                editablePriceText = newInput
                            },

                            label = {
                                Text(
                                    if (isEditingUnitPrice) {
                                        "Prix unitaire: ${
                                            String.format(
                                                "%.2f",
                                                latestTariffLocalData.prixCurrency / nombreUnite
                                            )
                                        }"
                                    } else {
                                        "Prix total: ${latestTariffLocalData.prixCurrency}"
                                    }
                                )
                            },
                            leadingIcon = {
                                IconButton(
                                    onClick = {
                                        isEditingUnitPrice = !isEditingUnitPrice
                                        editablePriceText = ""
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Calculate,
                                        contentDescription = if (isEditingUnitPrice) "Éditer prix total" else "Éditer prix unitaire",
                                        tint = if (isEditingUnitPrice) Color.Blue else Color.Gray
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { handlePriceEditDone() }
                            ),
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
                                            isEditingUnitPrice = false
                                        }
                                    } else if (isPurchasePriceTariff) {
                                        Modifier.clickable {
                                            // Start purchase price editing
                                            editablePurchasePriceText = ""
                                            isEditingPurchasePrice = true
                                        }
                                    } else {
                                        Modifier
                                    }
                                ),
                            color = if (isEditableTariff || isPurchasePriceTariff) {
                                Color.Black
                            } else {
                                typeTarification.couleur_Text  // Use the couleur_Text from TypeChoisi enum
                            },
                            fontSize = 14.sp,
                            maxLines = 2
                        )
                    }
                }

                // Show decrease button for both editable tariff types and purchase price
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
                } else if (isPurchasePriceTariff) {
                    IconButton(
                        onClick = {
                            val newPrice = (produit.prixAchat - 5.0).coerceAtLeast(0.0)
                            val updatedProduit = produit.copy(
                                prixAchat = newPrice,
                                prixAchatDernierTimeTempUpdate = System.currentTimeMillis()
                            )
                            viewModel.aCentralFacade.repositorysMainSetter.update_M1Produit(
                                updatedProduit
                            )
                            latestTariffLocalData =
                                latestTariffLocalData.copy(prixCurrency = newPrice)
                        },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Diminuer le prix d'achat",
                            tint = Color.Black
                        )
                    }
                }

                ElevatedCard(
                    onClick = {
                        // Allow price increase for editable tariffs
                        if (isEditableTariff) {
                            latestTariffLocalData = latestTariffLocalData.copy(
                                prixCurrency = latestTariffLocalData.prixCurrency + 5.0
                            )
                        } else if (isPurchasePriceTariff) {
                            val newPrice = produit.prixAchat + 5.0
                            val updatedProduit = produit.copy(
                                prixAchat = newPrice,
                                prixAchatDernierTimeTempUpdate = System.currentTimeMillis()
                            )
                            viewModel.aCentralFacade.repositorysMainSetter.update_M1Produit(
                                updatedProduit
                            )
                            latestTariffLocalData =
                                latestTariffLocalData.copy(prixCurrency = newPrice)
                        }
                    }
                ) {
                    // Show plus sign for editable tariffs and purchase price
                    val pls = if (isEditableTariff || isPurchasePriceTariff) " +" else ""

                    val priceBackgroundColor = if (isEditableTariff) {
                        Color.Yellow
                    } else if (isPurchasePriceTariff) {
                        Color.Cyan
                    } else {
                        couleurButton
                    }

                    val priceTextColor = if (isEditableTariff || isPurchasePriceTariff) {
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
        } else if (isPurchasePriceTariff) {
            Color.Cyan
        } else {
            couleurButton
        }

        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = {
                handelClick()
                onClickPrixButton(typeTarification, latestTariffLocalData, context)
            },
            containerColor = buttonBackgroundColor
        ) {
            typeTarification.iconVector?.let { iconVector ->
                val iconColor = if (isEditableTariff || isPurchasePriceTariff) {
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
