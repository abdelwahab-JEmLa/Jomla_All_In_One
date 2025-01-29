// ViewModelExtension_App1_F5.kt
package Z_MasterOfApps.Z.Android.Base.App.Main.C_EcranDeDepart.Startup.ViewModel

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database

class Startup_Extension(
    val viewModelInitApp: ViewModelInitApp,
) {
    fun clearAchats() {
        viewModelInitApp._modelAppsFather.produitsMainDataBase.forEach { produit ->
            // Safely add current data to history
            produit.bonCommendDeCetteCota?.let { currentBonCommend ->
                produit.historiqueBonsCommend.add(currentBonCommend)
            }
            if (produit.bonsVentDeCetteCotaList.isNotEmpty()) {
                produit.historiqueBonsVents.addAll(produit.bonsVentDeCetteCotaList)
            }

            // Clear current data
            produit.bonCommendDeCetteCota = null
            produit.bonsVentDeCetteCota.clear()

            // Update the product in the database
            try {
                val database = Firebase.database
                database
                    .getReference("K_GroupeurBonCommendToSupplierRef").removeValue()
                database
                    .getReference("O_SoldArticlesTabelle").removeValue()
                updateProduit(produit, viewModelInitApp)
            } catch (e: Exception) {
                Log.e("ClearHistory", "Failed to update product ${produit.id}", e)
            }
        }
    }

}
