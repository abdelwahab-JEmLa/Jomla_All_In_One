package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.UiStateSec9Frag1
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.Dialogs.CategorySelectionDialog
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import Z_CodePartageEntreApps.Modules.D.Glide.Proto.A_GlideDisplayImageByKeyId_Proto_5
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainItemEditeCategories(
    produit: M01Produit,
    availableCategories: List<Long>,
    onCategoryChanged: (M01Produit) -> Unit,
    modifier: Modifier = Modifier,
    categoriesMap: Map<Long, M16CategorieProduit> = emptyMap(),
    onAddCategory: ((String) -> Unit)? = null,
    onUpdateCategory: ((Long, String) -> Unit)? = null,
    selectedProducts: Set<M01Produit> = emptySet(),
    onProductSelectionToggle: (M01Produit) -> Unit = {},
    showBulkMoveDialog: Boolean = false,
    onShowBulkMoveDialog: (Boolean) -> Unit = {},
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
) {
    var showDialog by remember { mutableStateOf(false) }
    val isSelected = selectedProducts.contains(produit)

    // Collect UI state to access clickItemMode
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    // FIXED: Check the current click mode
                    when (uiState.clickItemMode) {
                        UiStateSec9Frag1.ClickItemMode.FastMove -> {
                            // In FastMove mode, directly show the category selection dialog
                            showDialog = true
                        }
                        UiStateSec9Frag1.ClickItemMode.Standart -> {
                            // In Standard mode, toggle selection as before
                            onProductSelectionToggle(produit)
                        }
                    }
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.surface
            ),
            border = if (isSelected)
                androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            else null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                A_GlideDisplayImageByKeyId_Proto_5(
                    produitVID = produit.id,
                    modifier = Modifier.weight(1f),
                    produitNom = produit.nom,
                    size = 80.dp,
                    product = produit,
                    refreshImage = produit.actualiseSonImageTest2
                )
                Text(
                    text = produit.nom,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Add availability toggle button
                DisponibilityToggleButton(
                    viewModel=viewModel,
                    currentState = produit.disponibilityEtates,
                    onToggle = {
                        val updatedProduct = produit.toggleDisponibilityEtates()
                        onCategoryChanged(updatedProduct)
                    },
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Selection indicator - only show in Standard mode when selected
        if (isSelected && uiState.clickItemMode == UiStateSec9Frag1.ClickItemMode.Standart) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(20.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // FastMove mode indicator - show add_New different icon when in FastMove mode
        if (uiState.clickItemMode == UiStateSec9Frag1.ClickItemMode.FastMove) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(20.dp)
                    .background(
                        uiState.clickItemMode.couleur.copy(alpha = 0.8f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = uiState.clickItemMode.icon,
                    contentDescription = "FastMove Mode",
                    modifier = Modifier.size(12.dp),
                    tint = Color.White
                )
            }
        }
    }

    // Show dialog only when bulk move dialog is triggered (Standard mode)
    if (showBulkMoveDialog && selectedProducts.isNotEmpty() && uiState.clickItemMode == UiStateSec9Frag1.ClickItemMode.Standart) {
        BulkCategorySelectionDialog(
            viewModel=viewModel,
            products = selectedProducts.toList(),
            onCategorySelected = { newId ->
                selectedProducts.forEach { product ->
                    newId?.let { product.copy(idParentCategorie = it) }
                        ?.let { onCategoryChanged(it) }
                }
                onShowBulkMoveDialog(false)
            },
            onDismiss = { onShowBulkMoveDialog(false) },
            onAddCategory = onAddCategory,
            onUpdateCategory = onUpdateCategory,
            categoriesMap = categoriesMap,
            availableCategories = availableCategories
        )
    }

    // Individual product dialog - show when FastMove mode is active or when explicitly requested
    if (showDialog) {
        CategorySelectionDialog(
            viewModel = viewModel,
            product = produit,
            onCategorySelected = { newId ->
                newId?.let { produit.copy(idParentCategorie = it) }?.let { onCategoryChanged(it) }
                showDialog = false
            },
            onDismiss = { showDialog = false },
            onUpdateCategory = onUpdateCategory,
            categoriesMap = categoriesMap,
            availableCategories = availableCategories
        )
    }
}

@Composable
fun BulkCategorySelectionDialog(
    products: List<M01Produit>,
    onCategorySelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    onAddCategory: ((String) -> Unit)? = null,
    onUpdateCategory: ((Long, String) -> Unit)? = null,
    categoriesMap: Map<Long, M16CategorieProduit> = emptyMap(),
    availableCategories: List<Long> = emptyList(),
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel
) {
    // Use the same CategorySelectionDialog but with modified title and text
    CategorySelectionDialog(
        viewModel = viewModel, // Use first product as reference
        product = products.first(),
        onCategorySelected = onCategorySelected,
        onDismiss = onDismiss,
        onUpdateCategory = onUpdateCategory,
        categoriesMap = categoriesMap,
        availableCategories = availableCategories,
    )
}


@Composable
fun DisponibilityToggleButton(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    currentState: DisponibilityEtates,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (color, icon, text) = when (currentState) {
        DisponibilityEtates.DISPO -> Triple(
            Color.Green,
            "✓",
            "Dispo"
        )
        DisponibilityEtates.NON_DISPO -> Triple(
            Color.Red,
            "✗",
            "Non Dispo"
        )
        DisponibilityEtates.PETITE_PROBABILITY -> Triple(
            Color.Blue,
            "?",
            "Possible"
        )
    }

    Button(
        onClick = onToggle,
        modifier = modifier.height(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color
        ),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$icon $text",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp
        )
    }
}
