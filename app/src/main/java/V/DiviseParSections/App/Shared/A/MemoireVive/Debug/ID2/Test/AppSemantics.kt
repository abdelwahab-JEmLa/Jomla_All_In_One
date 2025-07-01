package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

object AppSemantics {
    val ClientIdKey = SemanticsPropertyKey<String>("ClientId")
    val TransactionIdKey = SemanticsPropertyKey<String>("TransactionId")
    val TransactionTypeKey = SemanticsPropertyKey<String>("TransactionType")
    val AmountKey = SemanticsPropertyKey<Double>("Amount")
    val IsSelectedKey = SemanticsPropertyKey<Boolean>("IsSelected")
    val LastClickedKey = SemanticsPropertyKey<String>("LastClicked")
}

// Data classes needed for the example
data class Transaction(
    val id: String,
    val type: String,
    val amount: Double,
    val date: String
)


// Mock function - replace with your actual data source
fun getTransactionsForClient(clientId: String): List<Transaction> {
    return listOf(
        Transaction("txn_001", "payment", 150.0, "2024-01-01"),
        Transaction("txn_002", "refund", 75.0, "2024-01-02"),
        Transaction("txn_456", "payment", 200.0, "2024-01-03"),
        Transaction("txn_004", "payment", 50.0, "2024-01-04"),
        Transaction("txn_005", "charge", 125.0, "2024-01-05")
    )
}

@Composable
fun TransactionItem(transaction: Transaction, clientId: String) {
    val ClientIdKey = SemanticsPropertyKey<String>("ClientId")

    var isSelected by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableStateOf("") }

    val handleClick = {
        // Mettre à jour les valeurs des semantics keys au click
        isSelected = !isSelected
        lastClickTime = System.currentTimeMillis().toString()

        println("Transaction cliquée: ${transaction.id}")
        println("Nouvelle valeur IsSelected: $isSelected")
        println("Timestamp du click: $lastClickTime")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { handleClick() }  // Ajouter clickable pour rendre l'item cliquable
            .semantics {
                set(ClientIdKey, clientId)
                set(AppSemantics.IsSelectedKey, isSelected)

                onClick {
                    isSelected=false
                    true
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${transaction.type}")
            Text("${transaction.amount}€")
            Text("${transaction.date}")
            if (isSelected) {
                Text("✓ Sélectionné", color = androidx.compose.ui.graphics.Color.Green)
            }
        }
    }

    // TODO(1): Perform click quand tout s'affiche
    LaunchedEffect(Unit) {
        // Simuler un click automatique après affichage (par exemple pour la première transaction)
        if (transaction.id == "txn_001") {
            kotlinx.coroutines.delay(1000) // Attendre 1 seconde
            handleClick()
            println("Auto-click déclenché pour transaction: ${transaction.id}")
        }
    }
}

@Composable
fun ClientTransactionsList(clientId: String) {
    val transactions = getTransactionsForClient(clientId)

    LazyColumn {
        items(transactions) { transaction ->
            TransactionItem(
                transaction = transaction,
                clientId = clientId
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClientTransactionsListPreview() {
    ClientTransactionsList(clientId = "client_123")
}
