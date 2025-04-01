package Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.Extension

import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils_A_Produit {
    private var initialized = false
    private val initLock = Any()

    fun initializeFirebaseOfflineCapability() {
        synchronized(initLock) {
            if (!initialized) {
                try {
                    // Set persistence enabled for the entire database
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true)

                    // Keep synchronized for the specific reference we're using
                    A_ProduitRepository.caReference.keepSynced(true)

                    initialized = true
                } catch (e: Exception) {
                    // Log error but continue
                }
            }
        }
    }
}
