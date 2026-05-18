package A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent

data class Datas(
    val  relative_produits: List<M01Produit>?,
    val  relative_couleurs: List<M3CouleurProduitInfos>?,
    val relative_list_tariff: List<M13TarificationInfos>,
    val  on_vent_couleurs: List<M10OperationVentCouleur>,
    val on_vent_bon: M8BonVent?,
    val on_vent_m2client: M2Client?,
)
