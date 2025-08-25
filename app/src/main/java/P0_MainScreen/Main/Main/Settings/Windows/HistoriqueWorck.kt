package P0_MainScreen.Main.Main.Settings.Windows

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun HistoriqueWorck(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    modifier: Modifier = Modifier
) {
    val currentActiveFocuced_M14VentPeriode = focusedValuesGetter.currentActiveFocuced_M14VentPeriode
    val bons_De_Cette_Period = repositorysMainGetter.repo8BonVent.datasValue.filter {
        it.parent_M14VentPeriod_KeyId == (currentActiveFocuced_M14VentPeriode?.keyID ?: "")
    }

    HistoriqueWorckContent(
        bons_De_Cette_Period = bons_De_Cette_Period,
        repositorysMainGetter = repositorysMainGetter,
        currentPeriod = currentActiveFocuced_M14VentPeriode,
        modifier = modifier
    )
}

@Composable
private fun HistoriqueWorckContent(
    bons_De_Cette_Period: List<M8BonVent>,
    repositorysMainGetter: RepositorysMainGetter,
    currentPeriod: M14VentPeriode?,
    modifier: Modifier = Modifier
) {     //<--
//TODO(1): afficeh les commands aved temp passe >0 s afficeh au top et eleve les autres sans temp passe >0
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        // Header statistics card
        item {
            PeriodStatisticsCard(
                bons = bons_De_Cette_Period,
                currentPeriod = currentPeriod
            )
        }

        // Quick stats row
        item {
            QuickStatsRow(bons = bons_De_Cette_Period)
        }

        // Receipts list header
        if (bons_De_Cette_Period.isNotEmpty()) {
            item {
                Text(
                    text = "سجل الإيصالات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // Receipts sorted by creation time (newest first)
        items(
            items = bons_De_Cette_Period.sortedByDescending { it.creationTimestamps },
            key = { it.keyID }
        ) { bon ->
            EnhancedBonVentCard(
                bon = bon,
                repositorysMainGetter = repositorysMainGetter
            )
        }

        // Empty state
        if (bons_De_Cette_Period.isEmpty()) {
            item {
                EmptyStateCard()
            }
        }
    }
}

@Composable
private fun PeriodStatisticsCard(
    bons: List<M8BonVent>,
    currentPeriod: M14VentPeriode?,
    modifier: Modifier = Modifier
) {
    val totalRevenue = bons.sumOf { it.sum_De_Totale_Vents }
    val totalPayments = bons.sumOf { it.versement }
    val totalCredit = bons.sumOf { it.sum_De_Credit_Fait }
    val confirmedOrders = bons.count { it.etateActuellementEst == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "إحصائيات الفترة الحالية",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    title = "الطلبات المؤكدة",
                    value = confirmedOrders.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuickStatsRow(
    bons: List<M8BonVent>,
    modifier: Modifier = Modifier
) {
    val statusCounts = bons.groupBy { it.etateActuellementEst }.mapValues { it.value.size }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        M8BonVent.EtateActuellementEst.values().forEach { status ->
            val count = statusCounts[status] ?: 0
            if (count > 0) {
                StatusChip(
                    status = status,
                    count = count,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
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

@Composable
fun StatusIndicator(
    status: M8BonVent.EtateActuellementEst,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> Color.Green
        M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI -> Color.Blue
        M8BonVent.EtateActuellementEst.Cible -> Color.Red
        M8BonVent.EtateActuellementEst.Bloque_Probleme -> Color.Red
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT -> Color(0xFFFFD700)
        else -> Color.Gray
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .background(color = color, shape = CircleShape)
    )
}

@Composable
private fun ClientInfo(
    clientName: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "العميل",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = clientName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2
        )
    }
}

@Composable
private fun AmountInfo(
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "المبلغ الإجمالي",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "${totalAmount.toInt()} دج",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (totalAmount > 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun TimeInfoSection(
    creationTime: String,
    confirmationTime: String?,
    workHours: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TimeInfoItem(
                label = "وقت الإنشاء",
                time = creationTime,
                icon = Icons.Default.AccessTime
            )

            TimeInfoItem(
                label = "وقت التأكيد",
                time = confirmationTime ?: "غير مؤكد",
                icon = Icons.Default.CheckCircle,
                isConfirmed = confirmationTime != null,
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            )
        }

        if (workHours != "Non Defini - Non Defini") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "ساعات العمل: $workHours",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun TimeInfoItem(
    label: String,
    time: String,
    icon: ImageVector,
    isConfirmed: Boolean = true,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = if (isConfirmed) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}

@Composable
private fun FinancialDetailsSection(
    creditAmount: Double,
    paymentAmount: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (creditAmount > 0) {
                FinancialItem(
                    label = "الائتمان",
                    amount = creditAmount,
                    color = MaterialTheme.colorScheme.error,
                    icon = Icons.Default.CreditCard
                )
            }

            if (paymentAmount > 0) {
                FinancialItem(
                    label = "الدفع",
                    amount = paymentAmount,
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.Payment,
                    horizontalAlignment = Alignment.End
                )
            }
        }
    }
}

@Composable
private fun FinancialItem(
    label: String,
    amount: Double,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color.copy(alpha = 0.7f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f)
            )
        }
        Text(
            text = "${amount.toInt()} دج",
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyStateCard(
    modifier: Modifier = Modifier
) {
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
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = "لا توجد إيصالات في هذه الفترة",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "ستظهر الإيصالات هنا عند إنشائها",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
