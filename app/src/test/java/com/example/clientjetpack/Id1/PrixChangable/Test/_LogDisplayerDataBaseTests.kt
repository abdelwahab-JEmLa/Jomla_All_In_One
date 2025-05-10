package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import java.util.Calendar

@ExperimentalCoroutinesApi
class _TestsDisplayerLogDataBase {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: _TarificationViewModel
    private lateinit var tarificationRepo: TarificationDataBaseFacileEntre_RepositoryImp

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tarificationRepo = TarificationDataBaseFacileEntre_RepositoryImp()
        viewModel = _TarificationViewModel(tarificationRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun SepareReferentialDataBases() = runTest {
        try {
            val name = "A_DataBasesSepareReferential"

            println("======== TESTING $name TRANSACTIONS ========")

            // Advance the dispatcher to ensure coroutines complete
            testDispatcher.scheduler.advanceUntilIdle()

            mainLog(viewModel.imbriquantFlow.value)

            assertTrue(true)
            println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")

        } catch (e: Exception) {
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }



    enum class TreePrefix(private val lastItem: String, private val normalItem: String) {
        Type1("└─", "├─"),                 // For products
        Type2("  ├─", "  ├─"),             // For clients (not last product)
        Type3("  └─", "  └─"),             // For clients (last product)
        Type4("       ", "  │     ");      // For special spacing cases

        fun get(isLast: Boolean): String = if (isLast) lastItem else normalItem

        companion object {
            fun getNestedPrefix(isLast: Boolean): String = "  │     ${if (isLast) "└─" else "├─"}"
            fun getDeepNestedPrefix(isLast: Boolean): String = "          ${if (isLast) "└─" else "├─"}"
            fun getDeepNestedBranchPrefix(isLast: Boolean): String = "  │     │  ${if (isLast) "└─" else "├─"}"

            // New method specifically for the last client of the last product
            fun getLastClientPrefix(isLast: Boolean): String = "        ${if (isLast) "└─" else "├─"}"
        }
    }

    private fun mainLog(value: A_DataBase_Imbricant) {
        println("\n-- Hierarchical Structure --")

        println("Database (${value.produits.size} products):")

        value.produits.forEachIndexed { produitIndex, produit ->
            val isLastProduit = produitIndex == value.produits.size - 1
            val produitPrefix = TreePrefix.Type1.get(isLastProduit)

            val (produitDate, produitTime) = strDateEtTempFromVidTimestamp(produit.vidTimestamp)

            //Header
            println("$produitPrefix Product ID: ${produit.id}, Date: $produitDate Time: $produitTime (${produit.clients.size} clients)")

            logClients(produit.clients, isLastProduit)
        }
    }

    private fun logClients(
        clients: List<A_DataBase_Imbricant.Produit.Client>,
        isLastProduit: Boolean
    ) {
        clients.forEachIndexed { clientIndex, client ->
            val isLastClient = clientIndex == clients.size - 1
            val clientPrefix = if (isLastProduit) TreePrefix.Type3.get(isLastClient) else TreePrefix.Type2.get(isLastClient)

            val (clientDate, clientTime) = strDateEtTempFromVidTimestamp(client.vidTimestamp)

            println("$clientPrefix Client ID: ${client.id}, Date: $clientDate Time: $clientTime (${client.typeTarification.size} tarification types)")

            logTarificationTypes(client.typeTarification, isLastProduit, isLastClient)
        }
    }

    private fun logTarificationTypes(
        types: List<A_DataBase_Imbricant.Produit.Client.TypeTarification>,
        isLastProduit: Boolean,
        isLastClient: Boolean
    ) {
        types.forEachIndexed { typeIndex, type ->
            val isLastType = typeIndex == types.size - 1

            // This is the key change - special handling for the last client of the last product
            val typePrefix = if (isLastProduit && isLastClient) {
                // Use special spacing for the last client of the last product
                TreePrefix.getLastClientPrefix(isLastType)
            } else {
                // Use standard spacing for other cases
                TreePrefix.getNestedPrefix(isLastType)
            }

            val (typeDate, typeTime) = strDateEtTempFromVidTimestamp(type.vidTimestamp)

            println("$typePrefix Tarification Type ID: ${type.id}, Date: $typeDate Time: $typeTime (${type.PrixsCurrency.size} currencies)")

            logPrixCurrencies(type.PrixsCurrency, isLastProduit, isLastClient, isLastType)
        }
    }

    private fun logPrixCurrencies(
        currencies: List<A_DataBase_Imbricant.Produit.Client.TypeTarification.Prix>,
        isLastProduit: Boolean,
        isLastClient: Boolean,
        isLastType: Boolean
    ) {
        currencies.forEachIndexed { currencyIndex, currency ->
            val isLastCurrency = currencyIndex == currencies.size - 1

            // Adjust prefix based on position in the tree
            val currencyPrefix = if (isLastProduit && isLastClient) {
                // Special spacing for the last client of the last product
                "          ${if (isLastCurrency) "└─" else "├─"}"
            } else {
                // Standard spacing for other cases
                "  │     │  ${if (isLastCurrency) "└─" else "├─"}"
            }

            val (currencyDate, currencyTime) = strDateEtTempFromVidTimestamp(currency.vidTimestamp)

            println("$currencyPrefix Currency: ${currency.valeur}, Date: $currencyDate Time: $currencyTime")
        }
    }
    private fun logTarificationTypes(
        types: List<A_DataBase_Imbricant.Produit.Client.TypeTarification>,
        isLastProduit: Boolean,
        isLastClient: Boolean
    ) {
        types.forEachIndexed { typeIndex, type ->
            val isLastType = typeIndex == types.size - 1

            // Special case for the last client of the last product
            val typePrefix = if (isLastProduit && isLastClient) {
                "        ${if (isLastType) "└─" else "├─"}"
            } else {
                "  │     ${if (isLastType) "└─" else "├─"}"
            }

            val (typeDate, typeTime) = strDateEtTempFromVidTimestamp(type.vidTimestamp)

            println("$typePrefix Tarification Type ID: ${type.id}, Date: $typeDate Time: $typeTime (${type.PrixsCurrency.size} currencies)")

            logPrixCurrencies(type.PrixsCurrency, isLastProduit, isLastClient, isLastType)
        }
    }


    private fun strDateEtTempFromVidTimestamp(timestamp: Long): Pair<String, String> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        val date = "${calendar.get(Calendar.YEAR)}-" +
                "${(calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}-" +
                calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')

        val time = "${calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}:" +
                "${calendar.get(Calendar.MINUTE).toString().padStart(2, '0')}:" +
                calendar.get(Calendar.SECOND).toString().padStart(2, '0')

        return Pair(date, time)
    }
}
