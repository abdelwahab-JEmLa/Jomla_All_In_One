package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.InfosSqlDataBasesRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitNoSqlDataBase
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos as SqlClientInfos

class ConvertiseurNoSqlToSqlRepository(
    val sqlRepository: InfosSqlDataBasesRepository,
) {
    private val TAG = "ConvertiseurNoSqlToSqlRepo"
    private val repositoryCoroutine = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex() // For thread-safe operations

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

    // In ConvertiseurNoSqlToSqlRepository.kt, modify the addClientInfos function
// to ensure it properly handles the client data and correctly updates NoSQL data:

    suspend fun addClientInfos(newData: B_ClientInfos): Boolean {
        return mutex.withLock {
            try {
                Log.d(TAG, "Adding B_ClientInfos: ${newData.id}")
                val currentData = sqlRepository.modelListFlow.value.firstOrNull()
                if (currentData != null) {
                    // Check if client already exists
                    val existingClient = currentData.b_ClientInfosList.find { it.id == newData.id }
                    if (existingClient != null) {
                        Log.d(TAG, "Client ${newData.id} already exists, skipping addition")

                        // Even though client exists, we should verify product connections exist
                        verifyClientProductConnections(currentData, newData.id)
                        return@withLock true
                    }

                    val updatedData = currentData.copy(
                        b_ClientInfosList = currentData.b_ClientInfosList.toMutableList().apply {
                            add(newData)
                        }
                    )

                    sqlRepository.upsert(updatedData)

                    // After adding client, verify product connections
                    verifyClientProductConnections(updatedData, newData.id)

                    // Explicitly refresh NoSQL data after client addition
                    refreshNoSqlData()
                    return@withLock true
                } else {
                    Log.e(TAG, "Failed to upsert B_ClientInfos: no current data available")
                    return@withLock false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding B_ClientInfos", e)
                return@withLock false
            }
        }
    }

    // Add this helper method to verify client-product connections exist
    private suspend fun verifyClientProductConnections(
        currentData: DataBasesInfosSql,
        clientId: Long
    ) {
        // Ensure that each product has a connection to this client with at least PRIX_BASE type
        for (product in currentData.a_ProduitInfos) {
            // Check if this product has any tarification for this client
            val hasConnection = currentData.d_TarificationInfos.any {
                it.idProduit == product.id && it.idClient == clientId
            }

            if (!hasConnection) {
                Log.d(TAG, "Creating connection between product ${product.id} and client $clientId")

                // Create default connection with PRIX_BASE
                val newTarification = D_TarificationInfos(
                    vidTimestamp = System.currentTimeMillis(),
                    idProduit = product.id,
                    idClient = clientId,
                    idTypeTarification = 4L, // PRIX_BASE
                    prixCurrency = 0.0,
                    needUpdate = true
                )

                // Use direct approach to add to repository
                val updatedTarifications = currentData.d_TarificationInfos.toMutableList().apply {
                    add(newTarification)
                }

                val updatedData = currentData.copy(
                    d_TarificationInfos = updatedTarifications
                )

                sqlRepository.upsert(updatedData)
            }
        }

        // Force refresh to ensure NoSQL data is updated
        refreshNoSqlData()
    }

    suspend fun addTarificationInfos(newData: D_TarificationInfos): Boolean {
        return mutex.withLock {
            try {
                Log.d(
                    TAG,
                    "Adding D_TarificationInfos: ${newData.idProduit}-${newData.idClient}-${newData.idTypeTarification}"
                )
                val currentData = sqlRepository.modelListFlow.value.firstOrNull()
                if (currentData != null) {
                    // Check if an equivalent tarification already exists (same product, client, and type)
                    val existingTarification = currentData.d_TarificationInfos.find { tarif ->
                        tarif.idProduit == newData.idProduit &&
                                tarif.idClient == newData.idClient &&
                                tarif.idTypeTarification == newData.idTypeTarification
                    }

                    if (existingTarification != null) {
                        Log.d(TAG, "Found existing tarification with same key attributes, updating instead of adding new")

                        // Only update if price has changed
                        if (existingTarification.prixCurrency != newData.prixCurrency) {
                            val updatedTarification = existingTarification.copy(
                                prixCurrency = newData.prixCurrency,
                                vidTimestamp = System.currentTimeMillis(),
                                needUpdate = true
                            )

                            // Remove old and add updated
                            val updatedTarifications = currentData.d_TarificationInfos.toMutableList().apply {
                                remove(existingTarification)
                                add(updatedTarification)
                            }

                            val updatedData = currentData.copy(
                                d_TarificationInfos = updatedTarifications
                            )

                            sqlRepository.upsert(updatedData)
                            refreshNoSqlData()
                        } else {
                            Log.d(TAG, "Existing tarification has same price, no update needed")
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
                    Log.e(TAG, "Failed to upsert D_TarificationInfos: no current data available")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding D_TarificationInfos", e)
                false
            }
        }
    }

    // FIXED: Renamed the suspend function to avoid naming conflict
    private suspend fun addTypeTarificationInfos(newData: C_TypeTarificationInfos): Boolean {
        return mutex.withLock {
            try {
                Log.d(TAG, "Adding C_TypeTarificationInfos: ${newData.id}")
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
                    Log.e(
                        TAG,
                        "Failed to upsert C_TypeTarificationInfos: no current data available"
                    )
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding C_TypeTarificationInfos", e)
                false
            }
        }
    }

    // FIXED: Now calls the renamed suspend function
    fun copyAdd_C_TypeTarificationInfos(newData: C_TypeTarificationInfos): Unit {
        repositoryCoroutine.launch {
            addTypeTarificationInfos(newData)
        }
    }

    // FIXED: Renamed the suspend function to avoid naming conflict
    suspend fun addProduitInfos(newData: A_ProduitInfos): Boolean {
        return mutex.withLock {
            try {
                Log.d(TAG, "Adding A_ProduitInfos: ${newData.id}")
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
                    Log.e(TAG, "Failed to upsert A_ProduitInfos: no current data available")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding A_ProduitInfos", e)
                false
            }
        }
    }

    // FIXED: Now calls the renamed suspend function
    fun copyAdd_A_ProduitInfos(newData: A_ProduitInfos): Unit {
        repositoryCoroutine.launch {
            addProduitInfos(newData)
        }
    }


    // FIXED: Now calls the renamed suspend function
    fun copyAdd_B_ClientInfos(newData: B_ClientInfos): Unit {
        repositoryCoroutine.launch {
            val success = addClientInfos(newData)
            Log.d(TAG, "B_ClientInfos upsert result: $success for client ${newData.id}")
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
                    val produitTarifications =
                        sqlData.d_TarificationInfos.filter { it.idProduit == produit.id }
                    val clientGroups = produitTarifications.groupBy { it.idClient }

                    val clientAcheteurs = clientGroups.map { (clientId, clientTarifications) ->
                        val clientInfo = sqlData.b_ClientInfosList.find { it.id == clientId }
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
                Log.e(TAG, "Error in convertSqlToNoSql", e)
                ProduitNoSqlDataBase(emptyList())
            }
        }
    }
}
