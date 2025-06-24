package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import java.io.File

class Sec10Frag1ViewModel(
    val aCentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {

    companion object {
        private const val TAG = "Sec10Frag1ViewModel"
        private const val MAX_STRING_LENGTH = 100
    }

    fun addNewDAchatCouleurOperationEtOuvreLe(
        article: ArticlesBasesStatsTable,
        indexCouleur: Int,
    ) {
      /*  val data = FCouleurVentOperation(
            parentProduitBsonObjectId = article.bsonObjectId,
            nomImageFichieOuApellationDuCouleur = trouve_nomImageFichieOuApellationDuCouleurPar(
                article, indexCouleur
            ),
            parentBonVentObjectId = aCentralDatasHandlerProtoJuin9.ouvertTransactionCommercial!!.bsonObjectId
        )
        aCentralDatasHandlerProtoJuin9.dCouleurAchatOperationRepositoryComposable.addOrUpdateData(
            data
        )
        subClassFunctionality_zAppComptRepositoryComposable

            .ouvrireCouleurAchatOperationPourCeCompt(
            data.bsonObjectId,
            "${article.nom}_${data.nomImageFichieOuApellationDuCouleur}"
        )                   */
    }

    data class UiState(
        val catalogueFilterId: BsonObjectId? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun trouve_nomImageFichieOuApellationDuCouleurPar(
        article: ArticlesBasesStatsTable,
        indexCouleur: Int
    ): String {
        // Get the color name based on the index
        val couleurName = when (indexCouleur) {
            0 -> article.couleur1
            1 -> article.couleur2
            2 -> article.couleur3
            3 -> article.couleur4
            else -> null
        }

        // Return empty string if color is null or blank
        if (couleurName.isNullOrBlank()) {
            return ""
        }

        // Base path for images (same as in CreateCouleurInfosFromProduct.kt)
        val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

        // Create the image file name pattern: {articleId}_{imageIndex}
        val imageIndex = indexCouleur + 1
        val baseFileName = "${article.id}_$imageIndex"

        // Check for image file existence with different extensions
        val supportedExtensions = listOf("jpg", "webp", "jpeg", "png")
        val imageFile = supportedExtensions
            .map { extension -> File("$basePath/$baseFileName.$extension") }
            .firstOrNull { file ->
                file.exists() && file.canRead() && file.length() > 0
            }

        // Return image file name (without extension) if image exists, otherwise return color name
        return if (imageFile != null && imageFile.name != "NonTrouve.webp") {
            baseFileName // Return the base file name without extension
        } else {
            couleurName // Return the color name if no image is available
        }
    }

    fun ouvrireProduitEtCouleurVent(
        produit: ArticlesBasesStatsTable,
        baseFileName: String,
        colorIndex: Int,
        quantity: Int,
    ) {
        // Validate inputs immediately to prevent crashes
        if (!isValidInput(produit, baseFileName, colorIndex, quantity)) {
            return
        }

        // Use viewModelScope instead of GlobalScope for better lifecycle management
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting ouvrireProduitEtCouleurVent for product ${produit.id}")

                // Create safe copies of string parameters to prevent memory corruption
                val safeBaseFileName = baseFileName.take(MAX_STRING_LENGTH)
                val safeProduit = createSafeProduitCopy(produit)

                // Perform operations with additional safety measures
                withContext(Dispatchers.IO) {
                    try {
                        // First operation: Open product with safe error handling
                        val data = aCentralDatasHandlerProtoJuin9
                            .zAppComptRepositoryComposable
                            .ouvrireProduitEtCouleurVent(
                                safeProduit,
                                safeBaseFileName
                            )

                        // Add small delay to prevent race conditions
                        kotlinx.coroutines.delay(50)

                        // Second operation: Purchase color with additional validation
                        if (data != null) {
                            aCentralDatasHandlerProtoJuin9
                                .dCouleurAchatOperationRepositoryComposable
                                .acheterUneCouleur(
                                    ouvertData = data,
                                    produit = safeProduit,
                                    quantity = quantity,
                                    colorIndex = colorIndex
                                )
                        } else {
                            Log.w(TAG, "Data is null from ouvrireProduitEtCouleurVent")
                        }

                        Log.d(TAG, "Successfully completed ouvrireProduitEtCouleurVent for product ${produit.id}")

                    } catch (e: OutOfMemoryError) {
                        Log.e(TAG, "OutOfMemoryError in ouvrireProduitEtCouleurVent", e)
                        // Force garbage collection
                        System.gc()
                        // Try to recover gracefully
                        handleOutOfMemoryError(produit.id)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in IO operations for product ${produit.id}", e)
                        handleOperationError(produit.id, e)
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error in ouvrireProduitEtCouleurVent for product ${produit.id}", e)
                // Don't rethrow to prevent app crash
            }
        }
    }

    // Helper function to validate inputs
    private fun isValidInput(
        produit: ArticlesBasesStatsTable,
        baseFileName: String,
        colorIndex: Int,
        quantity: Int
    ): Boolean {
        return try {
            when {
                produit.id <= 0 -> {
                    Log.w(TAG, "Invalid product ID: ${produit.id}")
                    false
                }
                baseFileName.isBlank() -> {
                    Log.w(TAG, "BaseFileName is blank")
                    false
                }
                baseFileName.length > MAX_STRING_LENGTH -> {
                    Log.w(TAG, "BaseFileName too long: ${baseFileName.length}")
                    false
                }
                colorIndex < 0 || colorIndex > 3 -> {
                    Log.w(TAG, "Invalid color index: $colorIndex")
                    false
                }
                quantity <= 0 -> {
                    Log.w(TAG, "Invalid quantity: $quantity")
                    false
                }
                else -> true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error validating input", e)
            false
        }
    }

    // Helper function to create a safe copy of product data
    private fun createSafeProduitCopy(produit: ArticlesBasesStatsTable): ArticlesBasesStatsTable {
        return try {
            produit.copy(
                nom = produit.nom?.take(MAX_STRING_LENGTH) ?: "",
                couleur1 = produit.couleur1?.take(50) ?: "",
                couleur2 = produit.couleur2?.take(50) ?: "",
                couleur3 = produit.couleur3?.take(50) ?: "",
                couleur4 = produit.couleur4?.take(50) ?: ""
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error creating safe product copy", e)
            produit // Return original if copy fails
        }
    }

    // Error handling functions
    private fun handleOutOfMemoryError(productId: Long) {
        Log.w(TAG, "Handling OutOfMemoryError for product $productId")
        // Implement recovery logic here
        // Could notify UI about the error or retry with reduced data
    }

    private fun handleOperationError(productId: Long, error: Exception) {
        Log.w(TAG, "Handling operation error for product $productId: ${error.message}")
        // Implement error recovery logic here
        // Could retry the operation or show user-friendly error message
    }

    /*fun acheterUneCouleur(produit: ArticlesBasesStatsTable, colorIndex: Int, quantity: Int) {
        aCentralDatasHandlerProtoJuin9
            .dCouleurAchatOperationRepositoryComposable
            .acheterUneCouleur(
                aCentralDatasHandlerProtoJuin9.zAppComptRepositoryComposable,
                produit,
                quantity,
                colorIndex
            )
    }     */
}
