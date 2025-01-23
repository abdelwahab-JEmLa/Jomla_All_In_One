package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils

import com.google.firebase.Firebase
import com.google.firebase.database.database
import org.osmdroid.views.overlay.Marker

fun Marker.updateA_AppSettingsSaverModel() {
        Firebase.database
            .getReference("A_AppSettingsSaverModel")
            .child("1")
            .child("valueLong")
            .setValue(this.id)
    }
