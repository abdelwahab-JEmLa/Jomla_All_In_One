package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.W.Components

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.GetterFocusedVars.Companion.getSemanticsTagFocucedVars
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun QuantityDisplay(
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    allNonTrouve: Boolean,
    onQuantityClickToHaptic: () -> Unit
) {
    val getter = viewModel.getterFocusedVarsHandlerFacade

    val onVentM8BonVentM10OperationVentFilteredList = viewModel.getterFocusedVarsHandlerFacade
        .onVentM8BonVentM10OperationVentFilteredList

    val operationsForThisProduct = onVentM8BonVentM10OperationVentFilteredList
        .filter {
            it.parentM1ProduitInfosKeyId == produit.keyID
        }

    val totalQuantity = operationsForThisProduct.sumOf { it.quantityAchete }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (allNonTrouve) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clickable(enabled = !allNonTrouve) {
                // Fixed: Open dialog for this specific product
                viewModel.setterFocusedVarsHandlerFacade.ouvrireM1ProduitDialogChoisireQuantityFacade(
                    produit
                )
                onQuantityClickToHaptic()
            }
            .getSemanticsTag(
                "dialogChoisireQuantityM1ProduitInfosDebugName",
                getter.currentM9AppCompt?.dialogChoisireQuantityM1ProduitInfosDebugName
            )
            .getSemanticsTag(
                "dialogChoisireQuantityM1ProduitInfosDebugName",
                getter.currentM9AppCompt?.dialogChoisireQuantityM1ProduitInfosKeyID, 1
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

@Composable
fun VentProduitQuantityDialog_T1(
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    colorName: String,
    currentQuantity: Int,
    onDismiss: () -> Unit = {}
) {
    var selectedQuantity by remember { mutableStateOf(currentQuantity) }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    fun closeDialogChoisireQuantity() {
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = { closeDialogChoisireQuantity() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        title = {
            Column {
                Text(
                    text = "Select Quantity",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = colorName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column {
                QuantityGridM1Produit_T1(
                    produit = produit,
                    currentQuantity = selectedQuantity,
                    onQuantitySelected = { newQuantity ->
                        selectedQuantity = newQuantity
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        val message = if (newQuantity == 0) {
                            "Removed $colorName from cart"
                        } else {
                            "Updated $colorName quantity to $newQuantity"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                        closeDialogChoisireQuantity()
                    },
                    viewModel = viewModel
                )
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    closeDialogChoisireQuantity()
                },
                modifier = Modifier.getSemanticsTagFocucedVars(viewModel.getterFocusedVarsHandlerFacade)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun QuantityGridM1Produit_T1(
    produit: ArticlesBasesStatsTable,
    currentQuantity: Int,
    onQuantitySelected: (Int) -> Unit,
    viewModel: ViewModelsProduit_T1,
) {
    var showExtendedRange by remember { mutableStateOf(false) }

    val basicQuantities = remember {
        listOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 25, 30, 40, 50
        )
    }

    val extendedQuantities = remember {
        (0..50).toList()
    }

    val quantities = if (showExtendedRange) extendedQuantities else basicQuantities

    Column {
        // Toggle Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showExtendedRange) "All Numbers (0-50)" else "Quick Select",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = { showExtendedRange = !showExtendedRange },
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = if (showExtendedRange) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showExtendedRange) "Show less" else "Show more",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (showExtendedRange) "Less" else "More",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Quantity Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(if (showExtendedRange) 280.dp else 200.dp)
        ) {
            items(quantities.size) { index ->
                val quantityNumber = quantities[index]
                QuantityButtonM1Produit_T1(
                    produit = produit,
                    modifier = Modifier.fillMaxWidth(),
                    viewModel = viewModel,
                    newQuantity = quantityNumber,
                    isSelected = quantityNumber == currentQuantity,
                    onClick = onQuantitySelected
                )
            }
        }
    }
}

