package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models

import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class _14_Produits : RealmObject {
    var id: Long = 0L
    var startDesignation: String = ""
    var tempCreationString: String = "yyyy.mm.dd(HH:mm)"

    @PrimaryKey
    var keyID: String = "${id}_${startDesignation.replace(" ", "_")}"

    var quantity: Int = 0

    companion object{
        enum class nomsValeursModel{
            keyID,
            id,
            designation,
            tempCreationString,
            quantity,
        }
        fun testData(
        ):List<_14_Produits>  {
            val data = mutableListOf<_14_Produits>()

            for (k in 1..5) {
                data.add(_14_Produits().apply {
                    id = k.toLong()
                    startDesignation = "_14_Produits $k"
                    tempCreationString = "2025_04_20(12:00)"
                    keyID = "$id->$startDesignation"
                    quantity = k * 2
                })
            }
            return data
        }

        fun mapDatas(datas: List<_14_Produits>): Map<String, Any> {
            return datas.associate { data ->
                data.keyID to mapOf(
                    "id" to data.id,
                    "designation" to data.startDesignation,
                )
            }
        }

        fun parseDataFromSnapshot(snapshot: DataSnapshot): _14_Produits? {
            val produitKey = snapshot.key ?: return null

            return _14_Produits().apply {
                keyID = produitKey
                id = snapshot.child("id").getValue(Long::class.java) ?: 0L
                startDesignation = snapshot.child("designation").getValue(String::class.java) ?: ""
                quantity = snapshot.child(nomsValeursModel.quantity.name).getValue(Int::class.java) ?: 0
            }
        }
    }
}
