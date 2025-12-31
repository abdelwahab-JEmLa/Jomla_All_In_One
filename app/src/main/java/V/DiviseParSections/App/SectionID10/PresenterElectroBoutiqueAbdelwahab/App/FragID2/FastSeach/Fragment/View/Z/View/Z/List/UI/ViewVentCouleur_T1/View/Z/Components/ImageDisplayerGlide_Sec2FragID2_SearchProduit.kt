package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View.Z.Components

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ColorNameDisplayer
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
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
import org.koin.compose.koinInject
import java.io.File

private const val TAG = "ImageDisplayerGlide"

@SuppressLint("CheckResult")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageDisplayerGlide_Sec2FragID2_SearchProduit(
    modifier: Modifier = Modifier,
    relative_M3CouleurInfos: M3CouleurProduitInfos? = null,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    imageFile: File? = null,
    colorName: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    imageSize: DpSize,
    colorFilter: ColorFilter? = null,
    onClickToOpenWindow: () -> Unit = {},
    hideImage: Boolean = false
) {
    // Track which products have been displayed to detect duplicates
    val displayTracker = remember { mutableMapOf<String, Int>() }

    var isLoading by remember { mutableStateOf(true) }
    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    val imageExists = imageFile?.exists() == true

    // Log image display for the same product
    LaunchedEffect(relative_M3CouleurInfos, imageFile) {
        relative_M3CouleurInfos?.let { couleurInfo ->
            val productKey = "${couleurInfo.parentBProduitOldID}_${couleurInfo.indexCouleurDansAncienProto}"
            val displayCount = displayTracker.getOrDefault(productKey, 0) + 1
            displayTracker[productKey] = displayCount

            Log.d(TAG, buildString {
                append("=".repeat(60))
                append("\n")
                append("Image Display Event #$displayCount\n")
                append("-".repeat(60))
                append("\n")
                append("Product Info:\n")
                append("  - Product ID: ${couleurInfo.parentBProduitOldID}\n")
                append("  - Product Name: ${couleurInfo.parentId1ProduitInfosDebugName}\n")
                append("  - Color Index: ${couleurInfo.indexCouleurDansAncienProto}\n")
                append("  - Color Name: ${couleurInfo.nomCouleurStrSiSonImageDispo}\n")
                append("  - Key ID: ${couleurInfo.keyID.takeLast(8)}\n")
                append("\n")
                append("Image Details:\n")
                append("  - File Name: ${imageFile?.name ?: "N/A"}\n")
                append("  - File Exists: $imageExists\n")
                append("  - Hide Image: $hideImage\n")
                append("  - Display Type: ${couleurInfo.aAffiche}\n")
                append("\n")
                if (displayCount > 1) {
                    append("⚠️  WARNING: This product has been displayed $displayCount times!\n")
                }
                append("=".repeat(60))
            })

            // Additional warning log for duplicate displays
            if (displayCount > 1) {
                Log.w(TAG, "Duplicate display detected for product ${couleurInfo.parentBProduitOldID} " +
                        "(${couleurInfo.parentId1ProduitInfosDebugName}) - Display count: $displayCount")
            }
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
                .size(imageSize.width, imageSize.height)
        ) {
            // If hideImage is true, always show ColorNameDisplayer regardless of image existence
            if (hideImage) {
                ColorNameDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    colorName = colorName,
                    onClickToOpenWindow = onClickToOpenWindow
                )
            } else if (imageExists && imageFile != null) {
                GlideImage(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            set(value = imageFile.name, key = SemanticsPropertyKey("imageFile"))
                        }
                        .getSemanticsTag(relative_M3CouleurInfos, "")
                        .clickable {
                            onClickToOpenWindow()
                        }
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .graphicsLayer {
                            if (blurRadius > 0f) {
                                renderEffect =
                                    BlurEffect(blurRadius, blurRadius, TileMode.Decal)
                            }
                        },
                    model = imageFile,
                    contentDescription = "Color image for $colorName",
                    contentScale = contentScale,
                    colorFilter = colorFilter
                ) { request ->
                    request.apply {
                        thumbnail(0.1f)
                        transition(DrawableTransitionOptions.withCrossFade())
                        diskCacheStrategy(DiskCacheStrategy.ALL)
                        priority(Priority.HIGH)
                        signature(ObjectKey(imageFile.absolutePath))
                        listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e(TAG, "Image load failed for: ${imageFile.name}", e)
                                relative_M3CouleurInfos?.let { info ->
                                    Log.e(TAG, "Failed product: ${info.parentId1ProduitInfosDebugName} " +
                                            "(ID: ${info.parentBProduitOldID})")
                                }
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any,
                                target: Target<Drawable>?,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                if (isFirstResource) {
                                    isLoading = false
                                    Log.d(TAG, "Image loaded successfully: ${imageFile.name} " +
                                            "(Source: ${dataSource.name})")
                                }
                                return false
                            }
                        })
                    }
                }
            } else {
                ColorNameDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    colorName = colorName,
                    onClickToOpenWindow = onClickToOpenWindow
                )
            }
        }
    }
}
