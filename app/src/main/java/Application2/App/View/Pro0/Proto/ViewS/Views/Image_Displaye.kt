package Application2.App.View.Pro0.Proto.ViewS.Views

import Application2.App.Fragment.ViewModel.ViewModel_MainFragment
import Application2.App.View.Pro0.Proto.Components.ProduitExpandState
import Application2.App.View.Pro0.Proto.ViewS.getPrixDrawables
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.example.clientjetpack.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File
enum class ImageQualite(
    val encodeQuality: Int = 0,
    val override: Int = 0
) {
    max_possible,
    standart(400, 70) ,      // (400,70)
    min_possible
}

private fun resolveQualite(expandState: ProduitExpandState) = when {
    expandState.isExpanded -> ImageQualite.max_possible
    expandState.isAnyExpanded -> ImageQualite.min_possible
    else -> ImageQualite.standart
}
/** For static images: suppress animation, apply quality/size overrides. */
private fun RequestBuilder<Drawable>.applyOptimizedImageOptions(
    couleur: M3CouleurProduitInfos,
    qualite: ImageQualite,
) = this
    .dontAnimate()
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    .priority(
        when (qualite) {
            ImageQualite.max_possible -> Priority.HIGH
            ImageQualite.standart -> Priority.NORMAL
            ImageQualite.min_possible -> Priority.LOW
        }
    )
    .dontTransform()
    .signature(ObjectKey("${couleur.keyID}_${couleur.dernierTimeTampsSynchronisationAvecFireBase}"))
    .override(
        when (qualite) {
            ImageQualite.max_possible -> 800
            ImageQualite.standart -> 350
            ImageQualite.min_possible -> 150
        }
    )
    .disallowHardwareConfig()
    .format(
        when (qualite) {
            ImageQualite.max_possible -> DecodeFormat.PREFER_ARGB_8888
            ImageQualite.standart -> DecodeFormat.PREFER_RGB_565
            ImageQualite.min_possible -> DecodeFormat.PREFER_RGB_565
        }
    )
    .encodeQuality(
        when (qualite) {
            ImageQualite.max_possible -> 100
            ImageQualite.standart -> 50
            ImageQualite.min_possible -> 20
        }
    )
    .skipMemoryCache(qualite == ImageQualite.min_possible)

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Image_Displaye_app2(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    expandState: ProduitExpandState,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
    viewModel: ViewModel_MainFragment
) {
    // Get WiFi state to determine if user can interact with images
    val wifiState by viewModel.wifiState.collectAsState()
    val canInteract = wifiState.isHostPhone || !wifiState.isConnected

    val parentProduct = remember(relative_M3CouleurProduitInfos.parentBProduitInfosKeyID) {
        viewModel.uiState.value.list_ProductWithColors.find {
            it.first.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
        }?.first ?: viewModel.uiState.value.list_M1Produit.find {
            it.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
        }
    }

    val toggleExpandOnClick: () -> Unit = {
        viewModel.wifi.toggleExpandedCouleur(relative_M3CouleurProduitInfos)
    }

    if (relative_M3CouleurProduitInfos.affiche_que_c_don_le_panie) {
        val isThisColorTheExpandedPresenter =
            expandState.isExpanded && relative_M3CouleurProduitInfos.keyID == expandState.bigPresenterCouleur.keyID

        // Le nom + prix s'affichent toujours pour une couleur "panier" (plus seulement
        // quand elle est le bigPresenterCouleur du produit expand) ; seule la taille du
        // texte change selon l'état.
        PanierNamePriceDisplay(
            modifier = modifier
                .fillMaxSize()
                .then(
                    if (canInteract) Modifier.clickable(onClick = toggleExpandOnClick) else Modifier
                ),
            nomProduit = parentProduct?.nom?.ifBlank { null }
                ?: relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Produit" },
            prixVente = parentProduct?.clientPrixVentUnite ?: 0.0,
            isExpanded = isThisColorTheExpandedPresenter,
            contentScale = contentScale
        )
        return
    }

    val qualite = resolveQualite(expandState)

    val imageFile = remember(
        relative_M3CouleurProduitInfos.nomImageFichieSansEtansion,
        relative_M3CouleurProduitInfos.extensionDisponible
    ) {
        if (relative_M3CouleurProduitInfos.nomImageFichieSansEtansion != "Non Dispo")
            File(
                "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne",
                "${relative_M3CouleurProduitInfos.nomImageFichieSansEtansion}.${relative_M3CouleurProduitInfos.extensionDisponible}"
            )
        else null
    }

    if (imageFile != null && imageFile.exists()) {
        val completeModifier = modifier
            .then(
                if (relative_M3CouleurProduitInfos.il_a_une_video_presentaion && expandState.isExpanded && relative_M3CouleurProduitInfos.keyID == expandState.bigPresenterCouleur.keyID) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.fillMaxSize()
                }
            )
            .then(
                if (canInteract) {
                    Modifier.clickable(onClick = toggleExpandOnClick)
                } else {
                    Modifier
                }
            )

        if (relative_M3CouleurProduitInfos.il_a_une_video_presentaion) {
            val isMainExpandedColor = expandState.isExpanded && relative_M3CouleurProduitInfos.keyID == expandState.bigPresenterCouleur.keyID
            if (isMainExpandedColor) {
                val context = LocalContext.current
                val videoRatio = remember(imageFile) {
                    try {
                        MediaMetadataRetriever().use { mmr ->
                            mmr.setDataSource(imageFile.absolutePath)
                            val w = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toFloatOrNull() ?: 9f
                            val h = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toFloatOrNull() ?: 16f
                            val rotation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
                            if (rotation == 90 || rotation == 270) h / w else w / h
                        }
                    } catch (e: Exception) {
                        9f / 16f
                    }
                }
                val exoPlayer = remember(imageFile) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(Uri.fromFile(imageFile)))
                        prepare()
                        playWhenReady = true
                        repeatMode = Player.REPEAT_MODE_ONE
                        volume = 0f
                    }
                }
                DisposableEffect(imageFile) {
                    onDispose { exoPlayer.release() }
                }
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                        }
                    },
                    update = { view ->
                        view.player = exoPlayer
                    },
                    modifier = completeModifier.aspectRatio(videoRatio)
                )
            } else {
                // Compact: static first frame + play icon
                Box(modifier = completeModifier) {
                    GlideImage(
                        model = imageFile,
                        contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Color thumbnail" },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale
                    ) { it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos, ImageQualite.min_possible) }
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Video",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        } else {
            GlideImage(
                model = imageFile,
                contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Color image" },
                modifier = completeModifier,
                contentScale = contentScale
            ) { it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos, qualite) }
        }
    } else {
        Box(modifier = modifier.fillMaxSize())
    }
}

/**
 * Affichage pour une couleur "panier" (affiche_que_c_don_le_panie == true).
 * S'affiche TOUJOURS (pas seulement quand elle est le bigPresenterCouleur expand) :
 * l'image panier reste en fond, avec un cadre à fond blanc semi-transparent (alpha)
 * par-dessus contenant :
 *  - en haut : le nom du produit (en grand, animation de couleur en boucle infinie,
 *    que la couleur soit le presenter expand ou non),
 *  - en dessous : le prix de vente en texte secondaire, avec les petites images de
 *    monnaie (getPrixDrawables) représentant le prix.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PanierNamePriceDisplay(
    modifier: Modifier = Modifier,
    nomProduit: String,
    prixVente: Double,
    isExpanded: Boolean,
    contentScale: ContentScale
) {
    val infiniteTransition = rememberInfiniteTransition(label = "panierNameColorLoop")
    val colorProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "panierNameColorProgress"
    )

    val nameColor by animateColorAsState(
        targetValue = lerp(
            Color(0xFFE53935), // rouge
            Color(0xFF1E88E5), // bleu
            colorProgress
        ),
        label = "panierNameColor"
    )

    val nameFontSize = if (isExpanded) 26.sp else 18.sp
    val nameLineHeight = if (isExpanded) 28.sp else 20.sp
    val priceFontSize = if (isExpanded) 16.sp else 12.sp
    val coinSize = if (isExpanded) 28.dp else 20.dp
    val boxPadding = if (isExpanded) 12.dp else 8.dp
    val maxNameLines = if (isExpanded) 3 else 2

    val prixDrawables = remember(prixVente) { getPrixDrawables(prixVente.toInt()) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // L'image panier reste en fond, dans tous les cas.
        GlideImage(
            model = R.drawable.panie,
            contentDescription = "Panier",
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        ) {
            it.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.NORMAL)
                .disallowHardwareConfig()
                .format(DecodeFormat.PREFER_RGB_565)
                .override(400)
        }

        Box(
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(boxPadding),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nom du produit : toujours en haut, grand, avec l'animation de couleur,
                // que la couleur soit le presenter expand ou non.
                Text(
                    text = nomProduit,
                    color = nameColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = nameFontSize,
                    lineHeight = nameLineHeight,
                    textAlign = TextAlign.Center,
                    maxLines = maxNameLines,
                    overflow = TextOverflow.Ellipsis
                )

                if (prixVente > 0.0) {
                    Text(
                        text = "تتباع على",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = priceFontSize,
                        lineHeight = priceFontSize,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (prixDrawables.isNotEmpty()) {
                        androidx.compose.foundation.layout.Row(
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            prixDrawables.forEach { res ->
                                Image(
                                    painter = painterResource(id = res),
                                    contentDescription = null,
                                    modifier = Modifier.size(coinSize)
                                )
                            }
                        }
                    }
                    Text(
                        text = "و كيما تحب",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = priceFontSize,
                        lineHeight = priceFontSize,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
