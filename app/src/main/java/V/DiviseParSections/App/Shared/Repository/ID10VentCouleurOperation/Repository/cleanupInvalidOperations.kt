package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository

import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Jomla_Clients
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun cleanupInvalidOperations(repo10OperationVentCouleur: Repo10OperationVentCouleur) {
    repo10OperationVentCouleur.repoScope.launch {
        try {
            val operationsToDelete = repo10OperationVentCouleur.datasValue.filter { operation ->
                // Check if parent BonVent exists
                val parentBonVent = repo10OperationVentCouleur.zAppComptRepositoryComposable.currentAppCompt
                    ?.onVentM8BonVentKey?.let { bonVentKey ->
                        repo10OperationVentCouleur.dataBaseCreationFactory.dao.getAllFlow()
                            .first()
                            .find { it.parent_M8BonVent_KeyId == bonVentKey }
                    }

                // Check if this is a Jomla client (should NOT be deleted)
                val isJomlaClient = operation.parentClientName == "abdelwahab" ||
                        operation.parent_M2Client_KeyID == Jomla_Clients.ECHATILLANTS_KEY_ID ||
                        operation.parent_M2Client_KeyID == Jomla_Clients.Au_Command_KEY_ID

                // Check if parent client is invalid (not one of the protected clients)
                val isInvalidClient = !isJomlaClient

                // Delete if parent doesn't exist AND client is invalid (not a Jomla client)
                parentBonVent == null && isInvalidClient
            }

            // Delete invalid operations
            operationsToDelete.forEach { operation ->
                repo10OperationVentCouleur.delete(operation)
            }

            if (operationsToDelete.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        repo10OperationVentCouleur.context,
                        "Cleaned up ${operationsToDelete.size} invalid operations",
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
