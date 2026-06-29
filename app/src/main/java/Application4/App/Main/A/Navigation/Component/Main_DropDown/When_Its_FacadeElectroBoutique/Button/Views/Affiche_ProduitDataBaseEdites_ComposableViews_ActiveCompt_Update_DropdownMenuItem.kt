package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val TAG = "AfficheEdites"

@Composable
fun Affiche_ProduitDataBaseEdites_ComposableViews_ActiveCompt_Update_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit,
    affiche_ProduitDataBaseEdites_ComposableViews: Boolean = false,
    on_pour_update_affiche_ProduitDataBaseEdites_ComposableViews: (Boolean) -> Unit = {}
) {
    val activeCompt = viewModelNewProtoPatterns.active_Datas.active_M9Compt
    val showEditedProducts = affiche_ProduitDataBaseEdites_ComposableViews

    // Log every recomposition so we can confirm the state is being read fresh
    LaunchedEffect(activeCompt, showEditedProducts) {
        Log.d(TAG, "recomposed — activeCompt.keyID=${activeCompt?.keyID} | affiche_ProduitDataBaseEdites_ComposableViews=$showEditedProducts")
    }

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = if (showEditedProducts) Icons.Default.Edit else Icons.Default.EditOff,
                contentDescription = if (showEditedProducts) "Masquer produits édités" else "Afficher produits édités",
                tint = if (showEditedProducts) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Produits édités",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = if (showEditedProducts) "Afficher les produits modifiés" else "Masquer les produits modifiés",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (showEditedProducts) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingIcon = {
            Switch(
                checked = showEditedProducts,
                onCheckedChange = { isChecked ->
                    activeCompt?.let { compt ->
                        val updatedCompt = compt.copy(
                        )
                        Log.d(TAG, "Switch toggled → isChecked=$isChecked | keyID=${compt.keyID}")
                        on_pour_update_affiche_ProduitDataBaseEdites_ComposableViews(isChecked)
                        //viewModelNewProtoPatterns.update_active_Compt(updatedCompt)
                    } ?: Log.e(TAG, "Switch toggled but activeCompt is NULL — update skipped!")
                    onDismissDropdown()
                }
            )
        },
        onClick = {}
    )
}
