package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview._A.View.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview._A.View.function.round
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.room.PrimaryKey
import java.util.Calendar
import kotlin.random.Random

class UiState(
    val outputModel: TarificationViewModel.ProduitNoSqlDataBase = TarificationViewModel.ProduitNoSqlDataBase(emptyList()),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TarificationViewModel {
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    // Store the InfosSqlDataBases instance as a property
    private val noSqlData: InfosSqlDataBases = addTestData()

    init {
        // Use the InfosSqlDataBases instance to generate the ProduitNoSqlDataBase
        _uiState.value = UiState(outputModel = noSqlData.toOutputNoSqlModel())
    }

    /**
     * Creates and returns test data for the application
     */
    private fun addTestData(): InfosSqlDataBases {
        return InfosSqlDataBases(
            produitInfoList = mutableListOf(
                ProduitInfos(id = 1, nom = "Produit Optila"),
                ProduitInfos(id = 2, nom = "Produit Hnina"),
                ProduitInfos(id = 3, nom = "Produit kemya")
            ),
            clientInfoList = mutableListOf(
                ClientDataBase(
                    id = 1,
                    nom = "ClientAchteur Abderrahman",
                    idActiveTypeTarificationDataBase = 1
                ),
                ClientDataBase(id = 2, nom = "ClientAchteur Beta", idActiveTypeTarificationDataBase = 2),
                ClientDataBase(id = 3, nom = "ClientAchteur Gamma", idActiveTypeTarificationDataBase = 3)
            ),
            tarificationInfoList = mutableListOf(
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

    private fun InfosSqlDataBases.toOutputNoSqlModel(): ProduitNoSqlDataBase {
        val groupedByProduct = tarificationInfoList.groupBy { it.idProduit }

        val produits = groupedByProduct.map { (produitId, produitTarifications) ->
            val groupedByClient = produitTarifications.groupBy { it.idClient }

            val produitTimestamp =
                produitTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()

            val clientAchteurs = groupedByClient.map { (clientId, clientTarifications) ->
                val groupedByType = clientTarifications.groupBy { it.idTypeTarification }

                val clientTimestamp =
                    clientTarifications.maxOfOrNull { it.vidTimestamp }
                        ?: System.currentTimeMillis()

                val typeTarifications = groupedByType.map { (typeId, typeTarifications) ->
                    val typeTimestamp =
                        typeTarifications.maxOfOrNull { it.vidTimestamp }
                            ?: System.currentTimeMillis()

                    val prices = typeTarifications.map { tarif ->
                        ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                            vidTimestamp = tarif.vidTimestamp,
                            valeur = tarif.prixCurrency
                        )
                    }.sortedByDescending { it.vidTimestamp }

                    ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                        infosId = typeId,
                        vidTimestamp = typeTimestamp,
                        PrixsCurrency = prices
                    )
                }.sortedByDescending { it.vidTimestamp }

                ProduitNoSqlDataBase.Produit.ClientAchteur(
                    infosId = clientId,
                    vidTimestamp = clientTimestamp,
                    typeTarification = typeTarifications
                )
            }.sortedByDescending { it.vidTimestamp }

            ProduitNoSqlDataBase.Produit(
                infosId = produitId,
                vidTimestamp = produitTimestamp,
                clientAchteurs = clientAchteurs
            )
        }.sortedByDescending { it.vidTimestamp }

        return ProduitNoSqlDataBase(produits = produits)
    }

    fun getSqlClient(id: Long): ClientDataBase? {
        return noSqlData.clientInfoList.find { it.id == id }
    }

    fun getSqlProduit(id: Long): ProduitInfos? {
        return noSqlData.produitInfoList.find { it.id == id }
    }

    fun getSqlTypeTarification(id: Long): TypeTarificationDataBase? {
        val tarification = noSqlData.tarificationInfoList.find { it.idTypeTarification == id }
        return tarification?.let {
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

        noSqlData.tarificationInfoList.add(newTarification)

        // Update UI state with the new model
        _uiState.value = UiState(outputModel = noSqlData.toOutputNoSqlModel())
    }

    fun getOutputModel(): ProduitNoSqlDataBase {
        return noSqlData.toOutputNoSqlModel()
    }

    data class ProduitNoSqlDataBase(
        val produits: List<Produit>,
    ) {
        data class Produit(
            val vidTimestamp: Long,
            val infosId: Long,
            val clientAchteurs: List<ClientAchteur>,
        ) {
            data class ClientAchteur(
                val vidTimestamp: Long,
                val infosId: Long,
                val typeTarification: List<TypeTarification>,
            ) {
                data class TypeTarification(
                    val vidTimestamp: Long,
                    val infosId: Long,
                    val PrixsCurrency: List<Prix>,
                ) {
                    data class Prix(
                        val vidTimestamp: Long,
                        val valeur: Double,
                    )
                }
            }
        }
    }

    data class InfosSqlDataBases(
        val produitInfoList: MutableList<ProduitInfos> = mutableListOf(),
        val clientInfoList: MutableList<ClientDataBase> = mutableListOf(),
        val tarificationInfoList: MutableList<Tarification> = mutableListOf()
    )

    data class ProduitInfos(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val nom: String = ""
    )

    data class ClientDataBase(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val nom: String = "Non Difinie",
        val idActiveTypeTarificationDataBase: Long = 0,
    )

    data class TypeTarificationDataBase(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val typeTarificationEnum: TypeTarificationEnum = TypeTarificationEnum.ParBenifice
    )

    enum class TypeTarificationEnum {
        ParBenifice,
        Historique,
        LeMaxPrixArrive
    }

    data class Tarification(
        val vidTimestamp: Long = 0L,
        val idProduit: Long = 0L,
        val idClient: Long = 0L,
        val idTypeTarification: Long = 0L,
        val prixCurrency: Double = 0.0
    )
    fun createTimestamp(year: Int = 2025, month: Int=5, day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

}
