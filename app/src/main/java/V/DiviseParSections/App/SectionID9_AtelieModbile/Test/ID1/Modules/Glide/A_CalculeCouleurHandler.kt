package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.ViewModel.ViewModel_TestID2
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.imagesProduitsLocalExternalStorageBasePath
import android.util.Log
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

    companion object {
        private const val TAG = "CalculeCouleurHandler"
    }

    init {
        Log.d(TAG, "CalculeCouleurHandler initialized")
        updateProductImageInfoList()
        scope.launch {
            Log.d(TAG, "Starting to collect ViewModel uiState changes...")
            viewModel.uiState.collect { uiState ->
                Log.d(TAG, "=== ViewModel uiState changed ===")
                Log.d(TAG, "Products count: ${uiState.produitInfosList.size}")

                uiState.produitInfosList.forEach { product ->
                    Log.d(TAG, "Product ${product.id} (${product.nom}): actualiseSonImage=${product.actualiseSonImage}, needUpdate=${product.needUpdate}")
                }

                updateProductImageInfoList()
                Log.d(TAG, "=== ViewModel uiState processing completed ===")
            }
        }
    }

    fun updateProductImageInfoList() {
        Log.d(TAG, "=== updateProductImageInfoList called ===")

        val productsList = viewModel.uiState.value.produitInfosList
        Log.d(TAG, "Processing ${productsList.size} products")

        val allImageInfos = productsList.flatMap { product ->
            Log.d(TAG, "Processing product ${product.id} (${product.nom}) - actualiseSonImage: ${product.actualiseSonImage}")
            val colors = getAllDefinedColorsForProduct(product)
            Log.d(TAG, "Found ${colors.size} color variants for product ${product.id}")
            colors
        }

        Log.d(TAG, "Total image infos created: ${allImageInfos.size}")

        _productImageInfoFlowList.value = allImageInfos

        Log.d(TAG, "ProductImageInfoFlowList updated and emitted")
        Log.d(TAG, "=== updateProductImageInfoList completed ===")
    }

    fun findProductById(productId: Long): A_ProduitInfosTest? {
        Log.d(TAG, "Finding product by ID: $productId")
        val product = viewModel.uiState.value.produitInfosList.find { it.id == productId }
        if (product != null) {
            Log.d(TAG, "Found product: ${product.nom} (actualiseSonImage: ${product.actualiseSonImage})")
        } else {
            Log.w(TAG, "Product with ID $productId not found")
        }
        return product
    }

    fun getImageFilesForDisplay(
        produitVID: Long? = null,
        product: A_ProduitInfosTest? = null,
        produitNom: String? = null
    ): List<ProductImageInfo> {
        Log.d(TAG, "=== getImageFilesForDisplay called ===")
        Log.d(TAG, "Parameters: produitVID=$produitVID, product=${product?.id}, produitNom=$produitNom")

        return try {
            val defaultFile = File("$imagesProduitsLocalExternalStorageBasePath/logo.webp")
            val shouldUseDefaultImage = produitVID == null && product == null

            Log.d(TAG, "shouldUseDefaultImage: $shouldUseDefaultImage")

            if (!shouldUseDefaultImage) {
                val targetProduct = product ?: produitVID?.let { findProductById(it) }
                Log.d(TAG, "Target product: ${targetProduct?.id} (actualiseSonImage: ${targetProduct?.actualiseSonImage})")

                if (targetProduct != null) {
                    val allDefinedColors = getAllDefinedColorsForProduct(targetProduct)
                    Log.d(TAG, "Found ${allDefinedColors.size} defined colors for product ${targetProduct.id}")

                    if (allDefinedColors.isNotEmpty()) {
                        Log.d(TAG, "Returning ${allDefinedColors.size} color variants")
                        allDefinedColors.forEach { imageInfo ->
                            Log.d(TAG, "  - Color ${imageInfo.couleurId}: exists=${imageInfo.exists}, actualiseSonImage=${imageInfo.actualiseSonImage}")
                        }
                        allDefinedColors
                    } else {
                        Log.d(TAG, "No defined colors found, returning default image")
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
                    Log.w(TAG, "Target product is null, returning default image")
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
                Log.d(TAG, "Using default image (no product specified)")
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
            Log.e(TAG, "Exception in getImageFilesForDisplay", e)
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
        }.also { result ->
            Log.d(TAG, "=== getImageFilesForDisplay returning ${result.size} items ===")
        }
    }

    private fun getAllDefinedColorsForProduct(product: A_ProduitInfosTest): List<ProductImageInfo> {
        Log.d(TAG, "Getting all defined colors for product ${product.id} (${product.nom})")
        Log.d(TAG, "Product actualiseSonImage: ${product.actualiseSonImage}")

        val colors = (1..4).mapNotNull { couleurId ->
            val colorName = getColorNameById(product, couleurId).takeIf { it.isNotBlank() }
            Log.d(TAG, "  Color $couleurId: name='$colorName'")

            val keyImageId = "${product.id}_${couleurId}"
            val basePath = "$imagesProduitsLocalExternalStorageBasePath/$keyImageId"
            Log.d(TAG, "  Looking for image at: $basePath")

            val imageFile = listOf("jpg", "jpeg", "png", "webp")
                .map { ext -> File("$basePath.$ext") }
                .find { file ->
                    val exists = file.exists() && file.length() > 0
                    Log.d(TAG, "    Checking ${file.name}: exists=$exists, size=${if (file.exists()) file.length() else 0}")
                    exists
                }

            if (colorName != null || imageFile != null) {
                val imageInfo = ProductImageInfo(
                    file = imageFile ?: File(""),
                    couleurId = couleurId,
                    exists = imageFile != null,
                    colorName = colorName ?: "",
                    shouldShowColorText = imageFile == null && colorName != null,
                    productName = product.nom,
                    actualiseSonImage = product.actualiseSonImage
                )
                Log.d(TAG, "  Created ProductImageInfo for color $couleurId: exists=${imageInfo.exists}, shouldShowColorText=${imageInfo.shouldShowColorText}, actualiseSonImage=${imageInfo.actualiseSonImage}")
                imageInfo
            } else {
                Log.d(TAG, "  No color name or image file for color $couleurId")
                null
            }
        }

        Log.d(TAG, "Returning ${colors.size} color variants for product ${product.id}")
        return colors
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
        Log.v(TAG, "getColorNameById(${product?.id}, $colorId) = '$trimmedName'")
        return trimmedName
    }
}
