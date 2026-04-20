package P5_DialogeClientsEditer

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import java.util.UUID

private const val TAG = "ClientSelectionDialog"

@Composable
fun ClientSelectionDialog(
    clients: List<M2Client>,
    onClientSelected: (M2Client) -> Unit,
    onDismiss: () -> Unit,
    soldArticle: List<SoldArticlesTabelle?>,
    viewModel: HeadViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddClientDialog by remember { mutableStateOf(false) }

    // Log initial data
    LaunchedEffect(clients, soldArticle) {
        Log.d(TAG, "Initial Data: ${clients.size} clientAchteurs, ${soldArticle.size} sold articles")
        clients.forEach { client ->
            Log.v(TAG, "ClientAchteur ID: ${client.id}, Name: ${client.nom}")
        }
    }

    // Monitor search query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            Log.d(TAG, "Search query updated: $searchQuery")
        }
    }

    if (showAddClientDialog) {
        AddClientDialog(
            onDismiss = {
                Log.d(TAG, "Add client dialog dismissed")
                showAddClientDialog = false
            },
            onConfirm = { name ->
                Log.i(TAG, "Adding new client: $name")
                viewModel.addNewClient(name)
                showAddClientDialog = false
            }
        )
    }

    Dialog(
        onDismissRequest = {
            Log.d(TAG, "Main dialog dismissed")
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select ClientAchteur",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Search and Add ClientAchteur Row
                SearchAndAddClientRow(
                    searchQuery = searchQuery,
                    onSearchQueryChange = {
                        Log.d(TAG, "Search query changed to: $it")
                        searchQuery = it
                    },
                    onAddClientClick = {
                        Log.d(TAG, "Add client button clicked")
                        showAddClientDialog = true
                    }
                )

                // ClientAchteur Grouping with Logging
                val groupedClients = remember(clients, soldArticle, searchQuery) {
                    Log.d(TAG, "Regrouping clientAchteurs. Search query: $searchQuery")

                    val filteredClients = if (searchQuery.length >= 2) {
                        clients.filter { client ->
                            client.nom.contains(searchQuery, ignoreCase = true).also { matches ->
                                Log.v(TAG, "Filter: ${client.nom} matches search: $matches")
                            }
                        }
                    } else {
                        clients
                    }

                    filteredClients.groupBy { client ->
                        soldArticle.any { it?.clientSoldToItId == client.id }.also { hasOrders ->
                            Log.v(TAG, "ClientAchteur ${client.id} has orders: $hasOrders")
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Active Clients Section
                    if (!groupedClients[true].isNullOrEmpty()) {
                        item(key = "active_header_${UUID.randomUUID()}") {
                            ListHeader("Active Clients")
                            Log.d(TAG, "Rendering Active Clients sectionSqlRepository: ${groupedClients[true]?.size} clientAchteurs")
                        }

                        items(
                            items = groupedClients[true] ?: emptyList(),
                            key = { client ->
                                "active_${client.id}_${UUID.randomUUID()}".also { key ->
                                    Log.v(TAG, "Generated key for active client ${client.id}: $key")
                                }
                            }
                        ) { client ->
                            ClientItem(
                                client = client,
                                onClick = {
                                    Log.i(TAG, "Active client selected: ${client.id}")
                                    onClientSelected(client)
                                    onDismiss()
                                }
                            )
                        }
                    }

                    // Separator
                    if (!groupedClients[true].isNullOrEmpty() && !groupedClients[false].isNullOrEmpty()) {
                        item(key = "separator_${UUID.randomUUID()}") {
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            Log.d(TAG, "Rendering separator between sections")
                        }
                    }

                    // Other Clients Section
                    if (!groupedClients[false].isNullOrEmpty()) {
                        item(key = "other_header_${UUID.randomUUID()}") {
                            ListHeader("Other Clients")
                            Log.d(TAG, "Rendering Other Clients sectionSqlRepository: ${groupedClients[false]?.size} clientAchteurs")
                        }

                        items(
                            items = groupedClients[false] ?: emptyList(),
                            key = { client ->
                                "inactive_${client.id}_${UUID.randomUUID()}".also { key ->
                                    Log.v(TAG, "Generated key for inactive client ${client.id}: $key")
                                }
                            }
                        ) { client ->
                            ClientItem(
                                client = client,
                                onClick = {
                                    Log.i(TAG, "Inactive client selected: ${client.id}")
                                    onClientSelected(client)
                                    onDismiss()
                                }
                            )
                        }
                    }

                    // No Results Message
                    if (searchQuery.length >= 2 && groupedClients.isEmpty()) {
                        item(key = "no_results_${UUID.randomUUID()}") {
                            NoResultsMessage(searchQuery = searchQuery)
                            Log.d(TAG, "Showing no results message for query: $searchQuery")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchAndAddClientRow(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddClientClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search ClientAchteur") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        FloatingActionButton(
            onClick = onAddClientClick,
            modifier = Modifier.size(40.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add ClientAchteur",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ListHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun ClientItem(
    client: M2Client,
    onClick: () -> Unit
) {
    LaunchedEffect(client.id) {
        Log.v(TAG, "Rendering ClientItem for ID: ${client.id}")
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = client.nom,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )


            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun NoResultsMessage(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "No clientAchteurs found for \"$searchQuery\"",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun AddClientDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add New ClientAchteur",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("ClientAchteur Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number (Optional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(clientName) },
                enabled = clientName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
