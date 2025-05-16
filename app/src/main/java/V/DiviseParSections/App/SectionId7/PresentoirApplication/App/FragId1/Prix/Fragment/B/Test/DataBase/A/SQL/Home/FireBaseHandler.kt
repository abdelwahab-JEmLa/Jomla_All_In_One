package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FireBaseHandler {
    val TAG = "FireBaseHandler"
    val ref: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InfosSqlDataBases")

    // Added getter for reference path to help with debugging
    fun getRefPath(): String {
        return ref.toString()
    }

    fun addToFirebaseAsync(
        dataBasesInfosSql: DataBasesInfosSql,
        onSuccess: () -> Unit={}
    ) {
        Log.d(TAG, "Starting Firebase data upload to path: ${getRefPath()}")

        val firebaseData = mapToFirebaseFormat(dataBasesInfosSql)
        Log.d(TAG, "Mapped data to Firebase format: ${summarizeDataSize(firebaseData)}")

        // Using batch updates instead of single setValue operation
        val updates = mutableMapOf<String, Any>()

        // Add each collection as a separate batch update
        if (firebaseData.containsKey("produits")) {
            val products = firebaseData["produits"] as Map<*, *>
            updates["produits"] = firebaseData["produits"] as Any
            Log.d(TAG, "Adding ${products.size} products to updates")
        } else {
            Log.w(TAG, "No products data to upload")
        }

        if (firebaseData.containsKey("clients")) {
            val clients = firebaseData["clients"] as Map<*, *>
            updates["clients"] = firebaseData["clients"] as Any
            Log.d(TAG, "Adding ${clients.size} clients to updates")
        } else {
            Log.w(TAG, "No clients data to upload")
        }

        if (firebaseData.containsKey("typeTarifications")) {
            val types = firebaseData["typeTarifications"] as Map<*, *>
            updates["typeTarifications"] = firebaseData["typeTarifications"] as Any
            Log.d(TAG, "Adding ${types.size} type tarifications to updates")
        } else {
            Log.w(TAG, "No type tarifications data to upload")
        }

        if (firebaseData.containsKey("tarifications")) {
            val tarifications = firebaseData["tarifications"] as Map<*, *>
            updates["tarifications"] = firebaseData["tarifications"] as Any
            Log.d(TAG, "Adding ${tarifications.size} tarifications to updates")
        } else {
            Log.w(TAG, "No tarifications data to upload")
        }

        Log.d(TAG, "Performing Firebase updateChildren with ${updates.size} collections")

        // Check if updates is empty
        if (updates.isEmpty()) {
            Log.e(TAG, "No data to update in Firebase! Check data mapping.")
            onSuccess() // Still call onSuccess to not block the flow
            return
        }

        // Execute batch update and invoke onSuccess when complete
        ref.updateChildren(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Firebase update SUCCESSFUL")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Firebase update FAILED: ${exception.message}", exception)
                // Check if reference path is valid
                Log.e(TAG, "Firebase reference path being used: ${getRefPath()}")
                // Also log if the database is connected
                ref.root.child(".info/connected").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val connected = snapshot.getValue(Boolean::class.java) ?: false
                        Log.d(TAG, "Firebase connection status: ${if (connected) "CONNECTED" else "DISCONNECTED"}")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Failed to check connection status: ${error.message}")
                    }
                })
                onSuccess()
            }
    }

    // Helper function to summarize data size for logging
    private fun summarizeDataSize(data: Map<String, Any>): String {
        val summary = StringBuilder()
        data.forEach { (key, value) ->
            if (value is Map<*, *>) {
                summary.append("$key: ${value.size} items, ")
            } else {
                summary.append("$key: 1 item, ")
            }
        }
        return summary.toString().trimEnd(',', ' ')
    }





    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        continuation.invokeOnCancellation {
            if (isComplete.not()) {
                cancel()
            }
        }
    }
}
