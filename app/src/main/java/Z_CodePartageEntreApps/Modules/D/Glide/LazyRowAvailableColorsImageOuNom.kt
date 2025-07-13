package Z_CodePartageEntreApps.Modules.D.Glide

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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

@Composable
fun LazyRowAvailableColorsImageOuNom(
    productId: String?,
    achats: List<M10OperationVentCouleur>,
    bProduitDataBaseComposeRepositoryPJ17: RepoM1Produit,
    sizeDeChaqueItem: Dp= 250.dp,
) {
    val relatedProduitDataBase = bProduitDataBaseComposeRepositoryPJ17
        .datasValue
        .find { it.bsonObjectId == productId }

    createCouleurInfosFromProduct(
        relatedProduitDataBase,
        achats
    ).let { couleurInfos->
        if (couleurInfos.couleurInfosList.isNotEmpty()) {

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(couleurInfos.couleurInfosList) { couleurInfo ->
                    Column(
                        modifier = Modifier.width(sizeDeChaqueItem),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ImageDisplayer(
                            couleurInfo = couleurInfo,
                            modifier = Modifier
                                .width(sizeDeChaqueItem)
                                .height(sizeDeChaqueItem),
                            contentScale = if (couleurInfo.aAffiche!= Affiche.Image) ContentScale.Crop else ContentScale.Fit,
                            imageSize = DpSize(sizeDeChaqueItem, sizeDeChaqueItem),
                        )

                        Infos(achat = couleurInfos.matchingAchat)
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
    couleurInfo: FileCouleurInfos? = null,
    contentScale: ContentScale = ContentScale.Fit,
    imageSize: DpSize,
    onClickToOpenWindow: (FileCouleurInfos) -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(true) }
    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    val imageFile = couleurInfo?.imageCouleurFichie
    val imageExists = couleurInfo?.aAffiche != (Affiche.Image ?: false)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .clickable {
                    if (couleurInfo != null) {
                        onClickToOpenWindow(couleurInfo)
                    }
                }
                .size(imageSize.width, imageSize.height)
        ) {
            if (imageExists && imageFile != null) {
                GlideImage(
                    model = imageFile,
                    contentDescription = null,
                    contentScale = contentScale,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .graphicsLayer {
                            if (blurRadius > 0f) {
                                renderEffect =
                                    BlurEffect(blurRadius, blurRadius, TileMode.Decal)
                            }
                        }
                ) { request ->
                    request.apply {
                        thumbnail(0.1f)
                        transition(DrawableTransitionOptions.withCrossFade())
                        diskCacheStrategy(DiskCacheStrategy.ALL)
                        priority(Priority.HIGH)
                        signature(ObjectKey(couleurInfo.imageCouleurFichie))
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
                                if (isFirstResource) isLoading = false
                                return false
                            }
                        })
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = couleurInfo?.nomCouleurStrSiSonImageDispo ?: "No Color",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun Infos(achat: M10OperationVentCouleur?, modifier: Modifier = Modifier) {
    achat?.let { achatData ->
        Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Qty: ${achatData.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${achatData.provisoireMonPrix}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
