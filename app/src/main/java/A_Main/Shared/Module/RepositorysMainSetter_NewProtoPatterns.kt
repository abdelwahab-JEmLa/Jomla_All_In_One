package A_Main.Shared.Module

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.Ref_list_Filtred_Keys_M3Couleur_Main_Values
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Modules.Base.AppDatabase
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

    fun update_List_M1Produit_BathFireBase(
        datas: List<M01Produit>,
        onSuccess: () -> Unit = {}
    ) {
        composScope.launch {
            if (datas.isNotEmpty()) {
                datas.forEach { appDatabase.dao_M1Produit().update(it) }
                val updates: Map<String, Any> = datas.associate { it.keyID to it.toFirebaseMap() }
                M01Produit.Companion.ref.updateChildren(updates).await()
            }
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun update_List_M3CouleurProduitInfos_BathFireBase(
        datas: List<M3CouleurProduitInfos>,
        onSuccess: () -> Unit = {}
    ) {
        composScope.launch {
            if (datas.isNotEmpty()) {
                datas.forEach { appDatabase.dao_M03CouleurProduitInfos().update(it) }
                val updates: Map<String, Any> = datas.associate { it.keyID to it.to_Map() }
                M3CouleurProduitInfos.Companion.ref.updateChildren(updates).await()
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Firebase : ${datas.size} couleurs synchronisées ✓",
                    Toast.LENGTH_SHORT
                ).show()
                onSuccess()
            }
        }
    }

    fun update_M1Produit(data: M01Produit) {
        composScope.launch {
            appDatabase.dao_M1Produit().upsertData(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M01Produit.Companion.ref.updateChildren(updates).await()
        }
    }

    fun delete_M1Produit(data: M01Produit) {
        composScope.launch {
            appDatabase.dao_M1Produit().delete(data)
            M01Produit.Companion.ref.child(data.keyID).removeValue().await()
        }
    }

    fun delete_M3CouleurProduitInfos(data: M3CouleurProduitInfos) {
        composScope.launch {
            appDatabase.dao_M03CouleurProduitInfos().delete(data)
            M3CouleurProduitInfos.Companion.ref.child(data.keyID).removeValue().await()
        }
    }

    fun insertFireBase_list_Main_Values_M3CouleurProduitInfos(
        keys: Map<String, Ref_list_Filtred_Keys_M3Couleur_Main_Values>,
        onSuccess: () -> Unit = {}
    ) {
        val ref_listKeys = M3CouleurProduitInfos.Companion.ref_listKeys_M3CouleurProduitInfos
        composScope.launch {
            if (keys.isNotEmpty()) {
                val updates: Map<String, Any> = keys.mapValues { (_, v) ->
                    mapOf(
                        "nom"                    to v.nom,
                        "classment"              to v.classment,
                        "activated"              to v.activated,
                        "parentProduitKeyID"     to v.parentProduitKeyID,
                        "parentProduitDebugName" to v.parentProduitDebugName,
                        "parentProduitClassement" to v.parentProduitClassement
                    )
                }
                ref_listKeys.removeValue().await()
                ref_listKeys.updateChildren(updates).await()
            }
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun deleteFireBase_listKeys_M3CouleurProduitInfos(
        onSuccess: () -> Unit = {}
    ) {
        val ref_listKeys = M3CouleurProduitInfos.Companion.ref_listKeys_M3CouleurProduitInfos
        composScope.launch {
            ref_listKeys.removeValue().await()
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun deleteInsertFireBase_listKeys_M3CouleurProduitInfos(
        keys: Map<String, Boolean>,
        onSuccess: () -> Unit = {}
    ) {
        val ref_listKeys = M3CouleurProduitInfos.Companion.ref_listKeys_M3CouleurProduitInfos
        composScope.launch {
            ref_listKeys.removeValue().await()
            if (keys.isNotEmpty()) {
                val updates: Map<String, Any> = keys.mapValues { it.value }
                ref_listKeys.updateChildren(updates).await()
            }
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun update_M3CouleurProduitInfos(
        data: M3CouleurProduitInfos,
        onSuccess: () -> Unit = {}
    ) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur : données non disponibles, mise à jour annulée",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return
        }
        composScope.launch {
            appDatabase.dao_M03CouleurProduitInfos().upsert(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.to_Map())
            M3CouleurProduitInfos.Companion.ref.updateChildren(updates).await()
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    // -------------------------------------------------------------------------
    // M8BonVent
    // -------------------------------------------------------------------------

    fun update_M8BonVent(
        data: M8BonVent,
        onSuccess: () -> Unit = {}
    ) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur : données non disponibles, mise à jour annulée",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return
        }
        composScope.launch {
            try {
                appDatabase.dao_M8BonVent().update(data)
                val updates = mutableMapOf<String, Any>(data.keyID to data.to_Map())
                M8BonVent.Companion.ref.updateChildren(updates).await()
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur lors de la mise à jour : ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // M16CategorieProduit
    // -------------------------------------------------------------------------

    fun insert_M16CategorieProduit(data: M16CategorieProduit) {
        composScope.launch {
            appDatabase.dao_16CategorieProduit().insert(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M16CategorieProduit.Companion.ref.updateChildren(updates).await()
        }
    }

    fun update_M16CategorieProduit(data: M16CategorieProduit) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur : données non disponibles, mise à jour annulée",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return
        }
        composScope.launch {
            appDatabase.dao_16CategorieProduit().update(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M16CategorieProduit.Companion.ref.updateChildren(updates).await()
        }
    }

    // -------------------------------------------------------------------------
    // M13TarificationInfos
    // -------------------------------------------------------------------------

    fun update_M13TarificationInfos(data: M13TarificationInfos) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur : données non disponibles, mise à jour annulée",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return
        }
        composScope.launch {
            appDatabase.dao_M13TarificationInfos().update(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M13TarificationInfos.Companion.ref.updateChildren(updates).await()
        }
    }

    // -------------------------------------------------------------------------
    // M10OperationVentCouleur
    // -------------------------------------------------------------------------

    /** Deletes a single operation from Room and Firebase. */
    fun delete_M10OperationVentCouleur(
        data: M10OperationVentCouleur,
        onSuccess: () -> Unit = {}
    ) = deleteList_M10OperationVentCouleur(listOf(data), onSuccess)

    fun deleteList_M10OperationVentCouleur(
        datas: List<M10OperationVentCouleur>,
        onSuccess: () -> Unit = {}
    ) {
        if (datas.isEmpty()) {
            onSuccess()
            return
        }
        composScope.launch {
            try {
                datas.forEach { appDatabase.dao_M10OperationVentCouleur().delete(it) }
                val updates: Map<String, Any?> = datas.associate { it.keyID to null }
                M10OperationVentCouleur.Companion.ref.updateChildren(updates).await()
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur lors de la suppression : ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun upsert_M10OperationVentCouleur(
        operation: M10OperationVentCouleur,
        selectedTariff: M13TarificationInfos
    ) {
        composScope.launch {
            appDatabase.dao_M10OperationVentCouleur().upsert(operation)
            val opUpdates = mutableMapOf<String, Any>(operation.keyID to operation)
            M10OperationVentCouleur.Companion.ref.updateChildren(opUpdates).await()

            val tariffWithDefaults = selectedTariff.copy(
                defaultNonSaved_Entre = false,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            appDatabase.dao_M13TarificationInfos().update(tariffWithDefaults)
            val tariffUpdates = mutableMapOf<String, Any>(
                tariffWithDefaults.keyID to tariffWithDefaults.toFirebaseMap()
            )
            M13TarificationInfos.Companion.ref.updateChildren(tariffUpdates).await()
        }
    }

    // -------------------------------------------------------------------------
    // M9AppCompt
    // -------------------------------------------------------------------------

    fun insert_M9AppCompt(
        data: M09AppCompt,
        onSuccess: () -> Unit = {}
    ) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur : données du compte invalides", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        composScope.launch {
            try {
                appDatabase.dao_M9AppCompt().insert(data)
                val updates = mutableMapOf<String, Any>(data.keyID to data.to_Map())
                M09AppCompt.Companion.ref.updateChildren(updates).await()
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur lors de l'insertion : ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun update_M9AppCompt(
        data: M09AppCompt,
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
            try {
                appDatabase.dao_M9AppCompt().update(data)
                val updates = mutableMapOf<String, Any>(data.keyID to data.to_Map())
                M09AppCompt.Companion.ref.updateChildren(updates).await()
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur lors de la mise à jour : ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
        produit: M01Produit,
        currentAppCompt: M09AppCompt
    ) {
        val updatedAppCompt = currentAppCompt.copy(
            activeFocuce_TariffPrixDifineur_M1ProduitKeyID = produit.keyID,
            activeFocuceTariffPrixDifineurM1ProduitDebugInfos = produit.getDebugInfos(),
        )
        composScope.launch {
            appDatabase.dao_M9AppCompt().upsert(updatedAppCompt)
            val updates = mutableMapOf<String, Any>(updatedAppCompt.keyID to updatedAppCompt)
            M09AppCompt.Companion.ref.updateChildren(updates).await()
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
                M10OperationVentCouleur.Companion.ref.updateChildren(updates).await()
            }
        }
    }

    // -------------------------------------------------------------------------
    // M2Client
    // -------------------------------------------------------------------------

    fun update_M2(new: M2Client) {
        if (new.keyID.isBlank()) return
        composScope.launch {
            appDatabase.dao_M2Client().upsert(new)
            val updates = mutableMapOf<String, Any>(new.keyID to new.toFirebaseMap())
            M2Client.Companion.ref.updateChildren(updates).await()
        }
    }
}
