package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test._A.View.logDebug

// Fixed NewProduit.kt with proper logging and null handling
fun addNewTransactionType(produits: List<Produit>): Produit? {
    // Fixed TODO(1): Added logging to track what's happening in this function
    logDebug("Starting addNewTransactionType with ${produits.size} products")

    // Get the first active product
    val existingProduct = produits
        .find { it.cesStatuesMutable.cActiveDonsSonListParent }

    if (existingProduct == null) {
        logDebug("No active product found")
        return null
    }

    logDebug("Found active product: ${existingProduct.id} - ${existingProduct.infos.nom}")

    // Get the first active client
    val existingClient = existingProduct.clients
        .find { it.cesStatuesMutable.cActiveDonsSonListParent }

    if (existingClient == null) {
        logDebug("No active client found")
        return null
    }

    logDebug("Found active client: ${existingClient.id} - ${existingClient.infos.nom}")

    // Set a fixed ID for the new tarification type
    val typeId = 3L

    // Check if this type already exists
    val existingType = existingClient.typesTarification.find { it.id == typeId }
    logDebug("Type ID $typeId exists: ${existingType != null}")

    // Calculate the next price ID to prevent duplicates
    val maxPrixId = existingClient.typesTarification
        .flatMap { it.PrixsCurrency }
        .maxOfOrNull { it.id }
        ?: 0L
    val newPrixId = maxPrixId + 1
    logDebug("Using new price ID: $newPrixId (max was $maxPrixId)")

    // Create a new prix
    val currentTimestamp = System.currentTimeMillis()
    val newPrix = Produit.Client.TypeTarification.Prix(
        id = newPrixId,
        timestamp = currentTimestamp,
        valeur = 500.0
    )
    logDebug("Created new price with value: ${newPrix.valeur} at timestamp: $currentTimestamp")

    // Update or create the tarification type
    val updatedTypesTarification = if (existingType != null) {
        logDebug("Updating existing type $typeId with new price")
        existingClient.typesTarification.map { typeTarif ->
            if (typeTarif.id == typeId) {
                // Check if this price already exists to prevent duplicates
                val priceExists = typeTarif.PrixsCurrency.any { it.id == newPrixId }
                if (priceExists) {
                    logDebug("Price already exists in type $typeId. Skipping.")
                    typeTarif
                } else {
                    logDebug("Adding new price to type $typeId")
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
        logDebug("Creating new type $typeId")
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
    logDebug("Updated client has ${updatedClient.typesTarification.size} types and ${updatedClient.typesTarification.flatMap { it.PrixsCurrency }.size} total prices")

    // Update the product with the modified client
    val updatedProduct = existingProduct.copy(
        timestamp = currentTimestamp,
        clients = existingProduct.clients.map { client ->
            if (client.id == existingClient.id) updatedClient else client
        }
    )
    logDebug("Returning updated product")

    return updatedProduct
}
