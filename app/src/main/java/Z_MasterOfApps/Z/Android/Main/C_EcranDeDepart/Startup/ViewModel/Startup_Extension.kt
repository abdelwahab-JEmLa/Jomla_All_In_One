package Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.ViewModel

import Z_MasterOfApps.Kotlin.Model.E_AppsOptionsStates
import Z_MasterOfApps.Kotlin.Model.E_AppsOptionsStates.ApplicationEstInstalleDonTelephone.Companion.metricsWidthPixels
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import com.google.firebase.Firebase
import com.google.firebase.database.database

class Startup_Extension(
    val viewModelInitApp: ViewModelInitApp,
) {
    val produitsMainDataBase = viewModelInitApp.produitsMainDataBase
    val applicationEstInstalleDonTelephone =
        viewModelInitApp._modelAppsFather.applicationEstInstalleDonTelephone

    init {
        val manufacturer = android.os.Build.MANUFACTURER
        val model = android.os.Build.MODEL
        val phoneName = "$manufacturer $model"

        // Verify and add the phone
        verifyAndAddPhone(phoneName, metricsWidthPixels)
    }

    fun verifyAndAddPhone(phoneName: String, screenWidth: Int) {
        // Get reference to the phones in Firebase
        E_AppsOptionsStates.caReference.get().addOnSuccessListener { snapshot ->
            var phoneExists = false
            var maxId = 0

            // Check if phone exists and find max ID
            snapshot.children.forEach { snap ->
                try {
                    val phone = E_AppsOptionsStates.ApplicationEstInstalleDonTelephone().apply {
                        id = snap.child("id").getValue(Int::class.java) ?: 0
                        nom = snap.child("nom").getValue(String::class.java) ?: ""
                        widthScreen = snap.child("widthScreen").getValue(Int::class.java) ?: 0
                        itsReciverTelephone = snap.child("itsReciverTelephone").getValue(Boolean::class.java) ?: false
                    }

                    // Update max ID
                    if (phone.id > maxId) {
                        maxId = phone.id
                    }

                    // Check if phone exists
                    if (phone.nom == phoneName) {
                        phoneExists = true
                        // Update local state
                        applicationEstInstalleDonTelephone.add(phone)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // If phone doesn't exist, add it
            if (!phoneExists) {
                val newId = maxId + 1

                val newPhone = E_AppsOptionsStates.ApplicationEstInstalleDonTelephone().apply {
                    id = newId
                    nom = phoneName
                    widthScreen = screenWidth
                    itsReciverTelephone = false
                }

                // Add to local state
                applicationEstInstalleDonTelephone.add(newPhone)

                // Add to Firebase
                E_AppsOptionsStates.caReference
                    .child(newId.toString())
                    .setValue(newPhone)
            }
        }.addOnFailureListener { exception ->
            // Handle any errors
            exception.printStackTrace()
        }
    }


    fun clearAchats() {
        // Create a snapshot of the products to avoid concurrent modification
        val productsToProcess = viewModelInitApp._modelAppsFather.produitsMainDataBase.toList()

        // Clear Firebase references first
        val database = Firebase.database
        database.getReference("K_GroupeurBonCommendToSupplierRef").removeValue()
        database.getReference("O_SoldArticlesTabelle").removeValue()

        // Process each product safely
        productsToProcess.forEach { produit ->
            // Safely add current data to history
            produit.bonCommendDeCetteCota?.let { currentBonCommend ->
                // Create a new list and add all items to avoid concurrent modification
                val updatedHistorique = ArrayList(produit.historiqueBonsCommend)
                updatedHistorique.add(currentBonCommend)
                produit.historiqueBonsCommend.clear()
                produit.historiqueBonsCommend.addAll(updatedHistorique)
            }

            if (produit.bonsVentDeCetteCotaList.isNotEmpty()) {
                // Create a new list and add all items to avoid concurrent modification
                val updatedHistoriqueVents = ArrayList(produit.historiqueBonsVents)
                updatedHistoriqueVents.addAll(produit.bonsVentDeCetteCotaList)
                produit.historiqueBonsVents.clear()
                produit.historiqueBonsVents.addAll(updatedHistoriqueVents)
            }

            // Clear current data
            produit.bonCommendDeCetteCota = null
            produit.bonsVentDeCetteCota.clear()

            // Update the product in Firebase
            updateProduit(produit, viewModelInitApp)
        }
    }

    fun implimentClientsParProduits() {
        // Implementation remains unchanged
    }
}
