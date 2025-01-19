package Z_MasterOfApps.Kotlin.ViewModel.Actions._2_C_Serveur.Package_1

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
