package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.B.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ClickUpdate
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.B.List.UI.QuantityButton_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun QuantityGrid_T1(
    currentQuantity: Int,
    onQuantitySelected: (Int) -> Unit,
    viewModel: ViewModelsProduit_T1,
    vent: M10OperationVentCouleur,
    clickUpdate: ClickUpdate
) {
    var showExtendedRange by remember { mutableStateOf(false) }

    val basicQuantities = remember {
        listOf(
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            20,
            21,
            22,
            23,
            24,
            25,
            30,
            40,
            50
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
                QuantityButton_T1(
                    modifier = Modifier.fillMaxWidth(), // Pass the click upsert mode
                    clickUpdate = clickUpdate,
                    viewModel = viewModel,
                    newQuantity = quantityNumber,
                    isSelected = quantityNumber == currentQuantity,
                    onClick = onQuantitySelected,
                    vent = vent
                )
            }
        }
    }
}
