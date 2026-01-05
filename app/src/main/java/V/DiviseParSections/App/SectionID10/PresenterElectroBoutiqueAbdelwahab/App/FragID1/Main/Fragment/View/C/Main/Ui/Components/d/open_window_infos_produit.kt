package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.d

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable

 fun open_window_infos_produit(
    focusedVarsHandlerFacade: FocusedActiveValuesFacade,
    relative_M1Produit: ArticlesBasesStatsTable,
    onClickToOpenWindow: () -> Unit
) {
    focusedVarsHandlerFacade.focusedValuesSetter.active_CurrentApp_activeDialogSearchM1Produit(
        true
    )
    focusedVarsHandlerFacade.focusedValuesSetter.set_Current_startTextSearchM1Produit(
        relative_M1Produit.nom
    )
    focusedVarsHandlerFacade.focusedValuesSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
        relative_M1Produit
    )
    onClickToOpenWindow()
}
