package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models

import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class _13_Acheteurs : RealmObject {
    var id: Long = 0L
    var designation: String = ""
    var tempCreationString: String = "yyyy.mm.dd(HH:mm)"

    @PrimaryKey
    var keyID: String = "parent.keyID-<{BA}->(designation[id])"

    var child_15_Produits: RealmList<_15_Produits> = realmListOf()

    companion object {
        fun testData(parentkeyID: String): List<_13_Acheteurs> {
            val data = mutableListOf<_13_Acheteurs>()

            for (k in 1..5) {
                val acheteur = _13_Acheteurs().apply {
                    id = k.toLong()
                    designation = "_15_Produits $k"
                    tempCreationString = "2025.04.20(12:00)"
                    keyID = "$parentkeyID-<{BA}->($designation[$id])"
                    child_15_Produits = realmListOf()
                }

                // Create and add products
                val produits = _15_Produits.testData(acheteur.keyID)
                produits.forEach { produit ->
                    acheteur.child_15_Produits.add(produit)
                }

                data.add(acheteur)
            }

            return data
        }

        fun mapDatas(datas: List<_13_Acheteurs>): Map<String, Any> {
            return datas.associate { data ->
                data.keyID to mapOf(
                    nomsValeursModel.id.name to data.id,
                    nomsValeursModel.designation.name to data.designation,
                    nomsValeursModel.tempCreationString.name to data.tempCreationString,
                    nomsValeursModel.child_15_Produits.name to _15_Produits.mapDatas(data.child_15_Produits)
                )
            }
        }

        fun parse_13_AcheteursFromSnapshot(snapshot: DataSnapshot): _13_Acheteurs? {
            val acheteurKey = snapshot.key ?: return null

            val acheteur = _13_Acheteurs().apply {
                keyID = acheteurKey
                id = snapshot.child(nomsValeursModel.id.name).getValue(Long::class.java) ?: 0L
                designation = snapshot.child(nomsValeursModel.designation.name).getValue(String::class.java) ?: ""
                tempCreationString = snapshot.child(nomsValeursModel.tempCreationString.name).getValue(String::class.java) ?: "yyyy.mm.dd(HH:mm)"
                child_15_Produits = realmListOf()
            }

            val produitsSnapshot = snapshot.child(nomsValeursModel.child_15_Produits.name)
            produitsSnapshot.children.forEach { produitSnapshot ->
                val produit = _15_Produits.parse_13_AcheteursFromSnapshot(produitSnapshot) ?: return@forEach
                acheteur.child_15_Produits.add(produit)
            }

            return acheteur
        }
        
        enum class nomsValeursModel{
            keyID,                      
            id,
            designation,
            tempCreationString,
            child_15_Produits
        }
    }
}
