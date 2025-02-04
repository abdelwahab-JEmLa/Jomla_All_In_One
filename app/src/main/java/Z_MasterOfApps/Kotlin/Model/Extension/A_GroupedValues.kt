package Z_MasterOfApps.Kotlin.Model.Extension

import Z_MasterOfApps.Kotlin.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.Model.C_GrossistsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather


val _ModelAppsFather.groupedProductsParGrossist: List<Map.Entry<C_GrossistsDataBase, List<_ModelAppsFather.ProduitModel>>>
    get() = grossistsDataBase.map { grossist ->
        // Find all products for this grossist
        val matchingProducts = produitsMainDataBase.filter { product ->
            product.bonCommendDeCetteCota
                ?.idGrossistChoisi == grossist.id
        }

        // Create and return a proper pair
        java.util.AbstractMap.SimpleEntry(grossist, matchingProducts)
    }.sortedBy { entry ->
        entry.key.statueDeBase.itPositionInParentList
    }


val _ModelAppsFather.groupedProductsParClients: List<Map.Entry<B_ClientsDataBase, List<_ModelAppsFather.ProduitModel>>>
    get() = clientDataBase.map { client ->
        // Get all products where this client has associated bon vents
        val matchingProducts = produitsMainDataBase.filter { product ->
            // Check current bon vents
            product.bonsVentDeCetteCota.any { bonVent ->
                bonVent.clientIdChoisi== client.id
            }
        }

        // Create a map entry using AbstractMap.SimpleEntry
        java.util.AbstractMap.SimpleEntry(client, matchingProducts)
    }.sortedBy { entry ->
        entry.key.statueDeBase.positionDonClientsList
    }
