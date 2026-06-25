package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import EntreApps.Shared.Compose_Injectable_Sepecialise.Kotlin.ID1.EditeBaseDonne.Package.CatronAdd.CartonVentHandler_App4
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Modules.Utils.M1.Module.Views.FastInit_Outlined_Int_Edite_Modulable_Proto4
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun Lenceur_Vent_Handler_App4(
    modifier: Modifier = Modifier,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    relative_M1produit: M01Produit,
    selectedCouleur: M3CouleurProduitInfos,
    selectedTariff: M13TarificationInfos,
    compactMode: Boolean = false,
    listM10OperationVentCouleur_FilteredBy_activeM8BonVent: List<M10OperationVentCouleur>?,
    affiche_buttons_lien_unite_couleur_au_couleut_parent: Boolean = false,
    mode_selection_parent_couleur: M3CouleurProduitInfos? = null,
    on_pour_update_mode_selection_parent_couleur: (M3CouleurProduitInfos?) -> Unit = {},
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel

    val activeOnVent_M8BonVent = viewModel.active_Datas.activeOnVent_M8BonVent

    val relative_M10OperationVentCouleur by remember(
        selectedCouleur.keyID,
        listM10OperationVentCouleur_FilteredBy_activeM8BonVent
    ) {
        derivedStateOf {
            listM10OperationVentCouleur_FilteredBy_activeM8BonVent?.find {
                it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID
            }
        }
    }

    val isGrossist = viewModel.active_Datas.currentApp_ItsWorkChezGrossisst
    val isAdmin = viewModel.active_Datas.currentApp_Est_Admin

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val storageRef = Firebase.storage.reference.child("Images Articles Data Base").child("produits")

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val fileNameWithoutExtension = if (selectedCouleur.nomImageFichieSansEtansion.isNotBlank() && selectedCouleur.nomImageFichieSansEtansion != "Non Dispo") {
                        selectedCouleur.nomImageFichieSansEtansion
                    } else {
                        "${relative_M1produit.id}_${selectedCouleur.indexCouleurDansAncienProto}"
                    }

                    if (selectedCouleur.extensionDisponible.isNotBlank()) {
                        val oldFile = File(localPath, "${fileNameWithoutExtension}.${selectedCouleur.extensionDisponible}")
                        if (oldFile.exists()) {
                            oldFile.delete()
                        }
                    }

                    val contentResolver = context.contentResolver
                    val imageBytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (imageBytes != null) {
                        val newFile = File(localPath, "${fileNameWithoutExtension}.webp")
                        withContext(Dispatchers.IO) {
                            FileOutputStream(newFile).use { it.write(imageBytes) }
                            try {
                                storageRef.child("${fileNameWithoutExtension}.webp").putBytes(imageBytes).await()
                            } catch (e: Exception) {
                                // silent upload error
                            }
                        }

                        viewModel.update_m3couleur(selectedCouleur.copy(
                            aAffiche = M3CouleurProduitInfos.Type.Image,
                            nomImageFichieSansEtansion = fileNameWithoutExtension,
                            extensionDisponible = "webp",
                            il_a_une_video_presentaion = false,
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        ))

                        viewModel.update_m1Produit(relative_M1produit.copy(
                            actualiseSonImage = relative_M1produit.actualiseSonImage + 1,
                            actualiseSonImageTest2 = relative_M1produit.actualiseSonImageTest2 + 1,
                            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                        ))

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Image de couleur mise à jour !", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val fileNameWithoutExtension = if (selectedCouleur.nomImageFichieSansEtansion.isNotBlank() && selectedCouleur.nomImageFichieSansEtansion != "Non Dispo") {
                        selectedCouleur.nomImageFichieSansEtansion
                    } else {
                        "${relative_M1produit.id}_${selectedCouleur.indexCouleurDansAncienProto}"
                    }

                    if (selectedCouleur.extensionDisponible.isNotBlank()) {
                        val oldFile = File(localPath, "${fileNameWithoutExtension}.${selectedCouleur.extensionDisponible}")
                        if (oldFile.exists()) {
                            oldFile.delete()
                        }
                    }

                    val newFile = File(localPath, "${fileNameWithoutExtension}.gif")

                    // Conversion vidéo → GIF via MediaMetadataRetriever (Android intégré, minSdk 26+).
                    // On extrait ~10 frames sur les 5 premières secondes et on les encode
                    // dans un GIF animé avec AnimatedGifEncoder (classe utilitaire inline ci-dessous).     //<--
                    //TODO(1): enlve le converti au gif et uplod le mp4 fichie directement 
                    val success = withContext(Dispatchers.IO) {        //<--
                    //TODO(1): unleve l uolode au storage fib de image 
                        Log.d("GifConvert", "▶ Début conversion : $fileNameWithoutExtension")
                        val converted = convertVideoToGifInline(
                            context = context,
                            videoUri = uri,
                            outputFile = newFile,
                            maxDurationMs = 5_000L,
                            frameCount = 10,
                            targetWidth = 480
                        )
                        if (converted) Log.d("GifConvert", "✅ Conversion réussie → ${newFile.absolutePath} (${newFile.length() / 1024} Ko)")
                        else Log.w("GifConvert", "⚠️ Échec conversion GIF pour $fileNameWithoutExtension")

                        if (converted && newFile.exists()) {
                            try {
                                val gifBytes = newFile.readBytes()
                                storageRef.child("${fileNameWithoutExtension}.gif").putBytes(gifBytes).await()
                            } catch (e: Exception) {
                                // silent upload error
                            }
                            true
                        } else {
                            false
                        }
                    }

                    if (success) {
                        viewModel.update_m3couleur(selectedCouleur.copy(
                            aAffiche = M3CouleurProduitInfos.Type.Image,
                            nomImageFichieSansEtansion = fileNameWithoutExtension,
                            extensionDisponible = "gif",
                            il_a_une_video_presentaion = true,
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        ))

                        viewModel.update_m1Produit(relative_M1produit.copy(
                            actualiseSonImage = relative_M1produit.actualiseSonImage + 1,
                            actualiseSonImageTest2 = relative_M1produit.actualiseSonImageTest2 + 1,
                            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                        ))

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Vidéo convertie en GIF et mise à jour !", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Erreur de conversion de la vidéo !", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val au_depot by remember(
        selectedCouleur.keyID,
        viewModel.active_Datas.list_M03CouleurProduitInfos
    ) {
        derivedStateOf {
            viewModel.active_Datas.list_M03CouleurProduitInfos
                ?.find { it.keyID == selectedCouleur.keyID }
                ?.count_Don_Depot ?: selectedCouleur.count_Don_Depot
        }
    }

    val currentQuantity by remember(relative_M10OperationVentCouleur) {
        derivedStateOf { relative_M10OperationVentCouleur?.quantity ?: 0 }
    }

    val standardCount = remember(relative_M1produit.setIN_Vent_Its_Quantity_Represent) {
        if (relative_M1produit.setIN_Vent_Its_Quantity_Represent ==
            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton
        ) relative_M1produit.quantite_Boit_Par_Carton
        else 1
    }

    // Always allow selling regardless of depot count — a zero depot is informational only.
    val isAvailable = true

    val datasValue_distinct_type = remember(uiState.list_M13TarificationInfos) {
        uiState.list_M13TarificationInfos
            .filter { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
            .groupBy { it.typeChoisi }
            .mapValues { (_, tariffs) -> tariffs.maxByOrNull { it.creationTimestamps } }
            .values
            .filterNotNull()
    }

    val supperGro = datasValue_distinct_type.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                it.prixCurrency != 0.0
    }

    val detaille = datasValue_distinct_type.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                it.prixCurrency != 0.0
    }

    val new_Prix_Progressive_Editable = M13TarificationInfos.get_default()
        .copy(typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable)

    val tariff_Stocked_Pour_NewOperationVent = supperGro
        ?: detaille
        ?: new_Prix_Progressive_Editable

    fun handleLenceVent_WhenNew(
        newQuantity: Int,
        currentList: List<M10OperationVentCouleur>?,
    ) {
        val parentM13TarificationKeyID =
            if (selectedTariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client) "Prix_Progressive_Editable Non Saved"
            else selectedTariff.keyID

        val newOperation = M10OperationVentCouleur.get_Default().copy(
            creationTimestamps = System.currentTimeMillis(),
            setIN_Vent_Its_Quantity_Represent = relative_M1produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = relative_M1produit.quantite_Boit_Par_Carton,
            quantity = newQuantity,
            prix_de_Vent_entre_directement_NewProto = selectedTariff.prixCurrency,
            parentM13TarificationKeyID = parentM13TarificationKeyID,
            parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
            parent_M1Produit_KeyId = relative_M1produit.keyID,
            parent_M1Produit_DebugInfos = "par.produit ${relative_M1produit.nom}",
            parent_M3CouleurProduit_KeyID = selectedCouleur.keyID,
            parent_M3CouleurProduit_DebugInfos = selectedCouleur.get_DebugsInfos(),
            parent_M8BonVent_KeyId = activeOnVent_M8BonVent?.keyID ?: "",
            parent_M8BonVent_DebugInfos = activeOnVent_M8BonVent?.get_DebugInfos() ?: "",
            parent_M2Client_KeyID = activeOnVent_M8BonVent?.parent_M2Client_KeyID ?: "null",
            typeTarificationEnumT2 = selectedTariff.typeChoisi,
            its_created_in_working_for_wholesaler = isGrossist
        )
        val newList = (currentList ?: emptyList()) + newOperation
        viewModel.addNew_listM10OperationVentCouleur(newList)
    }

    fun handleLenceVent_When_There_Is_Old(
        newQuantity: Int,
        currentOp: M10OperationVentCouleur,
        currentList: List<M10OperationVentCouleur>?,
    ) {
        val updatedOperation = currentOp.copy(
            quantity = newQuantity,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        val updatedList = currentList?.map {
            if (it.keyID == updatedOperation.keyID) updatedOperation else it
        }
        viewModel.update_listM10OperationVentCouleur(updatedList)
    }

    // ── Dispatcher: route vers add, update, ou delete selon quantité et existence ──
    fun handleLenceVent(newQuantity: Int) {
        val currentList =
            viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
        val currentOp =
            currentList?.find { it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID }

        // When quantity drops to 0 we delete rather than update:
        // update_listM10OperationVentCouleur only maps/replaces existing entries and
        // would silently keep the filtered-out item alive in DAO + Firebase.
        when {
            newQuantity == 0 && currentOp != null -> {
                viewModel.delete_M10OperationVentCouleur(currentOp)
            }
            newQuantity == 0 -> {
                // Rien à faire : pas d'opération existante et quantité = 0
            }
            currentOp == null -> handleLenceVent_WhenNew(newQuantity, currentList)
            else -> handleLenceVent_When_There_Is_Old(newQuantity, currentOp, currentList)
        }

        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val horizontalPadding = if (compactMode) 4.dp else 8.dp
    val verticalPadding = if (compactMode) 2.dp else 4.dp

    val shape =
        RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp)

    val containerColor = if (!isAvailable) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    } else if (currentQuantity > 0) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }
    val boitParCarton = relative_M1produit.quantite_Boit_Par_Carton
    val currentCartons = if (boitParCarton > 0) currentQuantity / boitParCarton else 0
    val depotEnCartons = if (boitParCarton > 0) au_depot / boitParCarton else 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                set(
                    value = listM10OperationVentCouleur_FilteredBy_activeM8BonVent
                        ?.filter {
                            it.parent_M1Produit_DebugInfos.contains("Lino")
                        } ?: emptyList(),
                    key = SemanticsPropertyKey("filter")
                )
                set(
                    value = tariff_Stocked_Pour_NewOperationVent,
                    key = SemanticsPropertyKey("tariff_Stocked_Pour_NewOperationVent")
                )
            },
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (boitParCarton > 1 && isAdmin && viewModel.active_Datas.active_M9Compt?.affiche_ProduitDataBaseEdites_ComposableViews == true) {
            CartonVentHandler_App4(
                currentCartons = currentCartons,
                depotEnCartons = depotEnCartons,
                isAvailable = isAvailable,
                isAdmin = true,
                compactMode = compactMode,
                containerColor = containerColor,
                horizontalPadding = horizontalPadding,
                verticalPadding = verticalPadding,
                onVentUpdate = { newCartons ->
                    handleLenceVent(newCartons * boitParCarton)
                },
            )
        }

        // ── Boit / unit handler ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(containerColor.copy(alpha = 0.15f))
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            contentAlignment = Alignment.CenterEnd
        ) {
            FastInit_Outlined_Int_Edite_Modulable_Proto4(
                start_count = currentQuantity,
                au_depot = au_depot,
                standard_count = standardCount,
                icon = Icons.Default.ShoppingCart,
                isAvailable = isAvailable,
                compact_taille = compactMode,
                show_depot_card_on_top_in_flow_row = true,
                is_admin = isAdmin,
                add_spacing_between_depot_and_sale = isAdmin,
                affiche_ProduitDataBaseEdites = viewModel.active_Datas.active_M9Compt?.affiche_ProduitDataBaseEdites_ComposableViews == true,
                affiche_buttons_lien_unite_couleur_au_couleut_parent = affiche_buttons_lien_unite_couleur_au_couleut_parent,
                c_unite_couleur_de_couleurKey = selectedCouleur.c_unite_couleur_de_couleurKey,
                mode_selection_parent_couleur_key = mode_selection_parent_couleur?.keyID ?: "",
                is_this_color_selected_as_parent_for_link = mode_selection_parent_couleur?.keyID == selectedCouleur.keyID,
                on_pour_mode_selection_parent_couleur = { on_pour_update_mode_selection_parent_couleur(selectedCouleur) },
                onPickImage = { imagePickerLauncher.launch("image/*") },
                onPickVideo = { videoPickerLauncher.launch("video/*") },
                on_set_c_unite_key = { key ->
                    val parentColor = mode_selection_parent_couleur
                    if (parentColor != null) {
                        viewModel.update_m3couleur(parentColor.copy(c_unite_couleur_de_couleurKey = selectedCouleur.keyID))
                        on_pour_update_mode_selection_parent_couleur(null)
                    } else {
                        viewModel.update_m3couleur(selectedCouleur.copy(c_unite_couleur_de_couleurKey = key))
                    }
                },
                on_admin_depot_update = { newDepotCount ->
                    viewModel.update_depot_count(selectedCouleur, newDepotCount)
                },
                on_Data_Update = { newQuantity -> handleLenceVent(newQuantity) },
            )
        }
    } // end Column
}

// ─────────────────────────────────────────────────────────────────────────────
// convertVideoToGifInline
//
// Remplace l'ancienne dépendance externe (convertVideoToGif).
// Utilise uniquement des API Android intégrées (minSdk 26+) :
//   • MediaMetadataRetriever  → extraction de frames Bitmap
//   • AnimatedGifEncoder      → encodage GIF animé (implémentation inline ci-dessous)
//
// Paramètres :
//   maxDurationMs  – durée max à encoder (ms, défaut 5 000)
//   frameCount     – nombre de frames à extraire sur cette durée (défaut 10)
//   targetWidth    – largeur cible du GIF ; hauteur déduite du ratio (défaut 480)
//
// Retourne true si le fichier GIF a été créé avec succès.
// ─────────────────────────────────────────────────────────────────────────────
private fun convertVideoToGifInline(
    context: android.content.Context,
    videoUri: android.net.Uri,
    outputFile: File,
    maxDurationMs: Long = 5_000L,
    frameCount: Int = 10,
    targetWidth: Int = 480,
): Boolean {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, videoUri)

        val durationMs = retriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull() ?: 0L
        val capMs = minOf(durationMs, maxDurationMs).takeIf { it > 0 } ?: maxDurationMs

        // Calcul du ratio hauteur/largeur à partir de la première frame
        val probe = retriever.getFrameAtTime(0)
        val aspectRatio = if (probe != null && probe.width > 0)
            probe.height.toFloat() / probe.width.toFloat() else 1f
        val targetHeight = (targetWidth * aspectRatio).toInt()
        probe?.recycle()

        val stepUs = (capMs * 1_000L) / frameCount      // pas en microsecondes
        val delayCs = (capMs / frameCount / 10).toInt() // délai inter-frames en centièmes de sec (GIF)

        val encoder = AnimatedGifEncoder()
        encoder.start(java.io.FileOutputStream(outputFile))
        encoder.setDelay(delayCs * 10)                  // ms
        encoder.setRepeat(0)                            // boucle infinie
        encoder.setSize(targetWidth, targetHeight)
        encoder.setQuality(10)                          // 1 (meilleur) … 20 (rapide)

        var framesEncoded = 0
        for (i in 0 until frameCount) {
            val timeUs = i * stepUs
            val raw = retriever.getFrameAtTime(
                timeUs,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            ) ?: continue
            val scaled = Bitmap.createScaledBitmap(raw, targetWidth, targetHeight, true)
            raw.recycle()
            encoder.addFrame(scaled)
            scaled.recycle()
            framesEncoded++
            Log.v("GifConvert", "  frame $framesEncoded/$frameCount à ${timeUs / 1_000} ms")
        }

        encoder.finish()
        Log.d("GifConvert", "✅ $framesEncoded frames encodées → ${outputFile.length() / 1024} Ko")
        framesEncoded > 0 && outputFile.exists()
    } catch (e: Exception) {
        Log.e("GifConvert", "❌ Erreur conversion GIF", e)
        false
    } finally {
        retriever.release()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AnimatedGifEncoder – encodeur GIF animé minimal, 100 % Kotlin/JVM.
// Adapté du domaine public (Kevin Weiner / FMS), sans dépendance externe.
// ─────────────────────────────────────────────────────────────────────────────
private class AnimatedGifEncoder {

    private var width = 0
    private var height = 0
    private var delay = 0         // ms
    private var repeat = -1       // -1 = pas de répétition, 0 = infini
    private var quality = 10
    private var out: java.io.OutputStream? = null
    private var firstFrame = true

    fun setSize(w: Int, h: Int) { width = w; height = h }
    fun setDelay(ms: Int)       { delay = ms }
    fun setRepeat(count: Int)   { repeat = count }
    fun setQuality(q: Int)      { quality = q.coerceIn(1, 20) }

    fun start(os: java.io.OutputStream): Boolean {
        out = os
        return try {
            writeString("GIF89a")
            true
        } catch (e: Exception) { false }
    }

    fun addFrame(bitmap: Bitmap): Boolean {
        val os = out ?: return false
        return try {
            val pixels = quantize(bitmap)
            if (firstFrame) {
                writeLSD()
                writePalette()
                if (repeat >= 0) writeNetscapeExt()
                firstFrame = false
            }
            writeGraphicCtrlExt()
            writeImageDesc()
            writePixels(pixels)
            true
        } catch (e: Exception) { false }
    }

    fun finish(): Boolean {
        val os = out ?: return false
        return try {
            os.write(0x3B)   // GIF trailer
            os.flush()
            os.close()
            true
        } catch (e: Exception) { false }
    }

    // ── internals ─────────────────────────────────────────────────────────────

    private var colorTab  = ByteArray(768)   // palette RGB × 256
    private var indexedPixels = ByteArray(0) // indices palette par pixel

    /** Quantifie le bitmap → palette 256 couleurs + tableau d'indices. */
    private fun quantize(bitmap: Bitmap): ByteArray {
        val w = bitmap.width
        val h = bitmap.height
        val argb = IntArray(w * h)
        bitmap.getPixels(argb, 0, w, 0, 0, w, h)

        // NeuQuant simplified : on prend les 256 premières couleurs uniques
        // puis on mappe chaque pixel sur l'entrée la plus proche.
        val palette = LinkedHashMap<Int, Int>(256)
        for (px in argb) {
            val rgb = px and 0xFFFFFF
            if (palette.size < 256) palette.putIfAbsent(rgb, palette.size)
        }
        // Remplir colorTab
        colorTab.fill(0)
        for ((rgb, idx) in palette) {
            colorTab[idx * 3 + 0] = ((rgb shr 16) and 0xFF).toByte()
            colorTab[idx * 3 + 1] = ((rgb shr 8)  and 0xFF).toByte()
            colorTab[idx * 3 + 2] = ( rgb          and 0xFF).toByte()
        }
        // Mapper les pixels
        val pixels = ByteArray(w * h)
        for (i in argb.indices) {
            val rgb = argb[i] and 0xFFFFFF
            pixels[i] = (palette[rgb] ?: closestPaletteIndex(rgb, palette.size)).toByte()
        }
        return pixels
    }

    private fun closestPaletteIndex(rgb: Int, size: Int): Int {
        val r = (rgb shr 16) and 0xFF
        val g = (rgb shr 8)  and 0xFF
        val b =  rgb         and 0xFF
        var best = 0; var bestDist = Int.MAX_VALUE
        for (i in 0 until size) {
            val dr = r - (colorTab[i * 3]     .toInt() and 0xFF)
            val dg = g - (colorTab[i * 3 + 1] .toInt() and 0xFF)
            val db = b - (colorTab[i * 3 + 2] .toInt() and 0xFF)
            val dist = dr * dr + dg * dg + db * db
            if (dist < bestDist) { bestDist = dist; best = i }
        }
        return best
    }

    // ── bloc GIF helpers ──────────────────────────────────────────────────────

    private fun writeLSD() {
        writeShort(width)
        writeShort(height)
        // Packed : palette globale présente, résolution couleur 8 bits, taille palette 256
        out!!.write(0xF7)   // 1_111_0_111
        out!!.write(0)      // index couleur de fond
        out!!.write(0)      // ratio pixel (0 = non défini)
    }

    private fun writePalette() {
        out!!.write(colorTab)
        // padding pour compléter 256 × 3 si la palette est plus petite
        val n = 768 - colorTab.size
        if (n > 0) out!!.write(ByteArray(n))
    }

    private fun writeNetscapeExt() {
        out!!.write(0x21); out!!.write(0xFF); out!!.write(11)
        writeString("NETSCAPE2.0")
        out!!.write(3); out!!.write(1)
        writeShort(repeat)
        out!!.write(0)
    }

    private fun writeGraphicCtrlExt() {
        out!!.write(0x21); out!!.write(0xF9); out!!.write(4)
        out!!.write(0)              // packed (pas de transparence)
        writeShort(delay / 10)     // délai en centièmes de secondes
        out!!.write(0)             // indice transparent (ignoré)
        out!!.write(0)             // block terminator
    }

    private fun writeImageDesc() {
        out!!.write(0x2C)
        writeShort(0); writeShort(0)   // left, top
        writeShort(width); writeShort(height)
        out!!.write(0)                 // pas de palette locale
    }

    private fun writePixels(pixels: ByteArray) {
        val encoder = LzwEncoder(width, height, pixels, 8)
        encoder.encode(out!!)
    }

    private fun writeShort(v: Int) {
        out!!.write(v and 0xFF)
        out!!.write((v shr 8) and 0xFF)
    }
    private fun writeString(s: String) = s.forEach { out!!.write(it.code) }
}

/** Encodeur LZW minimal pour GIF (domaine public). */
private class LzwEncoder(
    private val imgW: Int,
    private val imgH: Int,
    private val pixAry: ByteArray,
    private val colorDepth: Int
) {
    private val EOF = -1
    private var initCodeSize = maxOf(2, colorDepth)
    private var remaining = 0
    private var curPixel = 0

    fun encode(os: java.io.OutputStream) {
        os.write(initCodeSize)
        remaining = imgW * imgH
        curPixel  = 0
        compress(initCodeSize + 1, os)
        os.write(0)
    }

    private fun compress(initBits: Int, oos: java.io.OutputStream) {
        val HSIZE = 5003
        val htab   = IntArray(HSIZE) { -1 }
        val codetab = IntArray(HSIZE)

        var nBits       = initBits
        val maxBits     = 12
        var maxCode     = 1 shl nBits
        val clearCode   = 1 shl (initBits - 1)
        val eofCode     = clearCode + 1
        var freeEnt     = clearCode + 2
        var clearFlag   = false

        var curAccum = 0; var curBits = 0
        val masks = intArrayOf(0,1,3,7,0xF,0x1F,0x3F,0x7F,0xFF,0x1FF,0x3FF,0x7FF,0xFFF)

        val accum = ByteArray(256)
        var aCount = 0

        fun writeCode(code: Int) {
            curAccum = curAccum or (code shl curBits)
            curBits += nBits
            while (curBits >= 8) {
                accum[aCount++] = (curAccum and 0xFF).toByte()
                if (aCount >= 254) { oos.write(aCount); oos.write(accum, 0, aCount); aCount = 0 }
                curAccum = curAccum ushr 8; curBits -= 8
            }
        }

        fun flushPacket() {
            if (curBits > 0) { accum[aCount++] = (curAccum and 0xFF).toByte() }
            if (aCount > 0) { oos.write(aCount); oos.write(accum, 0, aCount); aCount = 0 }
        }

        fun clearTable() {
            htab.fill(-1); freeEnt = clearCode + 2; clearFlag = true; writeCode(clearCode)
        }

        fun nextPixel(): Int {
            if (remaining == 0) return EOF
            remaining--
            return pixAry[curPixel++].toInt() and 0xFF
        }

        writeCode(clearCode)
        var ent = nextPixel()
        if (ent == EOF) { writeCode(eofCode); flushPacket(); return }

        outer@ while (true) {
            val c = nextPixel()
            if (c == EOF) break
            val fcode = (c shl maxBits) + ent
            var i = (c shl (maxBits - 8)) xor ent
            var disp = if (i == 0) 1 else HSIZE - i
            while (true) {
                if (htab[i] == fcode) { ent = codetab[i]; continue@outer }
                if (htab[i] < 0) break
                i -= disp; if (i < 0) i += HSIZE
            }
            writeCode(ent)
            ent = c
            if (freeEnt < (1 shl maxBits)) {
                codetab[i] = freeEnt++; htab[i] = fcode
            } else {
                clearTable()
            }
            if (clearFlag) {
                clearFlag = false; nBits = initBits
                maxCode = 1 shl nBits
            } else if (freeEnt > maxCode) {
                nBits++
                if (nBits == maxBits) maxCode = 1 shl maxBits else maxCode = 1 shl nBits
            }
        }
        writeCode(ent)
        writeCode(eofCode)
        flushPacket()
    }
}
