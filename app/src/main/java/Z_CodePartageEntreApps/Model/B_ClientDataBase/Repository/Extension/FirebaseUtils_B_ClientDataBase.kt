package Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.Extension

import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils_B_ClientDataBase {
    private var initialized = false
    private val initLock = Any()

    fun initializeFirebaseOfflineCapability() {
        synchronized(initLock) {
            if (!initialized) {
                try {
                    // Set persistence enabled for the entire database
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true)

                    // Keep synchronized for the specific reference we're using
                    B_ClientDataBaseRepository.caReference.keepSynced(true)

                    initialized = true
                } catch (e: Exception) {
                    // Log error but continue
                }
            }
        }
    }
}
