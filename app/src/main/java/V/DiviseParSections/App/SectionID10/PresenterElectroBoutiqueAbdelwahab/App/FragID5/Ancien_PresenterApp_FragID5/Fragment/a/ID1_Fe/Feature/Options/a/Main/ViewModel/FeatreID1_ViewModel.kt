package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.a.Main.ViewModel

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Modules.Setter_LongOperations
import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Stable
class ActiveDatas {
    var active_M9Compt: M09AppCompt? by mutableStateOf(null)
    var list_M8bon: List<M8BonVent>? by mutableStateOf(null)
    var list_M03: List<M3CouleurProduitInfos>? by mutableStateOf(null)
}

@SuppressLint("StaticFieldLeak")
class FeatureID1_ViewModel(
    private val appDatabase: AppDatabase,
) : ViewModel() {
    val active_Datas = ActiveDatas()
    val setter_LongOperations = Setter_LongOperations(
        appDatabase,
    )

    var captureRequested by mutableStateOf(false)

    init {
        viewModelScope.launch {
            active_Datas.list_M8bon = (appDatabase.dao_M8BonVent().getAll())
            active_Datas.list_M03 = (appDatabase.dao_M03CouleurProduitInfos().getAll())
            active_Datas.active_M9Compt = appDatabase.dao_M9AppCompt().getBy_M00_Lence_Key()
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun reload() {
        viewModelScope.launch {
            active_Datas.list_M8bon = appDatabase.dao_M8BonVent().getAll()
            active_Datas.list_M03 = appDatabase.dao_M03CouleurProduitInfos().getAll()
            active_Datas.active_M9Compt = appDatabase.dao_M9AppCompt().getBy_M00_Lence_Key()
        }
    }

    fun update_M8(it: M8BonVent) {
        active_Datas.list_M8bon = active_Datas.list_M8bon
            ?.map { bon -> if (bon.keyID == it.keyID) it else bon }

        viewModelScope.launch {
            setter_LongOperations.update_M8(it)
        }
    }

    fun update_m9(it: M09AppCompt) {
        active_Datas.active_M9Compt = it
        viewModelScope.launch {
            appDatabase.dao_M9AppCompt().upsert(it)
        }
    }

    fun add_New_M8BonVent(bon: M8BonVent) {
        viewModelScope.launch {
            setter_LongOperations.add_New_M8BonVent(bon)
        }
    }
}


