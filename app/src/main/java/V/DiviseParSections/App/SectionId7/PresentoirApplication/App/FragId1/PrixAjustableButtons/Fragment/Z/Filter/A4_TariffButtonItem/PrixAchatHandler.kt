package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import Z_CodePartageEntreApps.Modules.DatesHandler.Companion.getTimeDifferenceInArabicWithMintes
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import java.util.SortedMap

@SuppressLint("DefaultLocale")
@Composable
fun PrixAchatHandler(
    relative_Produit: M01Produit,
    relative_Tariff: M13TarificationInfos,
    allTariffsGroupedAndSorted: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,

    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,

    showLabels: Boolean,
    nombreUnite: Int = 1,
    list_M13TarificationInfos: List<M13TarificationInfos> = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue,
    prixAchat_depuit_Prix_SupperGro_Et_PresentationService: M13TarificationInfos?,
) {

    val typeTarification = relative_Tariff.typeChoisi
    val currentApp_Est_Admin = focusedValuesGetter.currentApp_Est_Admin

    var editablePurchasePriceText by remember(relative_Produit) { mutableStateOf("") }
    var isEditingPurchasePrice by remember(relative_Produit) { mutableStateOf(false) }
    var isEditingUnitPrice by remember(relative_Produit) { mutableStateOf(false) }

    val purchasePriceFocusRequester = remember { FocusRequester() }

    fun toggleUnitPriceMode() {
        isEditingUnitPrice = !isEditingUnitPrice
        editablePurchasePriceText = ""
    }

    fun updateRecentRelatedTariffs(newPurchasePrice: Double) {
        val currentTime = System.currentTimeMillis()
        val oneMinuteAgo = currentTime - (60 * 1000)
        val oldPurchasePrice = relative_Produit.prixAchat

        val recentTariffs = allTariffsGroupedAndSorted.values.flatten().filter { tariff ->
            tariff.parent_M1Produit_KeyId == relative_Produit.keyID &&
                    tariff.typeChoisi in setOf(
                M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService,
                M13TarificationInfos.TypeChoisi.Prix_Detaille
            ) &&
                    tariff.creationTimestamps >= oneMinuteAgo
        }

        recentTariffs.forEach { tariff ->
            val ancienBenefice = tariff.prixCurrency - oldPurchasePrice
            val nouveauPrix = newPurchasePrice + ancienBenefice

            val updatedTariff = tariff.copy(
                prixCurrency = nouveauPrix,
                dernierTimeTampsSynchronisationAvecFireBase = currentTime
            )
            repositorysMainSetter.upsert_M13TarificationInfos(updatedTariff)
        }
    }

    fun createOrUpdateRelatedTariffs(newPurchasePrice: Double) {
        val currentTime = System.currentTimeMillis()
        val oldPurchasePrice = relative_Produit.prixAchat

        val targetTariffTypes = listOf(
            M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService,
            M13TarificationInfos.TypeChoisi.Prix_Detaille
        )

        targetTariffTypes.forEach { tariffType ->
            val existingTariff = allTariffsGroupedAndSorted[tariffType]
                ?.filter { it.parent_M1Produit_KeyId == relative_Produit.keyID }
                ?.maxByOrNull { it.creationTimestamps }

            if (existingTariff != null) {
                // Tarif existant: préserver l'ancien bénéfice
                val ancienBenefice = existingTariff.prixCurrency - oldPurchasePrice
                val nouveauPrix = newPurchasePrice + ancienBenefice

                val updatedTariff = existingTariff.copy(
                    prixCurrency = nouveauPrix.coerceAtLeast(newPurchasePrice),
                    dernierTimeTampsSynchronisationAvecFireBase = currentTime
                )
                repositorysMainSetter.upsert_M13TarificationInfos(updatedTariff)
            } else {
                val beneficeFixe = when (tariffType) {
                    M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService -> 0.0
                    M13TarificationInfos.TypeChoisi.Prix_Detaille -> 0.0
                    else -> 0.0
                }

                val newTariff = M13TarificationInfos(
                    parent_M1Produit_KeyId = relative_Produit.keyID,
                    parent_M1Produit_DebugInfos = relative_Produit.getDebugInfos(),
                    typeChoisi = tariffType,
                    prixCurrency = newPurchasePrice + beneficeFixe,
                    creationTimestamps = currentTime,
                    dernierTimeTampsSynchronisationAvecFireBase = currentTime
                )
                repositorysMainSetter.add_M13TarificationInfos(newTariff)
            }
        }
    }

    fun handel_Add_Diminue_Prix(newPrix: Double) {
        // Met à jour le prix d'achat du produit
        repositorysMainSetter.upsert_M1Produit(
            relative_Produit.copy(
                prixAchat = newPrix,
                prixAchatDernierTimeTempUpdate = System.currentTimeMillis()
            )
        )

        // Met à jour les tarifs récents
        updateRecentRelatedTariffs(newPrix)

        // Créé ou met à jour tous les tarifs SuperGros et Détaille
        createOrUpdateRelatedTariffs(newPrix)
    }

    fun handlePurchasePriceEditDone() {
        val newPurchasePrice = editablePurchasePriceText.toDoubleOrNull()
        if (newPurchasePrice != null && newPurchasePrice >= 0) {
            val finalPrice = if (isEditingUnitPrice) {
                newPurchasePrice * nombreUnite
            } else {
                newPurchasePrice
            }
            handel_Add_Diminue_Prix(finalPrice)
        }
        isEditingPurchasePrice = false
        isEditingUnitPrice = false
    }

    val timeDifference = remember(relative_Produit.prixAchatDernierTimeTempUpdate) {
        getTimeDifferenceInArabicWithMintes(relative_Produit.prixAchatDernierTimeTempUpdate)
    }

    Column {
        if (showLabels && relative_Produit.prixAchatDernierTimeTempUpdate != 0L) {
            Text(
                text = "آخر تحديث: $timeDifference",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            modifier = Modifier
                .getSemanticsTag(nomVal = "produit", data = relative_Produit.nom),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val couleurButton = Color.Cyan

            if (showLabels) {
                val typeName = typeTarification.nomArabe

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ElevatedCard {
                        if (isEditingPurchasePrice) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .width(100.dp)
                                    .focusRequester(purchasePriceFocusRequester),
                                value = editablePurchasePriceText,
                                onValueChange = { newInput ->
                                    editablePurchasePriceText = newInput
                                },
                                label = {
                                    Text(
                                        if (isEditingUnitPrice) {
                                            "Prix unitaire: ${
                                                String.format(
                                                    "%.2f",
                                                    relative_Produit.prixAchat / nombreUnite
                                                )
                                            }"
                                        } else {
                                            "Prix d'achat: ${relative_Produit.prixAchat}"
                                        }
                                    )
                                },
                                leadingIcon = {
                                    IconButton(
                                        onClick = {
                                            toggleUnitPriceMode()
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
                                    onDone = { handlePurchasePriceEditDone() }
                                ),
                            )

                            LaunchedEffect(isEditingPurchasePrice) {
                                if (isEditingPurchasePrice) {
                                    purchasePriceFocusRequester.requestFocus()
                                }
                            }
                        } else {
                            Text(
                                typeName,
                                modifier = Modifier
                                    .width(100.dp)
                                    .background(couleurButton)
                                    .padding(4.dp)
                                    .then(
                                        if (currentApp_Est_Admin) {
                                            Modifier.clickable {
                                                editablePurchasePriceText = ""
                                                isEditingPurchasePrice = true
                                                isEditingUnitPrice = false
                                            }
                                        } else {
                                            Modifier
                                        }
                                    ),
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 2
                            )
                        }
                    }

                    val decrease_Value = if (relative_Produit.prixAchat < 200.0) 1.0 else 5.0

                    if (currentApp_Est_Admin) {
                        IconButton(
                            onClick = {
                                handel_Add_Diminue_Prix(relative_Produit.prixAchat - decrease_Value)
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
                            if (currentApp_Est_Admin) {
                                handel_Add_Diminue_Prix(relative_Produit.prixAchat + decrease_Value)
                            }
                        }
                    ) {
                        val pls = if (currentApp_Est_Admin) " +" else ""

                        Column {
                            Text(
                                "${prixAchat_depuit_Prix_SupperGro_Et_PresentationService?.prixCurrency}$pls",
                                modifier = Modifier
                                    .background(couleurButton)
                                    .padding(4.dp),
                                color = Color.Black
                            )

                            val unitPrice = relative_Produit.prixAchat / nombreUnite
                            Text(
                                "س.و: ${String.format("%.2f", unitPrice)}",
                                modifier = Modifier
                                    .background(couleurButton.copy(alpha = 0.6f))
                                    .padding(2.dp),
                                color = Color.Black,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                modifier = Modifier.size(40.dp),
                onClick = {},
                containerColor = couleurButton
            ) {
                typeTarification.iconVector?.let { iconVector ->
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
        }
    }
}
