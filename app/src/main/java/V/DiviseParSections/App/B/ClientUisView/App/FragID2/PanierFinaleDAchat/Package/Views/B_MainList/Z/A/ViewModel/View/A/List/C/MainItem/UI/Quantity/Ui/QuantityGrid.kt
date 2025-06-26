package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.A.List.C.MainItem.UI.Quantity.Ui

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.ZViewModel_Sec1Frag3
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
 fun QuantityGrid(
    currentQuantity: Int,
    onQuantitySelected: (Int) -> Unit,
    viewModel: ZViewModel_Sec1Frag3 // Ajout du viewModel pour le QuantityButton personnalisé
) {
    val quantities = remember {
        listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 40, 50)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(200.dp)
    ) {
        items(quantities.size) { index ->
            val quantityNumber = quantities[index]
            // Utilisation de votre QuantityButton personnalisé
            QuantityButton(
                viewModel = viewModel,
                quantity = quantityNumber,
                isSelected = quantityNumber == currentQuantity,
                onClick = onQuantitySelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
