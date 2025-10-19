package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.A.DownerBar.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Genere_Tariffs_currentApp_ItsWorkChezGrossisst
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
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

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun QuantityDisplay_Mo_F_(
    produit: ArticlesBasesStatsTable,
    allNonTrouve: Boolean,
    aCentralFacade: ACentralFacade,
    onShowColorsClick: (() -> Unit)? = null,
    isEditMode: Boolean = false,
    onEditModeChange: (Boolean) -> Unit = {}
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter
    val repositorysMainGetter = aCentralFacade.repositorysMainGetter
    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst

    val getterFocusedVarsHandlerFacade =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedValuesSetter =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter

    val totalQuantity by derivedStateOf {
        getterFocusedVarsHandlerFacade
            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { ventOperation ->
                ventOperation.parent_M1Produit_KeyId == produit.keyID
            }.sumOf { it.quantity }
    }

    // Get the SuperGros tariff for the current product
    val superGrosTariff = if (currentApp_ItsWorkChezGrossisst) {
        Genere_Tariffs_currentApp_ItsWorkChezGrossisst()
            .find_existing_Tariff_Grossist_SuperGros(aCentralFacade, produit)
    } else null

    // Get repositories
    val repo3CouleurProduitInfos = repositorysMainGetter.repo03CouleurProduitInfos
    val repo10OperationVentCouleur = repositorysMainGetter.repo10OperationVentCouleur
    val repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos

    var quantityInput by remember(totalQuantity) { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    // Auto-focus when entering edit mode
    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            focusRequester.requestFocus()
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditMode && totalQuantity > 0) {
            // Edit mode: Show outlined text field
            OutlinedTextField(
                value = quantityInput,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        quantityInput = newValue
                    }
                },
                modifier = Modifier
                    .width(80.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val newQuantity = quantityInput.toIntOrNull() ?: 0

                        // Update existing vent with new quantity
                        val existingVent = focusedValuesGetter
                            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                            .find { it.parent_M1Produit_KeyId == produit.keyID }

                        if (existingVent != null && newQuantity > 0) {
                            val updatedVent = existingVent.copy(
                                quantity = newQuantity,
                                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                            )
                            repo10OperationVentCouleur.addOrUpdateData(updatedVent)
                        } else if (newQuantity == 0 && existingVent != null) {
                            // Delete vent if quantity is 0
                            repo10OperationVentCouleur.delete(existingVent)
                        }

                        // Exit edit mode
                        onEditModeChange(false)
                    }
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        } else {
            // Normal mode: Show quantity card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (allNonTrouve) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    else if (totalQuantity > 0) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .clickable(enabled = !allNonTrouve) {
                        // Get product colors
                        val productColors = repo3CouleurProduitInfos.datasValue.filter {
                            it.parentBProduitInfosKeyID == produit.keyID
                        }

                        if (productColors.isNotEmpty()) {
                            // Check if there's already a vent operation for this product
                            val existingVent = focusedValuesGetter
                                .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                                .find { it.parent_M1Produit_KeyId == produit.keyID }

                            if (existingVent != null) {
                                // Second click: Enter edit mode instead of opening dialog
                                onEditModeChange(true)
                            } else {
                                // First click: Create new vent with quantity 1 on first color
                                val defaultVent = getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()

                                if (defaultVent != null) {
                                    val firstColor = productColors.first()

                                    // Check if tariff exists for this product and current client
                                    val existingTariff = repo13TarificationInfos.datasValue.find { tariff ->
                                        tariff.parent_M1Produit_KeyId == produit.keyID &&
                                                tariff.typeChoisi == TypeChoisi.Prix_Detaille
                                    }

                                    val newVent = defaultVent.copy(
                                        keyID = getPushFireBase(M10OperationVentCouleur.ref),
                                        parent_M1Produit_KeyId = produit.keyID,
                                        parent_M1Produit_DebugInfos = produit.nom,
                                        parent_M3CouleurProduit_KeyID = firstColor.keyID,
                                        parent_M3CouleurProduit_DebugInfos = "${produit.nom}_${firstColor.indexCouleurDansAncienProto}",
                                        quantity = 1,
                                        etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ParentBonVentConfirme,
                                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                    )

                                    repo10OperationVentCouleur.addOrUpdateData(newVent)

                                    // Create tariff if it doesn't exist
                                    if (existingTariff == null) {
                                        val defaultTariff = M13TarificationInfos.get_default_P0(
                                            produit,
                                            start_Prix_Depuit_Ancient = produit.prixAchat
                                        ).first

                                        repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                                            m13TarificationInfos_Pour_Produit = defaultTariff,
                                            m10OperationVentCouleurs = listOf(newVent),
                                            aCentralFacade = aCentralFacade
                                        )
                                    }
                                }
                            }
                        }
                    }
                    .getSemanticsTag(
                        nomVal = "dialogChoisireQuantityM1ProduitInfosDebugName",
                        data = focusedValuesGetter.currentActive_M9AppCompt?.dialogChoisireQuantityM1ProduitInfosDebugName
                    )
                    .getSemanticsTag_By_datas_A_Affiche_Au_Nom(
                        1,
                        "dialogChoisireQuantityM1ProduitInfosKeyID",
                        focusedValuesGetter.currentActive_M9AppCompt?.dialogChoisireQuantityM1ProduitInfosKeyID
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Total quantity",
                        tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = totalQuantity.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        val datasValue = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue

        val findTariff = datasValue.find { tariff ->
            tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                    tariff.parent_M1Produit_KeyId == produit.keyID
        }

        val default_Tariff = M13TarificationInfos.get_default_P0(produit, start_Prix_Depuit_Ancient = produit.prixAchat)
        val finale_Tariff = findTariff ?: default_Tariff.first

        val shouldUseManagerColors = finale_Tariff.laisse_Au_Gerant

        Card(
            modifier = Modifier
                .getSemanticsTag(nomVal = "repo13TarificationInfos", data = datasValue)
                .getSemanticsTag_By_datas_A_Affiche_Au_Nom(2, "finale_Tariff", finale_Tariff)
                .getSemanticsTag_By_datas_A_Affiche_Au_Nom(3, "findTariff", findTariff)
                .clickable(enabled = !allNonTrouve) {
                    repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                        m13TarificationInfos_Pour_Produit = finale_Tariff,
                        m10OperationVentCouleurs = focusedValuesGetter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit,
                        aCentralFacade = aCentralFacade
                    )

                    focusedVarsHandlerFacade.focusedValuesSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
                        produit
                    )
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    allNonTrouve -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    shouldUseManagerColors -> Color.White
                    else -> MaterialTheme.colorScheme.error
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val (depuit_Qui, tariffIcon) = if (findTariff != null) {
                    "Définie Par Ali" to Icons.Default.TrendingUp
                } else {
                    "Depuis Mon Old BaseDonnée" to Icons.Default.History
                }

                val displayText = if (currentApp_ItsWorkChezGrossisst) {
                    superGrosTariff?.let { tariff ->
                        "${tariff.prixCurrency} DA"
                    } ?: "غير متوفر"
                } else {
                    "اضغط لاظهار السعر"
                }

                Text(
                    text = displayText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        shouldUseManagerColors -> Color.Black
                        else -> MaterialTheme.colorScheme.onSecondary
                    }
                )

                Icon(
                    imageVector = tariffIcon,
                    contentDescription = if (findTariff != null) "Defined by Ali" else "From old database",
                    tint = when {
                        allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        shouldUseManagerColors -> Color.Black
                        else -> MaterialTheme.colorScheme.onSecondary
                    },
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
