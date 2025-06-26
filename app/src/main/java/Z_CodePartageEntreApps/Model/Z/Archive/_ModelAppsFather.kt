package Z_CodePartageEntreApps.Model.Z.Archive

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model.C_GrossistsDataBase
import Z_CodePartageEntreApps.Model.D_CouleursEtGoutesProduitsInfos
import Z_CodePartageEntreApps.Model.E_AppsOptionsStates
import Z_CodePartageEntreApps.Model.H_GroupeCategories
import Z_CodePartageEntreApps.Model.I_CategoriesProduits
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.Exclude
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

open class _ModelAppsFather(
    initial_Produits_Main_DataBase: List<A_ProduitModel> = emptyList()
) {
    @get:Exclude
    var applicationEstInstalleDonTelephone: SnapshotStateList<E_AppsOptionsStates.ApplicationEstInstalleDonTelephone> =
        emptyList<E_AppsOptionsStates.ApplicationEstInstalleDonTelephone>().toMutableStateList()
    @get:Exclude
    var f_PrototypseDeProgramationInfos: SnapshotStateList<E_AppsOptionsStates.F_PrototypseDeProgramationInfos> =
        emptyList<E_AppsOptionsStates.F_PrototypseDeProgramationInfos>().toMutableStateList()

    @get:Exclude
    var h_GroupesCategories: SnapshotStateList<H_GroupeCategories> =
        emptyList<H_GroupeCategories>().toMutableStateList()

    @get:Exclude
    var i_CategoriesProduits: SnapshotStateList<I_CategoriesProduits> =
        emptyList<I_CategoriesProduits>().toMutableStateList()

    @get:Exclude
    var produitsMainDataBase: SnapshotStateList<A_ProduitModel> =
        initial_Produits_Main_DataBase.toMutableStateList()

    @get:Exclude
    var clientDataBase: SnapshotStateList<B_ClientInfosProtoJuin3> =
        emptyList<B_ClientInfosProtoJuin3>().toMutableStateList()

    @get:Exclude
    var grossistsDataBase: SnapshotStateList<C_GrossistsDataBase> =
        emptyList<C_GrossistsDataBase>().toMutableStateList()
    @get:Exclude
    var couleursProduitsInfos: SnapshotStateList<D_CouleursEtGoutesProduitsInfos> =
        emptyList<D_CouleursEtGoutesProduitsInfos>().toMutableStateList()

    val groupedProductsParGrossist: List<Map.Entry<C_GrossistsDataBase, List<A_ProduitModel>>>
        get() = grossistsDataBase.map { grossist ->
            val matchingProducts = produitsMainDataBase.filter { product ->
                product.bonCommendDeCetteCota?.idGrossistChoisi == grossist.id &&
                        (product.bonsVentDeCetteCota.any { bonVent ->
                            bonVent.colours_Achete.any { color ->
                                color.quantity_Achete > 0
                            }
                        } ||

                                (product.bonCommendDeCetteCota?.coloursEtGoutsCommendee?.any { bon ->
                                    bon.quantityAchete > 0
                                } ?: false))
            }

            java.util.AbstractMap.SimpleEntry(grossist, matchingProducts)
        }.sortedBy { entry ->
            entry.key.statueDeBase.itIndexInParentList
        }


    companion object {
        val firebaseDatabase = Firebase.database

        val ref_HeadOfModels = firebaseDatabase
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")

        val produitsFireBaseRef = firebaseDatabase
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("produits")
        val imagesProduitsFireBaseStorageRef = Firebase.storage.reference
            .child("Images Articles Data Base")
            .child("produits")
        const val imagesProduitsLocalExternalStorageBasePath =
            "/storage/emulated/0/" +
                    "Abdelwahab_jeMla.com" +
                    "/IMGs" +
                    "/BaseDonne"

        fun update_AllProduits(
            updatedProducts: List<A_ProduitModel>,
            viewModelProduits: ViewModelInitApp
        ) {
            viewModelProduits.viewModelScope.launch {
                try {
                    // Update local state
                    viewModelProduits._modelAppsFather.produitsMainDataBase.clear()
                    viewModelProduits._modelAppsFather.produitsMainDataBase.addAll(updatedProducts)

                    // Then upsertLenceCommandeRepoGroupedProtoAvantJuin3 Firebase in chunks to prevent overwhelming the connection
                    UpdateFireBase(updatedProducts)
                } catch (e: Exception) {
                    Log.e("Firebase", "Error updating products", e)
                    throw e
                }
            }
        }

        suspend fun UpdateFireBase(updatedProducts: List<A_ProduitModel>) {
            updatedProducts.forEach { product ->
                try {
                    produitsFireBaseRef.child(product.id.toString()).setValue(product)
                        .await()
                    Log.d("Firebase", "Successfully updated product ${product.id}")
                } catch (e: Exception) {
                    Log.e("Firebase", "Failed to upsertLenceCommandeRepoGroupedProtoAvantJuin3 product ${product.id}", e)
                }
            }
        }

        fun updateProduit(product: A_ProduitModel, viewModelProduits: ViewModelInitApp) {
            viewModelProduits.viewModelScope.launch {
                try {
                    // Créer une nouvelle liste temporaire
                    val updatedList =
                        viewModelProduits._modelAppsFather.produitsMainDataBase.toMutableList()
                    val index = updatedList.indexOfFirst { it.id == product.id }
                    if (index != -1) {
                        updatedList[index] = product
                        // Remplacer toute la liste
                        viewModelProduits._modelAppsFather.produitsMainDataBase.clear()
                        viewModelProduits._modelAppsFather.produitsMainDataBase.addAll(updatedList)
                    }

                    produitsFireBaseRef.child(product.id.toString()).setValue(product).await()
                } catch (e: Exception) {
                    Log.e("ViewModelInitApp", "Failed to upsertLenceCommandeRepoGroupedProtoAvantJuin3 product ${product.id}", e)
                }
            }
        }
    }

}
