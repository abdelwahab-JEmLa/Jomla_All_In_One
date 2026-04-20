package P0_MainScreen.Main.Main.Settings.Windows.g

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import androidx.compose.foundation.BorderStroke
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
 fun StatusChip(
    status: M8BonVent.EtateActuellementEst,
    count: Int,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (status) {
        M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> Color.Green to Icons.Default.CheckCircle
        M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI -> Color.Blue to Icons.Default.LocalShipping
        M8BonVent.EtateActuellementEst.Cible -> Color.Red to Icons.Default.LocalShipping
        M8BonVent.EtateActuellementEst.Bloque_Probleme -> Color.Red to Icons.Default.Block
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT -> Color(0xFFFFD700) to Icons.Default.Schedule
        else -> Color.Gray to Icons.Default.Help
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
