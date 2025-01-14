package Y_AppsFather.Z_AppsFather.Kotlin._3.Init

import Y_AppsFather.Kotlin.Model._ModelAppsFather
import Y_AppsFather.Kotlin.Model._ModelAppsFather.Companion.UpdateFireBase
import Y_AppsFather.Z_AppsFather.Kotlin._3.Init.Z.Parent.GetAncienDataBasesMain
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale


// In StartImplementerProduits.kt

suspend fun CreeNewStart(
    _appsHeadModel: _ModelAppsFather,
    NOMBRE_ENTRE: Int,
) {
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        Log.d("CreeNewStart", "Starting data initialization with NOMBRE_ENTRE: $NOMBRE_ENTRE")

        val ancienData = GetAncienDataBasesMain()
        Log.d("CreeNewStart", "Retrieved ancien data: ${ancienData.produitsDatabase.size} products")

        // Process products
        ancienData.produitsDatabase.forEachIndexed { index, ancien ->
            Log.d("CreeNewStart", "Processing product ${ancien.idArticle} ($index/${ancienData.produitsDatabase.size})")

            val depuitAncienDataBase = _ModelAppsFather.ProduitModel(
                id = ancien.idArticle,
                itsTempProduit = ancien.idArticle > 2000,
                init_nom = ancien.nomArticleFinale,
                init_visible = false,
                init_besoin_To_Be_Updated = true
            )

            // Add colors/tastes with logging
            var colorsAdded = 0
            listOf(
                ancien.idcolor1 to 1L,
                ancien.idcolor2 to 2L,
                ancien.idcolor3 to 3L,
                ancien.idcolor4 to 4L
            ).forEach { (colorId, position) ->
                ancienData.couleurs_List.find { it.idColore == colorId }?.let { couleur ->
                    depuitAncienDataBase.coloursEtGouts.add(
                        _ModelAppsFather.ProduitModel.ColourEtGout_Model(
                            position_Du_Couleur_Au_Produit = position,
                            nom = couleur.nameColore,
                            imogi = couleur.iconColore,
                            sonImageNeExistPas = depuitAncienDataBase.itsTempProduit && position == 1L,
                        )
                    )
                    colorsAdded++
                }
            }
            Log.d("CreeNewStart", "Added $colorsAdded colors to product ${ancien.idArticle}")

            // Add product to main database
            _appsHeadModel.produitsMainDataBase.add(depuitAncienDataBase)
            Log.d("CreeNewStart", "Added product ${ancien.idArticle} to main database")
        }

        // Clear and update Firebase
        Log.d("CreeNewStart", "Starting Firebase update with ${_appsHeadModel.produitsMainDataBase.size} products")
        _ModelAppsFather.produitsFireBaseRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("CreeNewStart", "Successfully cleared Firebase database")
            } else {
                Log.e("CreeNewStart", "Failed to clear Firebase database", task.exception)
            }
        }

        UpdateFireBase(_appsHeadModel.produitsMainDataBase)

    } catch (e: Exception) {
        Log.e("CreeNewStart", "Error in CreeNewStart", e)
        throw e
    }
}
