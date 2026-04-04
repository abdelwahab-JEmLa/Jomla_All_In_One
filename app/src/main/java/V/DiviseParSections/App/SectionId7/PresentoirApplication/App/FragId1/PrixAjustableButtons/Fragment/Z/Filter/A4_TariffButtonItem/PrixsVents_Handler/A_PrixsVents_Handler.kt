package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.PrixsVents_Handler

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import java.util.SortedMap

@SuppressLint("DefaultLocale")
@Composable
fun PrixsVents_Handler(
    relative_Produit: M01Produit,
    relative_Tariff: M13TarificationInfos,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,
    currentApp_ItsNotWorkChezGrossisst_And_NotAdmin: Boolean,
    prixachatDepuitPrixSuppergroEtPresentationservice: M13TarificationInfos?,
) {
    val typeTarification = relative_Tariff.typeChoisi
    val currentApp_Est_Admin = focusedValuesGetter.currentApp_Est_Admin

    var currentTariffPrice by remember(relative_Tariff.prixCurrency) {
        mutableStateOf(relative_Tariff.prixCurrency)
    }

    val m10OperationVentCouleurs =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .focused_ListM10OpeVentCouleur_Par_PD_M1Produit

    fun executeClickLogic() {
        repositorysMainSetter
            .saveTariff_Et_RelateIt_Au_Vents_Correspond(
                m13TarificationInfos_Pour_Produit = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                m10OperationVentCouleurs = m10OperationVentCouleurs,
                aCentralFacade = aCentralFacade
            )

        aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
            .dismisses_By_toggle_CurrentApp_activeDialogSearchM1Produit()
    }

    fun handelClick() {
        executeClickLogic()
    }

    fun handel_Add_Diminue_Prix(newPrix: Double, shouldCreateNew: Boolean) {
        val currentTime = System.currentTimeMillis()

        if (shouldCreateNew) {
            val newTariff = relative_Tariff.copy(
                prixCurrency = newPrix,
                creationTimestamps = currentTime,
                dernierTimeTampsSynchronisationAvecFireBase = currentTime
            )
            repositorysMainSetter.upsert_M13TarificationInfos(newTariff)
            currentTariffPrice = newPrix
        } else {
            currentTariffPrice = newPrix
            repositorysMainSetter.upsert_M13TarificationInfos(
                relative_Tariff.copy(
                    prixCurrency = newPrix,
                    dernierTimeTampsSynchronisationAvecFireBase = currentTime
                )
            )
        }
    }

    Column {
        Row(
            modifier = Modifier
                .semantics(mergeDescendants = true) {
                    set(value = relative_Tariff, key = SemanticsPropertyKey("relative_Tariff"))
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val couleurButton = typeTarification.couleur
            val textColor = typeTarification.couleur_Text

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Show different adjustment buttons based on tariff type and admin status
                if (currentApp_Est_Admin) {
                    when (typeTarification) {
                        M13TarificationInfos.TypeChoisi.Edited_Pour_Client -> {
                            // Use ProgressivePercentageAdjustmentCard for progressive pricing
                            ProgressivePercentageAdjustmentCard(
                                currentApp_Est_Admin = currentApp_Est_Admin,
                                allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                                relative_Produit = relative_Produit,
                                currentTariffPrice = currentTariffPrice,
                                onPriceChange = { newPrice, shouldCreateNew ->
                                    handel_Add_Diminue_Prix(newPrice, shouldCreateNew)
                                },
                                produit = relative_Produit,
                                typeTarification = typeTarification,
                                repositorysMainSetter = repositorysMainSetter,
                                onPercentageChange = { newPercentage ->
                                    // Recalculate the progressive price when percentage changes
                                    val prixDetaille = allTariffsGroupedAndSorted[M13TarificationInfos.TypeChoisi.Prix_Detaille]
                                        ?.maxByOrNull { it.creationTimestamps }?.prixCurrency
                                        ?: relative_Produit.prixVent

                                    val prixVent = relative_Produit.prixVent
                                    val priceDifference = prixDetaille - prixVent
                                    val adjustedPercentage = if (newPercentage == 50) 60 else newPercentage
                                    val progressiveAdjustment = priceDifference * (adjustedPercentage / 100.0)
                                    val newProgressivePrice = prixVent + progressiveAdjustment

                                    handel_Add_Diminue_Prix(newProgressivePrice, false)
                                }
                            )
                        }
                        else -> {
                            // Bénéfice client : visible uniquement si le prix de vente unitaire est défini
                            // bénéfice = clientPrixVentUnite * nombreUnite - prixVent (base)
                            val nombreUnite = relative_Produit.nombreUniteInt
                            val prixVentUnitaire =
                                if (nombreUnite > 0) currentTariffPrice / nombreUnite else 0.0

                            if (prixVentUnitaire != 0.0) {
                                BeneficeClientAdjustmentCard(
                                    relative_Produit = relative_Produit,
                                    relative_Tariff = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                                    onPriceChange_To_Change_Tariff_Prix = { newPrice, shouldCreateNew ->
                                        handel_Add_Diminue_Prix(newPrice, shouldCreateNew)
                                    }
                                )
                            }

                            BenificeAdjustmentButtons(
                                prixachatDepuitPrixSuppergroEtPresentationservice=prixachatDepuitPrixSuppergroEtPresentationservice,
                                allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                                relative_Produit = relative_Produit,
                                relative_Tariff = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                                onPriceChange = { newPrice, shouldCreateNew ->
                                    handel_Add_Diminue_Prix(newPrice, shouldCreateNew)
                                }
                            )
                        }
                    }
                }

                PrixVentAdjustmentButtons(
                    prixachatDepuitPrixSuppergroEtPresentationservice=prixachatDepuitPrixSuppergroEtPresentationservice,
                    allTariffsGroupedAndSorted = allTariffsGroupedAndSorted,
                    relative_Produit = relative_Produit,
                    relative_Tariff = relative_Tariff.copy(prixCurrency = currentTariffPrice),
                    onPriceChange = { newPrice, shouldCreateNew ->
                        handel_Add_Diminue_Prix(newPrice, shouldCreateNew)
                    },
                    currentApp_ItsNotWorkChezGrossisst_And_NotAdmin = currentApp_ItsNotWorkChezGrossisst_And_NotAdmin,
                )
            }

            FloatingActionButton(
                modifier = Modifier.width(20.dp),
                onClick = ::handelClick,
                containerColor = couleurButton
            ) {
                Text(
                    text = typeTarification.nomArabe,
                    color = textColor,
                    fontSize = 10.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun BenefitDisplayRow(
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,
    relative_Produit: M01Produit,
    currentTariffPrice: Double,
    onPriceChange: (Double, Boolean) -> Unit
) {
    val prixAchatTariff =
        allTariffsGroupedAndSorted[M13TarificationInfos.TypeChoisi.Tariff_Achat_Depuit_Grossisst]
            ?.maxByOrNull { it.creationTimestamps }

    val prixAchat = prixAchatTariff?.prixCurrency ?: relative_Produit.prixAchat
    val benefice = currentTariffPrice - prixAchat
    val nombreUnite = relative_Produit.nombreUniteInt
    val beneficeUnitaire = if (nombreUnite > 0) benefice / nombreUnite else 0.0

    var isEditingTotalBenefit by remember { mutableStateOf(false) }
    var totalBenefitText by remember { mutableStateOf("") }
    val totalBenefitFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingTotalBenefit) {
        if (isEditingTotalBenefit) {
            totalBenefitFocusRequester.requestFocus()
        }
    }

    val benefitAdjustmentValue = when {
        benefice < 10.0 -> 1.0
        benefice < 50.0 -> 5.0
        benefice < 200.0 -> 10.0
        benefice < 500.0 -> 25.0
        else -> 50.0
    }

    fun updateBenefitImmediately(newBenefit: Double) {
        val newSellingPrice = prixAchat + newBenefit
        onPriceChange(newSellingPrice, false)
    }

    fun handleTotalBenefitEditDone() {
        val newTotalBenefit = totalBenefitText.toDoubleOrNull()
        if (newTotalBenefit != null && newTotalBenefit >= 0) {
            updateBenefitImmediately(newTotalBenefit)
        }
        isEditingTotalBenefit = false
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        // Decrease total benefit button
        IconButton(
            onClick = {
                val newBenefit = (benefice - benefitAdjustmentValue).coerceAtLeast(0.0)
                updateBenefitImmediately(newBenefit)
            },
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = Color(0xFF9C27B0),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "تقليل الفائدة",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }

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
                        totalBenefitText = ""
                    },
                color = Color.White
            )
        }

        // Increase total benefit button
        IconButton(
            onClick = {
                val newBenefit = benefice + benefitAdjustmentValue
                updateBenefitImmediately(newBenefit)
            },
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = Color(0xFF9C27B0),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "زيادة الفائدة",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }

        // Show unit benefit as well for information
        Text(
            text = "وحدة: ${String.format("%.2f", beneficeUnitaire)}",
            modifier = Modifier
                .padding(start = 8.dp)
                .background(Color(0xD8D9C3DC))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            color = Color.White,
            fontSize = 8.sp
        )
    }
}
