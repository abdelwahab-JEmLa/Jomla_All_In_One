package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.TypeTarificationEnum
import com.google.firebase.database.DataSnapshot

fun mapFromFirebaseSnapshot(snapshot: DataSnapshot): DataBasesInfosSql {
    val products = mutableListOf<A_ProduitInfos>()
    val clients = mutableListOf<B_ClientInfos>()
    val typeTarifications = mutableListOf<C_TypeTarificationInfos>()
    val tarifications = mutableListOf<D_TarificationInfos>()

    val productsSnapshot = snapshot.child("produits")
    if (productsSnapshot.exists()) {
        for (productSnap in productsSnapshot.children) {
            try {
                val product = A_ProduitInfos::class.java.getDeclaredConstructor().newInstance()
                for (field in A_ProduitInfos::class.java.declaredFields) {
                    field.isAccessible = true
                    val childSnapshot = productSnap.child(field.name)
                    if (childSnapshot.exists()) {
                        val value = when (field.type) {
                            Long::class.java -> childSnapshot.getValue(Long::class.java)
                            String::class.java -> childSnapshot.getValue(String::class.java)
                            Boolean::class.java -> childSnapshot.getValue(Boolean::class.java)
                            else -> null
                        }
                        if (value != null) {
                            field.set(product, value)
                        }
                    }
                }
                products.add(product)
            } catch (e: Exception) {}
        }
    }

    val clientsSnapshot = snapshot.child("clients")
    if (clientsSnapshot.exists()) {
        for (clientSnap in clientsSnapshot.children) {
            try {
                val client = B_ClientInfos::class.java.getDeclaredConstructor().newInstance()
                for (field in B_ClientInfos::class.java.declaredFields) {
                    field.isAccessible = true
                    val childSnapshot = clientSnap.child(field.name)
                    if (childSnapshot.exists()) {
                        val value = when (field.type) {
                            Long::class.java -> childSnapshot.getValue(Long::class.java)
                            String::class.java -> childSnapshot.getValue(String::class.java)
                            Boolean::class.java -> childSnapshot.getValue(Boolean::class.java)
                            else -> null
                        }
                        if (value != null) {
                            field.set(client, value)
                        }
                    }
                }
                clients.add(client)
            } catch (e: Exception) {}
        }
    }

    val typeTarifsSnapshot = snapshot.child("typeTarifications")
    if (typeTarifsSnapshot.exists()) {
        for (typeSnap in typeTarifsSnapshot.children) {
            try {
                val typeTarif = C_TypeTarificationInfos::class.java.getDeclaredConstructor().newInstance()
                for (field in C_TypeTarificationInfos::class.java.declaredFields) {
                    field.isAccessible = true
                    val childSnapshot = typeSnap.child(field.name)
                    if (childSnapshot.exists()) {
                        val value = when (field.type) {
                            Long::class.java -> childSnapshot.getValue(Long::class.java)
                            String::class.java -> childSnapshot.getValue(String::class.java)
                            Boolean::class.java -> childSnapshot.getValue(Boolean::class.java)
                            TypeTarificationEnum::class.java -> {
                                val typeTarifString = childSnapshot.getValue(String::class.java) ?: "ParBenifice"
                                try {
                                    TypeTarificationEnum.valueOf(typeTarifString)
                                } catch (e: Exception) {
                                    TypeTarificationEnum.ParBenifice
                                }
                            }
                            else -> null
                        }
                        if (value != null) {
                            field.set(typeTarif, value)
                        }
                    }
                }
                typeTarifications.add(typeTarif)
            } catch (e: Exception) {}
        }
    }

    val tarifsSnapshot = snapshot.child("tarifications")
    if (tarifsSnapshot.exists()) {
        for (tarifSnap in tarifsSnapshot.children) {
            try {
                val tarif = D_TarificationInfos::class.java.getDeclaredConstructor().newInstance()
                for (field in D_TarificationInfos::class.java.declaredFields) {
                    field.isAccessible = true
                    val childSnapshot = tarifSnap.child(field.name)
                    if (childSnapshot.exists()) {
                        val value = when (field.type) {
                            Long::class.java -> childSnapshot.getValue(Long::class.java)
                            Double::class.java -> childSnapshot.getValue(Double::class.java)
                            Boolean::class.java -> childSnapshot.getValue(Boolean::class.java)
                            else -> null
                        }
                        if (value != null) {
                            field.set(tarif, value)
                        }
                    }
                }
                tarifications.add(tarif)
            } catch (e: Exception) {}
        }
    }

    return DataBasesInfosSql(
        a_ProduitInfos = products,
        b_ClientInfos = clients,
        c_TypeTarificationInfos = typeTarifications,
        d_TarificationInfos = tarifications
    )
}
