package f_Wifi


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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.clientjetpack.PermissionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

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

    init {
        Log.d(TAG, "🚀 Initializing P2PManager")
        checkWifiAndPermissions()
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
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
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

    private suspend fun startDiscoveryWithRetry(attempts: Int = 3) {
        var currentAttempt = 0
        while (currentAttempt < attempts) {
            currentAttempt++
            Log.d(TAG, "🔍 Starting discovery attempt $currentAttempt/$attempts")

            if (startDiscoveryAttempt()) {
                break
            } else {
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private suspend fun startDiscoveryAttempt(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!checkRequiredPermissions()) {
                Log.e(TAG, "❌ Missing required permissions for discovery")
                return@withContext false
            }

            var success = false
            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "✨ Discovery started successfully")
                    success = true
                    updateStatus(P2PStatus(
                        message = if (isHost) "En attente de connexion..." else "Recherche d'un hôte...",
                        isConnected = false,
                        isHost = isHost
                    ))
                }
                override fun onFailure(reason: Int) {
                    Log.e(TAG, "💥 Discovery failed with reason: $reason")
                    success = false
                    handleDiscoveryFailure(reason)
                }
            })
            success
        } catch (e: SecurityException) {
            Log.e(TAG, "⛔ Security exception during discovery: ${e.localizedMessage}")
            false
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

    fun toggleRole() {
        if (!checkRequiredPermissions()) {
            updateStatus(P2PStatus(
                message = "Permissions requises",
                isConnected = false,
                isHost = isHost
            ))
            return
        }

        isHost = !isHost
        disconnect()
        scope.launch {
            startDiscoveryWithRetry()
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
data class UiState(
    val scrollPosition: Int = 0,
    val isConnected: Boolean = false,
    val connectionStatus: String = "Déconnecté",
    val isHost: Boolean = true
)
