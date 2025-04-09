package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
import Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase._2_2_ClientsDataBase_Repository

class _0_0_HeadOfRepositorys_Model(
    val _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    val _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
    val _1_3_BonAchat_Repository: _1_3_BonAchat_Repository,
    val _1_4_PeriodeVent_Repository: _1_4_PeriodeVent_Repository,
    val _1_5_Vendeur_Repository: _1_5_Vendeur_Repository,

    val _2_2_ClientsDataBase_Repository: _2_2_ClientsDataBase_Repository,
    var activeVID_1_3_BonAchat: Long,
    var activeVidRepository_1_4: Long=0,
    var activeVidRepository_1_5: Long=0,

    )
