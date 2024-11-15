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
    val isHostEnabled = Build.MODEL.lowercase().contains("m200")

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
            ConnectionStatus(productDisplayController)

            if (!productDisplayController.isConnected) {
                ConnectionButtons(
                    isHostEnabled = isHostEnabled,
                    onHostClick = {
                        appViewModels.headViewModel.run {
                            startAsHost()
                            updateTypePhone(type = true)
                        }
                    },
                    onClientClick = {
                        appViewModels.headViewModel.run {
                            startAsClient()
                            updateTypePhone()
                        }
                        onClickToStartAsClient()
                    }
                )
            } else {
                MessageSection(
                    messageText = messageText,
                    onMessageChange = { messageText = it },
                    onSendClick = {
                        if (messageText.isNotEmpty()) {
                            appViewModels.headViewModel.sendOrderToClientDisplayer("Message", messageText)
                            messageText = ""
                        }
                    },
                    onDisconnectClick = { appViewModels.headViewModel.disconnect() },
                    receivedMessage = productDisplayController.testMessageByWifi
                )
            }
        }
    }
}

@Composable
private fun ConnectionStatus(productDisplayController: ProductDisplayController) {
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

    productDisplayController.error?.let { error ->
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ConnectionButtons(
    isHostEnabled: Boolean,
    onHostClick: () -> Unit,
    onClientClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onHostClick,
        ) {
            Text("Mode Hôte")
        }
        Button(onClick = onClientClick) {
            Text("Mode Client")
        }
    }

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

@Composable
private fun MessageSection(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    receivedMessage: String
) {
    OutlinedTextField(
        value = messageText,
        onValueChange = onMessageChange,
        label = { Text("Message") },
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onSendClick,
            modifier = Modifier.weight(1f)
        ) {
            Text("Envoyer")
        }
        Button(
            onClick = onDisconnectClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Déconnecter")
        }
    }

    if (receivedMessage.isNotEmpty()) {
        Text(
            text = "Message reçu: $receivedMessage",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
