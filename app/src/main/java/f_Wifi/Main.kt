package f_Wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.util.Log
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.net.NetworkInterface
import java.net.ServerSocket
import java.time.Duration
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class NetworkManager(private val context: Context) {
    private val _serverUrl = MutableStateFlow<String?>(null)
    val serverUrl = _serverUrl.asStateFlow()

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun initialize() {
        // Observer les changements de réseau
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateServerUrl()
            }

            override fun onLost(network: Network) {
                _serverUrl.value = null
            }
        })

        // Initialisation initiale
        updateServerUrl()
    }

    private fun updateServerUrl() {
        try {
            val ipAddress = getWifiIpAddress()
            if (ipAddress != null) {
                val port = findAvailablePort()
                _serverUrl.value = "$ipAddress:$port"
                Log.d("NetworkManager", "Server URL updated: ${_serverUrl.value}")
            }
        } catch (e: Exception) {
            Log.e("NetworkManager", "Error updating server URL", e)
        }
    }

    private fun getWifiIpAddress(): String? {
        // Essayer d'abord l'adresse IP du WiFi
        val wifiInfo = wifiManager.connectionInfo
        if (wifiInfo != null) {
            val ipInt = wifiInfo.ipAddress
            if (ipInt != 0) {
                return String.format(
                    Locale.US,
                    "%d.%d.%d.%d",
                    ipInt and 0xff,
                    ipInt shr 8 and 0xff,
                    ipInt shr 16 and 0xff,
                    ipInt shr 24 and 0xff
                )
            }
        }

        // Backup: parcourir toutes les interfaces réseau
        return NetworkInterface.getNetworkInterfaces()?.toList()?.flatMap { networkInterface ->
            networkInterface.inetAddresses.toList()
                .filter { !it.isLoopbackAddress && it.hostAddress?.contains(":") == false }
                .map { it.hostAddress }
        }?.firstOrNull()
    }

    private fun findAvailablePort(startPort: Int = 8080): Int {
        for (port in startPort..65535) {
            try {
                ServerSocket(port).use { return port }
            } catch (e: Exception) {
                // Port is in use, try next one
            }
        }
        throw IllegalStateException("No available ports found")
    }

    fun getServerUrlOrDefault(): String {
        return serverUrl.value ?: "192.168.1.1:8080"
    }
}
class ScrollSyncManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var server: WebSocketServer? = null
    private var client: WebSocketClient? = null

    fun startServer(port: Int = 8080) {
        server = WebSocketServer(port)
        server?.start()
    }

    fun startClient(serverUrl: String) {
        client = WebSocketClient(serverUrl)
        client?.connect()
    }

    suspend fun stop() {
        server?.stop()
        client?.disconnect()
    }

    fun sendScrollPosition(position: Int) {
        server?.broadcastScrollPosition(position) ?: client?.sendScrollPosition(position)
    }

    fun observeScrollPosition(): Flow<Int> {
        return client?.scrollPositionFlow ?: server?.scrollPositionFlow ?: emptyFlow()
    }
}

class WebSocketServer(private val port: Int) {
    private val _scrollPositionFlow = MutableSharedFlow<Int>()
    val scrollPositionFlow: Flow<Int> = _scrollPositionFlow.asSharedFlow()

    private val connections = ConcurrentHashMap<String, WebSocketServerSession>()
    private var ktorServer: ApplicationEngine? = null

    fun start() {
        ktorServer = embeddedServer(Netty, port = port) {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(30)
            }
            routing {
                webSocket("/scroll-sync") {
                    val sessionId = generateSessionId()
                    try {
                        connections[sessionId] = this
                        Log.d("WebSocketServer", "Client connected: $sessionId")

                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val message = frame.readText()
                                    handleIncomingMessage(message, sessionId)
                                }
                                else -> { /* Ignore other frame types */ }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("WebSocketServer", "Error in connection $sessionId", e)
                    } finally {
                        connections.remove(sessionId)
                        Log.d("WebSocketServer", "Client disconnected: $sessionId")
                    }
                }
            }
        }.start(wait = false)
    }

    suspend fun stop() {
        connections.values.forEach { it.close() }
        connections.clear()
        ktorServer?.stop(1000, 2000)
    }

    fun broadcastScrollPosition(position: Int) {
        val message = createScrollMessage(position)
        connections.values.forEach { session ->
            scope.launch {
                try {
                    session.send(message)
                } catch (e: Exception) {
                    Log.e("WebSocketServer", "Error sending to client", e)
                }
            }
        }
    }

    private fun handleIncomingMessage(message: String, senderId: String) {
        when {
            message.startsWith("SCROLL:") -> {
                val position = message.substringAfter("SCROLL:").toIntOrNull() ?: return
                scope.launch {
                    _scrollPositionFlow.emit(position)
                    // Broadcast to all other clients except sender
                    connections.forEach { (id, session) ->
                        if (id != senderId) {
                            session.send(message)
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private val scope = CoroutineScope(Dispatchers.IO)
        private var sessionCounter = 0
        private fun generateSessionId() = "client-${++sessionCounter}"
        private fun createScrollMessage(position: Int) = "SCROLL:$position"
    }
}

class WebSocketClient(private val serverUrl: String) {
    private val _scrollPositionFlow = MutableSharedFlow<Int>()
    val scrollPositionFlow: Flow<Int> = _scrollPositionFlow.asSharedFlow()

    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(15, TimeUnit.SECONDS)
        .build()

    private val reconnectChannel = Channel<Unit>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        setupReconnection()
    }

    fun connect() {
        val request = Request.Builder()
            .url("ws://$serverUrl/scroll-sync")
            .build()

        webSocket = client.newWebSocket(request, createListener())
    }

    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
    }

    fun sendScrollPosition(position: Int) {
        webSocket?.send("SCROLL:$position")
    }

    private fun setupReconnection() {
        scope.launch {
            for (unit in reconnectChannel) {
                Log.d("WebSocketClient", "Attempting to reconnect...")
                kotlinx.coroutines.delay(5000)
                connect()
            }
        }
    }

    private fun createListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocketClient", "Connected to server")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            when {
                text.startsWith("SCROLL:") -> {
                    val position = text.substringAfter("SCROLL:").toIntOrNull() ?: return
                    scope.launch {
                        _scrollPositionFlow.emit(position)
                    }
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocketClient", "WebSocket failure", t)
            scope.launch {
                reconnectChannel.send(Unit)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocketClient", "WebSocket closed: $reason")
        }
    }
}
