package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.B.NoSQL.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL.InfosSqlDataBasesRepository
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitsNoSqlDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ConvertiseurNoSqlToSqlRepositorys(
    val sqlRepository: InfosSqlDataBasesRepository,
) {
    val TAG = "ConvertiseurNoSqlToSqlRepo"
    private val repositoryCoroutine = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    private val _noSqlDataFlow = MutableStateFlow(ProduitsNoSqlDataBase(emptyList()))
    val noSqlDataFlow: StateFlow<ProduitsNoSqlDataBase> = _noSqlDataFlow.asStateFlow()

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

    suspend fun addClientInfos(newData: B_ClientInfos): Boolean {
        return mutex.withLock {
            try {
                val currentData = sqlRepository.modelListFlow.value.firstOrNull()
                if (currentData != null) {
                    val existingClient = currentData.b_ClientInfosList.find { it.id == newData.id }
                    if (existingClient != null) {
                        verifyClientProductConnections(currentData, newData.id)
                        return@withLock true
                    }

                    val updatedData = currentData.copy(
                        b_ClientInfosList = currentData.b_ClientInfosList.toMutableList().apply {
                            add(newData)
                        }
                    )

                    sqlRepository.upsert(updatedData)
                    verifyClientProductConnections(updatedData, newData.id)
                    refreshNoSqlData()
                    return@withLock true
                } else {
                    return@withLock false
                }
            } catch (e: Exception) {
                return@withLock false
            }
        }
    }

    private suspend fun verifyClientProductConnections(
        currentData: DataBasesInfosSql,
        clientId: Long
    ) {
        for (product in currentData.a_ProduitInfos) {
            val hasConnection = currentData.d_TarificationInfos.any {
                it.idProduit == product.id && it.idClient == clientId
            }

            if (!hasConnection) {
                val newTarification = D_TarificationInfos(
                    vidTimestamp = System.currentTimeMillis(),
                    idProduit = product.id,
                    idClient = clientId,
                    idTypeTarification = 4L,
                    prixCurrency = 0.0,
                    needUpdate = true
                )

                val updatedTarifications = currentData.d_TarificationInfos.toMutableList().apply {
                    add(newTarification)
                }

                val updatedData = currentData.copy(
                    d_TarificationInfos = updatedTarifications
                )

                sqlRepository.upsert(updatedData)
            }
        }

        refreshNoSqlData()
    }

    suspend fun addTarificationInfos(newData: D_TarificationInfos): Boolean {
        return mutex.withLock {
            try {
                val currentData = sqlRepository.modelListFlow.value.firstOrNull()
                if (currentData != null) {
                    val existingTarification = currentData.d_TarificationInfos.find { tarif ->
                        tarif.idProduit == newData.idProduit &&
                                tarif.idClient == newData.idClient &&
                                tarif.idTypeTarification == newData.idTypeTarification
                    }

                    if (existingTarification != null) {
                        if (existingTarification.prixCurrency != newData.prixCurrency) {
                            val updatedTarification = existingTarification.copy(
                                prixCurrency = newData.prixCurrency,
                                vidTimestamp = System.currentTimeMillis(),
                                needUpdate = true
                            )

                            val updatedTarifications = currentData.d_TarificationInfos.toMutableList().apply {
                                remove(existingTarification)
                                add(updatedTarification)
                            }

                            val updatedData = currentData.copy(
                                d_TarificationInfos = updatedTarifications
                            )

                            sqlRepository.upsert(updatedData)
                            refreshNoSqlData()
                        }

                        return@withLock true
                    }

                    val updatedData = currentData.copy(
                        d_TarificationInfos = currentData.d_TarificationInfos.toMutableList()
                            .apply {
                                add(newData)
                            }
                    )

                    sqlRepository.upsert(updatedData)
                    refreshNoSqlData()
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    private suspend fun addTypeTarificationInfos(newData: C_TypeTarificationInfos): Boolean {
        return mutex.withLock {
            try {
                val currentData = sqlRepository.modelListFlow.value.firstOrNull()
                if (currentData != null) {
                    val updatedData = currentData.copy(
                        c_TypeTarificationInfos = currentData.c_TypeTarificationInfos.toMutableList()
                            .apply {
                                add(newData)
                            }
                    )

                    sqlRepository.upsert(updatedData)
                    refreshNoSqlData()
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    fun copyAdd_C_TypeTarificationInfos(newData: C_TypeTarificationInfos): Unit {
        repositoryCoroutine.launch {
            addTypeTarificationInfos(newData)
        }
    }

    suspend fun addProduitInfos(newData: A_ProduitInfos): Boolean {
        return mutex.withLock {
            try {
                val currentData = sqlRepository.modelListFlow.value.firstOrNull()
                if (currentData != null) {
                    val updatedData = currentData.copy(
                        a_ProduitInfos = currentData.a_ProduitInfos.toMutableList().apply {
                            add(newData)
                        }
                    )

                    sqlRepository.upsert(updatedData)
                    refreshNoSqlData()
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    fun copyAdd_A_ProduitInfos(newData: A_ProduitInfos): Unit {
        repositoryCoroutine.launch {
            addProduitInfos(newData)
        }
    }

    fun copyAdd_B_ClientInfos(newData: B_ClientInfos): Unit {
        repositoryCoroutine.launch {
            val success = addClientInfos(newData)
        }
    }

    fun getProduitInfos(id: Long): A_ProduitInfos? {
        val sqlDataList = sqlRepository.modelListFlow.value
        if (sqlDataList.isEmpty()) return null

        val sqlData = sqlDataList.first()
        return sqlData.a_ProduitInfos.find { it.id == id }
    }

    fun getB_ClientInfos(id: Long): B_ClientInfos? {
        val sqlDataList = sqlRepository.modelListFlow.value
        if (sqlDataList.isEmpty()) return null

        val sqlData = sqlDataList.first()
        return sqlData.b_ClientInfosList.find { it.id == id }
    }

    fun getTypeTarificationInfos(id: Long): C_TypeTarificationInfos? {
        val sqlDataList = sqlRepository.modelListFlow.value
        if (sqlDataList.isEmpty()) return null

        val sqlData = sqlDataList.first()
        return sqlData.c_TypeTarificationInfos.find { it.id == id }
    }

    fun getTarificationInfos(
        idProduit: Long,
        idClient: Long,
        idTypeTarification: Long,
        ancienRepoProduitPrixVent: Double?
    ): List<D_TarificationInfos> {
        val sqlDataList = sqlRepository.modelListFlow.value
        if (sqlDataList.isEmpty()) return emptyList()

        val sqlData = sqlDataList.first()
        return sqlData.d_TarificationInfos.filter {
            it.idProduit == idProduit &&
                    it.idClient == idClient &&
                    it.idTypeTarification == idTypeTarification &&
                    it.prixCurrency == ancienRepoProduitPrixVent
        }
    }

    suspend fun refreshNoSqlData() {
        val noSqlData = convertSqlToNoSql()
        _noSqlDataFlow.value = noSqlData
    }
}
