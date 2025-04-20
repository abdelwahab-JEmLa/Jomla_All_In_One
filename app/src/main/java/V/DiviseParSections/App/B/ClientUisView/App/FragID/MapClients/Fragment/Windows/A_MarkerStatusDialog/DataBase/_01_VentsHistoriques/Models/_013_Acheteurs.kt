package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models

import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class _013_Acheteurs : RealmObject {
    var id: Long = 0L

    var idClient: Long = 0L

    var startDesignation: String = ""
    var tempCreationString: String = "yyyy_mm_dd(HH:mm)"

    @PrimaryKey
    var keyID: String = "${id}=${startDesignation.replace(" ", "_")}"

    var child_14Produits: RealmList<_014_Produits> = realmListOf()

    companion object {
        fun testData(): List<_013_Acheteurs> {
            val data = mutableListOf<_013_Acheteurs>()

            for (k in 1..5) {
                val acheteur = _013_Acheteurs().apply {
                    id = k.toLong()
                    startDesignation = "_013_Acheteurs $k"
                    tempCreationString = "2025_04_20(12:00)"
                    keyID = "$id->$startDesignation"
                    child_14Produits = realmListOf()
                }

                // Create and add products
                val produits = _014_Produits.testData()
                produits.forEach { produit ->
                    acheteur.child_14Produits.add(produit)
                }

                data.add(acheteur)
            }

            return data
        }

        fun mapDatas(datas: List<_013_Acheteurs>): Map<String, Any> {
            return datas.associate { data ->
                data.keyID to mapOf(
                    NomsValeursModel.id.name to data.id,
                    NomsValeursModel.designation.name to data.startDesignation,
                    NomsValeursModel.tempCreationString.name to data.tempCreationString,
                    NomsValeursModel.child_15_Produits.name to _014_Produits.mapDatas(data.child_14Produits)
                )
            }
        }

        fun parse_13_AcheteursFromSnapshot(snapshot: DataSnapshot): _013_Acheteurs? {
            val acheteurKey = snapshot.key ?: return null

            val acheteur = _013_Acheteurs().apply {
                keyID = acheteurKey
                id = snapshot.child(NomsValeursModel.id.name).getValue(Long::class.java) ?: 0L
                startDesignation = snapshot.child(NomsValeursModel.designation.name).getValue(String::class.java) ?: ""
                tempCreationString = snapshot.child(NomsValeursModel.tempCreationString.name).getValue(String::class.java) ?: "yyyy.mm.dd(HH:mm)"
                child_14Produits = realmListOf()
            }

            val produitsSnapshot = snapshot.child(NomsValeursModel.child_15_Produits.name)
            produitsSnapshot.children.forEach { produitSnapshot ->
                val produit = _014_Produits.parseDataFromSnapshot(produitSnapshot) ?: return@forEach
                acheteur.child_14Produits.add(produit)
            }

            return acheteur
        }
        

    }
    enum class NomsValeursModel{
        keyID,
        id,
        designation,
        tempCreationString,
        child_15_Produits
    }
}
