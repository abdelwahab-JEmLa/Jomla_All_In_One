/*// TransactionTests.kt - FULLY FIXED
package com.example.clientjetpack.Sementics.Test

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.AppSemantics
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ClientTransactionsList
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasSemantics  //->
//TODO(FIXME):Fix erreur Unresolved reference: hasSemantics
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTransactionsForSpecificClient() {
        composeTestRule.setContent {
            ClientTransactionsList(clientId = "client_123")
        }

        // Vérifier que toutes les transactions affichées sont bien pour client_123
        composeTestRule
            .onAllNodes(hasSemantics {
                this[AppSemantics.ClientIdKey] == "client_123"
            })
            .assertCountEquals(5) // On s'attend à 5 transactions
    }

    @Test
    fun testOnlyPaymentTransactions() {
        composeTestRule.setContent {
            ClientTransactionsList(clientId = "client_123")
        }

        // Compter seulement les paiements
        val paymentNodes = composeTestRule
            .onAllNodes(hasSemantics {
                this[AppSemantics.TransactionTypeKey] == "payment"
            })

        // Vérifier qu'il y a au moins 1 paiement
        paymentNodes.assertCountEquals(3) // Based on mock data: 3 payments
    }

    @Test
    fun testHighAmountTransactions() {
        composeTestRule.setContent {
            ClientTransactionsList(clientId = "client_123")
        }

        // Trouver les transactions > 100€
        val highAmountNodes = composeTestRule
            .onAllNodes(hasSemantics {
                val amount = this.getOrElse(AppSemantics.AmountKey) { 0.0 }
                amount > 100.0
            })

        // Vérifier qu'il y a au moins 1 transaction > 100€
        assertTrue("Il devrait y avoir au moins 1 transaction > 100€",
            try {
                highAmountNodes.assertCountEquals(0)
                false // If assertion passes, there are 0 transactions > 100€
            } catch (e: AssertionError) {
                true // If assertion fails, there are transactions > 100€
            }
        )
    }

    @Test
    fun testClickOnSpecificTransaction() {
        composeTestRule.setContent {
            ClientTransactionsList(clientId = "client_123")
        }

        // Cliquer sur une transaction spécifique
        composeTestRule
            .onNode(hasSemantics {
                this[AppSemantics.TransactionIdKey] == "txn_456"
            })
            .performClick()

        // Note: Add assertions here for what should happen after click
        // For example, check if a dialog opens, navigation occurs, etc.
    }
}
              */
