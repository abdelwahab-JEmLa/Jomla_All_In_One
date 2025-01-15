package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.UpdateFireBase
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.Z.Parent.GetAncienDataBasesMain
import android.util.Log

suspend fun CreeNewStart(
    _appsHeadModel: _ModelAppsFather,
) {
    try {
        val ancienData = GetAncienDataBasesMain()

        // Process products
        ancienData.produitsDatabase.forEachIndexed { index, ancien ->

                val depuitAncienDataBase = _ModelAppsFather.ProduitModel(
                    id = ancien.idArticle,
                    init_nom = ancien.nomArticleFinale,
                    init_visible = false,
                    init_besoin_To_Be_Updated = true
                )

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

                // Add product to main database
                _appsHeadModel.produitsMainDataBase.add(depuitAncienDataBase)
        }


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
