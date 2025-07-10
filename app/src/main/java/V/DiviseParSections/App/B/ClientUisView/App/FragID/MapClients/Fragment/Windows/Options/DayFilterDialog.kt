package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DayFilterDialog(
    viewModel: MapClientsViewModel,
    onDismiss: () -> Unit
) {
    val days = listOf(
        "الأحد",    // Sunday
        "الإثنين",   // Monday
        "الثلاثاء",  // Tuesday
        "الأربعاء",  // Wednesday
        "الخميس",   // Thursday
        "الجمعة",   // Friday
        "السبت"     // Saturday
    )

    // Create add_New local state for selected days to provide immediate UI feedback before updating viewModel
    var selectedDays by remember { mutableStateOf(viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList) }

    // Local state to track the toggle value of afficheLesJoursAuNoms
    var showDayNames by remember { mutableStateOf(viewModel.afficheLesJoursAuNoms) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "فلتر حسب اليوم",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // New: Toggle for showing day names on markers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Show Day Names",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "إظهار أسماء الأيام على العلامات", // "Show day names on markers" in Arabic
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Switch(
                        checked = showDayNames,
                        onCheckedChange = { isChecked ->
                            showDayNames = isChecked
                            viewModel.afficheLesJoursAuNoms = isChecked
                            // Trigger map reload
                        }
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // ButtonAutreEtates to clear all selections
                Button(
                    onClick = {
                        selectedDays = emptyList()
                        viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList = emptyList()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إزالة جميع المرشحات")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    itemsIndexed(days) { _, day ->
                        val isSelected = selectedDays.contains(day)

                        DayItem(
                            day = day,
                            isSelected = isSelected,
                            onToggle = {
                                selectedDays = if (isSelected) {
                                    selectedDays - day
                                } else {
                                    selectedDays + day
                                }

                                // Update viewModel's filter list
                                viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList = selectedDays
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("موافق")
                }
            }
        }
    }
}

@Composable
private fun DayItem(
    day: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = day,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}
