package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import android.util.Log
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
    calculeCouleurHandler: CalculeCouleurHandler = koinInject()
) {
    val TAG = "MainDisplayComponent"

    var imageFiles by remember { mutableStateOf<List<ProductImageInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentActualiseSonImage by remember { mutableStateOf(0) }

    val allProductImages by calculeCouleurHandler.productImageInfoFlowList.collectAsState()

    Log.d(TAG, "=== Main Display Component Render ===")
    Log.d(TAG, "Parameters:")
    Log.d(TAG, "  produitVID: $produitVID")
    Log.d(TAG, "  produitNom: $produitNom")
    Log.d(TAG, "  product: ${product?.id} (${product?.nom})")
    Log.d(TAG, "  product.actualiseSonImage: ${product?.actualiseSonImage}")
    Log.d(TAG, "  size: $size")
    Log.d(TAG, "  qualityImage: $qualityImage")
    Log.d(TAG, "Current state:")
    Log.d(TAG, "  imageFiles.size: ${imageFiles.size}")
    Log.d(TAG, "  isLoading: $isLoading")
    Log.d(TAG, "  currentActualiseSonImage: $currentActualiseSonImage")
    Log.d(TAG, "  allProductImages.size: ${allProductImages.size}")

    LaunchedEffect(produitVID, product, allProductImages, product?.actualiseSonImage) {
        Log.d(TAG, "=== LaunchedEffect triggered ===")
        Log.d(TAG, "Dependencies changed:")
        Log.d(TAG, "  produitVID: $produitVID")
        Log.d(TAG, "  product?.id: ${product?.id}")
        Log.d(TAG, "  product?.actualiseSonImage: ${product?.actualiseSonImage}")
        Log.d(TAG, "  allProductImages.size: ${allProductImages.size}")

        withContext(Dispatchers.IO) {
            val newActualiseSonImage = product?.actualiseSonImage ?: 0

            Log.d(TAG, "Processing in IO context:")
            Log.d(TAG, "  Previous currentActualiseSonImage: $currentActualiseSonImage")
            Log.d(TAG, "  New actualiseSonImage: $newActualiseSonImage")

            Log.d(TAG, "Calling calculeCouleurHandler.getImageFilesForDisplay...")
            val newImageFiles = calculeCouleurHandler.getImageFilesForDisplay(
                produitVID = produitVID,
                product = product,
                produitNom = produitNom
            )

            Log.d(TAG, "Received ${newImageFiles.size} image files:")
            newImageFiles.forEachIndexed { index, imageInfo ->
                Log.d(TAG, "  [$index] File: ${imageInfo.file.name}")
                Log.d(TAG, "       Exists: ${imageInfo.exists}")
                Log.d(TAG, "       Color: ${imageInfo.couleurId} (${imageInfo.colorName})")
                Log.d(TAG, "       actualiseSonImage: ${imageInfo.actualiseSonImage}")
                Log.d(TAG, "       shouldShowColorText: ${imageInfo.shouldShowColorText}")
            }

            imageFiles = newImageFiles
            currentActualiseSonImage = newActualiseSonImage
            isLoading = false

            Log.d(TAG, "State updated:")
            Log.d(TAG, "  imageFiles.size: ${imageFiles.size}")
            Log.d(TAG, "  currentActualiseSonImage: $currentActualiseSonImage")
            Log.d(TAG, "  isLoading: $isLoading")
        }

        Log.d(TAG, "=== LaunchedEffect completed ===")
    }

    Box(
        modifier = modifier.then(size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize()),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageFiles.isEmpty() || isLoading -> {
                Log.d(TAG, "Displaying placeholder - isEmpty: ${imageFiles.isEmpty()}, isLoading: $isLoading")
                OnImageExistPas()
            }
            imageFiles.size > 1 -> {
                Box(contentAlignment = Alignment.Center) {
                    MultipleImagesDisplay(
                        imageFiles = imageFiles,
                        size = size,
                        qualityImage = qualityImage,
                        onLoadComplete = onLoadComplete,
                        actualiseSonImage = currentActualiseSonImage // FIXED: Pass to force refresh
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
                    imageInfo = imageFiles.first(),
                    qualityImage = qualityImage,
                    onLoadComplete = onLoadComplete,
                    actualiseSonImage = currentActualiseSonImage // FIXED: Pass to force refresh
                )
            }
        }
    }
}
