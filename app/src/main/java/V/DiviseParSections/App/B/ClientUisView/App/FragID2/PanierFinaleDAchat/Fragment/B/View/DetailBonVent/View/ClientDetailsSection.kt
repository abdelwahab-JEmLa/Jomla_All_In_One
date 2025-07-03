package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ifFalse
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ClientDetailsSection(
    viewModel: ZViewModel_Sec1Frag3,
) {
    val uiState by viewModel.uiState.collectAsState()
    val isMinimized = uiState.isMinimized

    val fClientRepository = viewModel.uiStateCentralRepositorys.iD2ClientRepository
    val onVentClient = fClientRepository.onVentId2ClientInfos

    isMinimized.ifFalse { Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        // FIXED: Compact spacing when minimized
        horizontalArrangement = Arrangement.spacedBy(if (isMinimized) 8.dp else 12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Client",
            tint = MaterialTheme.colorScheme.primary,
            // FIXED: Smaller icon when minimized
            modifier = Modifier.size(if (isMinimized) 20.dp else 24.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            // FIXED: Compact vertical spacing when minimized
            verticalArrangement = Arrangement.spacedBy(if (isMinimized) 2.dp else 4.dp)
        ) {
            Text(
                text = "Client On Vent",
                // FIXED: Smaller text when minimized
                style = if (isMinimized) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (onVentClient != null) {
                with(onVentClient) {
                    // FIXED: Show client name on same line as status when minimized
                    if (isMinimized) {
                        Text(
                            text = "Nom: $nom",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    } else {
                        Text(
                            text = "Nom: $nom",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "keyID: $keyID",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Téléphone: $numTelephone",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                Text(
                    text = "Non Définie Pour Le Moment",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
