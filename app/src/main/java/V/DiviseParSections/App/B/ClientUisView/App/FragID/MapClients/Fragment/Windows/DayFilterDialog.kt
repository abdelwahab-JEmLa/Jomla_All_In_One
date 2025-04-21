package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DayFilterDialog(
    viewModel: ViewModel_MapClients_App2FragID1,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "فلتر حسب اليوم",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val days = listOf(
                    "الأحد",    // Sunday
                    "الإثنين",   // Monday
                    "الثلاثاء",  // Tuesday
                    "الأربعاء",  // Wednesday
                    "الخميس",   // Thursday
                    "الجمعة",   // Friday
                    "السبت"     // Saturday
                )

                days.chunked(2).forEach { rowDays ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowDays.forEach { day ->
                            DayButton(
                                day = day,
                                isSelected = viewModel.filterLesClientsOuLeurDernierAchatsDataStr == day,
                                onClick = {
                                    viewModel.filterLesClientsOuLeurDernierAchatsDataStr = day
                                    viewModel.mapReloadTigger++
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Clear filter button
                    Button(
                        onClick = {
                            viewModel.filterLesClientsOuLeurDernierAchatsDataStr = null.toString()
                            viewModel.mapReloadTigger++
                        }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear filter",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مسح الفلتر")
                    }

                    // Toggle day display button
                    Button(
                        onClick = {
                            viewModel.afficheLesJoursAuNoms = !viewModel.afficheLesJoursAuNoms
                            viewModel.mapReloadTigger++
                        }
                    ) {
                        Icon(
                            if (viewModel.afficheLesJoursAuNoms) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle day visibility",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (viewModel.afficheLesJoursAuNoms) "إخفاء الأيام" else "إظهار الأيام")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إغلاق")
                }
            }
        }
    }
}

@Composable
fun DayButton(
    day: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .wrapContentSize(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
