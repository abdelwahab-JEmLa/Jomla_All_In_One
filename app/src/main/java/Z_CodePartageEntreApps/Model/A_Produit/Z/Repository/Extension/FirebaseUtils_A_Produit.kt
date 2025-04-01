package Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.Extension

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

                    // We're removing this keepSynced call to avoid conflict with value listeners
                    // A_ProduitRepository.caReference.keepSynced(true)

                    initialized = true
                } catch (e: Exception) {
                    // Log error but continue
                }
            }
        }
    }
}
