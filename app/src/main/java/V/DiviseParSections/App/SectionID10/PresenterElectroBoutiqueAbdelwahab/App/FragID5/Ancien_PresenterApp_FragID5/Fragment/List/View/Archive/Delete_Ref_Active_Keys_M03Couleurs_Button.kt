package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.List.View.Archive


import A_Main.Shared.Module.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun Delete_Ref_Active_Keys_M03Couleurs_Button(
    onDismissDropdown: () -> Unit,
    context: Context = koinInject(),
    appDatabase: AppDatabase = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()
    var isDeleting by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    DropdownMenuItem(
        leadingIcon = {
            if (isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        text = {
            Text(
                text = if (isDeleting) "Suppression en cours..." else "Supprimer Ref_Active_Keys_M03Couleurs",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        },
        enabled = !isDeleting,
        onClick = { showConfirmDialog = true }
    )

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmer la suppression") },
            text = {
                Text(
                    "Cette action va supprimer toutes les clés dans " +
                            "Ref_Active_Keys_M03Couleurs sur Firebase. Cette opération est irréversible. Continuer ?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        coroutineScope.launch(Dispatchers.IO) {
                            isDeleting = true
                            RepositorysMainSetter_NewProtoPatterns(
                                appDatabase = appDatabase,
                                context = context
                            ).deleteFireBase_listKeys_M3CouleurProduitInfos {}
                            isDeleting = false
                            onDismissDropdown()
                        }
                    }
                ) { Text("Supprimer", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Annuler") }
            }
        )
    }
}
