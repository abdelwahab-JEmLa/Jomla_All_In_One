package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.createTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.strDateEtTempFromVidTimestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class _TestsDisplayerLogDataBase {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: _TarificationViewModel
    private lateinit var b_GroupeRepositoryImp: B_GroupeRepositoryImp
    private lateinit var tarificationRepo: TarificationDataBaseFacileEntre_RepositoryImp

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tarificationRepo = TarificationDataBaseFacileEntre_RepositoryImp()
        b_GroupeRepositoryImp = B_GroupeRepositoryImp()
        viewModel = _TarificationViewModel(tarificationRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun B_logUpdateReferentialDataBases(): Unit = runTest {
        // Create a new tarification entry
        val newTarification = AA_TarificationDataBaseFacileEntre(
            vidTimestamp = createTimestamp(day = 10, hour = 16, minute = 30),
            idProduit = 1L,
            idClient = 1L,
            idTypeTarification = 2L,
            prixCurrency = 9.99
        )

        // First ensure Client A exists in the repository
        val newClient = AB_ReferentialSepareDataBases.ClientDataBase(
            id = 1L,
            nom = "Client A",
            idActiveTypeTarificationDataBase = 2L  // Set to match the tarification type
        )

        // Add the client first to ensure it exists
        b_GroupeRepositoryImp.addNewData(newClient)

        // Verify client was added
        val clientExists = B_GroupeRepositoryImp.clientRepository.modelList.any { it.id == 1L }
        println("Client with ID 1 exists in repository: $clientExists")

        // Add the tarification entry
        tarificationRepo.add(newTarification)

        // Force dispatcher to process coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Refresh view model data
        viewModel.refreshData()

        // Force dispatcher to process coroutines again
        testDispatcher.scheduler.advanceUntilIdle()

        println("\n========Apre Update========\n")

        SepareReferentialDataBases()
    }

    @Test
    fun A_logSepareReferentialDataBases(): Unit {
        SepareReferentialDataBases()
    }

    private fun SepareReferentialDataBases() = runTest {
        try {
            val name = "A_DataBasesSepareReferential"
            val currentStrTime = strDateEtTempFromVidTimestamp(System.currentTimeMillis())
            println(
                "======== C Le Test Log Output Print Du Temp=${currentStrTime.first} " +
                        "${currentStrTime.second} du  $name  ========"
            )
            // Advance the dispatcher to ensure coroutines complete
            testDispatcher.scheduler.advanceUntilIdle()

            mainLog(viewModel.imbriquantFlow.value)

            assertTrue(true)
            println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")

        } catch (e: Exception) {
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }

    private fun mainLog(value: A_DataBase_Imbricant) {
        println("\n-- Hierarchical Structure --")

        logProduits(value)
    }

    private fun logProduits(value: A_DataBase_Imbricant) {
        val produitRepository = B_GroupeRepositoryImp.ProduitDataBase_RepositoryImp()

        value.produits.forEachIndexed { produitIndex, produit ->
            val isLastProduit = produitIndex == value.produits.size - 1
            val produitPrefix = TreePrefix.Type1.get(isLastProduit)

            val (produitDate, produitTime) = strDateEtTempFromVidTimestamp(produit.vidTimestamp)
            val relatedInfos = produitRepository.modelList.find { it.id == produit.id }

            // Using StringBuilder for more efficient string concatenation
            val produitInfos = StringBuilder().apply {
                append(produitPrefix)
                append(" Product : ")
                append(produit.id)
                append("=(${relatedInfos?.nom ?: " Unknown "})")
            }.toString()

            println("$produitInfos, Date: $produitDate Time: $produitTime (${produit.clients.size} clients)")

            logClients(produit.clients, isLastProduit)
        }
    }

    private fun logClients(
        clients: List<A_DataBase_Imbricant.Produit.Client>,
        isLastProduit: Boolean,
    ) {
        val clientRepository = B_GroupeRepositoryImp.clientRepository

        clients.forEachIndexed { clientIndex, client ->
            val isLastClient = clientIndex == clients.size - 1
            val clientPrefix =
                if (isLastProduit) TreePrefix.Type3.get(isLastClient) else TreePrefix.Type2.get(
                    isLastClient
                )

            val (clientDate, clientTime) = strDateEtTempFromVidTimestamp(client.vidTimestamp)
            val clientInfo = clientRepository.modelList.find { it.id == client.id }

            // Using StringBuilder for more efficient string concatenation
            val clientInfos = StringBuilder().apply {
                append(clientPrefix)
                append(" Client ID: ")
                append(client.id)
                append("=(${clientInfo?.nom ?: "Unknown"})")
                append(", Date: ")
                append(clientDate)
                append(" Time: ")
                append(clientTime)
                append(" (${client.typeTarification.size} tarification types)")
            }.toString()

            println(clientInfos)

            logTarificationTypes(client.typeTarification, isLastProduit, isLastClient)
        }
    }

    private fun logTarificationTypes(
        types: List<A_DataBase_Imbricant.Produit.Client.TypeTarification>,
        isLastProduit: Boolean,
        isLastClient: Boolean,
    ) {
        val typeRepository = B_GroupeRepositoryImp.TypeTarificationDataBase_RepositoryImp()
        val clientRepository = B_GroupeRepositoryImp.clientRepository

        // Find the client that owns these tarification types
        val currentClient = viewModel.imbriquantFlow.value.produits
            .flatMap { it.clients }
            .find { client -> client.typeTarification.any { types.contains(it) } }

        // Only proceed if we found the client
        if (currentClient != null) {
            // Get full client info from repository
            val clientInfo = clientRepository.modelList.find { it.id == currentClient.id }

            types.forEachIndexed { typeIndex, type ->
                val isLastType = typeIndex == types.size - 1
                val typePrefix = when {
                    isLastProduit && isLastClient -> TreePrefix.Type4.get(isLastType)
                    isLastClient -> "  │     ${if (isLastType) "└─" else "├─"}"
                    else -> "  │     ${if (isLastType) "└─" else "├─"}"
                }

                val (typeDate, typeTime) = strDateEtTempFromVidTimestamp(type.vidTimestamp)
                val typeInfo = typeRepository.modelList.find { it.id == type.id }

                // Check if this type is the active one for this client
                val isActive = clientInfo?.idActiveTypeTarificationDataBase == type.id
                val activeStatus = if (isActive) " [ACTIVE]" else ""

                // Using StringBuilder for more efficient string concatenation
                val typeInfos = StringBuilder().apply {
                    append(typePrefix)
                    append(" Tarification Type : ")
                    append(type.id)
                    append("=(${typeInfo?.typeTarificationEnum ?: "Unknown"})")
                    append(activeStatus)
                    append(" , Date: ")
                    append(typeDate)
                    append(" Time: ")
                    append(typeTime)
                    append(" (${type.PrixsCurrency.size} currencies)")
                }.toString()

                println(typeInfos)

                logPrixCurrencies(type.PrixsCurrency, isLastProduit, isLastClient, isLastType)
            }
        }
    }

    private fun logPrixCurrencies(
        currencies: List<A_DataBase_Imbricant.Produit.Client.TypeTarification.Prix>,
        isLastProduit: Boolean,
        isLastClient: Boolean,
        isLastType: Boolean,
    ) {
        currencies.forEachIndexed { currencyIndex, currency ->
            val isLastCurrency = currencyIndex == currencies.size - 1
            val currencyPrefix = when {
                isLastProduit && isLastClient && isLastType -> "          ${if (isLastCurrency) "└─" else "├─"}"
                isLastClient && isLastType -> "          ${if (isLastCurrency) "└─" else "├─"}"
                else -> "  │     │  ${if (isLastCurrency) "└─" else "├─"}"
            }

            val (currencyDate, currencyTime) = strDateEtTempFromVidTimestamp(currency.vidTimestamp)

            // Using StringBuilder for more efficient string concatenation
            val currencyInfos = StringBuilder().apply {
                append(currencyPrefix)
                append(" Currency: ")
                append(currency.valeur)
                append(", Date: ")
                append(currencyDate)
                append(" Time: ")
                append(currencyTime)
            }.toString()

            println(currencyInfos)
        }
    }

    enum class TreePrefix(private val lastItem: String, private val normalItem: String) {
        Type1("└─", "├─"),                 // For products
        Type2("  ├─", "  ├─"),             // For clients (not last product)
        Type3("  └─", "  └─"),             // For clients (last product)
        Type4("     └─", "     ├─");       // For tarification types (last client)

        fun get(isLast: Boolean): String = if (isLast) lastItem else normalItem
    }
}
