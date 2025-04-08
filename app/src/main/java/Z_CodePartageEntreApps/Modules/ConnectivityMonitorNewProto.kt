package Z_CodePartageEntreApps.Modules

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.net.InetSocketAddress
import java.net.Socket

class ConnectivityMonitorNewProto(private val scope: CoroutineScope) {
    private val CHECK_INTERVAL = 3000L
    private val CHECK_TIMEOUT = 10000L
    private var isOnline = false
    private var lastCheckTime = 0L
    private var connectivityCheckJob: Job? = null
    private var lastNotifiedState: Boolean? = null
    private var onConnectivityChanged: ((Boolean) -> Unit)? = null
    private var connectedRef: DatabaseReference? = null
    private var connectionListener: ValueEventListener? = null

    fun checkConnectivityAndSync(
        databaseReference: DatabaseReference,
        onOnline: () -> Unit = {},
        onOffline: () -> Unit = {}
    ) {
        // Use Firebase's built-in connected reference for more reliable tracking
        connectedRef = Firebase.database.reference.child(".info/connected")

        connectionListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.value as? Boolean ?: false

                if (connected) {
                    databaseReference.keepSynced(true)
                    Firebase.database.goOnline()
                } else {
                    databaseReference.keepSynced(false)
                    Firebase.database.goOffline()
                    onOffline()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle potential errors
                onOffline()
            }
        }

        connectedRef?.addValueEventListener(connectionListener!!)
    }

    suspend fun checkConnectivity(): Boolean {
        val currentTime = System.currentTimeMillis()

        // Early return with cached state if within checkADD_1_4_PeriodeVent interval
        if (currentTime - lastCheckTime < CHECK_INTERVAL && lastNotifiedState != null) {
            return isOnline
        }

        return try {
            val result = withTimeoutOrNull(CHECK_TIMEOUT) {
                try {
                    withContext(Dispatchers.IO) {
                        val socket = Socket()
                        val socketAddress = InetSocketAddress("8.8.8.8", 53)

                        try {
                            socket.connect(socketAddress, 3000)
                            socket.close()
                            true
                        } catch (e: Exception) {
                            false
                        }
                    }
                } catch (e: Exception) {
                    false
                }
            } ?: false

            isOnline = result
            lastCheckTime = currentTime

            // Notify about connectivity change if state has changed
            if (lastNotifiedState != result) {
                lastNotifiedState = result
                onConnectivityChanged?.invoke(result)
            }

            result

        } catch (e: Exception) {
            false
        }
    }


}
