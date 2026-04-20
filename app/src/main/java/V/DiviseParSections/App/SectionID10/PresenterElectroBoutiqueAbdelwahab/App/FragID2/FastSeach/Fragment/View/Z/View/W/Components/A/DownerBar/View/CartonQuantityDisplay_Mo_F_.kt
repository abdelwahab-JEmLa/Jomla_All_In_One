package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.A.DownerBar.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.Dialog_Choisire_Quantity_Modularized
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import EntreApps.Shared.Models.Home.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun CartonQuantityDisplay_Mo_F_(
    produit: M01Produit,
    allNonTrouve: Boolean,
    aCentralFacade: ACentralFacade,
    isEditMode: Boolean = false,
    focusRequester: FocusRequester? = null,
    onRequestSearchFocus: () -> Unit = {},
    onEditModeChange: (Boolean) -> Unit = {}
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repositorysMainGetter = aCentralFacade.repositorysMainGetter
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter
    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val getterFocusedVarsHandlerFacade =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

    // Calculate total cartons from vent operations
    val totalCartons by derivedStateOf {
        val totalUnits = getterFocusedVarsHandlerFacade
            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { ventOperation ->
                ventOperation.parent_M1Produit_KeyId == produit.keyID
            }.sumOf { it.quantity }

        if (produit.quantite_Boit_Par_Carton > 0) {
            totalUnits / produit.quantite_Boit_Par_Carton
        } else {
            0
        }
    }

    val remainingUnits by derivedStateOf {
        val totalUnits = getterFocusedVarsHandlerFacade
            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { ventOperation ->
                ventOperation.parent_M1Produit_KeyId == produit.keyID
            }.sumOf { it.quantity }

        if (produit.quantite_Boit_Par_Carton > 0) {
            totalUnits % produit.quantite_Boit_Par_Carton
        } else {
            totalUnits
        }
    }

    val repo3CouleurProduitInfos = repositorysMainGetter.repo03CouleurProduitInfos
    val repo10OperationVentCouleur = repositorysMainGetter.repo10OperationVentCouleur
    val repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos

    var cartonInput by remember(totalCartons) { mutableStateOf("") }
    val localFocusRequester = remember { FocusRequester() }

    val affiche_Produit_OnGrid = ActiveCentralValues.get_Default().affiche_Produit_OnGrid

    // Auto-focus when entering edit mode
    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            delay(50)
            try {
                localFocusRequester.requestFocus()
            } catch (e: IllegalStateException) {
                // FocusRequester not initialized yet
            }
        }
    }

    fun handleCartonUpdate() {
        coroutineScope.launch {
            val newCartons = cartonInput.toIntOrNull() ?: 0
            val newTotalUnits = newCartons * produit.quantite_Boit_Par_Carton

            val existingVent = focusedValuesGetter
                .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                .find { it.parent_M1Produit_KeyId == produit.keyID }

            if (existingVent != null && newTotalUnits > 0) {
                val updatedVent = existingVent.copy(
                    quantity = newTotalUnits,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
                repo10OperationVentCouleur.addOrUpdateData(updatedVent)
            } else if (newTotalUnits == 0 && existingVent != null) {
                repo10OperationVentCouleur.delete(existingVent)
            }

            keyboardController?.hide()
            delay(100)
            onEditModeChange(false)
            delay(100)

            try {
                focusRequester?.requestFocus()
                onRequestSearchFocus()
            } catch (e: Exception) {
                onRequestSearchFocus()
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditMode && totalCartons > 0) {
            // Edit mode: Show outlined text field for cartons
            OutlinedTextField(
                value = cartonInput,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        cartonInput = newValue
                    }
                },
                modifier = Modifier
                    .width(80.dp)
                    .focusRequester(localFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        handleCartonUpdate()
                    }
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                label = { Text("Cartons") }
            )

        } else {
            if (currentApp_ItsWorkChezGrossisst && produit.quantite_Boit_Par_Carton == 1) {
                var shouldShowDialog_quantite_Boit_Par_Carton by remember { mutableStateOf(false) }

                affiche_Produit_OnGrid.ifFalse {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickable {
                                shouldShowDialog_quantite_Boit_Par_Carton = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "Changer quantité carton",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${produit.quantite_Boit_Par_Carton}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
                // Dialog to edit quantite_Boit_Par_Carton
                if (shouldShowDialog_quantite_Boit_Par_Carton) {
                    Dialog_Choisire_Quantity_Modularized(
                        old_quantity = produit.quantite_Boit_Par_Carton,
                        label = "quantite_Boit_Par_Carton",
                    ) { new_Qyt ->
                        if (new_Qyt != null) {
                            val updatedProduit = produit.copy(
                                quantite_Boit_Par_Carton = new_Qyt,
                                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                            )
                            repositorysMainGetter.repo1ProduitInfos.update(updatedProduit)
                        }
                        shouldShowDialog_quantite_Boit_Par_Carton = false
                    }
                }
            }

            // Main carton display card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (allNonTrouve) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    else if (totalCartons > 0) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .clickable(enabled = !allNonTrouve) {
                        val productColors = repo3CouleurProduitInfos.datasValue.filter {
                            it.parentBProduitInfosKeyID == produit.keyID
                        }

                        if (productColors.isNotEmpty()) {
                            val existingVent = focusedValuesGetter
                                .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                                .find { it.parent_M1Produit_KeyId == produit.keyID }

                            if (existingVent != null) {
                                // Enter edit mode immediately
                                onEditModeChange(true)
                            } else {
                                // First click: Create new vent with 1 carton worth of units
                                val defaultVent =
                                    getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()

                                if (defaultVent != null) {
                                    val firstColor = productColors.first()
                                    val unitsForOneCarton = produit.quantite_Boit_Par_Carton

                                    // Find existing tariff based on current mode
                                    val existingTariff = if (currentApp_ItsWorkChezGrossisst) {
                                        repo13TarificationInfos.datasValue.find { tariff ->
                                            tariff.parent_M1Produit_KeyId == produit.keyID &&
                                                    tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
                                        }
                                    } else {
                                        repo13TarificationInfos.datasValue.find { tariff ->
                                            tariff.parent_M1Produit_KeyId == produit.keyID &&
                                                    tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille
                                        }
                                    }

                                    // Determine or create the tariff first
                                    val tariffToUse = if (existingTariff != null) {
                                        existingTariff
                                    } else {
                                        val newTariff = if (currentApp_ItsWorkChezGrossisst) {
                                            M13TarificationInfos(
                                                typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                                                prixCurrency = produit.prixAchat,
                                                parent_M1Produit_KeyId = produit.keyID,
                                                parent_M1Produit_DebugInfos = produit.nom
                                            )
                                        } else {
                                            M13TarificationInfos.get_default_P0(
                                                produit,
                                                start_Prix_Depuit_Ancient = produit.prixAchat
                                            ).first
                                        }

                                        repo13TarificationInfos.add(newTariff)
                                        newTariff
                                    }

                                    // Create vent with tariff reference
                                    val newVent = defaultVent.copy(
                                        keyID = getPushFireBase(M10OperationVentCouleur.ref),
                                        parent_M1Produit_KeyId = produit.keyID,
                                        parent_M1Produit_DebugInfos = produit.nom,
                                        parent_M3CouleurProduit_KeyID = firstColor.keyID,
                                        parent_M3CouleurProduit_DebugInfos = "${produit.nom}_${firstColor.indexCouleurDansAncienProto}",
                                        parentM13TarificationKeyID = tariffToUse.keyID,
                                        parentM13TarificationDebugInfos = tariffToUse.getDebugInfos(),
                                        quantity = unitsForOneCarton,
                                        etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ParentBonVentConfirme,
                                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                    )

                                    repo10OperationVentCouleur.addOrUpdateData(newVent)

                                    // Link tariff to vent if it was newly created
                                    if (existingTariff == null) {
                                        repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                                            m13TarificationInfos_Pour_Produit = tariffToUse,
                                            m10OperationVentCouleurs = listOf(newVent),
                                            aCentralFacade = aCentralFacade
                                        )
                                    }
                                }
                            }
                        }
                    }
                    .getSemanticsTag(
                        nomVal = "cartonQuantityCard",
                        data = totalCartons
                    )
            ) {
                if (affiche_Produit_OnGrid && remainingUnits > 0) {
                    // Grid mode: Display cartons and remaining units vertically
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "Total cartons",
                                tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                else MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${totalCartons}C",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                else MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        // Remaining units below
                        Text(
                            text = "+${remainingUnits}B",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                        )
                    }
                } else {
                    // List mode OR no remaining units: Display horizontally
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = "Total cartons",
                            tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            else MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = "${totalCartons}C",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            else MaterialTheme.colorScheme.onSecondary
                        )

                        // Display remaining units if any
                        if (remainingUnits > 0) {
                            Text(
                                text = " ${remainingUnits}B",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
