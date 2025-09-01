package P0_MainScreen.Main.Main.Settings.Windows.g

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatusChipClickable(
    status: M8BonVent.EtateActuellementEst,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (baseColor, icon) = when (status) {
        M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> Color.Green to Icons.Default.CheckCircle
        M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI -> Color.Blue to Icons.Default.LocalShipping
        M8BonVent.EtateActuellementEst.Cible -> Color.Red to Icons.Default.LocalShipping
        M8BonVent.EtateActuellementEst.Bloque_Probleme -> Color.Red to Icons.Default.Block
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT -> Color(0xFFFFD700) to Icons.Default.Schedule
        else -> Color.Gray to Icons.Default.Help
    }

    // Adjust colors based on selection state
    val backgroundColor = if (isSelected) baseColor.copy(alpha = 0.3f) else baseColor.copy(alpha = 0.1f)
    val borderColor = if (isSelected) baseColor else baseColor.copy(alpha = 0.3f)
    val contentColor = if (isSelected) baseColor else baseColor
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Surface(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "$count",
                style = if (isSelected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
