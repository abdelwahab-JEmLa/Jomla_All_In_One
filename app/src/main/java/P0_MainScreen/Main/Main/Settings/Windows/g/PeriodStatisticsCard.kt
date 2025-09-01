package P0_MainScreen.Main.Main.Settings.Windows.g

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
 fun PeriodStatisticsCard(
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    bons: List<M8BonVent>,
    currentPeriod: M14VentPeriode?,
    modifier: Modifier = Modifier
) {
    val totalRevenue = bons.sumOf { it.sum_De_Totale_Vents }
    val totalPayments = bons.sumOf { it.versement }
    val totalCredit = bons.sumOf { it.sum_De_Credit_Fait }
    val confirmedOrders = bons.count { it.confirmeCommande_TimeTamp > 0 }
    val totalOrders = bons.size

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
                    title = "المجموع الكلي",
                    value = totalOrders.toString(),
                    icon = Icons.Default.Assignment,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                StatisticItem(
                    title = "الطلبات المؤكدة",
                    value = confirmedOrders.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                StatisticItem(
                    title = "غير مؤكدة",
                    value = (totalOrders - confirmedOrders).toString(),
                    icon = Icons.Default.Schedule,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
