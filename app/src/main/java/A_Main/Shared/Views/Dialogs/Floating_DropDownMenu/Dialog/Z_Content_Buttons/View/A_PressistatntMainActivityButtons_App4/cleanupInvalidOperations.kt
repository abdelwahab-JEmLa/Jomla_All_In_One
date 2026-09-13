package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "CleanupInvalidOps"

fun cleanupInvalidOperations_Np(
    repo10OperationVentCouleur: Repo10OperationVentCouleur,
    on_vent_key: String,
    nonDeletableClientNames: Set<String> = emptySet(),
    nom_contains_a_evite_de_delete_leur_oeprations: String = "",
    onDone: () -> Unit = {},
) {
    repo10OperationVentCouleur.repoScope.launch {
        Log.d(TAG, "coroutine started — total ops in repo=${repo10OperationVentCouleur.datasValue.size}  on_vent_key='$on_vent_key'")
        try {

            val protectedTerms = nom_contains_a_evite_de_delete_leur_oeprations
                .split(",")
                .map { it.trim().lowercase() }
                .filter { it.isNotEmpty() }

            val nonDeletableNamesLower = nonDeletableClientNames.map { it.lowercase() }.toSet()

            val operationsToDelete = repo10OperationVentCouleur.datasValue.filter { operation ->
                if (operation.parent_M8BonVent_KeyId == on_vent_key) return@filter false
                if (operation.parentClientName.lowercase() in nonDeletableNamesLower) return@filter false
                val isSpecialClient = 
                        protectedTerms.any { term -> operation.parentClientName.lowercase().contains(term) }
                !isSpecialClient
            }

            val nullUpdates: Map<String, Any?> = operationsToDelete.associate { it.keyID to null }
            M10OperationVentCouleur.ref.updateChildren(nullUpdates).await()

            operationsToDelete.forEachIndexed { index, operation ->
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
