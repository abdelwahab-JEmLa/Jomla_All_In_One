package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.Z.List.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel_M14VentPeriod
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun View_DefaultAddItem_M14VentPeriode(
    viewModel: ViewModel_M14VentPeriod,
) {
    // Add some debug info to see if the component is being called
    println("DEBUG: View_DefaultAddItem_M14VentPeriode is being rendered")

    // Get the required data with null safety
    val currentKeyID = viewModel.aCentralFacade.get.parametresAppComptNonSaved
        .currentActiveFocucedM9AppComptKeyID ?: "default_key"

    val currentDebugInfos = viewModel.aCentralFacade.get.parametresAppComptNonSaved
        .currentActiveFocucedM9AppComptDebugInfos ?: "default_debug"

    val generatedDefaultM14 = M14VentPeriode(
        parent_M9AppCompt_KeyID = currentKeyID,
        parent_M9AppCompt_DebugInfos = currentDebugInfos
    )

    // Add some spacing before the component
    Spacer(modifier = Modifier.height(8.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                println("DEBUG: Add button clicked")
                try {
                    viewModel.aCentralFacade.set.addNewM14VentPeriode(generatedDefaultM14)
                } catch (e: Exception) {
                    println("DEBUG: Error adding M14VentPeriode: ${e.message}")
                }
            }
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Ajouter une période",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Debug: ${generatedDefaultM14.get_DebugInfos()}",
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter une période",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Add some spacing after the component
    Spacer(modifier = Modifier.height(8.dp))
}
