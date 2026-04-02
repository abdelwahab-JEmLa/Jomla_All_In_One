package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.example.clientjetpack.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun SyncDropboxImages_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit,
) {
    val scope    = rememberCoroutineScope()
    var running  by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    DropdownMenuItem(
        enabled = !running,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sync images Dropbox", modifier = Modifier.weight(1f))
                    AnimatedVisibility(visible = running, enter = fadeIn(), exit = fadeOut()) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text  = if (progress < 0f) "…" else "${(progress * 100).toInt()} %",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                AnimatedVisibility(visible = running, enter = fadeIn(), exit = fadeOut()) {
                    Spacer(Modifier.height(4.dp))
                    if (progress < 0f) LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(2.dp))
                    else LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(2.dp))
                }
            }
        },
        leadingIcon = { Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(20.dp)) },
        onClick = {
            if (running) return@DropdownMenuItem
            running  = true
            progress = 0f
            scope.launch {
                syncMissingDropboxImages(vm = viewModelNewProtoPatterns, onProgress = { p -> progress = p })
                running = false
                onDismissDropdown()
            }
        }
    )
}

private suspend fun syncMissingDropboxImages(
    vm: A_ViewModel_NewProtoPatterns,
    onProgress: (Float) -> Unit,
) = withContext(Dispatchers.IO) {
    val dao       = vm.appDatabase.dao_M03CouleurProduitInfos()
    val toProcess = dao.getAll().filter { it.nomImageFichieSansEtansion != "Non Dispo" && it.nomImageFichieSansEtansion.isNotBlank() }
    if (toProcess.isEmpty()) { onProgress(1f); return@withContext }

    val client = DbxClientV2(
        DbxRequestConfig.newBuilder("jeMla-app/1.0").build(),
        DbxCredential("", -1L, BuildConfig.DROPBOX_REFRESH_TOKEN, BuildConfig.DROPBOX_APP_KEY, BuildConfig.DROPBOX_APP_SECRET)
    )

    onProgress(-1f)

    val indexMeta = mutableMapOf<String, FileMetadata>()
    var page = client.files().listFolderBuilder("/images").withRecursive(true).start()
    while (true) {
        page.entries.filterIsInstance<FileMetadata>().forEach { indexMeta[it.name.substringBeforeLast(".")] = it }
        if (!page.hasMore) break
        page = client.files().listFolderContinue(page.cursor)
    }

    val localBase = File(M00CentralParametresOfAllApps.images_central_Local_storageLink)
    val total     = toProcess.size.coerceAtLeast(1)

    toProcess.forEachIndexed { i, couleur ->
        val meta = indexMeta[couleur.nomImageFichieSansEtansion]

        if (meta == null) {
            if (couleur.dropBox_key != "Non Dispo")
                vm.repositorysMainSetter_NewProtoPatterns.update_M3CouleurProduitInfos(couleur.copy(dropBox_key = "Non Dispo"))
            onProgress((i + 1).toFloat() / total); return@forEachIndexed
        }

        val dropboxPath  = meta.pathLower ?: run { onProgress((i + 1).toFloat() / total); return@forEachIndexed }
        val dropboxModMs = meta.serverModified.time ?: 0L
        val localFile    = File(localBase, "${couleur.nomImageFichieSansEtansion}.${couleur.extensionDisponible}")

        if (!localFile.exists() || dropboxModMs > localFile.lastModified()) {
            try {
                localFile.parentFile?.mkdirs()
                FileOutputStream(localFile).use { client.files().download(dropboxPath).download(it) }
                if (dropboxModMs > 0L) localFile.setLastModified(dropboxModMs)
            } catch (e: Exception) {
                localFile.delete(); onProgress((i + 1).toFloat() / total); return@forEachIndexed
            }
        }

        if (couleur.dropBox_key != dropboxPath)
            vm.repositorysMainSetter_NewProtoPatterns.update_M3CouleurProduitInfos(couleur.copy(dropBox_key = dropboxPath))

        onProgress((i + 1).toFloat() / total)
    }

    onProgress(1f)
}
