package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

class K_TempTravaille(var vid: String = "2025_01_01") {
    var infosDeBase by mutableStateOf(InfosDeBase())

    @IgnoreExtraProperties
    class InfosDeBase {
        var dateInString by mutableStateOf("2025_01_01")
        var paye by mutableStateOf(false)
    }

    @get:Exclude
    var intervalesDeTravaille: SnapshotStateList<IntervalesDeTravaille> = mutableStateListOf()

    @IgnoreExtraProperties
    class IntervalesDeTravaille(var vid: String = "HH_mm") {
        var typeTemp by mutableStateOf(TypeTemp.ACHAT)

        enum class TypeTemp(val color: Color, val icon: ImageVector, val nomArabe: String = "") {
            DEPLACEMENT(Color(0xFF2196F3), Icons.Filled.DirectionsCar, "تنقل"),
            VENT(Color(0xFF4CAF50), Icons.Filled.ShoppingCart, "بيع"),
            ACHAT(Color(0xFFFFC107), Icons.Filled.Store, "شراء"),
            ENTRE_PAR_MAIN(Color(0xFF7F7866), Icons.Filled.Timer, "غير محدد"),
            MAHABAT_KHATER(Color(0xFF9C27B0), Icons.Filled.MonetizationOn, "هل جزاء الاحسان الا الاحسان");

            override fun toString(): String {
                return name
            }
        }

        var idClientSiAchat by mutableStateOf(0L)
        var enCoureDEnregestrement by mutableStateOf(false)
        var tempDepart by mutableStateOf("HH:mm")
        var temparrete by mutableStateOf("HH:mm")

        companion object {
            fun calculateDuration(start: String, end: String): String {
                if (start == "HH:mm" || end == "HH:mm") return "N/A"

                try {
                    val startTime = start.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
                    val endTime = end.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
                    val durationMinutes = endTime - startTime
                    val hours = durationMinutes / 60
                    val minutes = durationMinutes % 60
                    return "${hours}h ${minutes}m"
                } catch (e: Exception) {
                    return "N/A"
                }
            }
        }
    }

    companion object {
        fun calculateDurationMinutes(start: String, end: String): Int {
            if (start == "HH:mm" || end == "HH:mm") return 0

            try {
                val startTime = start.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
                val endTime = end.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
                return endTime - startTime
            } catch (e: Exception) {
                return 0
            }
        }
    }
}
