package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_Repository
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase_Repository
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_Repository
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow

class _0_0_HeadOfRepositorys_Model(
    val _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    val _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,

    val _1_3_BonAchat_Repository: _1_3_BonAchat_Repository,
    var activeId_1_3_BonAchat: MutableStateFlow<Long>,

    val repository_1_4_PeriodeVent: _1_4_PeriodeVent_Repository,

    val repository_1_5_Vendeur: _1_5_Vendeur_Repository,

    val _2_1_ProduitsDataBase_Repository: _2_1_ProduitsDataBase_Repository,
    val _3_ClientsDataBase_Repository: _3_ClientsDataBase_Repository,

    val _4_CouleurOperationCommand_Repository: _4_CouleurOperationCommand_Repository,
    val databaseReference_1_5_Vendeur: DatabaseReference = _1_5_Vendeur_Repository.sonDataBaseRef,
    val databaseReference_1_4_PeriodeVent: DatabaseReference = _1_4_PeriodeVent_Repository.sonDataBaseRef,

    val activeIdDe_1_5_Vendeur: Long = 1L,
    val activeIdDe_1_4_PeriodeVent: Long = 2L,
    )
