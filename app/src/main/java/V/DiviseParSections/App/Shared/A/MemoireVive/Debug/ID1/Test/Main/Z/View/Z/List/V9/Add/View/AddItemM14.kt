package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.V9.Add.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.ViewModel.ViewModel_AdminAppPanelControleur
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddItemM14(
    viewModel: ViewModel_AdminAppPanelControleur,
) {
    val generatedDefaultM14 = M14VentPeriode(
        parent_M9AppCompt_KeyID = viewModel.aCentralFacade.get.parametresAppComptNonSaved
            .currentActiveFocucedM9AppComptKeyID,
        parent_M9AppCompt_DebugInfos = viewModel.aCentralFacade.get.parametresAppComptNonSaved
            .currentActiveFocucedM9AppComptDebugInfos
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                 viewModel.aCentralFacade.set.addNewM14VentPeriode(generatedDefaultM14)
            }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ajouter une période ${generatedDefaultM14.get_DebugInfos()}",
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter une période",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
