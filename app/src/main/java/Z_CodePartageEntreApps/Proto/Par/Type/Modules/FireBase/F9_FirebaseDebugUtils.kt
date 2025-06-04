package Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// Firebase Debug and Validation Utilities
object F9_FirebaseDebugUtils {
    fun logFirebaseOperation(
        operation: String,
        reference: DatabaseReference,
        itemCount: Int = 0,
        success: Boolean = false,
        error: Exception? = null
    ) {
        val logMessage = buildString {
            appendLine("=== Firebase Operation Log ===")
            appendLine("Operation: $operation")
            appendLine("Reference: ${reference.toString()}")
            appendLine("Item Count: $itemCount")
            appendLine("Success: $success")
            appendLine("Timestamp: ${System.currentTimeMillis()}")

            if (error != null) {
                appendLine("Error: ${error.message}")
                appendLine("Error Type: ${error::class.simpleName}")
            }
            appendLine("===============================")
        }

        println(logMessage)
    }

    suspend fun verifyFirebaseReference(reference: DatabaseReference): Boolean {
        return try {
            suspendCancellableCoroutine<Boolean> { continuation ->
                reference.child("_test_connection").setValue("test")
                    .addOnSuccessListener {
                        reference.child("_test_connection").removeValue()
                        continuation.resume(true)
                    }
                    .addOnFailureListener {
                        continuation.resume(false)
                    }
            }
        } catch (e: Exception) {
            false
        }
    }
}
