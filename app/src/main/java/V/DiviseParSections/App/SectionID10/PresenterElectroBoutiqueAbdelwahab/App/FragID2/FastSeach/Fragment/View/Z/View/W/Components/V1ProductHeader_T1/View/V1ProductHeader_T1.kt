package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.V1ProductHeader_T1.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.Dialog_Choisire_Quantity_Modularized
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.Dialogs.CategorySelectionDialog
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur.Companion.ref
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProductHeader_T1(
    relative_Produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    aCentralFacade: ACentralFacade= koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
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

    // Get category name from the categories map
    val categoriesMap = viewModel.aCentralFacade.repositorysMainGetter.repoM16CategorieProduit.datasValue.associateBy { it.id }
    val categoryName = relative_Produit.idParentCategorie?.let { categoryId ->
        categoriesMap[categoryId]?.nom
    } ?: "Sans Catégorie"
    fun update_produit(produit:ArticlesBasesStatsTable): Unit {
        repositorysMainSetter.upsert_M1Produit(
            produit
        )
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
            // Row 1: Product name and category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = relative_Produit.nom,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Catégorie: $categoryName",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )

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
                    onUpdateProduit = ::update_produit
                )

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (allNonTrouve) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        IconButton(
                            // FIXED: Properly handle carton toggle with quantity recalculation
                            onClick = {
                                val currentMode = relative_Produit.setIN_Vent_Its_Quantity_Represent
                                val newMode = currentMode.toggle()

                                // Store current total quantities before mode change
                                val currentVentOperations = listFiltered_M10OperationVentCouleurs_By_M1Produit
                                val totalQuantitiesByColor = currentVentOperations.groupBy { it.parent_M3CouleurProduit_KeyID }
                                    .mapValues { entry -> entry.value.sumOf { it.quantity } }

                                // Update product mode
                                relative_Produit.apply {
                                    setIN_Vent_Its_Quantity_Represent = newMode
                                }.also {
                                    repositorysMainGetter.repo1ProduitInfos.update(it)
                                }

                                // Delete existing operations
                                viewModel.aCentralFacade.repositorysMainSetter.delete_ListM10OperationVentCouleur(
                                    currentVentOperations
                                )

                                // Recreate operations with converted quantities if there were any
                                if (totalQuantitiesByColor.isNotEmpty()) {
                                    val repo3CouleurProduitInfos = viewModel.getter.repo03CouleurProduitInfos
                                    val repo10OperationVentCouleur = viewModel.getter.repo10OperationVentCouleur
                                    val defaultVent = viewModel.getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()

                                    totalQuantitiesByColor.forEach { (colorKeyId, totalQuantity) ->
                                        val colorInfo = repo3CouleurProduitInfos.datasValue.find { it.keyID == colorKeyId }

                                        if (colorInfo != null && defaultVent != null && totalQuantity > 0) {
                                            // Convert quantity based on new mode
                                            val convertedQuantity = when (newMode) {
                                                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton -> {
                                                    // Converting from units (boit) to cartons
                                                    if (currentMode == M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit) {
                                                        // Convert units to cartons
                                                        if (relative_Produit.quantite_Boit_Par_Carton > 0) {
                                                            (totalQuantity / relative_Produit.quantite_Boit_Par_Carton).coerceAtLeast(1)
                                                        } else totalQuantity
                                                    } else totalQuantity
                                                }
                                                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit -> {
                                                    // Converting from cartons to units (boit)
                                                    if (currentMode == M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton) {
                                                        // Convert cartons to units
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
                                    allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                    relative_Produit.copy(idParentCategorie = newCategoryId).also {
                        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(it)
                    }
                    shouldShowCategoryDialog = false
                },
                onDismiss = { shouldShowCategoryDialog = false },
                onUpdateCategory = { categoryId, newName ->
                    categoriesMap[categoryId]?.copy(nom = newName)?.let { updatedCategory ->
                        viewModel.aCentralFacade.repositorysMainGetter.repoM16CategorieProduit.addOrUpdateData(updatedCategory)
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
    onUpdateProduit: (ArticlesBasesStatsTable) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Carton:",
                style = MaterialTheme.typography.labelMedium,
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
                    onUpdateProduit(updatedProduit)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Text(
                text = if (relative_Produit.its_Carton) "Oui" else "Non",
                style = MaterialTheme.typography.labelSmall,
                color = if (relative_Produit.its_Carton)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
