package Z_CodePartageEntreApps.Modules.D.Glide.Module

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.D_AchatOperation
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.ArticlesBasesStatsTable
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
import kotlin.random.Random

@Composable
fun LazyRowAvailableColorsImageOuNom(
    data: ArticlesBasesStatsTable,
    reloadTrigger: Int,
    onAddOrUpdateData_achatOperationComposeRepository: (D_AchatOperation) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        val availableColors =
            (0..3).filter { data.getColorIdForIndex(it) != null }
        items(availableColors.size) { index ->
            val colorIndex = availableColors[index]
            val imageExists =
                remember(data.id, colorIndex, reloadTrigger) {
                    val fileName =
                        "${data.id}_${if (colorIndex == -1) "Unite" else (colorIndex + 1)}"
                    val basePath =
                        "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

                    listOf("jpg", "jpeg", "png", "webp").any { ext ->
                        File("$basePath/$fileName.$ext").run {
                            exists() && canRead() && length() > 0
                        }
                    }
                }

            ImageDisplayer(
                article = data,
                colorIndex = colorIndex,
                modifier = Modifier
                    .width(250.dp)
                    .height(if (!imageExists) 70.dp else 250.dp),
                contentScale = if (!imageExists) ContentScale.Crop else ContentScale.Fit,
                imageSize = DpSize(
                    width = 250.dp,
                    height = if (!imageExists) 70.dp else 250.dp
                ),
                onClickToOpenWindow = { aProduitinfos, indexCouleur ->
                    val randomQuantity = Random.nextInt(1, 11)
                    val newAchatOperation = D_AchatOperation(
                        nomImageFichieOuApellationDuCouleur = determineImageOrColorName(
                            aProduitinfos,
                            indexCouleur
                        ),
                        parentBonVentObjectId = "1",
                        parentComptVendeurCreateurObjectId = "1",
                        parentProduitBsonObjectId = aProduitinfos.bsonObjectId,
                        quantityAchete = randomQuantity,
                        produitAcheterAncienID = aProduitinfos.id,
                    )
                    onAddOrUpdateData_achatOperationComposeRepository(
                        newAchatOperation
                    )
                }
            )
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
            listOf("jpg", "webp", "jpeg", "png")
                .map { File("$basePath/$fileName.$it") }
                .firstOrNull { it.exists() && it.canRead() && it.length() > 0 }
        }
    }

    val imageExists = remember(article.id, colorIndex) {
        listOf("jpg", "webp", "jpeg", "png").any { ext ->
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
            imageFile?.let { file ->
                GlideImage(
                    model = file,
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
            }

            if (!imageExists) {
                val availableColors = getProduitInfoImageParIndex(article)
                availableColors.getOrNull(colorIndex)?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                            .graphicsLayer {
                                renderEffect = BlurEffect(25f, 25f, TileMode.Decal)
                            }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.15f))
                    )
                }
            }
        }
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

fun determineImageOrColorName(produit: ArticlesBasesStatsTable, indexCouleur: Int): String {
    val imagesBasePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val keyImageId = "${produit.id}_${indexCouleur + 1}"

    val extensions = listOf("jpg", "jpeg", "png", "webp")

    // Find the actual file path if it exists
    val actualFilePath = extensions.firstNotNullOfOrNull { ext ->
        val file = File("$imagesBasePath/$keyImageId.$ext")
        if (file.exists() && file.length() > 0) {
            file.absolutePath
        } else {
            null
        }
    }

    return if (actualFilePath != null) {
        actualFilePath
    } else {
        // Return color name if no image file exists
        val colorName = when (indexCouleur) {
            0 -> produit.couleur1?.takeIf { it.isNotBlank() }
            1 -> produit.couleur2?.takeIf { it.isNotBlank() }
            2 -> produit.couleur3?.takeIf { it.isNotBlank() }
            3 -> produit.couleur4?.takeIf { it.isNotBlank() }
            else -> null
        }
        colorName ?: produit.nom
    }
}

fun ArticlesBasesStatsTable.getColorIdForIndex(index: Int): String? =
    when (index) {
        0 -> couleur1?.takeIf { it.isNotBlank() }
        1 -> couleur2?.takeIf { it.isNotBlank() }
        2 -> couleur3?.takeIf { it.isNotBlank() }
        3 -> couleur4?.takeIf { it.isNotBlank() }
        else -> null
    }
