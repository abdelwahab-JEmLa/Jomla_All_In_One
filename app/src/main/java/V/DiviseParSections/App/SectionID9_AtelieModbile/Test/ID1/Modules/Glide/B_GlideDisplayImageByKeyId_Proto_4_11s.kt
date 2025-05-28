package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
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
import androidx.compose.runtime.collectAsState
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
    modifier: Modifier = Modifier,
    produitVID: Long? = null,
    produitNom: String? = null,
    size: Dp? = null,
    onLoadComplete: () -> Unit = {},
    qualityImage: Int = 3,
    product: A_ProduitInfosTest? = null,
    calculeCouleurHandler: CalculeCouleurHandler = koinInject(),
    refreshImage: Int
) {
    var imageFiles by remember { mutableStateOf<List<ProductImageInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val allProductImages by calculeCouleurHandler.productImageInfoFlowList.collectAsState()

    // FIXED: Create consistent refresh key similar to working GlidDisplaye
    val imageKey by remember(produitVID, product?.id, refreshImage, allProductImages.size) {
        derivedStateOf {
            val baseKey = produitVID ?: product?.id ?: 0L
            "$baseKey-$refreshImage-${System.currentTimeMillis()}"
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
                        product=product,

                        imageFiles = imageFiles,
                        size = size,
                        qualityImage = qualityImage,
                        onLoadComplete = onLoadComplete,
                        actualiseSonImage = refreshImage,
                        imageRefreshKey = imageKey // FIXED: Pass consistent refresh key
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
                    product=product,
                    imageInfo = imageFiles.first(),
                    qualityImage = qualityImage,
                    onLoadComplete = onLoadComplete,
                    actualiseSonImage = refreshImage,
                    imageRefreshKey = imageKey // FIXED: Pass consistent refresh key
                )
            }
        }
    }
}
