package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
    var imageFiles by remember { mutableStateOf<List<ProductImageInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val allProductImages by calculeCouleurHandler.productImageInfoFlowList.collectAsState()

    LaunchedEffect(produitVID, product, allProductImages) {
        withContext(Dispatchers.IO) {
            // Use the centralized logic from CalculeCouleurHandler
            imageFiles = calculeCouleurHandler.getImageFilesForDisplay(
                produitVID = produitVID,
                product = product,
                produitNom = produitNom
            )
            isLoading = false
        }
    }

    Box(
        modifier = modifier.then(size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize()),
        contentAlignment = Alignment.Center
    ) {
        if (imageFiles.isEmpty() || isLoading) {
            OnImageExistPas()
        } else if (imageFiles.size == 1) {
            SingleImageDisplay(
                imageInfo = imageFiles.first(),
                qualityImage = qualityImage,
                onLoadComplete = onLoadComplete
            )
        } else {
            MultipleImagesDisplay(
                imageFiles = imageFiles,
                size = size,
                qualityImage = qualityImage,
                onLoadComplete = onLoadComplete
            )
        }
    }
}
