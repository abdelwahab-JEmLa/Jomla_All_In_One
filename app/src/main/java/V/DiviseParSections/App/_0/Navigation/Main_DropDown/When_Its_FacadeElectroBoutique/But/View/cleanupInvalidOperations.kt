package V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique.But.View

import EntreApps.Shared.Models.Client_Speciale
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun cleanupInvalidOperations_Np(
    repo10OperationVentCouleur: Repo10OperationVentCouleur,
    on_vent_key: String
) {
    repo10OperationVentCouleur.repoScope.launch {
        try {
            val specialClientKeyIDs = Client_Speciale.entries
                .map { it.keyID }
                .filter { it.isNotEmpty() }
                .toSet()

            val operationsToDelete = repo10OperationVentCouleur.datasValue.filter { operation ->
                // Keep: belongs to the currently active on_vent BonVent
                if (operation.parent_M8BonVent_KeyId == on_vent_key) return@filter false

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
