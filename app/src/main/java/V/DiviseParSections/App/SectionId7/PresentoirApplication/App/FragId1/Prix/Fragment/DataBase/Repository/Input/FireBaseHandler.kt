package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import com.google.firebase.database.DatabaseReference

class FireBaseHandler(private val testContext: Any) {
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
                    if (testContext is InputEtInfosSqlGroupeRepositorysImp.TestCallbacks) {
                        (testContext).onOperationSuccess()
                    }
                }
            }
        }
    }
}
