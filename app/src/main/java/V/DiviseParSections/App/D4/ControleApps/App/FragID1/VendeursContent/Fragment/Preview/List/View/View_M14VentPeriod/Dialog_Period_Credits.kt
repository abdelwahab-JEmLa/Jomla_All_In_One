package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A.TransactionItem
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A.saveTransactionToFirebase
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A.checkAndDownloadAllImages
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A.checkAndDownloadImage
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A.ImageViewDialog
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun Dialog_Period_Credits(
    ventPeriod: M14VentPeriode,
    repositorysMainGetter: RepositorysMainGetter,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State variables
    var transactionItems by remember { mutableStateOf<List<TransactionItem>>(emptyList()) }
    var versementItems by remember { mutableStateOf<List<VersementItem>>(emptyList()) }
    var grossistMap by remember { mutableStateOf<Map<String, M15Grossist>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var totalCredit by remember { mutableStateOf(0.0) }
    var totalVersement by remember { mutableStateOf(0.0) }
    var showFilterOptions by remember { mutableStateOf(false) }
    var selectedGrossistFilter by remember { mutableStateOf<String?>(null) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Get period timestamp range
    val periodRange = remember(ventPeriod.keyID) {
        getPeriodTimestampRange(ventPeriod.keyID, repositorysMainGetter)
    }

    // Load grossists map
    LaunchedEffect(Unit) {
        grossistMap = repositorysMainGetter.repo15Grossist.datasValue
            .associateBy { it.keyID }
    }

    // Load transactions and versements for this period
    LaunchedEffect(ventPeriod.keyID, periodRange) {
        if (periodRange != null) {        //->
            //TODO(FIXME):Fix erreur Unresolved reference: loadPeriodTransactions
            loadPeriodTransactions(
                periodStartTimestamp = periodRange.first,
                periodEndTimestamp = periodRange.second,
                ventPeriodKeyID = ventPeriod.keyID,
                repositorysMainGetter = repositorysMainGetter,
                onTransactionsLoaded = { transactions, versements ->
                    transactionItems = transactions
                    versementItems = versements
                    totalCredit = transactions.sumOf { it.credit }
                    totalVersement = versements.sumOf { it.versement }
                    isLoading = false
                },
                onError = { error ->
                    errorMessage = error
                    isLoading = false
                }
            )
        } else {
            errorMessage = "Impossible de déterminer la période"
            isLoading = false
        }
    }

    // Filter transactions and versements based on selected grossist
    val filteredTransactions = remember(transactionItems, selectedGrossistFilter) {
        if (selectedGrossistFilter != null) {
            transactionItems.filter { it.parent_GrossistKeyID == selectedGrossistFilter }
        } else {
            transactionItems
        }
    }

    val filteredVersements = remember(versementItems, selectedGrossistFilter) {
        if (selectedGrossistFilter != null) {
            versementItems.filter { it.parent_GrossistKeyID == selectedGrossistFilter }
        } else {
            versementItems
        }
    }

    // Calculate filtered totals
    val filteredTotalCredit = filteredTransactions.sumOf { it.credit }
    val filteredTotalVersement = filteredVersements.sumOf { it.versement }
    val filteredBalance = filteredTotalCredit - filteredTotalVersement

    // Get unique grossists from transactions
    val availableGrossists = remember(transactionItems, versementItems, grossistMap) {
        val grossistIds = (transactionItems.map { it.parent_GrossistKeyID } +
                versementItems.map { it.parent_GrossistKeyID }).distinct()
        grossistIds.mapNotNull { grossistMap[it] }.sortedBy { it.nom }
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
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Crédits de la Période",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = ventPeriod.get_DebugInfos(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row {
                        IconButton(onClick = { showFilterOptions = !showFilterOptions }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filtres",
                                tint = if (selectedGrossistFilter != null)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Clear, contentDescription = "Fermer")
                        }
                    }
                }

                // Filter dropdown
                if (showFilterOptions) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Filtrer par grossiste:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // All grossists option
                            FilterChip(
                                selected = selectedGrossistFilter == null,
                                onClick = { selectedGrossistFilter = null },
                                label = { Text("Tous les grossistes") },
                                modifier = Modifier.padding(end = 8.dp, bottom = 4.dp)
                            )

                            // Individual grossists
                            availableGrossists.chunked(2).forEach { rowGrossists ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    rowGrossists.forEach { grossist ->
                                        FilterChip(
                                            selected = selectedGrossistFilter == grossist.keyID,
                                            onClick = {
                                                selectedGrossistFilter = if (selectedGrossistFilter == grossist.keyID) {
                                                    null
                                                } else {
                                                    grossist.keyID
                                                }
                                            },
                                            label = { Text(grossist.nom) },
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                    }
                                    // Fill remaining space if odd number
                                    if (rowGrossists.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Total Credits Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = "Crédits",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Crédits",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${String.format("%.2f", filteredTotalCredit)} DA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Total Versements Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = "Versements",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Versements",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "${String.format("%.2f", filteredTotalVersement)} DA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Balance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (filteredBalance >= 0)
                            MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Balance: ${String.format("%.2f", filteredBalance)} DA",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (filteredBalance >= 0)
                                MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Content
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Chargement des transactions...")
                            }
                        }
                    }

                    errorMessage != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Receipt,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Erreur: $errorMessage",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    filteredTransactions.isEmpty() && filteredVersements.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Receipt,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (selectedGrossistFilter != null) {
                                        "Aucune transaction pour le grossiste sélectionné"
                                    } else {
                                        "Aucune transaction pour cette période"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    else -> {
                        // Transactions List
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Combine and sort all items by timestamp (most recent first)
                            val allItems = (filteredTransactions.map { "credit" to it } +
                                    filteredVersements.map { "versement" to it })
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
                                        val grossist = grossistMap[transactionItem.parent_GrossistKeyID]

                                        PeriodTransactionCard(
                                            transaction = transactionItem,
                                            grossist = grossist,
                                            onUpdateItem = { updatedItem ->
                                                transactionItems = transactionItems.map {
                                                    if (it.id == updatedItem.id) updatedItem else it
                                                }
                                                saveTransactionToFirebase(updatedItem)
                                            }
                                        )
                                    }
                                    "versement" -> {
                                        val versementItem = item as VersementItem
                                        val grossist = grossistMap[versementItem.parent_GrossistKeyID]

                                        PeriodVersementCard(
                                            versement = versementItem,
                                            grossist = grossist
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Fermer")
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodTransactionCard(
    transaction: TransactionItem,
    grossist: M15Grossist?,
    onUpdateItem: (TransactionItem) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showImageDialog by remember { mutableStateOf(false) }
    var showImageDropdown by remember { mutableStateOf(false) }
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    var isDownloadingImage by remember { mutableStateOf(false) }

    // Get all available image paths
    val availableImages = remember(transaction) {
        listOfNotNull(
            transaction.receiptImagePath,
            transaction.receiptImage2Path,
            transaction.receiptImage3Path,
            transaction.receiptImage4Path
        ).filter { File(it).exists() }
    }

    LaunchedEffect(transaction.receiptImagePath, transaction.receiptImage2Path, transaction.receiptImage3Path, transaction.receiptImage4Path, transaction.firebaseStoragePath) {
        checkAndDownloadAllImages(
            item = transaction,
            onImagesReady = { /* handled in availableImages computation */ },
            onDownloadStart = { isDownloadingImage = true },
            onDownloadEnd = { isDownloadingImage = false }
        )
    }

    // Dialog pour afficher l'image
    if (showImageDialog && selectedImagePath != null) {
        ImageViewDialog(
            imagePath = selectedImagePath!!,
            onDismiss = {
                showImageDialog = false
                selectedImagePath = null
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        ),
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
                    text = grossist?.nom ?: "Grossiste inconnu",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Crédit ajouté",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = "${transaction.date} à ${transaction.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${String.format("%.2f", transaction.credit)} DA",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Show download indicator
                if (isDownloadingImage) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Téléchargement...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                // Show clickable image indicator if receipt images exist
                else if (availableImages.isNotEmpty()) {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.clickable {
                                if (availableImages.size == 1) {
                                    selectedImagePath = availableImages.first()
                                    showImageDialog = true
                                } else {
                                    showImageDropdown = true
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = "Photo du reçu",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = if (availableImages.size > 1) "Photos (${availableImages.size})" else "Photo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            if (availableImages.size > 1) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Plus d'images",
                                    modifier = Modifier.size(10.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showImageDropdown,
                            onDismissRequest = { showImageDropdown = false }
                        ) {
                            availableImages.forEachIndexed { index, imagePath ->
                                DropdownMenuItem(
                                    text = { Text("Image ${index + 1}") },
                                    onClick = {
                                        selectedImagePath = imagePath
                                        showImageDialog = true
                                        showImageDropdown = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Image,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                // Show download button if image is only in Firebase
                else if (transaction.firebaseStoragePath != null && !isDownloadingImage) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.clickable {
                            scope.launch {
                                checkAndDownloadImage(
                                    item = transaction,
                                    onImageReady = { imagePath ->
                                        if (imagePath != null) {
                                            // Image downloaded successfully
                                        }
                                    },
                                    onDownloadStart = { isDownloadingImage = true },
                                    onDownloadEnd = { isDownloadingImage = false }
                                )
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = "Télécharger photo",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Télécharger",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodVersementCard(
    versement: VersementItem,
    grossist: M15Grossist?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
        ),
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
                    text = grossist?.nom ?: "Grossiste inconnu",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Versement effectué",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = "${versement.date} à ${versement.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "-${String.format("%.2f", versement.versement)} DA",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// Data class for VersementItem
data class VersementItem(
    val id: String = "",
    val parent_GrossistKeyID: String = "",
    val versement: Double = 0.0,
    val date: String = "",
    val time: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
