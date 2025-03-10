package Z_MasterOfApps.Kotlin._WorkingON.WO_.Conexion

import Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephone
import Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephoneRepository
import Z_MasterOfApps.Kotlin._WorkingON.WO_.ConnectionManager
import Z_MasterOfApps.Kotlin._WorkingON.WO_.PermissionHandler
import android.content.Context
import android.os.Build
import androidx.lifecycle.viewModelScope
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Components class that handles device management and connection initialization functions
 * extracted from ConnectionManager
 */
class ConnectionComponents(
    private val context: Context,
    private val connectionManager: ConnectionManager,
    private val j_AppInstalleDonTelephoneRepository: J_AppInstalleDonTelephoneRepository
) {
    private val serviceId = "com.example.clientjetpack"
    private val strategy = Strategy.P2P_POINT_TO_POINT

    /**
     * Initializes the module by loading repository and setting up device connections
     */
    suspend fun initializeModule() {
        connectionManager.logI("Initializing ConnectionComponents")

        val timeout = 60000L // 60 second timeout as a safety measure
        val startTime = System.currentTimeMillis()

        while (j_AppInstalleDonTelephoneRepository.progressRepo.value < 1.0f) {
            // Check if we've exceeded the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                connectionManager.logE("Repository loading timeout after ${timeout / 1000} seconds")
                break
            }

            connectionManager.logI("Waiting for repository to load, progress: ${j_AppInstalleDonTelephoneRepository.progressRepo.value}")
            delay(500) // Check every half second

            // Exit if the coroutine is cancelled
            if (!isActive) return
        }

        connectionManager.logI("Repository loaded, progress: ${j_AppInstalleDonTelephoneRepository.progressRepo.value}")

        // Get current device name without potential extras
        val manufacturerModel = "${Build.MANUFACTURER} ${Build.MODEL}"
        connectionManager.logI("Raw device name: $manufacturerModel")

        // Clean up device name (remove potential extras like "pk" suffix)
        val currentDeviceName = manufacturerModel.trim().split(" ").take(4).joinToString(" ")
        connectionManager.logI("Cleaned current device name: $currentDeviceName")

        // Check for and clean up duplicate entries
        cleanupDuplicateDevices(currentDeviceName)

        // Log available devices in repository for debugging
        connectionManager.logI("Available devices in repository after cleanup: ${j_AppInstalleDonTelephoneRepository.modelDatas.map { "${it.id}: ${it.infosDeBase.nom}" }}")

        // More flexible device matching
        val currentPhone = findCurrentDevice(currentDeviceName)

        if (currentPhone != null) {
            connectionManager.logI("Found current phone in repository: ${currentPhone.id} - ${currentPhone.infosDeBase.nom}")
            // Check if this is a receiver phone
            val isReceiver = currentPhone.etatesMutable.itsReciverTelephone
            connectionManager.logI("Is this a receiver phone? $isReceiver")

            if (!isReceiver) {
                // This is a host device
                connectionManager.logI("This is a host device, setting up host configuration")
                currentPhone.etatesMutable.nearbyWifiAdressIpConexion =
                    "host_${currentDeviceName.replace(" ", "_")}"
                j_AppInstalleDonTelephoneRepository.updatePhones()
                connectionManager.logI("Set nearbyWifiAdressIpConexion to: ${currentPhone.etatesMutable.nearbyWifiAdressIpConexion}")

                // Start as host
                connectionManager.logI("Starting as host")
                startAsHost(connectionManager.connectionLifecycleCallback, connectionManager.permissionHandler)
                connectionManager.lastConnectionMode = ConnectionManager.ConnectionMode.HOST
            } else {
                connectionManager.logI("This is a client device, looking for host phone")
                val hostPhone = j_AppInstalleDonTelephoneRepository.modelDatas.find {
                    !it.etatesMutable.itsReciverTelephone && it.etatesMutable.nearbyWifiAdressIpConexion.isNotEmpty()
                }

                if (hostPhone != null) {
                    connectionManager.logI("Found host phone: ${hostPhone.infosDeBase.nom} with connection: ${hostPhone.etatesMutable.nearbyWifiAdressIpConexion}")
                    currentPhone.etatesMutable.nearbyWifiAdressIpConexion =
                        hostPhone.etatesMutable.nearbyWifiAdressIpConexion
                    j_AppInstalleDonTelephoneRepository.updatePhones()
                    connectionManager.logI("Set client nearbyWifiAdressIpConexion to: ${currentPhone.etatesMutable.nearbyWifiAdressIpConexion}")

                    // Start as client
                    connectionManager.logI("Starting as client")
                    connectionManager.startAsClient()
                    connectionManager.lastConnectionMode = ConnectionManager.ConnectionMode.CLIENT
                } else {
                    connectionManager.logE("No host phone found in repository!")
                    connectionManager.logI("Converting this device to a host as fallback")

                    // Convert this device to a host
                    currentPhone.etatesMutable.itsReciverTelephone = false
                    currentPhone.etatesMutable.nearbyWifiAdressIpConexion =
                        "host_${currentDeviceName.replace(" ", "_")}"
                    j_AppInstalleDonTelephoneRepository.updatePhones()

                    startAsHost(connectionManager.connectionLifecycleCallback, connectionManager.permissionHandler)
                    connectionManager.lastConnectionMode = ConnectionManager.ConnectionMode.HOST
                }
            }

            // Set up connection monitoring
            connectionManager.startConnectionMonitoring()
        } else {
            connectionManager.logE("Current phone not found in repository! Device: $currentDeviceName")
            connectionManager.logI("Attempting to register the device automatically")

            // Auto-register this device if possible
            val newPhone = createNewPhoneEntry(currentDeviceName)
            if (newPhone != null) {
                connectionManager.logI("Successfully registered new device with ID: ${newPhone.id}")

                // Set up as host by default for a new device
                newPhone.etatesMutable.nearbyWifiAdressIpConexion =
                    "host_${currentDeviceName.replace(" ", "_")}"
                j_AppInstalleDonTelephoneRepository.updatePhones()

                connectionManager.logI("Starting as host for new device")
                startAsHost(connectionManager.connectionLifecycleCallback, connectionManager.permissionHandler)
                connectionManager.lastConnectionMode = ConnectionManager.ConnectionMode.HOST

                // Start monitoring
                connectionManager.startConnectionMonitoring()
            }
        }
    }

    /**
     * Find the current device in the repository
     */
    private fun findCurrentDevice(currentDeviceName: String): J_AppInstalleDonTelephone? {
        return j_AppInstalleDonTelephoneRepository.modelDatas.find { phone ->
            // Try exact match first
            if (phone.infosDeBase.nom == currentDeviceName) return@find true

            // Then try case-insensitive contains match
            if (phone.infosDeBase.nom.contains(
                    currentDeviceName,
                    ignoreCase = true
                )
            ) return@find true

            // Then try matching just the model part if it's distinctive enough
            val model = Build.MODEL.trim()
            if (model.length > 3 && phone.infosDeBase.nom.contains(
                    model,
                    ignoreCase = true
                )
            ) return@find true

            false
        }
    }

    /**
     * Clean up duplicate device entries in the repository
     */
    fun cleanupDuplicateDevices(deviceName: String) {
        // Find all entries matching this device
        val matchingDevices = j_AppInstalleDonTelephoneRepository.modelDatas.filter { phone ->
            phone.infosDeBase.nom == deviceName ||
                    phone.infosDeBase.nom.contains(deviceName, ignoreCase = true) ||
                    deviceName.contains(phone.infosDeBase.nom, ignoreCase = true)
        }

        if (matchingDevices.size > 1) {
            connectionManager.logI("Found ${matchingDevices.size} potential duplicate entries for '$deviceName'")

            // Keep the first one and remove others
            val deviceToKeep = matchingDevices.first()
            matchingDevices.drop(1).forEach { duplicate ->
                connectionManager.logI("Removing duplicate device: ID ${duplicate.id} - ${duplicate.infosDeBase.nom}")
                j_AppInstalleDonTelephoneRepository.modelDatas.remove(duplicate)
            }

            // Update repository
            j_AppInstalleDonTelephoneRepository.updatePhones()
            connectionManager.logI("Repository cleaned, kept device ID: ${deviceToKeep.id}")
        }
    }

    /**
     * Create a new phone entry in the repository
     */
    fun createNewPhoneEntry(deviceName: String): J_AppInstalleDonTelephone? {
        try {
            // Find the highest ID currently in use
            val maxId = j_AppInstalleDonTelephoneRepository.modelDatas.maxOfOrNull { it.id } ?: 0
            val newId = maxId + 1

            // Create new phone entry
            val newPhone = J_AppInstalleDonTelephone(newId).apply {
                infosDeBase.nom = deviceName
                // Get screen width in dp
                val displayMetrics = context.resources.displayMetrics
                val widthInDp = (displayMetrics.widthPixels / displayMetrics.density).toInt()
                infosDeBase.widthScreen = widthInDp

                // If it's a tablet or has "TAB" in the name, make it a receiver
                val isTablet = widthInDp > 400 || deviceName.contains("TAB", ignoreCase = true)
                infosDeBase.itsTablette = isTablet
                etatesMutable.itsReciverTelephone = isTablet

                // If it's a receiver, set nearbyWifiAdressIpConexion to empty to start as client
                if (isTablet) {
                    etatesMutable.nearbyWifiAdressIpConexion = ""
                } else {
                    etatesMutable.nearbyWifiAdressIpConexion = "host_${deviceName.replace(" ", "_")}"
                }
            }

            // Add to repository
            j_AppInstalleDonTelephoneRepository.modelDatas.add(newPhone)
            j_AppInstalleDonTelephoneRepository.updatePhones()

            return newPhone
        } catch (e: Exception) {
            connectionManager.logE("Failed to create new phone entry", e)
            return null
        }
    }

    /**
     * Start the device as a client looking for connections
     */
    fun startAsClient(connectionLifecycleCallback: com.google.android.gms.nearby.connection.ConnectionLifecycleCallback, 
                      endpointDiscoveryCallback: com.google.android.gms.nearby.connection.EndpointDiscoveryCallback,
                      permissionHandler: PermissionHandler) {
        connectionManager.viewModelScope.launch {
            connectionManager.logI("Starting as client")
            if (!permissionHandler.checkRequiredPermissions()) {
                connectionManager.logE("Missing required permissions for Nearby Connections")
                val missingPermissions = permissionHandler.getRequiredPermissions().filter { permission ->
                    androidx.core.content.ContextCompat.checkSelfPermission(context, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED
                }
                connectionManager.logE("Missing permissions: $missingPermissions")
                connectionManager.handleError("Permissions manquantes")
                return@launch
            }

            connectionManager.logI("All required permissions granted")
            connectionManager.lastConnectionMode = ConnectionManager.ConnectionMode.CLIENT
            connectionManager._connectionUiState.update { it.copy(isHostPhone = false) }

            try {
                // First stop any existing discovery/connections
                connectionManager.cleanupExistingConnections()

                val discoveryOptions = DiscoveryOptions.Builder()
                    .setStrategy(strategy)
                    .build()

                connectionManager.logI("Starting discovery with service ID: $serviceId and strategy: $strategy")
                Nearby.getConnectionsClient(context).startDiscovery(
                    serviceId,
                    endpointDiscoveryCallback,
                    discoveryOptions
                ).addOnSuccessListener {
                    connectionManager.isDiscovering = true
                    connectionManager.logI("Successfully started discovery as client")
                    connectionManager.updateConnectionStatus("Recherche d'appareils...")
                }.addOnFailureListener { e ->
                    connectionManager.isDiscovering = false
                    connectionManager.logE("Failed to start discovery: ${e.message}", e)
                    connectionManager.handleConnectionFailure("Erreur de démarrage de la recherche: ${e.message}")
                }
            } catch (e: Exception) {
                connectionManager.logE("Exception during discovery: ${e.message}", e)
                connectionManager.handleConnectionFailure(e.message ?: "Erreur inconnue")
            }
        }
    }
    
    /**
     * Start the device as a host advertising for connections
     */
    fun startAsHost(connectionLifecycleCallback: com.google.android.gms.nearby.connection.ConnectionLifecycleCallback,
                    permissionHandler: PermissionHandler
    ) {
        connectionManager.viewModelScope.launch {
            connectionManager.logI("Starting as host")
            if (!permissionHandler.checkRequiredPermissions()) {
                connectionManager.logE("Missing required permissions for Nearby Connections")
                val missingPermissions = permissionHandler.getRequiredPermissions().filter { permission ->
                    androidx.core.content.ContextCompat.checkSelfPermission(context, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED
                }
                connectionManager.logE("Missing permissions: $missingPermissions")
                connectionManager.handleError("Permissions manquantes")
                return@launch
            }

            connectionManager.logI("All required permissions granted")
            connectionManager.lastConnectionMode = ConnectionManager.ConnectionMode.HOST
            connectionManager._connectionUiState.update { it.copy(isHostPhone = true) }

            try {
                // Clean up any existing connections before starting advertising
                connectionManager.cleanupExistingConnections()

                val advertisingOptions = AdvertisingOptions.Builder()
                    .setStrategy(strategy)
                    .build()

                connectionManager.logI("Starting advertising with service ID: $serviceId and strategy: $strategy")
                Nearby.getConnectionsClient(context).startAdvertising(
                    "Host Device",
                    serviceId,
                    connectionLifecycleCallback,
                    advertisingOptions
                ).addOnSuccessListener {
                    connectionManager.isAdvertising = true
                    connectionManager.logI("Successfully started advertising as host")
                    connectionManager.updateConnectionStatus("En attente de connexion...")
                }.addOnFailureListener { e ->
                    connectionManager.isAdvertising = false
                    connectionManager.logE("Failed to start advertising: ${e.message}", e)
                    connectionManager.handleConnectionFailure("Erreur de démarrage du mode hôte: ${e.message}")
                }
            } catch (e: Exception) {
                connectionManager.logE("Exception during advertising: ${e.message}", e)
                connectionManager.handleConnectionFailure(e.message ?: "Erreur inconnue")
            }
        }
    }
}
