package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Details.UI.B.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.petitePaddine
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GBonVentInfosHeader(
    viewModel: ZViewModel_Sec1Frag3,
) {
    val uiState by viewModel.uiState.collectAsState()
    val repo = viewModel.uiStateCentralRepositorys.gBonVentRepository
    val onVentData = repo.onVentData
    val fVentCouleurOperationRepository = viewModel.uiStateCentralRepositorys.fVentCouleurOperationRepository

    // Determine colors based on delivery state and filter mode
    val (backgroundColors, textColor) = getDeliveryStateColors(
        panieMode = uiState.panieMode,
        filterNonTrouve = uiState.filterNonTrouve,
        hasNonTrouveItems = fVentCouleurOperationRepository.onVentFilteredDatas.any {
            it.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve
        }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = backgroundColors
                )
            )
            .padding(petitePaddine)
    ) {
        onVentData?.let { data ->
            with(data) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bon de Vente",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = keyID,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor.copy(alpha = 0.8f)
                        )
                        // Show current mode and filter state
                        Text(
                            text = buildString {
                                append("Mode: ${uiState.panieMode.name}")
                                if (uiState.filterNonTrouve) append(" | Filtré: Non trouvé")
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "Bon de vente",
                        tint = textColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        } ?: run {
            Text(
                text = "Aucune donnée de vente disponible",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun getDeliveryStateColors(
    panieMode: ZViewModel_Sec1Frag3.PanieMode,
    filterNonTrouve: Boolean,
    hasNonTrouveItems: Boolean
): Pair<List<Color>, Color> {
    return when {
        panieMode == ZViewModel_Sec1Frag3.PanieMode.Delivery && hasNonTrouveItems && filterNonTrouve -> {
            // Red gradient for delivery mode with non-trouvé items when filtered
            listOf(
                Color(0xFFE57373), // Light red
                Color(0xFFD32F2F)  // Dark red
            ) to Color.White
        }
        panieMode == ZViewModel_Sec1Frag3.PanieMode.Delivery -> {
            // Green gradient for normal delivery mode
            listOf(
                Color(0xFF81C784), // Light green
                Color(0xFF388E3C)  // Dark green
            ) to Color.White
        }
        panieMode == ZViewModel_Sec1Frag3.PanieMode.Vent -> {
            // Blue gradient for sales mode
            listOf(
                Color(0xFF64B5F6), // Light blue
                Color(0xFF1976D2)  // Dark blue
            ) to Color.White
        }
        else -> {
            // Default theme colors
            listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary
            ) to MaterialTheme.colorScheme.onPrimary
        }
    }
}
