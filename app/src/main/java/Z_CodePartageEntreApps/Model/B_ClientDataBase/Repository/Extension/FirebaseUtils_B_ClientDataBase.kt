package Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.Extension

import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository

object FirebaseUtils_B_ClientDataBase {
    private const val DEBOUNCE_INTERVAL = 500L

    fun initializeFirebaseOfflineCapability() {
        try {
            B_ClientDataBaseRepository.caReference.keepSynced(true)
        } catch (e: Exception) {
            // Silently handle exception
        }
    }

}
