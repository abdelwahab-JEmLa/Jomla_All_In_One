package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.floor

/**
 * Calculateur d'horaires de prière basé sur les algorithmes standards
 * Compatible avec Kotlin 1.8.0
 */
class PrayerTimesCalculator {

    enum class CalculationMethod {
        MWL,           // Muslim World League
        ISNA,          // Islamic Society of North America
        EGYPT,         // Egyptian General Authority of Survey
        MAKKAH,        // Umm al-Qura University, Makkah
        KARACHI,       // University of Islamic Sciences, Karachi
        TEHRAN,        // Institute of Geophysics, University of Tehran
        JAFARI,        // Shia Ithna Ashari, Leva Research Institute, Qum
        ALGERIA        // Algerian Ministry of Religious Affairs and Wakfs
    }

    data class PrayerTimes(
        val fajr: String,
        val sunrise: String,
        val dhuhr: String,
        val asr: String,
        val maghrib: String,
        val isha: String
    )

    data class Coordinates(
        val latitude: Double,
        val longitude: Double
    )

    private var method = CalculationMethod.ALGERIA
    private var asrJuristic = 0 // 0 = Shafi, 1 = Hanafi
    private var adjustHighLats = 1 // 1 = middle of night
    private val timeFormat = 0 // 0 = 24h, 1 = 12h

    // Calculation parameters
    // Format: [FajrAngle, IshaInterval, IshaAngle, MaghribInterval, MaghribAngle]
    private val methodParams = mapOf(
        CalculationMethod.MWL to doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0),
        CalculationMethod.ISNA to doubleArrayOf(15.0, 1.0, 0.0, 0.0, 15.0),
        CalculationMethod.EGYPT to doubleArrayOf(19.5, 1.0, 0.0, 0.0, 17.5),
        CalculationMethod.MAKKAH to doubleArrayOf(18.5, 1.0, 0.0, 1.0, 90.0),
        CalculationMethod.KARACHI to doubleArrayOf(18.0, 1.0, 0.0, 0.0, 18.0),
        CalculationMethod.TEHRAN to doubleArrayOf(17.7, 0.0, 4.5, 0.0, 14.0),
        CalculationMethod.JAFARI to doubleArrayOf(16.0, 0.0, 4.0, 0.0, 14.0),
        CalculationMethod.ALGERIA to doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0) // Same as MWL
    )

    private var lat = 0.0
    private var lng = 0.0
    private var timeZone = 0.0
    private var jDate = 0.0

    fun setCalculationMethod(method: CalculationMethod) {
        this.method = method
    }

    fun setAsrJuristic(juristic: Int) {
        this.asrJuristic = if (juristic == 1) 1 else 0
    }

    fun getPrayerTimes(
        date: Calendar,
        coordinates: Coordinates,
        timeZoneOffset: Double = TimeZone.getDefault().rawOffset / 3600000.0
    ): PrayerTimes {
        lat = coordinates.latitude
        lng = coordinates.longitude
        timeZone = timeZoneOffset

        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1
        val day = date.get(Calendar.DAY_OF_MONTH)

        jDate = julianDate(year, month, day) - lng / (15.0 * 24.0)

        return computePrayerTimes()
    }

    private fun computePrayerTimes(): PrayerTimes {
        val times = doubleArrayOf(5.0, 6.0, 12.0, 13.0, 18.0, 19.0)

        for (i in 1..2) {
            val params = methodParams[method] ?: methodParams[CalculationMethod.ALGERIA]!!
            times[0] = computeTime(180.0 - params[0], times[0]) // Fajr
            times[1] = computeTime(180.0 - 0.833, times[1])     // Sunrise
            times[2] = computeMidDay(times[2])                   // Dhuhr
            times[3] = computeAsr((1 + asrJuristic).toDouble(), times[3]) // Asr
            times[4] = computeTime(0.833, times[4])              // Maghrib
            times[5] = computeTime(params[4], times[5])          // Isha
        }

        return PrayerTimes(
            fajr = formatTime(times[0]),
            sunrise = formatTime(times[1]),
            dhuhr = formatTime(times[2]),
            asr = formatTime(times[3]),
            maghrib = formatTime(times[4]),
            isha = formatTime(times[5])
        )
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun computeMidDay(t: Double): Double {
        val eqt = sunPosition(jDate + t).second
        return fixHour(12 - eqt)
    }

    private fun computeTime(angle: Double, time: Double): Double {
        val decl = sunPosition(jDate + time).first
        val noon = computeMidDay(time)
        val t = 1.0 / 15.0 * arccos(
            (-sin(angle) - sin(decl) * sin(lat)) /
                    (cos(decl) * cos(lat))
        )
        return if (angle > 90) noon + t else noon - t
    }

    private fun computeAsr(factor: Double, time: Double): Double {
        val decl = sunPosition(jDate + time).first
        val angle = -arccot(factor + tan(abs(lat - decl)))
        return computeTime(angle, time)
    }

    private fun sunPosition(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(g) + 0.020 * sin(2 * g))

        val e = 23.439 - 0.00000036 * d
        val ra = arctan2(cos(e) * sin(l), cos(l)) / 15.0
        val decl = arcsin(sin(e) * sin(l))
        val eqt = q / 15.0 - fixHour(ra)

        return Pair(decl, eqt)
    }

    private fun formatTime(time: Double): String {
        var t = fixHour(time + 0.5 / 60.0) // add 0.5 minutes for rounding
        t = fixHour(t + timeZone)
        val hours = floor(t).toInt()
        val minutes = floor((t - hours) * 60.0).toInt()
        return String.format("%02d:%02d", hours, minutes)
    }

    // Math helper functions
    private fun sin(d: Double) = kotlin.math.sin(Math.toRadians(d))
    private fun cos(d: Double) = kotlin.math.cos(Math.toRadians(d))
    private fun tan(d: Double) = kotlin.math.tan(Math.toRadians(d))
    private fun arcsin(x: Double) = Math.toDegrees(kotlin.math.asin(x))
    private fun arccos(x: Double) = Math.toDegrees(kotlin.math.acos(x))
    private fun arctan(x: Double) = Math.toDegrees(kotlin.math.atan(x))
    private fun arccot(x: Double) = Math.toDegrees(kotlin.math.atan(1.0 / x))
    private fun arctan2(y: Double, x: Double) = Math.toDegrees(kotlin.math.atan2(y, x))
    private fun fixAngle(a: Double): Double {
        var angle = a
        angle -= 360.0 * floor(angle / 360.0)
        angle = if (angle < 0) angle + 360.0 else angle
        return angle
    }
    private fun fixHour(a: Double): Double {
        var hour = a
        hour -= 24.0 * floor(hour / 24.0)
        hour = if (hour < 0) hour + 24.0 else hour
        return hour
    }
}

// Exemple d'utilisation
object PrayerTimesHelper {

    fun getTodayPrayerTimes(
        latitude: Double,
        longitude: Double,
        method: PrayerTimesCalculator.CalculationMethod = PrayerTimesCalculator.CalculationMethod.ALGERIA
    ): PrayerTimesCalculator.PrayerTimes {
        val calculator = PrayerTimesCalculator()
        calculator.setCalculationMethod(method)

        val coordinates = PrayerTimesCalculator.Coordinates(latitude, longitude)
        val today = Calendar.getInstance()

        return calculator.getPrayerTimes(today, coordinates)
    }
}
