package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.M2Client
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// ─── Constants ───────────────────────────────────────────────────────────────

private const val DEFAULT_LAT       = 36.720027701275505
private const val DEFAULT_LNG       = 3.1436710147865483
private const val THRESHOLD_METERS  = 200.0

// ─── Shared predicate (also used by Fab_CleanupM8AndM10 for the badge count) ─

/** Returns true when a client should be moved: phone blank AND on the default pin. */
fun invalidM2ClientPredicate(numTelephone: String, latitude: Double, longitude: Double): Boolean {
    if (numTelephone.isNotBlank()) return false
    return haversineMeters(latitude, longitude, DEFAULT_LAT, DEFAULT_LNG) <= THRESHOLD_METERS
}

// ─── Internal geometry helper ─────────────────────────────────────────────────

private fun haversineMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val r    = 6_371_000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a    = sin(dLat / 2).pow(2) +
               cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}
fun moveM2InvalidClients(
    repositorysMainGetter: RepositorysMainGetter,
    onProgress: (Float) -> Unit = {},
) {
    val toMove = repositorysMainGetter.repo2Client.datasValue.filter { client ->
        invalidM2ClientPredicate(client.numTelephone, client.latitude, client.longitude)
    }
    if (toMove.isEmpty()) { onProgress(1f); return }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Batch write to non-active node
            val nonActiveUpdates: Map<String, Any> =
                toMove.associate { it.keyID to it.toFirebaseMap() }
            M2Client.ref_Non_Active_Datas.updateChildren(nonActiveUpdates).await()
            withContext(Dispatchers.Main) { onProgress(0.5f) }

            // Batch delete from active node (null = delete in multi-path update)
            val nullUpdates: Map<String, Any?> = toMove.associate { it.keyID to null }
            M2Client.ref.updateChildren(nullUpdates).await()
        } catch (_: Exception) { }

        withContext(Dispatchers.Main) { onProgress(1f) }
    }
}
