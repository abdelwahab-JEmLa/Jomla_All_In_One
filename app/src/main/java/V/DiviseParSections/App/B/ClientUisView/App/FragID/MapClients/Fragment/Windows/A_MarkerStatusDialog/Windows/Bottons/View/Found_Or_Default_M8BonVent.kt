package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View

import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent


data class Found_Or_Default_M8BonVent(
    val found: M8BonVent?,
    val default_If_No_Found: M8BonVent,
)

fun get_Found_Or_Default_M8BonVent(
    aCentralFacade: ACentralFacade,
    relative_M2Client: M2Client,
    etateActuellementEst: M8BonVent.EtateActuellementEst?=null,
    onShowToast: (ToastData) -> Unit = {}
): Found_Or_Default_M8BonVent {
    val getFocusedVars = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent
    val currentPeriod = getFocusedVars.currentActiveFocuced_M14VentPeriode
    val currentPeriodKeyID = currentPeriod!!.keyID
    val relative_M2Client_KeyID = relative_M2Client.keyID

    val found_M8 =
        M8BonVent.find_By_MainValuesKeys_Depuit_List(
            data_List = repo8BonVent.datasValue,
            parent_M14VentPeriod_KeyId = currentPeriodKeyID,
            parent_M2Client_KeyID = relative_M2Client_KeyID,
            relative_Etate = etateActuellementEst,
        )

    val defaultEdited_M8BonVent = M8BonVent.get_default(
        parent_M9AppCompt_KeyID = getFocusedVars.currentActive_M9AppCompt?.keyID ?: "",
        parent_M9AppCompt_DebugInfos = getFocusedVars.currentActive_M9AppCompt?.get_DebugInfos() ?: "",
        parent_M14VentPeriod_KeyId = currentPeriod.keyID ,
        parent_M14VentPeriod_DebugInfos =currentPeriod.get_DebugInfos(),
        parent_M2Client_KeyID = relative_M2Client.keyID,
        parent_M2Client_DebugInfos = relative_M2Client.get_DebugInfos(),
        etateActuellementEst = etateActuellementEst
    )

    return Found_Or_Default_M8BonVent(
        found_M8,
        defaultEdited_M8BonVent,
    )
}
