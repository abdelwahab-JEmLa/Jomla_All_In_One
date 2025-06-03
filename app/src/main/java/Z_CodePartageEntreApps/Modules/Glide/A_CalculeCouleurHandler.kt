package Z_CodePartageEntreApps.Modules.Glide

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.StartUpFragmentViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.A_ProduitInfosProtoJuin3
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File

class CalculeCouleurHandler(private val viewModel: StartUpFragmentViewModel) {
    private val TAG = "CalculeCouleurHandler"
    private val imagesProduitsLocalExternalStorageBasePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun getAllDefinedColorsForProduct(product: A_ProduitInfosProtoJuin3): List<ProductImageInfo> {
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

    fun findProductById(productId: Long): A_ProduitInfosProtoJuin3? {
        val product = viewModel.uiState.value.a_ProduitInfosList.find { it.id == productId }
        Log.d(TAG, "Finding product by ID $productId: ${if (product != null) "found" else "not found"}")
        return product
    }

    fun getImageFilesForDisplay(
        produitVID: Long? = null,
        product: A_ProduitInfosProtoJuin3? = null,
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

    fun getColorNameById(product: A_ProduitInfosProtoJuin3?, colorId: Int): String {
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
    data class ProductImageInfo(
        val file: File,
        val couleurId: Int,
        val exists: Boolean = true,
        val colorName: String = "",
        val shouldShowColorText: Boolean = false,
        val productName: String = "",
        val actualiseSonImage: Int = 0
    )

}
