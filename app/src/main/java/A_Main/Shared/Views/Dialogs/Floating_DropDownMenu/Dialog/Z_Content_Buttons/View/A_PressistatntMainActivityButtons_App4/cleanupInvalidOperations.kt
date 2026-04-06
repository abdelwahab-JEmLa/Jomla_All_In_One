package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.AbdelwahabJomla_Client_Speciale
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "CleanupInvalidOps"

fun cleanupInvalidOperations_Np(
    repo10OperationVentCouleur: Repo10OperationVentCouleur,
    on_vent_key: String,
    onDone: () -> Unit = {},           // ← NEW: called on Main when the coroutine finishes
) {
    repo10OperationVentCouleur.repoScope.launch {
        Log.d(TAG, "coroutine started — total ops in repo=${repo10OperationVentCouleur.datasValue.size}  on_vent_key='$on_vent_key'")
        try {
            val specialClientKeyIDs = AbdelwahabJomla_Client_Speciale.entries
                .map { it.keyID }
                .filter { it.isNotEmpty() }
                .toSet()
            Log.d(TAG, "specialClientKeyIDs count=${specialClientKeyIDs.size}: $specialClientKeyIDs")

            val operationsToDelete = repo10OperationVentCouleur.datasValue.filter { operation ->
                if (operation.parent_M8BonVent_KeyId == on_vent_key) return@filter false
                val isSpecialClient = operation.parent_M2Client_KeyID in specialClientKeyIDs ||
                        operation.parentClientName == "abdelwahab"
                !isSpecialClient
            }
            Log.d(TAG, "operationsToDelete count=${operationsToDelete.size}")

            operationsToDelete.forEachIndexed { index, operation ->
                Log.d(TAG, "  deleting [${index + 1}/${operationsToDelete.size}] keyID=${operation.keyID}")
                repo10OperationVentCouleur.delete(operation)
            }

            withContext(Dispatchers.Main) {
                if (operationsToDelete.isNotEmpty()) {
                    Toast.makeText(
                        repo10OperationVentCouleur.context,
                        "Cleaned up ${operationsToDelete.size} invalid operations (kept special clients)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d(TAG, "success — calling onDone()")
                onDone()
            }
        } catch (e: Exception) {
            Log.e(TAG, "EXCEPTION: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    repo10OperationVentCouleur.context,
                    "Failed to cleanup operations: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "error path — calling onDone() anyway")
                onDone()
            }
        }
    }
}
