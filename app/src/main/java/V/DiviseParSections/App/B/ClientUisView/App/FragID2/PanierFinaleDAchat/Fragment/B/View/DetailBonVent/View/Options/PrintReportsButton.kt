package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

// Data class for PrintPCBackEnd
data class PrintPCBackEnd(
    val id: String = "",
    val commencement_du_job: String = "",
    val end_du_job: String = "",
    val nom_fichier: String = ""
)

// Repository for Firebase operations
class PrintReportsRepository {
    private val database = FirebaseDatabase.getInstance("https://abdelwahab-jemla-com-default-rtdb.europe-west1.firebasedatabase.app/")
    private val printReportsRef = database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/PrintPCBackEnd")
    
    private var currentListener: ValueEventListener? = null
    
    // Real-time listener for Firebase data changes
    fun observePrintReports(onResult: (Result<List<PrintPCBackEnd>>) -> Unit) {
        // Remove existing listener if any
        removeListener()
        
        currentListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val reports = mutableListOf<PrintPCBackEnd>()
                    
                    snapshot.children.forEach { child ->
                        val report = child.getValue(PrintPCBackEnd::class.java)
                        report?.let { reports.add(it) }
                    }
                    
                    // Sort by commencement_du_job in descending order (most recent first)
                    val sortedReports = reports.sortedByDescending { report ->
                        try {
                            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            format.parse(report.commencement_du_job)?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    }
                    
                    onResult(Result.success(sortedReports))
                } catch (exception: Exception) {
                    onResult(Result.failure(exception))
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                onResult(Result.failure(Exception(error.message)))
            }
        }
        M00CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

        printReportsRef.addValueEventListener(currentListener!!)}
    }
    
    fun removeListener() {
        currentListener?.let { listener ->
            printReportsRef.removeEventListener(listener)
            currentListener = null
        }
    }
    
    // Keep the suspend function as backup if needed
    suspend fun fetchPrintReports(): Result<List<PrintPCBackEnd>> {
        return try {
            val snapshot = printReportsRef.get().await()
            val reports = mutableListOf<PrintPCBackEnd>()
            
            snapshot.children.forEach { child ->
                val report = child.getValue(PrintPCBackEnd::class.java)
                report?.let { reports.add(it) }
            }
            
            // Sort by commencement_du_job in descending order (most recent first)
            val sortedReports = reports.sortedByDescending { report ->
                try {
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    format.parse(report.commencement_du_job)?.time ?: 0L
                } catch (e: Exception) {
                    0L
                }
            }
            
            Result.success(sortedReports)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}

// Print Reports Dialog Composable
@Composable
fun PrintReportsDialog(
    uiState: PrintReportsViewModel.PrintReportsUiState,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Rapports d'Impression",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Chargement des rapports...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    uiState.errorMessage != null -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Erreur",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Erreur: ${uiState.errorMessage}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    uiState.printReports.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Aucun rapport",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Aucun rapport d'impression trouvé",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.printReports) { report ->
                                PrintReportItem(report = report)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}
// Updated ViewModel with reload functionality
class PrintReportsViewModel(
    private val repository: PrintReportsRepository = PrintReportsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrintReportsUiState())
    val uiState: StateFlow<PrintReportsUiState> = _uiState.asStateFlow()

    data class PrintReportsUiState(
        val isLoading: Boolean = false,
        val printReports: List<PrintPCBackEnd> = emptyList(),
        val errorMessage: String? = null,
        val isDialogVisible: Boolean = false,
        val lastUpdated: Long = 0L
    )

    fun showDialog() {
        _uiState.value = _uiState.value.copy(isDialogVisible = true)
        fetchPrintReports()
    }

    fun hideDialog() {
        _uiState.value = _uiState.value.copy(isDialogVisible = false)
        repository.removeListener()
    }

    fun reloadData() {
        fetchPrintReports()
    }

    private fun fetchPrintReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.fetchPrintReports()
                .onSuccess { reports ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        printReports = reports,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erreur inconnue"
                    )
                }
        }
    }

    // Alternative method for real-time updates
    fun enableRealTimeUpdates() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        repository.observePrintReports { result ->
            result.onSuccess { reports ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    printReports = reports,
                    lastUpdated = System.currentTimeMillis()
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Erreur inconnue"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }
}

// Updated Print Reports Dialog Composable with reload functionality
@Composable
fun PrintReportsDialog(
    uiState: PrintReportsViewModel.PrintReportsUiState,
    onDismiss: () -> Unit,
    onReload: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rapports d'Impression",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )

                // Reload button in title bar
                IconButton(
                    onClick = onReload,
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualiser",
                        tint = if (uiState.isLoading)
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column {
                // Last updated info
                if (uiState.lastUpdated > 0) {
                    Text(
                        text = "Dernière mise à jour: ${formatLastUpdated(uiState.lastUpdated)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    when {
                        uiState.isLoading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Chargement des rapports...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        uiState.errorMessage != null -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Erreur",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Erreur: ${uiState.errorMessage}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                TextButton(
                                    onClick = onReload
                                ) {
                                    Text("Réessayer")
                                }
                            }
                        }

                        uiState.printReports.isEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "Aucun rapport",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Aucun rapport d'impression trouvé",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                TextButton(
                                    onClick = onReload
                                ) {
                                    Text("Actualiser")
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.printReports) { report ->
                                    PrintReportItem(report = report)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onReload,
                    enabled = !uiState.isLoading
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Actualiser")
                    }
                }

                TextButton(onClick = onDismiss) {
                    Text("Fermer")
                }
            }
        }
    )
}

// Updated PrintReportsButton to pass the reload function
@Composable
fun PrintReportsButton(
    showLabel: Boolean,
    viewModel: PrintReportsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = { viewModel.showDialog() },
            containerColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Assessment,
                contentDescription = "Voir rapports d'impression",
                modifier = Modifier.size(20.dp)
            )
        }

        if (showLabel) {
            Text(
                text = "Rapports",
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    // Show dialog when needed
    if (uiState.isDialogVisible) {
        PrintReportsDialog(
            uiState = uiState,
            onDismiss = { viewModel.hideDialog() },
            onReload = { viewModel.reloadData() }
        )
    }
}

// Helper function to format last updated time
private fun formatLastUpdated(timestamp: Long): String {
    return try {
        val currentTime = System.currentTimeMillis()
        val diffMs = currentTime - timestamp
        val diffSeconds = diffMs / 1000

        when {
            diffSeconds < 60 -> "Il y a ${diffSeconds}s"
            diffSeconds < 3600 -> {
                val minutes = diffSeconds / 60
                "Il y a ${minutes}m"
            }
            diffSeconds < 86400 -> {
                val hours = diffSeconds / 3600
                "Il y a ${hours}h"
            }
            else -> {
                val format = SimpleDateFormat("dd/MM à HH:mm", Locale.getDefault())
                format.format(timestamp)
            }
        }
    } catch (e: Exception) {
        "Inconnue"
    }
}

// Individual Print Report Item with countdown for ongoing jobs
@Composable
fun PrintReportItem(
    report: PrintPCBackEnd,
    modifier: Modifier = Modifier,
    init_countdown:Int = 30
) {
    // State for countdown timer
    var countdown by remember { mutableStateOf(init_countdown) }
    var isJobOngoing by remember { mutableStateOf(report.end_du_job.isEmpty()) }

    // Effect for countdown timer when job is ongoing
    LaunchedEffect(isJobOngoing) {
        if (isJobOngoing) {
            while (countdown > 0 && isJobOngoing) {
                delay(1000) // Wait 1 second

                countdown--
                // Check if job is still ongoing (you might want to refresh data here)
                if (report.end_du_job.isNotEmpty()) {
                    isJobOngoing = false
                }
            }
            // After 30 seconds, assume job is complete or failed
            if (countdown == 0) {
                isJobOngoing = false
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${report.id}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                if (isJobOngoing) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${countdown}s",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Impression",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Fichier:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = report.nom_fichier,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Début:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDateTime(report.commencement_du_job),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Fin:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isJobOngoing) {
                    Text(
                        text = "En cours...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                } else {
                    Text(
                        text = if (report.end_du_job.isEmpty()) "Terminé/Échoué" else formatDateTime(report.end_du_job),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (report.end_du_job.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Calculate and show duration only for completed jobs
            if (!isJobOngoing && report.end_du_job.isNotEmpty()) {
                val duration = calculateDuration(report.commencement_du_job, report.end_du_job)
                if (duration.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Durée:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else if (isJobOngoing) {
                // Show elapsed time for ongoing jobs
                val elapsedTime = calculateElapsedTime(report.commencement_du_job)
                if (elapsedTime.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Temps écoulé:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = elapsedTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Status indicator
            if (isJobOngoing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Statut:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Impression en cours",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Helper function to calculate elapsed time from start
private fun calculateElapsedTime(start: String): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startTime = format.parse(start)
        val currentTime = System.currentTimeMillis()

        if (startTime != null) {
            val elapsedMs = currentTime - startTime.time
            val elapsedSeconds = elapsedMs / 1000

            when {
                elapsedSeconds < 60 -> "${elapsedSeconds}s"
                elapsedSeconds < 3600 -> {
                    val minutes = elapsedSeconds / 60
                    val seconds = elapsedSeconds % 60
                    "${minutes}m ${seconds}s"
                }
                else -> {
                    val hours = elapsedSeconds / 3600
                    val minutes = (elapsedSeconds % 3600) / 60
                    "${hours}h ${minutes}m"
                }
            }
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}

// Helper function to format date time for better display
private fun formatDateTime(dateTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        date?.let { outputFormat.format(it) } ?: dateTime
    } catch (e: Exception) {
        dateTime
    }
}

// Helper function to calculate duration
private fun calculateDuration(start: String, end: String): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startTime = format.parse(start)
        val endTime = format.parse(end)
        
        if (startTime != null && endTime != null) {
            val durationMs = endTime.time - startTime.time
            val durationSeconds = durationMs / 1000
            
            when {
                durationSeconds < 60 -> "${durationSeconds}s"
                durationSeconds < 3600 -> {
                    val minutes = durationSeconds / 60
                    val seconds = durationSeconds % 60
                    "${minutes}m ${seconds}s"
                }
                else -> {
                    val hours = durationSeconds / 3600
                    val minutes = (durationSeconds % 3600) / 60
                    "${hours}h ${minutes}m"
                }
            }
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}

// Updated action buttons list in your main composable
// Add this to your actionButtons list in DetailsBonVent:
/*
ActionButtonData("reports") {
    PrintReportsButton(
        showLabel = !isMinimized
    )
},
*/
