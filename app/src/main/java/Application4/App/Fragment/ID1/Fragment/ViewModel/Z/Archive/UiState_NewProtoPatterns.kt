package Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive

import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client

data class UiState_NewProtoPatterns(
    val list_Datas: List_Datas? = null,
    val initDatasProgressEtate: Float = 0f,
) {
    val list_M16CategorieProduit: List<M16CategorieProduit>
        get() = list_Datas?.m16CategorieProduit ?: emptyList()
    val list_M13TarificationInfos: List<M13TarificationInfos>
        get() = list_Datas?.m13TarificationInfos ?: emptyList()
}

data class List_Datas(
    val m2Client: List<M2Client> = emptyList(),
    val m14VentPeriode: List<M14VentPeriode> = emptyList(),
    val m16CategorieProduit: List<M16CategorieProduit> = emptyList(),
    val m8BonVent: List<M8BonVent> = emptyList(),
    val m10OperationVentCouleur: List<M10OperationVentCouleur> = emptyList(),
    val m13TarificationInfos: List<M13TarificationInfos> = emptyList(),
)
