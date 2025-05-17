package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.InfosSqlDataBasesRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Model.ProduitNoSqlDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos as SqlClientInfos

class ConvertiseurNoSqlToSqlRepository(
    private val sqlRepository: InfosSqlDataBasesRepository,
) {
    private val repositoryCoroutine = CoroutineScope(Dispatchers.IO)

    private val _noSqlDataFlow = MutableStateFlow(ProduitNoSqlDataBase(emptyList()))
    val noSqlDataFlow: StateFlow<ProduitNoSqlDataBase> = _noSqlDataFlow.asStateFlow()

    init {
        repositoryCoroutine.launch {
            val initialNoSqlData = convertSqlToNoSql()
            _noSqlDataFlow.value = initialNoSqlData

            sqlRepository.modelListFlow.collect { sqlDataList ->
                if (sqlDataList.isNotEmpty()) {
                    val noSqlData = convertSqlToNoSql()
                    _noSqlDataFlow.value = noSqlData
                }
            }
        }
    }

    private suspend fun convertSqlToNoSql(
        onSuccess: () -> Unit = {}
    ): ProduitNoSqlDataBase {
        return withContext(Dispatchers.IO) {
            try {
                val sqlDataList = sqlRepository.modelListFlow.first()

                if (sqlDataList.isEmpty()) {
                    return@withContext ProduitNoSqlDataBase(emptyList())
                }

                val sqlData = sqlDataList.first()

                val produitsList = sqlData.a_ProduitInfos.map { produit ->
                    val produitTarifications = sqlData.d_TarificationInfos.filter { it.idProduit == produit.id }
                    val clientGroups = produitTarifications.groupBy { it.idClient }

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

                val result = ProduitNoSqlDataBase(produitsList)
                onSuccess()
                result
            } catch (e: Exception) {
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
        val noSqlData = convertSqlToNoSql()
        _noSqlDataFlow.value = noSqlData
    }
}
