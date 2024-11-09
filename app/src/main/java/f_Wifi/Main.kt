package j_Wifi

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException

// WifiDirectDiscoveryService.kt
class WifiDirectDiscoveryService(private val context: Context) {
    private val wifiP2pManager: WifiP2pManager by lazy {
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }
    private val channel: WifiP2pManager.Channel by lazy {
        wifiP2pManager.initialize(context, context.mainLooper, null)
    }

    sealed class DiscoveryEvent {
        data class DevicesFound(val devices: List<WifiP2pDevice>) : DiscoveryEvent()
        data class Connected(val device: WifiP2pDevice) : DiscoveryEvent()
        data class Error(val message: String) : DiscoveryEvent()
        object Started : DiscoveryEvent()
        object PermissionRequired : DiscoveryEvent()
    }

    private val _discoveryEvents = MutableStateFlow<DiscoveryEvent?>(null)
    val discoveryEvents = _discoveryEvents.asStateFlow()
    private val peers = mutableListOf<WifiP2pDevice>()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    handleWifiP2pStateChanged(intent)
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    handlePeersChanged()
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    handleConnectionChanged(intent)
                }
            }
        }
    }

    private fun handleWifiP2pStateChanged(intent: Intent) {
        val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
        if (state != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            _discoveryEvents.value = DiscoveryEvent.Error("WiFi Direct n'est pas activé")
        }
    }

    private fun handlePeersChanged() {
        try {
            if (!checkPermissions()) {
                _discoveryEvents.value = DiscoveryEvent.PermissionRequired
                return
            }

            wifiP2pManager.requestPeers(channel) { peerList ->
                peers.clear()
                peers.addAll(peerList.deviceList)
                _discoveryEvents.value = DiscoveryEvent.DevicesFound(peers.toList())
            }
        } catch (e: SecurityException) {
            _discoveryEvents.value = DiscoveryEvent.Error("Permissions manquantes: ${e.message}")
        }
    }

    private fun handleConnectionChanged(intent: Intent) {
        try {
            if (!checkPermissions()) {
                _discoveryEvents.value = DiscoveryEvent.PermissionRequired
                return
            }

            val networkInfo: NetworkInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
            if (networkInfo?.isConnected == true) {
                wifiP2pManager.requestConnectionInfo(channel) { info ->
                    info?.groupOwnerAddress?.let { address ->
                        startDataTransfer(address)
                    }
                }
            }
        } catch (e: SecurityException) {
            _discoveryEvents.value = DiscoveryEvent.Error("Erreur de connexion: ${e.message}")
        }
    }

    fun startDiscovery(isServer: Boolean) {
        if (!checkPermissions()) {
            _discoveryEvents.value = DiscoveryEvent.PermissionRequired
            return
        }

        try {
            registerReceiver()
            setDeviceName(if (isServer) "FilterServer" else "FilterClient")
        } catch (e: SecurityException) {
            _discoveryEvents.value = DiscoveryEvent.Error("Erreur au démarrage: ${e.message}")
        }
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        }
        context.registerReceiver(receiver, intentFilter)
    }

    private fun setDeviceName(deviceName: String) {
        try {
            if (!checkPermissions()) {
                _discoveryEvents.value = DiscoveryEvent.PermissionRequired
                return
            }
            fun WifiP2pManager.setDeviceNameSafe(
                channel: WifiP2pManager.Channel,
                deviceName: String,
                listener: WifiP2pManager.ActionListener
            ) {
                try {
                    val method = this.javaClass.getMethod(
                        "setDeviceName",
                        WifiP2pManager.Channel::class.java,
                        String::class.java,
                        WifiP2pManager.ActionListener::class.java
                    )
                    method.invoke(this, channel, deviceName, listener)
                } catch (e: Exception) {
                    listener.onFailure(WifiP2pManager.ERROR)
                }
            }

// Usage example:
            wifiP2pManager.setDeviceNameSafe(channel, deviceName, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    // Device name changed successfully
                }

                override fun onFailure(reason: Int) {
                    val errorMessage = when (reason) {
                        WifiP2pManager.P2P_UNSUPPORTED -> "WiFi Direct non supporté"
                        WifiP2pManager.ERROR -> "Erreur lors du changement de nom"
                        WifiP2pManager.BUSY -> "Système occupé"
                        else -> "Erreur inconnue"
                    }
                    // Handle error
                }
            })

        } catch (e: SecurityException) {
            _discoveryEvents.value = DiscoveryEvent.Error("Erreur de configuration: ${e.message}")
        }
    }

    private fun startPeerDiscovery() {
        try {
            if (!checkPermissions()) {
                _discoveryEvents.value = DiscoveryEvent.PermissionRequired
                return
            }

            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    _discoveryEvents.value = DiscoveryEvent.Started
                }
                override fun onFailure(reason: Int) {
                    handleActionFailure(reason, "Découverte")
                }
            })
        } catch (e: SecurityException) {
            _discoveryEvents.value = DiscoveryEvent.Error("Erreur de découverte: ${e.message}")
        }
    }

    fun connectToDevice(device: WifiP2pDevice) {
        try {
            if (!checkPermissions()) {
                _discoveryEvents.value = DiscoveryEvent.PermissionRequired
                return
            }

            val config = WifiP2pConfig().apply {
                deviceAddress = device.deviceAddress
            }

            wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    _discoveryEvents.value = DiscoveryEvent.Connected(device)
                }
                override fun onFailure(reason: Int) {
                    handleActionFailure(reason, "Connexion")
                }
            })
        } catch (e: SecurityException) {
            _discoveryEvents.value = DiscoveryEvent.Error("Erreur de connexion: ${e.message}")
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED
    }

    private fun handleActionFailure(reason: Int, action: String) {
        val errorMsg = when(reason) {
            WifiP2pManager.P2P_UNSUPPORTED -> "WiFi Direct non supporté"
            WifiP2pManager.ERROR -> "Erreur de $action"
            WifiP2pManager.BUSY -> "Système occupé"
            else -> "Erreur inconnue"
        }
        _discoveryEvents.value = DiscoveryEvent.Error(errorMsg)
    }

    private fun startDataTransfer(address: InetAddress) {
        // Implement your data transfer logic here
    }

    fun stop() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            // Receiver already unregistered
        }
    }
}
class WifiDirectService(private val context: Context) {
    private var wifiP2pManager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var serverSocket: ServerSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    init {
        initializeWifiP2p()
    }

    private fun initializeWifiP2p() {
        wifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
        channel = wifiP2pManager?.initialize(context, context.mainLooper, null)
    }

    @SuppressLint("DiscouragedPrivateApi")
    fun setDeviceName(deviceName: String, callback: (Boolean) -> Unit) {
        val manager = wifiP2pManager ?: run {
            callback(false)
            return
        }

        val currentChannel = channel ?: run {
            callback(false)
            return
        }

        try {
            // Using reflection to access setDeviceName method
            val method = manager.javaClass.getDeclaredMethod(
                "setDeviceName",
                WifiP2pManager.Channel::class.java,
                String::class.java,
                WifiP2pManager.ActionListener::class.java
            )

            method.isAccessible = true
            method.invoke(
                manager,
                currentChannel,
                deviceName,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        callback(true)
                    }

                    override fun onFailure(reason: Int) {
                        val errorMsg = when (reason) {
                            WifiP2pManager.P2P_UNSUPPORTED -> "WiFi Direct non supporté"
                            WifiP2pManager.ERROR -> "Erreur lors du changement de nom"
                            WifiP2pManager.BUSY -> "Système occupé"
                            else -> "Erreur inconnue"
                        }
                        Log.e(TAG, "Échec du changement de nom: $errorMsg")
                        callback(false)
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception lors du changement de nom: ${e.message}")
            callback(false)
        }
    }

    fun startServer(onStateChange: (Boolean) -> Unit) {
        scope.launch {
            try {
                serverSocket = ServerSocket(0).apply {
                    reuseAddress = true
                    soTimeout = 30000  // 30 seconds timeout
                }

                while (isActive) {
                    try {
                        val client = serverSocket?.accept()
                        client?.let {
                            handleClient(it, onStateChange)
                        }
                    } catch (e: SocketTimeoutException) {
                        continue
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erreur serveur: ${e.message}")
                onStateChange(false)
            }
        }
    }

    private fun handleClient(client: Socket, onStateChange: (Boolean) -> Unit) {
        scope.launch {
            try {
                val reader = BufferedReader(InputStreamReader(client.getInputStream()))
                val writer = PrintWriter(client.getOutputStream(), true)

                while (isActive && !client.isClosed) {
                    val message = reader.readLine() ?: break
                    when (message) {
                        "TEST_ACTIVE" -> {
                            onStateChange(true)
                            writer.println("ACK")
                        }
                        "TEST_INACTIVE" -> {
                            onStateChange(false)
                            writer.println("ACK")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erreur client: ${e.message}")
                onStateChange(false)
            } finally {
                try {
                    client.close()
                } catch (e: Exception) {
                    // Ignore close errors
                }
            }
        }
    }

    fun cleanup() {
        scope.cancel()
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Erreur cleanup: ${e.message}")
        }
        channel = null
        wifiP2pManager = null
    }

    companion object {
        private const val TAG = "WifiDirectService"
    }
}
