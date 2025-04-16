package Z_CodePartageEntreApps.Proto.Test.FragID1.DemiNoSQL.Fragment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.firestore.Exclude
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Class representing a period of sales
class PeriodesVent {
    var keyID by mutableStateOf("")
    private var startTimePeriode by mutableStateOf(0L)
    private var endTimePeriode by mutableStateOf(0L)


    @get:Exclude
    var vendeursActiveDonsCettePeriode: SnapshotStateList<VendeursActiveDonsCettePeriode> =
        mutableStateListOf()

    fun updateKeyID() {
        val dateFormat = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date(startTimePeriode))
        val timeStr = timeFormat.format(Date(endTimePeriode))
        keyID = "${dateStr}->($timeStr)"
    }
}

// Class representing a vendor active during a sales period
class VendeursActiveDonsCettePeriode {
    var keyID by mutableStateOf("")
    private var _nom by mutableStateOf("")
    private var _startIndex by mutableStateOf(0)

    // Use custom getters and setters to avoid JVM signature clash
    var nom: String
        get() = _nom
        set(value) {
            _nom = value
            updateKeyID()
        }

    var startIndex: Int
        get() = _startIndex
        set(value) {
            _startIndex = value
            updateKeyID()
        }

    @get:Exclude
    var produitsVenduParLui: SnapshotStateList<ProduitsVenduParLui> = mutableStateListOf()

    fun updateKeyID() {
        keyID = "${_startIndex}->($_nom)"
    }
}

// Class representing a product sold by a vendor
class ProduitsVenduParLui {
    var keyID by mutableStateOf("")
    private var _nom by mutableStateOf("")
    private var _startIndex by mutableStateOf(0)
    var quantity by mutableStateOf(0)

    // Use custom getters and setters to avoid JVM signature clash
    var nom: String
        get() = _nom
        set(value) {
            _nom = value
            updateKeyID()
        }

    var startIndex: Int
        get() = _startIndex
        set(value) {
            _startIndex = value
            updateKeyID()
        }

    fun updateKeyID() {
        keyID = "${_startIndex}->($_nom)"
    }
}
