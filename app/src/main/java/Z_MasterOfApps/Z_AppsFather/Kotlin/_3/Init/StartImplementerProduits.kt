package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.UpdateFireBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.Z.Parent.GetAncienDataBasesMain
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

 fun CreeDepuitAncienDataBases(
    _appsHeadModel: _ModelAppsFather,
    viewModelInitApp: ViewModelInitApp,

    ) {
    viewModelInitApp.viewModelScope.launch {
    try {
        val ancienData = GetAncienDataBasesMain()

        // Process products and filter out IDs above 2000
        ancienData.produitsDatabase.forEach { ancien ->
            // Skip products with ID > 2000
            if (ancien.idArticle <= 2000) {
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
        }

        _ModelAppsFather.produitsFireBaseRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("CreeDepuitAncienDataBases", "Successfully cleared Firebase database")
            } else {
                Log.e("CreeDepuitAncienDataBases", "Failed to clear Firebase database", task.exception)
            }
        }

        UpdateFireBase(_appsHeadModel.produitsMainDataBase)

    } catch (e: Exception) {
        Log.e("CreeDepuitAncienDataBases", "Error in CreeDepuitAncienDataBases", e)
        throw e
    }
    }
}
