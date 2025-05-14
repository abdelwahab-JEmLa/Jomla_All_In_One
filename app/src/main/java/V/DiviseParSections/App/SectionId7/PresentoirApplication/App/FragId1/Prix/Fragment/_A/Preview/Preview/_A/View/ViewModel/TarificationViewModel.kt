package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview._A.View.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.ClientDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.NoSqlDataBases
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.Tarification
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.TypeTarificationDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.TypeTarificationEnum
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.createTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview._A.View.function.round
import kotlin.random.Random

class TarificationViewModel {
    private val dataProvider: NoSqlDataBases
    private val produitMap: Map<Long, ProduitInfos>
    private val clientMap: Map<Long, ClientDataBase>
    private val typeTarificationMap: Map<Long, TypeTarificationDataBase>

    init {
        dataProvider = addTestData()
        produitMap = dataProvider.produitInfos.associateBy { it.id }
        clientMap = dataProvider.clientDataBase.associateBy { it.id }

        val typeTarifEnumValues = TypeTarificationEnum.entries.toTypedArray()
        typeTarificationMap = (1..3).associateBy(
            keySelector = { it.toLong() },
            valueTransform = { id ->
                val enumValue = typeTarifEnumValues[(id - 1) % typeTarifEnumValues.size]
                TypeTarificationDataBase(id = id.toLong(), typeTarificationEnum = enumValue)
            }
        )
    }

    /**
     * Creates and returns test data for the application
     */
    private fun addTestData(): NoSqlDataBases {
        return NoSqlDataBases(
            produitInfos = mutableListOf(
                ProduitInfos(id = 1, nom = "Produit Optila"),
                ProduitInfos(id = 2, nom = "Produit Hnina"),
                ProduitInfos(id = 3, nom = "Produit kemya")
            ),
            clientDataBase = mutableListOf(
                ClientDataBase(id = 1, nom = "Client Abderrahman", idActiveTypeTarificationDataBase = 1),
                ClientDataBase(id = 2, nom = "Client Beta", idActiveTypeTarificationDataBase = 2),
                ClientDataBase(id = 3, nom = "Client Gamma", idActiveTypeTarificationDataBase = 3)
            ),
            tarificationEntries = mutableListOf(
                Tarification(
                    vidTimestamp = createTimestamp(day = 1, hour = 12, minute = 30),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 20.99
                ),
                Tarification(
                    vidTimestamp = createTimestamp(day = 5, hour = 13, minute = 30),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 25.50
                ),
                Tarification(
                    vidTimestamp = createTimestamp(day = 5, hour = 14, minute = 30),
                    idProduit = 1,
                    idClient = 2,
                    idTypeTarification = 2,
                    prixCurrency = 9.75
                ),
                Tarification(
                    vidTimestamp = createTimestamp(day = 6, hour = 3, minute = 30),
                    idProduit = 2,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 15.25
                ),
                Tarification(
                    vidTimestamp = createTimestamp(day = 6, hour = 4, minute = 30),
                    idProduit = 3,
                    idClient = 1,
                    idTypeTarification = 3,
                    prixCurrency = 14.80
                )
            )
        )
    }

    fun NoSqlDataBases.toOutputNoSqlModel(): OutputNoSqlModel {
        val groupedByProduct = tarificationEntries.groupBy { it.idProduit }

        val produits = groupedByProduct.map { (produitId, produitTarifications) ->
            val groupedByClient = produitTarifications.groupBy { it.idClient }

            val produitTimestamp =
                produitTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()

            val clients = groupedByClient.map { (clientId, clientTarifications) ->
                val groupedByType = clientTarifications.groupBy { it.idTypeTarification }

                val clientTimestamp =
                    clientTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()

                val typeTarifications = groupedByType.map { (typeId, typeTarifications) ->
                    val typeTimestamp =
                        typeTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()

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
                    infosId = clientId,
                    vidTimestamp = clientTimestamp,
                    typeTarification = typeTarifications
                )
            }.sortedByDescending { it.vidTimestamp }

            OutputNoSqlModel.Produit(
                infosId = produitId,
                vidTimestamp = produitTimestamp,
                clients = clients
            )
        }.sortedByDescending { it.vidTimestamp }

        return OutputNoSqlModel(produits = produits)
    }

    fun getSqlClient(id: Long) = clientMap[id]
    fun getSqlProduit(id: Long) = produitMap[id]
    fun getSqlTypeTarification(id: Long) = typeTarificationMap[id]

    /**
     * Adds a random tarification entry to the database
     */
    fun addRandomTarification() {
        val randomProduitId = 1
        val randomClientId = 3
        val randomTypeId = 2

        // Generate a random price between 5.0 and 50.0
        val randomPrice = (Random.nextDouble() * 45.0 + 5.0).round(2)

        // Create a timestamp for current time
        val currentTime = System.currentTimeMillis()

        val newTarification = Tarification(
            vidTimestamp = currentTime,
            idProduit = randomProduitId.toLong(),
            idClient = randomClientId.toLong(),
            idTypeTarification = randomTypeId.toLong(),
            prixCurrency = randomPrice
        )

        dataProvider.tarificationEntries.add(newTarification)
    }

    fun getOutputModel(): OutputNoSqlModel {
        return dataProvider.toOutputNoSqlModel()
    }
}
