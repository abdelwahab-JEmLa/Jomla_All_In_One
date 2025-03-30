package Z_CodePartageEntreApps.Model.BProto_ClientsDataBaseRepo.Repository.Extension

import Z_CodePartageEntreApps.Model.BProto_ClientsDataBaseRepo.Repository.BProto_ClientsDataBaseRepository

object FirebaseUtilsBProto_ClientsDataBaseNewProto {
    private const val DEBOUNCE_INTERVAL = 500L

    fun initializeFirebaseOfflineCapability() {
        try {
            BProto_ClientsDataBaseRepository.caReference.keepSynced(true)
        } catch (e: Exception) {
            // Silently handle exception
        }
    }


}
