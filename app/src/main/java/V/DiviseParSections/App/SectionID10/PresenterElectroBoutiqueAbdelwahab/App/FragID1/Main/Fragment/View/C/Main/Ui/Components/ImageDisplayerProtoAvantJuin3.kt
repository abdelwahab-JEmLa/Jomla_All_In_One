package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Modules.D.Glide.Proto.CalculeCouleurHandler
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File

private const val TAG = "ImageDisplayer"

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageDisplayerProtoAvantJuin3(
    relative_M1Produit: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    viewModel: HeadViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repoMainGetter: RepositorysMainGetter = viewModel.aCentralFacade.repositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    calculeCouleurHandler: CalculeCouleurHandler = koinInject(),
    indexColor: Int,
    reloadKey: Any,
    showOverlay: Boolean,
    imageScale: ContentScale = ContentScale.Fit,
    cornerRadius: Dp = 4.dp,
    imageSize: DpSize,
    finalequalityImagePourcentage: Int = 100,
    viewModelInitApp: ViewModelInitApp,
    onClickToOpenWindow: () -> Unit = {},
) {
    // Log initial parameters
    Log.d(TAG, "═══════════════════════════════════════════════════════")
    Log.d(TAG, "ImageDisplayer INIT - Product: ${relative_M1Produit.id} '${relative_M1Produit.nom}'")
    Log.d(TAG, "  ├─ indexColor: $indexColor")
    Log.d(TAG, "  ├─ reloadKey: $reloadKey")
    Log.d(TAG, "  ├─ showOverlay: $showOverlay")
    Log.d(TAG, "  ├─ imageSize: ${imageSize.width} x ${imageSize.height}")
    Log.d(TAG, "  └─ quality: $finalequalityImagePourcentage%")

    val relative_M9AppCompt = focusedValuesGetter.currentActive_M9AppCompt
    val enablePerformAutoClickImageDisplayer =
        viewModel.aCentralFacade.repositorysMainGetter.repo18CentralParametresOfAllApps.dataValue?.enablePerformAutoClickImageDisplayer

    val a_ProduitModelRepository = viewModelInitApp.produitModelRepository
    val produitDepuitNewDATABASE = a_ProduitModelRepository.modelDatas.find { it.id == relative_M1Produit.id }

    var currentQuality by remember { mutableStateOf(5f) }
    var isLoading by remember { mutableStateOf(true) }
    var imageLoaded by remember { mutableStateOf(false) }
    var hasPerformedAutoClick by remember { mutableStateOf(false) }

    val initialQuality = remember(imageSize) {
        (imageSize.width.value * imageSize.height.value / 10000).coerceIn(5f, 25f)
    }

    val targetQuality = remember(finalequalityImagePourcentage) {
        finalequalityImagePourcentage.toFloat().coerceIn(30f, 100f)
    }

    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    LaunchedEffect(reloadKey) {
        Log.d(TAG, "LaunchedEffect triggered for Product ${relative_M1Produit.id}, Color $indexColor")
        isLoading = true
        imageLoaded = false
        currentQuality = initialQuality
        hasPerformedAutoClick = false
        Log.d(TAG, "  └─ Loading started, initial quality: $initialQuality")
        delay(300)
        currentQuality = targetQuality
        imageLoaded = true
        Log.d(TAG, "  └─ Image marked as loaded, target quality: $targetQuality")
        delay(700)
        isLoading = false
        Log.d(TAG, "  └─ Loading complete")
    }

    val colorInfo = remember(relative_M1Produit.keyID, indexColor) {
        if (indexColor == -1) {
            Log.d(TAG, "Product ${relative_M1Produit.id}: Using Unite image (indexColor=-1)")
            null
        } else {
            val info = repoMainGetter.repo03CouleurProduitInfos.datasValue.find {
                it.parentBProduitInfosKeyID == relative_M1Produit.keyID &&
                        it.indexCouleurDansAncienProto == indexColor
            }
            Log.d(TAG, "Product ${relative_M1Produit.id}, Color $indexColor:")
            Log.d(TAG, "  ├─ ColorInfo found: ${info != null}")
            if (info != null) {
                Log.d(TAG, "  ├─ keyID: ${info.keyID}")
                Log.d(TAG, "  ├─ nomImageFichieSansEtansion: ${info.nomImageFichieSansEtansion}")
                Log.d(TAG, "  ├─ extensionDisponible: ${info.extensionDisponible}")
                Log.d(TAG, "  └─ dernierTimeTampsSynchronisation: ${info.dernierTimeTampsSynchronisationAvecFireBase}")
            }
            info
        }
    }

    val baseFileName = remember(colorInfo, relative_M1Produit.id, indexColor) {
        val fileName = when {
            indexColor == -1 -> "${relative_M1Produit.id}_Unite"
            colorInfo != null && colorInfo.nomImageFichieSansEtansion != "Non Dispo" ->
                colorInfo.nomImageFichieSansEtansion
            else -> "${relative_M1Produit.id}_${indexColor}"
        }
        Log.d(TAG, "Product ${relative_M1Produit.id}, Color $indexColor: baseFileName = '$fileName'")
        fileName
    }

    val imagePath by remember(viewModel.viewModelImagesPath, baseFileName) {
        derivedStateOf {
            val path = File(viewModel.viewModelImagesPath, baseFileName)
            Log.d(TAG, "Product ${relative_M1Produit.id}: imagePath = ${path.absolutePath}")
            path
        }
    }

    val imageFile by produceState<File?>(
        initialValue = null,
        key1 = imagePath,
        key2 = reloadKey,
        key3 = colorInfo?.dernierTimeTampsSynchronisationAvecFireBase
    ) {
        Log.d(TAG, "─────────────────────────────────────────────────────")
        Log.d(TAG, "Searching for image file: Product ${relative_M1Produit.id}, Color $indexColor")

        value = withContext(Dispatchers.IO) {
            val extensions = if (colorInfo != null) {
                listOf(colorInfo.extensionDisponible, "webp", "jpg").distinct()
            } else {
                listOf("webp", "jpg")
            }
            Log.d(TAG, "  ├─ Extensions to try: $extensions")

            val requestedImage = extensions
                .asSequence()
                .map { ext -> File("${imagePath.absolutePath}.$ext") }
                .onEach { file ->
                    Log.d(TAG, "  ├─ Checking: ${file.name}")
                    Log.d(TAG, "  │  ├─ exists: ${file.exists()}")
                    Log.d(TAG, "  │  ├─ canRead: ${file.canRead()}")
                    Log.d(TAG, "  │  └─ size: ${if (file.exists()) file.length() else 0} bytes")
                }
                .firstOrNull { it.exists() && it.canRead() }

            if (requestedImage != null) {
                Log.d(TAG, "  ✓ FOUND primary image: ${requestedImage.name}")
                requestedImage
            } else {
                Log.d(TAG, "  ✗ Primary image NOT FOUND")

                if (indexColor != -1) {
                    Log.d(TAG, "  └─ Searching for fallback images...")
                    val allColorsForProduct = repoMainGetter.repo03CouleurProduitInfos.datasValue
                        .filter { it.parentBProduitInfosKeyID == relative_M1Produit.keyID }
                        .filter { it.nomImageFichieSansEtansion != "Non Dispo" }
                        .sortedBy { it.indexCouleurDansAncienProto }

                    Log.d(TAG, "     Available colors for this product: ${allColorsForProduct.size}")

                    val fallback = allColorsForProduct
                        .asSequence()
                        .filter { it.keyID != colorInfo?.keyID }
                        .onEach { fallbackColor ->
                            Log.d(TAG, "     Trying fallback: ${fallbackColor.nomImageFichieSansEtansion} (index ${fallbackColor.indexCouleurDansAncienProto})")
                        }
                        .mapNotNull { fallbackColor ->
                            val fallbackPath = File(viewModel.viewModelImagesPath, fallbackColor.nomImageFichieSansEtansion)
                            val fallbackExtensions = listOf(fallbackColor.extensionDisponible, "webp", "jpg").distinct()
                            fallbackExtensions
                                .asSequence()
                                .map { ext -> File("${fallbackPath.absolutePath}.$ext") }
                                .onEach { file ->
                                    Log.d(TAG, "       ├─ ${file.name}: exists=${file.exists()}, canRead=${file.canRead()}")
                                }
                                .firstOrNull { it.exists() && it.canRead() }
                        }
                        .firstOrNull()

                    if (fallback != null) {
                        Log.d(TAG, "     ✓ FOUND fallback image: ${fallback.name}")
                    } else {
                        Log.d(TAG, "     ✗ NO fallback images found")
                    }
                    fallback
                } else {
                    Log.d(TAG, "  └─ Unite image, no fallback search")
                    null
                }
            }
        }

        Log.d(TAG, "─────────────────────────────────────────────────────")
        Log.d(TAG, "Final result for Product ${relative_M1Produit.id}, Color $indexColor:")
        Log.d(TAG, "  └─ imageFile: ${value?.name ?: "NULL"}")
        Log.d(TAG, "═══════════════════════════════════════════════════════")
    }

    LaunchedEffect(imageLoaded, isLoading, enablePerformAutoClickImageDisplayer) {
        if (enablePerformAutoClickImageDisplayer == true && imageLoaded && !isLoading && !hasPerformedAutoClick) {
            hasPerformedAutoClick = true
            Log.d(TAG, "Auto-click triggered for Product ${relative_M1Produit.id}")
            val focusedVarsHandlerFacade = viewModel.aCentralFacade.focusedActiveValuesFacade
            focusedVarsHandlerFacade.focusedValuesSetter.active_CurrentApp_activeDialogSearchM1Produit(true)
            focusedVarsHandlerFacade.focusedValuesSetter.set_Current_startTextSearchM1Produit(relative_M1Produit.nom)
            onClickToOpenWindow()
        }
    }

    Box(modifier = modifier.size(width = imageSize.width, height = imageSize.height)) {
        imageFile?.let { file ->
            Log.d(TAG, "Rendering GlideImage for Product ${relative_M1Produit.id}, file: ${file.name}")
            val focusedVarsHandlerFacade = viewModel.aCentralFacade.focusedActiveValuesFacade
            GlideImage(
                modifier = Modifier
                    .getSemanticsTag(relative_M1Produit, "")
                    .clickable {
                        Log.d(TAG, "Image clicked: Product ${relative_M1Produit.id}, Color $indexColor")
                        focusedVarsHandlerFacade.focusedValuesSetter.active_CurrentApp_activeDialogSearchM1Produit(true)
                        focusedVarsHandlerFacade.focusedValuesSetter.set_Current_startTextSearchM1Produit(relative_M1Produit.nom)
                        focusedVarsHandlerFacade.focusedValuesSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(relative_M1Produit)
                        onClickToOpenWindow()
                    }
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .graphicsLayer {
                        if (blurRadius > 0f) {
                            renderEffect = BlurEffect(
                                radiusX = blurRadius,
                                radiusY = blurRadius,
                                edgeTreatment = TileMode.Decal
                            )
                        }
                    },
                model = file,
                contentDescription = file.toString(),
                contentScale = imageScale
            ) {
                it.apply {
                    applyImageOptions(relative_M1Produit, indexColor, currentQuality) { isFirstResource ->
                        if (isFirstResource && currentQuality < targetQuality) {
                            currentQuality = targetQuality
                        }
                    }
                }
            }
        } ?: run {
            Log.w(TAG, "⚠ No image file to display for Product ${relative_M1Produit.id}, Color $indexColor")
        }

        if (indexColor == 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = 0.6f))
                        .clickable { }
                        .padding(4.dp)
                )
            }
        }

        if (showOverlay) {
            val productImageInfos = calculeCouleurHandler.getProduitInfoImageParIndex(relative_M1Produit)
            val currentColorInfo = productImageInfos.getOrNull(indexColor)
            currentColorInfo?.let { colorInfo ->
                Log.d(TAG, "Showing overlay for Product ${relative_M1Produit.id}, Color $indexColor")
                ColorOverlayWithBlur(color = colorInfo, cornerRadius = cornerRadius)
            }
        }
    }

    produitDepuitNewDATABASE?.let { produit ->
        if (produit.probablementNonDispo) {
            Log.d(TAG, "Product ${relative_M1Produit.id} marked as probablement non dispo")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFA000).copy(alpha = 0.8f),
                        shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
                    )
            ) {
                Text(
                    text = "احتمال انو غير متوفر لكن نحاولو نبحثولك عليه",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ColorOverlayWithBlur(color: CalculeCouleurHandler.ProductImageInfo, cornerRadius: Dp) {
    Box {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color.White)
                .graphicsLayer {
                    renderEffect = BlurEffect(radiusX = 25f, radiusY = 25f, edgeTreatment = TileMode.Decal)
                }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color.Black.copy(alpha = 0.15f))
        )
        ColorOverlay(
            color = color,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(cornerRadius))
        )
    }
}

@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    maxLines: Int = Int.MAX_VALUE
) {
    var fontSize by remember(text) { mutableStateOf(style.fontSize) }
    var previousFontSize by remember { mutableStateOf(fontSize) }

    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                previousFontSize = fontSize
                fontSize *= 0.9f
            } else if (fontSize != previousFontSize) {
                previousFontSize = fontSize
            }
        }
    )
}

fun RequestBuilder<Drawable>.applyImageOptions(
    article: ArticlesBasesStatsTable,
    indexColor: Int,
    quality: Float,
    onResourceReady: (Boolean) -> Unit
) = this
    .thumbnail(this.clone().transform(jp.wasabeef.glide.transformations.BlurTransformation(10)))
    .transition(DrawableTransitionOptions.withCrossFade())
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .priority(Priority.HIGH)
    .signature(ObjectKey("${article.id}_${indexColor}_${quality}"))
    .listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ): Boolean {
            Log.e(TAG, "════════════════════════════════════════════════════")
            Log.e(TAG, "✗ GLIDE LOAD FAILED for Product ${article.id}, Color $indexColor")
            Log.e(TAG, "  ├─ model: $model")
            Log.e(TAG, "  ├─ isFirstResource: $isFirstResource")
            if (e != null) {
                Log.e(TAG, "  ├─ Exception: ${e.message}")
                e.rootCauses.forEachIndexed { index, cause ->
                    Log.e(TAG, "  ├─ Root cause $index: ${cause.message}")
                }
                e.logRootCauses(TAG)
            }
            Log.e(TAG, "════════════════════════════════════════════════════")
            return false
        }

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            Log.d(TAG, "✓ GLIDE SUCCESS for Product ${article.id}, Color $indexColor")
            Log.d(TAG, "  ├─ dataSource: $dataSource")
            Log.d(TAG, "  ├─ isFirstResource: $isFirstResource")
            Log.d(TAG, "  └─ quality: $quality")
            onResourceReady(isFirstResource)
            return false
        }
    })
