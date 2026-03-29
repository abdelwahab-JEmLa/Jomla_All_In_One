package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.UploadFilteredData_DropdownMenuItem.View
 /*
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
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

@Composable
fun Upload_Filtered_M03Couleurs_DropdownMenuItem_App4(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    DropdownMenuItem(
        leadingIcon = {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        text = {
            Text(
                text = if (isUploading) "Envoi en cours..." else "Envoyer produits filtrés → firebase",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        enabled = !isUploading,
        onClick = { showConfirmDialog = true }
    )

    if (showConfirmDialog) {

        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmer l'envoi") },
            text = {
                Text(
                    "Cette action va supprimer les données existantes dans firebase " +
                            "(ref_Active_Filtred_Datas) et les remplacer par les produits/couleurs/catégories " +
                            "actuellement filtrés. Continuer ?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        coroutineScope.launch(Dispatchers.IO) {
                            isUploading = true
                            val activeDatas = viewModelNewProtoPatterns.active_Datas
                            val filteredTree = activeDatas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur

                            // Collect every M03Couleur keyID that survived the priority/catalogue/category filter
                            val filteredM03KeyIDs: Set<String> = filteredTree
                                .flatMap { (_, categoryList) ->
                                    categoryList.flatMap { (_, productList) ->
                                        productList.flatMap { (_, couleurList) ->
                                            couleurList.map { it.keyID }
                                        }
                                    }
                                }
                                .toSet()

                            val filtred_listM03 = activeDatas.list_M03CouleurProduitInfos
                                ?.filter { it.keyID in filteredM03KeyIDs }

                            val keys: Map<String, Boolean> =
                                filtred_listM03
                                    ?.filter { it.keyID.isNotBlank() }
                                    ?.associate { it.keyID to true }
                                    ?: emptyMap()

                            viewModelNewProtoPatterns
                                .deleteInsertFireBase_listKeys_M3CouleurProduitInfos(keys)

                            isUploading = false
                            onDismissDropdown()
                        }
                    }
                ) { Text("Envoyer", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Annuler") }
            }
        )
    }
}
                               */
