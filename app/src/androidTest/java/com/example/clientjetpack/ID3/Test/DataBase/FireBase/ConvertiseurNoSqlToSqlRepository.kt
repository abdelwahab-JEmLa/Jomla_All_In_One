package com.example.clientjetpack.ID3.Test.DataBase.FireBase

import com.example.clientjetpack.ID3.Test.DataBase.FireBase.Model.ProduitNoSqlDataBase
import com.example.clientjetpack.ID3.Test.DataBase.SQL.InfosSqlDataBasesRepository
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.C_TypeTarificationInfos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.B_ClientInfos as SqlClientInfos

class ConvertiseurNoSqlToSqlRepository(
    private val sqlRepository: InfosSqlDataBasesRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    // State flow for NoSQL data
    private val _noSqlDataFlow = MutableStateFlow(ProduitNoSqlDataBase(emptyList()))
    val noSqlDataFlow: StateFlow<ProduitNoSqlDataBase> = _noSqlDataFlow.asStateFlow()

    init {
        coroutineScope.launch {
            // Perform initial conversion
            val initialNoSqlData = convertSqlToNoSql()
            _noSqlDataFlow.value = initialNoSqlData

            // Then collect SQL data changes and convert to NoSQL format
            sqlRepository.modelListFlow.collect { sqlDataList ->
                if (sqlDataList.isNotEmpty()) {
                    val noSqlData = convertSqlToNoSql()
                    _noSqlDataFlow.value = noSqlData
                }
            }
        }
    }

    suspend fun convertSqlToNoSql(
        onSuccess: () -> Unit = {}
    ): ProduitNoSqlDataBase {
        return withContext(ioDispatcher) {
            try {
                // Get the latest data from the SQL repository
                val sqlDataList = sqlRepository.modelListFlow.first()
                if (sqlDataList.isEmpty()) {
                    return@withContext ProduitNoSqlDataBase(emptyList())
                }

                val sqlData = sqlDataList.first()

                // Process each product
                val produitsList = sqlData.a_ProduitInfos.map { produit ->
                    // Get all tarifications for this product
                    val produitTarifications = sqlData.d_TarificationInfos.filter { it.idProduit == produit.id }

                    // Group tarifications by client ID
                    val clientGroups = produitTarifications.groupBy { it.idClient }

                    // Process each client group
                    val clientAcheteurs = clientGroups.map { (clientId, clientTarifications) ->
                        // Find client info
                        val clientInfo = sqlData.b_ClientInfos.find { it.id == clientId }
                            ?: SqlClientInfos(id = clientId)

                        // Group by tarification type
                        val typeGroups = clientTarifications.groupBy { it.idTypeTarification }

                        // Process each tarification type
                        val typeTarifications = typeGroups.map { (typeId, tarificationsForType) ->
                            // Find type info
                            val typeInfo = sqlData.c_TypeTarificationInfos.find { it.id == typeId }
                                ?: C_TypeTarificationInfos(id = typeId)

                            // Create price list
                            val prixList = tarificationsForType.map { tarif ->
                                ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                    vidTimestamp = tarif.vidTimestamp,
                                    valeur = tarif.prixCurrency
                                )
                            }

                            ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = typeInfo.id,
                                PrixsCurrency = prixList
                            )
                        }

                        ProduitNoSqlDataBase.Produit.ClientAchteur(
                            vidTimestamp = System.currentTimeMillis(),
                            infosId = clientInfo.id,
                            typeTarification = typeTarifications
                        )
                    }

                    ProduitNoSqlDataBase.Produit(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = produit.id,
                        clientAchteurs = clientAcheteurs
                    )
                }

                val result = ProduitNoSqlDataBase(produitsList)
                onSuccess()
                result
            } catch (e: Exception) {
                e.printStackTrace()
                ProduitNoSqlDataBase(emptyList())
            }
        }
    }

    // Force immediate update of NoSQL data - useful for testing
    suspend fun refreshNoSqlData() {
        val noSqlData = convertSqlToNoSql()
        _noSqlDataFlow.value = noSqlData
    }
}
