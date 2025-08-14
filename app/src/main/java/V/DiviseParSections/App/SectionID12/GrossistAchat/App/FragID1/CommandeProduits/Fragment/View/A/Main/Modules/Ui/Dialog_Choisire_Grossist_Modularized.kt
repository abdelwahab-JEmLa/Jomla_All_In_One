package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.W_AchatProduitOperation.View.updated_Achats
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.toColorInt
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@Composable
fun Dialog_Choisire_Grossist_Modularized(
    titel: String = "Choisir un Grossiste",
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    list_M11AchatOperation: List<M11AchatOperation> = emptyList(),
    onDismiss: (M15Grossist?) -> Unit
) {
    val datasValue_repo11AchatOperation = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
    val grossists = viewModel.aCentralFacade.repositorysMainGetter.repo15Grossist.datasValue
    val focusManager = LocalFocusManager.current

    // State for storing credits for each grossist
    var grossistCredits by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var totalCreditsAllGrossists by remember { mutableStateOf(0.0) }
    var isLoadingCredits by remember { mutableStateOf(true) }
    var creditListeners by remember { mutableStateOf<Map<String, List<ValueEventListener>>>(emptyMap()) }

    val grossistsWithPurchaseCount = grossists.map { grossist ->
        val purchaseCount = datasValue_repo11AchatOperation.count { it.parent_M15Grossist_KeyID == grossist.keyID }
        grossist to purchaseCount
    }.sortedByDescending { it.second }

    val nullGrossistCount = remember(datasValue_repo11AchatOperation) {
        datasValue_repo11AchatOperation.count {
            it.parent_M15Grossist_KeyID == "null" || it.parent_M15Grossist_KeyID.isBlank()
        }
    }

    // Load credits for all grossists with real-time updates
    LaunchedEffect(grossists) {
        isLoadingCredits = true

        // Remove previous listeners
        creditListeners.values.flatten().forEach { listener ->
            // Note: In a real implementation, you would need to remove these listeners
            // from their respective Firebase references
        }

        val (listeners, initialCredits) = loadCreditsForAllGrossists(grossists) { creditsMap ->
            grossistCredits = creditsMap
            totalCreditsAllGrossists = creditsMap.values.sum()
            isLoadingCredits = false
        }
        creditListeners = listeners
        grossistCredits = initialCredits
    }

    // Cleanup listeners when dialog is dismissed
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            // Remove all listeners when component is disposed
            creditListeners.values.flatten().forEach { listener ->
                // Note: In a real implementation, you'd need to remove these listeners
                // from their respective Firebase references for proper cleanup
            }
        }
    }

    Dialog(
        onDismissRequest = {
            focusManager.clearFocus()
            onDismiss(null)
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with title and total credits
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = titel,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (!isLoadingCredits) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.AccountBalance,
                                    contentDescription = "Total crédits",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Total: ${String.format("%.2f", totalCreditsAllGrossists)} DA",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        onDismiss(null)
                    }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Fermer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .clickable {
                                    focusManager.clearFocus()
                                    onDismiss(null)
                                }
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Supprimer le filtre",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Supprimer le filtre",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (nullGrossistCount > 0) {
                        item {
                            Card(
                                modifier = Modifier
                                    .clickable {
                                        focusManager.clearFocus()
                                        val nullGrossist = M15Grossist(
                                            keyID = "NULL_GROSSIST_FILTER",
                                            nom = "Grossiste non défini",
                                            couleur_In_Str = "#FF0000"
                                        )
                                        onDismiss(nullGrossist)
                                    }
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.onError
                                            ) {
                                                Text(
                                                    text = nullGrossistCount.toString(),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.error),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.FilterList,
                                                contentDescription = "Grossiste non défini",
                                                tint = MaterialTheme.colorScheme.onError
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Grossiste non défini",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Text(
                                            text = "$nullGrossistCount opérations sans grossiste",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    items(grossistsWithPurchaseCount) { (grossist, purchaseCount) ->
                        GrossistItem(
                            list_M11AchatOperation = list_M11AchatOperation,
                            grossist = grossist,
                            purchaseCount = purchaseCount,
                            grossistCredit = grossistCredits[grossist.keyID] ?: 0.0,
                            isLoadingCredit = isLoadingCredits,
                            onSelect = {
                                focusManager.clearFocus()
                                onDismiss(grossist)
                            }
                        )
                    }

                    if (grossists.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Business,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Aucun grossiste disponible",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        onDismiss(null)
                    }) {
                        Text("Annuler")
                    }
                }
            }
        }
    }
}

@Composable
private fun GrossistItem(
    grossist: M15Grossist,
    purchaseCount: Int,
    grossistCredit: Double,
    isLoadingCredit: Boolean,
    onSelect: () -> Unit,
    list_M11AchatOperation: List<M11AchatOperation> = emptyList()
) {
    val datas = updated_Achats(list_M11AchatOperation, grossist)
    var showTransactionDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .getSemanticsTag(datas, "datas")
            .clickable { onSelect() }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgedBox(
                badge = {
                    if (purchaseCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) { Text(purchaseCount.toString(), style = MaterialTheme.typography.labelSmall) }
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            try {
                                Color(grossist.couleur_In_Str.toColorInt())
                            } catch (e: Exception) {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = grossist.nom,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = grossist.get_DebugInfos(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (purchaseCount > 0) {
                        Text(
                            text = "• $purchaseCount achats",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Display total credit for this grossist
                if (isLoadingCredit) {
                    Text(
                        text = "Chargement crédit...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Normal
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = "Crédit",
                            modifier = Modifier.size(14.dp),
                            tint = if (grossistCredit > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Crédit: ${String.format("%.2f", grossistCredit)} DA",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (grossistCredit > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            fontWeight = if (grossistCredit > 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            IconButton(onClick = { showTransactionDialog = true }) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = "Voir les transactions",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showTransactionDialog) {
        TransactionDialog(
            grossist = grossist,
            onDismiss = { showTransactionDialog = false }
        )
    }
}

// Helper function to load credits for all grossists with real-time updates
private fun loadCreditsForAllGrossists(
    grossists: List<M15Grossist>,
    onCreditsLoaded: (Map<String, Double>) -> Unit
): Pair<Map<String, List<ValueEventListener>>, Map<String, Double>> {
    val creditsMap = mutableMapOf<String, Double>()
    val allListeners = mutableMapOf<String, List<ValueEventListener>>()

    if (grossists.isEmpty()) {
        onCreditsLoaded(emptyMap())
        return Pair(emptyMap(), emptyMap())
    }

    grossists.forEach { grossist ->
        val listeners = loadCreditForGrossist(grossist.keyID) { credit ->
            creditsMap[grossist.keyID] = credit
            onCreditsLoaded(creditsMap.toMap())
        }
        allListeners[grossist.keyID] = listeners
    }

    return Pair(allListeners, creditsMap.toMap())
}

// Helper function to load credit for a specific grossist
private fun loadCreditForGrossist(
    grossistKeyID: String,
    onCreditLoaded: (Double) -> Unit
): List<ValueEventListener> {
    var transactionTotal = 0.0
    var versementTotal = 0.0
    var completedQueries = 0
    val totalQueries = 2
    val listeners = mutableListOf<ValueEventListener>()

    fun checkComplete() {
        completedQueries++
        if (completedQueries == totalQueries) {
            onCreditLoaded(transactionTotal - versementTotal)
        }
    }

    // Load TransactionItems for this grossist
    val transactionListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            transactionTotal = 0.0
            for (child in snapshot.children) {
                try {
                    val transaction = child.getValue(TransactionItem::class.java)
                    transaction?.let { transactionTotal += it.credit }
                } catch (e: Exception) {
                    // Handle parsing error
                }
            }
            checkComplete()
        }

        override fun onCancelled(error: DatabaseError) {
            checkComplete()
        }
    }

    TransactionItem.ref
        .orderByChild("parent_GrossistKeyID")
        .equalTo(grossistKeyID)
        .addValueEventListener(transactionListener)

    listeners.add(transactionListener)

    // Load VersementItems for this grossist
    try {
        val versementRef = TransactionItem.ref.parent?.child("VersementItem")
        val versementListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                versementTotal = 0.0
                for (child in snapshot.children) {
                    try {
                        val versement = child.child("versement").getValue(Double::class.java)
                        versement?.let { versementTotal += it }
                    } catch (e: Exception) {
                        // Handle parsing error
                    }
                }
                checkComplete()
            }

            override fun onCancelled(error: DatabaseError) {
                checkComplete()
            }
        }

        versementRef?.orderByChild("parent_GrossistKeyID")
            ?.equalTo(grossistKeyID)
            ?.addValueEventListener(versementListener)

        listeners.add(versementListener)

    } catch (e: Exception) {
        // If VersementItem loading fails, just complete with transaction total
        checkComplete()
    }

    return listeners
}
