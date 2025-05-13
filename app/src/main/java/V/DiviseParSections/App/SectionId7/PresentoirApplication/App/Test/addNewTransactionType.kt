package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

// Clean version without logging
fun addNewTransactionType(produits: List<Produit>): Produit? {
    // Get the first active product
    val existingProduct = produits
        .find { it.cesStatuesMutable.cActiveDonsSonListParent }

    if (existingProduct == null) {
        return null
    }

    // Get the first active client
    val existingClient = existingProduct.clients
        .find { it.cesStatuesMutable.cActiveDonsSonListParent }

    if (existingClient == null) {
        return null
    }

    // Set a fixed ID for the new tarification type
    val typeId = 3L

    // Check if this type already exists
    val existingType = existingClient.typesTarification.find { it.id == typeId }

    // Calculate the next price ID to prevent duplicates
    val maxPrixId = existingClient.typesTarification
        .flatMap { it.PrixsCurrency }
        .maxOfOrNull { it.id }
        ?: 0L
    val newPrixId = maxPrixId + 1

    // Create a new prix
    val currentTimestamp = System.currentTimeMillis()
    val newPrix = Produit.Client.TypeTarification.Prix(
        id = newPrixId,
        timestamp = currentTimestamp,
        valeur = 500.0
    )

    // Update or create the tarification type
    val updatedTypesTarification = if (existingType != null) {
        existingClient.typesTarification.map { typeTarif ->
            if (typeTarif.id == typeId) {
                // Check if this price already exists to prevent duplicates
                val priceExists = typeTarif.PrixsCurrency.any { it.id == newPrixId }
                if (priceExists) {
                    typeTarif
                } else {
                    typeTarif.copy(
                        timestamp = currentTimestamp,
                        PrixsCurrency = typeTarif.PrixsCurrency + newPrix
                    )
                }
            } else {
                typeTarif
            }
        }
    } else {
        existingClient.typesTarification + Produit.Client.TypeTarification(
            id = typeId,
            timestamp = currentTimestamp,
            infos = Produit.Client.TypeTarification.Infos(
                type = Produit.Client.TypeTarification.TypeTarificationEnum.LeMaxPrixArrive
            ),
            PrixsCurrency = listOf(newPrix)
        )
    }

    // Update the client with the modified transaction types
    val updatedClient = existingClient.copy(
        timestamp = currentTimestamp,
        typesTarification = updatedTypesTarification
    )

    // Update the product with the modified client
    val updatedProduct = existingProduct.copy(
        timestamp = currentTimestamp,
        clients = existingProduct.clients.map { client ->
            if (client.id == existingClient.id) updatedClient else client
        }
    )

    return updatedProduct
}
