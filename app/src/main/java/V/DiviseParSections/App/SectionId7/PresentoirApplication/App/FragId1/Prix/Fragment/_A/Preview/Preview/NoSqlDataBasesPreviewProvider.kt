package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.ClientDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.NoSqlDataBases
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.Tarification
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Provider for NoSqlDataBases preview data that will be converted to OutputNoSqlModel
 */
class NoSqlDataBasesPreviewProvider : PreviewParameterProvider<NoSqlDataBases> {
    override val values = sequenceOf(
        NoSqlDataBases(
            produitInfos = mutableListOf(
                ProduitInfos(id = 1, nom = "Produit A"),
                ProduitInfos(id = 2, nom = "Produit B"),
                ProduitInfos(id = 3, nom = "Produit C")
            ),
            clientDataBase = mutableListOf(
                ClientDataBase(id = 1, nom = "Client Alpha", idActiveTypeTarificationDataBase = 1),
                ClientDataBase(id = 2, nom = "Client Beta", idActiveTypeTarificationDataBase = 2),
                ClientDataBase(id = 3, nom = "Client Gamma", idActiveTypeTarificationDataBase = 3)
            ),
            tarificationEntries = mutableListOf(
                // Produit A - Client Alpha
                Tarification(
                    vidTimestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 10.99
                ),
                Tarification(
                    vidTimestamp = System.currentTimeMillis(),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 12.50
                ),
                
                // Produit A - Client Beta
                Tarification(
                    vidTimestamp = System.currentTimeMillis() - 43200000, // 12 hours ago
                    idProduit = 1,
                    idClient = 2,
                    idTypeTarification = 2,
                    prixCurrency = 9.75
                ),
                
                // Produit B - Client Alpha
                Tarification(
                    vidTimestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                    idProduit = 2,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 15.25
                ),
                
                // Produit B - Client Gamma
                Tarification(
                    vidTimestamp = System.currentTimeMillis() - 21600000, // 6 hours ago
                    idProduit = 2,
                    idClient = 3,
                    idTypeTarification = 3,
                    prixCurrency = 14.80
                )
            )
        )
    )
}

/**
 * Extension function to convert NoSqlDataBases to OutputNoSqlModel
 */
fun NoSqlDataBases.toOutputNoSqlModel(): OutputNoSqlModel {
    // Group tarifications by product
    val groupedByProduct = tarificationEntries.groupBy { it.idProduit }
    
    // Map each product with its clients and tarifications
    val produits = groupedByProduct.map { (produitId, produitTarifications) ->
        // Group tarifications by client for this product
        val groupedByClient = produitTarifications.groupBy { it.idClient }
        
        // Get the latest timestamp for this product
        val produitTimestamp = produitTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()
        
        // Map each client with its tarification types
        val clients = groupedByClient.map { (clientId, clientTarifications) ->
            // Group tarifications by type for this client
            val groupedByType = clientTarifications.groupBy { it.idTypeTarification }
            
            // Get the latest timestamp for this client
            val clientTimestamp = clientTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()
            
            // Map each tarification type with its prices
            val typeTarifications = groupedByType.map { (typeId, typeTarifications) ->
                // Get the latest timestamp for this tarification type
                val typeTimestamp = typeTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()
                
                // Map each price
                val prices = typeTarifications.map { tarif ->
                    OutputNoSqlModel.Produit.Client.TypeTarification.Prix(
                        vidTimestamp = tarif.vidTimestamp,
                        valeur = tarif.prixCurrency
                    )
                }.sortedByDescending { it.vidTimestamp }
                
                OutputNoSqlModel.Produit.Client.TypeTarification(
                    id = typeId,
                    vidTimestamp = typeTimestamp,
                    PrixsCurrency = prices
                )
            }.sortedByDescending { it.vidTimestamp }
            
            OutputNoSqlModel.Produit.Client(
                id = clientId,
                vidTimestamp = clientTimestamp,
                typeTarification = typeTarifications
            )
        }.sortedByDescending { it.vidTimestamp }
        
        OutputNoSqlModel.Produit(
            id = produitId,
            vidTimestamp = produitTimestamp,
            clients = clients
        )
    }.sortedByDescending { it.vidTimestamp }
    
    return OutputNoSqlModel(produits = produits)
}

/**
 * Helper class that provides an OutputNoSqlModel preview parameter from NoSqlDataBases
 */
class NoSqlToOutputModelPreviewProvider : PreviewParameterProvider<OutputNoSqlModel> {
    private val noSqlProvider = NoSqlDataBasesPreviewProvider()
    
    override val values = sequence {
        noSqlProvider.values.forEach { noSqlData ->
            yield(noSqlData.toOutputNoSqlModel())
        }
    }
}
