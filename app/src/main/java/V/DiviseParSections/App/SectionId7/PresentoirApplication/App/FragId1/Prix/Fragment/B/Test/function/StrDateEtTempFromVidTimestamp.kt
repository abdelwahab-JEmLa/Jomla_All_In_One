package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.function

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Helper function to format timestamp to readable date and time
fun strDateEtTempFromVidTimestamp(timestamp: Long): Pair<String, String> {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return Pair(dateFormat.format(date), timeFormat.format(date))
}
