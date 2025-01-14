package Y_AppsFather.Kotlin.Model.Res

import Y_AppsFather.Kotlin.BonType
import Y_AppsFather.Kotlin.Model._ModelAppsFather
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes
import Y_AppsFather.Kotlin.ViewModelInitApp
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

open class ExtensionProduitModel {
    val produitsFireBaseRef = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
        .child("produits")
    val imagesProduitsFireBaseStorageRef = Firebase.storage.reference
        .child("Images Articles Data Base")
        .child("produits")
     val imagesProduitsLocalExternalStorageBasePath =
        "/storage/emulated/0/" +
                "Abdelwahab_jeMla.com" +
                "/IMGs" +
                "/BaseDonne"

    fun createNewProduct(
        viewModelInitApp: ViewModelInitApp,
        nameArticle: String? = null
    ): _ModelAppsFather.ProduitModel {
        val maxId = viewModelInitApp._modelAppsFather.produitsMainDataBase
            .maxOfOrNull { it.id } ?: 0

        return _ModelAppsFather.ProduitModel(
            id = maxId + 1,
            itsTempProduit = true,
        ).apply {
            nom = nameArticle ?: "New Product ${maxId + 1}"
            coloursEtGouts.add(
                _ModelAppsFather.ProduitModel.ColourEtGout_Model(
                    sonImageNeExistPas = true
                )
            )
        }.also {
            viewModelInitApp._modelAppsFather.produitsMainDataBase.add(it)
        }
    }

    fun update_produitsAvecBonsGrossist(
        updatedProducts: List<_ModelAppsFather.ProduitModel>, // Change parameter type to List
        viewModelProduits: ViewModelInitApp
    ) {
        viewModelProduits.viewModelScope.launch {
            try {
                // Update local state
                viewModelProduits._produitsAvecBonsGrossist.clear()
                viewModelProduits._produitsAvecBonsGrossist.addAll(updatedProducts)

                // Then update Firebase in chunks to prevent overwhelming the connection
                UpdateFireBase(updatedProducts)
            } catch (e: Exception) {
                Log.e("Firebase", "Error updating products", e)
                throw e
            }
        }
    }

    suspend fun UpdateFireBase(updatedProducts: List<_ModelAppsFather.ProduitModel>) {
        updatedProducts.chunked(5).forEach { chunk ->
            chunk.forEach { product ->
                try {
                    produitsFireBaseRef.child(product.id.toString()).setValue(product)
                        .await()
                    Log.d("Firebase", "Successfully updated product ${product.id}")
                } catch (e: Exception) {
                    Log.e("Firebase", "Failed to update product ${product.id}", e)
                }
            }
        }
    }

    fun collectBonType(
        viewModel: ViewModelInitApp,
        scope: CoroutineScope
    ) {
        scope.launch {
            viewModel.bonTypeFlow.collect { bonType ->
                when (bonType) {
                    is BonType.BonVente -> {
                        // Handle bon vente updates
                        val bonVente = bonType.data
                        viewModel._modelAppsFather.produitsMainDataBase
                            .filter { it.bonsVentDeCetteCota.any { bv -> bv.clientInformations?.id == bonVente.clientInformations?.id } }
                            .forEach { produit ->
                                ClientBonVentModel.updateSelf(produit, bonVente, viewModel)
                                GrossistBonCommandes.calculeSelf(viewModel, produit)
                            }
                    }

                    is BonType.BonCommande -> {
                        // Handle bon commande updates
                        val bonCommande = bonType.data
                        viewModel._modelAppsFather.produitsMainDataBase
                            .filter { it.bonCommendDeCetteCota?.grossistInformations?.id == bonCommande.grossistInformations?.id }
                            .forEach { produit ->
                                GrossistBonCommandes.updateSelf(produit, bonCommande, viewModel)
                            }
                    }

                    null -> {}
                }
            }
        }
    }

    fun updateProduit(
        product: _ModelAppsFather.ProduitModel,
        viewModelProduits: ViewModelInitApp
    ) {
        viewModelProduits.viewModelScope.launch {
            try {
                // Update Firebase
                produitsFireBaseRef.child(product.id.toString()).setValue(product).await()

                // Update _produitsAvecBonsGrossist
                val index =
                    viewModelProduits._modelAppsFather.produitsMainDataBase.indexOfFirst { it.id == product.id }
                if (index != -1) {
                    // Direct update of the SnapshotStateList
                    viewModelProduits._modelAppsFather.produitsMainDataBase[index] = product
                }

                Log.d("ViewModelInitApp", "Successfully updated product ${product.id}")
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Failed to update product ${product.id}", e)
            }
        }
    }
}
