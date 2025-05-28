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
    private val TAG = "CalculeCouleurHandler"
    private val _productImageInfoFlowList = MutableStateFlow<List<ProductImageInfo>>(emptyList())
    val productImageInfoFlowList: StateFlow<List<ProductImageInfo>> = _productImageInfoFlowList.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        updateProductImageInfoList()
        scope.launch {
            viewModel.uiState.collect { uiState ->
                Log.d(TAG, "ViewModel state changed - updating image info list")
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
        Log.d(TAG, "Updated product image info list with ${allImageInfos.size} items")
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
                    Log.d(TAG, "Checking file ${file.absolutePath}: exists=$exists, size=${if (exists) file.length() else 0}")
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

        Log.d(TAG, "Found ${colors.size} color variants for product ${product.nom} (ID: ${product.id})")
        return colors
    }

    fun findProductById(productId: Long): A_ProduitInfosTest? {
        val product = viewModel.uiState.value.produitInfosList.find { it.id == productId }
        Log.d(TAG, "Finding product by ID $productId: ${if (product != null) "found" else "not found"}")
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
                    Log.d(TAG, "Getting image files for product: ${targetProduct.nom} (ID: ${targetProduct.id}, refresh: ${targetProduct.actualiseSonImage})")

                    // Force refresh of product colors to get latest state
                    val allDefinedColors = getAllDefinedColorsForProduct(targetProduct)

                    if (allDefinedColors.isNotEmpty()) {
                        Log.d(TAG, "Found ${allDefinedColors.size} image files for product ${targetProduct.nom}")
                        allDefinedColors
                    } else {
                        Log.d(TAG, "No image files found for product ${targetProduct.nom}, using default")
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
                    Log.w(TAG, "Target product not found, using default image")
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
            Log.e(TAG, "Error getting image files for display", e)
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
        Log.d(TAG, "Color name for ID $colorId: '$trimmedName'")
        return trimmedName
    }
}
