package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components

import java.util.Calendar
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

/**
 * Calculateur d'horaires de prière - Version corrigée
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
    private var asrJuristic = 0 // 0 = Shafi (standard), 1 = Hanafi

    // Calculation parameters: [FajrAngle, IshaAngle]
    private val methodParams = mapOf(
        CalculationMethod.MWL to Pair(18.0, 17.0),
        CalculationMethod.ISNA to Pair(15.0, 15.0),
        CalculationMethod.EGYPT to Pair(19.5, 17.5),
        CalculationMethod.MAKKAH to Pair(18.5, 90.0),
        CalculationMethod.KARACHI to Pair(18.0, 18.0),
        CalculationMethod.TEHRAN to Pair(17.7, 14.0),
        CalculationMethod.JAFARI to Pair(16.0, 14.0),
        CalculationMethod.ALGERIA to Pair(18.0, 17.0)
    )

    fun setCalculationMethod(method: CalculationMethod) {
        this.method = method
    }

    fun setAsrJuristic(juristic: Int) {
        this.asrJuristic = if (juristic == 1) 1 else 0
    }

    fun getPrayerTimes(
        date: Calendar,
        coordinates: Coordinates,
        timeZoneOffset: Double = 1.0
    ): PrayerTimes {
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1
        val day = date.get(Calendar.DAY_OF_MONTH)

        val lat = coordinates.latitude
        val lng = coordinates.longitude

        // Calculate Julian Day
        val jd = getJulianDay(year, month, day)

        // Get sun declination and equation of time
        val sunData = getSunPosition(jd)
        val declination = sunData.first
        val eqTime = sunData.second

        // Calculate Dhuhr (solar noon)
        val dhuhr = 12.0 - (lng / 15.0) - eqTime + timeZoneOffset

        // Get method parameters
        val params = methodParams[method] ?: methodParams[CalculationMethod.ALGERIA]!!
        val fajrAngle = params.first
        val ishaAngle = params.second

        // Calculate Fajr
        val fajr = dhuhr - getSunAngleTime(lat, declination, 90.0 + fajrAngle) / 15.0

        // Calculate Sunrise
        val sunrise = dhuhr - getSunAngleTime(lat, declination, 90.833) / 15.0

        // Calculate Asr
        val asrFactor = if (asrJuristic == 1) 2.0 else 1.0 // Hanafi vs Shafi
        val asrAngle = Math.toDegrees(atan(1.0 / (asrFactor + tan(Math.toRadians(abs(lat - declination))))))
        val asr = dhuhr + getSunAngleTime(lat, declination, 90.0 - asrAngle) / 15.0

        // Calculate Maghrib
        val maghrib = dhuhr + getSunAngleTime(lat, declination, 90.833) / 15.0

        // Calculate Isha
        val isha = dhuhr + getSunAngleTime(lat, declination, 90.0 + ishaAngle) / 15.0

        return PrayerTimes(
            fajr = formatTime(fajr),
            sunrise = formatTime(sunrise),
            dhuhr = formatTime(dhuhr),
            asr = formatTime(asr),
            maghrib = formatTime(maghrib),
            isha = formatTime(isha)
        )
    }

    private fun getJulianDay(year: Int, month: Int, day: Int): Double {
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

    private fun getSunPosition(jd: Double): Pair<Double, Double> {
        // Days since J2000.0
        val d = jd - 2451545.0

        // Mean anomaly
        val g = 357.529 + 0.98560028 * d

        // Mean longitude
        val q = 280.459 + 0.98564736 * d

        // Ecliptic longitude
        val L = q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g))

        // Obliquity of ecliptic
        val e = 23.439 - 0.00000036 * d

        // Right ascension
        val RA = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(L)),
            cos(Math.toRadians(L)))) / 15.0

        // Declination
        val decl = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(L))))

        // Equation of time
        val eqTime = (q / 15.0) - fixHour(RA)

        return Pair(decl, eqTime)
    }

    private fun getSunAngleTime(lat: Double, decl: Double, angle: Double): Double {
        val latRad = Math.toRadians(lat)
        val declRad = Math.toRadians(decl)
        val angleRad = Math.toRadians(angle)

        val cosH = (cos(angleRad) - sin(latRad) * sin(declRad)) / (cos(latRad) * cos(declRad))

        // Check if sun reaches this angle
        if (cosH > 1.0 || cosH < -1.0) {
            return 0.0
        }

        return Math.toDegrees(acos(cosH))
    }

    private fun formatTime(time: Double): String {
        val t = fixHour(time)
        val hours = floor(t).toInt()
        val minutes = floor((t - hours) * 60.0).toInt()
        return String.format("%02d:%02d", hours, minutes)
    }

    private fun fixHour(hour: Double): Double {
        var h = hour
        h = h - 24.0 * floor(h / 24.0)
        return if (h < 0) h + 24.0 else h
    }
}

// Helper object
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

        return calculator.getPrayerTimes(today, coordinates, 1.0)
    }
}
