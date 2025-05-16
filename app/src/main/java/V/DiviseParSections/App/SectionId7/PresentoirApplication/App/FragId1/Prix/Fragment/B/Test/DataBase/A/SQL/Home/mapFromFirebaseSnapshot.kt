package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.TypeTarificationEnum
import android.util.Log
import com.google.firebase.database.DataSnapshot
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

// Improved tag for logging
private const val TAG = "FirebaseMapping"

fun mapFromFirebaseSnapshot(snapshot: DataSnapshot): DataBasesInfosSql {
    val products = mutableListOf<A_ProduitInfos>()
    val clients = mutableListOf<B_ClientInfos>()
    val typeTarifications = mutableListOf<C_TypeTarificationInfos>()
    val tarifications = mutableListOf<D_TarificationInfos>()

    // Create a default model to get reference names
    val defaultModel = DataBasesInfosSql()

    // Map products using reflection
    val productsSnapshot = snapshot.child(defaultModel.refFireBaseA_ProduitInfos)
    if (productsSnapshot.exists()) {
        products.addAll(mapSnapshotToObjects(productsSnapshot, A_ProduitInfos::class))
    }

    // Map clients using reflection
    val clientsSnapshot = snapshot.child(defaultModel.refFireBaseB_ClientInfos)
    if (clientsSnapshot.exists()) {
        clients.addAll(mapSnapshotToObjects(clientsSnapshot, B_ClientInfos::class))
    }

    // Type tarifications now use reflection with special handling for the enum
    val typeTarifsSnapshot = snapshot.child(defaultModel.refFireBaseC_TypeTarificationInfos)
    if (typeTarifsSnapshot.exists()) {
        typeTarifications.addAll(mapTypeTarificationsWithReflection(typeTarifsSnapshot))
    }

    // Map tarifications using reflection
    val tarifsSnapshot = snapshot.child(defaultModel.refFireBaseD_TarificationInfos)
    if (tarifsSnapshot.exists()) {
        tarifications.addAll(mapSnapshotToObjects(tarifsSnapshot, D_TarificationInfos::class))
    }

    // Log the mapping results for debugging
    Log.d(TAG, "Firebase mapping complete - Products: ${products.size}, Clients: ${clients.size}, " +
            "TypeTarifications: ${typeTarifications.size}, Tarifications: ${tarifications.size}")

    // Log products for verification
    products.forEach { product ->
        Log.d(TAG, "Mapped product: ID=${product.id}, Name=${product.nom}, NeedUpdate=${product.needUpdate}")
    }

    return DataBasesInfosSql(
        a_ProduitInfos = products,
        b_ClientInfos = clients,
        c_TypeTarificationInfos = typeTarifications,
        d_TarificationInfos = tarifications
    )
}

// Specialized function for mapping TypeTarificationInfos with enum handling using reflection
private fun mapTypeTarificationsWithReflection(snapshot: DataSnapshot): List<C_TypeTarificationInfos> {
    val results = mutableListOf<C_TypeTarificationInfos>()

    for (childSnap in snapshot.children) {
        try {
            val constructor = C_TypeTarificationInfos::class.primaryConstructor
                ?: throw Exception("No primary constructor found for C_TypeTarificationInfos")

            // Extract all parameters needed for the constructor
            val id = childSnap.child("id").getValue(Long::class.java) ?: 0L
            val needUpdate = childSnap.child("needUpdate").getValue(Boolean::class.java) ?: false

            // Handle enum using reflection
            val typeTarifString = childSnap.child("typeTarificationEnum").getValue(String::class.java) ?: "ParBenifice"
            val typeTarifEnum = try {
                java.lang.Enum.valueOf(TypeTarificationEnum::class.java, typeTarifString)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to parse enum value '$typeTarifString', using default. Error: ${e.message}")
                TypeTarificationEnum.ParBenifice
            }

            // Create the instance using constructor parameters
            val instance = C_TypeTarificationInfos(
                id = id,
                typeTarificationEnum = typeTarifEnum,
                needUpdate = needUpdate
            )

            results.add(instance)
            Log.d(TAG, "Successfully mapped TypeTarification: id=$id, enum=$typeTarifEnum")
        } catch (e: Exception) {
            Log.e(TAG, "Error mapping TypeTarification ${childSnap.key}: ${e.message}", e)
        }
    }

    return results
}

private inline fun <reified T : Any> mapSnapshotToObjects(snapshot: DataSnapshot, kClass: KClass<T>): List<T> {
    val results = mutableListOf<T>()

    for (childSnap in snapshot.children) {
        try {
            // Create a new instance using the primary constructor
            val constructor = kClass.constructors.firstOrNull()
                ?: throw Exception("No constructor found for ${kClass.simpleName}")

            // Prepare parameters for constructor
            val parameters = constructor.parameters.associateWith { param ->
                val paramName = param.name ?: ""
                val childValue = childSnap.child(paramName)

                when {
                    !childValue.exists() -> null
                    param.type.classifier == Long::class -> childValue.getValue(Long::class.java)
                    param.type.classifier == String::class -> childValue.getValue(String::class.java)
                    param.type.classifier == Boolean::class -> childValue.getValue(Boolean::class.java)
                    param.type.classifier == Double::class -> childValue.getValue(Double::class.java)
                    param.type.classifier == Int::class -> childValue.getValue(Int::class.java)?.toLong()
                    else -> null
                }
            }

            // Create instance with parameters
            val instance = constructor.callBy(parameters)
            results.add(instance)

            // Log for debugging
            Log.d(TAG, "Successfully mapped ${kClass.simpleName}: ${childSnap.key}")
        } catch (e: Exception) {
            Log.e(TAG, "Error mapping ${kClass.simpleName} ${childSnap.key}: ${e.message}", e)
        }
    }

    return results
}
