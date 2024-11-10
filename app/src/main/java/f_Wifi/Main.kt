package f_Wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
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

data class UiState(
    val scrollPosition: Int = 0,
    val isConnected: Boolean = false,
    val connectionStatus: String = "Déconnecté",
    val isHost: Boolean = true
)

class P2PManager(
    private val context: Context,
    private val permissionHandler: PermissionHandler,
    private val onStatusUpdate: (P2PStatus) -> Unit
) {
    private val wifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel = wifiP2pManager.initialize(context, context.mainLooper, null)
    private var isHost = true
    private var connection: P2PConnection? = null

    init {
        if (permissionHandler.areNearbyPermissionsGranted()) {
            registerP2PReceiver()
            startDiscovery()
        }
    }

    fun startDiscovery() {
        if (!permissionHandler.areNearbyPermissionsGranted()) {
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
                    updateStatus(P2PStatus(
                        message = if (isHost) "En attente de connexion..." else "Recherche d'un hôte...",
                        isConnected = false,
                        isHost = isHost
                    ))
                }
                override fun onFailure(reason: Int) {
                    handleDiscoveryFailure(reason)
                }
            })
        } catch (e: SecurityException) {
            updateStatus(P2PStatus(
                message = "Erreur de permission: ${e.localizedMessage}",
                isConnected = false,
                isHost = isHost
            ))
        }
    }

    fun toggleRole() {
        if (!permissionHandler.areNearbyPermissionsGranted()) {
            updateStatus(P2PStatus(
                message = "Permissions requises",
                isConnected = false,
                isHost = isHost
            ))
            return
        }

        isHost = !isHost
        disconnect()
        startDiscovery()
    }

    fun sendScrollPosition(position: Int) {
        if (!permissionHandler.areNearbyPermissionsGranted()) return
        connection?.sendPosition(position)
    }

    fun disconnect() {
        connection?.close()
        connection = null
        try {
            if (permissionHandler.areNearbyPermissionsGranted()) {
                wifiP2pManager.removeGroup(channel, null)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception during disconnect: ${e.localizedMessage}")
        }

        updateStatus(P2PStatus(
            message = "Déconnecté",
            isConnected = false,
            isHost = isHost
        ))
    }

    private fun registerP2PReceiver() {
        if (!permissionHandler.areNearbyPermissionsGranted()) return

        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        }

        try {
            context.registerReceiver(
                createP2PReceiver(),
                intentFilter
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception during receiver registration: ${e.localizedMessage}")
        }
    }

    private fun createP2PReceiver() = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    handleConnectionChanged(intent)
                }
            }
        }
    }

    private fun handleConnectionChanged(intent: Intent) {
        try {
            val networkInfo: NetworkInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
            if (networkInfo?.isConnected == true) {
                wifiP2pManager.requestConnectionInfo(channel) { info ->
                    val isGroupOwner = info.isGroupOwner
                    if (isGroupOwner == isHost) {
                        connection = P2PConnection(isHost, info.groupOwnerAddress) { status ->
                            updateStatus(status)
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception during connection handling: ${e.localizedMessage}")
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

    companion object {
        private const val TAG = "P2PManager"
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
        scope.launch {
            setupConnection()
        }
    }

    private suspend fun setupConnection() = withContext(Dispatchers.IO) {
        try {
            if (isHost) {
                serverSocket = ServerSocket(PORT)
                onStatusUpdate(P2PStatus(
                    message = "En attente de connexion...",
                    isConnected = false,
                    isHost = isHost
                ))
                socket = serverSocket?.accept()
            } else {
                socket = Socket(groupOwnerAddress, PORT)
            }

            onStatusUpdate(P2PStatus(
                message = "Connecté",
                isConnected = true,
                isHost = isHost
            ))

        } catch (e: Exception) {
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
                socket?.let {
                    ObjectOutputStream(it.getOutputStream()).writeInt(position)
                }
            } catch (e: Exception) {
                close()
            }
        }
    }

    fun close() {
        socket?.close()
        serverSocket?.close()
        scope.cancel()
    }
}
