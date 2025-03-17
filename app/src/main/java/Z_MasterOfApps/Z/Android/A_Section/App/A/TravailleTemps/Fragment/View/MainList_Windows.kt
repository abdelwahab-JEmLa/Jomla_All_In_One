package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Components.DayHeader
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Components.WeekHeader
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.K_TempTravaille
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainList_Windows(
    modifier: Modifier = Modifier,
    viewModel: Windows__ViewModel = koinViewModel(),
) {
    val dateList = viewModel.dateList
    val progress by viewModel.repository.progressRepo.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Show loading indicator as first item when loading
            if (progress < 1.0f) {
                item {
                    LoadingIndicator(progress = progress)
                }
            }

            // Group items by week
            val groupedByWeek = groupItemsByWeek(dateList)

            // Iterate through each week group
            groupedByWeek.forEach { (weekInfo, itemsInWeek) ->
                // Add weekly header
                stickyHeader {
                    WeekHeader(viewModel=viewModel,weekInfo = weekInfo)
                }

                // Process items for each day in this week
                itemsInWeek.forEach { tempTravaille ->
                    stickyHeader {
                        DayHeader(
                            tempTravaille = tempTravaille,
                            viewModel = viewModel
                        )
                    }

                    items(tempTravaille.intervalesDeTravaille.size) { index ->
                        val intervale = tempTravaille.intervalesDeTravaille[index]
                        MainItem_Windows(
                            intervale = intervale,
                            viewModel = viewModel

                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingIndicator(progress: Float) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Loading data... ${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 32.dp)
                )
            }
        }
    }
}

// Data class to hold week information
data class WeekInfo(
    val weekNumber: Int,
    val year: Int,
    val isCurrentWeek: Boolean
)

// Function to group K_TempTravaille objects by week
private fun groupItemsByWeek(dateList: List<K_TempTravaille>): Map<WeekInfo, List<K_TempTravaille>> {
    val calendar = Calendar.getInstance()
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentYear = calendar.get(Calendar.YEAR)
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    return dateList.groupBy { tempTravaille ->
        try {
            val date = dateFormat.parse(tempTravaille.infosDeBase.dateInString) ?: Date()
            calendar.time = date
            val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
            val year = calendar.get(Calendar.YEAR)
            WeekInfo(
                weekNumber = weekOfYear,
                year = year,
                isCurrentWeek = weekOfYear == currentWeek && year == currentYear
            )
        } catch (e: Exception) {
            // Default to current week if parsing fails
            WeekInfo(
                weekNumber = currentWeek,
                year = currentYear,
                isCurrentWeek = true
            )
        }
    }
}
