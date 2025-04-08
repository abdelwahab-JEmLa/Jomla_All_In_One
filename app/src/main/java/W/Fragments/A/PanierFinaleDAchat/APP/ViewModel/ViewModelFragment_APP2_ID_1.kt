package W.Fragments.A.PanierFinaleDAchat.APP.ViewModel

import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ViewModelFragment_APP2_ID_1(
    val _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    val _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
    val _1_3_BonAchat_Repository: _1_3_BonAchat_Repository,
    val _1_4_PeriodeVent_Repository: _1_4_PeriodeVent_Repository ,
    val _1_5_Vendeur_Repository: _1_5_Vendeur_Repository
) : ViewModel() {
    val isDataLoading: StateFlow<Boolean> = combine(
        _1_1_CouleurAcheteOperation_Repository.progressRepo,
        _1_2_ProduitAcheteOperation_Repository.progressRepo,
        _1_3_BonAchat_Repository.progressRepo,
        _1_4_PeriodeVent_Repository.progressRepo,
        _1_5_Vendeur_Repository.progressRepo
    ) { progress1, progress2, progress3, progress4, progress5 ->
        progress1 < 1.0f || progress2 < 1.0f || progress3 < 1.0f || progress4 < 1.0f || progress5 < 1.0f
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true // Initially loading
    )
}
