package Y_AppsFather.Z_AppsFather.Kotlin._3.Init

import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes.Companion.initCalculeSelf
import Y_AppsFather.Kotlin.ViewModelInitApp

suspend fun calculateurOktapuluse(viewModelInitApp: ViewModelInitApp) {
    try {
        viewModelInitApp.isLoading = true
        viewModelInitApp.loadingProgress = 0f

        // Start Firebase load - 40% of progress
        viewModelInitApp.loadingProgress = 0.1f
        LoadFromFirebaseHandler.loadFromFirebase(viewModelInitApp)
        viewModelInitApp.loadingProgress = 0.4f

        // Process products - 30% of progress
        val products = viewModelInitApp.produitsMainDataBase
        val totalProducts = products.size
        products.forEachIndexed { index, produit ->
            // Update progress for each product processed
            viewModelInitApp.loadingProgress = 0.4f + (0.3f * (index + 1) / totalProducts)
        }

        // Update grossist and final steps - 30% of progress
        viewModelInitApp.loadingProgress = 0.7f
        viewModelInitApp.apply {
            initCalculeSelf(viewModelInitApp)
            updateProduitsAvecBonsGrossist()
            this.loadingProgress = 0.9f
            this.loadingProgress = 1f
        }

        // Complete
        viewModelInitApp.loadingProgress = 1.0f

    } catch (e: Exception) {
        viewModelInitApp.loadingProgress = 0f
        throw e
    } finally {
        viewModelInitApp.isLoading = false
    }
}
