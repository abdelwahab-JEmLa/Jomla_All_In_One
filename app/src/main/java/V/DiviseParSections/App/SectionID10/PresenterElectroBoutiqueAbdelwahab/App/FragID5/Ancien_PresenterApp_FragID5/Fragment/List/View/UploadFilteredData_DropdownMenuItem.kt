package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.List.View

import EntreApps.Shared.Models.Home.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M3CouleurProduitInfos
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

                            val keys: Map<String, RepositorysMainSetter_NewProtoPatterns.Couleur_Main_Values> =
                                list_M03CouleurProduitInfos
                                    .filter { it.keyID.isNotBlank() }
                                    .mapIndexed { index, couleur ->
                                        couleur.keyID to RepositorysMainSetter_NewProtoPatterns.Couleur_Main_Values(
                                            nom                    = couleur.nomCouleurStrSiSonImageDispo,
                                            classment              = index,
                                            activated              = true,
                                            parentProduitKeyID     = couleur.parentBProduitInfosKeyID,
                                            parentProduitDebugName = couleur.parentId1ProduitInfosDebugName
                                        )
                                    }
                                    .toMap()

                            android.util.Log.d(
                                "UploadFilteredData",
                                "⬆️ Upload démarré — ${keys.size} couleurs à envoyer"
                            )
                            keys.entries.forEachIndexed { index, (keyID, couleur) ->
                                android.util.Log.d(
                                    "UploadFilteredData",
                                    "  [$index] keyID=$keyID | nom=${couleur.nom} | classment=${couleur.classment} | parentProduit=${couleur.parentProduitDebugName} (${couleur.parentProduitKeyID})"
                                )
                            }

                            RepositorysMainSetter_NewProtoPatterns(
                                appDatabase = appDatabase,
                                context = context
                            ).insertFireBase_list_Main_Values_M3CouleurProduitInfos(keys)

                            android.util.Log.d(
                                "UploadFilteredData",
                                "✅ Upload terminé — ${keys.size} couleurs envoyées vers ref_Active_Filtred_Datas"
                            )

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
