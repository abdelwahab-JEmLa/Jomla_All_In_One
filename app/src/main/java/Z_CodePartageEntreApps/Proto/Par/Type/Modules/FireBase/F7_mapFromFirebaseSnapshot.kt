package Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase

import Z_CodePartageEntreApps.Model.A0_DataBasesGroup
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.TypeTarificationEnumT2
import Z_CodePartageEntreApps.Model.getKeyFireBase
import com.google.firebase.database.DataSnapshot

private const val TAG = "FirebaseMapping"

fun mapFromFirebaseSnapshot(snapshot: DataSnapshot): A0_DataBasesGroup {
    val tarifications = mutableListOf<D_TarificationInfos>()

    val defaultModel = A0_DataBasesGroup()

    val tariffsSnapshot = snapshot.child(defaultModel.refFireBaseD_TarificationInfos)
    if (tariffsSnapshot.exists()) {
        tarifications.addAll(mapTarificationInfos(tariffsSnapshot))
    }

    return A0_DataBasesGroup(
        d_TarificationInfos = tarifications
    )
}
private fun mapTarificationInfos(snapshot: DataSnapshot): List<D_TarificationInfos> {
    val results = mutableListOf<D_TarificationInfos>()

    for (childSnap in snapshot.children) {
        try {
            val id = childSnap.child("id").getValue(Long::class.java) ?: 0L
            val nom = childSnap.child("nom").getValue(String::class.java) ?: ""
            val needUpdate = childSnap.child("needUpdate").getValue(Boolean::class.java) ?: false
            val keyFireBase = childSnap.key ?: getKeyFireBase(id, nom)

            // Map all the missing fields from Firebase JSON
            val parentIdClient = childSnap.child("parentIdClient").getValue(Long::class.java) ?: 0L
            val idParentProduit = childSnap.child("idParentProduit").getValue(Long::class.java) ?: 0L
            val prixCurrency = childSnap.child("prixCurrency").getValue(Double::class.java) ?: 0.0
            val timestamps = childSnap.child("timestamps").getValue(Long::class.java) ?: System.currentTimeMillis()

            // Map the enum field
            val typeTarificationEnumString = childSnap.child("typeTarificationEnumT2Correspond").getValue(String::class.java) ?: "PRIX_BASE"
            val typeTarificationEnum = try {
                TypeTarificationEnumT2.valueOf(typeTarificationEnumString)
            } catch (e: Exception) {
                TypeTarificationEnumT2.PRIX_BASE // Default fallback
            }

            val instance = D_TarificationInfos(
                id = id,
                nom = nom,
                needUpdate = needUpdate,
                keyFireBase = keyFireBase,
                parentIdClient = parentIdClient,
                idParentProduit = idParentProduit,
                prixCurrency = prixCurrency,
                timestamps = timestamps,
                typeTarificationEnumT2Correspond = typeTarificationEnum
            )

            results.add(instance)
        } catch (e: Exception) {
        }
    }

    return results
}

