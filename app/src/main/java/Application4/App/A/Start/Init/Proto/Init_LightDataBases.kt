package Application4.App.A.Start.Init.Proto

import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.M10OperationVentCouleur
import kotlinx.coroutines.tasks.await

object Init_LightDataBases {
    data class LightDataBasesResult(
        val m13TarificationInfos: List<M13TarificationInfos> = emptyList(),
        val m14VentPeriode: List<M14VentPeriode> = emptyList(),
        val m8BonVent: List<M8BonVent> = emptyList(),
        val m10OperationVentCouleur: List<M10OperationVentCouleur> = emptyList(),
    )

    suspend fun returne_FireBase_LightDataBases(): LightDataBasesResult {
        var m13List = emptyList<M13TarificationInfos>()
        var m14List = emptyList<M14VentPeriode>()
        var m8List  = emptyList<M8BonVent>()
        var m10List = emptyList<M10OperationVentCouleur>()

        try {
            m13List = M13TarificationInfos.ref.get().await()
                .children.mapNotNull { it.getValue(M13TarificationInfos::class.java) }
        } catch (_: Exception) {}

        try {
            m14List = M14VentPeriode.ref.get().await()
                .children.mapNotNull { it.getValue(M14VentPeriode::class.java) }
        } catch (_: Exception) {}

        try {
            m8List = M8BonVent.ref.get().await()
                .children.mapNotNull { it.getValue(M8BonVent::class.java) }
        } catch (_: Exception) {}

        try {
            m10List = M10OperationVentCouleur.ref.get().await()
                .children.mapNotNull { it.getValue(M10OperationVentCouleur::class.java) }
        } catch (_: Exception) {}

        return LightDataBasesResult(
            m13TarificationInfos    = m13List,
            m14VentPeriode          = m14List,
            m8BonVent               = m8List,
            m10OperationVentCouleur = m10List,
        )
    }
}
