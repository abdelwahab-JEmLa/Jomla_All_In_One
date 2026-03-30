package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository

import EntreApps.Shared.Models.Jomla_Clients
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun cleanupInvalidOperations(repo10OperationVentCouleur: Repo10OperationVentCouleur) {
    repo10OperationVentCouleur.repoScope.launch {
        try {
            // Get current BonVent key from AppCompt
            val currentBonVentKey = repo10OperationVentCouleur.zAppComptRepositoryComposable
                .currentAppCompt?.onVentM8BonVentKey

            // Filter operations to delete: those that are NOT Jomla clients
            val operationsToDelete = repo10OperationVentCouleur.datasValue.filter { operation ->
                // Check if this is a Jomla client (protected from deletion)
                val isJomlaClient = operation.parentClientName == "abdelwahab" ||
                        operation.parent_M2Client_KeyID == Jomla_Clients.ECHATILLANTS_KEY_ID ||
                        operation.parent_M2Client_KeyID == Jomla_Clients.Au_Command_KEY_ID

                // Keep Jomla clients, delete all others
                !isJomlaClient
            }

            // Delete non-Jomla operations
            operationsToDelete.forEach { operation ->
                repo10OperationVentCouleur.delete(operation)
            }

            // Show success message if any operations were deleted
            if (operationsToDelete.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        repo10OperationVentCouleur.context,
                        "Cleaned up ${operationsToDelete.size} invalid operations (kept Jomla clients)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    repo10OperationVentCouleur.context,
                    "Failed to cleanup operations: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
