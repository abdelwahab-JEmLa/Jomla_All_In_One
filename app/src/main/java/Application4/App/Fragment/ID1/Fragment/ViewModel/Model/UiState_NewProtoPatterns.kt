package Application4.App.Fragment.ID1.Fragment.ViewModel.Model

import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos

data class UiState_NewProtoPatterns(
    val active_Central_Values: ActiveCentralValues = ActiveCentralValues.Companion.get_Default(),
    val list_Datas: List_Datas? = null,
    val initDatasProgressEtate: Float = 0f,
) {
    val list_M16CategorieProduit: List<M16CategorieProduit>
        get() = list_Datas?.m16CategorieProduit ?: emptyList()
    val list_M3CouleurProduit: List<M3CouleurProduitInfos>
        get() = list_Datas?.m3CouleurProduit ?: emptyList()
    val list_M13TarificationInfos: List<M13TarificationInfos>
        get() = list_Datas?.m13TarificationInfos ?: emptyList()
}
