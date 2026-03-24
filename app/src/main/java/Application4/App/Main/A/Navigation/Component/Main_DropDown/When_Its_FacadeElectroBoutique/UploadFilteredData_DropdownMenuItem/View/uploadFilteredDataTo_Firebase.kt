package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.UploadFilteredData_DropdownMenuItem.View

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await


suspend fun uploadFilteredDataTo_Firebase(
    filteredCatalogues: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
) {
    val filteredProducts = mutableListOf<M01Produit>()
    val filteredColors = mutableListOf<M3CouleurProduitInfos>()
    val filteredCategories = mutableListOf<M16CategorieProduit>()

    filteredCatalogues.forEach { (_, categories) ->
        categories.forEach { (category, productColorPairs) ->
            if (filteredCategories.none { it.id == category.id }) {
                filteredCategories += category
            }
            productColorPairs.forEach { (product, colors) ->
                filteredProducts += product
                filteredColors += colors
            }
        }
    }

    suspend fun <T> syncCollection(
        ref: DatabaseReference,
        items: List<T>,
        keyOf: (T) -> String,
        mapOf: (T) -> Map<String, Any?>,
    ) {
        ref.removeValue().await()

        items.forEach { item ->
            ref.child(keyOf(item)).setValue(mapOf(item)).await()
        }
    }

    syncCollection(
        ref = M01Produit.ref_Active_Filtred_Datas,
        items = filteredProducts,
        keyOf = { product -> product.keyFireBase.ifEmpty { product.keyID } },
        mapOf = { product -> product.toFirebaseMap() }
    )

    syncCollection(
        ref = M3CouleurProduitInfos.ref_Active_Filtred_Datas,
        items = filteredColors,
        keyOf = { color -> color.keyID },
        mapOf = { color -> color.toFirebaseMap() }
    )

    syncCollection(
        ref = M16CategorieProduit.ref_Active_Filtred_Datas,
        items = filteredCategories,
        keyOf = { category -> category.keyID },
        mapOf = { category -> category.toFirebaseMap() }
    )
}
