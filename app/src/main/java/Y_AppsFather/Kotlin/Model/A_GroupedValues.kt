package Y_AppsFather.Kotlin.Model

import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations

val _ModelAppsFather.groupedProductsPatGrossist: List<Pair<GrossistInformations, List<ProduitModel>>>
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

val _ModelAppsFather.groupedProductsParClients: List<Pair<ProduitModel.ClientBonVentModel.ClientInformations, List<ProduitModel>>>
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
