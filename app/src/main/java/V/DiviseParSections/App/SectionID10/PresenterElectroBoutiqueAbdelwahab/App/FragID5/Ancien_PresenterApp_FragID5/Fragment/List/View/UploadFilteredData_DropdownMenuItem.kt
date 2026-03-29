package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.List.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Ref_list_Filtred_Keys_M3Couleur_Main_Values
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
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
import org.koin.compose.koinInject

@Composable
fun Upload_Filtered_Au_Ref_Active_Keys_M03Couleurs_Button(
    list_M03CouleurProduitInfos: List<M3CouleurProduitInfos>,
    parentProduit_Classement: Map<String, Int>,
    onDismissDropdown: () -> Unit,
    context: Context = koinInject(),
    appDatabase: AppDatabase = koinInject(),
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

                            // parentProduit_Classement now carries classement_By_FilterKeys_M3
                            // (fixed in LazyStigerList). We write the same value into both
                            // `classment` (M3 color key sort field) AND `parentProduitClassement`
                            // (parent M1 product sort field) so that getReturne_M1_3_16 can
                            // restore the correct display order on next seed.
                            val keys: Map<String, Ref_list_Filtred_Keys_M3Couleur_Main_Values> =
                                list_M03CouleurProduitInfos
                                    .filter { it.keyID.isNotBlank() }
                                    .associate { couleur ->
                                        val produitClassement =
                                            parentProduit_Classement[couleur.parentBProduitInfosKeyID] ?: 0
                                        couleur.keyID to Ref_list_Filtred_Keys_M3Couleur_Main_Values(
                                            nom                   = couleur.nomCouleurStrSiSonImageDispo,
                                            classment             = produitClassement,
                                            activated             = true,
                                            parentProduitKeyID    = couleur.parentBProduitInfosKeyID,
                                            parentProduitDebugName = couleur.parentId1ProduitInfosDebugName,
                                            parentProduitClassement = produitClassement,
                                        )
                                    }

                            RepositorysMainSetter_NewProtoPatterns(
                                appDatabase = appDatabase,
                                context = context
                            ).insertFireBase_list_Main_Values_M3CouleurProduitInfos(keys)

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
