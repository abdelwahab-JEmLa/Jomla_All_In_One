package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.ViewModel.ViewModel_TestID2
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.imagesProduitsLocalExternalStorageBasePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        updateProductImageInfoList()
        scope.launch {
            viewModel.uiState.collect {
                updateProductImageInfoList()
            }
        }
    }

    fun updateProductImageInfoList() {
        _productImageInfoFlowList.value = viewModel.uiState.value.produitInfosList.flatMap { product ->
            getAllDefinedColorsForProduct(product)
        }
    }

    fun findProductById(productId: Long): A_ProduitInfosTest? {
        return viewModel.uiState.value.produitInfosList.find { it.id == productId }
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
                    val allDefinedColors = getAllDefinedColorsForProduct(targetProduct)

                    if (allDefinedColors.isNotEmpty()) {
                        allDefinedColors
                    } else {
                        listOf(ProductImageInfo(defaultFile, 0, false, produitNom ?: targetProduct.nom))
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

    private fun getAllDefinedColorsForProduct(product: A_ProduitInfosTest): List<ProductImageInfo> {
        return (1..4).mapNotNull { couleurId ->
            val colorName = getColorNameById(product, couleurId).takeIf { it.isNotBlank() }

            val keyImageId = "${product.id}_${couleurId}"
            val basePath = "$imagesProduitsLocalExternalStorageBasePath/$keyImageId"

            val imageFile = listOf("jpg", "jpeg", "png", "webp")
                .map { ext -> File("$basePath.$ext") }
                .find { it.exists() && it.length() > 0 }

            if (colorName != null || imageFile != null) {
                ProductImageInfo(
                    file = imageFile ?: File(""),
                    couleurId = couleurId,
                    exists = imageFile != null,
                    colorName = colorName ?: "",
                    shouldShowColorText = imageFile == null && colorName != null,
                    productName = product.nom
                )
            } else {
                null
            }
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
}
