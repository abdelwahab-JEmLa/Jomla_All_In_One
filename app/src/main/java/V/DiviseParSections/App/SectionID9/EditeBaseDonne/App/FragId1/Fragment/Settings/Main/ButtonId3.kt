package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ButtonId3(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    showLabels: Boolean,
    selectedCount: Int,
    onCatalogueMove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabels) Text("Catalogue ($selectedCount)")
        FloatingActionButton(
            onClick = onCatalogueMove,
            modifier = Modifier.size(40.dp),
            containerColor = if (selectedCount > 0) Color.Blue else Color.Gray
        ) {
            Icon(Icons.Default.Category, "Move Categories to Catalogue", tint = Color.White)
        }
    }
}
