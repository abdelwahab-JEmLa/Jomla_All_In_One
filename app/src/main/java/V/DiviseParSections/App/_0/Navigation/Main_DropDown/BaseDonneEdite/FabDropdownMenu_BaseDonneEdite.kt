package V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos.Companion.rootFolder_DropBox
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenItsAchatsFragment_1
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
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
    val rootFolder: String = rootFolder_DropBox

    private val client: DbxClientV2 by lazy {
        DbxClientV2(
            DbxRequestConfig.newBuilder("jeMla-app/1.0").build(),
            DbxCredential("", -1L, BuildConfig.DROPBOX_REFRESH_TOKEN, BuildConfig.DROPBOX_APP_KEY, BuildConfig.DROPBOX_APP_SECRET)
        )
    }

    suspend fun organizeByCategories(
        catalogueGroups: Map<M21CataloguesCategorie, List<M3CouleurProduitInfos>>,
        onProgress: (Float) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        onProgress(0f)
        val allColors = catalogueGroups.values.flatten().filter { it.hasValidImage() }
        if (allColors.isEmpty()) { onProgress(1f); return@withContext }

        val index = buildIndex()
        if (index.isEmpty()) { onProgress(1f); return@withContext }

        val total = allColors.size.toFloat()
        var done  = 0

        for ((catalogue, colors) in catalogueGroups) {
            val folderPath = catalogue.drp_image_folder_catalogue_path
            ensureDropboxFolder(folderPath)

            for (color in colors) {
                if (!color.hasValidImage()) { done++; onProgress(done / total); continue }

                val filename  = color.nomImageFichieSansEtansion
                val fullName  = "$filename.${color.extensionDisponible}"
                val meta      = index[filename]
                val localFile = File(M00CentralParametresOfAllApps.images_central_Local_storageLink, fullName)

                if (meta != null) {
                    val dropboxPath  = meta.pathLower
                    val dropboxModMs = meta.serverModified?.time ?: 0L

                    if (dropboxPath != null && (!localFile.exists() || dropboxModMs > localFile.lastModified())) {
                        try {
                            localFile.parentFile?.mkdirs()
                            FileOutputStream(localFile).use { client.files().download(dropboxPath).download(it) }
                            if (dropboxModMs > 0L) localFile.setLastModified(dropboxModMs)
                        } catch (_: Exception) { localFile.delete() }
                    }

                    val targetPath = "$folderPath/$fullName"
                    if (dropboxPath != null && !dropboxPath.equals(targetPath, ignoreCase = true))
                        moveDropboxFile(fromPath = dropboxPath, toPath = targetPath)
                }

                done++
                onProgress(done / total)
            }
        }
        onProgress(1f)
    }

    private suspend fun buildIndex(): MutableMap<String, FileMetadata> = withContext(Dispatchers.IO) {
        val index = mutableMapOf<String, FileMetadata>()
        try {
            var result = client.files().listFolderBuilder(rootFolder).withRecursive(true).start()
            while (true) {
                result.entries.filterIsInstance<FileMetadata>().forEach { index[it.name.substringBeforeLast(".")] = it }
                if (!result.hasMore) break
                result = client.files().listFolderContinue(result.cursor)
            }
        } catch (_: Exception) {}
        index
    }

    private suspend fun ensureDropboxFolder(path: String) = withContext(Dispatchers.IO) {
        try { client.files().createFolderV2(path) } catch (_: Exception) {}
    }

    private suspend fun moveDropboxFile(fromPath: String, toPath: String) = withContext(Dispatchers.IO) {
        try { client.files().moveV2(fromPath, toPath) }
        catch (_: RelocationErrorException) {}
        catch (_: Exception) {}
    }

    private fun M3CouleurProduitInfos.hasValidImage() =
        nomImageFichieSansEtansion.isNotBlank() && nomImageFichieSansEtansion != "Non Dispo"
}

@Composable
fun FabDropdownMenu_BaseDonneEdite(
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var organizeProgress by remember { mutableStateOf<Float?>(null) }

    Box(modifier = modifier.offset(y = (-90).dp)) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropDownItemWBaseDonne_OrganiserParCatalogue(
                progress = organizeProgress,
                enabled  = organizeProgress == null,
                onClick  = {
                    coroutineScope.launch {
                        organizeProgress = 0f

                        val catalogueGroups = withContext(Dispatchers.Default) {
                            val catalogues      = get_ListM21CataloguesCategorie()
                            val categories      = repositorysMainGetter.repoM16CategorieProduit.datasValue
                            val produits        = repositorysMainGetter.repo1ProduitInfos.datasValue
                            val couleurs        = repositorysMainGetter.repo03CouleurProduitInfos.datasValue
                            val sansCatalogue   = catalogues.find { it.nom == "Sans Catalogue" }
                                ?: M21CataloguesCategorie(keyID = "t4", id = 4, nom = "Sans Catalogue")
                            val catalogueById           = catalogues.associateBy { it.id }
                            val catalogueByCategorieId  = categories.associate { cat -> cat.id to (catalogueById[cat.catalogueParentId] ?: sansCatalogue) }
                            val catalogueByProduitKey   = produits.associate { p -> p.keyID to (catalogueByCategorieId[p.idParentCategorie] ?: sansCatalogue) }
                            couleurs
                                .filter { it.nomImageFichieSansEtansion.isNotBlank() && it.nomImageFichieSansEtansion != "Non Dispo" }
                                .groupBy { catalogueByProduitKey[it.parentBProduitInfosKeyID] ?: sansCatalogue }
                        }

                        DropBox_Init_2.organizeByCategories(catalogueGroups = catalogueGroups, onProgress = { p -> organizeProgress = p })
                        organizeProgress = null
                        onDismissDropdown()
                    }
                }
            )

            DropDownItemWBaseDonne_2(nomFun = "FABs Mode Edites Produit", onDismissDropdown = onDismissDropdown)
            DropDownItemWBaseDonne_1(nomFun = "Givre le neveau Classement", onDismissDropdown = onDismissDropdown)
            DropDownItem_WhenItsAchatsFragment_1(nomFun = "", onDismissDropdown = onDismissDropdown)
        }
    }
}

@Composable
private fun DropDownItemWBaseDonne_OrganiserParCatalogue(
    progress: Float?,
    enabled:  Boolean,
    onClick:  () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuItem(
            text = {
                Text(
                    text = when {
                        progress == null -> "Organiser images par Catalogue"
                        progress < 1f    -> "Déplacement… ${(progress * 100).toInt()} %"
                        else             -> "Terminé ✓"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            enabled = enabled,
            onClick = onClick
        )
        if (progress != null) SyncProgressIndicator(progress = progress, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp))
    }
}

@Composable
fun SyncProgressIndicator(progress: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress    = { progress },
        modifier    = modifier.height(6.dp),
        color       = Color(0xFF4CAF50),
        trackColor  = Color.Gray.copy(alpha = 0.25f),
    )
}

@Composable
fun DropDownItemWBaseDonne_2(nomFun: String, onDismissDropdown: () -> Unit) {
    DropdownMenuItem(
        text    = { Text(text = nomFun, style = MaterialTheme.typography.bodyMedium) },
        onClick = { onDismissDropdown() }
    )
}

@Composable
fun DropDownItemWBaseDonne_1(nomFun: String, onDismissDropdown: () -> Unit) {
    DropdownMenuItem(
        text    = { Text(text = nomFun, style = MaterialTheme.typography.bodyMedium) },
        onClick = { onDismissDropdown() }
    )
}
