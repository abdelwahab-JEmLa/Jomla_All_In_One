package V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

class LocationTracker(
    private val context: Context,
    private val mapView: MapView,
    private val radius: Double,
    private val xmlResources: List<Pair<String, Int>>?
) : SensorEventListener, LocationListener {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private var directionMarker: Marker? = null
    private var proximityCircle: Polygon? = null
    private var isTracking = false
    private var followLocation = true

    var currentBearing by mutableStateOf(0f)
        private set

    var currentLocation by mutableStateOf<Location?>(null)
        private set

    fun getCurrentPosition(): Location? {
        return try {
            val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (gpsLocation != null) {
                currentLocation = gpsLocation
                return gpsLocation
            }

            // Fallback to network location if GPS is not available
            val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (networkLocation != null) {
                currentLocation = networkLocation
                return networkLocation
            }

            // Return current stored location if available
            currentLocation
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        }
    }

    // Get current latitude
    fun getCurrentLatitude(): Double {
        return getCurrentPosition()?.latitude ?: 0.0
    }

    // Get current longitude
    fun getCurrentLongitude(): Double {
        return getCurrentPosition()?.longitude ?: 0.0
    }

    private fun createProximityCircle(center: GeoPoint): Polygon {
        val points = mutableListOf<GeoPoint>()
        val numberOfPoints = 60 // Points pour faire le cercle

        for (i in 0..numberOfPoints) {
            val angle = Math.PI * 2 * i / numberOfPoints
            // Calculer la distance en degrés (conversion depuis mètres)
            val latRadius = radius / 111320.0 // 1 degré = environ 111.32 km
            val lonRadius = radius / (111320.0 * Math.cos(Math.toRadians(center.latitude)))

            val lat = center.latitude + latRadius * Math.sin(angle)
            val lon = center.longitude + lonRadius * Math.cos(angle)
            points.add(GeoPoint(lat, lon))
        }

        return Polygon(mapView).apply {
            this.points = points
            fillColor = android.graphics.Color.argb(40, 33, 150, 243) // Bleu transparent
            strokeColor =
                android.graphics.Color.argb(128, 33, 150, 243) // Bleu plus opaque pour le contour
            strokeWidth = 3f
        }
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        val geoPoint = GeoPoint(location.latitude, location.longitude)

        mainHandler.post {
            // Supprimer les anciens overlays
            mapView.overlays.remove(proximityCircle)
            mapView.overlays.remove(directionMarker)

            // Créer et ajouter le nouveau cercle de proximité
            proximityCircle = createProximityCircle(geoPoint).also {
                mapView.overlays.add(it)
            }

            // Mettre à jour et ajouter le marker de direction
            directionMarker?.let { marker ->
                marker.position = geoPoint
                marker.rotation = currentBearing
                mapView.overlays.add(marker)
            }

            if (followLocation) {
                mapView.controller.animateTo(geoPoint)
            }

            mapView.invalidate()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            val orientationValues = FloatArray(3)

            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationValues)

            val azimuth = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
            currentBearing = (azimuth + 360) % 360

            mainHandler.post {
                directionMarker?.rotation = currentBearing
                mapView.invalidate()
            }
        }
    }

    fun startTracking() {
        if (!isTracking) {
            // Créer le marker de direction avec l'icône personnalisée
            directionMarker = Marker(mapView).apply {
                icon = ContextCompat.getDrawable(
                    context, xmlResources
                        ?.find { it.first == "location_arrow" }?.second
                        ?: throw IllegalStateException(" layout not found")
                )
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon?.setTint(android.graphics.Color.BLUE) // Rendre l'icône plus visible
            }

            // Ajouter le marker initial
            currentLocation?.let { location ->
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                directionMarker?.position = geoPoint
                proximityCircle = createProximityCircle(geoPoint)
                mapView.overlays.add(proximityCircle)
                mapView.overlays.add(directionMarker)
            }

            // Démarrer les mises à jour du capteur
            sensorManager.registerListener(
                this,
                rotationSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )

            try {
                // Demander les mises à jour de localisation
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_INTERVAL,
                    LOCATION_UPDATE_DISTANCE,
                    this
                )

                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_UPDATE_INTERVAL,
                    LOCATION_UPDATE_DISTANCE,
                    this
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }

            isTracking = true
            mapView.invalidate()
        }
    }

    fun stopTracking() {
        if (isTracking) {
            // Arrêter les listeners
            sensorManager.unregisterListener(this)
            try {
                locationManager.removeUpdates(this)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }

            // Nettoyer les overlays
            mapView.overlays.remove(proximityCircle)
            mapView.overlays.remove(directionMarker)

            // Réinitialiser les états
            directionMarker = null
            proximityCircle = null
            isTracking = false
            currentLocation = null

            mapView.invalidate()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Non utilisé
    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 1000L // 1 second
        private const val LOCATION_UPDATE_DISTANCE = 1f    // 1 meter
    }
}

@Composable
fun rememberLocationTracker(
    mapView: MapView,
    proxim: Double,
    xmlResources: List<Pair<String, Int>>?
): LocationTracker {
    val context = LocalContext.current
    val locationTracker = remember { LocationTracker(context, mapView, proxim, xmlResources) }

    DisposableEffect(locationTracker) {
        onDispose {
            locationTracker.stopTracking()
        }
    }

    return locationTracker
}
