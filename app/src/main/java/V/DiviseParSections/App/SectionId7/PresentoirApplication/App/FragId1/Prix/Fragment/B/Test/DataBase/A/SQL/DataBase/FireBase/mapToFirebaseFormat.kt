package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.DataBase.FireBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.TypeTarificationEnum
import kotlin.reflect.full.memberProperties

fun mapToFirebaseFormat(dataBasesInfosSql: DataBasesInfosSql): Map<String, Any> {
    val data = mutableMapOf<String, Any>()

    val productsMap = mutableMapOf<String, Any>()
    dataBasesInfosSql.a_ProduitInfos.forEach { produit ->
        try {
            val produitMap = produit::class.memberProperties.associate {
                it.name to (it.getter.call(produit) ?: "null")
            }
            productsMap["prod_${produit.id}"] = produitMap
        } catch (e: Exception) {}
    }
    if (productsMap.isNotEmpty()) {
        // Using reference from DataBasesInfosSql instead of hardcoded "produits"
        data[dataBasesInfosSql.refFireBaseA_ProduitInfos] = productsMap
    }

    val clientsMap = mutableMapOf<String, Any>()
    dataBasesInfosSql.b_ClientInfos.forEach { client ->
        try {
            val clientMap = client::class.memberProperties.associate {
                it.name to (it.getter.call(client) ?: "null")
            }
            clientsMap["client_${client.id}"] = clientMap
        } catch (e: Exception) {}
    }
    if (clientsMap.isNotEmpty()) {
        // Using reference from DataBasesInfosSql instead of hardcoded "clients"
        data[dataBasesInfosSql.refFireBaseB_ClientInfos] = clientsMap
    }

    val typeTarifMap = mutableMapOf<String, Any>()
    dataBasesInfosSql.c_TypeTarificationInfos.forEach { typeTarif ->
        try {
            val typeMap = mutableMapOf<String, Any>()
            typeTarif::class.memberProperties.forEach { prop ->
                val value = prop.getter.call(typeTarif)
                if (value is TypeTarificationEnum) {
                    typeMap[prop.name] = value.name
                } else if (value != null) {
                    typeMap[prop.name] = value as Any
                } else {
                    typeMap[prop.name] = "null"
                }
            }
            typeTarifMap["type_${typeTarif.id}"] = typeMap
        } catch (e: Exception) {}
    }
    if (typeTarifMap.isNotEmpty()) {
        // Using reference from DataBasesInfosSql instead of hardcoded "typeTarifications"
        data[dataBasesInfosSql.refFireBaseC_TypeTarificationInfos] = typeTarifMap
    }

    val tarifsMap = mutableMapOf<String, Any>()
    dataBasesInfosSql.d_TarificationInfos.forEach { tarif ->
        try {
            val tarifMap = tarif::class.memberProperties.associate {
                it.name to (it.getter.call(tarif) ?: "null")
            }
            tarifsMap["tarif_${tarif.vidTimestamp}"] = tarifMap
        } catch (e: Exception) {}
    }
    if (tarifsMap.isNotEmpty()) {
        // Using reference from DataBasesInfosSql instead of hardcoded "tarifications"
        data[dataBasesInfosSql.refFireBaseD_TarificationInfos] = tarifsMap
    }

    return data
}
