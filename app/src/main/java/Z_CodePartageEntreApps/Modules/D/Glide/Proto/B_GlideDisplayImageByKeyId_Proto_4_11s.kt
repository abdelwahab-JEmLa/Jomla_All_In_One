package Z_CodePartageEntreApps.Modules.D.Glide.Proto

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun A_GlideDisplayImageByKeyId_Proto_5(
    produitVID: Long? = null,
    modifier: Modifier = Modifier,
    produitNom: String? = null,
    size: Dp? = null,
    onLoadComplete: () -> Unit = {},
    qualityImage: Int = 3,
    product: ArticlesBasesStatsTable? = null,
    calculeCouleurHandler: CalculeCouleurHandler = koinInject(),
    refreshImage: Int,
    enableAutoScroll: Boolean = false
) {
    val activeLeChangementInfiniEntreCouleursImages by remember { mutableStateOf(enableAutoScroll) }

    var imageFiles by remember { mutableStateOf<List<CalculeCouleurHandler.ProductImageInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }


    val imageKey by remember(produitVID, product?.id, refreshImage) {
        derivedStateOf {
            val l = produitVID ?: product?.id ?: 0L
            val id =if (l ==0L ) product?.bsonObjectId else product?.id  ?: 0L
            "$id-$refreshImage-${System.currentTimeMillis()}"
        }
    }

    LaunchedEffect(imageKey) {
        withContext(Dispatchers.IO) {
            isLoading = true

            val newImageFiles = calculeCouleurHandler.getImageFilesForDisplay(
                produitVID = produitVID,
                product = product,
                produitNom = produitNom
            )

            imageFiles = newImageFiles
            isLoading = false
        }
    }

    Box(
        modifier = modifier.then(size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize()),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageFiles.isEmpty() || isLoading -> {
                OnImageExistPas()
            }
            imageFiles.size > 1 -> {
                Box(contentAlignment = Alignment.Center) {
                    MultipleImagesDisplay(
                        product = product,
                        imageFiles = imageFiles,
                        size = size,
                        qualityImage = qualityImage,
                        onLoadComplete = onLoadComplete,
                        actualiseSonImage = refreshImage,
                        imageRefreshKey = imageKey,
                        enableAutoScroll = activeLeChangementInfiniEntreCouleursImages // FIXED: Pass the control variable
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .size(20.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Plus d'articles",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
            else -> {
                SingleImageDisplay(
                    product = product,
                    imageInfo = imageFiles.first(),
                    qualityImage = qualityImage,
                    onLoadComplete = onLoadComplete,
                    actualiseSonImage = refreshImage,
                    imageRefreshKey = imageKey
                )
            }
        }
    }
}
