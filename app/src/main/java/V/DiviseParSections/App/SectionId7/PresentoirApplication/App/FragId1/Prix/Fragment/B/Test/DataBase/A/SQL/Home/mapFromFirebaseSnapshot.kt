package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.TypeTarificationEnum
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

            val instance = C_TypeTarificationInfos(
                id = id,
                typeTarificationEnum = typeTarifEnum,
                needUpdate = needUpdate
            )

            results.add(instance)
        } catch (e: Exception) {
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

            val instance = constructor.callBy(parameters)
            results.add(instance)
        } catch (e: Exception) {
        }
    }

    return results
}
