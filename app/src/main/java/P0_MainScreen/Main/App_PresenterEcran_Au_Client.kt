package P0_MainScreen.Main

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ColorNameDisplayer
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.io.File

@SuppressLint("UnrememberedMutableState", "ModifierParameter")
@Composable
fun App_PresenterEcran_Au_Client(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedActiveValuesFacade: FocusedActiveValuesFacade = aCentralFacade.focusedActiveValuesFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    modifier: Modifier = Modifier,
    isWifiClientConnected: Boolean
) {
    val relative_M9AppCompt = focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt
    val productKeyID = relative_M9AppCompt?.active_ProduitKeyID_Au_DroopDown_PresenterEcran

    val shouldShowLogo =
        productKeyID == "-OV3rmTfv1RVCax896N1" || true

    val relative_ListCouleurs =
        productKeyID?.let { repositorysMainGetter.find_ListM3CouleurInfos_By_Parent_Produit_KeyID(it) }

    val activeCouleurKeyID = when {
        relative_M9AppCompt?.active_CouleurKeyID_Extended_Image.isNullOrEmpty() && !shouldShowLogo -> {
            relative_ListCouleurs?.firstOrNull()?.keyID
        }
        else -> relative_M9AppCompt?.active_CouleurKeyID_Extended_Image
    }

    val its_Tablette = Build.MODEL.contains("ncar")      //<--
    //(1): fait que si

    val heights_telep = Pair(420.dp, 160.dp)    //<--
    //(1): si non ca
    val fixedWidth_telep = 310.dp

    val heights = Pair(700.dp, 250.dp)   //<--
    //(1): ca
    val fixedWidth = 450.dp

    fun handelClick(
        repositorysMainSetter: RepositorysMainSetter,
        relative_M9AppCompt: Z_AppCompt,
        isClicked: Boolean,
        couleur: M3CouleurProduitInfos
    ) {
        repositorysMainSetter.update_M9AppCompt(
            relative_M9AppCompt.copy(
                active_CouleurKeyID_Extended_Image = if (isClicked) "" else couleur.keyID
            )
        )
    }

    fun handleCloseClick(
        repositorysMainSetter: RepositorysMainSetter,
        relative_M9AppCompt: Z_AppCompt
    ) {
        repositorysMainSetter.update_M9AppCompt(
            relative_M9AppCompt.copy(
                active_CouleurKeyID_Extended_Image = ""
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Replace the logo Card section with this code:

        if (shouldShowLogo) {
            Card(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {
                        set(value = productKeyID, key = SemanticsPropertyKey("productKeyID"))
                    }
                    .semantics(mergeDescendants = true) {
                        set(value = isWifiClientConnected, key = SemanticsPropertyKey("isWifiClientConnected"))
                    }
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFD22317)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Main text
                        Text(
                            text = "اشري لهنــــآ بالجــــــملة",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Text(
                                text = "عند عبدالوهاب",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(relative_ListCouleurs.orEmpty()) { couleur ->
                    val isClicked = activeCouleurKeyID == couleur.keyID
                    val targetValue = if (isClicked) heights.first else heights.second

                    val animatedHeight by animateDpAsState(
                        targetValue,
                        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                        label = "height"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(animatedHeight)
                            .animateContentSize(
                                spring(
                                    Spring.DampingRatioMediumBouncy,
                                    Spring.StiffnessMedium
                                )
                            ),
                        onClick = {
                            if (relative_M9AppCompt != null) {
                                handelClick(
                                    repositorysMainSetter,
                                    relative_M9AppCompt,
                                    isClicked,
                                    couleur
                                )
                            }
                        }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (isClicked) {
                                IconButton(
                                    onClick = {
                                        if (relative_M9AppCompt != null) {
                                            handleCloseClick(
                                                repositorysMainSetter,
                                                relative_M9AppCompt
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close"
                                    )
                                }
                            }

                            if (isClicked) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .animateContentSize(
                                            spring(
                                                Spring.DampingRatioLowBouncy,
                                                Spring.StiffnessLow
                                            )
                                        ),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CouleurDisplayer(
                                        height = heights.first,
                                        keyCouleur = couleur.keyID,
                                        size = fixedWidth, // Use fixed width instead of animated size
                                        modifier = Modifier
                                            .width(fixedWidth) // Fixed width
                                            .height(animatedHeight), // Animated height only
                                        onClickToChangeSelected = { keyID ->
                                            if (relative_M9AppCompt != null) {
                                                handelClick(
                                                    repositorysMainSetter,
                                                    relative_M9AppCompt,
                                                    activeCouleurKeyID == keyID,
                                                    couleur
                                                )
                                            }
                                        }
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .animateContentSize(
                                            spring(
                                                Spring.DampingRatioMediumBouncy,
                                                Spring.StiffnessMedium
                                            )
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    CouleurDisplayer(
                                        modifier = Modifier
                                            .width(fixedWidth) // Fixed width
                                            .height(animatedHeight), // Animated height only
                                        keyCouleur = couleur.keyID,
                                        size = fixedWidth, // Use fixed width instead of animated size
                                        onClickToChangeSelected = { keyID ->
                                            if (relative_M9AppCompt != null) {
                                                handelClick(
                                                    repositorysMainSetter,
                                                    relative_M9AppCompt,
                                                    activeCouleurKeyID == keyID,
                                                    couleur
                                                )
                                            }
                                        },
                                        height = heights.second
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("CheckResult")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageDisplayer(
    modifier: Modifier = Modifier,
    imageFile: File? = null,
    colorName: String = "",
    contentScale: ContentScale = ContentScale.Crop,
    imageSize: DpSize,
    cornerRadius: Dp = 4.dp,
    finalequalityImagePourcentage: Int = 100,
    reloadKey: Any = Unit,
    onClickToOpenWindow: () -> Unit = {},
) {
    var currentQuality by remember { mutableStateOf(5f) }
    var isLoading by remember { mutableStateOf(true) }

    val targetQuality = finalequalityImagePourcentage.toFloat().coerceIn(30f, 100f)
    val blurRadius by animateFloatAsState(
        if (isLoading) 25f else 0f,
        tween(700),
        label = "blur"
    )

    LaunchedEffect(reloadKey) {
        isLoading = true
        delay(300)
        currentQuality = targetQuality
        delay(700)
        isLoading = false
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .clickable { onClickToOpenWindow() }
                .fillMaxWidth()
                .height(imageSize.height)
        ) {
            if (imageFile?.exists() == true) {
                GlideImage(
                    model = imageFile,
                    contentDescription = "Color image for $colorName",
                    contentScale = contentScale,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(cornerRadius))
                        .graphicsLayer {
                            if (blurRadius > 0f) {
                                renderEffect = BlurEffect(blurRadius, blurRadius, TileMode.Decal)
                            }
                        }
                ) { request ->
                    request.apply {
                        thumbnail(
                            request.clone()
                                .transform(jp.wasabeef.glide.transformations.BlurTransformation(10))
                        )
                        transition(DrawableTransitionOptions.withCrossFade())
                        diskCacheStrategy(DiskCacheStrategy.ALL)
                        priority(Priority.HIGH)
                        signature(ObjectKey("${imageFile.absolutePath}_$currentQuality"))
                        listener(object : RequestListener<Drawable> {
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
                                if (isFirstResource && currentQuality < targetQuality) currentQuality =
                                    targetQuality
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

@SuppressLint("UnrememberedMutableState")
@Composable
fun CouleurDisplayer(
    modifier: Modifier = Modifier,
    keyCouleur: String,
    b1CouleurOuGoutProduitDataBaseRepository: Repo03CouleurProduitInfos = koinInject(),
    size: Dp = 200.dp,
    finalequalityImagePourcentage: Int = 100,
    reloadKey: Any = Unit,
    onClickToOpenWindow: (M3CouleurProduitInfos) -> Unit = {},
    onClickToChangeSelected: (String) -> Unit = {},
    height: Dp
) {
    val data = b1CouleurOuGoutProduitDataBaseRepository.datasValue.find { it.keyID == keyCouleur }!!

    val imageFile by derivedStateOf {
        if (data.nomImageFichieSansEtansion != "Non Dispo") {
            File(
                "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne",
                "${data.nomImageFichieSansEtansion}.${data.extensionDisponible}"
            )
        } else null
    }

    val onClick = {
        onClickToOpenWindow(data)
        onClickToChangeSelected(keyCouleur)
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            when (data.aAffiche) {
                M3CouleurProduitInfos.Type.Image -> ImageDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    imageFile = imageFile,
                    colorName = data.nomCouleurStrSiSonImageDispo,
                    contentScale = ContentScale.Crop,
                    imageSize = DpSize(size, height), // Use the size parameter for width
                    cornerRadius = 4.dp,
                    finalequalityImagePourcentage = finalequalityImagePourcentage,
                    reloadKey = reloadKey,
                    onClickToOpenWindow = onClick
                )

                M3CouleurProduitInfos.Type.Nom -> ColorNameDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    colorName = data.nomCouleurStrSiSonImageDispo,
                    onClickToOpenWindow = onClick
                )
            }
        }
    }
}
