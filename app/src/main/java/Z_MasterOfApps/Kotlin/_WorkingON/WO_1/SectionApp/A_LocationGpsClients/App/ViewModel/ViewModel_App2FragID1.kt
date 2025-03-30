package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel

import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase.DernierEtatAAffiche
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase.TypeDeSonMagasine
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.BProto_ClientsDataBaseRepository
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.Parent.AppSettingsSaverModel
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.views.MapView
import java.util.Date

class ViewModel_App2FragID1(
    private val repository: BProto_ClientsDataBaseRepository,
) : ViewModel() {
    private val TAG = "ViewModel_App2FragID1" // Tag for logging

    // Expose clients as read-only StateFlow for better encapsulation
    private val _clients = repository.modelDatas
    val clients: SnapshotStateList<BProto_ClientsDataBase> get() = _clients

    // Expose progress as StateFlow
    private val _progress: MutableStateFlow<Float> = repository.progressRepo
    val progress: StateFlow<Float> get() = _progress

    // Current filter type for client display
    private var _currentFilter = VisibleClientsFilter.SHOW_ALL
    val currentFilter: VisibleClientsFilter get() = _currentFilter

    // Type to use when creating new clients
    var selectedClientType by mutableStateOf(TypeDeSonMagasine.ATAYAT_MOUKASSARAT)
        private set

    // Initialize and log the client count
    init {
        viewModelScope.launch {
            _clients.apply {
                Log.d(TAG, "ViewModel initialized with ${_clients.size} clients")
            }
        }
    }

    /**
     * Updates a single client without recreating the entire list
     * @param client The client to update
     */
    fun updateClient(client: BProto_ClientsDataBase) {
        Log.d(TAG, "Updating client: ${client.id} - ${client.nom}")
        repository.updateData(client)
    }

    /**
     * Updates a client and triggers a complete reload of the client list
     * @param client The client with updated data
     */
    fun updateClientWithReload(client: BProto_ClientsDataBase) {
        Log.d(TAG, "Updating client with reload: ${client.id} - ${client.nom}")

        // Create a deep copy of the current list
        val updatedClients = mutableStateListOf<BProto_ClientsDataBase>()

        // Update the specific client while preserving others
        for (existingClient in _clients.toList()) {
            if (existingClient.id == client.id) {
                // Create a copy with updated values
                val updatedClient = BProto_ClientsDataBase().apply {
                    // Copy all properties from client
                    id = client.id

                    // Section InfosBase
                    nom = client.nom

                    // Section Etates Mutable
                    numTelephone = client.numTelephone
                    couleur = client.couleur
                    bonDuClientsSu = client.bonDuClientsSu
                    currentCreditBalance = client.currentCreditBalance
                    positionDonClientsList = client.positionDonClientsList
                    cUnClientTemporaire = client.cUnClientTemporaire
                    auFilterFAB = client.auFilterFAB
                    typeDeSonMagasine = client.typeDeSonMagasine
                    clientTypeMode = client.clientTypeMode

                    // Section GpsLocation
                    latitude = client.latitude
                    longitude = client.longitude
                    title = client.title
                    snippet = client.snippet
                    actuelleEtat = client.actuelleEtat
                }
                updatedClients.add(updatedClient)
            } else {
                updatedClients.add(existingClient)
            }
        }

        // Update entire repository with new list
        viewModelScope.launch {
            repository.updateDatas(updatedClients.toMutableStateList())
        }
    }

    /**
     * Sets the current client type to be used when creating new clients
     * @param type The type to set
     */
    fun setClientType(type: TypeDeSonMagasine) {
        selectedClientType = type
        Log.d(TAG, "Client type set to: $type")
    }

    /**
     * Creates and adds a new client marker at the center of the map
     * @param mapView The map to center the marker on
     */
    fun addClientMarker(mapView: MapView) {
        val center = mapView.mapCenter

        // Validate latitude
        if (center.latitude == 0.0 && center.longitude == 0.0) {
            Log.e(TAG, "Invalid map center coordinates")
            return
        }

        // Generate unique ID
        val newId = generateUniqueClientId()
        val clientName = "ز.$newId"

        Log.d(TAG, "Creating new client: ID=$newId, Name=$clientName at Lat=${center.latitude}, Lng=${center.longitude}")

        // Create new client with default values
        val newClient = BProto_ClientsDataBase().apply {
            id = newId
            nom = clientName

            // Set client properties
            cUnClientTemporaire = true
            typeDeSonMagasine = selectedClientType

            // Set location properties
            latitude = center.latitude
            longitude = center.longitude
            title = clientName
            snippet = "Client temporaire"
            actuelleEtat = DernierEtatAAffiche.CIBLE_PRIORITE_2
        }

        // Add to local repository
        repository.addData(newClient)

        // Sync with remote database
        B_ClientsDataBase.refClientsDataBase
            .child(newId.toString())
            .setValue(newClient)
            .addOnSuccessListener {
                Log.d(TAG, "Client $newId successfully added to Firebase")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to add client $newId to Firebase", e)
            }
    }

    /**
     * Generates a unique ID for a new client
     * @return A unique long ID
     */
    private fun generateUniqueClientId(): Long {
        val newId = if (_clients.isEmpty()) {
            1L // Start with 1 if the list is empty
        } else {
            // Find highest existing ID and increment by 1
            (_clients.maxOfOrNull { it.id } ?: 0L) + 1L
        }

        Log.d(TAG, "Generated new client ID: $newId")
        return newId
    }

    /**
     * Updates a long value in the app settings
     * @param value The new value
     * @param name The setting name
     */
    fun updateLongAppSetting(
        value: Long,
        name: String = "clientBuyerNowId",
    ) {
        viewModelScope.launch {
            try {
                val appSettingsSaverModel = AppSettingsSaverModel(
                    id = 1,
                    name = name,
                    valueLong = value,
                    date = Date()
                )

                Firebase.database.getReference("A_AppSettingsSaverModel")
                    .child(appSettingsSaverModel.id.toString())
                    .setValue(appSettingsSaverModel)
                    .await()

                Log.d(TAG, "Updated app setting: $name = $value")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update app setting: $name = $value", e)
            }
        }
    }

    /**
     * Sets the current client filter
     * @param filter The filter to apply
     */
    fun setClientFilter(filter: VisibleClientsFilter) {
        _currentFilter = filter
        Log.d(TAG, "Filter changed to: $filter")
    }

    /**
     * Gets clients filtered according to current filter setting
     * @return Filtered list of clients
     */
    fun getFilteredClients(): List<BProto_ClientsDataBase> {
        return when (_currentFilter) {
            VisibleClientsFilter.SHOW_NON_ABSENT ->
                _clients.filter { it.actuelleEtat != DernierEtatAAffiche.CLIENT_ABSENT }

            VisibleClientsFilter.SHOW_COMMENDABLE ->
                _clients.filter { it.actuelleEtat == DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT }

            VisibleClientsFilter.SHOW_ATAY_CLIENTS ->
                _clients.filter { it.typeDeSonMagasine == TypeDeSonMagasine.ATAYAT_MOUKASSARAT }

            VisibleClientsFilter.SHOW_TARGET_CLIENTS ->
                _clients.filter { it.actuelleEtat == DernierEtatAAffiche.CIBLE_POUR_2 }

            VisibleClientsFilter.SHOW_ALIMENTATION_CLIENTS ->
                _clients.filter { it.typeDeSonMagasine == TypeDeSonMagasine.AlIMENTATION_GENERALE }

            VisibleClientsFilter.SHOW_ALL -> _clients
        }
    }

    /**
     * Renamed from original for clarity
     * @param mapView The map view to center marker on
     */
    fun onClickAddMarkerButton(mapView: MapView) {
        addClientMarker(mapView)
    }

    /**
     * Enum defining possible client filter types
     */
    enum class VisibleClientsFilter(val icon: Any) {
        SHOW_NON_ABSENT(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl),
        SHOW_COMMENDABLE(LottieJsonGetterR_Raw_Icons.afficheFenetre),
        SHOW_ATAY_CLIENTS(LottieJsonGetterR_Raw_Icons.atay),
        SHOW_TARGET_CLIENTS(Icons.Default.CheckCircleOutline),
        SHOW_ALIMENTATION_CLIENTS(LottieJsonGetterR_Raw_Icons.alimentation),
        SHOW_ALL(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl);
    }
}
