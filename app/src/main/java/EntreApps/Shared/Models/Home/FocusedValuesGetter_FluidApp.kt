package EntreApps.Shared.Models.Home

import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.Archive.List_Datas
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Stable
class FocusedValues_NewProtoPatterns(
    list_Datas: StateFlow<List_Datas?>,  // Now receives the VM's flow directly — no duplicate state
) {
    private val _activeCentralValues = MutableStateFlow(ActiveCentralValues())

    val active_Central_Values: StateFlow<ActiveCentralValues> = combine(
        list_Datas,
        _activeCentralValues
    ) { listDatas, centralValues ->
        val activeCompt = listDatas?.m9AppCompt
            ?.find { it.keyID == centralValues.activeCompt_KeyID }

        val activeOnVent_M8BonVent = listDatas?.m8BonVent
            ?.find { it.keyID == activeCompt?.onVentM8BonVentKey }

        val onVentList = listDatas?.m10OperationVentCouleur
            ?.filter { it.parent_M8BonVent_KeyId == activeCompt?.onVentM8BonVentKey }
            ?: emptyList()

        val activeOnVent_M2Client = activeOnVent_M8BonVent?.parent_M2Client_KeyID
            ?.let { clientKey -> listDatas?.m2Client?.find { it.keyID == clientKey } }

        val activePeriodKeyID = activeCompt?.current_OnVent_M14VentPeriode_KeyID ?: ""
        val filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod =
            if (activePeriodKeyID.isBlank()) emptyList()
            else listDatas?.m8BonVent
                ?.filter { it.parent_M14VentPeriod_KeyId == activePeriodKeyID }
                ?: emptyList()

        val currentApp_Est_Admin = activeCompt?.its_Admin == true
        val currentApp_ItsWorkChezGrossisst = activeCompt?.travailleChezGrossisst3Ali == true

        centralValues.copy(
            activeCompt = activeCompt,
            activeOnVent_M8BonVent = activeOnVent_M8BonVent,
            onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent = onVentList,
            activeOnVent_M2Client = activeOnVent_M2Client,
            filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod = filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod,
            currentApp_Est_Admin = currentApp_Est_Admin,
            currentApp_ItsWorkChezGrossisst = currentApp_ItsWorkChezGrossisst,
        )
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = ActiveCentralValues()
    )

    fun update_activeCentralValues(new: ActiveCentralValues) {
        _activeCentralValues.value = new
    }
    fun update_oneMutableStateLesseRessources(isControleFabVisible: Boolean) {
        _activeCentralValues.value.isControleFabVisible = isControleFabVisible
    }
}


