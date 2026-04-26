package Application5.App

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MonthSelectionDialog_SeparatedAppsCodingPattern(
    onDismiss: () -> Unit,
    onMonthSelected: (Calendar) -> Unit
) {
    val currentCalendar = Calendar.getInstance()
    val months = remember {
        // Get last 6 months including current
        (0..5).map { monthsBack ->
            Calendar.getInstance().apply {
                add(Calendar.MONTH, -monthsBack)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("اختر الشهر") },
        text = {
            LazyColumn {
                items(months) { month ->
                    val monthName = SimpleDateFormat("MMMM yyyy", Locale("ar")).format(month.time)
                    OutlinedButton(
                        onClick = { onMonthSelected(month) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(monthName)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

data class SessionDate(
    val dayOfMonth: Int,
    val dayOfWeek: Int,
    val date: Date,
    val timestamp: Long
)

// FIXED: Added nullable Calendar parameter for month selection
fun getSessionDatesForMonth(
    selectedMonth: Calendar? = null
): List<SessionDate> {
    val calendar = selectedMonth?.clone() as? Calendar ?: Calendar.getInstance()

    val sessionDates = mutableListOf<SessionDate>()

    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    for (day in 1..maxDay) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Sessions on Sunday and Thursday
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.THURSDAY) {
            sessionDates.add(
                SessionDate(
                    dayOfMonth = day,
                    dayOfWeek = dayOfWeek,
                    date = calendar.time,
                    timestamp = calendar.timeInMillis
                )
            )
        }
    }

    return sessionDates
}

// Helper function to get month display name
fun getMonthDisplayName(calendar: Calendar): String {
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("ar"))
    return monthFormat.format(calendar.time)
}

// Helper function to check if a session date matches a specific date
fun SessionDate.isSameDay(timestamp: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = this@isSameDay.timestamp }
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp }

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
