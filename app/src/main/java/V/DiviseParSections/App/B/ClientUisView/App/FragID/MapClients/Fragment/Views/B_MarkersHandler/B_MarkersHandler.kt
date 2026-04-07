package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.Screen_NewProtoPattern
import EntreApps.Shared.Models.AppType
import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M2Client
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View.get_Found_Or_Default_M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.get_sum_Bon_Vents
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LATITUDE
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import Z_CodePartageEntreApps.Modules.DatesHandler
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_MasterOfApps.Resources.XmlsFilesHandler.Companion.xmlResources
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun addOuUpdateMapMarkers(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    mapView: MapView,
    currentFilterMode: MapClientsViewModel.VisibleClientsNow,
    showMarkerDetails: Boolean,
    proximityFilterCenter: GeoPoint?,
    proximityFilterRadiusMeters: Double,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
) {        //<--
    val clientDataBaseSnapList = uiState.b_ClientInfosProtoJuin3List

    val existingMarkers = mapView.overlays.filterIsInstance<Marker>()
    existingMarkers.forEach { it.closeInfoWindow() }

    val markersOnMap = mapView.overlays.filterIsInstance<Marker>()
    val markersToRemove = markersOnMap
        .filter { marker -> clientDataBaseSnapList.any { it.id.toString() == marker.id } }
    mapView.overlays.removeAll(markersToRemove)

    val locationOverlay = preserveLocationOverlay(mapView)

    val modeFilteredClients = filterClientsBasedOnMode(viewModel, currentFilterMode)

    val clientsToShow = if (proximityFilterCenter != null) {
        modeFilteredClients.filter { client ->
            haversineMeters(
                proximityFilterCenter.latitude,
                proximityFilterCenter.longitude,
                client.latitude,
                client.longitude,
            ) <= proximityFilterRadiusMeters
        }
    } else {
        modeFilteredClients
    }
    addMarkersForFilteredClients(
        mapView,
        clientsToShow,
        viewModel,
        showMarkerDetails,
        fragmentNavigationHandler_NewProto=fragmentNavigationHandler_NewProto,
    )

    restoreLocationOverlayAtBottom(mapView, locationOverlay)
}

fun addMarkersForFilteredClients(
    mapView: MapView,
    clientsToShow: List<M2Client>,
    viewModel: MapClientsViewModel,
    showMarkerDetails: Boolean,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
) {
    val context = mapView.context

    clientsToShow.forEach { client ->
        try {
            createAndAddMarker(
                m2Client = client,
                viewModel,
                mapView = mapView,
                context = context,
                showMarkerDetails = showMarkerDetails,
                fragmentNavigationHandler_NewProto=fragmentNavigationHandler_NewProto,
            )
        } catch (e: Exception) {
        }
    }

    mapView.invalidate()
}

fun createAndAddMarker(
    m2Client: M2Client,
    viewModel: MapClientsViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    mapView: MapView,
    context: Context,
    showMarkerDetails: Boolean,
    fragmentNavigationHandler: FragmentNavigationHandler = aCentralFacade.modulesCentral.fragmentNavigationHandler,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
) {
    val repo = viewModel.getter.repo2Client

    val marker = Marker(mapView).apply {
        id = m2Client.id.toString()
        position = GeoPoint(
            m2Client.latitude.takeIf { it != 0.0 } ?: DEFAULT_LATITUDE,
            m2Client.longitude
        )

        title(viewModel, m2Client)

        snippet = if (m2Client.cUnClientTemporaire)
            "ClientAchteur temporaire" else "ClientAchteur permanent"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        try {
            configureMarkerInfoWindow(this, mapView, context, viewModel, m2Client)
        } catch (_: Exception) {
        }
        val compt = viewModel.active_Datas.active_M9Compt
        val currentMode = compt?.click_On_Marque ?: ActiveCentralValues.Click_On_Marque.Standart
        setOnMarkerClickListener { clickedMarker, _ ->
            val activeCentralValues = focusedValuesGetter.active_Central_Values
            val actuelle_Ciblage_MaxPosition = activeCentralValues.actuelle_Ciblage_MaxPosition
            val newPosition = actuelle_Ciblage_MaxPosition + 1

            val modeLabel = when (currentMode) {
                ActiveCentralValues.Click_On_Marque.Standart                              -> "Standard"
                ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients                -> "Ajouter Ciblage"
                ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> "Afficher Commande"
                ActiveCentralValues.Click_On_Marque.Call                                  -> "Appeler Client"
                ActiveCentralValues.Click_On_Marque.Navigate                              -> "Navigation GPS"
                ActiveCentralValues.Click_On_Marque.Marck_Ferme                          -> "Marquer Fermé"
                ActiveCentralValues.Click_On_Marque.Marck_Command_Livret                 -> "Marquer Livré"
            }
            Toast.makeText(context, "▶ $modeLabel — ${m2Client.nom}", Toast.LENGTH_LONG).show()

            when (currentMode) {
                ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> {
                    val datasValue = aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue

                    val onCommandBon_ventPeriod = datasValue.lastOrNull {
                        it.parent_M2Client_KeyID == m2Client.keyID
                                &&
                                it.parent_M14VentPeriod_KeyId == (aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode
                            ?.keyID ?: "")
                                && it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                    }

                    if (M00CentralParametresOfAllApps.get_Default().its_AppType != AppType.AllInOne) {
                        onCommandBon_ventPeriod?.let {
                            aCentralFacade.focusedActiveValuesFacade
                                .focusedValuesSetter
                                .setIN_M9CurrentApp_onVentM8BonVentKey(
                                    it
                                )
                            fragmentNavigationHandler_NewProto.navigateTo(
                                Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4
                            )
                        } ?: Toast.makeText(
                            context,
                            "Aucune onCommandBon_ventPeriod",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        if (onCommandBon_ventPeriod != null) {
                            aCentralFacade.focusedActiveValuesFacade
                                .focusedValuesSetter
                                .setIN_M9CurrentApp_onVentM8BonVentKey(
                                    onCommandBon_ventPeriod
                                )
                            fragmentNavigationHandler.navigateToCartScreen()
                        } else {
                            Toast.makeText(
                                context,
                                "Aucune commande en cours pour ce client",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    true
                }
                ActiveCentralValues.Click_On_Marque.Standart -> {
                    val clickedMarkerM2Client =
                        repo.datasValue.find { it.id.toString() == clickedMarker.id }

                    viewModel.set_M2Client_UiState_In_MarkerStatusDialog(clickedMarkerM2Client)

                    if (showMarkerDetails) clickedMarker.showInfoWindow()
                    true
                }

                // Add client to targeting list
                ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> {
                    val found_Or_Default = get_Found_Or_Default_M8BonVent(
                        aCentralFacade = aCentralFacade,
                        relative_M2Client = m2Client,
                        etateActuellementEst = M8BonVent.EtateActuellementEst.Cible,
                    ) ?: run {
                        Toast.makeText(context, "Période non initialisée", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnMarkerClickListener true
                    }

                    aCentralFacade.repositorysMainSetter
                        .addNew_M8BonVent(
                            found_Or_Default.default_If_No_Found
                                .copy(
                                    position_Don_Lis_Cible_Clients_au_VentPeriod = newPosition
                                )
                        )

                    focusedValuesGetter.update_activeCentralValues(
                        activeCentralValues.copy(
                            actuelle_Ciblage_MaxPosition = newPosition
                        )
                    )

                    Toast.makeText(
                        context,
                        "Client ajouté à la liste de ciblage (Position: $newPosition)",
                        Toast.LENGTH_SHORT
                    ).show()

                    true
                }

                // Show on-command transaction

                // Direct phone call to client
                ActiveCentralValues.Click_On_Marque.Call -> {
                    val phoneNumber = m2Client.numTelephone

                    if (phoneNumber.isNotEmpty() && phoneNumber != "null") {
                        try {
                            val truecallerIntent = Intent(
                                Intent.ACTION_DIAL,
                                Uri.fromParts("tel", phoneNumber, null)
                            ).apply {
                                setPackage("com.truecaller")
                            }

                            val packageManager = context.packageManager
                            val isTruecallerInstalled =
                                truecallerIntent.resolveActivity(packageManager) != null

                            if (isTruecallerInstalled) {
                                context.startActivity(truecallerIntent)
                                Toast.makeText(
                                    context,
                                    "Appel vers ${m2Client.nom} via Truecaller",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val defaultDialerIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$phoneNumber")
                                }
                                context.startActivity(defaultDialerIntent)

                                Toast.makeText(
                                    context,
                                    "Appel vers ${m2Client.nom} (Truecaller non installé)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Impossible de lancer l'appel",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Aucun numéro de téléphone pour ${m2Client.nom}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    true
                }

                // Navigate to client using Google Maps
                ActiveCentralValues.Click_On_Marque.Navigate -> {
                    val latitude = m2Client.latitude.takeIf { it != 0.0 } ?: DEFAULT_LATITUDE
                    val longitude = m2Client.longitude

                    try {
                        val gmmIntentUri =
                            Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }

                        context.startActivity(mapIntent)

                        Toast.makeText(
                            context,
                            "Navigation vers ${m2Client.nom}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        try {
                            val geoUri = Uri.parse(
                                "geo:$latitude,$longitude?q=$latitude,$longitude(${m2Client.nom})"
                            )
                            val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri)
                            context.startActivity(fallbackIntent)
                        } catch (e2: Exception) {
                            Toast.makeText(
                                context,
                                "Aucune application de navigation disponible",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    true
                }

                // Mark client as closed/fermé
                ActiveCentralValues.Click_On_Marque.Marck_Ferme -> {
                    val found_Or_Default = get_Found_Or_Default_M8BonVent(
                        aCentralFacade = aCentralFacade,
                        relative_M2Client = m2Client,
                        etateActuellementEst = M8BonVent.EtateActuellementEst.FERME,
                    ) ?: run {
                        Toast.makeText(context, "Période non initialisée", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnMarkerClickListener true
                    }

                    aCentralFacade.repositorysMainSetter
                        .addNew_M8BonVent(found_Or_Default.default_If_No_Found)

                    Toast.makeText(
                        context,
                        "${m2Client.nom} marqué comme fermé",
                        Toast.LENGTH_SHORT
                    ).show()

                    true
                }

                // Mark client command as delivered (livré)
                ActiveCentralValues.Click_On_Marque.Marck_Command_Livret -> {
                    val datasValue = aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue

                    val onCommandBon_ventPeriod = datasValue.lastOrNull {
                        it.parent_M2Client_KeyID == m2Client.keyID
                                &&
                                it.parent_M14VentPeriod_KeyId == (aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode
                            ?.keyID ?: "")
                                && it.etateActuellementEst == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
                    }

                    if (onCommandBon_ventPeriod != null) {
                        aCentralFacade.repositorysMainSetter
                            .addNew_M8BonVent(
                                onCommandBon_ventPeriod.copy(
                                    etateActuellementEst = M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                                )
                            )

                        Toast.makeText(
                            context,
                            "Commande de ${m2Client.nom} marquée comme livrée",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Aucune commande confirmée à livrer pour ce client",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    true
                }
            }
        }
    }

    mapView.overlays.add(marker)

    if (showMarkerDetails) {
        marker.showInfoWindow()
    }
}

private fun Marker.title(
    viewModel: MapClientsViewModel,
    m2Client: M2Client,
) {
    val relative_M8Transaction = viewModel.getLastTransaction(m2Client)

    val sumBonVents = relative_M8Transaction?.let { get_sum_Bon_Vents(viewModel.getter, it) }

    val position = relative_M8Transaction?.position_Don_Lis_Cible_Clients_au_VentPeriod ?: 0
    val positionPrefix = if (position != 0) "[$position] " else ""

    title =
        if (viewModel.afficheLesJoursAuNoms && position == 0) {
            val dateHandler = DatesHandler()
            val timeStr = relative_M8Transaction?.creationTimestamps?.let {
                dateHandler.getDateAndTimString(it).time
            }
            val dayName = dateHandler.getArabicDayNameFromTimestamp(
                relative_M8Transaction?.creationTimestamps ?: 0
            )
            val distanceSemain =
                dateHandler.getAbrgDistanceSemain(relative_M8Transaction?.creationTimestamps)

            if (relative_M8Transaction != null) {
                val text = " بالتقريب$sumBonVents"
                val texy_Safe = text.takeIf { sumBonVents!! > 0.0 } ?: ""
                val demande_Versemet_si_Type = relative_M8Transaction.demande_Versemet_si_Type
                    .takeIf { relative_M8Transaction.demande_Versemet_si_Type > 0.0 } ?: ""

                "$distanceSemain.$dayName (${timeStr})" +
                        "\n${relative_M8Transaction.etateActuellementEst.nomArabe}" +
                        texy_Safe +
                        demande_Versemet_si_Type +
                        "\n${
                            m2Client.nom.split(" ")
                                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                        } ${
                            if (m2Client.numTelephone.isNotEmpty()) "📞${
                                m2Client.numTelephone.takeLast(
                                    2
                                )
                            }" else ""
                        }"
            } else {
                m2Client.nom
            }
        } else {
            if (position != 0 && relative_M8Transaction != null) {
                "$positionPrefix${relative_M8Transaction.etateActuellementEst.nomArabe}" +
                        "\n${m2Client.nom}"
            } else {
                "$positionPrefix${m2Client.nom}"
            }
        }
}

fun configureMarkerInfoWindow(
    marker: Marker,
    mapView: MapView,
    context: Context,
    viewModel: MapClientsViewModel,
    client: M2Client,
) {
    val markerInfoWindowLayout = xmlResources
        .find { it.first == "marker_info_window" }?.second

    if (markerInfoWindowLayout == null) {
        throw IllegalStateException("marker_info_window layout not found")
    }

    marker.infoWindow = MarkerInfoWindow(markerInfoWindowLayout, mapView)

    val containerResourceId = xmlResources
        .find { it.first == "info_window_container" }?.second

    if (containerResourceId == null) {
        throw IllegalStateException("info_window_container ID not found")
    }

    val container = marker.infoWindow.view.findViewById<LinearLayout>(containerResourceId)
    container?.let { it ->
        val backgroundColor = viewModel.getLastTransaction(client)?.let {
            ContextCompat.getColor(
                context,
                viewModel.getLastTransaction(client)!!.etateActuellementEst.color
            )
        }

        if (backgroundColor != null) {
            it.setBackgroundColor(backgroundColor)
        }

        val titleTextViewId = xmlResources
            .find { it.first == "title" }?.second
        titleTextViewId?.let { titleId ->
            val titleTextView =
                marker.infoWindow.view.findViewById<android.widget.TextView>(titleId)
            titleTextView?.gravity = android.view.Gravity.CENTER
        }
    }
}

private fun preserveLocationOverlay(mapView: MapView): Any? {
    return mapView.overlays.find { overlay ->
        overlay.javaClass.simpleName.contains("Location") ||
                overlay.toString().contains("location", ignoreCase = true)
    }
}

private fun restoreLocationOverlayAtBottom(mapView: MapView, locationOverlay: Any?) {
    locationOverlay?.let { overlay ->
        mapView.overlays.remove(overlay)
        mapView.overlays.add(0, overlay as Overlay?)
    }
}

/** Haversine distance in metres between two lat/lng points. */
private fun haversineMeters(
    lat1: Double, lng1: Double,
    lat2: Double, lng2: Double,
): Double {
    val r = 6_371_000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}
