package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter.Companion.getSemanticsTagFocucedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
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
    modifier: Modifier,
    viewModel: ZViewModel_Sec1Frag3,
) {
    val uiState by viewModel.uiState.collectAsState()
    val isMinimized = uiState.isMinimized
    val get = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    // FIXED: Use the correct property name
    val onVentClient = get.activeOnVent_M2Client

    isMinimized.ifFalse { Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (isMinimized) 8.dp else 12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Client",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(if (isMinimized) 20.dp else 24.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
                .getSemanticsTagFocucedVars(get),
            // FIXED: Now that we're using the correct property, the semantic tag should work properly
            verticalArrangement = Arrangement.spacedBy(if (isMinimized) 2.dp else 4.dp)
        ) {
            Text(
                text = "Client On Vent",
                style = if (isMinimized) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (onVentClient != null) {
                with(onVentClient) {
                    if (isMinimized) {
                        Text(
                            text = "Etate: $nom",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    } else {
                        Text(
                            text = "Etate: $nom",
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
                    text = "ليس هناك اي زبون محدد",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
