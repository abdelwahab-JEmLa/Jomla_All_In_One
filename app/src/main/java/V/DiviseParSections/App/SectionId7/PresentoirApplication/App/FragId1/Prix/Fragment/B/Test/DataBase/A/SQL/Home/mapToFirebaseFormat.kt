package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.TypeTarificationEnum
import android.util.Log
import kotlin.reflect.full.memberProperties

fun FireBaseHandler.mapToFirebaseFormat(dataBasesInfosSql: DataBasesInfosSql): Map<String, Any> {
        Log.d(TAG, "Starting mapping data to Firebase format")

        val data = mutableMapOf<String, Any>()

        // Map products
        val productsMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.a_ProduitInfos.forEach { produit ->
            try {
                // Use reflection to get all properties dynamically
                val produitMap = produit::class.memberProperties.associate {
                    it.name to (it.getter.call(produit) ?: "null")
                }
                productsMap["prod_${produit.id}"] = produitMap
            } catch (e: Exception) {
                Log.e(TAG, "Error mapping product ${produit.id}: ${e.message}", e)
            }
        }
        if (productsMap.isNotEmpty()) {
            data["produits"] = productsMap
            Log.d(TAG, "Mapped ${productsMap.size} products")
        } else {
            Log.w(TAG, "No products were mapped")
        }

        // Map clients
        val clientsMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.b_ClientInfos.forEach { client ->
            try {
                val clientMap = client::class.memberProperties.associate {
                    it.name to (it.getter.call(client) ?: "null")
                }
                clientsMap["client_${client.id}"] = clientMap
            } catch (e: Exception) {
                Log.e(TAG, "Error mapping client ${client.id}: ${e.message}", e)
            }
        }
        if (clientsMap.isNotEmpty()) {
            data["clients"] = clientsMap
            Log.d(TAG, "Mapped ${clientsMap.size} clients")
        } else {
            Log.w(TAG, "No clients were mapped")
        }

        // Map type tarifications
        val typeTarifMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.c_TypeTarificationInfos.forEach { typeTarif ->
            try {
                val typeMap = mutableMapOf<String, Any>()
                typeTarif::class.memberProperties.forEach { prop ->
                    val value = prop.getter.call(typeTarif)
                    // Handle enum specially
                    if (value is TypeTarificationEnum) {
                        typeMap[prop.name] = value.name
                    } else if (value != null) {
                        typeMap[prop.name] = value as Any
                    } else {
                        typeMap[prop.name] = "null"
                    }
                }
                typeTarifMap["type_${typeTarif.id}"] = typeMap
            } catch (e: Exception) {
                Log.e(TAG, "Error mapping type tarification ${typeTarif.id}: ${e.message}", e)
            }
        }
        if (typeTarifMap.isNotEmpty()) {
            data["typeTarifications"] = typeTarifMap
            Log.d(TAG, "Mapped ${typeTarifMap.size} type tarifications")
        } else {
            Log.w(TAG, "No type tarifications were mapped")
        }

        // Map tarifications
        val tarifsMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.d_TarificationInfos.forEach { tarif ->
            try {
                val tarifMap = tarif::class.memberProperties.associate {
                    it.name to (it.getter.call(tarif) ?: "null")
                }
                tarifsMap["tarif_${tarif.vidTimestamp}"] = tarifMap
            } catch (e: Exception) {
                Log.e(TAG, "Error mapping tarification ${tarif.vidTimestamp}: ${e.message}", e)
            }
        }
        if (tarifsMap.isNotEmpty()) {
            data["tarifications"] = tarifsMap
            Log.d(TAG, "Mapped ${tarifsMap.size} tarifications")
        } else {
            Log.w(TAG, "No tarifications were mapped")
        }

        Log.d(TAG, "Completed mapping data to Firebase format, total collections: ${data.size}")
        return data
    }
