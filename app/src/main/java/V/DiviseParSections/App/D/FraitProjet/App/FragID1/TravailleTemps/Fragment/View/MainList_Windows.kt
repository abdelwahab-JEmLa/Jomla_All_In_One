package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.DayHeader
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View.WeekHeader
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
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
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainList_Windows(
    modifier: Modifier = Modifier,
    viewModel: RecordingViewModel = koinViewModel(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
) {
    val filteredDateList = viewModel.dateList
        .map { tempTravaille ->
            val filteredIntervals = tempTravaille.intervalesDeTravaille
            K_TempTravaille(tempTravaille.vid).apply {
                this.infosDeBase = tempTravaille.infosDeBase
                this.intervalesDeTravaille.clear()
                this.intervalesDeTravaille.addAll(filteredIntervals)
            }
        }
        .sortedByDescending { it.infosDeBase.dateInString }

    val progress by viewModel.repository.progressRepo.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (progress < 1.0f) {
                item {
                    LoadingIndicator(progress = progress)
                }
            }

            val groupedByWeek = groupItemsByWeek(
                filteredDateList,
                    Calendar.MONDAY
            )
            val sortedWeekGroups = groupedByWeek.entries
                .sortedWith(compareByDescending<Map.Entry<WeekInfo, List<K_TempTravaille>>> { it.key.year }
                    .thenByDescending { it.key.weekNumber })

            sortedWeekGroups.forEach { (weekInfo, itemsInWeek) ->
                stickyHeader(key = "week_${weekInfo.year}_${weekInfo.weekNumber}") {
                    WeekHeader(
                        weekInfo = weekInfo,
                        viewModel = viewModel,
                        weekRecords = itemsInWeek
                    )
                }

                itemsInWeek.forEach { tempTravaille ->
                    stickyHeader(key = "day_${tempTravaille.infosDeBase.dateInString}") {
                        DayHeader(
                            tempTravaille = tempTravaille,
                            viewModel = viewModel
                        )
                    }

                    items(
                        count = tempTravaille.intervalesDeTravaille.size,
                        key = { index ->
                            "${tempTravaille.infosDeBase.dateInString}_interval_$index"
                        }
                    ) { index ->
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

data class WeekInfo(
    val weekNumber: Int,
    val year: Int,
    val isCurrentWeek: Boolean
)

private fun groupItemsByWeek(
    dateList: List<K_TempTravaille>, first_daye: Int
): Map<WeekInfo, List<K_TempTravaille>> {
    val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = first_daye
        minimalDaysInFirstWeek = 1
    }

    val currentCalendar = Calendar.getInstance().apply {
        firstDayOfWeek = first_daye
        minimalDaysInFirstWeek = 1
    }

    val currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR)
    val currentYear = currentCalendar.get(Calendar.YEAR)
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    val dateFormatUnderscore = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())

    return dateList.groupBy { tempTravaille ->
        try {
            val date = try {
                dateFormat.parse(tempTravaille.infosDeBase.dateInString)
            } catch (e: Exception) {
                dateFormatUnderscore.parse(tempTravaille.infosDeBase.dateInString)
            } ?: Date()

            calendar.time = date
            val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
            val year = calendar.get(Calendar.YEAR)
            WeekInfo(
                weekNumber = weekOfYear,
                year = year,
                isCurrentWeek = weekOfYear == currentWeek && year == currentYear
            )
        } catch (e: Exception) {
            WeekInfo(
                weekNumber = currentWeek,
                year = currentYear,
                isCurrentWeek = true
            )
        }
    }
}
