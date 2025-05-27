package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.ViewModel.ViewModel_TestID2
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.imagesProduitsLocalExternalStorageBasePath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

data class ProductImageInfo(
    val file: File,
    val couleurId: Int,
    val exists: Boolean = true,
    val colorName: String = "",
    val shouldShowColorText: Boolean = false,
    val productName: String = ""
)

class CalculeCouleurHandler(private val viewModel: ViewModel_TestID2) {
    private val _productImageInfoFlowList = MutableStateFlow<List<ProductImageInfo>>(emptyList())
    val productImageInfoFlowList: StateFlow<List<ProductImageInfo>> = _productImageInfoFlowList.asStateFlow()

    init {
        updateProductImageInfoList()
    }

    fun updateProductImageInfoList() {
        _productImageInfoFlowList.value = viewModel.uiState.value.produitInfosList.flatMap { product ->
            (1..4).mapNotNull { couleurId ->
                val colorName = getColorNameById(product, couleurId).takeIf { it.isNotBlank() }

                if (colorName == null) return@mapNotNull null

                val keyImageId = "${product.id}_${couleurId}"
                val basePath = "$imagesProduitsLocalExternalStorageBasePath/$keyImageId"

                val imageFile = listOf("jpg", "jpeg", "png", "webp")
                    .map { ext -> File("$basePath.$ext") }
                    .find { it.exists() && it.length() > 0 }

                ProductImageInfo(
                    file = imageFile ?: File(""),
                    couleurId = couleurId,
                    exists = imageFile != null,
                    colorName = colorName,
                    shouldShowColorText = imageFile == null,
                    productName = product.nom
                )
            }
        }
    }

    fun findProductById(productId: Long): A_ProduitInfosTest? {
        return viewModel.uiState.value.produitInfosList.find { it.id == productId }
    }

    fun findAllValidImageFiles(produitVID: Long): List<ProductImageInfo> {
        return _productImageInfoFlowList.value.filter { imageInfo ->
            extractProductIdFromImagePath(imageInfo.file.absolutePath) == produitVID
        }
    }

    fun getImageFilesForDisplay(
        produitVID: Long? = null,
        product: A_ProduitInfosTest? = null,
        produitNom: String? = null
    ): List<ProductImageInfo> {
        return try {
            val defaultFile = File("$imagesProduitsLocalExternalStorageBasePath/logo.webp")
            val shouldUseDefaultImage = produitVID == null && product == null

            if (!shouldUseDefaultImage) {
                val targetProduct = product ?: produitVID?.let { findProductById(it) }

                if (targetProduct != null) {
                    // First, try to get actual image files
                    val productSpecificImages = _productImageInfoFlowList.value.filter { imageInfo ->
                        extractProductIdFromImagePath(imageInfo.file.absolutePath) == targetProduct.id && imageInfo.exists
                    }

                    if (productSpecificImages.isNotEmpty()) {
                        // Return actual images if found
                        productSpecificImages
                    } else {
                        // If no images exist, create color text fallbacks for ALL defined colors
                        val colorFallbacks = createColorTextFallbacks(targetProduct)
                        if (colorFallbacks.isNotEmpty()) {
                            colorFallbacks
                        } else {
                            // If no colors defined, return default
                            listOf(ProductImageInfo(defaultFile, 0, false, produitNom ?: targetProduct.nom))
                        }
                    }
                } else {
                    listOf(ProductImageInfo(defaultFile, 0, false, produitNom ?: ""))
                }
            } else {
                listOf(ProductImageInfo(defaultFile, 0, false, produitNom ?: ""))
            }
        } catch (e: Exception) {
            val defaultFile = File("$imagesProduitsLocalExternalStorageBasePath/logo.webp")
            listOf(ProductImageInfo(defaultFile, 0, false, produitNom ?: ""))
        }
    }

    private fun createColorTextFallbacks(product: A_ProduitInfosTest): List<ProductImageInfo> {
        // Fixed: Create fallbacks for ALL defined colors, not just when images don't exist
        return (1..4).mapNotNull { couleurId ->
            val colorName = getColorNameById(product, couleurId).takeIf { it.isNotBlank() }
            if (colorName != null) {
                ProductImageInfo(
                    file = File(""), // Empty file since we're showing text
                    couleurId = couleurId,
                    exists = false,
                    colorName = colorName,
                    shouldShowColorText = true,
                    productName = product.nom
                )
            } else null
        }
    }

    fun getColorNameById(product: A_ProduitInfosTest?, colorId: Int): String {
        val colorName = when (colorId) {
            1 -> product?.couleur1
            2 -> product?.couleur2
            3 -> product?.couleur3
            4 -> product?.couleur4
            else -> null
        }
        return colorName?.trim()?.takeIf { it.isNotEmpty() } ?: ""
    }

    private fun extractProductIdFromImagePath(path: String): Long? {
        return try {
            val fileName = File(path).nameWithoutExtension
            val parts = fileName.split("_")
            if (parts.size >= 2) {
                parts[0].toLongOrNull()
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
