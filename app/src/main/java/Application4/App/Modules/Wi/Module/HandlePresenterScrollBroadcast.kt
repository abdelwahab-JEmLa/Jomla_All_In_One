package Application4.App.Modules.Wi.Module

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val TAG = "FragID4GridScroll"

@Composable
fun HandlePresenterScrollBroadcast(
    isHostPhone: Boolean,
    isConnected: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: A_ViewModel_NewProtoPatterns,
    onScrollHostChange: (Int) -> Unit = {}
) {                         //<--

    //<--            ---------------------------- PROCESS STARTED (16328) for package com.example.clientjetpack ----------------------------
    //15:00:09.625 EtagerLazy        ━━━ wifiState changed ━━━ isHost=true | isConnected=false | scrollPos=0
    //15:00:09.626                   ⚠️ Not connected or role undefined — scroll broadcast is INACTIVE
    //15:00:09.626                   📋 displayList ready — 1505 produits | isHost=true | isConnected=false
    //15:00:09.638                   🔢 gridState — firstItem=0 | scrolling=false | totalItems=1505 | isHost=true | isConnected=false
    //15:00:09.639 FragID4GridScroll HandlePresenterScrollBroadcast: inactive — isHost=true, isConnected=false
    //15:01:46.947 WifiTransferDatas sendData: sending to endpoint=G4QI, payload="Connection established"
    //15:01:47.140                   sendData: SUCCESS for payload="Connection established"
    //15:01:47.184                   payloadCallback.onPayloadReceived: raw="Connection established"
    //15:01:47.185                   handlePayload: no matching order for payload="Connection established"
    //15:01:49.436 EtagerLazy        🔢 gridState — firstItem=0 | scrolling=true | totalItems=1505 | isHost=true | isConnected=false
    //15:01:50.501                   🔢 gridState — firstItem=2 | scrolling=true | totalItems=1505 | isHost=true | isConnected=false
    //15:01:50.516                   🔢 gridState — firstItem=2 | scrolling=false | totalItems=1505 | isHost=true | isConnected=false
    //15:01:50.729                   🔢 gridState — firstItem=2 | scrolling=true | totalItems=1505 | isHost=true | isConnected=false
    //15:01:51.620                   🔢 gridState — firstItem=2 | scrolling=false | totalItems=1505 | isHost=true | isConnected=false
    //15:01:52.011 WifiTransferDatas sendData: sending to endpoint=G4QI, payload="ping"
    //15:01:52.023                   sendData: SUCCESS for payload="ping"
    //15:01:52.067                   payloadCallback.onPayloadReceived: raw="ping"
    //15:01:52.067                   handlePayload: no matching order for payload="ping"
    //15:01:57.012                   sendData: sending to endpoint=G4QI, payload="ping"
    //15:01:57.015                   sendData: SUCCESS for payload="ping"
    //15:02:02.012                   sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:02.019                   sendData: SUCCESS for payload="ping"
    //15:02:06.496 EtagerLazy        🔢 gridState — firstItem=2 | scrolling=true | totalItems=1505 | isHost=true | isConnected=false
    //15:02:06.529                   🔢 gridState — firstItem=3 | scrolling=true | totalItems=1505 | isHost=true | isConnected=false
    //15:02:06.568                   🔢 gridState — firstItem=4 | scrolling=true | totalItems=1505 | isHost=true | isConnected=false
    //15:02:07.191                   🔢 gridState — firstItem=6 | scrolling=true | totalItems=1505 | isHost=true | isConnected=false
    //15:02:07.223                   🔢 gridState — firstItem=6 | scrolling=false | totalItems=1505 | isHost=true | isConnected=false
    //15:02:07.226                   🔢 gridState — firstItem=6 | scrolling=true | totalItems=1505 | isHost=true | isConnected=false
    //15:02:07.253 WifiTransferDatas sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:07.290                   sendData: SUCCESS for payload="ping"
    //15:02:08.103 EtagerLazy        🔢 gridState — firstItem=6 | scrolling=false | totalItems=1505 | isHost=true | isConnected=false
    //15:02:12.254 WifiTransferDatas sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:12.257                   sendData: SUCCESS for payload="ping"
    //15:02:17.254                   sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:17.259                   sendData: SUCCESS for payload="ping"
    //15:02:22.256                   sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:22.268                   sendData: SUCCESS for payload="ping"
    //15:02:27.259                   sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:27.278                   sendData: SUCCESS for payload="ping"
    //15:02:32.264                   sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:32.276                   sendData: SUCCESS for payload="ping"
    //15:02:37.270                   sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:37.283                   sendData: SUCCESS for payload="ping"

    // Note: MEDIUM_ERROR [NETWORK][WIFI_LAN][RECEIVE_PAYLOAD][RECEIVE_PAYLOAD_FAILED][NULL_MESSAGE], service-id=com.example.clientjetpack
    // This error occurs when the VirtualOutputStream closes during payload transfer (see stack trace below)
    //15:02:27.259 WifiTransferDatas sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:27.278                   sendData: SUCCESS for payload="ping"
    //15:02:27.287 NearbyConnections EndpointManager failed to write DATA at offset -1 of Payload -8795766111645277089 to endpoint G4QI on WIFI_LAN [CONTEXT service_id=54 ]
    //java.io.IOException: Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //	at dget.write(:com.google.android.gms@261133022@26.11.33 (150400-887465546):342)
    //	at java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:82)
    //	at java.io.BufferedOutputStream.flush(BufferedOutputStream.java:140)
    //	at java.io.DataOutputStream.flush(DataOutputStream.java:123)
    //	at dbfx.A(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at dbfx.z(:com.google.android.gms@261133022@26.11.33 (150400-887465546):46)
    //	at dbhl.d(:com.google.android.gms@261133022@26.11.33 (150400-887465546):64)
    //	at dbhl.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):12)
    //	at dbld.a(:com.google.android.gms@261133022@26.11.33 (150400-887465546):796)
    //	at dbkp.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):801)
    //	at bfem.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):50)
    //	at bfem.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
    //	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
    //	at bfkd.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):8)
    //	at java.lang.Thread.run(Thread.java:923)
    //15:02:27.287 NearbyMediums     MEDIUM_ERROR [NETWORK][WIFI_LAN][SEND_PAYLOAD][SEND_PAYLOAD_FAILED][UNKNOWN], service-id=com.example.clientjetpack
    //15:02:27.287                   Extra Sensitive/PII message : Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //15:02:27.295 NearbyConnections EndpointManager failed to write DATA at offset -1 of Payload -8795766111645277089 to endpoint G4QI on WIFI_LAN [CONTEXT service_id=54 ]
    //java.io.IOException: Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //	at dget.write(:com.google.android.gms@261133022@26.11.33 (150400-887465546):342)
    //	at java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:82)
    //	at java.io.BufferedOutputStream.flush(BufferedOutputStream.java:140)
    //	at java.io.DataOutputStream.flush(DataOutputStream.java:123)
    //	at dbfx.A(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at dbfx.z(:com.google.android.gms@261133022@26.11.33 (150400-887465546):46)
    //	at dbhl.d(:com.google.android.gms@261133022@26.11.33 (150400-887465546):64)
    //	at dbhl.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):12)
    //	at dbld.a(:com.google.android.gms@261133022@26.11.33 (150400-887465546):796)
    //	at dbkp.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):801)
    //	at bfem.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):50)
    //	at bfem.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
    //	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
    //	at bfkd.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):8)
    //	at java.lang.Thread.run(Thread.java:923)
    //15:02:27.297 NearbyMediums     MEDIUM_ERROR [NETWORK][WIFI_LAN][SEND_PAYLOAD][SEND_PAYLOAD_FAILED][UNKNOWN], service-id=com.example.clientjetpack
    //15:02:27.297                   Extra Sensitive/PII message : Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //15:02:32.264 WifiTransferDatas sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:32.276                   sendData: SUCCESS for payload="ping"
    //15:02:32.282 NearbyConnections EndpointManager failed to write DATA at offset -1 of Payload -6230288947735332021 to endpoint G4QI on WIFI_LAN [CONTEXT service_id=54 ]
    //java.io.IOException: Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //	at dget.write(:com.google.android.gms@261133022@26.11.33 (150400-887465546):342)
    //	at java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:82)
    //	at java.io.BufferedOutputStream.flush(BufferedOutputStream.java:140)
    //	at java.io.DataOutputStream.flush(DataOutputStream.java:123)
    //	at dbfx.A(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at dbfx.z(:com.google.android.gms@261133022@26.11.33 (150400-887465546):46)
    //	at dbhl.d(:com.google.android.gms@261133022@26.11.33 (150400-887465546):64)
    //	at dbhl.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):12)
    //	at dbld.a(:com.google.android.gms@261133022@26.11.33 (150400-887465546):796)
    //	at dbkp.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):801)
    //	at bfem.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):50)
    //	at bfem.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
    //	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
    //	at bfkd.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):8)
    //	at java.lang.Thread.run(Thread.java:923)
    //15:02:32.282 NearbyMediums     MEDIUM_ERROR [NETWORK][WIFI_LAN][SEND_PAYLOAD][SEND_PAYLOAD_FAILED][UNKNOWN], service-id=com.example.clientjetpack
    //15:02:32.282                   Extra Sensitive/PII message : Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //15:02:32.299 NearbyConnections EndpointManager failed to write DATA at offset -1 of Payload -6230288947735332021 to endpoint G4QI on WIFI_LAN [CONTEXT service_id=54 ]
    //java.io.IOException: Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //	at dget.write(:com.google.android.gms@261133022@26.11.33 (150400-887465546):342)
    //	at java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:82)
    //	at java.io.BufferedOutputStream.flush(BufferedOutputStream.java:140)
    //	at java.io.DataOutputStream.flush(DataOutputStream.java:123)
    //	at dbfx.A(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at dbfx.z(:com.google.android.gms@261133022@26.11.33 (150400-887465546):46)
    //	at dbhl.d(:com.google.android.gms@261133022@26.11.33 (150400-887465546):64)
    //	at dbhl.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):12)
    //	at dbld.a(:com.google.android.gms@261133022@26.11.33 (150400-887465546):796)
    //	at dbkp.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):801)
    //	at bfem.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):50)
    //	at bfem.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
    //	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
    //	at bfkd.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):8)
    //	at java.lang.Thread.run(Thread.java:923)
    //15:02:32.300 NearbyMediums     MEDIUM_ERROR [NETWORK][WIFI_LAN][SEND_PAYLOAD][SEND_PAYLOAD_FAILED][UNKNOWN], service-id=com.example.clientjetpack
    //15:02:32.300                   Extra Sensitive/PII message : Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //15:02:37.270 WifiTransferDatas sendData: sending to endpoint=G4QI, payload="ping"
    //15:02:37.283                   sendData: SUCCESS for payload="ping"
    //15:02:37.293 NearbyConnections EndpointManager failed to write DATA at offset -1 of Payload -6255901537207174227 to endpoint G4QI on WIFI_LAN [CONTEXT service_id=54 ]
    //java.io.IOException: Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //	at dget.write(:com.google.android.gms@261133022@26.11.33 (150400-887465546):342)
    //	at java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:82)
    //	at java.io.BufferedOutputStream.flush(BufferedOutputStream.java:140)
    //	at java.io.DataOutputStream.flush(DataOutputStream.java:123)
    //	at dbfx.A(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at dbfx.z(:com.google.android.gms@261133022@26.11.33 (150400-887465546):46)
    //	at dbhl.d(:com.google.android.gms@261133022@26.11.33 (150400-887465546):64)
    //	at dbhl.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):12)
    //	at dbld.a(:com.google.android.gms@261133022@26.11.33 (150400-887465546):796)
    //	at dbkp.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):801)
    //	at bfem.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):50)
    //	at bfem.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
    //	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
    //	at bfkd.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):8)
    //	at java.lang.Thread.run(Thread.java:923)
    //15:02:37.295 NearbyMediums     MEDIUM_ERROR [NETWORK][WIFI_LAN][SEND_PAYLOAD][SEND_PAYLOAD_FAILED][UNKNOWN], service-id=com.example.clientjetpack
    //15:02:37.295                   Extra Sensitive/PII message : Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //15:02:37.300 NearbyConnections EndpointManager failed to write DATA at offset -1 of Payload -6255901537207174227 to endpoint G4QI on WIFI_LAN [CONTEXT service_id=54 ]
    //java.io.IOException: Failed to write data because the VirtualOutputStream for com.example.clientjetpack_UPGRADE closed
    //	at dget.write(:com.google.android.gms@261133022@26.11.33 (150400-887465546):342)
    //	at java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:82)
    //	at java.io.BufferedOutputStream.flush(BufferedOutputStream.java:140)
    //	at java.io.DataOutputStream.flush(DataOutputStream.java:123)
    //	at dbfx.A(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at dbfx.z(:com.google.android.gms@261133022@26.11.33 (150400-887465546):46)
    //	at dbhl.d(:com.google.android.gms@261133022@26.11.33 (150400-887465546):64)
    //	at dbhl.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):12)
    //	at dbld.a(:com.google.android.gms@261133022@26.11.33 (150400-887465546):796)
    //	at dbkp.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):801)
    //	at bfem.c(:com.google.android.gms@261133022@26.11.33 (150400-887465546):50)
    //	at bfem.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):73)
    //	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
    //	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
    //	at bfkd.run(:com.google.android.gms@261133022@26.11.33 (150400-887465546):8)
    //	at java.lang.Thread.run(Thread.java:923)
    //15:02:37.301 NearbyMediums     MEDIUM_ERROR [NETWORK][WIFI_LAN][SEND_PAYLOAD][SEND_PAYLOAD_FAILED][UNKNOWN], service-id=com.example.clientjetpack
    //15:02:37.301                   Extra Sensitive/PII message : Failed to write data because the VirtualOu
    // Track the last scroll position sent to avoid redundant network calls
    var lastSentPosition by remember { mutableIntStateOf(-1) }

    LaunchedEffect(isHostPhone, isConnected) {
        if (!isHostPhone || !isConnected) {
            Log.d(TAG, "HandlePresenterScrollBroadcast: inactive — isHost=$isHostPhone, isConnected=$isConnected")
            return@LaunchedEffect
        }

        Log.d(TAG, "HandlePresenterScrollBroadcast: starting scroll observation")

        snapshotFlow {
            Triple(
                gridState.firstVisibleItemIndex,
                gridState.firstVisibleItemScrollOffset,
                gridState.isScrollInProgress   // replaces the manual isDragging heuristic
            )
        }
            .distinctUntilChanged()
            .collect { (position, offset, isScrolling) ->
                Log.d(
                    TAG, "Scroll snapshot — position=$position, offset=$offset, " +
                            "isScrollInProgress=$isScrolling, lastSent=$lastSentPosition"
                )

                if (position != lastSentPosition) {
                    lastSentPosition = position
                    onScrollHostChange(position)
                    Log.d(TAG, "Sending scroll position=$position to client (offset=$offset, scrolling=$isScrolling)")
                    viewModel.sendOrderToClientDisplayerT(
                        WifiUpdateClientDisplayerStats_NewProto.ClientMainGridScrollPosition,
                        position
                    )
                } else {
                    Log.v(TAG, "Position unchanged ($position), skipping send")
                }
            }
    }
}

/**
 * Handle scroll receiving on CLIENT from HOST for FragID4 Presenter Grid.
 *
 * Tracks the latest requested position so rapid updates from the host never
 * cause the client to fall behind: even if a scroll is in progress, the next
 * emission will always catch up to the most recent position.
 */
@Composable
fun HandlePresenterClientScroll(
    isHostPhone: Boolean,
    scrollPosition: Int,
    gridState: LazyStaggeredGridState,
    tag: String = TAG
) {
    val scope = rememberCoroutineScope()
    var lastAppliedPosition by remember { mutableIntStateOf(-1) }
    // Always holds the freshest position requested by the host.
    var pendingPosition by remember { mutableIntStateOf(-1) }
    // True while a coroutine is driving the grid; it will drain pendingPosition before exiting.
    var isScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(scrollPosition) {
        if (isHostPhone) return@LaunchedEffect
        if (scrollPosition == lastAppliedPosition) {
            Log.v(tag, "Client: position $scrollPosition already applied, skipping")
            return@LaunchedEffect
        }

        Log.d(tag, "Client: received position=$scrollPosition (last applied=$lastAppliedPosition, scrolling=$isScrolling)")

        // Always update the target — the running coroutine reads this before exiting.
        pendingPosition = scrollPosition

        if (isScrolling) {
            Log.d(tag, "Client: scroll already in progress, updated pendingPosition=$pendingPosition")
            return@LaunchedEffect
        }

        isScrolling = true
        scope.launch {
            try {
                // Loop until we've caught up with every position that arrived
                // while the coroutine was running (prevents falling behind on fast scrolls).
                while (pendingPosition != lastAppliedPosition) {
                    val target = pendingPosition
                    Log.d(tag, "Client: scrolling to target=$target")
                    gridState.scrollToItem(target, scrollOffset = 0)
                    lastAppliedPosition = target
                    Log.d(tag, "Client: reached target=$target")
                }
            } catch (e: Exception) {
                Log.e(tag, "Client: error scrolling to $pendingPosition — ${e.message}", e)
                try {
                    gridState.scrollToItem(pendingPosition)
                    lastAppliedPosition = pendingPosition
                } catch (e2: Exception) {
                    Log.e(tag, "Client: fallback scroll also failed — ${e2.message}", e2)
                }
            } finally {
                isScrolling = false
            }
        }
    }
}
