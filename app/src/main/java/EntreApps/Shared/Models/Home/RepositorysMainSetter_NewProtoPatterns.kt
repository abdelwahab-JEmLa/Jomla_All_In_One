package EntreApps.Shared.Models.Home

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.AppDatabase
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class RepositorysMainSetter_NewProtoPatterns(
    val appDatabase: AppDatabase,
    val context: Context
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    // -------------------------------------------------------------------------
    // M01Produit
    // -------------------------------------------------------------------------

    fun update_M1Produit(data: M01Produit) {
        composScope.launch {
            appDatabase.dao_M1Produit().update(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M01Produit.ref.updateChildren(updates).await()
        }
    }

    fun delete_M1Produit(data: M01Produit) {
        composScope.launch {
            appDatabase.dao_M1Produit().delete(data)
            M01Produit.ref.child(data.keyID).removeValue().await()
        }
    }

    // -------------------------------------------------------------------------
    // M3CouleurProduitInfos
    // -------------------------------------------------------------------------

    fun update_M3CouleurProduitInfos(
        data: M3CouleurProduitInfos,
        onSuccess: () -> Unit = {}
    ) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur : données non disponibles, mise à jour annulée", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        composScope.launch {
            appDatabase.dao_M3CouleurProduitInfos().update(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M3CouleurProduitInfos.ref.updateChildren(updates).await()
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    // -------------------------------------------------------------------------
    // M16CategorieProduit
    // -------------------------------------------------------------------------

    fun insert_M16CategorieProduit(data: M16CategorieProduit) {
        composScope.launch {
            appDatabase.dao_16CategorieProduit().insert(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M16CategorieProduit.ref.updateChildren(updates).await()
        }
    }

    fun update_M16CategorieProduit(data: M16CategorieProduit) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur : données non disponibles, mise à jour annulée", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        composScope.launch {
            appDatabase.dao_16CategorieProduit().update(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M16CategorieProduit.ref.updateChildren(updates).await()
        }
    }

    // -------------------------------------------------------------------------
    // M13TarificationInfos
    // -------------------------------------------------------------------------

    fun update_M13TarificationInfos(data: M13TarificationInfos) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur : données non disponibles, mise à jour annulée", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        composScope.launch {
            appDatabase.dao_M13TarificationInfos().update(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M13TarificationInfos.ref.updateChildren(updates).await()
        }
    }

    // -------------------------------------------------------------------------
    // M10OperationVentCouleur
    // -------------------------------------------------------------------------

    /**
     * Inserts or updates an [M10OperationVentCouleur] and links it to [selectedTariff].
     * Mirrors the old saveTariff_Et_RelateIt_Au_Vents_Correspond pattern but
     * without requiring ACentralFacade.
     */
    fun upsert_M10OperationVentCouleur(
        operation: M10OperationVentCouleur,
        selectedTariff: M13TarificationInfos
    ) {
        composScope.launch {
            // Persist operation
            appDatabase.dao_M10OperationVentCouleur().upsert(operation)
            val opUpdates = mutableMapOf<String, Any>(operation.keyID to operation)
            M10OperationVentCouleur.ref.updateChildren(opUpdates).await()

            // Persist tariff
            val tariffWithDefaults = selectedTariff.copy(
                defaultNonSaved_Entre = false,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            appDatabase.dao_M13TarificationInfos().update(tariffWithDefaults)
            val tariffUpdates = mutableMapOf<String, Any>(
                tariffWithDefaults.keyID to tariffWithDefaults.toFirebaseMap()
            )
            M13TarificationInfos.ref.updateChildren(tariffUpdates).await()
        }
    }

    // -------------------------------------------------------------------------
    // Z_AppCompt
    // -------------------------------------------------------------------------

    fun setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
        produit: M01Produit,
        currentAppCompt: Z_AppCompt
    ) {
        val updatedAppCompt = currentAppCompt.copy(
            activeFocuce_TariffPrixDifineur_M1ProduitKeyID = produit.keyID,
            activeFocuceTariffPrixDifineurM1ProduitDebugInfos = produit.getDebugInfos(),
        )
        composScope.launch {
            appDatabase.dao_M9AppCompt().upsert(updatedAppCompt)
            val updates = mutableMapOf<String, Any>(updatedAppCompt.keyID to updatedAppCompt)
            Z_AppCompt.ref.updateChildren(updates).await()
        }
    }

    // -------------------------------------------------------------------------
    // Bulk tariff update for all operations of a product
    // -------------------------------------------------------------------------

    fun updateTariffForProductOperations(
        produitKeyID: String,
        newTariff: M13TarificationInfos,
    ) {
        composScope.launch {
            val operations = appDatabase.dao_M10OperationVentCouleur()
                .getAll()
                .filter { it.parent_M1Produit_KeyId == produitKeyID }

            val updated = operations.map { op ->
                op.copy(
                    parentM13TarificationKeyID = newTariff.keyID,
                    parentM13TarificationDebugInfos = newTariff.getDebugInfos(),
                )
            }

            updated.forEach { op ->
                appDatabase.dao_M10OperationVentCouleur().update(op)
                val updates = mutableMapOf<String, Any>(op.keyID to op)
                M10OperationVentCouleur.ref.updateChildren(updates).await()
            }
        }
    }
}
