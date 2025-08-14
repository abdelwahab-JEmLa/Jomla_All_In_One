package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.centralRef
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
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class TransactionItem(
    val id: String = generePushKey(),
    val parent_GrossistKeyID: String = "",
    val credit: Double = 0.0,
    val date: String = "",
    val time: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val receiptImagePath: String? = null,
    val firebaseStoragePath: String? = null // Firebase Storage path for the receipt image
) {
    companion object {
        val ref = centralRef.child("TransactionItem")
        fun generePushKey() = ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
    }
}

private data class VersementItem(
    val id: String = generePushKey(),
    val parent_GrossistKeyID: String = "",
    val versement: Double = 0.0,
    val date: String = "",
    val time: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        val ref = centralRef.child("VersementItem")
        fun generePushKey() = ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
    }
}

@Composable
fun TransactionDialog(
    grossist: M15Grossist,
    onDismiss: () -> Unit
) {
    var creditText by remember { mutableStateOf("") }
    var versementText by remember { mutableStateOf("") }
    var transactionItems by remember { mutableStateOf<List<TransactionItem>>(emptyList()) }
    var versementItems by remember { mutableStateOf<List<VersementItem>>(emptyList()) }
    var totalSum by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    // Load existing transactions for this grossist
    LaunchedEffect(grossist.keyID) {
        loadTransactionsForGrossist(grossist.keyID) { transactions, versements ->
            transactionItems = transactions
            versementItems = versements
            totalSum = transactions.sumOf { it.credit } - versements.sumOf { it.versement }
            isLoading = false
        }
    }

    fun addCredit() {
        if (creditText.isNotBlank()) {
            try {
                val credit = creditText.toDouble()
                val now = System.currentTimeMillis()
                val newTransaction = TransactionItem(
                    parent_GrossistKeyID = grossist.keyID,
                    credit = credit,
                    date = dateFormat.format(Date(now)),
                    time = timeFormat.format(Date(now)),
                    timestamp = now
                )

                // Add to local list immediately
                transactionItems = transactionItems + newTransaction
                totalSum += credit
                creditText = ""

                // Save to Firebase
                saveTransactionToFirebase(newTransaction)

            } catch (e: NumberFormatException) {
                // Handle invalid number input
            }
        }
    }

    fun addVersement() {
        if (versementText.isNotBlank()) {
            try {
                val versement = versementText.toDouble()
                val now = System.currentTimeMillis()
                val newVersement = VersementItem(
                    parent_GrossistKeyID = grossist.keyID,
                    versement = versement,
                    date = dateFormat.format(Date(now)),
                    time = timeFormat.format(Date(now)),
                    timestamp = now
                )

                // Add to local list immediately
                versementItems = versementItems + newVersement
                totalSum -= versement
                versementText = ""

                // Save to Firebase
                saveVersementToFirebase(newVersement)

            } catch (e: NumberFormatException) {
                // Handle invalid number input
            }
        }
    }

    fun deleteTransaction(item: TransactionItem) {
        transactionItems = transactionItems.filter { it.id != item.id }
        totalSum -= item.credit
        deleteTransactionFromFirebase(item)
    }

    fun deleteVersement(item: VersementItem) {
        versementItems = versementItems.filter { it.id != item.id }
        totalSum += item.versement
        deleteVersementFromFirebase(item)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
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
                    value = String.format("%.2f DA", totalSum),
                    onValueChange = { },
                    label = { Text("Total des crédits") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = "Crédit total"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = creditText,
                    onValueChange = { creditText = it },
                    label = { Text("Ajouter crédit") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
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
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { addVersement() }),
                    trailingIcon = {
                        IconButton(onClick = { addVersement() }) {
                            Icon(Icons.Default.Remove, contentDescription = "Ajouter versement")
                        }
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.TrendingDown,
                            contentDescription = "Versement",
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.error,
                        focusedLabelColor = MaterialTheme.colorScheme.error,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.error
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Chargement des transactions...")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Sort all items by timestamp (most recent first)
                        val allItems = (transactionItems.map { "credit" to it } +
                                versementItems.map { "versement" to it })
                            .sortedByDescending {
                                when (it.first) {
                                    "credit" -> (it.second as TransactionItem).timestamp
                                    else -> (it.second as VersementItem).timestamp
                                }
                            }

                        items(allItems) { (type, item) ->
                            when (type) {
                                "credit" -> {
                                    val transactionItem = item as TransactionItem
                                    CreditCard(
                                        item = transactionItem,
                                        onDelete = { deleteTransaction(transactionItem) },
                                        onUpdateItem = { updatedItem ->
                                            transactionItems = transactionItems.map {
                                                if (it.id == updatedItem.id) updatedItem else it
                                            }
                                            // Update in Firebase
                                            saveTransactionToFirebase(updatedItem)
                                        }
                                    )
                                }
                                "versement" -> {
                                    val versementItem = item as VersementItem
                                    VersementCard(
                                        item = versementItem,
                                        onDelete = { deleteVersement(versementItem) }
                                    )
                                }
                            }
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
                                        Icon(
                                            Icons.Default.Receipt,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Aucune transaction disponible")
                                    }
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
                    text = "-${String.format("%.2f", item.versement)} DA",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Firebase helper functions
private fun loadTransactionsForGrossist(
    grossistKeyID: String,
    onLoaded: (List<TransactionItem>, List<VersementItem>) -> Unit
) {
    val transactions = mutableListOf<TransactionItem>()
    val versements = mutableListOf<VersementItem>()
    var loadedCount = 0
    val totalToLoad = 2

    fun checkComplete() {
        loadedCount++
        if (loadedCount == totalToLoad) {
            onLoaded(transactions, versements)
        }
    }

    // Load TransactionItems
    TransactionItem.ref
        .orderByChild("parent_GrossistKeyID")
        .equalTo(grossistKeyID)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transactions.clear()
                for (child in snapshot.children) {
                    try {
                        val transaction = child.getValue(TransactionItem::class.java)
                        transaction?.let { transactions.add(it) }
                    } catch (e: Exception) {
                        // Handle parsing error
                    }
                }
                checkComplete()
            }

            override fun onCancelled(error: DatabaseError) {
                checkComplete()
            }
        })

    // Load VersementItems
    VersementItem.ref
        .orderByChild("parent_GrossistKeyID")
        .equalTo(grossistKeyID)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                versements.clear()
                for (child in snapshot.children) {
                    try {
                        val versement = child.getValue(VersementItem::class.java)
                        versement?.let { versements.add(it) }
                    } catch (e: Exception) {
                        // Handle parsing error
                    }
                }
                checkComplete()
            }

            override fun onCancelled(error: DatabaseError) {
                checkComplete()
            }
        })
}

private fun saveTransactionToFirebase(transaction: TransactionItem) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            TransactionItem.ref.child(transaction.id).setValue(transaction)
        } catch (e: Exception) {
            // Handle error
        }
    }
}

private fun saveVersementToFirebase(versement: VersementItem) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            VersementItem.ref.child(versement.id).setValue(versement)
        } catch (e: Exception) {
            // Handle error
        }
    }
}

private fun deleteTransactionFromFirebase(transaction: TransactionItem) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            TransactionItem.ref.child(transaction.id).removeValue()
        } catch (e: Exception) {
            // Handle error
        }
    }
}

private fun deleteVersementFromFirebase(versement: VersementItem) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            VersementItem.ref.child(versement.id).removeValue()
        } catch (e: Exception) {
            // Handle error
        }
    }
}

// Image management functions
suspend fun downloadImageFromFirebase(
    firebaseStoragePath: String,
    localPath: String
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val storageRef = Firebase.storage.reference.child(firebaseStoragePath)
            val localFile = File(localPath)

            // Create parent directories if they don't exist
            localFile.parentFile?.mkdirs()

            // Download the file
            storageRef.getFile(localFile).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}

fun getReceiptImagePath(
    item: TransactionItem,
    onImageReady: (String?) -> Unit
) {
    val localPath = item.receiptImagePath

    // Check if local file exists
    if (localPath != null && File(localPath).exists()) {
        onImageReady(localPath)
        return
    }

    // If local file doesn't exist but Firebase path is available, download it
    val firebasePath = item.firebaseStoragePath
    if (firebasePath != null && localPath != null) {
        CoroutineScope(Dispatchers.IO).launch {
            val success = downloadImageFromFirebase(firebasePath, localPath)
            withContext(Dispatchers.Main) {
                onImageReady(if (success) localPath else null)
            }
        }
    } else {
        onImageReady(null)
    }
}
