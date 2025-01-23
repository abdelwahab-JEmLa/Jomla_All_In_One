package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils

import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.ViewModelExtensionMapsHandler
import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.clientjetpack.R
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

fun ViewModelExtensionMapsHandler.safeUpdateInfoWindows(marker: Marker, context: Context) {
        try {
            val infoWindow = marker.infoWindow as? MarkerInfoWindow ?: return
            val container = infoWindow.view.findViewById<LinearLayout>(R.id.info_window_container) ?: return

            val bonVent = findBonVentForMarker(marker)
            val backgroundColor = bonVent?.bonStatueDeBase?.currentStatue?.let { statue ->
                ContextCompat.getColor(context, statue.color)
            } ?: ContextCompat.getColor(context, android.R.color.white)

            container.setBackgroundColor(backgroundColor)

            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
                marker.showInfoWindow()
            }
        } catch (e: Exception) {
            Log.e("Marker", "Error updating info window", e)
        }
    }
