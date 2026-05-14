package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.List_Datas
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import kotlinx.coroutines.flow.update

private const val TAG_SETTER = "Setter_ViewModel"

class Setter_ViewModel_NewProtoPatterns(private val vm: A_ViewModel_NewProtoPatterns) {
    // M01 ─────────────────────────────────────────────────────────────────
    fun update_m1Produit(new: M01Produit) {
        vm.active_Datas.list_M1Produit =
            vm.active_Datas.list_M1Produit?.map { if (it.keyID == new.keyID) new else it }
        vm.repositorysMainSetter_NewProtoPatterns.update_M1Produit(new)
    }

    fun delete_m1Produit(produit: M01Produit) {
        vm.active_Datas.list_M1Produit =
            vm.active_Datas.list_M1Produit?.filter { it.keyID != produit.keyID }
        vm.repositorysMainSetter_NewProtoPatterns.delete_M1Produit(produit)
    }

    // M03 ─────────────────────────────────────────────────────────────────
    fun deleteInsertFireBase_listKeys_M3CouleurProduitInfos(
        keys: Map<String, Boolean>,
        onSuccess: () -> Unit = {}
    ) {
        vm.repositorysMainSetter_NewProtoPatterns
            .deleteInsertFireBase_listKeys_M3CouleurProduitInfos(keys, onSuccess)
    }

    fun update_m3couleur(couleur: M3CouleurProduitInfos) {
        vm.active_Datas.list_M03CouleurProduitInfos =
            vm.active_Datas.list_M03CouleurProduitInfos?.map { if (it.keyID == couleur.keyID) couleur else it }
        vm.repositorysMainSetter_NewProtoPatterns.update_M3CouleurProduitInfos(couleur)
    }

    fun delete_m3couleur(couleur: M3CouleurProduitInfos) {
        vm.active_Datas.list_M03CouleurProduitInfos =
            vm.active_Datas.list_M03CouleurProduitInfos?.filter { it.keyID != couleur.keyID }
        vm.repositorysMainSetter_NewProtoPatterns.delete_M3CouleurProduitInfos(couleur)
    }

    fun update_depot_count(
        couleur: M3CouleurProduitInfos,
        newDepotCount: Int,
        onSuccess: () -> Unit = {},
    ) {
        val updated = couleur.copy(
            count_Don_Depot = newDepotCount,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        vm.active_Datas.list_M03CouleurProduitInfos =
            vm.active_Datas.list_M03CouleurProduitInfos?.map { if (it.keyID == updated.keyID) updated else it }
        vm.repositorysMainSetter_NewProtoPatterns.update_M3CouleurProduitInfos(
            data = updated,
            onSuccess = onSuccess
        )
    }

    // M09 ─────────────────────────────────────────────────────────────────

    fun update_active_Compt(compt: M09AppCompt) {
        vm.active_Datas.active_M9Compt = compt
        vm.repositorysMainSetter_NewProtoPatterns.update_M9AppCompt(compt) {
        }
    }

    // M10 ─────────────────────────────────────────────────────────────────

    fun addNew_listM10OperationVentCouleur(
        updatedList: List<M10OperationVentCouleur>?,
    ) {
        if (!updatedList.isNullOrEmpty()) {
            val existingKeys = vm.active_Datas.list_M10OperationVentCouleur
                ?.map { it.keyID }?.toSet() ?: emptySet()
            val newOnly = updatedList.filter { it.keyID !in existingKeys }
            vm.active_Datas.list_M10OperationVentCouleur =
                (vm.active_Datas.list_M10OperationVentCouleur ?: emptyList()) + newOnly
        }
        upsert_M10OperationVentCouleur(updatedList)
    }

    /**
     * Updates existing M10 operations in-place.
     * Aborts if any entry in [updatedList] does not already exist in the current list —
     * those should go through [addNew_listM10OperationVentCouleur] instead.
     */
    fun update_listM10OperationVentCouleur(
        updatedList: List<M10OperationVentCouleur>?,
    ) {
        val currentAll = vm.active_Datas.list_M10OperationVentCouleur ?: emptyList()
        val existingKeys = currentAll.map { it.keyID }.toSet()

        val missingEntries = updatedList?.filter { it.keyID !in existingKeys }
        if (!missingEntries.isNullOrEmpty()) {
            android.util.Log.e(
                TAG_SETTER,
                "update_listM10OperationVentCouleur: entry not found — aborting. " +
                        "Missing keyIDs: ${missingEntries.map { it.keyID }}"
            )
            return
        }

        val updatedMap = updatedList?.associateBy { it.keyID } ?: emptyMap()
        vm.active_Datas.list_M10OperationVentCouleur =
            currentAll.map { existing -> updatedMap[existing.keyID] ?: existing }
        upsert_M10OperationVentCouleur(updatedList)
    }

    fun update_listM10OperationVentCouleur_FilteredBy_activeM8BonVent(
        updatedList: List<M10OperationVentCouleur>?,
    ) {
        val updatedMap = updatedList?.associateBy { it.keyID } ?: emptyMap()
        vm.active_Datas.list_M10OperationVentCouleur =
            vm.active_Datas.list_M10OperationVentCouleur?.map { existing ->
                updatedMap[existing.keyID] ?: existing
            }
        upsert_M10OperationVentCouleur(updatedList)
    }

    /**
     * Removes [op] from in-memory state and propagates the delete to DAO + Firebase.
     * Use this when quantity drops to 0 — do NOT route through [update_listM10OperationVentCouleur],
     * which only maps/replaces and will silently keep the entry alive.
     */
    fun delete_M10OperationVentCouleur(op: M10OperationVentCouleur) {
        vm.active_Datas.list_M10OperationVentCouleur =
            vm.active_Datas.list_M10OperationVentCouleur?.filter { it.keyID != op.keyID }
        vm.repositorysMainSetter_NewProtoPatterns.delete_M10OperationVentCouleur(op)
    }

    /**
     * Inserts only genuinely new entries (by keyID) into the current list.
     * Entries whose keyID already exists are silently skipped — use
     * [update_listM10OperationVentCouleur] to update existing ones.
     */
    private fun addNew_ListM10OperationVentCouleur(datas: List<M10OperationVentCouleur>?) {
        if (datas.isNullOrEmpty()) return
        val existingKeys = vm.active_Datas.list_M10OperationVentCouleur
            ?.map { it.keyID }?.toSet() ?: emptySet()
        val newOnly = datas.filter { it.keyID !in existingKeys }
        if (newOnly.isEmpty()) return
        vm.active_Datas.list_M10OperationVentCouleur =
            (vm.active_Datas.list_M10OperationVentCouleur ?: emptyList()) + newOnly
        upsert_M10OperationVentCouleur(newOnly)
    }

    private fun upsert_M10OperationVentCouleur(updatedList: List<M10OperationVentCouleur>?) {
        val allTariffs = vm._uiStateNewProtoPatterns.value.list_Datas?.m13TarificationInfos
        updatedList?.forEach { operation ->
            val tariff = allTariffs?.find { it.keyID == operation.parentM13TarificationKeyID }
                ?: allTariffs?.filter { it.parent_M1Produit_KeyId == operation.parent_M1Produit_KeyId }
                    ?.maxByOrNull { it.creationTimestamps }
            if (tariff == null) return@forEach
            val opToSave = if (tariff.keyID != operation.parentM13TarificationKeyID) {
                operation.copy(
                    parentM13TarificationKeyID = tariff.keyID,
                    parentM13TarificationDebugInfos = tariff.getDebugInfos()
                )
            } else operation
            vm.repositorysMainSetter_NewProtoPatterns.upsert_M10OperationVentCouleur(opToSave, tariff)
        }
    }

    // M13 ─────────────────────────────────────────────────────────────────

    fun update_M13TarificationInfos(tariff: M13TarificationInfos) {
        vm._uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(
                list_Datas = current.copy(
                    m13TarificationInfos = current.m13TarificationInfos
                        .filter { it.keyID != tariff.keyID } + tariff
                )
            )
        }
        vm.repositorysMainSetter_NewProtoPatterns.update_M13TarificationInfos(tariff)

        val currentAll = vm.active_Datas.list_M10OperationVentCouleur
        val affected = currentAll?.filter { it.parentM13TarificationKeyID == tariff.keyID }
        if (!affected.isNullOrEmpty()) {
            vm.active_Datas.list_M10OperationVentCouleur = currentAll.map { op ->
                if (op.parentM13TarificationKeyID == tariff.keyID)
                    op.copy(
                        prix_de_Vent_entre_directement_NewProto = tariff.prixCurrency,
                        typeTarificationEnumT2 = tariff.typeChoisi,
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                else op
            }
            upsert_M10OperationVentCouleur(vm.active_Datas.list_M10OperationVentCouleur?.filter { op ->
                affected.any { it.keyID == op.keyID }
            })
        }
    }

    // M16 ─────────────────────────────────────────────────────────────────

    fun insert_M16CategorieProduit(new: M16CategorieProduit) {
        vm._uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(list_Datas = current.copy(m16CategorieProduit = current.m16CategorieProduit + new))
        }
        vm.repositorysMainSetter_NewProtoPatterns.insert_M16CategorieProduit(new)
    }

    fun update_m16CategorieProduit(new: M16CategorieProduit) {
        vm._uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(
                list_Datas = current.copy(
                    m16CategorieProduit = current.m16CategorieProduit.map {
                        if (it.keyID == new.keyID) new else it
                    }
                )
            )
        }
        vm.repositorysMainSetter_NewProtoPatterns.update_M16CategorieProduit(new)
    }

    fun update_m2(new: M2Client) {
        vm._uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(
                list_Datas = current.copy(
                    m2Client = current.m2Client.map {
                        if (it.keyID == new.keyID) new else it
                    }
                )
            )
        }
        vm.repositorysMainSetter_NewProtoPatterns.update_M2(new)
    }
}
