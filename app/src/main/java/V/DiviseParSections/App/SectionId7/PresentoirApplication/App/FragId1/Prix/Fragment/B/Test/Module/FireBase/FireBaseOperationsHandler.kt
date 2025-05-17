package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.Module.FireBase

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.Module.SQl.RoomOperationsHandler
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
                         //<--
                         //TODO(1): regle le code pour qe le sorit {
                         //  "A_ProduitInfos": {
                         //    "-<1(Produit Optila)": {
                         //      "id": 1,
                         //      "keyFireBase": "-<1(Produit Optila)",
                         //      "needUpdate": false,
                         //      "nom": "Produit Optila"
                         //    },
                         //    "-<2(Produit Hnina)": {
                         //      "id": 2,
                         //      "keyFireBase": "-<2(Produit Hnina)",
                         //      "needUpdate": false,
                         //      "nom": "Produit Hnina"
                         //    },
                         //    "-<3(Produit kemya)": {
                         //      "id": 3,
                         //      "keyFireBase": "-<3(Produit kemya)",
                         //      "needUpdate": false,
                         //      "nom": "Produit kemya"
                         //    }
                         //  },
                         //  "B_ClientInfos": {
                         //    "-<1(ClientAchteur Abderrahman)": {
                         //      "id": 1,
                         //      "idActiveTypeTarificationDataBase": 1,
                         //      "keyFireBase": "-<1(ClientAchteur Abderrahman)",
                         //      "needUpdate": false,
                         //      "nom": "ClientAchteur Abderrahman"
                         //    },
                         //    "-<2(ClientAchteur Beta)": {
                         //      "id": 2,
                         //      "idActiveTypeTarificationDataBase": 2,
                         //      "keyFireBase": "-<2(ClientAchteur Beta)",
                         //      "needUpdate": false,
                         //      "nom": "ClientAchteur Beta"
                         //    },
                         //    "-<3(ClientAchteur Gamma)": {
                         //      "id": 3,
                         //      "idActiveTypeTarificationDataBase": 3,
                         //      "keyFireBase": "-<3(ClientAchteur Gamma)",
                         //      "needUpdate": false,
                         //      "nom": "ClientAchteur Gamma"
                         //    }
                         //  },
                         //  "D_TarificationInfos": {
                         //    "-<1746099000000(2025-05-17 -< 15:23)": {
                         //      "idClient": 1,
                         //      "idProduit": 1,
                         //      "idTypeTarification": 1,
                         //      "keyFireBase": "-<1746099000000(2025-05-17 -< 15:23)",
                         //      "needUpdate": false,
                         //      "nom": "2025-05-17 -< 15:23",
                         //      "prixCurrency": 20.99,
                         //      "vidTimestamp": 1746099000000
                         //    },
                         //    "-<1746448200000(2025-05-17 -< 15:23)": {
                         //      "idClient": 1,
                         //      "idProduit": 1,
                         //      "idTypeTarification": 1,
                         //      "keyFireBase": "-<1746448200000(2025-05-17 -< 15:23)",
                         //      "needUpdate": false,
                         //      "nom": "2025-05-17 -< 15:23",
                         //      "prixCurrency": 25.5,
                         //      "vidTimestamp": 1746448200000
                         //    },
                         //    "-<1746451800000(2025-05-17 -< 15:23)": {
                         //      "idClient": 2,
                         //      "idProduit": 1,
                         //      "idTypeTarification": 2,
                         //      "keyFireBase": "-<1746451800000(2025-05-17 -< 15:23)",
                         //      "needUpdate": false,
                         //      "nom": "2025-05-17 -< 15:23",
                         //      "prixCurrency": 9.75,
                         //      "vidTimestamp": 1746451800000
                         //    },
                         //    "-<1746498600000(2025-05-17 -< 15:23)": {
                         //      "idClient": 1,
                         //      "idProduit": 2,
                         //      "idTypeTarification": 1,
                         //      "keyFireBase": "-<1746498600000(2025-05-17 -< 15:23)",
                         //      "needUpdate": false,
                         //      "nom": "2025-05-17 -< 15:23",
                         //      "prixCurrency": 15.25,
                         //      "vidTimestamp": 1746498600000
                         //    },
                         //    "-<1746502200000(2025-05-17 -< 15:23)": {
                         //      "idClient": 1,
                         //      "idProduit": 3,
                         //      "idTypeTarification": 3,
                         //      "keyFireBase": "-<1746502200000(2025-05-17
class FireBaseOperationsHandler(
     val roomOperationsHandler: RoomOperationsHandler
) {
    companion object {
        const val TAG = "FireBaseOperationsHandler"
    }

    val ref: DatabaseReference = _0_0_HeadOfRepositorys_Model
        .getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var needUpdateListener: ValueEventListener? = null

    private fun isDatabaseEmpty(onResult: (Boolean) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isEmpty = !snapshot.exists() || !snapshot.hasChildren()
                onResult(isEmpty)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false)
            }
        })
    }

    suspend fun isDatabaseEmptyAsync(): Boolean = suspendCancellableCoroutine { continuation ->
        isDatabaseEmpty { isEmpty ->
            continuation.resume(isEmpty)
        }
    }

    fun addToFirebaseAsync(dataBasesInfosSql: DataBasesInfosSql, onSuccess: () -> Unit = {}) {
        val firebaseData = mapToFirebaseFormat(dataBasesInfosSql)
        val updates = mutableMapOf<String, Any>()

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseA_ProduitInfos)) {
            updates[dataBasesInfosSql.refFireBaseA_ProduitInfos] = firebaseData[dataBasesInfosSql.refFireBaseA_ProduitInfos] as Any
        }

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseB_ClientInfos)) {
            updates[dataBasesInfosSql.refFireBaseB_ClientInfos] = firebaseData[dataBasesInfosSql.refFireBaseB_ClientInfos] as Any
        }

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseC_TypeTarificationInfos)) {
            updates[dataBasesInfosSql.refFireBaseC_TypeTarificationInfos] = firebaseData[dataBasesInfosSql.refFireBaseC_TypeTarificationInfos] as Any
        }

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseD_TarificationInfos)) {
            updates[dataBasesInfosSql.refFireBaseD_TarificationInfos] = firebaseData[dataBasesInfosSql.refFireBaseD_TarificationInfos] as Any
        }

        if (updates.isEmpty()) {
            onSuccess()
            return
        }

        ref.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                ref.root.child(".info/connected").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
                onSuccess()
            }
    }
}
