package EntreApps.Shared.Models.Home

import EntreApps.Shared.Models.M3CouleurProduitInfos


fun find_ListM3CouleurInfos_By_Parent_Produit_KeyID(datas : List<M3CouleurProduitInfos>, parentBProduitInfosKeyID: String) =
    datas.filter { it.parentBProduitInfosKeyID == parentBProduitInfosKeyID }
