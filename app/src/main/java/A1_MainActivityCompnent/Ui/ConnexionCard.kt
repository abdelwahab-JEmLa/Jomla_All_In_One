package A1_MainActivityCompnent.Ui
import A1_MainActivityCompnent.Main.UiState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.AppViewModels

@Composable
fun ConnexionCard(
    uiState: UiState,
    appViewModels: AppViewModels,
) {
    var messageText by remember { mutableStateOf("") }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "État de la connexion",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = uiState.connectionStatus,
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    uiState.isConnected -> MaterialTheme.colorScheme.primary
                    uiState.error != null -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Error Display
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            // Connection Controls
            if (!uiState.isConnected) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        appViewModels.startUpNewArticlesViewModels.startAsHost()
                        appViewModels.startUpNewArticlesViewModels.updateTypePhone(type = true)
                    }) {
                        Text("Mode Hôte")
                    }
                    Button(onClick = {
                        appViewModels.startUpNewArticlesViewModels.startAsClient()
                        appViewModels.startUpNewArticlesViewModels.updateTypePhone()
                    }) {
                        Text("Mode Client")
                    }
                }
            }

            // Message Input and Controls when Connected
            if (uiState.isConnected) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (messageText.isNotEmpty()) {
                                appViewModels.startUpNewArticlesViewModels.sendTestMessage(
                                    messageText
                                )
                                messageText = ""
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Envoyer")
                    }
                    Button(
                        onClick = { appViewModels.startUpNewArticlesViewModels.disconnect() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Déconnecter")
                    }
                }

                // Received Message Display
                if (uiState.messageByWifi.isNotEmpty()) {
                    Text(
                        text = "Message reçu: ${uiState.messageByWifi}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

