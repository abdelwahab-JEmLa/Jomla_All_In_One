package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.List.View

import A_Main.Shared.Module.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.LayersClear
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Updated_list_m3couleurs_Affichable_Au_Presenters(
    onDismissDropdown: () -> Unit,
    context: Context = koinInject(),
    appDatabase: AppDatabase = koinInject(),
    updated_list_m3couleurs_Affichable_Au_Presenters: List<M3CouleurProduitInfos>,
    updated_list_m3couleurs_Affichable_Au_Presenters_filtred: List<Pair<String, M3CouleurProduitInfos>>,
) {
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
                text = if (isUploading) "Envoi en cours..." else "Updated_list_m3couleurs_Affichable_Au_Presenters",
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
                            "et Updated_list_m3couleurs_Affichable_Au_Presenters" +
                            "actuellement filtrés. Continuer ?"
                )
            },
            confirmButton = {
                TextButton(
                    modifier = Modifier.semantics(mergeDescendants = true) {},
                    onClick = {
                        showConfirmDialog = false
                        // isUploading is set here on the Main thread (onClick runs on Main).
                        // It will be cleared inside onSuccess, which the repository also
                        // dispatches on Main — after Firebase.updateChildren().await() completes.
                        isUploading = true
                        RepositorysMainSetter_NewProtoPatterns(
                            appDatabase = appDatabase,
                            context = context
                        ).update_List_M3CouleurProduitInfos_BathFireBase(
                            datas = updated_list_m3couleurs_Affichable_Au_Presenters,
                            onSuccess = {
                                // Runs on Dispatchers.Main after Firebase write is confirmed.
                                isUploading = false
                                onDismissDropdown()
                            }
                        )
                    }
                ) { Text("Envoyer", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Annuler") }
            }
        )
    }
}

/** Resets [its_pour_affiche_au_presenter] to null for every colour in [allColors],
 *  then batch-uploads the result to Firebase. */
@Composable
fun Reset_its_pour_affiche_au_presenter(
    onDismissDropdown: () -> Unit,
    allColors: List<M3CouleurProduitInfos>,
    context: Context = koinInject(),
    appDatabase: AppDatabase = koinInject(),
) {
    var isResetting by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    DropdownMenuItem(
        leadingIcon = {
            if (isResetting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Icon(
                    imageVector = Icons.Default.LayersClear,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        text = {
            Text(
                text = if (isResetting) "Réinitialisation..." else "Réinitialiser its_pour_affiche_au_presenter",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        enabled = !isResetting,
        onClick = { showConfirmDialog = true }
    )

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmer la réinitialisation") },
            text = {
                Text(
                    "Cette action va mettre its_pour_affiche_au_presenter à null " +
                            "pour toutes les couleurs (${allColors.size}) et envoyer vers Firebase. " +
                            "Continuer ?"
                )
            },
            confirmButton = {
                TextButton(
                    modifier = Modifier.semantics(mergeDescendants = true) {},
                    onClick = {
                        showConfirmDialog = false
                        // isResetting is set here on the Main thread (onClick runs on Main).
                        // It will be cleared inside onSuccess, which the repository also
                        // dispatches on Main — after Firebase.updateChildren().await() completes.
                        isResetting = true
                        val resetColors = allColors.map { it.copy(its_pour_affiche_au_presenter = false) }
                        RepositorysMainSetter_NewProtoPatterns(
                            appDatabase = appDatabase,
                            context = context
                        ).update_List_M3CouleurProduitInfos_BathFireBase(
                            datas = resetColors,
                            onSuccess = {
                                // Runs on Dispatchers.Main after Firebase write is confirmed.
                                isResetting = false
                                onDismissDropdown()
                            }
                        )
                    }
                ) { Text("Réinitialiser", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Annuler") }
            }
        )
    }
}
