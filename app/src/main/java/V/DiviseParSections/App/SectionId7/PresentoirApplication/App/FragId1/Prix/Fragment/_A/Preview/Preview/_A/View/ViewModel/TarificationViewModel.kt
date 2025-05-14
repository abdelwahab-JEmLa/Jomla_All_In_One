package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview._A.View.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.ClientDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.InfosSqlDataBases
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.Tarification
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.TypeTarificationDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.TypeTarificationEnum
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.createTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview._A.View.function.round
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlin.random.Random

class UiState(
    val outputModel: OutputNoSqlModel = OutputNoSqlModel(emptyList()),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TarificationViewModel {
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    // Store the InfosSqlDataBases instance as a property
    private val noSqlData: InfosSqlDataBases = addTestData()

    init {
        // Use the InfosSqlDataBases instance to generate the OutputNoSqlModel
        _uiState.value = UiState(outputModel = noSqlData.toOutputNoSqlModel())
    }

    /**
     * Creates and returns test data for the application
     */
    private fun addTestData(): InfosSqlDataBases {
        return InfosSqlDataBases(
            produitInfos = mutableListOf(
                ProduitInfos(id = 1, nom = "Produit Optila"),
                ProduitInfos(id = 2, nom = "Produit Hnina"),
                ProduitInfos(id = 3, nom = "Produit kemya")
            ),
            clientDataBase = mutableListOf(
                ClientDataBase(
                    id = 1,
                    nom = "Client Abderrahman",
                    idActiveTypeTarificationDataBase = 1
                ),
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

    private fun InfosSqlDataBases.toOutputNoSqlModel(): OutputNoSqlModel {
        val groupedByProduct = tarificationEntries.groupBy { it.idProduit }

        val produits = groupedByProduct.map { (produitId, produitTarifications) ->
            val groupedByClient = produitTarifications.groupBy { it.idClient }

            val produitTimestamp =
                produitTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()

            val clients = groupedByClient.map { (clientId, clientTarifications) ->
                val groupedByType = clientTarifications.groupBy { it.idTypeTarification }

                val clientTimestamp =
                    clientTarifications.maxOfOrNull { it.vidTimestamp }
                        ?: System.currentTimeMillis()

                val typeTarifications = groupedByType.map { (typeId, typeTarifications) ->
                    val typeTimestamp =
                        typeTarifications.maxOfOrNull { it.vidTimestamp }
                            ?: System.currentTimeMillis()

                    val prices = typeTarifications.map { tarif ->
                        OutputNoSqlModel.Produit.Client.TypeTarification.Prix(
                            vidTimestamp = tarif.vidTimestamp,
                            valeur = tarif.prixCurrency
                        )
                    }.sortedByDescending { it.vidTimestamp }

                    OutputNoSqlModel.Produit.Client.TypeTarification(
                        infosId = typeId,
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

    fun getSqlClient(id: Long): ClientDataBase? {
        return noSqlData.clientDataBase.find { it.id == id }
    }

    fun getSqlProduit(id: Long): ProduitInfos? {
        return noSqlData.produitInfos.find { it.id == id }
    }

    fun getSqlTypeTarification(id: Long): TypeTarificationDataBase? {
        // Since TypeTarificationDataBase is not part of InfosSqlDataBases directly,
        // we need to create it based on the Tarification entries
        val tarification = noSqlData.tarificationEntries.find { it.idTypeTarification == id }
        return tarification?.let {
            // Create TypeTarificationDataBase with enum based on the id
            // This is a simplified approach based on the available data
            val enumType = when (id) {
                1L -> TypeTarificationEnum.ParBenifice
                2L -> TypeTarificationEnum.Historique
                3L -> TypeTarificationEnum.LeMaxPrixArrive
                else -> TypeTarificationEnum.ParBenifice // Default value
            }
            TypeTarificationDataBase(id = id, typeTarificationEnum = enumType)
        }
    }

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

        noSqlData.tarificationEntries.add(newTarification)

        // Update UI state with the new model
        _uiState.value = UiState(outputModel = noSqlData.toOutputNoSqlModel())
    }

    fun getOutputModel(): OutputNoSqlModel {
        return noSqlData.toOutputNoSqlModel()
    }
}
