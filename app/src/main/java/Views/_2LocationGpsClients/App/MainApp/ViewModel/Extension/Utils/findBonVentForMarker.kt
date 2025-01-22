package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils

import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.ViewModelExtensionMapsHandler
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import org.osmdroid.views.overlay.Marker

fun ViewModelExtensionMapsHandler.findBonVentForMarker(marker: Marker): _ModelAppsFather.ProduitModel.ClientBonVentModel? {
        produitsMainDataBase.forEach { product ->
            product.historiqueBonsVents.forEach { bonVent ->
                if (bonVent.clientInformations?.gpsLocation?.locationGpsMark?.id == marker.id) {
                    return bonVent
                }
            }
        }
        return null
    }
