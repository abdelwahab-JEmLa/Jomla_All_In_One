package z_LearnTryLibsWIfi


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.clientjetpack.PermissionHandler
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import kotlin.coroutines.resume

@RequiresApi(Build.VERSION_CODES.Q)
class P2PManager(
    private val context: Context,
    private val permissionHandler: PermissionHandler,
    private val onStatusUpdate: (P2PStatus) -> Unit
) {
    private val wifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel = wifiP2pManager.initialize(context, context.mainLooper, null)
    private var isHost = true
    private var connection: P2PConnection? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var receiver: BroadcastReceiver? = null
    private var discoveryActive = false

    init {
        Log.d(TAG, "🚀 Initializing P2PManager")
        checkWifiAndPermissions()
    }

    private fun ensureWifiEnabled(): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            updateStatus(P2PStatus(
                message = "WiFi désactivé. Veuillez activer le WiFi.",
                isConnected = false,
                isHost = isHost
            ))
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun startDiscoveryWithRetry(attempts: Int = 3) {
        if (!ensureWifiEnabled()) return

        var currentAttempt = 0
        while (currentAttempt < attempts && !discoveryActive) {
            currentAttempt++
            Log.d(TAG, "🔍 Starting discovery attempt $currentAttempt/$attempts")

            if (startDiscoveryAttempt()) {
                discoveryActive = true
                break
            } else {
                // Add exponential backoff
                val delayTime = (1000L * (1 shl (currentAttempt - 1))).coerceAtMost(5000L)
                delay(delayTime)

                // Check if WiFi P2P is enabled before retrying
                safeExecuteWithPermissions {
                    wifiP2pManager.requestP2pState(channel) { state ->
                        if (state != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                            updateStatus(P2PStatus(
                                message = "WiFi Direct désactivé. Veuillez l'activer dans les paramètres.",
                                isConnected = false,
                                isHost = isHost
                            ))
                            discoveryActive = false
                        }
                    }
                }
            }
        }

        if (!discoveryActive) {
            updateStatus(P2PStatus(
                message = "Échec de la découverte après $attempts tentatives. Veuillez réessayer.",
                isConnected = false,
                isHost = isHost
            ))
        }
    }

    private suspend fun startDiscoveryAttempt(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!checkRequiredPermissions()) {
                Log.e(TAG, "❌ Missing required permissions for discovery")
                return@withContext false
            }

            var result = CompletableDeferred<Boolean>()

            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "✨ Discovery started successfully")
                    updateStatus(P2PStatus(
                        message = if (isHost) "En attente de connexion..." else "Recherche d'un hôte...",
                        isConnected = false,
                        isHost = isHost
                    ))
                    result.complete(true)
                }

                override fun onFailure(reason: Int) {
                    Log.e(TAG, "💥 Discovery failed with reason: $reason")
                    handleDiscoveryFailure(reason)
                    result.complete(false)
                }
            })

            return@withContext result.await()
        } catch (e: SecurityException) {
            Log.e(TAG, "⛔ Security exception during discovery: ${e.localizedMessage}")
            return@withContext false
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun toggleRole() {
        if (!checkRequiredPermissions()) {
            updateStatus(P2PStatus(
                message = "Permissions requises",
                isConnected = false,
                isHost = isHost
            ))
            return
        }

        discoveryActive = false
        isHost = !isHost
        disconnect()
        scope.launch {
            startDiscoveryWithRetry()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkRequiredPermissions(): Boolean {
        val basicPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )

        val modernPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.NEARBY_WIFI_DEVICES)
        } else {
            emptyList()
        }

        return (basicPermissions + modernPermissions).all { permission ->
            checkPermission(permission)
        }
    }

    private fun safeExecuteWithPermissions(action: () -> Unit) {
        try {
            if (checkRequiredPermissions()) {
                action()
            } else {
                Log.e(TAG, "❌ Missing required permissions")
                updateStatus(P2PStatus(
                    message = "Permissions manquantes",
                    isConnected = false,
                    isHost = isHost
                ))
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "⛔ Security exception: ${e.localizedMessage}")
            updateStatus(P2PStatus(
                message = "Erreur de permission",
                isConnected = false,
                isHost = isHost
            ))
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkWifiAndPermissions() {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            updateStatus(P2PStatus(
                message = "WiFi désactivé",
                isConnected = false,
                isHost = isHost
            ))
            return
        }

        if (checkRequiredPermissions()) {
            registerP2PReceiver()
            scope.launch {
                startDiscoveryWithRetry()
            }
        }
    }

    private val peerListListener = WifiP2pManager.PeerListListener { peers ->
        Log.d(TAG, "👥 Found ${peers.deviceList.size} peers")
        handlePeerList(peers)
    }

    private fun handlePeerList(peers: WifiP2pDeviceList) {
        safeExecuteWithPermissions {
            peers.deviceList.forEach { device ->
                Log.d(TAG, "📱 Device: ${device.deviceName} (${device.deviceAddress})")
            }
        }
    }

    private val connectionInfoListener = WifiP2pManager.ConnectionInfoListener { info ->
        handleConnectionInfo(info)
    }

    private fun handleConnectionInfo(info: WifiP2pInfo) {
        safeExecuteWithPermissions {
            if (info.groupFormed && info.isGroupOwner == isHost) {
                connection = P2PConnection(isHost, info.groupOwnerAddress) { status ->
                    updateStatus(status)
                }
            }
        }
    }

    private fun createP2PReceiver() = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    handleWifiP2pStateChanged(intent)
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    safeExecuteWithPermissions {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.NEARBY_WIFI_DEVICES
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {

                            return@safeExecuteWithPermissions
                        }
                        wifiP2pManager.requestPeers(channel, peerListListener)
                    }
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    handleConnectionChanged(intent)
                }
            }
        }
    }

    private fun handleWifiP2pStateChanged(intent: Intent) {
        val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
        val isEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED

        updateStatus(P2PStatus(
            message = if (isEnabled) "WiFi P2P activé" else "WiFi P2P désactivé",
            isConnected = false,
            isHost = isHost
        ))
    }

    private fun handleConnectionChanged(intent: Intent) {
        safeExecuteWithPermissions {
            val networkInfo: NetworkInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO, NetworkInfo::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
            }

            if (networkInfo?.isConnected == true) {
                wifiP2pManager.requestConnectionInfo(channel, connectionInfoListener)
            } else {
                connection?.close()
                connection = null
                updateStatus(P2PStatus(
                    message = "Déconnecté",
                    isConnected = false,
                    isHost = isHost
                ))
            }
        }
    }

    private fun registerP2PReceiver() {
        safeExecuteWithPermissions {
            val intentFilter = IntentFilter().apply {
                addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
                addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            }

            receiver = createP2PReceiver()
            context.registerReceiver(receiver, intentFilter)
        }
    }



    fun disconnect() {
        safeExecuteWithPermissions {
            connection?.close()
            connection = null
            wifiP2pManager.removeGroup(channel, null)
            updateStatus(P2PStatus(
                message = "Déconnecté",
                isConnected = false,
                isHost = isHost
            ))
        }
    }


    fun sendScrollPosition(position: Int) {
        if (checkRequiredPermissions()) {
            connection?.sendPosition(position)
        }
    }

    private fun handleDiscoveryFailure(reason: Int) {
        val errorMsg = when(reason) {
            WifiP2pManager.P2P_UNSUPPORTED -> "P2P non supporté"
            WifiP2pManager.ERROR -> "Erreur générale"
            WifiP2pManager.BUSY -> "Système occupé"
            else -> "Erreur inconnue"
        }
        updateStatus(P2PStatus(
            message = "Échec découverte: $errorMsg",
            isConnected = false,
            isHost = isHost
        ))
    }

    private fun updateStatus(status: P2PStatus) {
        onStatusUpdate(status)
    }

    fun cleanup() {
        safeExecuteWithPermissions {
            receiver?.let {
                context.unregisterReceiver(it)
                receiver = null
            }
            disconnect()
        }
    }

    companion object {
        private const val TAG = "P2PManager"
    }


    private fun hasRequiredPermissions(): Boolean {
        val basicPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )

        val additionalPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
        } else {
            emptyArray()
        }

        val allPermissions = basicPermissions + additionalPermissions

        return allPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }




    fun startDiscovery() {
        Log.d(TAG, "\uD83D\uDD0D Starting discovery as ${if (isHost) "HOST" else "CLIENT"}")
        if (!permissionHandler.areNearbyPermissionsGranted()) {
            Log.d(TAG, "❌ Discovery failed: Missing permissions")
            updateStatus(P2PStatus(
                message = "Permissions manquantes",
                isConnected = false,
                isHost = isHost
            ))
            return
        }

        try {
            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "✨ Discovery started successfully")
                    updateStatus(P2PStatus(
                        message = if (isHost) "En attente de connexion..." else "Recherche d'un hôte...",
                        isConnected = false,
                        isHost = isHost
                    ))
                }
                override fun onFailure(reason: Int) {
                    Log.d(TAG, "\uD83D\uDCA5 Discovery failed with reason: $reason")
                    handleDiscoveryFailure(reason)
                }
            })
        } catch (e: SecurityException) {
            Log.e(TAG, "⛔ Security exception during discovery: ${e.localizedMessage}")
            updateStatus(P2PStatus(
                message = "Erreur de permission: ${e.localizedMessage}",
                isConnected = false,
                isHost = isHost
            ))
        }
    }

}

data class P2PStatus(
    val message: String,
    val isConnected: Boolean,
    val isHost: Boolean
)


private class P2PConnection(
    private val isHost: Boolean,
    private val groupOwnerAddress: InetAddress,
    private val onStatusUpdate: (P2PStatus) -> Unit
) {
    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val PORT = 8888

    init {
        Log.d(TAG, "\uD83D\uDD17 Initializing P2PConnection as ${if (isHost) "HOST" else "CLIENT"}")
        scope.launch {
            setupConnection()
        }
    }

    private suspend fun setupConnection() = withContext(Dispatchers.IO) {
        try {
            if (isHost) {
                Log.d(TAG, "\uD83D\uDCE5 HOST: Creating server socket on port $PORT")
                serverSocket = ServerSocket(PORT)
                onStatusUpdate(P2PStatus(
                    message = "En attente de connexion...",
                    isConnected = false,
                    isHost = isHost
                ))
                Log.d(TAG, "⏳ HOST: Waiting for client connection")
                socket = serverSocket?.accept()
                Log.d(TAG, "✅ HOST: Client connected!")
            } else {
                Log.d(TAG, "\uD83D\uDCE4 CLIENT: Connecting to ${groupOwnerAddress.hostAddress}:$PORT")
                socket = Socket(groupOwnerAddress, PORT)
                Log.d(TAG, "✅ CLIENT: Connected to host!")
            }

            onStatusUpdate(P2PStatus(
                message = "Connecté",
                isConnected = true,
                isHost = isHost
            ))

        } catch (e: Exception) {
            Log.e(TAG, "💥 Connection failed: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTrace.joinToString("\n")}")
            onStatusUpdate(P2PStatus(
                message = "Erreur connexion: ${e.message}",
                isConnected = false,
                isHost = isHost
            ))
        }
    }

    fun sendPosition(position: Int) {
        scope.launch {
            try {
                Log.d(TAG, "\uD83D\uDCE2 Sending position: $position")
                socket?.let {
                    ObjectOutputStream(it.getOutputStream()).writeInt(position)
                    Log.d(TAG, "✅ Position sent successfully")
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 Failed to send position: ${e.message}")
                close()
            }
        }
    }

    fun close() {
        Log.d(TAG, "\uD83D\uDD34 Closing P2P connection")
        socket?.close()
        serverSocket?.close()
        scope.cancel()
    }

    companion object {
        private const val TAG = "P2PConnection"
    }

}

class P2PDiagnostics(private val context: Context) {
    private val wifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel = wifiP2pManager.initialize(context, context.mainLooper, null)
    private val TAG = "P2PDiagnostics"

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun runDiagnostics(): String = withContext(Dispatchers.IO) {
        val diagnostics = StringBuilder()

        try {
            // Basic WiFi Check
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val isWifiEnabled = wifiManager.isWifiEnabled
            Log.d(TAG, "📡 WiFi Enabled: $isWifiEnabled")
            diagnostics.append("WiFi Enabled: $isWifiEnabled\n")

            // P2P State Check
            val p2pState = suspendCancellableCoroutine<Int> { continuation ->
                wifiP2pManager.requestP2pState(channel) { state ->
                    continuation.resume(state)
                }
            }
            val stateStr = when (p2pState) {
                WifiP2pManager.WIFI_P2P_STATE_ENABLED -> "ENABLED"
                WifiP2pManager.WIFI_P2P_STATE_DISABLED -> "DISABLED"
                else -> "UNKNOWN"
            }
            Log.d(TAG, "🔄 P2P State: $stateStr")
            diagnostics.append("WiFi P2P State: $stateStr\n")

            // Permission Check
            val permissions = checkPermissions()
            Log.d(TAG, "🔐 Permissions Check: ${permissions.joinToString()}")
            diagnostics.append("Permissions Status:\n${permissions.joinToString("\n")}\n")

            // Detailed Group Info Check
            val groupInfo = suspendCancellableCoroutine<String> { continuation ->
                try {
                    wifiP2pManager.requestGroupInfo(channel) { group ->
                        val info = if (group != null) {
                            val clientList = group.clientList.joinToString("\n") { client ->
                                "- Device: ${client.deviceName} (${client.deviceAddress})"
                            }

                            Log.d(TAG, "✅ Group found with ${group.clientList.size} clients")

                            """
                            Active P2P Group: YES
                            Network Name: ${group.networkName}
                            Is Group Owner: ${group.isGroupOwner}
                            Connected Clients: ${group.clientList.size}
                            Interface: ${group.getInterface()}
                            
                            Client Details:
                            $clientList
                            """.trimIndent()
                        } else {
                            Log.d(TAG, "❌ No active P2P group")
                            "Active P2P Group: NO\n"
                        }
                        continuation.resume(info)
                    }
                } catch (e: SecurityException) {
                    Log.e(TAG, "⛔ Security Exception during group info request", e)
                    continuation.resume("Failed to get group info: ${e.message}\n")
                }
            }
            diagnostics.append(groupInfo)

            // Network Interface Check
            try {
                val networkInterfaces = NetworkInterface.getNetworkInterfaces().toList()
                    .filter { it.name.startsWith("p2p") || it.name.startsWith("wlan") }

                if (networkInterfaces.isNotEmpty()) {
                    diagnostics.append("\nNetwork Interfaces:\n")
                    networkInterfaces.forEach { netInterface ->
                        val hwAddress = netInterface.hardwareAddress?.joinToString(":") {
                            "%02x".format(it)
                        } ?: "Not Available"

                        Log.d(TAG, "📶 Interface ${netInterface.name} - Up: ${netInterface.isUp}")
                        diagnostics.append("- ${netInterface.name}: ${netInterface.isUp}\n")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 Network Interface Error", e)
                diagnostics.append("Failed to get network interfaces: ${e.message}\n")
            }

        } catch (e: Exception) {
            Log.e(TAG, "💥 General diagnostics error", e)
            diagnostics.append("Error running diagnostics: ${e.message}\n")
        }

        return@withContext diagnostics.toString()
    }

    private fun checkPermissions(): List<String> {
        val requiredPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION to "Location",
            Manifest.permission.ACCESS_WIFI_STATE to "WiFi State",
            Manifest.permission.CHANGE_WIFI_STATE to "WiFi Change",
            Manifest.permission.INTERNET to "Internet"
        ) + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.NEARBY_WIFI_DEVICES to "Nearby Devices")
        } else {
            emptyList()
        }

        return requiredPermissions.map { (permission, name) ->
            val granted = ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
            "$name: ${if (granted) "✓" else "✗"}"
        }
    }
}
data class UiState(
    val scrollPosition: Int = 0,
    val isConnected: Boolean = false,
    val connectionStatus: String = "Déconnecté",
    val isHost: Boolean = true
)
