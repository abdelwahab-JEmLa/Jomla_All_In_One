package Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos.Companion.rootFolder_DropBox
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Button_State
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.RelocationErrorException
import com.example.clientjetpack.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

data class Button_State(
    val showLabels: Boolean = true,
    val its_Active: Boolean = false,
    val text_Label: String = "",
    val colors: Pair<Color, Color> = Pair(Color.White, Color.White),
    val icons: Pair<ImageVector, ImageVector> = Pair(Icons.Default.Remove, Icons.Default.Add),
    val description_Functionement: String = "",
) {
    companion object {
        fun get_Default(): Button_State = Button_State()
    }
}

@Composable
fun Floating_Separated_Button(
    list_m16:    List<M16CategorieProduit>? = emptyList(),
    list_m1:     List<M01Produit>?          = emptyList(),
    list_m3:     List<M3CouleurProduitInfos>? = emptyList(),
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "",
        icons  = Pair(Icons.Default.FilterList, Icons.Default.ViewList),
        colors = Pair(Color.Red, Color.Green)
    )
) {
    val isShowingAll     = true
    val updatedButtonState = buttonState.copy(its_Active = isShowingAll)

    val configuration  = LocalConfiguration.current
    val screenWidth    = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX      by remember { mutableFloatStateOf(screenWidth.value - 200f) }
    var offsetY      by remember { mutableFloatStateOf(screenHeightDp.value - 300f) }
    var showDropdown by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, screenWidth.value - 100f)
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, screenHeightDp.value - 100f)
                    }
                }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    modifier       = Modifier.size(48.dp),
                    onClick        = { showDropdown = true },
                    containerColor = if (updatedButtonState.its_Active)
                        updatedButtonState.colors.second
                    else
                        updatedButtonState.colors.first
                ) {
                    Icon(
                        imageVector      = if (updatedButtonState.its_Active)
                            updatedButtonState.icons.second
                        else
                            updatedButtonState.icons.first,
                        contentDescription = if (isShowingAll) "Switch to Targeted View" else "Switch to Show All",
                        tint             = Color.White,
                        modifier         = Modifier.size(24.dp)
                    )
                }

                FragMap_DropdownMenu(
                    expanded  = showDropdown,
                    onDismiss = { showDropdown = false },
                    list_m16  = list_m16,
                    list_m1   = list_m1,
                    list_m3   = list_m3,
                )
            }
        }
    }
}

// ─── DropBox client & organizer ───────────────────────────────────────────────

object DropBox_Init_3 {
    val rootFolder: String = rootFolder_DropBox

    private val client: DbxClientV2 by lazy {
        DbxClientV2(
            DbxRequestConfig.newBuilder("jeMla-app/1.0").build(),
            DbxCredential(
                "", -1L,
                BuildConfig.DROPBOX_REFRESH_TOKEN,
                BuildConfig.DROPBOX_APP_KEY,
                BuildConfig.DROPBOX_APP_SECRET
            )
        )
    }

    suspend fun organizeByCategories(
        catalogueGroups: Map<M21CataloguesCategorie, List<M3CouleurProduitInfos>>?,
        onProgress: (Float) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        onProgress(0f)
        val allColors = catalogueGroups?.values?.flatten()?.filter { it.hasValidImage() }
        if (allColors.isNullOrEmpty()) { onProgress(1f); return@withContext }

        val index = buildIndex()
        if (index.isEmpty()) { onProgress(1f); return@withContext }

        val total = allColors.size.toFloat()
        var done  = 0

        catalogueGroups?.forEach { (catalogue, colors) ->
            val folderPath = catalogue.drp_image_folder_catalogue_path
            ensureDropboxFolder(folderPath)

            colors.forEach { color ->
                if (!color.hasValidImage()) { done++; onProgress(done / total); return@forEach }

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
                result.entries.filterIsInstance<FileMetadata>()
                    .forEach { index[it.name.substringBeforeLast(".")] = it }
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
