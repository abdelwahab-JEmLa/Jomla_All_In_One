package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import Z_CodePartageEntreApps.Modules.D.Glide.Proto.CalculeCouleurHandler
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageDisplayerProtoAvantJuin3(
    relative_M1Produit: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    viewModel: HeadViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repoMainGetter: RepositorysMainGetter = viewModel.aCentralFacade.repoMainGetter,
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
    val relative_M3Couleur = repoMainGetter.find_M3Couleur_By(relative_M1Produit, indexColor)
    val enablePerformAutoClickImageDisplayer =
        viewModel.aCentralFacade.repoMainGetter.parametresAppComptNonSaved.enablePerformAutoClickImageDisplayer

    val baseFileName =
        "${relative_M1Produit.id}_${if (indexColor == -1) "Unite" else (indexColor + 1)}"

    val a_ProduitModelRepository = viewModelInitApp.produitModelRepository

    val produitDepuitNewDATABASE = a_ProduitModelRepository
        .modelDatas.find { it.id == relative_M1Produit.id }

    var currentQuality by remember { mutableStateOf(5f) }
    var isLoading by remember { mutableStateOf(true) }
    var imageLoaded by remember { mutableStateOf(false) }
    var hasPerformedAutoClick by remember { mutableStateOf(false) }

    // Calculate the initial and target quality based on device capabilities and image size
    val initialQuality = remember(imageSize) {
        // Start with lower quality for faster initial load
        (imageSize.width.value * imageSize.height.value / 10000).coerceIn(5f, 25f)
    }

    val targetQuality = remember(finalequalityImagePourcentage) {
        // Use the finalequalityImagePourcentage parameter to determine final quality
        finalequalityImagePourcentage.toFloat().coerceIn(30f, 100f)
    }

    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    LaunchedEffect(reloadKey) {
        isLoading = true
        imageLoaded = false
        currentQuality = initialQuality
        hasPerformedAutoClick = false

        // Progressive loading strategy
        delay(300) // Initial loading delay
        currentQuality = targetQuality // Transition to target quality
        imageLoaded = true

        delay(700) // Keep blur for 700ms after image loads
        isLoading = false
    }

    val imagePath by remember(viewModel.viewModelImagesPath, relative_M1Produit.id, indexColor) {
        derivedStateOf {
            File(viewModel.viewModelImagesPath, baseFileName)
        }
    }

    val imageFile by produceState<File?>(
        initialValue = null,
        key1 = imagePath,
        key2 = reloadKey
    ) {
        value = withContext(Dispatchers.IO) {
            listOf("jpg", "webp")
                .asSequence()
                .map { ext -> File("${imagePath.absolutePath}.$ext") }
                .firstOrNull { it.exists() && it.canRead() }
        }
    }

    LaunchedEffect(imageLoaded, isLoading, enablePerformAutoClickImageDisplayer) {
        if (enablePerformAutoClickImageDisplayer && imageLoaded && !isLoading && !hasPerformedAutoClick) {
            hasPerformedAutoClick = true
            val focusedVarsHandlerFacade = viewModel.aCentralFacade.focusedActiveValuesFacade
            focusedVarsHandlerFacade.focusedValuesSetter.active_CurrentApp_activeDialogSearchM1Produit(
                true
            )
            focusedVarsHandlerFacade.focusedValuesSetter.set_Current_startTextSearchM1Produit(
                relative_M1Produit.nom
            )
            onClickToOpenWindow()
        }
    }

    Box(modifier = modifier.size(width = imageSize.width, height = imageSize.height)) {
        imageFile?.let { file ->
            val focusedVarsHandlerFacade = viewModel.aCentralFacade.focusedActiveValuesFacade
            val get = focusedVarsHandlerFacade.focusedValuesGetter
            val activeProduit =
                get.focused_M1ProduitInfos_Pour_PrixDifineur

            GlideImage(
                modifier = Modifier
                    .getSemanticsTag_By_datas_A_Affiche_Au_Nom(
                        0,
                        "activeDialogSearchM1Produit",
                        get.activeDialogSearchM1Produit
                    )
                    .getSemanticsTag_By_datas_A_Affiche_Au_Nom(
                        1,
                        "produit",
                        relative_M1Produit.getDebugInfos()
                    )
                    .getSemanticsTag_By_datas_A_Affiche_Au_Nom(
                        1,
                        "activeProduit",
                        activeProduit?.getDebugInfos() ?: "null"
                    )
                    .clickable {
                        // Manual click handler - always available
                        focusedVarsHandlerFacade.focusedValuesSetter.active_CurrentApp_activeDialogSearchM1Produit(
                            true
                        )
                        focusedVarsHandlerFacade.focusedValuesSetter.set_Current_startTextSearchM1Produit(
                            relative_M1Produit.nom
                        )
                        focusedVarsHandlerFacade.focusedValuesSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
                            relative_M1Produit
                        )

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
                    applyImageOptions(
                        relative_M1Produit,
                        indexColor,
                        currentQuality
                    ) { isFirstResource ->
                        if (isFirstResource && currentQuality < targetQuality) {
                            currentQuality = targetQuality
                        }
                    }
                }
            }
        }


        if (showOverlay) {
            val productImageInfos =
                calculeCouleurHandler.getProduitInfoImageParIndex(relative_M1Produit)
            val currentColorInfo = productImageInfos.getOrNull(indexColor)

            currentColorInfo?.let { colorInfo ->
                ColorOverlayWithBlur(
                    color = colorInfo,
                    cornerRadius = cornerRadius,
                )
            }
        }
    }

    produitDepuitNewDATABASE?.let { produit ->
        if (produit.probablementNonDispo) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFA000).copy(alpha = 0.8f),
                        shape = RoundedCornerShape(
                            topStart = cornerRadius,
                            topEnd = cornerRadius
                        )
                    )
            ) {
                Text(
                    text = "احتمال انو غير متوفر لكن نحاولو نبحثولك عليه",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ColorOverlayWithBlur(
    color: CalculeCouleurHandler.ProductImageInfo,
    cornerRadius: Dp,
) {
    Box {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color.White) // White background holder
                .graphicsLayer {
                    renderEffect = BlurEffect(
                        radiusX = 25f,
                        radiusY = 25f,
                        edgeTreatment = TileMode.Decal
                    )
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
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius)),
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
    var fontSize by remember(text) {
        mutableStateOf(style.fontSize)
    }

    var previousFontSize by remember {
        mutableStateOf(fontSize)
    }

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
    .thumbnail(
        this.clone()
            .transform(jp.wasabeef.glide.transformations.BlurTransformation(10))
    )
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
        ) = false

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            onResourceReady(isFirstResource)
            return false
        }
    })
