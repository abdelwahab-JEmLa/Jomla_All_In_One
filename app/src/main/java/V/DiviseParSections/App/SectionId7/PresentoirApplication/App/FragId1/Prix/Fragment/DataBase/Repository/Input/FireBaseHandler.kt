package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import com.google.firebase.database.DatabaseReference

class FireBaseHandler(private val testContext: Any) {
    /**
     * Interface for test callbacks moved to InputEtInfosSqlGroupeRepositorysImp
     */

    fun <T> addAllToFireBase(modelList: List<T>, databaseRef: DatabaseReference) {
        if (modelList.isEmpty()) return

        modelList.forEach { item ->
            val key = when (item) {
                is InputEtInfosSqlModels.Tarification -> item.vidTimestamp
                is InputEtInfosSqlModels.ClientDataBase -> item.id
                is InputEtInfosSqlModels.ProduitInfos -> item.id
                is InputEtInfosSqlModels.TypeTarificationDataBase -> item.id
                else -> databaseRef.push().key
            }

            key?.let {
                databaseRef.child(it.toString()).setValue(item).addOnSuccessListener {
                    // Success callback
                    if (testContext is InputEtInfosSqlGroupeRepositorysImp.TestCallbacks) {
                        (testContext as InputEtInfosSqlGroupeRepositorysImp.TestCallbacks).onOperationSuccess()
                    }
                }.addOnFailureListener { exception ->
                    // Optional: handle failures if needed
                    // Currently no failure handling in the TestCallbacks interface
                }
            }
        }
    }

    // Add a single item to Firebase with better error handling
    fun <T> addToFireBase(item: T, databaseRef: DatabaseReference, onSuccess: () -> Unit = {}) {
        val key = when (item) {
            is InputEtInfosSqlModels.Tarification -> item.vidTimestamp
            is InputEtInfosSqlModels.ClientDataBase -> item.id
            is InputEtInfosSqlModels.ProduitInfos -> item.id
            is InputEtInfosSqlModels.TypeTarificationDataBase -> item.id
            else -> databaseRef.push().key
        }

        key?.let {
            databaseRef.child(it.toString()).setValue(item).addOnSuccessListener {
                // Execute the onSuccess callback
                onSuccess()

                // Also notify test context if applicable
                if (testContext is InputEtInfosSqlGroupeRepositorysImp.TestCallbacks) {
                    (testContext as InputEtInfosSqlGroupeRepositorysImp.TestCallbacks).onOperationSuccess()
                }
            }
        }
    }
}
