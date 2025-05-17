package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.Module.FireBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.TypeTarificationEnum
import kotlin.reflect.full.memberProperties

fun mapToFirebaseFormat(dataBasesInfosSql: DataBasesInfosSql): Map<String, Any> {
    val data = mutableMapOf<String, Any>()

    val productsMap = mutableMapOf<String, Any>()
    dataBasesInfosSql.a_ProduitInfos.forEach { produit ->
        try {
            val produitMap = produit::class.memberProperties.associate {
                it.name to (it.getter.call(produit) ?: "null")
            }
            // Use keyFireBase as the key if it's valid, otherwise fallback to default format
            val key = if (produit.keyFireBase.isNotBlank() && !produit.keyFireBase.startsWith("-<")) {
                produit.keyFireBase
            } else {
                "prod_${produit.id}"
            }
            productsMap[key] = produitMap
        } catch (e: Exception) {}
    }
    if (productsMap.isNotEmpty()) {
        data[dataBasesInfosSql.refFireBaseA_ProduitInfos] = productsMap
    }

    val clientsMap = mutableMapOf<String, Any>()
    dataBasesInfosSql.b_ClientInfos.forEach { client ->
        try {
            val clientMap = client::class.memberProperties.associate {
                it.name to (it.getter.call(client) ?: "null")
            }
            // Use keyFireBase as the key if it's valid, otherwise fallback to default format
            val key = if (client.keyFireBase.isNotBlank() && !client.keyFireBase.startsWith("-<")) {
                client.keyFireBase
            } else {
                "client_${client.id}"
            }
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
            // Use keyFireBase as the key if it's valid, otherwise fallback to default format
            val key = if (typeTarif.keyFireBase.isNotBlank() && !typeTarif.keyFireBase.startsWith("-<")) {
                typeTarif.keyFireBase
            } else {
                "type_${typeTarif.id}"
            }
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
            // Use keyFireBase as the key if it's valid, otherwise fallback to default format
            val key = if (tarif.keyFireBase.isNotBlank() && !tarif.keyFireBase.startsWith("-<")) {
                tarif.keyFireBase
            } else {
                "tarif_${tarif.vidTimestamp}"
            }
            tarifsMap[key] = tarifMap
        } catch (e: Exception) {}
    }
    if (tarifsMap.isNotEmpty()) {
        data[dataBasesInfosSql.refFireBaseD_TarificationInfos] = tarifsMap
    }

    return data
}
