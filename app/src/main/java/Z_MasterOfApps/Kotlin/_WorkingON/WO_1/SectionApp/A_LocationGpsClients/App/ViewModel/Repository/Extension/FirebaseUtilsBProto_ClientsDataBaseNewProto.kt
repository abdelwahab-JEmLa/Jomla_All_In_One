package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.Extension

import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.BProto_ClientsDataBaseRepository

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
