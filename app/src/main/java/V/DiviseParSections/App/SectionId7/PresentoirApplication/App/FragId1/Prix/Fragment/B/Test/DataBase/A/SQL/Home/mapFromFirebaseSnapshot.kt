package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.TypeTarificationEnum
import android.util.Log
import com.google.firebase.database.DataSnapshot

fun mapFromFirebaseSnapshot(snapshot: DataSnapshot): DataBasesInfosSql {
    val products = mutableListOf<A_ProduitInfos>()
    val clients = mutableListOf<B_ClientInfos>()
    val typeTarifications = mutableListOf<C_TypeTarificationInfos>()
    val tarifications = mutableListOf<D_TarificationInfos>()

    // Create a default model to get reference names
    val defaultModel = DataBasesInfosSql()

    // Debug tag
    val TAG = "FirebaseMapping"

    val productsSnapshot = snapshot.child(defaultModel.refFireBaseA_ProduitInfos)
    if (productsSnapshot.exists()) {
        for (productSnap in productsSnapshot.children) {
            try {
                // Log the product data for debugging
                Log.d(TAG, "Processing product: ${productSnap.key}, data: ${productSnap.value}")

                // Extract values directly from the snapshot
                val id = productSnap.child("id").getValue(Long::class.java) ?: 0L
                val nom = productSnap.child("nom").getValue(String::class.java) ?: ""
                val needUpdate = productSnap.child("needUpdate").getValue(Boolean::class.java) ?: false

                // Create product instance with extracted values
                val product = A_ProduitInfos(
                    id = id,
                    nom = nom,
                    needUpdate = needUpdate
                )

                // Add the product to the list
                products.add(product)
                Log.d(TAG, "Added product: $id, $nom, $needUpdate")
            } catch (e: Exception) {
                Log.e(TAG, "Error processing product ${productSnap.key}: ${e.message}", e)
            }
        }
    }

    val clientsSnapshot = snapshot.child(defaultModel.refFireBaseB_ClientInfos)
    if (clientsSnapshot.exists()) {
        for (clientSnap in clientsSnapshot.children) {
            try {
                // Extract values directly
                val id = clientSnap.child("id").getValue(Long::class.java) ?: 0L
                val nom = clientSnap.child("nom").getValue(String::class.java) ?: "Non Difinie"
                val idActiveTypeTarificationDataBase = clientSnap.child("idActiveTypeTarificationDataBase").getValue(Long::class.java) ?: 0L
                val needUpdate = clientSnap.child("needUpdate").getValue(Boolean::class.java) ?: false

                // Create client instance
                val client = B_ClientInfos(
                    id = id,
                    nom = nom,
                    idActiveTypeTarificationDataBase = idActiveTypeTarificationDataBase,
                    needUpdate = needUpdate
                )

                clients.add(client)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing client ${clientSnap.key}: ${e.message}", e)
            }
        }
    }

    val typeTarifsSnapshot = snapshot.child(defaultModel.refFireBaseC_TypeTarificationInfos)
    if (typeTarifsSnapshot.exists()) {
        for (typeSnap in typeTarifsSnapshot.children) {
            try {
                // Extract values directly
                val id = typeSnap.child("id").getValue(Long::class.java) ?: 0L
                val typeTarifString = typeSnap.child("typeTarificationEnum").getValue(String::class.java) ?: "ParBenifice"
                val needUpdate = typeSnap.child("needUpdate").getValue(Boolean::class.java) ?: false

                // Convert string to enum
                val typeTarifEnum = try {
                    TypeTarificationEnum.valueOf(typeTarifString)
                } catch (e: Exception) {
                    TypeTarificationEnum.ParBenifice
                }

                // Create type tarification instance
                val typeTarif = C_TypeTarificationInfos(
                    id = id,
                    typeTarificationEnum = typeTarifEnum,
                    needUpdate = needUpdate
                )

                typeTarifications.add(typeTarif)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing type tarification ${typeSnap.key}: ${e.message}", e)
            }
        }
    }

    val tarifsSnapshot = snapshot.child(defaultModel.refFireBaseD_TarificationInfos)
    if (tarifsSnapshot.exists()) {
        for (tarifSnap in tarifsSnapshot.children) {
            try {
                // Extract values directly
                val vidTimestamp = tarifSnap.child("vidTimestamp").getValue(Long::class.java) ?: 0L
                val idProduit = tarifSnap.child("idProduit").getValue(Long::class.java) ?: 0L
                val idClient = tarifSnap.child("idClient").getValue(Long::class.java) ?: 0L
                val idTypeTarification = tarifSnap.child("idTypeTarification").getValue(Long::class.java) ?: 0L
                val prixCurrency = tarifSnap.child("prixCurrency").getValue(Double::class.java) ?: 0.0
                val needUpdate = tarifSnap.child("needUpdate").getValue(Boolean::class.java) ?: false

                // Create tarification instance
                val tarif = D_TarificationInfos(
                    vidTimestamp = vidTimestamp,
                    idProduit = idProduit,
                    idClient = idClient,
                    idTypeTarification = idTypeTarification,
                    prixCurrency = prixCurrency,
                    needUpdate = needUpdate
                )

                tarifications.add(tarif)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing tarification ${tarifSnap.key}: ${e.message}", e)
            }
        }
    }

    return DataBasesInfosSql(
        a_ProduitInfos = products,
        b_ClientInfos = clients,
        c_TypeTarificationInfos = typeTarifications,
        d_TarificationInfos = tarifications
    )
}
