package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Utils

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.K_TempTravaille
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.K_TempTravailleRepositoryImpl
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.MainScreen_Windows
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview

// Added preview without mock that uses the actual Firebase repository
@Preview
@Composable
fun MainScreenWithFirebasePreview() {
    // Create a real repository
    val firebaseRepository = remember { K_TempTravailleRepositoryImpl() }

    // Create a ViewModel with the real Firebase repository
    val realViewModel = remember {
        Windows__ViewModel(repository = firebaseRepository)
    }

    // Use the MaterialTheme with the real implementation
    MaterialTheme {
        MainScreen_Windows(viewModel = realViewModel, fabsVisibility = false)
    }
}
   /*
@Preview
@Composable
fun MainScreenWhithMockPreview() {
    // Create a mock repository
    val mockRepository = remember { MockTempTravailleRepository() }

    // Create a sample ViewModel with the mock repository
    val sampleViewModel = remember {
        Windows__ViewModel(repository = mockRepository).apply {
            // Populate with sample data
            populateSampleData(dateList)
        }
    }

    // Use the MaterialTheme with the sample data
    MaterialTheme {
        MainScreen_Windows(viewModel = sampleViewModel, fabsVisibility = false)
    }
}
         */


// Populate the provided list with hardcoded sample data
private fun populateSampleData(dateList: SnapshotStateList<K_TempTravaille>) {
    // Clear any existing data
    dateList.clear()

    // Define hardcoded dates for the sample data (covering 2 weeks)
    val sampleDates = listOf(
        "2025/01/01", "2025/01/02", "2025/01/03", "2025/01/04", "2025/01/05",
        "2025/01/06", "2025/01/07", "2025/01/08", "2025/01/09", "2025/01/10",
        "2025/01/11", "2025/01/12", "2025/01/13", "2025/01/14"
    )

    // Sample data structure: date -> list of intervals with fixed parameters
    val sampleData = mapOf(
        // Week 1
        "2025/01/01" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "08:30", "10:00"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.VENT, "13:15", "15:45")
        ),
        "2025/01/02" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.ACHAT, "09:00", "11:30"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "14:00", "16:30"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.VENT, "17:15", "18:45")
        ),
        "2025/01/03" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.VENT, "08:45", "12:15"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "14:30", "17:00")
        ),
        "2025/01/04" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.ACHAT, "10:00", "11:30")
        ),
        "2025/01/05" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "11:30", "13:00")
        ),
        "2025/01/06" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "08:15", "09:45"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.VENT, "13:00", "16:30")
        ),
        "2025/01/07" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.ACHAT, "09:30", "12:00"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "14:15", "17:30"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.VENT, "18:00", "19:30")
        ),
        // Week 2
        "2025/01/08" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "08:00", "10:30"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.ACHAT, "13:30", "15:00")
        ),
        "2025/01/09" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.VENT, "09:15", "11:45"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "14:45", "16:15")
        ),
        "2025/01/10" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.ACHAT, "08:45", "11:15"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.VENT, "14:00", "17:30")
        ),
        "2025/01/11" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "10:30", "12:00")
        ),
        "2025/01/12" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.ACHAT, "12:00", "13:30")
        ),
        "2025/01/13" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "08:30", "10:00"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.VENT, "13:45", "16:15")
        ),
        "2025/01/14" to listOf(
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.ACHAT, "09:00", "12:30"),
            Triple(K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT, "14:30", "17:00")
        )
    )

    // Create data for each date
    sampleDates.forEachIndexed { dayIndex, dateString ->
        val tempTravaille = K_TempTravaille(vid = dateString)
        tempTravaille.infosDeBase.dateInString = dateString

        // Get the intervals for this date or use an empty list if none defined
        val intervals = sampleData[dateString] ?: emptyList()

        // Create each interval for this date
        intervals.forEachIndexed { i, (type, start, end) ->
            val interval = K_TempTravaille.IntervalesDeTravaille(vid = "${dateString}_$i")

            // Set the defined type and times
            interval.typeTemp = type
            interval.tempDepart = start
            interval.temparrete = end

            // Set a sample ID based on the date and interval index
            interval.idBonDeCetteIntervale = dayIndex * 100L + i

            // Add the interval to the day's intervals
            tempTravaille.intervalesDeTravaille.add(interval)
        }

        // Add the day data to the provided list
        dateList.add(tempTravaille)
    }
}
