package P0_MainScreen.Ui.Objects

import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.AppViewModels
import com.example.clientjetpack.Models.ProductDisplayController

@Composable
fun ConnexionCard(
    productDisplayController: ProductDisplayController,
    appViewModels: AppViewModels,
    onClickToStartAsClient: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Check if device name contains "M200" (case insensitive)
    val isHostEnabled = remember {
        val deviceName = Build.MODEL.lowercase()
        deviceName.contains("M200")
    }

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
                text = productDisplayController.connectionStatus,
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    productDisplayController.isConnected -> MaterialTheme.colorScheme.primary
                    productDisplayController.error != null -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Error Display
            productDisplayController.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            // Connection Controls with Host mode restriction
            if (!productDisplayController.isConnected) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            appViewModels.headViewModel.startAsHost()
                            appViewModels.headViewModel.updateTypePhone(type = true)
                        },
                        enabled = isHostEnabled
                    ) {
                        Text("Mode Hôte")
                    }
                    Button(
                        onClick = {
                            appViewModels.headViewModel.startAsClient()
                            appViewModels.headViewModel.updateTypePhone()
                            onClickToStartAsClient()
                        }
                    ) {
                        Text("Mode Client")
                    }
                }

                // Display message if host mode is disabled
                if (!isHostEnabled) {
                    Text(
                        text = "Le mode Hôte n'est disponible que pour les appareils M200",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Message Input and Controls when Connected
            if (productDisplayController.isConnected) {
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
                                appViewModels.headViewModel.sendOrderToClient(
                                    "Message",
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
                        onClick = { appViewModels.headViewModel.disconnect() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Déconnecter")
                    }
                }

                // Received Message Display
                if (productDisplayController.testMessageByWifi.isNotEmpty()) {
                    Text(
                        text = "Message reçu: ${productDisplayController.testMessageByWifi}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
