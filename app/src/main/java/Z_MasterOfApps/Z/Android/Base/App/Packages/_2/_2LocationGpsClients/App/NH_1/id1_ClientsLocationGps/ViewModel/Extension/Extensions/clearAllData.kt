package Z_MasterOfApps.Z.Android.Base.App.Packages._2._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.ViewModel.Extension.Extensions

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Z.Android.Base.App.Packages._2._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.ViewModel.Extension.ViewModelExtension_App2_F1
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

fun ViewModelExtension_App2_F1.clearAllData() {
    viewModelScope.launch {
        try {
            produitsMainDataBase.forEach { produit ->
                produit.historiqueBonsVents.forEachIndexed { index, _ ->
                    try {
                        // Delete all GPS related data for each client
                        val gpsRef = produitsFireBaseRef
                            .child(produit.id.toString())
                            .child("historiqueBonsVents")
                            .child(index.toString())
                            .child("clientInformations")
                            .child("gpsLocation")

                        // Delete specific fields within gpsLocation
                        gpsRef.child("latitude").removeValue().await()
                        gpsRef.child("longitude").removeValue().await()
                        gpsRef.child("title").removeValue().await()
                        gpsRef.child("snippet").removeValue().await()
                        gpsRef.child("couleur").removeValue().await()

                        Log.d(
                            "FirebaseCleanup",
                            "Cleared GPS data for product ${produit.id}, bon vent index $index"
                        )
                    } catch (e: Exception) {
                        Log.e(
                            "FirebaseCleanup",
                            "Failed to clear GPS data for product ${produit.id}, bon vent index $index",
                            e
                        )
                    }
                }
            }

            // Special handling for product with ID 0
            val productZeroRef = produitsFireBaseRef.child("0")
            try {
                productZeroRef
                    .child("historiqueBonsVents")
                    .get()
                    .await()
                    .children
                    .forEach { snapshot ->
                        val clientRef = snapshot.ref
                            .child("clientInformations")
                            .child("gpsLocation")

                        // Delete specific fields within gpsLocation
                        clientRef.child("latitude").removeValue().await()
                        clientRef.child("longitude").removeValue().await()
                        clientRef.child("title").removeValue().await()
                        clientRef.child("snippet").removeValue().await()
                        clientRef.child("couleur").removeValue().await()
                    }

                Log.d("FirebaseCleanup", "Successfully cleared product 0 GPS data")
            } catch (e: Exception) {
                Log.e("FirebaseCleanup", "Failed to clear product 0 GPS data", e)
            }

            Log.d(
                "FirebaseCleanup",
                "Successfully cleared all data from UI, local storage, and Firebase"
            )
        } catch (e: Exception) {
            Log.e("FirebaseCleanup", "Failed to clear data", e)
            throw e
        }
    }
}
