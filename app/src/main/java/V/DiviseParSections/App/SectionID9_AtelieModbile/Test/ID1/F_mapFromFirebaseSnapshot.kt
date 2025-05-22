package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.TypeTarificationEnum
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.B_ClientInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.C_TypeTarificationInfos
import com.google.firebase.database.DataSnapshot
import kotlin.reflect.KClass

private const val TAG = "FirebaseMapping"

fun mapFromFirebaseSnapshot(snapshot: DataSnapshot): DataBasesInfosSql {
    val products = mutableListOf<A_ProduitInfos>()
    val clients = mutableListOf<B_ClientInfos>()
    val typeTarifications = mutableListOf<C_TypeTarificationInfos>()
    val tarifications = mutableListOf<D_TarificationInfos>()

    val defaultModel = DataBasesInfosSql()

    val productsSnapshot = snapshot.child(defaultModel.refFireBaseA_ProduitInfos)
    if (productsSnapshot.exists()) {
        products.addAll(mapSnapshotToObjects(productsSnapshot, A_ProduitInfos::class))
    }

    val clientsSnapshot = snapshot.child(defaultModel.refFireBaseB_ClientInfos)
    if (clientsSnapshot.exists()) {
        clients.addAll(mapSnapshotToObjects(clientsSnapshot, B_ClientInfos::class))
    }

    val typeTarifsSnapshot = snapshot.child(defaultModel.refFireBaseC_TypeTarificationInfos)
    if (typeTarifsSnapshot.exists()) {
        typeTarifications.addAll(mapTypeTarificationsWithReflection(typeTarifsSnapshot))
    }

    val tarifsSnapshot = snapshot.child(defaultModel.refFireBaseD_TarificationInfos)
    if (tarifsSnapshot.exists()) {
        tarifications.addAll(mapTarificationInfos(tarifsSnapshot))
    }

    return DataBasesInfosSql(
        a_ProduitInfos = products,
        b_ClientInfosList = clients,
        c_TypeTarificationInfos = typeTarifications,
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
            val idParentBonAchat = childSnap.child("idParentBonAchat").getValue(Long::class.java) ?: 0L
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
                idParentBonAchat = idParentBonAchat,
                idParentProduit = idParentProduit,
                prixCurrency = prixCurrency,
                timestamps = timestamps,
                typeTarificationEnumT2Correspond = typeTarificationEnum
            )

            results.add(instance)
        } catch (e: Exception) {
            println("Error mapping D_TarificationInfos: ${e.message}")
            println("Failed to map child with key: ${childSnap.key}")
        }
    }

    return results
}

private fun mapTypeTarificationsWithReflection(snapshot: DataSnapshot): List<C_TypeTarificationInfos> {
    val results = mutableListOf<C_TypeTarificationInfos>()

    for (childSnap in snapshot.children) {
        try {
            val id = childSnap.child("id").getValue(Long::class.java) ?: 0L
            val needUpdate = childSnap.child("needUpdate").getValue(Boolean::class.java) ?: false

            val typeTarifString =
                childSnap.child("typeTarificationEnum").getValue(String::class.java)
                    ?: "ParBenifice"
            val typeTarifEnum = try {
                java.lang.Enum.valueOf(TypeTarificationEnum::class.java, typeTarifString)
            } catch (e: Exception) {
                TypeTarificationEnum.ParBenifice
            }

            // Use Firebase key as keyFireBase property
            val keyFireBase = childSnap.key ?: getKeyFireBase(id, typeTarifEnum.name)

            val instance = C_TypeTarificationInfos(
                id = id,
                entityCorrespond = typeTarifEnum,
                needUpdate = needUpdate,
                keyFireBase = keyFireBase
            )

            results.add(instance)
        } catch (e: Exception) {
            // Consider logging the exception here
            println("Error mapping C_TypeTarificationInfos: ${e.message}")
        }
    }

    return results
}

private inline fun <reified T : Any> mapSnapshotToObjects(
    snapshot: DataSnapshot,
    kClass: KClass<T>
): List<T> {
    val results = mutableListOf<T>()

    for (childSnap in snapshot.children) {
        try {
            val constructor = kClass.constructors.firstOrNull()
                ?: throw Exception("No constructor found for ${kClass.simpleName}")

            // Create a map to hold parameter values
            val paramValues = mutableMapOf<String, Any?>()

            // Get id and nom for keyFireBase generation - collect these first
            var id: Long = 0
            var nom: String = ""

            // First pass: collect id and nom
            for (param in constructor.parameters) {
                val paramName = param.name ?: continue

                if (paramName == "id") {
                    val idValue = childSnap.child(paramName).getValue(Long::class.java) ?: 0L
                    id = idValue
                } else if (paramName == "nom") {
                    val nomValue = childSnap.child(paramName).getValue(String::class.java) ?: ""
                    nom = nomValue
                }
            }

            // Second pass: collect all values including keyFireBase
            for (param in constructor.parameters) {
                val paramName = param.name ?: continue

                when (paramName) {
                    "id" -> {
                        paramValues[paramName] = id
                    }

                    "nom" -> {
                        paramValues[paramName] = nom
                    }

                    "keyFireBase" -> {
                        // Now we can safely generate keyFireBase with id and nom
                        paramValues[paramName] = childSnap.key ?: getKeyFireBase(id, nom)
                    }

                    else -> {
                        val childValue = childSnap.child(paramName)
                        if (childValue.exists()) {
                            when (param.type.classifier) {
                                Long::class -> paramValues[paramName] =
                                    childValue.getValue(Long::class.java)

                                String::class -> paramValues[paramName] =
                                    childValue.getValue(String::class.java)

                                Boolean::class -> paramValues[paramName] =
                                    childValue.getValue(Boolean::class.java)

                                Double::class -> paramValues[paramName] =
                                    childValue.getValue(Double::class.java)

                                Int::class -> paramValues[paramName] =
                                    childValue.getValue(Int::class.java)?.toLong()

                                else -> paramValues[paramName] = null
                            }
                        } else {
                            // Provide default values for missing parameters
                            when (param.type.classifier) {
                                Long::class -> paramValues[paramName] = 0L
                                String::class -> paramValues[paramName] = ""
                                Boolean::class -> paramValues[paramName] = false
                                Double::class -> paramValues[paramName] = 0.0
                                Int::class -> paramValues[paramName] = 0
                                else -> paramValues[paramName] = null
                            }
                        }
                    }
                }
            }

            // Map the values to constructor parameters
            val parameters = constructor.parameters.associateWith { param ->
                val paramName = param.name ?: ""
                paramValues[paramName]
            }

            val instance = constructor.callBy(parameters)
            results.add(instance)
        } catch (e: Exception) {
            // Enhanced error logging
            println("Error mapping ${kClass.simpleName}: ${e.message}")
            println("Failed to map child with key: ${childSnap.key}")
            e.printStackTrace()
        }
    }

    return results
}
