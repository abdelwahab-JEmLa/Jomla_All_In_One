package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.TypeTarificationEnum
import android.util.Log
import com.google.firebase.database.DataSnapshot

fun FireBaseHandler.mapFromFirebaseSnapshot(snapshot: DataSnapshot): DataBasesInfosSql {
        Log.d(TAG, "Starting mapping data from Firebase snapshot")

        val products = mutableListOf<A_ProduitInfos>()
        val clients = mutableListOf<B_ClientInfos>()
        val typeTarifications = mutableListOf<C_TypeTarificationInfos>()
        val tarifications = mutableListOf<D_TarificationInfos>()

        val productsSnapshot = snapshot.child("produits")
        if (productsSnapshot.exists()) {
            Log.d(TAG, "Found products collection with ${productsSnapshot.childrenCount} items")
            for (productSnap in productsSnapshot.children) {
                try {
                    val product = A_ProduitInfos::class.java.getDeclaredConstructor().newInstance()

                    // Set each field dynamically
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
                    Log.d(TAG, "Mapped product ID: ${product.id}, Name: ${product.nom}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing product data: ${e.message}", e)
                }
            }
        } else {
            Log.w(TAG, "Products collection does not exist in Firebase")
        }

        val clientsSnapshot = snapshot.child("clients")
        if (clientsSnapshot.exists()) {
            Log.d(TAG, "Found clients collection with ${clientsSnapshot.childrenCount} items")
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
                    Log.d(TAG, "Mapped client ID: ${client.id}, Name: ${client.nom}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing client data: ${e.message}", e)
                }
            }
        } else {
            Log.w(TAG, "Clients collection does not exist in Firebase")
        }

        val typeTarifsSnapshot = snapshot.child("typeTarifications")
        if (typeTarifsSnapshot.exists()) {
            Log.d(TAG, "Found typeTarifications collection with ${typeTarifsSnapshot.childrenCount} items")
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
                                        Log.w(TAG, "Invalid TypeTarificationEnum value: $typeTarifString, using default")
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
                    Log.d(TAG, "Mapped typeTarification ID: ${typeTarif.id}, Type: ${typeTarif.typeTarificationEnum}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing type tarification data: ${e.message}", e)
                }
            }
        } else {
            Log.w(TAG, "TypeTarifications collection does not exist in Firebase")
        }

        val tarifsSnapshot = snapshot.child("tarifications")
        if (tarifsSnapshot.exists()) {
            Log.d(TAG, "Found tarifications collection with ${tarifsSnapshot.childrenCount} items")
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
                    Log.d(TAG, "Mapped tarification ID: ${tarif.vidTimestamp}, Price: ${tarif.prixCurrency}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing tarification data: ${e.message}", e)
                }
            }
        } else {
            Log.w(TAG, "Tarifications collection does not exist in Firebase")
        }

        val result = DataBasesInfosSql(
            a_ProduitInfos = products,
            b_ClientInfos = clients,
            c_TypeTarificationInfos = typeTarifications,
            d_TarificationInfos = tarifications
        )

        Log.d(TAG, "Completed mapping from Firebase snapshot: " +
                "Products: ${result.a_ProduitInfos.size}, " +
                "Clients: ${result.b_ClientInfos.size}, " +
                "TypeTarifs: ${result.c_TypeTarificationInfos.size}, " +
                "Tarifications: ${result.d_TarificationInfos.size}")

        return result
    }
