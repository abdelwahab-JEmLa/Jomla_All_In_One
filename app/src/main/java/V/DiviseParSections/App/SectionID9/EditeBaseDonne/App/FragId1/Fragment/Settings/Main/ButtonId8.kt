package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ButtonId8(
    showLabels: Boolean,
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    onClicked: () -> Unit = {} // Add this parameter
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showLabels) {
            Text(
                text = uiState.clickItemMode.name,
                color = Color.White,
                modifier = Modifier
                    .background(
                        color = uiState.clickItemMode.couleur,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        FloatingActionButton(
            onClick = {
                viewModel.toggleEntreEntitiesClickItemMode()
            },
            modifier = Modifier.size(48.dp),
            containerColor = uiState.clickItemMode.couleur
        ) {
            Icon(
                imageVector = uiState.clickItemMode.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
