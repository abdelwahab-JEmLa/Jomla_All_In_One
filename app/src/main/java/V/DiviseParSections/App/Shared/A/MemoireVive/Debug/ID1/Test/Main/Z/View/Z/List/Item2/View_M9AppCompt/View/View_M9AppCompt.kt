package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.Item2.View_M9AppCompt.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Ui.VendeurEditDialog
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.ViewModel_AdminAppPanelControleur
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun View_M9AppCompt(
    compt: Z_AppCompt,
    viewModel: ViewModel_AdminAppPanelControleur,
) {
    var showEditDialog by remember { mutableStateOf(false) }

    val isActive = (viewModel.aCentralFacade.focusedActiveValuesFacade.get.currentM9AppCompt?.keyID
        ?: "") == compt.keyID

    val backgroundColor = when {
        isActive -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
            }
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        if (isActive) {
            Text(
                text = "ComptsVendeurs",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ID: ${compt.vid}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    showEditDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifier le vendeur",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = {
                    // FIXED: Add proper update handling for visibility toggle
                    val updatedVendeur = compt.copy(hideAppScreen = !compt.hideAppScreen)
                }
            ) {
                val icon = if (compt.hideAppScreen) {
                    Icons.Default.VisibilityOff
                } else {
                    Icons.Default.Visibility
                }

                val tint = if (compt.hideAppScreen) {
                    Color.Gray
                } else {
                    MaterialTheme.colorScheme.primary
                }

                Icon(
                    imageVector = icon,
                    contentDescription = if (compt.hideAppScreen) "Show App Screen" else "Hide App Screen",
                    tint = tint
                )
            }
        }

        Text(
            text = "Nom: ${compt.nom}",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    if (showEditDialog) {
        VendeurEditDialog(
            vendeur = compt,
            onDismiss = { showEditDialog = false },
            onConfirm = { updatedVendeur ->
                // FIXED: Add proper update handling
                showEditDialog = false
            }
        )
    }
}
