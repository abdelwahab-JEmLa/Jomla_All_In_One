package Z_MasterOfApps.Z.Android.ViewModel.Extensions._1.C_Serveur._1NavHost.Fragment_Id4

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp

fun clearAchates(viewModel: ViewModelInitApp) {
    // Clear all products' bonCommendDeCetteCota and bonsVentDeCetteCota
    viewModel._modelAppsFather.produitsMainDataBase.forEach { produit ->
        // Clear data locally
        produit.bonCommendDeCetteCota = null
        produit.bonsVentDeCetteCota.clear()

        // Update Firebase
        val productRef = produitsFireBaseRef.child(produit.id.toString())

        // Remove bonCommendDeCetteCota
        productRef.child("bonCommendDeCetteCota").removeValue()

        // Clear bonsVentDeCetteCota
        productRef.child("bonsVentDeCetteCotaList").removeValue()
    }
}
