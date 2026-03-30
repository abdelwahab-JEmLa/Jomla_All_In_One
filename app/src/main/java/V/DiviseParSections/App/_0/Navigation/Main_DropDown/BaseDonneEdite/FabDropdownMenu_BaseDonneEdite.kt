package V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenItsAchatsFragment_1
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.RelocationErrorException
import com.example.clientjetpack.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream

object DropBox_Init_2 {
    const val rootFolder: String = "/images"
    val localImagesBaseDir: File =
        File(M00CentralParametresOfAllApps.images_central_Local_storageLink)

    private val client: DbxClientV2 by lazy {
        DbxClientV2(
            DbxRequestConfig.newBuilder("jeMla-app/1.0").build(),
            DbxCredential(
                "",
                -1L,
                BuildConfig.DROPBOX_REFRESH_TOKEN,
                BuildConfig.DROPBOX_APP_KEY,
                BuildConfig.DROPBOX_APP_SECRET
            )
        )
    }


    // ─────────────────────────────────────────────────────────────────────────
    suspend fun organizeByCategories(
        catalogueGroups: Map<String, List<M3CouleurProduitInfos>>,
        onProgress: (Float) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        onProgress(0f)

        val allColors = catalogueGroups.values.flatten().filter { it.hasValidImage() }
        if (allColors.isEmpty()) {
            onProgress(1f); return@withContext
        }

        // ── Step 1 : build a full recursive index of /images ─────────────────
        val index = buildIndex()          // filename-without-ext → dropboxPath
        if (index.isEmpty()) {
            onProgress(1f); return@withContext
        }

        val total = allColors.size.toFloat()
        var done = 0

        // ── Step 2 : per-catalogue folder + download + move ──────────────────
        for ((rawName, colors) in catalogueGroups) {
            val catalogueName = rawName.toDropboxSafeName()
            val folderPath = "$rootFolder/$catalogueName"

            // Create the folder (ignore "already exists" errors)
            ensureDropboxFolder(folderPath)

            for (color in colors) {
                if (!color.hasValidImage()) {
                    done++; onProgress(done / total); continue
                }

                val filename = color.nomImageFichieSansEtansion
                val extension = color.extensionDisponible
                val fullName = "$filename.$extension"

                // ── 2a. Fetch locally if the file is missing ──────────────────
                val localFile = File(
                    M00CentralParametresOfAllApps.images_central_Local_storageLink,
                    fullName
                )
                if (!localFile.exists()) {
                    val dropboxPath = index[filename]
                    if (dropboxPath != null) {
                        try {
                            localFile.parentFile?.mkdirs()
                            FileOutputStream(localFile).use {
                                client.files().download(dropboxPath).download(it)
                            }
                        } catch (_: Exception) {
                            localFile.delete()
                        }
                    }
                }

                // ── 2b. Move in Dropbox to /images/{catalogue}/{file} ─────────
                val currentDropboxPath = index[filename]
                if (currentDropboxPath != null) {
                    val targetPath = "$folderPath/$fullName"
                    // Skip if it's already in the right place
                    if (!currentDropboxPath.equals(targetPath, ignoreCase = true)) {
                        moveDropboxFile(fromPath = currentDropboxPath, toPath = targetPath)
                        // Update the in-memory index so subsequent lookups use the new path
                        index[filename] = targetPath.lowercase()
                    }
                }

                done++
                onProgress(done / total)
            }
        }
        onProgress(1f)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Builds filename-without-extension → dropboxPathLower index for /images (recursive). */
    private suspend fun buildIndex(): MutableMap<String, String> =
        withContext(Dispatchers.IO) {
            val index = mutableMapOf<String, String>()
            try {
                var result =
                    client.files().listFolderBuilder(rootFolder).withRecursive(true).start()
                while (true) {
                    result.entries.filterIsInstance<FileMetadata>().forEach { entry ->
                        val path = entry.pathLower ?: return@forEach
                        index[entry.name.substringBeforeLast(".")] = path
                    }
                    if (!result.hasMore) break
                    result = client.files().listFolderContinue(result.cursor)
                }
            } catch (_: Exception) {
            }
            index
        }

    private suspend fun ensureDropboxFolder(path: String) = withContext(Dispatchers.IO) {
            client.files().createFolderV2(path)
    }

    /** Moves a file on Dropbox; silently skips if source is gone or target exists. */
    private suspend fun moveDropboxFile(fromPath: String, toPath: String) =
        withContext(Dispatchers.IO) {
            try {
                client.files().moveV2(fromPath, toPath)
            } catch (_: RelocationErrorException) {
                // e.g. source not found or destination already exists — skip
            } catch (_: Exception) {
            }
        }

    private suspend fun syncImage(
        color: M3CouleurProduitInfos,
        index: Map<String, String>
    ) {
        val filename = color.nomImageFichieSansEtansion
        if (!color.hasValidImage()) return
        val localFile = File(
            M00CentralParametresOfAllApps.images_central_Local_storageLink,
            "$filename.${color.extensionDisponible}"
        )
        if (localFile.exists()) return
        val dropboxPath = index[filename] ?: return
        try {
            withContext(Dispatchers.IO) {
                localFile.parentFile?.mkdirs()
                FileOutputStream(localFile).use {
                    client.files().download(dropboxPath).download(it)
                }
            }
        } catch (_: Exception) {
            localFile.delete()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Extension helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun M3CouleurProduitInfos.hasValidImage() =
        nomImageFichieSansEtansion.isNotBlank() && nomImageFichieSansEtansion != "Non Dispo"

    /** Converts a catalogue display name into a Dropbox-safe folder name. */
    private fun String.toDropboxSafeName(): String =
        trim()
            .replace(Regex("[^a-zA-Z0-9_\\-àâäéèêëîïôùûüç]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')
            .ifEmpty { "Divers" }

    private fun Boolean?.orFalse() = this ?: false
}

@Composable
fun FabDropdownMenu_BaseDonneEdite(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // null → idle, 0f..1f → running
    var organizeProgress by remember { mutableStateOf<Float?>(null) }

    Box(modifier = modifier.offset(y = (-90).dp)) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropDownItemWBaseDonne_OrganiserParCatalogue(
                progress = organizeProgress,
                enabled = organizeProgress == null,
                onClick = {
                    coroutineScope.launch {
                        organizeProgress = 0f

                        // ── Build Map<catalogueName, List<M3CouleurProduitInfos>> ──
                        val catalogueGroups = withContext(Dispatchers.Default) {
                            val catalogues = get_ListM21CataloguesCategorie()   // static list
                            val categories =
                                repositorysMainGetter.repoM16CategorieProduit.datasValue
                            val produits = repositorysMainGetter.repo1ProduitInfos.datasValue
                            val couleurs =
                                repositorysMainGetter.repo03CouleurProduitInfos.datasValue

                            // catalogue.id → catalogue.nom
                            val catalogueNameById = catalogues.associate { it.id to it.nom }

                            // category.id → catalogue.nom
                            val catalogueNomByCategId = categories.associate { cat ->
                                cat.id to (catalogueNameById[cat.catalogueParentId]
                                    ?: "Sans_Catalogue")
                            }

                            // produit.keyID → catalogue.nom
                            val catalogueNomByProduitKey = produits.associate { p ->
                                p.keyID to (catalogueNomByCategId[p.idParentCategorie]
                                    ?: "Sans_Catalogue")
                            }

                            // Group valid colours by their catalogue name
                            couleurs
                                .filter {
                                    it.nomImageFichieSansEtansion.isNotBlank() &&
                                            it.nomImageFichieSansEtansion != "Non Dispo"
                                }
                                .groupBy { couleur ->
                                    catalogueNomByProduitKey[couleur.parentBProduitInfosKeyID]
                                        ?: "Sans_Catalogue"
                                }
                        }

                        // ── Hand off to DropBox_Init ─────────────────────────
                        DropBox_Init_2.organizeByCategories(
                            catalogueGroups = catalogueGroups,
                            onProgress = { p -> organizeProgress = p }
                        )

                        organizeProgress = null
                        onDismissDropdown()
                    }
                }
            )
            // ────────────────────────────────────────────────────────────────

            DropDownItemWBaseDonne_2(
                nomFun = "FABs Mode Edites Produit",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItemWBaseDonne_1(
                nomFun = "Givre le neveau Classement",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenItsAchatsFragment_1(
                nomFun = "",
                onDismissDropdown = onDismissDropdown
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Dropdown item: Organiser images par catalogue (with progress bar)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DropDownItemWBaseDonne_OrganiserParCatalogue(
    progress: Float?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuItem(
            text = {
                Text(
                    text = when {
                        progress == null -> "Organiser images par Catalogue"
                        progress < 1f -> "Déplacement… ${(progress * 100).toInt()} %"
                        else -> "Terminé ✓"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            enabled = enabled,
            onClick = onClick
        )
        if (progress != null) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Other local dropdown items
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DropDownItemWBaseDonne_2(

    nomFun: String,
    onDismissDropdown: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = nomFun, style = MaterialTheme.typography.bodyMedium) },
        onClick = { onDismissDropdown() }
    )
}

@Composable
fun DropDownItemWBaseDonne_1(
    nomFun: String,
    onDismissDropdown: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = nomFun, style = MaterialTheme.typography.bodyMedium) },
        onClick = { onDismissDropdown() }
    )
}
