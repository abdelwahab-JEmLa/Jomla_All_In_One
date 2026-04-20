package P0_MainScreen.Main.Main.Settings.Windows.g

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ActiveFilterIndicator(
    selectedStatus: M8BonVent.EtateActuellementEst,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (selectedStatus) {
        M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> Color.Green to Icons.Default.CheckCircle
        M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI -> Color.Blue to Icons.Default.LocalShipping
        M8BonVent.EtateActuellementEst.Cible -> Color.Red to Icons.Default.LocalShipping
        M8BonVent.EtateActuellementEst.Bloque_Probleme -> Color.Red to Icons.Default.Block
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT -> Color(0xFFFFD700) to Icons.Default.Schedule
        else -> Color.Gray to Icons.Default.Help
    }

    val statusText = when (selectedStatus) {
        M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> "الطلبات المؤكدة"
        M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI -> "الطلبات المُسلمة"
        M8BonVent.EtateActuellementEst.Cible -> "الطلبات المستهدفة"
        M8BonVent.EtateActuellementEst.Bloque_Probleme -> "الطلبات المحظورة"
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT -> "الطلبات قيد التنفيذ"
        else -> "غير محدد"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            
            Text(
                text = "تصفية: $statusText",
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            IconButton(
                onClick = onClearFilter,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "إزالة التصفية",
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun FilteredEmptyStateCard(
    selectedStatus: M8BonVent.EtateActuellementEst,
    modifier: Modifier = Modifier
) {
    val statusText = when (selectedStatus) {
        M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> "الطلبات المؤكدة"
        M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI -> "الطلبات المُسلمة"
        M8BonVent.EtateActuellementEst.Cible -> "الطلبات المستهدفة"
        M8BonVent.EtateActuellementEst.Bloque_Probleme -> "الطلبات المحظورة"
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT -> "الطلبات قيد التنفيذ"
        else -> "هذا النوع من الطلبات"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = "لا توجد نتائج للتصفية المحددة",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "لا توجد $statusText في هذه الفترة",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
