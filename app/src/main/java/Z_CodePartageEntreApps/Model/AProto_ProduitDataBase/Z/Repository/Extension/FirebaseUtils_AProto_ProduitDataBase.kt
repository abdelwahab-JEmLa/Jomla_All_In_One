package Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.Z.Repository.Extension

import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.Z.Repository.AProto_ProduitDataBaseRepository
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils_AProto_ProduitDataBase {
    private var initialized = false
    private val initLock = Any()

    fun initializeFirebaseOfflineCapability() {
        synchronized(initLock) {
            if (!initialized) {
                try {
                    // Set persistence enabled for the entire database
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true)

                    // Keep synchronized for the specific reference we're using
                    AProto_ProduitDataBaseRepository.caReference.keepSynced(true)

                    initialized = true
                } catch (e: Exception) {
                    // Log error but continue
                }
            }
        }
    }
}
