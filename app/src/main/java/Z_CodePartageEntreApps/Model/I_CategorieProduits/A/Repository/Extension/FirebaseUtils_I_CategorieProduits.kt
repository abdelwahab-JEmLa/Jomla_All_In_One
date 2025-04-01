package Z_CodePartageEntreApps.Model.I_CategorieProduits.A.Repository.Extension

import Z_CodePartageEntreApps.Model.I_CategorieProduits.A.Repository.I_CategorieProduitsRepository
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils_I_CategorieProduits {
    private var initialized = false
    private val initLock = Any()

    fun initializeFirebaseOfflineCapability() {
        synchronized(initLock) {
            if (!initialized) {
                try {
                    // Set persistence enabled for the entire database
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true)

                    // Keep synchronized for the specific reference we're using
                    I_CategorieProduitsRepository.caReference.keepSynced(true)

                    initialized = true
                } catch (e: Exception) {
                    // Log error but continue
                }
            }
        }
    }
}
