package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows
// Additional imports needed at the top of the file:
import V.DiviseParSections.App.Shared.Repository.ID2HClientInfos.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2HClientInfos.Repository.HClientRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ID4ClientSearchButton(
    showLabels: Boolean,
    hClientRepository: HClientRepository,
    onClientSelected: (HClientInfos) -> Unit = {}
) {
    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredClients by remember { mutableStateOf<List<HClientInfos>>(emptyList()) }
    var showDropdown by remember { mutableStateOf(false) }

    val clients = hClientRepository.datasValue

    // Debounced search effect
    LaunchedEffect(searchQuery) {
        delay(300) // Debounce delay
        if (searchQuery.isNotEmpty()) {
            filteredClients = clients.filter { client ->
                client.nom.contains(searchQuery, ignoreCase = true) ||
                        client.numTelephone.contains(searchQuery, ignoreCase = true)
            }
            showDropdown = filteredClients.isNotEmpty()
        } else {
            filteredClients = emptyList()
            showDropdown = false
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (!isSearchMode) {
            // Normal button mode
            FloatingActionButton(
                onClick = {
                    isSearchMode = true
                },
                modifier = Modifier.size(40.dp),
                containerColor = Color(0xFF4CAF50), // Green color for GPS/search
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Rechercher Client",
                    tint = Color.White
                )
            }

            if (showLabels) {
                Text(
                    text = "Rechercher Client",
                    modifier = Modifier
                        .background(Color(0xFF4CAF50))
                        .padding(4.dp),
                    color = Color.White
                )
            }
        } else {
            // Search mode
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .focusRequester(remember { FocusRequester() }),
                        placeholder = { Text("Nom ou téléphone...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Rechercher"
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    isSearchMode = false
                                    searchQuery = ""
                                    showDropdown = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fermer"
                                )
                            }
                        }
                    )
                }

                // Dropdown list
                if (showDropdown) {
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .heightIn(max = 200.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        LazyColumn {
                            items(filteredClients) { client ->
                                ClientSearchItem(
                                    client = client,
                                    onClick = {
                                        onClientSelected(client)
                                        isSearchMode = false
                                        searchQuery = ""
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Auto-focus when entering search mode
    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            // Focus will be handled by the FocusRequester
        }
    }
}

@Composable
fun ClientSearchItem(
    client: HClientInfos,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Client state indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = Color(client.actuelleEtat.color),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = client.nom,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            if (client.numTelephone.isNotEmpty()) {
                Text(
                    text = client.numTelephone,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // Client type icon
        Icon(
            imageVector = client.clientTypeMode.icon,
            contentDescription = null,
            tint = client.clientTypeMode.color,
            modifier = Modifier.size(16.dp)
        )
    }
}
