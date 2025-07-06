package Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.Proto.FireBase

import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import Z_CodePartageEntreApps.Model.A0_DataBasesGroup
import Z_CodePartageEntreApps.Model.getKeyFireBase
import com.google.firebase.database.DataSnapshot

private const val TAG = "FirebaseMapping"

fun mapFromFirebaseSnapshot(snapshot: DataSnapshot): A0_DataBasesGroup {
    val tarifications = mutableListOf<M13TarificationInfos>()

    val defaultModel = A0_DataBasesGroup()

    val tariffsSnapshot = snapshot.child(defaultModel.refFireBaseD_TarificationInfos)
    if (tariffsSnapshot.exists()) {
        tarifications.addAll(mapTarificationInfos(tariffsSnapshot))
    }

    return A0_DataBasesGroup(
        d_TarificationInfos = tarifications
    )
}
private fun mapTarificationInfos(snapshot: DataSnapshot): List<M13TarificationInfos> {
    val results = mutableListOf<M13TarificationInfos>()

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
                M13TarificationInfos.TypeChoisi.valueOf(typeTarificationEnumString)
            } catch (e: Exception) {
                M13TarificationInfos.TypeChoisi.PRIX_BASE // Default fallback
            }

            val instance = M13TarificationInfos(
                id = id,
                nom = nom,
                needUpdate = needUpdate,
                keyFireBase = keyFireBase,
                parentIdClient = parentIdClient,
                idParentProduit = idParentProduit,
                prixCurrency = prixCurrency,
                timestamps = timestamps,
                typeChoisi = typeTarificationEnum
            )

            results.add(instance)
        } catch (e: Exception) {
        }
    }

    return results
}

