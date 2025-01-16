package Z_MasterOfApps.Kotlin.Model.Extension

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather


val _ModelAppsFather.groupedProductsPatGrossist: List<Pair<_ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations, List<_ModelAppsFather.ProduitModel>>>
    get() = produitsMainDataBase
        .mapNotNull { product ->
            product.bonCommendDeCetteCota?.grossistInformations?.let { grossistInfo ->
                grossistInfo to product
            }
        }
        .groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        )
        .toList()
        .sortedBy { (grossist, _) ->
            grossist.positionInGrossistsList
        }

val _ModelAppsFather.groupedProductsParClients: List<Pair<_ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations, List<_ModelAppsFather.ProduitModel>>>
    get() = produitsMainDataBase
        .asSequence()
        .filter { product ->
            product.bonsVentDeCetteCota.isNotEmpty() &&
                    product.bonsVentDeCetteCota.any { it.clientInformations != null }
        }
        .flatMap { product ->
            product.bonsVentDeCetteCota.mapNotNull { bonVent ->
                bonVent.clientInformations?.let { clientInfo ->
                    clientInfo to product
                }
            }
        }
        .groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        )
        .toList()
        .sortedBy { (client, _) ->
            client.positionDonClientsList
        }
        .toList()
