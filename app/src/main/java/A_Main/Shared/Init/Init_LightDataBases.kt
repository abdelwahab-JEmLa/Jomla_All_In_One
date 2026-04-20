package A_Main.Shared.Init

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import kotlinx.coroutines.tasks.await

object Init_LightDataBases {
    const val nombre_periods_a_prendre = 10

    data class LightDataBasesResult(
        val m13TarificationInfos: List<M13TarificationInfos> = emptyList(),
        val m14VentPeriode: List<M14VentPeriode> = emptyList(),
        val m8BonVent: List<M8BonVent> = emptyList(),
        val m10OperationVentCouleur: List<M10OperationVentCouleur> = emptyList(),
        val m2Clients: List<M2Client> = emptyList(),
    )

    suspend fun returne_FireBase_LightDataBases(
        filteredProductKeys: Set<String>? = null,
    ): LightDataBasesResult {
        val applyFilters = filteredProductKeys != null

        var m14List = emptyList<M14VentPeriode>()
        try {
            val raw = M14VentPeriode.ref.get().await()
                .children.mapNotNull { it.getValue(M14VentPeriode::class.java) }
            m14List = if (applyFilters) {
                raw.sortedByDescending { it.creationTimestamp }.take(nombre_periods_a_prendre)
            } else raw
        } catch (_: Exception) {}

        val m14Keys = m14List.map { it.keyID }.toSet()

        var m8List = emptyList<M8BonVent>()
        try {
            val raw = M8BonVent.ref.get().await()
                .children.mapNotNull { it.getValue(M8BonVent::class.java) }
            m8List = if (applyFilters) {
                raw.filter { bon ->
                    bon.parent_M14VentPeriod_KeyId in m14Keys
                }
            } else raw
        } catch (_: Exception) {}

        val m8Keys = m8List.map { it.keyID }.toSet()

        var m10List = emptyList<M10OperationVentCouleur>()
        try {
            val raw = M10OperationVentCouleur.ref.get().await()
                .children.mapNotNull { it.getValue(M10OperationVentCouleur::class.java) }
            m10List = if (applyFilters) raw.filter { op -> op.parent_M8BonVent_KeyId in m8Keys } else raw
        } catch (_: Exception) {}

        var m13List = emptyList<M13TarificationInfos>()
        try {
            val raw = M13TarificationInfos.ref.get().await()
                .children.mapNotNull { it.getValue(M13TarificationInfos::class.java) }
            m13List = if (applyFilters) {
                raw.filter { tariff ->
                    tariff.parent_M1Produit_KeyId in filteredProductKeys!!
                }
            } else raw
        } catch (_: Exception) {}

        var m2ClientList = emptyList<M2Client>()
        try {
            val raw = M2Client.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val client = child.getValue(M2Client::class.java) ?: return@mapNotNull null
                    if (client.keyID.isBlank() || client.keyID != nodeKey)
                        client.copy(keyID = nodeKey)
                    else
                        client
                }
            m2ClientList = if (applyFilters)
                raw
            else
                raw
        } catch (_: Exception) {}

        return LightDataBasesResult(
            m13TarificationInfos    = m13List,
            m14VentPeriode          = m14List,
            m8BonVent               = m8List,
            m10OperationVentCouleur = m10List,
            m2Clients               = m2ClientList,
        )
    }
}
