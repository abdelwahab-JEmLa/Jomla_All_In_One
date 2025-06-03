package Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models

import Z_CodePartageEntreApps.Model.I_CategoriesProduits
import android.util.Log
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

data class AncienProto(
    val c_CategorieProduitInfos: C_CategorieProduitInfosAncienProto,
) {
    data class C_CategorieProduitInfosAncienProto(
        val proto1: Proto1,
        val protoB2: ProtoB2,
        val proto2: Proto2,
    ) {
        data class Proto1(
            val ref: DatabaseReference = Companion.ref.child("Proto1"),
            val datas: I_CategoriesProduits,
        )

        data class ProtoB2(
            @PrimaryKey(autoGenerate = true)
            var id: Long = 0L,

            // Section InfosDeBase
            var nom: String = "Non Defini",
            var groupeParentId: Long = 0L,

            // Section StatuesMutable
            var indexDonsParentList: Long = 0,
            var afficheSonHeader: Boolean = false,
        ) {
            companion object {
                val caRef = ref.child("ProtoB2")

                private suspend fun getFireBaseProto2Datas(): List<CategoriesTabelle> {
                    return try {
                        Log.d("Test", "ProtoB2Ref=$caRef")

                        val snapshot = caRef.get().await()

                        val result = mutableListOf<CategoriesTabelle>()

                        snapshot.children.forEach { dataSnapshot ->
                            try {
                                // Skip the "0_NomModel" entry and "1Proto1DataBase" entry
                                if (dataSnapshot.key == "0_NomModel" || dataSnapshot.key == "1Proto1DataBase") {
                                    return@forEach
                                }

                                // Use the ProtoB2 data class to parse the Firebase data
                                val protoB2Data = dataSnapshot.getValue(ProtoB2::class.java)

                                if (protoB2Data != null) {
                                    // Convert ProtoB2 to CategoriesTabelle using the new schema
                                    val categorieProduitInfo = CategoriesTabelle(
                                        id = protoB2Data.id,
                                        nom = protoB2Data.nom,
                                        position = protoB2Data.indexDonsParentList.toInt(), // Map indexDonsParentList to position
                                        displayedHeader = protoB2Data.afficheSonHeader, // Map afficheSonHeader to displayedHeader
                                        itsHeldPourDeplacement = false, // Default value as it doesn't exist in ProtoB2
                                        dernierFireBaseUpdateTimestamps = System.currentTimeMillis(), // Set current time
                                        keyFireBase = dataSnapshot.key ?: "" // Use the Firebase key as keyFireBase
                                    )

                                    result.add(categorieProduitInfo)
                                }

                            } catch (e: Exception) {
                                Log.e("RepositorysPreviewViewModel", "Error parsing ProtoB2 data: ${e.message}")
                            }
                        }

                        result

                    } catch (e: Exception) {
                        Log.e("RepositorysPreviewViewModel", "Error getting Firebase Proto2 data: ${e.message}")
                        emptyList()
                    }
                }

                suspend fun updateCategoriePositionDepuitProto2(categorysInit: List<CategoriesTabelle>)
                        : List<CategoriesTabelle> {
                    val firebaseProto2Data = getFireBaseProto2Datas()

                    Log.d("firebaseProto2Data", "firebaseProto2Data=${firebaseProto2Data}")

                    Log.d("updateCategoriePositionDepuitProto2", "-----------firebaseProto2Datamap=${firebaseProto2Data.map { it.nom }}")

                    return categorysInit.map { dataActuelle ->
                        val matchedDataDeptuitAncienProto2 =
                            firebaseProto2Data.find { it.nom == dataActuelle.nom }

                        if (dataActuelle.id == 4L) {
                            Log.d(
                                "updateCategoriePositionDepuitProto2",
                                "Actelle${dataActuelle.nom}==position>${dataActuelle.position}" +
                                        "Proto${matchedDataDeptuitAncienProto2?.nom}==position>${matchedDataDeptuitAncienProto2?.position}"
                            )
                        }

                        if (matchedDataDeptuitAncienProto2 != null) {
                            dataActuelle.copy(position = matchedDataDeptuitAncienProto2.position)
                        } else {
                            dataActuelle.copy(position = 0)
                        }
                    }
                }
            }
        }

        data class Proto2(
            val ref: DatabaseReference = Companion.ref.child("Proto2"),
            val datas: I_CategoriesProduits,
        )

        companion object {
            val ref = AncienProto.ref.child("C_CategorieProduitInfosAncienProto")

            fun securedRemoveFireBaseDB() {
                ref.removeValue()
            }
        }
    }

    companion object {
        val ref =
            Firebase.database.getReference(
                "00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/AncienDataBase"
            )

        fun securedRemoveFireBaseDB() {
            ref.removeValue()
        }
    }

}
