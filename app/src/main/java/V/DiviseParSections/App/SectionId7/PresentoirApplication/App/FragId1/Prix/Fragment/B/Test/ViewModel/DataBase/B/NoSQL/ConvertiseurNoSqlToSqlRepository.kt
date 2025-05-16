package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.B.NoSQL

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.InfosSqlDataBasesRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.B.NoSQL.Model.ProduitNoSqlDataBase
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.B_ClientInfos as SqlClientInfos

class ConvertiseurNoSqlToSqlRepository(
    private val sqlRepository: InfosSqlDataBasesRepository,
) {
    private val TAG = "ConvertiseurNoSqlToSql"
    private val repositoryCoroutine = CoroutineScope(Dispatchers.IO)

    private val _noSqlDataFlow = MutableStateFlow(ProduitNoSqlDataBase(emptyList()))
    val noSqlDataFlow: StateFlow<ProduitNoSqlDataBase> = _noSqlDataFlow.asStateFlow()

    init {
        Log.d(TAG, "Initializing ConvertiseurNoSqlToSqlRepository")
        repositoryCoroutine.launch {
            Log.d(TAG, "Starting data conversion coroutine")
            val startTime = System.currentTimeMillis()

            val initialNoSqlData = convertSqlToNoSql {
                val endTime = System.currentTimeMillis()
                Log.d(TAG, "Initial data conversion completed in ${endTime - startTime}ms")
            }
            _noSqlDataFlow.value = initialNoSqlData
            Log.d(TAG, "Initial data set to noSqlDataFlow")

            Log.d(TAG, "Setting up collection from sqlRepository")
            sqlRepository.modelListFlow.collect { sqlDataList ->
                val collectTime = System.currentTimeMillis()
                Log.d(TAG, "Received new SQL data list of size: ${sqlDataList.size}")

                if (sqlDataList.isNotEmpty()) {
                    Log.d(TAG, "Starting conversion for new data")
                    val conversionStartTime = System.currentTimeMillis()

                    val noSqlData = convertSqlToNoSql {
                        val conversionEndTime = System.currentTimeMillis()
                        Log.d(TAG, "Data conversion completed in ${conversionEndTime - conversionStartTime}ms")
                    }
                    _noSqlDataFlow.value = noSqlData

                    val updateCompleteTime = System.currentTimeMillis()
                    Log.d(TAG, "Data flow update completed in ${updateCompleteTime - collectTime}ms")
                } else {
                    Log.d(TAG, "Received empty SQL data list, skipping conversion")
                }
            }
        }
    }

    private suspend fun convertSqlToNoSql(
        onSuccess: () -> Unit = {}
    ): ProduitNoSqlDataBase {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "convertSqlToNoSql: Starting to fetch SQL data")
                val fetchStartTime = System.currentTimeMillis()

                val sqlDataList = sqlRepository.modelListFlow.first()
                val fetchEndTime = System.currentTimeMillis()
                Log.d(TAG, "convertSqlToNoSql: SQL data fetched in ${fetchEndTime - fetchStartTime}ms, size: ${sqlDataList.size}")

                if (sqlDataList.isEmpty()) {
                    Log.d(TAG, "convertSqlToNoSql: SQL data list is empty, returning empty NoSQL model")
                    return@withContext ProduitNoSqlDataBase(emptyList())
                }

                val sqlData = sqlDataList.first()
                Log.d(TAG, "convertSqlToNoSql: Processing SQL data with ${sqlData.a_ProduitInfos.size} products, ${sqlData.b_ClientInfos.size} clients")

                val processingStartTime = System.currentTimeMillis()
                val produitsList = sqlData.a_ProduitInfos.map { produit ->
                    Log.d(TAG, "convertSqlToNoSql: Processing product ID: ${produit.id}")

                    val produitTarifications = sqlData.d_TarificationInfos.filter { it.idProduit == produit.id }
                    Log.d(TAG, "convertSqlToNoSql: Found ${produitTarifications.size} tarifications for product ${produit.id}")

                    val clientGroups = produitTarifications.groupBy { it.idClient }
                    Log.d(TAG, "convertSqlToNoSql: Grouped into ${clientGroups.size} client groups")

                    val clientAcheteurs = clientGroups.map { (clientId, clientTarifications) ->
                        val clientInfo = sqlData.b_ClientInfos.find { it.id == clientId }
                            ?: SqlClientInfos(id = clientId)

                        val typeGroups = clientTarifications.groupBy { it.idTypeTarification }

                        val typeTarifications = typeGroups.map { (typeId, tarificationsForType) ->
                            val typeInfo = sqlData.c_TypeTarificationInfos.find { it.id == typeId }
                                ?: C_TypeTarificationInfos(id = typeId)

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

                val processingEndTime = System.currentTimeMillis()
                Log.d(TAG, "convertSqlToNoSql: Processing completed in ${processingEndTime - processingStartTime}ms, created ${produitsList.size} products")

                val result = ProduitNoSqlDataBase(produitsList)
                onSuccess()
                result
            } catch (e: Exception) {
                Log.e(TAG, "convertSqlToNoSql: Error during conversion", e)
                e.printStackTrace()
                ProduitNoSqlDataBase(emptyList())
            }
        }
    }

    fun getProduitInfos(id: Long): A_ProduitInfos? {
        val sqlDataList = sqlRepository.modelListFlow.value
        if (sqlDataList.isEmpty()) return null

        val sqlData = sqlDataList.first()
        return sqlData.a_ProduitInfos.find { it.id == id }
    }

    fun getClientInfos(id: Long): SqlClientInfos? {
        val sqlDataList = sqlRepository.modelListFlow.value
        if (sqlDataList.isEmpty()) return null

        val sqlData = sqlDataList.first()
        return sqlData.b_ClientInfos.find { it.id == id }
    }

    fun getTypeTarificationInfos(id: Long): C_TypeTarificationInfos? {
        val sqlDataList = sqlRepository.modelListFlow.value
        if (sqlDataList.isEmpty()) return null

        val sqlData = sqlDataList.first()
        return sqlData.c_TypeTarificationInfos.find { it.id == id }
    }

    fun getTarificationInfos(idProduit: Long, idClient: Long, idTypeTarification: Long): List<D_TarificationInfos> {
        val sqlDataList = sqlRepository.modelListFlow.value
        if (sqlDataList.isEmpty()) return emptyList()

        val sqlData = sqlDataList.first()
        return sqlData.d_TarificationInfos.filter {
            it.idProduit == idProduit &&
                    it.idClient == idClient &&
                    it.idTypeTarification == idTypeTarification
        }
    }

    suspend fun refreshNoSqlData() {
        Log.d(TAG, "refreshNoSqlData: Manually refreshing NoSQL data")
        val startTime = System.currentTimeMillis()

        val noSqlData = convertSqlToNoSql {
            val endTime = System.currentTimeMillis()
            Log.d(TAG, "refreshNoSqlData: Data refreshed in ${endTime - startTime}ms")
        }
        _noSqlDataFlow.value = noSqlData
    }
}
