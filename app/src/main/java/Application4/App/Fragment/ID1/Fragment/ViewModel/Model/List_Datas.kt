package Application4.App.Fragment.ID1.Fragment.ViewModel.Model

import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client

data class List_Datas(
    val m2Client: List<M2Client> = emptyList(),
    val m14VentPeriode: List<M14VentPeriode> = emptyList(),
    val m16CategorieProduit: List<M16CategorieProduit> = emptyList(),
    val m3CouleurProduit: List<M3CouleurProduitInfos> = emptyList(),
    val m9AppCompt: List<Z_AppCompt> = emptyList(),
    val m8BonVent: List<M8BonVent> = emptyList(),
    val m10OperationVentCouleur: List<M10OperationVentCouleur> = emptyList(),
    val m13TarificationInfos: List<M13TarificationInfos> = emptyList(),
)
