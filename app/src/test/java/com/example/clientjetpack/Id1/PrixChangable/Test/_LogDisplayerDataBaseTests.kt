package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Z_Passive.strDateFromVidTimestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
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

    private lateinit var viewModel: B_TarificationViewModel
    private lateinit var tarificationRepo: TarificationDataBaseFacileEntre_RepositoryImp

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tarificationRepo = TarificationDataBaseFacileEntre_RepositoryImp()
        viewModel = B_TarificationViewModel(tarificationRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun SepareReferentialDataBases() {
        try {
            val name = "A_DataBasesSepareReferential"

            println("======== TESTING $name TRANSACTIONS ========")
            mainLog(viewModel.imbriquantFlow.value)

            assertTrue(true)
            println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")

        } catch (e: Exception) {
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }

    private fun mainLog(value: A_DataBase_Imbricant) {
        println("\n-- Hierarchical Structure --")

        println("Database (${value.produits.size} products):")

        value.produits.forEachIndexed { produitIndex, produit ->
            val isLastProduit = produitIndex == value.produits.size - 1
            val produitPrefix = TreePrefix.Type1.get(isLastProduit)

            val produitDate = strDateFromVidTimestamp(produit.vidTimestamp)

            //Header
            println("$produitPrefix Product ID: ${produit.id}, Date: $produitDate (${produit.clients.size} clients)")

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

            val clientDate = strDateFromVidTimestamp(client.vidTimestamp)

            println("$clientPrefix Client ID: ${client.id}, Date: $clientDate (${client.typeTarification.size} tarification types)")

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
            val typePrefix = if (isLastProduit && isLastClient) {
                TreePrefix.Type4.get(isLastType)
            } else {
                TreePrefix.getNestedPrefix(isLastType)
            }

            val typeDate = strDateFromVidTimestamp(type.vidTimestamp)

            println("$typePrefix Tarification Type ID: ${type.id}, Date: $typeDate (${type.PrixsCurrency.size} currencies)")

            logPrixCurrencies(type.PrixsCurrency, isLastProduit, isLastClient, isLastType)
        }
    }

    private fun logPrixCurrencies(
        currencies: List<A_DataBase_Imbricant.Produit.Client.TypeTarification.PrixCurrency>,
        isLastProduit: Boolean,
        isLastClient: Boolean,
        isLastType: Boolean
    ) {
        currencies.forEachIndexed { currencyIndex, currency ->
            val isLastCurrency = currencyIndex == currencies.size - 1
            val currencyPrefix = when {
                isLastProduit && isLastClient && isLastType -> TreePrefix.getDeepNestedPrefix(isLastCurrency)
                else -> TreePrefix.getDeepNestedBranchPrefix(isLastCurrency)
            }

            val currencyDate = strDateFromVidTimestamp(currency.vidTimestamp)

            println("$currencyPrefix Currency: ${currency.currency}, Date: $currencyDate")
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
        }
    }
}
