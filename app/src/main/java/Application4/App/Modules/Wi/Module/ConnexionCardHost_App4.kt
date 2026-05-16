package Application4.App.Modules.Wi.Module

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ConnexionCardHost_App4(
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp
) {
    val state by wifiTransferDatas_ControllerApp.state.collectAsState()
    var isCollapsed by remember { mutableStateOf(true) }
    var messageText by remember { mutableStateOf("") }
    val isHostEnabled = true
    val errorColor = MaterialTheme.colorScheme.error
    val connectedColor = MaterialTheme.colorScheme.primary
    val pendingColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .clickable { isCollapsed = !isCollapsed }
            .then(
                if (isCollapsed) Modifier.height(40.dp).padding(0.dp)
                else Modifier.fillMaxWidth().padding(16.dp)
            )
            .animateContentSize(animationSpec = tween(300))
    ) {
        if (!isCollapsed) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("État de la connexion", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = state.connectionStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        state.isConnected   -> MaterialTheme.colorScheme.primary
                        state.error != null -> MaterialTheme.colorScheme.error
                        else                -> MaterialTheme.colorScheme.onSurface
                    }
                )
                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }

                if (!state.isConnected) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        if (isHostEnabled) {
                            Button(onClick = { wifiTransferDatas_ControllerApp.startAsHost() }) {
                                Text("Mode Hôte")
                            }
                        }            //<--
                        //TODO(1): ajot un button qui arere tou le wifi c comme si il restart de 0 et forced arret de l app pas de tentative cach corrupted
                    }
                } else {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        label = { Text("Message") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (messageText.isNotEmpty()) {
                                    wifiTransferDatas_ControllerApp.sendOrderToClientDisplayerT(
                                        Wifi_Messages_Types_NewProto.SearchWindowsDisplaye,
                                        messageText
                                    )
                                    messageText = ""
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Envoyer") }
                        Button(
                            onClick = { wifiTransferDatas_ControllerApp.disconnect() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("Déconnecter") }
                    }
                    if (state.testMessageByWifi.isNotEmpty()) {
                        Text(
                            "Message reçu: ${state.testMessageByWifi}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Canvas(modifier = Modifier.padding(horizontal = 8.dp).height(8.dp).weight(0.1f)) {
                    val dotColor = when {
                        state.isConnected   -> connectedColor
                        state.error != null -> errorColor
                        else                -> pendingColor
                    }
                    drawCircle(color = dotColor, radius = size.minDimension / 2)
                }
                Text("Connexion", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.9f))
            }
        }
    }
}
