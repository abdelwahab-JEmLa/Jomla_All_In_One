package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.Module.FireBase

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.TypeTarificationEnum
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
        tarifications.addAll(mapSnapshotToObjects(tarifsSnapshot, D_TarificationInfos::class))
    }

    return DataBasesInfosSql(
        a_ProduitInfos = products,
        b_ClientInfos = clients,
        c_TypeTarificationInfos = typeTarifications,
        d_TarificationInfos = tarifications
    )
}

private fun mapTypeTarificationsWithReflection(snapshot: DataSnapshot): List<C_TypeTarificationInfos> {
    val results = mutableListOf<C_TypeTarificationInfos>()

    for (childSnap in snapshot.children) {
        try {
            val id = childSnap.child("id").getValue(Long::class.java) ?: 0L
            val needUpdate = childSnap.child("needUpdate").getValue(Boolean::class.java) ?: false

            val typeTarifString = childSnap.child("typeTarificationEnum").getValue(String::class.java) ?: "ParBenifice"
            val typeTarifEnum = try {
                java.lang.Enum.valueOf(TypeTarificationEnum::class.java, typeTarifString)
            } catch (e: Exception) {
                TypeTarificationEnum.ParBenifice
            }

            // Use Firebase key as keyFireBase property
            val keyFireBase = childSnap.key ?: ""

            val instance = C_TypeTarificationInfos(
                id = id,
                entityCorrespond = typeTarifEnum,
                needUpdate = needUpdate,
                keyFireBase = keyFireBase
            )

            results.add(instance)
        } catch (e: Exception) {
            // Consider logging the exception here
        }
    }

    return results
}

private inline fun <reified T : Any> mapSnapshotToObjects(snapshot: DataSnapshot, kClass: KClass<T>): List<T> {
    val results = mutableListOf<T>()

    for (childSnap in snapshot.children) {
        try {
            val constructor = kClass.constructors.firstOrNull()
                ?: throw Exception("No constructor found for ${kClass.simpleName}")

            // Create a map to hold parameter values
            val paramValues = mutableMapOf<String, Any?>()

            // First pass: collect all values from the snapshot
            for (param in constructor.parameters) {
                val paramName = param.name ?: continue

                if (paramName == "keyFireBase") {
                    // For keyFireBase, use the Firebase key directly
                    paramValues[paramName] = childSnap.key
                } else {
                    val childValue = childSnap.child(paramName)
                    if (childValue.exists()) {
                        when (param.type.classifier) {
                            Long::class -> paramValues[paramName] = childValue.getValue(Long::class.java)
                            String::class -> paramValues[paramName] = childValue.getValue(String::class.java)
                            Boolean::class -> paramValues[paramName] = childValue.getValue(Boolean::class.java)
                            Double::class -> paramValues[paramName] = childValue.getValue(Double::class.java)
                            Int::class -> paramValues[paramName] = childValue.getValue(Int::class.java)?.toLong()
                            else -> paramValues[paramName] = null
                        }
                    }
                }
            }

            // Second pass: map the values to constructor parameters
            val parameters = constructor.parameters.associateWith { param ->
                val paramName = param.name ?: ""
                paramValues[paramName]
            }

            val instance = constructor.callBy(parameters)
            results.add(instance)
        } catch (e: Exception) {
            // Consider logging the exception here
        }
    }

    return results
}
