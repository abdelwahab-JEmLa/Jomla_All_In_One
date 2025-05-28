package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide

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
    val productName: String = "",
    val actualiseSonImage: Int = 0
)

class CalculeCouleurHandler(private val viewModel: ViewModel_TestID2) {
    private val _productImageInfoFlowList = MutableStateFlow<List<ProductImageInfo>>(emptyList())
    val productImageInfoFlowList: StateFlow<List<ProductImageInfo>> = _productImageInfoFlowList.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        updateProductImageInfoList()
        scope.launch {
            viewModel.uiState.collect { uiState ->
                updateProductImageInfoList()
            }
        }
    }

    fun updateProductImageInfoList() {
        val productsList = viewModel.uiState.value.produitInfosList

        val allImageInfos = productsList.flatMap { product ->
            getAllDefinedColorsForProduct(product)
        }

        _productImageInfoFlowList.value = allImageInfos.toList()
    }

    private fun getAllDefinedColorsForProduct(product: A_ProduitInfosTest): List<ProductImageInfo> {
        val colors = (1..4).mapNotNull { couleurId ->
            val colorName = getColorNameById(product, couleurId).takeIf { it.isNotBlank() }

            val keyImageId = "${product.id}_${couleurId}"
            val basePath = "$imagesProduitsLocalExternalStorageBasePath/$keyImageId"

            val imageFile = listOf("jpg", "jpeg", "png", "webp")
                .map { ext -> File("$basePath.$ext") }
                .find { file ->
                    val exists = file.exists() && file.length() > 0
                    exists
                }

            if (colorName != null || imageFile != null) {
                ProductImageInfo(
                    file = imageFile ?: File(""),
                    couleurId = couleurId,
                    exists = imageFile != null,
                    colorName = colorName ?: "",
                    shouldShowColorText = imageFile == null && colorName != null,
                    productName = product.nom,
                    actualiseSonImage = product.actualiseSonImage
                )
            } else null
        }

        return colors
    }

    fun findProductById(productId: Long): A_ProduitInfosTest? {
        val product = viewModel.uiState.value.produitInfosList.find { it.id == productId }
        return product
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
                        listOf(
                            ProductImageInfo(
                                file = defaultFile,
                                couleurId = 0,
                                exists = false,
                                productName = produitNom ?: targetProduct.nom,
                                actualiseSonImage = targetProduct.actualiseSonImage
                            )
                        )
                    }
                } else {
                    listOf(
                        ProductImageInfo(
                            file = defaultFile,
                            couleurId = 0,
                            exists = false,
                            productName = produitNom ?: "",
                            actualiseSonImage = 0
                        )
                    )
                }
            } else {
                listOf(
                    ProductImageInfo(
                        file = defaultFile,
                        couleurId = 0,
                        exists = false,
                        productName = produitNom ?: "",
                        actualiseSonImage = 0
                    )
                )
            }
        } catch (e: Exception) {
            val defaultFile = File("$imagesProduitsLocalExternalStorageBasePath/logo.webp")
            listOf(
                ProductImageInfo(
                    file = defaultFile,
                    couleurId = 0,
                    exists = false,
                    productName = produitNom ?: "",
                    actualiseSonImage = 0
                )
            )
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
        val trimmedName = colorName?.trim()?.takeIf { it.isNotEmpty() } ?: ""
        return trimmedName
    }
}
