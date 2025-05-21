package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL.Models.Function

import android.annotation.SuppressLint
import java.util.Calendar

@SuppressLint("DefaultLocale")
fun getStrDateTime(vidTimestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = vidTimestamp  // Use the provided timestamp

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    // Format: yyyy-mm-dd -< HH:mm:ss
    return String.format("%04d-%02d-%02d -< %02d:%02d:%02d", year, month, day, hour, minute, second)
}
