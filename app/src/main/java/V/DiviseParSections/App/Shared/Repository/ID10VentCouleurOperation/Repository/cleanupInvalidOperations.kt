package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository

import EntreApps.Shared.Models.AbdelwahabJomla_Client_Speciale
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun cleanupInvalidOperations(repo10OperationVentCouleur: Repo10OperationVentCouleur) {
    repo10OperationVentCouleur.repoScope.launch {
        try {
            val specialClientKeyIDs = AbdelwahabJomla_Client_Speciale.entries
                .map { it.keyID }
                .filter { it.isNotEmpty() }
                .toSet()

            val operationsToDelete = repo10OperationVentCouleur.datasValue.filter { operation ->
                val isSpecialClient = operation.parent_M2Client_KeyID in specialClientKeyIDs ||
                        operation.parentClientName == "abdelwahab"

                // Keep special clients, delete all others
                !isSpecialClient

            }

            operationsToDelete.forEach { operation ->
                repo10OperationVentCouleur.delete(operation)
            }

            if (operationsToDelete.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        repo10OperationVentCouleur.context,
                        "Cleaned up ${operationsToDelete.size} invalid operations (kept special clients)",
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
