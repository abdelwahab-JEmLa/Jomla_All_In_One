package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun ButtonId6(
    viewModelPRODUCTS_LIST: Sec9FragId1ViewId2ViewModel = koinViewModel(),
    showLabels: Boolean,
) {
    val uiState by viewModelPRODUCTS_LIST.uiState.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showLabels) {
            Text(
                text = if (uiState.showDetailsExpandedPourTout) "Hide Details" else "Show Details",
                color = Color.Black,
                modifier = Modifier
                    .background(
                        color = if (uiState.showDetailsExpandedPourTout) Color.Red else Color.Green,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        FloatingActionButton(
            onClick = {
                viewModelPRODUCTS_LIST.update_showDetailsExpanded()
            },
            modifier = Modifier.size(48.dp),
            containerColor = if (uiState.showDetailsExpandedPourTout) Color.Red else Color.Green
        ) {
            Icon(
                imageVector = if (uiState.showDetailsExpandedPourTout) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "Toggle product details expansion",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
enum class ModeAffichageEtates {
    ShowDetails,
    HideDetailsTOUT,
    HideDetailsDePossible
}

