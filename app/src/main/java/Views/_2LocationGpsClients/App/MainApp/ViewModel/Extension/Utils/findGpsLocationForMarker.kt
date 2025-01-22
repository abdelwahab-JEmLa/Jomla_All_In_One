package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils

import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.ViewModelExtensionMapsHandler
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import org.osmdroid.views.overlay.Marker

fun ViewModelExtensionMapsHandler.findGpsLocationForMarker(marker: Marker): _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations.GpsLocation? {
        produitsMainDataBase.forEach { product ->
            product.historiqueBonsVents.forEach { bonVent ->
                if (bonVent.clientInformations?.gpsLocation?.locationGpsMark == marker) {
                    return bonVent.clientInformations?.gpsLocation
                }
            }
        }
        return null
    }
