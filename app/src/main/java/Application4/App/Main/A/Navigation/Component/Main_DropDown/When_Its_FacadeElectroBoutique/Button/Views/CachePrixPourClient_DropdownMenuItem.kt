package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun CachePrixPourClient_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit
) {
    val context = LocalContext.current
    val activeCompt = viewModelNewProtoPatterns.active_Datas.active_M9Compt
    val isCachePrix = activeCompt?.cache_prix_pour_que_le_client_ne_connait_pas ?: false

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = if (isCachePrix) Icons.Default.Close else Icons.Default.Check,
                contentDescription = if (isCachePrix) "Tarifs masqués" else "Tarifs affichés",
                tint = if (isCachePrix) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Masquer tarifs client",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = if (isCachePrix) "Tarifs masqués pour le client" else "Tarifs affichés",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCachePrix) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        },
        trailingIcon = {
            Switch(
                checked = isCachePrix,
                onCheckedChange = { isChecked ->
                    activeCompt?.let { compt ->
                        val updatedCompt = compt.copy(
                            cache_prix_pour_que_le_client_ne_connait_pas = isChecked,
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        )
                        viewModelNewProtoPatterns.update_active_Compt(updatedCompt)
                        val msg = if (isChecked) "Tarifs masqués pour le client" else "Tarifs affichés"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                    onDismissDropdown()
                }
            )
        },
        onClick = {
            activeCompt?.let { compt ->
                val newChecked = !isCachePrix
                val updatedCompt = compt.copy(
                    cache_prix_pour_que_le_client_ne_connait_pas = newChecked,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
                viewModelNewProtoPatterns.update_active_Compt(updatedCompt)
                val msg = if (newChecked) "Tarifs masqués pour le client" else "Tarifs affichés"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
            onDismissDropdown()
        }
    )
}
