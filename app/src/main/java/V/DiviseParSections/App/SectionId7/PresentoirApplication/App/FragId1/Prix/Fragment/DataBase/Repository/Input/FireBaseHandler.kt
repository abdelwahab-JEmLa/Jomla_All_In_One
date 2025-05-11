package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FireBaseHandler(private val testContext: Any) {

    fun <T> loadDatas(databaseRef: DatabaseReference, dataClass: Class<T>): List<T> {
        val dataList = mutableListOf<T>()
        // Use ValueEventListener to fetch data from Firebase
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    childSnapshot.getValue(dataClass)?.let { item ->
                        dataList.add(item)
                    }
                }

                if (testContext is TarificationViewModel.TestCallbacks) {
                    testContext.onOperationSuccess()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        return dataList
    }

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
                    if (testContext is TarificationViewModel.TestCallbacks) {
                        (testContext).onOperationSuccess()
                    }
                }
            }
        }
    }
}
