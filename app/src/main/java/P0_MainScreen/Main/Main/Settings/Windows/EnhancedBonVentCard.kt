package P0_MainScreen.Main.Main.Settings.Windows

import P0_MainScreen.Main.Main.Settings.Windows.g.ClientInfo
import P0_MainScreen.Main.Main.Settings.Windows.g.FinancialDetailsSection
import P0_MainScreen.Main.Main.Settings.Windows.g.StatusIndicator
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M8BonVent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EnhancedBonVentCard(
    bon: M8BonVent,
    repositorysMainGetter: RepositorysMainGetter,
    modifier: Modifier = Modifier
) {
    val client = repositorysMainGetter.find_M2Client(bon.parent_M2Client_KeyID)
    val creationTime = remember(bon.creationTimestamps) {
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(Date(bon.creationTimestamps))
    }
    val confirmationTime = remember(bon.confirmeCommande_TimeTamp) {
        if (bon.confirmeCommande_TimeTamp > 0) {
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date(bon.confirmeCommande_TimeTamp))
        } else null
    }

    val orderDuration = remember(bon.creationTimestamps, bon.confirmeCommande_TimeTamp) {
        if (bon.confirmeCommande_TimeTamp > 0) {
            val durationMillis = bon.confirmeCommande_TimeTamp - bon.creationTimestamps
            val totalSeconds = (durationMillis / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60

            // Format français
            "${minutes} min et ${seconds} sec"
        } else null
    }


    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 2.dp,
            color = when (bon.etateActuellementEst) {
                M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> Color.Green.copy(alpha = 0.3f)
                M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI -> Color.Blue.copy(alpha = 0.3f)
                M8BonVent.EtateActuellementEst.Cible -> Color.Red.copy(alpha = 0.3f)
                M8BonVent.EtateActuellementEst.Bloque_Probleme -> Color.Red.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Status and ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusIndicator(status = bon.etateActuellementEst)
                    Text(
                        text = bon.etateActuellementEst.nomArabe ?: "غير محدد",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "# ${bon.keyID.takeLast(6)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontFamily = FontFamily.Monospace
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Client and Amount Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ClientInfo(
                    clientName = client?.nom ?: "عميل غير معروف",
                    modifier = Modifier.weight(1f)
                )

            }

            // Time Information - Enhanced confirmation time display
            TimeInfoSection(
                creationTime = creationTime,
                confirmationTime = confirmationTime,
                workHours = "${bon.heurDebutInString} - ${bon.heurFinInString}",
                isConfirmed = confirmationTime != null
            )

            // Duration Card - NEW FEATURE
            if (orderDuration != null) {
                OrderDurationCard(duration = orderDuration)
            }

            // Financial Details (if any)
            if (bon.sum_De_Credit_Fait > 0 || bon.versement > 0) {
                FinancialDetailsSection(
                    creditAmount = bon.sum_De_Credit_Fait,
                    paymentAmount = bon.versement
                )
            }
        }
    }
}

@Composable
private fun OrderDurationCard(
    duration: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )

            Column {
                Text(
                    text = "الوقت الإجمالي للطلبية",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = duration,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun TimeInfoSection(
    creationTime: String,
    confirmationTime: String?,
    workHours: String,
    isConfirmed: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Creation time - smaller display
            TimeInfoItem(
                label = "وقت الإنشاء",
                time = creationTime,
                icon = Icons.Default.AccessTime,
                isLargeDisplay = false
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "وقت التأكيد",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isConfirmed) Color.Green else MaterialTheme.colorScheme.error
                    )
                }

                // LARGE confirmation time display
                Text(
                    text = confirmationTime ?: "غير مؤكد",
                    style = MaterialTheme.typography.titleLarge, // Much larger text
                    fontSize = 18.sp, // Explicit large font size
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isConfirmed) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }

    }
}

@Composable
fun TimeInfoItem(
    label: String,
    time: String,
    icon: ImageVector,
    isLargeDisplay: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
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
            style = if (isLargeDisplay) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isLargeDisplay) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
