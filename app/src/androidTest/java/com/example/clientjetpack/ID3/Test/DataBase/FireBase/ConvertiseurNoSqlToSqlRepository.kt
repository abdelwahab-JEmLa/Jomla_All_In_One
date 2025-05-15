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
    private val sqlRepository: InfosSqlDataBasesRepository ,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    // State flow pour les données NoSQL
    private val _noSqlDataFlow = MutableStateFlow(ProduitNoSqlDataBase(emptyList()))
    val noSqlDataFlow: StateFlow<ProduitNoSqlDataBase> = _noSqlDataFlow.asStateFlow()

    init {
        coroutineScope.launch {
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
        return withContext(Dispatchers.Default) {
            // Get the latest data from the SQL repository
            val sqlData = sqlRepository.modelListFlow.first().firstOrNull()
                ?: return@withContext ProduitNoSqlDataBase(emptyList())

            val produits = sqlData.a_ProduitInfos
            val clients = sqlData.b_ClientInfos
            val typeTarifications = sqlData.c_TypeTarificationInfos
            val tarifications = sqlData.d_TarificationInfos

            val produitsList = produits.map { produit ->
                // Find all tarifications for this product
                val produitTarifications = tarifications.filter { it.idProduit == produit.id }

                // Group by client
                val clientMap = produitTarifications.groupBy { it.idClient }

                val clientAcheteurs = clientMap.map { (clientId, clientTarifications) ->
                    // Find client info
                    val clientInfo = clients.find { it.id == clientId } ?: SqlClientInfos(clientId)

                    // Group by tarification type
                    val typeTarificationMap = clientTarifications.groupBy { it.idTypeTarification }

                    val typeTarifications = typeTarificationMap.map { (typeId, tarificationsForType) ->
                        // Find tarification type info
                        val typeInfo = typeTarifications.find { it.id == typeId }
                            ?: C_TypeTarificationInfos(typeId)

                        // Create price list
                        val prix = tarificationsForType.map { tarif ->
                            ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                vidTimestamp = tarif.vidTimestamp,
                                valeur = tarif.prixCurrency
                            )
                        }

                        ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                            vidTimestamp = System.currentTimeMillis(),
                            infosId = typeInfo.id,
                            PrixsCurrency = prix
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
        }
    }
}
