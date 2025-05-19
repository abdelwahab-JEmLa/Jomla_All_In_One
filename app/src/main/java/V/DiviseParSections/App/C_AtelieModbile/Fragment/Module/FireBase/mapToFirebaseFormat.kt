// Updated mapToFirebaseFormat.kt
package V.DiviseParSections.App.C_AtelieModbile.Fragment.Module.FireBase
import V.DiviseParSections.App.C_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.C_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.TypeTarificationEnum
import V.DiviseParSections.App.C_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.getKeyFireBase
import kotlin.reflect.full.memberProperties

fun mapToFirebaseFormat(dataBasesInfosSql: DataBasesInfosSql): Map<String, Any> {
    val data = mutableMapOf<String, Any>()
    val productsMap = mutableMapOf<String, Any>()
    dataBasesInfosSql.a_ProduitInfos.forEach { produit ->
        try {
            val produitMap = produit::class.memberProperties.associate {
                it.name to (it.getter.call(produit) ?: "null")
            }
            // Always use keyFireBase as the key, or generate one if empty
            val key = produit.keyFireBase.takeIf { it.isNotEmpty() }
                ?: getKeyFireBase(produit.id, produit.nom)
            productsMap[key] = produitMap
        } catch (e: Exception) {}
    }
    if (productsMap.isNotEmpty()) {
        data[dataBasesInfosSql.refFireBaseA_ProduitInfos] = productsMap
    }

    val clientsMap = mutableMapOf<String, Any>()
    dataBasesInfosSql.b_ClientInfosList.forEach { client ->
        try {
            val clientMap = client::class.memberProperties.associate {
                it.name to (it.getter.call(client) ?: "null")
            }
            // Always use keyFireBase as the key, or generate one if empty
            val key = client.keyFireBase.takeIf { it.isNotEmpty() }
                ?: getKeyFireBase(client.id, client.nom)
            clientsMap[key] = clientMap
        } catch (e: Exception) {}
    }
    if (clientsMap.isNotEmpty()) {
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
            // Always use keyFireBase as the key, or generate one if empty
            val key = typeTarif.keyFireBase.takeIf { it.isNotEmpty() }
                ?: getKeyFireBase(typeTarif.id, typeTarif.nom)
            typeTarifMap[key] = typeMap
        } catch (e: Exception) {}
    }
    if (typeTarifMap.isNotEmpty()) {
        data[dataBasesInfosSql.refFireBaseC_TypeTarificationInfos] = typeTarifMap
    }

    val tarifsMap = mutableMapOf<String, Any>()
    dataBasesInfosSql.d_TarificationInfos.forEach { tarif ->
        try {
            val tarifMap = tarif::class.memberProperties.associate {
                it.name to (it.getter.call(tarif) ?: "null")
            }
            // Always use keyFireBase as the key, or generate one if empty
            val key = tarif.keyFireBase.takeIf { it.isNotEmpty() }
                ?: getKeyFireBase(tarif.vidTimestamp, tarif.nom)
            tarifsMap[key] = tarifMap
        } catch (e: Exception) {}
    }
    if (tarifsMap.isNotEmpty()) {
        data[dataBasesInfosSql.refFireBaseD_TarificationInfos] = tarifsMap
    }

    return data
}
