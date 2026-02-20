package Z_CodePartageEntreApps.Modules.D.Glide.Proto

import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File

class CalculeCouleurHandler(private val viewModel: EditeBaseDonneMainScreenIdS9ViewModel) {
    private val TAG = "CalculeCouleurHandler"
    private val imagesProduitsLocalExternalStorageBasePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun getCouleurNomParIndex(
        article: M01Produit,
        indexColor: Int
    ) = when (indexColor) {
        0 -> article.couleur1.takeIf { it != "" }
        1 -> article.couleur2.takeIf { it != "" }
        2 -> article.couleur3.takeIf { it != "" }
        3 -> article.couleur4.takeIf { it != "" }
        else -> null
    }

    fun getProduitInfoImageParIndex(product: M01Produit): List<ProductImageInfo> {
        val colors = (0..3).mapNotNull { index ->
            val colorName = getCouleurNomParIndex(product, index)?.takeIf { it.isNotBlank() }

            val keyImageId = "${product.id}_${index + 1}"
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
                    couleurId = index + 1, // couleurId should be 1-based
                    exists = imageFile != null,
                    colorName = if (imageFile != null) "" else (colorName ?: ""),
                    shouldShowColorText = imageFile == null && colorName != null,
                    productName = product.nom,
                    actualiseSonImage = product.actualiseSonImage
                )
            } else null
        }

        Log.d(TAG, "Found ${colors.size} color variants for product ${product.nom} (ID: ${product.id})")
        return colors
    }

    private fun getAllDefinedColorsForProduct(product: M01Produit): List<ProductImageInfo> {
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
                    colorName = if (imageFile != null) "" else (colorName ?: ""),
                    shouldShowColorText = imageFile == null && colorName != null,
                    productName = product.nom,
                    actualiseSonImage = product.actualiseSonImage
                )
            } else null
        }

        Log.d(TAG, "Found ${colors.size} color variants for product ${product.nom} (ID: ${product.id})")
        return colors
    }

    fun findProductById(productId: Long): M01Produit? {
        val product = viewModel.uiState.value.a_ProduitInfosList.find { it.id == productId }
        Log.d(TAG, "Finding product by ID $productId: ${if (product != null) "found" else "not found"}")
        return product
    }

    fun getImageFilesForDisplay(
        produitVID: Long? = null,
        product: M01Produit? = null,
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

    fun getColorNameById(product: M01Produit?, colorId: Int): String {
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
        val couleurId: Int = 0,
        val exists: Boolean = true,
        val colorName: String = "",
        val imageCouleurNomDeSonFichie: String = "",
        val shouldShowColorText: Boolean = false,
        val productName: String = "",
        val actualiseSonImage: Int = 0
    )
}
