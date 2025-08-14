package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui

import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class TransactionItem(
    val id: String = UUID.randomUUID().toString(),
    val credit: Double,
    val date: String,
    val time: String,
    val timestamp: Long = System.currentTimeMillis(),
    val receiptImagePath: String? = null
)

private data class VersementItem(
    val id: String = UUID.randomUUID().toString(),
    val versement: Double,
    val date: String,
    val time: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun TransactionDialog(
    grossist: M15Grossist,
    transactions: List<M11AchatOperation>,
    onDismiss: () -> Unit
) {
    var creditText by remember { mutableStateOf("") }
    var versementText by remember { mutableStateOf("") }
    var transactionItems by remember { mutableStateOf<List<TransactionItem>>(emptyList()) }
    var versementItems by remember { mutableStateOf<List<VersementItem>>(emptyList()) }
    var totalSum by remember { mutableStateOf(0.0) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun addCredit() {
        if (creditText.isNotBlank()) {
            try {
                val credit = creditText.toDouble()
                val now = System.currentTimeMillis()
                transactionItems = transactionItems + TransactionItem(
                    credit = credit,
                    date = dateFormat.format(Date(now)),
                    time = timeFormat.format(Date(now))
                )
                totalSum += credit
                creditText = ""
            } catch (e: NumberFormatException) {}
        }
    }

    fun addVersement() {
        if (versementText.isNotBlank()) {
            try {
                val versement = versementText.toDouble()
                val now = System.currentTimeMillis()
                versementItems = versementItems + VersementItem(
                    versement = versement,
                    date = dateFormat.format(Date(now)),
                    time = timeFormat.format(Date(now))
                )
                totalSum -= versement
                versementText = ""
            } catch (e: NumberFormatException) {}
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions - ${grossist.nom}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Clear, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = totalSum.toString(),
                    onValueChange = { },
                    label = { Text("Total des crédits") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = "Crédit total") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = creditText,
                    onValueChange = { creditText = it },
                    label = { Text("Ajouter crédit") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { addCredit() },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addCredit() }),
                    trailingIcon = {
                        IconButton(onClick = { addCredit() }) {
                            Icon(Icons.Default.Add, contentDescription = "Ajouter transaction")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = versementText,
                    onValueChange = { versementText = it },
                    label = { Text("Ajouter versement") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { addVersement() },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addVersement() }),
                    trailingIcon = {
                        IconButton(onClick = { addVersement() }) {
                            Icon(Icons.Default.Remove, contentDescription = "Ajouter versement")
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.TrendingDown, contentDescription = "Versement", tint = MaterialTheme.colorScheme.error)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.error,
                        focusedLabelColor = MaterialTheme.colorScheme.error,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.error
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactionItems) { item ->
                        CreditCard(
                            item = item,
                            onDelete = {
                                transactionItems = transactionItems.filter { it.id != item.id }
                                totalSum -= item.credit
                            },
                            onUpdateItem = { updatedItem ->
                                transactionItems = transactionItems.map {
                                    if (it.id == updatedItem.id) updatedItem else it
                                }
                            }
                        )
                    }

                    items(versementItems) { item ->
                        VersementCard(
                            item = item,
                            onDelete = {
                                versementItems = versementItems.filter { it.id != item.id }
                                totalSum += item.versement
                            }
                        )
                    }

                    if (transactionItems.isEmpty() && versementItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Aucune transaction disponible")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Fermer") }
                }
            }
        }
    }
}

@Composable
private fun VersementCard(item: VersementItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Versement",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "${item.date} à ${item.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "-${item.versement} DA",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
