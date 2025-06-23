package Z_CodePartageEntreApps.Modules.D.Glide.Module

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.CouleurInfos
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


@Composable
fun LazyRowAvailableColorsImageOuNom(
    data: ArticlesBasesStatsTable,
    couleurInfos: List<CouleurInfos>,
    reloadTrigger: Int=0,
    infos: @Composable () -> Unit,  // Changed from Unit to () -> Unit
) {
    // Filter out colors with zero availability count
    val availableCouleurInfos = couleurInfos.filter { it.countDeDisponibility > 0 }

    if (availableCouleurInfos.isEmpty()) {
        return // Don't show anything if no colors are available
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(availableCouleurInfos) { couleurInfo ->
            val colorIndex = getColorIndexFromCouleurInfo(couleurInfo, data)
            val imageExists = remember(data.id, colorIndex, reloadTrigger) {
                couleurInfo.imageCouleurFichie.exists() &&
                        couleurInfo.imageCouleurFichie.canRead() &&
                        couleurInfo.imageCouleurFichie.length() > 0
            }

            Column(
                modifier = Modifier.width(250.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageDisplayer(
                    article = data,
                    colorIndex = colorIndex,
                    couleurInfo = couleurInfo,
                    modifier = Modifier
                        .width(250.dp)
                        .height(if (!imageExists) 70.dp else 250.dp),
                    contentScale = if (!imageExists) ContentScale.Crop else ContentScale.Fit,
                    imageSize = DpSize(
                        width = 250.dp,
                        height = if (!imageExists) 70.dp else 250.dp
                    ),
                )

                infos()
            }
        }
    }
}


@SuppressLint("CheckResult")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageDisplayer(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTable,
    colorIndex: Int,
    couleurInfo: CouleurInfos? = null,
    onClickToOpenWindow: (ArticlesBasesStatsTable, Int) -> Unit = { _, _ -> },
    contentScale: ContentScale = ContentScale.Fit,
    imageSize: DpSize,
) {
    var isLoading by remember { mutableStateOf(true) }

    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val fileName = "${article.id}_${if (colorIndex == -1) "Unite" else (colorIndex + 1)}"

    val imageFile by produceState<File?>(null, article.id, colorIndex) {
        value = withContext(Dispatchers.IO) {
            couleurInfo?.imageCouleurFichie?.takeIf {
                it.exists() && it.canRead() && it.length() > 0
            } ?: listOf("jpg", "webp", "jpeg", "png")
                .map { File("$basePath/$fileName.$it") }
                .firstOrNull { it.exists() && it.canRead() && it.length() > 0 }
        }
    }

    val imageExists = remember(article.id, colorIndex, couleurInfo) {
        couleurInfo?.imageCouleurFichie?.let {
            it.exists() && it.canRead() && it.length() > 0
        } ?: listOf("jpg", "webp", "jpeg", "png").any { ext ->
            File("$basePath/$fileName.$ext").run { exists() && canRead() && length() > 0 }
        }
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .clickable { onClickToOpenWindow(article, colorIndex) }
                .size(imageSize.width, imageSize.height)
        ) {
            if (imageExists && imageFile != null) {
                GlideImage(
                    model = imageFile,
                    contentDescription = "Article ${article.id}",
                    contentScale = contentScale,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .graphicsLayer {
                            if (blurRadius > 0f) {
                                renderEffect = BlurEffect(blurRadius, blurRadius, TileMode.Decal)
                            }
                        }
                ) { request ->
                    request.apply {
                        thumbnail(0.1f)
                        transition(DrawableTransitionOptions.withCrossFade())
                        diskCacheStrategy(DiskCacheStrategy.ALL)
                        priority(Priority.HIGH)
                        signature(ObjectKey("${article.id}_${colorIndex}"))
                        listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean) = false
                            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                if (isFirstResource) isLoading = false
                                return false
                            }
                        })
                    }
                }
            } else {
                // Show color name or fallback when no image exists
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = couleurInfo?.nomSiDispo ?: getColorNameForIndex(article, colorIndex) ?: "No Color",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// Helper function to get color index from CouleurInfo
private fun getColorIndexFromCouleurInfo(couleurInfo: CouleurInfos, article: ArticlesBasesStatsTable): Int {
    return when (couleurInfo.nomSiDispo) {
        article.couleur1 -> 0
        article.couleur2 -> 1
        article.couleur3 -> 2
        article.couleur4 -> 3
        else -> -1
    }
}

// Helper function to get color name by index
private fun getColorNameForIndex(article: ArticlesBasesStatsTable, index: Int): String? {
    return when (index) {
        0 -> article.couleur1
        1 -> article.couleur2
        2 -> article.couleur3
        3 -> article.couleur4
        else -> null
    }
}

// Fixed: Added the missing function
fun getProduitInfoImageParIndex(article: ArticlesBasesStatsTable): List<String> {
    return listOfNotNull(
        article.couleur1?.takeIf { it.isNotBlank() },
        article.couleur2?.takeIf { it.isNotBlank() },
        article.couleur3?.takeIf { it.isNotBlank() },
        article.couleur4?.takeIf { it.isNotBlank() }
    )
}
