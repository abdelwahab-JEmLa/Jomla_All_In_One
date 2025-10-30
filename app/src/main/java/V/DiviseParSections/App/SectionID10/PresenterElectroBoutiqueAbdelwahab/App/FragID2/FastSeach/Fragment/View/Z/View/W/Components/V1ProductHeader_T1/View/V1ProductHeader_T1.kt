package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.V1ProductHeader_T1.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.Dialog_Choisire_Quantity_Modularized
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.Dialogs.CategorySelectionDialog
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur.Companion.ref
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProductHeader_T1(
    relative_Produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    isExpanded: Boolean,
) {
    val repositorysMainGetter = viewModel.aCentralFacade.repositorysMainGetter

    val listFiltered_M10OperationVentCouleurs_By_M1Produit by derivedStateOf {
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.get_ListFiltered_M10OperationVentCouleurs_By_M1Produit(
            relative_Produit
        )
    }
    var shouldShowCategoryDialog by remember { mutableStateOf(false) }

    val allNonTrouve =
        listFiltered_M10OperationVentCouleurs_By_M1Produit.isNotEmpty() && listFiltered_M10OperationVentCouleurs_By_M1Produit.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }

    val onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent =
        viewModel.getterFocusedVarsHandlerFacade.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent

    var shouldShowDialog_quantite_Boit_Par_Carton by remember { mutableStateOf(false) }
    var shouldShowDialog_quantite_Unite_Par_Boit by remember { mutableStateOf(false) }

    // State for editing product name (French)
    var isEditingName by remember { mutableStateOf(false) }
    var editingNameText by remember { mutableStateOf(relative_Produit.nom) }
    val focusRequester = remember { FocusRequester() }

    // State for editing Arabic name
    var isEditingArabicName by remember { mutableStateOf(false) }
    var editingArabicNameText by remember { mutableStateOf(relative_Produit.nomArab) }
    val focusRequesterArabic = remember { FocusRequester() }

    // Get category name from the categories map
    val categoriesMap =
        viewModel.aCentralFacade.repositorysMainGetter.repoM16CategorieProduit.datasValue.associateBy { it.id }
    val categoryName = relative_Produit.idParentCategorie?.let { categoryId ->
        categoriesMap[categoryId]?.nom
    } ?: "Sans Catégorie"

    fun update_produit(produit: ArticlesBasesStatsTable): Unit {
        repositorysMainSetter.upsert_M1Produit(
            produit
        )
    }

    // Function to save the edited French name
    fun saveEditedName() {
        if (editingNameText.isNotBlank() && editingNameText != relative_Produit.nom) {
            val updatedProduit = relative_Produit.copy(
                nom = editingNameText.trim(),
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(updatedProduit)
        }
        isEditingName = false
    }

    // Function to save the edited Arabic name
    fun saveEditedArabicName() {
        if (editingArabicNameText.isNotBlank() && editingArabicNameText != relative_Produit.nomArab) {
            val updatedProduit = relative_Produit.copy(
                nomArab = editingArabicNameText.trim(),
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(updatedProduit)
        }
        isEditingArabicName = false
    }

    // Function to cancel editing French name
    fun cancelEditingName() {
        editingNameText = relative_Produit.nom
        isEditingName = false
    }

    // Function to cancel editing Arabic name
    fun cancelEditingArabicName() {
        editingArabicNameText = relative_Produit.nomArab
        isEditingArabicName = false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (allNonTrouve) {
                        listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    } else {
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                )
            )
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .getSemanticsTag(
                    relative_Produit, "produit"
                )
                .getSemanticsTag(
                    nomVal = "onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent",
                    data = onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent
                )
                .getSemanticsTag(
                    nomVal = "ventOperationsForProduct",
                    data = listFiltered_M10OperationVentCouleurs_By_M1Produit
                )
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1: Product names and category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // CategoryTypeDisplay at the top
                    CategoryTypeDisplay(
                        produit = relative_Produit,
                        category = categoriesMap[relative_Produit.idParentCategorie],
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        repositorysMainSetter = repositorysMainSetter
                    ) { textValue ->
                        update_produit(
                            relative_Produit.copy(
                                nomMutable = textValue,
                                dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                            )
                        )
                    }

                    // French product name - now editable
                    if (isEditingName) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = editingNameText,
                                onValueChange = { editingNameText = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                label = { Text("Nom français") },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { editingNameText = "" }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Effacer le texte",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            )

                            // Save button
                            IconButton(
                                onClick = { saveEditedName() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Sauvegarder",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Cancel button
                            IconButton(
                                onClick = { cancelEditingName() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Annuler",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Request focus when entering edit mode
                        LaunchedEffect(isEditingName) {
                            if (isEditingName) {
                                focusRequester.requestFocus()
                            }
                        }
                    } else {
                        // Display mode - clickable to edit
                        Text(
                            text = relative_Produit.nom,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.6f
                            )
                            else MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editingNameText = relative_Produit.nom
                                    isEditingName = true
                                }
                        )
                    }

                    // Arabic product name - editable
                    if (isEditingArabicName && isExpanded) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = editingArabicNameText,
                                onValueChange = { editingArabicNameText = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequesterArabic),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                label = { Text("الاسم العربي") },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { editingArabicNameText = "" }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "مسح النص",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            )

                            // Save button
                            IconButton(
                                onClick = { saveEditedArabicName() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "حفظ",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Cancel button
                            IconButton(
                                onClick = { cancelEditingArabicName() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إلغاء",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Request focus when entering edit mode
                        LaunchedEffect(isEditingArabicName) {
                            if (isEditingArabicName) {
                                focusRequesterArabic.requestFocus()
                            }
                        }
                    } else {
                        // Display Arabic name - clickable to edit
                        if (relative_Produit.nomArab.isNotBlank() && isExpanded) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = relative_Produit.nomArab,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.5f
                                    )
                                    else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            editingArabicNameText = relative_Produit.nomArab
                                            isEditingArabicName = true
                                        }
                                )

                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تعديل الاسم العربي",
                                    tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.3f
                                    )
                                    else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            editingArabicNameText = relative_Produit.nomArab
                                            isEditingArabicName = true
                                        }
                                )
                            }
                        } else {
                            // Show placeholder for Arabic name if empty
                            isExpanded.ifTrue {
                                Text(
                                    text = "إضافة اسم عربي",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.3f
                                    )
                                    else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .clickable {
                                            editingArabicNameText = ""
                                            isEditingArabicName = true
                                        }
                                )
                            }
                        }
                    }
                    focusedValuesGetter.currentApp_ItsWorkChezGrossisst.ifFalse {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "Catégorie:",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                )
                                else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.6f
                                )
                                else MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    shouldShowCategoryDialog = true
                                }
                            )
                        }
                    }

                    if (allNonTrouve) {
                        Text(
                            text = "Non disponible",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
            isExpanded.ifTrue {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card_Produit_Nombre_Unites(
                        allNonTrouve = allNonTrouve,
                        produit = relative_Produit,
                        viewModel = viewModel
                    ) {
                        shouldShowDialog_quantite_Unite_Par_Boit = true
                    }

                    Card_StatueDuProduit(
                        relative_Produit = relative_Produit,
                    )

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (allNonTrouve) MaterialTheme.colorScheme.surface.copy(
                                alpha = 0.5f
                            )
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.padding(start = petitePaddine)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(petitePaddine)
                        ) {
                            IconButton(
                                onClick = {
                                    val currentMode =
                                        relative_Produit.setIN_Vent_Its_Quantity_Represent
                                    val newMode = currentMode.toggle()

                                    val currentVentOperations =
                                        listFiltered_M10OperationVentCouleurs_By_M1Produit
                                    val totalQuantitiesByColor =
                                        currentVentOperations.groupBy { it.parent_M3CouleurProduit_KeyID }
                                            .mapValues { entry -> entry.value.sumOf { it.quantity } }

                                    relative_Produit.apply {
                                        setIN_Vent_Its_Quantity_Represent = newMode
                                    }.also {
                                        repositorysMainGetter.repo1ProduitInfos.update(it)
                                    }

                                    viewModel.aCentralFacade.repositorysMainSetter.delete_ListM10OperationVentCouleur(
                                        currentVentOperations
                                    )

                                    if (totalQuantitiesByColor.isNotEmpty()) {
                                        val repo3CouleurProduitInfos =
                                            viewModel.getter.repo03CouleurProduitInfos
                                        val repo10OperationVentCouleur =
                                            viewModel.getter.repo10OperationVentCouleur
                                        val defaultVent =
                                            viewModel.getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()

                                        totalQuantitiesByColor.forEach { (colorKeyId, totalQuantity) ->
                                            val colorInfo =
                                                repo3CouleurProduitInfos.datasValue.find { it.keyID == colorKeyId }

                                            if (colorInfo != null && defaultVent != null && totalQuantity > 0) {
                                                val convertedQuantity = when (newMode) {
                                                    M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton -> {
                                                        if (currentMode == M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit) {
                                                            if (relative_Produit.quantite_Boit_Par_Carton > 0) {
                                                                (totalQuantity / relative_Produit.quantite_Boit_Par_Carton).coerceAtLeast(
                                                                    1
                                                                )
                                                            } else totalQuantity
                                                        } else totalQuantity
                                                    }

                                                    M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit -> {
                                                        if (currentMode == M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton) {
                                                            totalQuantity * relative_Produit.quantite_Boit_Par_Carton
                                                        } else totalQuantity
                                                    }
                                                }

                                                val newVent = defaultVent.copy(
                                                    keyID = getPushFireBase(ref),
                                                    parent_M1Produit_KeyId = relative_Produit.keyID,
                                                    parent_M1Produit_DebugInfos = relative_Produit.nom,
                                                    parent_M3CouleurProduit_KeyID = colorKeyId,
                                                    parent_M3CouleurProduit_DebugInfos = "${relative_Produit.nom}_${colorInfo.indexCouleurDansAncienProto}",
                                                    quantity = convertedQuantity,
                                                    etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ParentBonVentConfirme,
                                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                                )

                                                repo10OperationVentCouleur.addOrUpdateData(newVent)
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                val carton =
                                    relative_Produit.setIN_Vent_Its_Quantity_Represent == M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton

                                Icon(
                                    imageVector = if (carton) Icons.Default.Inventory2
                                    else Icons.Default.ViewModule,
                                    contentDescription = if (carton) "Mode carton activé" else "Mode unité activé",
                                    tint = when {
                                        allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.6f
                                        )

                                        carton -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.secondary
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    shouldShowDialog_quantite_Boit_Par_Carton = true
                                }, modifier = Modifier.size(36.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Numbers,
                                        contentDescription = "Quantity per carton",
                                        tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.6f
                                        )
                                        else MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${relative_Produit.quantite_Boit_Par_Carton}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.6f
                                        )
                                        else MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (shouldShowDialog_quantite_Unite_Par_Boit) {
            Dialog_Choisire_Quantity_Modularized(
                old_quantity = relative_Produit.nombreUniteInt,
                label = "nombreUniteInt",
            ) { new_Qyt ->
                if (new_Qyt != null) {
                    relative_Produit.apply {
                        nombreUniteInt = new_Qyt

                    }.also {
                        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(it)
                    }

                    viewModel.aCentralFacade.repositorysMainSetter.delete_ListM10OperationVentCouleur(
                        listFiltered_M10OperationVentCouleurs_By_M1Produit
                    )
                }
                shouldShowDialog_quantite_Unite_Par_Boit = false
            }
        }

        if (shouldShowDialog_quantite_Boit_Par_Carton) {
            Dialog_Choisire_Quantity_Modularized(
                old_quantity = relative_Produit.quantite_Boit_Par_Carton,
                label = "quantite_Boit_Par_Carton",
            ) { new_Qyt ->
                if (new_Qyt != null) {
                    relative_Produit.apply {
                        quantite_Boit_Par_Carton = new_Qyt
                    }.also {
                        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(it)
                    }
                }

                shouldShowDialog_quantite_Boit_Par_Carton = false
            }
        }

        // Category Selection Dialog
        if (shouldShowCategoryDialog) {
            CategorySelectionDialog(
                product = relative_Produit,
                onCategorySelected = { newCategoryId ->
                    if (newCategoryId != null) {
                        relative_Produit.copy(idParentCategorie = newCategoryId).also {
                            viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(
                                it
                            )
                        }
                    }
                    shouldShowCategoryDialog = false
                },
                onDismiss = { shouldShowCategoryDialog = false },
                onUpdateCategory = { categoryId, newName ->
                    categoriesMap[categoryId]?.copy(nom = newName)?.let { updatedCategory ->
                        viewModel.aCentralFacade.repositorysMainGetter.repoM16CategorieProduit.addOrUpdateData(
                            updatedCategory
                        )
                    }
                },
                categoriesMap = categoriesMap,
                availableCategories = categoriesMap.keys.toList()
            )
        }
    }
}

@Composable
fun Card_StatueDuProduit(
    relative_Produit: ArticlesBasesStatsTable,
    repositorysMainSetter: RepositorysMainSetter = koinInject()
) {
    fun update_produit(produit: ArticlesBasesStatsTable): Unit {
        repositorysMainSetter.upsert_M1Produit(
            produit
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp), // Reduced from 8.dp
            modifier = Modifier.padding(6.dp) // Reduced from 8.dp
        ) {
            Text(
                text = "C",
                style = MaterialTheme.typography.labelSmall, // Changed from labelMedium
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )

            Switch(
                checked = relative_Produit.its_Carton,
                onCheckedChange = { isChecked ->
                    val updatedProduit = relative_Produit.copy(
                        its_Carton = isChecked,
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                    update_produit(updatedProduit)
                },
                modifier = Modifier
                    .scale(0.8f), // Scale down the switch to make it smaller
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
