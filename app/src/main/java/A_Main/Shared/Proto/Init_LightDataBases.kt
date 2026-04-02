package A_Main.Shared.Proto

import EntreApps.Shared.Models.Jomla_Clients
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Models.M8BonVent
import android.util.Log
import kotlinx.coroutines.tasks.await

object Init_LightDataBases {
    data class LightDataBasesResult(
        val m13TarificationInfos: List<M13TarificationInfos> = emptyList(),
        val m14VentPeriode: List<M14VentPeriode> = emptyList(),
        val m8BonVent: List<M8BonVent> = emptyList(),
        val m10OperationVentCouleur: List<M10OperationVentCouleur> = emptyList(),
    )

    /**
     * Fetches all light-database collections from Firebase.
     *
     * @param filteredProductKeys When non-null (i.e. [EntreApps.Shared.Models.Do.DeleteInsertAll_Active_Key] mode),
     *   the four collections are trimmed before returning:
     *   - m14: only the 5 most recent periods (sorted descending by creationTimestamp)
     *   - m8 : only bons whose parent M14 period is in the filtered m14 set
     *          OR whose client is the Echantillon key
     *   - m10: only operations whose parent M8 bon is in the filtered m8 set
     *   - m13: only tariffs whose parent product is in [filteredProductKeys]
     *          AND whose type is not a grossist-only tariff
     *   Passing null fetches everything unfiltered (used by [Do.DeleteInsertAll_Ref_All_Datas]).
     */
    suspend fun returne_FireBase_LightDataBases(
        filteredProductKeys: Set<String>? = null,
    ): LightDataBasesResult {
        val applyFilters = filteredProductKeys != null

        // ── m14 VentPeriode ──────────────────────────────────────────────────
        var m14List = emptyList<M14VentPeriode>()
        try {
            val raw = M14VentPeriode.ref.get().await()
                .children.mapNotNull { it.getValue(M14VentPeriode::class.java) }
            m14List = if (applyFilters) {
                // Keep the 5 most recent periods
                raw.sortedByDescending { it.creationTimestamp }.take(5)
            } else raw
        } catch (_: Exception) {}

        // ── m8 BonVent ───────────────────────────────────────────────────────
        val m14Keys = m14List.map { it.keyID }.toSet()
        var m8List = emptyList<M8BonVent>()
        try {
            val raw = M8BonVent.ref.get().await()
                .children.mapNotNull { it.getValue(M8BonVent::class.java) }
            m8List = if (applyFilters) {
                raw.filter { bon ->
                    bon.parent_M14VentPeriod_KeyId in m14Keys ||
                            bon.parent_M2Client_KeyID == Jomla_Clients.ECHATILLANTS_KEY_ID
                }
            } else raw
        } catch (_: Exception) {}

        // Log M10 ops belonging to the Echatillants client (for debugging the filter)
        val echatillantsM8Keys = m8List
            .filter { it.parent_M2Client_KeyID == Jomla_Clients.ECHATILLANTS_KEY_ID }
            .map { it.keyID }
            .toSet()
        Log.d(
            "Init_LightDataBases",
            "Echatillants bons (${echatillantsM8Keys.size}): $echatillantsM8Keys"
        )
        // ── m10 OperationVentCouleur ─────────────────────────────────────────
        val m8Keys = m8List.map { it.keyID }.toSet()
        var m10List = emptyList<M10OperationVentCouleur>()
        try {
            val raw = M10OperationVentCouleur.ref.get().await()
                .children.mapNotNull { it.getValue(M10OperationVentCouleur::class.java) }
            m10List = if (applyFilters) {
                raw.filter { op -> op.parent_M8BonVent_KeyId in m8Keys }
            } else raw
        } catch (_: Exception) {}

        // Log all M10 ops that belong to the Echatillants client's bons
        val echatillantsM10 = m10List.filter { it.parent_M8BonVent_KeyId in echatillantsM8Keys }
        Log.d(
            "Init_LightDataBases",
            "Echatillants M10 ops (${echatillantsM10.size}): " +
                    echatillantsM10.joinToString { "bon=${it.parent_M8BonVent_KeyId} prod=${it.parent_M1Produit_KeyId}" }
        )
        // ── m13 TarificationInfos ────────────────────────────────────────────
        var m13List = emptyList<M13TarificationInfos>()
        try {
            val raw = M13TarificationInfos.ref.get().await()
                .children.mapNotNull { it.getValue(M13TarificationInfos::class.java) }
            m13List = if (applyFilters) {
                raw.filter { tarif ->
                    tarif.parent_M1Produit_KeyId in filteredProductKeys!! &&
                            !tarif.typeChoisi.its_gro_app
                }
            } else raw
        } catch (_: Exception) {}

        return LightDataBasesResult(
            m13TarificationInfos    = m13List,
            m14VentPeriode          = m14List,
            m8BonVent               = m8List,
            m10OperationVentCouleur = m10List,
        )
    }

}
