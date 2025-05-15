package com.example.clientjetpack.Components.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.ProduitNoSqlDataBase
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf


class TarificationViewModel {
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState
      /*
    // Store the _InfosSqlDataBases instance as a property
    private val noSqlData: _InfosSqlDataBases = addTestData()

    init {
        // Use the _InfosSqlDataBases instance to generate the ProduitNoSqlDataBase
        _uiState.value = UiState(outputModel = noSqlData.toOutputNoSqlModel())
    }

    /**
     * Creates and returns test data for the application
     */
    private fun addTestData(): _InfosSqlDataBases {
        return _InfosSqlDataBases(
            a_ProduitInfos = mutableListOf(
                A_ProduitInfos(id = 1, nom = "Produit Optila"),
                A_ProduitInfos(id = 2, nom = "Produit Hnina"),
                A_ProduitInfos(id = 3, nom = "Produit kemya")
            ),
            b_ClientInfos = mutableListOf(
                B_ClientInfos(
                    id = 1,
                    nom = "ClientAchteur Abderrahman",
                    idActiveTypeTarificationDataBase = 1
                ),
                B_ClientInfos(id = 2, nom = "ClientAchteur Beta", idActiveTypeTarificationDataBase = 2),
                B_ClientInfos(id = 3, nom = "ClientAchteur Gamma", idActiveTypeTarificationDataBase = 3)
            ),
            d_TarificationInfos = mutableListOf(
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 1, hour = 12, minute = 30),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 20.99
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 5, hour = 13, minute = 30),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 25.50
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 5, hour = 14, minute = 30),
                    idProduit = 1,
                    idClient = 2,
                    idTypeTarification = 2,
                    prixCurrency = 9.75
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 6, hour = 3, minute = 30),
                    idProduit = 2,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 15.25
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 6, hour = 4, minute = 30),
                    idProduit = 3,
                    idClient = 1,
                    idTypeTarification = 3,
                    prixCurrency = 14.80
                )
            )
        )
    }

    private fun _InfosSqlDataBases.toOutputNoSqlModel(): ProduitNoSqlDataBase {
        val groupedByProduct = d_TarificationInfos.groupBy { it.idProduit }

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

    fun getSqlClient(id: Long): B_ClientInfos? {
        return noSqlData.b_ClientInfos.find { it.id == id }
    }

    fun getSqlProduit(id: Long): A_ProduitInfos? {
        return noSqlData.a_ProduitInfos.find { it.id == id }
    }

    fun getSqlTypeTarification(id: Long): C_TypeTarificationInfos? {
        val tarification = noSqlData.d_TarificationInfos.find { it.idTypeTarification == id }
        return tarification?.let {
            val enumType = when (id) {
                1L -> TypeTarificationEnum.ParBenifice
                2L -> TypeTarificationEnum.Historique
                3L -> TypeTarificationEnum.LeMaxPrixArrive
                else -> TypeTarificationEnum.ParBenifice // Default value
            }
            C_TypeTarificationInfos(id = id, typeTarificationEnum = enumType)
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

        val newDTarification = D_TarificationInfos(
            vidTimestamp = currentTime,
            idProduit = randomProduitId.toLong(),
            idClient = randomClientId.toLong(),
            idTypeTarification = randomTypeId.toLong(),
            prixCurrency = randomPrice
        )

        noSqlData.d_TarificationInfos.add(newDTarification)

        // Update UI state with the new model
        _uiState.value = UiState(outputModel = noSqlData.toOutputNoSqlModel())
    }

    fun getOutputModel(): ProduitNoSqlDataBase {
        return noSqlData.toOutputNoSqlModel()
    }

    fun createTimestamp(year: Int = 2025, month: Int=5, day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
            */
}
class UiState(
    val outputModel: ProduitNoSqlDataBase =
        ProduitNoSqlDataBase(emptyList()),

    val isLoading: Boolean = false,
    val error: String? = null
)
