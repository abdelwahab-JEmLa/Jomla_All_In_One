package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.B_ProductGroup.ProductHeader_SemiModularized

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.Dialog_Choisire_Quantity_Modularized
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.Dialogs.CategorySelectionDialog
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
@SuppressLint("UnrememberedMutableState")
@Composable
fun ProductHeader_SemiModularized(
    relative_M1Produit: M01Produit,
    viewModel: ZViewModel_Sec1Frag3,
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>,
) {
    val listFiltered_M10OperationVentCouleurs_By_M1Produit by derivedStateOf {
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .get_ListFiltered_M10OperationVentCouleurs_By_M1Produit(
                relative_M1Produit
            )
    }

    val allNonTrouve =
        listFiltered_M10OperationVentCouleurs_By_M1Produit.isNotEmpty() && listFiltered_M10OperationVentCouleurs_By_M1Produit.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }

    val onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent

    val hasNonTrouve =
        listFiltered_M10OperationVentCouleurs_By_M1Produit.any { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }

    var shouldShowDialog_quantite_Boit_Par_Carton by remember { mutableStateOf(false) }
    var shouldShowDialog_quantite_Unite_Par_Boit by remember { mutableStateOf(false) }
    var shouldShowCategoryDialog by remember { mutableStateOf(false) }

    // Get category name from the categories map
    val categoriesMap = viewModel.aCentralFacade.repositorysMainGetter.repoM16CategorieProduit.datasValue.associateBy { it.id }
    val categoryName = relative_M1Produit.idParentCategorie?.let { categoryId ->
        categoriesMap[categoryId]?.nom
    } ?: "Sans Catégorie"

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
        // Main vertical layout: Header info on top, actions on bottom
        Column(
            modifier = Modifier
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
            // Top section: Product name and category in a prominent display
            ProductInfoSection(
                productName = relative_M1Produit.nom,
                categoryName = categoryName,
                allNonTrouve = allNonTrouve,
                onCategoryClick = { shouldShowCategoryDialog = true }
            )

            // Bottom section: Units, cartons and actions in a row
            ActionsSection(
                relative_M1Produit = relative_M1Produit,
                allNonTrouve = allNonTrouve,
                hasNonTrouve = hasNonTrouve,
                viewModel = viewModel,
                relative_List_M10OperationVentCouleur = relative_List_M10OperationVentCouleur,
                onShowUnitsDialog = { shouldShowDialog_quantite_Unite_Par_Boit = true },
                onShowCartonsDialog = { shouldShowDialog_quantite_Boit_Par_Carton = true }
            )
        }

        // Dialogs
        if (shouldShowDialog_quantite_Unite_Par_Boit) {
            Dialog_Choisire_Quantity_Modularized(
                old_quantity = relative_M1Produit.nombreUniteInt,
                label = "nombreUniteInt",
            ) { new_Qyt ->
                if (new_Qyt != null) {
                    relative_M1Produit.apply {
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
                old_quantity = relative_M1Produit.quantite_Boit_Par_Carton,
                label = "quantite_Boit_Par_Carton",
            ) { new_Qyt ->
                if (new_Qyt != null) {
                    relative_M1Produit.apply {
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
                product = relative_M1Produit,
                onCategorySelected = { newCategoryId ->
                    if (newCategoryId != null) {
                        relative_M1Produit.copy(idParentCategorie = newCategoryId).also {
                            viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(it)
                        }
                    }
                    shouldShowCategoryDialog = false
                },
                onDismiss = { shouldShowCategoryDialog = false },
                onUpdateCategory = { categoryId, newName ->
                    // Handle category name update if needed
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
private fun Card_Produit_Nombre_Unites_Enhanced(
    allNonTrouve: Boolean,
    relative_Produit: M01Produit,
    onClick_PourOuvrireDialog: () -> Unit,
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter
) {
    var toggleState by remember { mutableStateOf(relative_Produit.afficheUniteAuPrint) }

    fun clickHandel() {
        toggleState = !toggleState
        repositorysMainSetter.upsert_M1Produit(
            relative_Produit.copy(afficheUniteAuPrint = toggleState)
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allNonTrouve)
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(petitePaddine),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Text(
                text = "Unités",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Quantity display
                Column {
                    Text(
                        text = "Nombre",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${relative_Produit.nombreUniteInt}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onClick_PourOuvrireDialog() }
                    )
                }

                // Print toggle
                IconButton(
                    onClick = { clickHandel() },
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (toggleState) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    Icon(
                        imageVector = if (toggleState) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = if (toggleState) "Print units enabled" else "Print units disabled",
                        tint = if (toggleState) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Card_Controls_Enhanced(
    allNonTrouve: Boolean,
    hasNonTrouve: Boolean,
    relative_M1Produit: M01Produit,
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>,
    viewModel: ZViewModel_Sec1Frag3,
    onShowCartonsDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repositorysMainGetter = viewModel.aCentralFacade.repositorysMainGetter

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allNonTrouve)
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(petitePaddine),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Text(
                text = "Contrôles",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Display toggle button
                IconButton(
                    onClick = {
                        val toggled_setIN_Vent_Its_Quantity_Represent =
                            relative_M1Produit.setIN_Vent_Its_Quantity_Represent.toggle()

                        relative_M1Produit.apply {
                            setIN_Vent_Its_Quantity_Represent = toggled_setIN_Vent_Its_Quantity_Represent
                        }.also {
                            repositorysMainGetter.repo1ProduitInfos.update(it)
                        }
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    val carton = relative_M1Produit.setIN_Vent_Its_Quantity_Represent ==
                            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton

                    Icon(
                        imageVector = if (carton) Icons.Default.Inventory2 else Icons.Default.ViewModule,
                        contentDescription = null,
                        tint = when {
                            allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            carton -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.secondary
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Carton quantity
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onShowCartonsDialog() }
                ) {
                    Text(
                        text = "Cartons",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${relative_M1Produit.quantite_Boit_Par_Carton}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Status toggle button
                NonTrouve_Handler(
                    allNonTrouve = allNonTrouve,
                    hasNonTrouve = hasNonTrouve,
                    relative_List_M10OperationVentCouleur = relative_List_M10OperationVentCouleur
                )
            }
        }
    }
}


@Composable
private fun ProductInfoSection(
    productName: String,
    categoryName: String,
    allNonTrouve: Boolean,
    onCategoryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Enhanced product name display
        Text(
            text = productName.uppercase(), // Made uppercase for better visibility
            style = MaterialTheme.typography.headlineSmall, // Larger typography
            fontWeight = FontWeight.Bold,
            color = if (allNonTrouve)
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            letterSpacing = 0.5.sp // Better letter spacing for readability
        )

        // Enhanced category display
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Catégorie:",
                style = MaterialTheme.typography.labelMedium,
                color = if (allNonTrouve)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )

            Text(
                text = categoryName,
                style = MaterialTheme.typography.bodyMedium,
                color = if (allNonTrouve)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else
                    MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onCategoryClick() }
            )
        }

        // Status indicator for unavailable products
        if (allNonTrouve) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                ),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "⚠ NON DISPONIBLE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionsSection(
    relative_M1Produit: M01Produit,
    allNonTrouve: Boolean,
    hasNonTrouve: Boolean,
    viewModel: ZViewModel_Sec1Frag3,
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>,
    onShowUnitsDialog: () -> Unit,
    onShowCartonsDialog: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Units card - enhanced
        Card_Produit_Nombre_Unites_Enhanced(
            allNonTrouve = allNonTrouve,
            relative_Produit = relative_M1Produit,
            onClick_PourOuvrireDialog = onShowUnitsDialog,
            modifier = Modifier.weight(1f)
        )

        // Cartons and controls card - enhanced
        Card_Controls_Enhanced(
            allNonTrouve = allNonTrouve,
            hasNonTrouve = hasNonTrouve,
            relative_M1Produit = relative_M1Produit,
            relative_List_M10OperationVentCouleur = relative_List_M10OperationVentCouleur,
            viewModel = viewModel,
            onShowCartonsDialog = onShowCartonsDialog,
            modifier = Modifier.weight(1f)
        )
    }
}


