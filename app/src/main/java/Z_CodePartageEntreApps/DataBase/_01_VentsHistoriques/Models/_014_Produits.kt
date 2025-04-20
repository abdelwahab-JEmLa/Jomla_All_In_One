package Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Models

import com.google.firebase.database.DataSnapshot
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class _014_Produits : RealmObject {
    var id: Long = 0L
    var startDesignation: String = ""
    var tempCreationString: String = "yyyy_mm_dd(HH:mm)"

    @PrimaryKey
    var keyID: String = "${id}=(${startDesignation})"

    var quantity: Int = 0

    companion object{
        enum class NomsValeursModel{
            keyID,
            id,
            designation,
            tempCreationString,
            quantity,
        }
        fun testData(
        ):List<_014_Produits>  {
            val data = mutableListOf<_014_Produits>()

            for (k in 1..5) {
                data.add(_014_Produits().apply {
                    id = k.toLong()
                    startDesignation = "_014_Produits $k"
                    tempCreationString = "2025_04_20(12:00)"
                    keyID = "$id->$startDesignation"
                    quantity = k * 2
                })
            }
            return data
        }

        fun mapDatas(datas: List<_014_Produits>): Map<String, Any> {
            return datas.associate { data ->
                data.keyID to mapOf(
                    "id" to data.id,
                    "designation" to data.startDesignation,
                    NomsValeursModel.quantity.name to data.quantity,
                )
            }
        }

        fun parseDataFromSnapshot(snapshot: DataSnapshot): _014_Produits? {
            val produitKey = snapshot.key ?: return null

            return _014_Produits().apply {
                keyID = produitKey
                id = snapshot.child("id").getValue(Long::class.java) ?: 0L
                startDesignation = snapshot.child("designation").getValue(String::class.java) ?: ""
                quantity = snapshot.child(NomsValeursModel.quantity.name).getValue(Int::class.java) ?: 0
            }
        }
    }
}
