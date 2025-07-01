package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test

import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag

@Composable
fun ClientCard(client: Client) {
    val ClientKey = SemanticsPropertyKey<String>("ClientKey")

    Card(
        modifier = Modifier.semantics {
            contentDescription = "Carte du client ${client.name}"
            testTag = "client_card"
            set(ClientKey, client.id) // Info personnalisée
        }
    ) {
        Text("${client.name}")
        Text("Commandes: ${client.orderCount}")
    }
}
