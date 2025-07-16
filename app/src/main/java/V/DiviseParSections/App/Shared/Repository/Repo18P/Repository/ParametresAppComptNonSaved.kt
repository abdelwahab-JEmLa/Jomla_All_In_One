package V.DiviseParSections.App.Shared.Repository.Repo18P.Repository

import V.DiviseParSections.App._0.Navigation.Screen

data class ParametresAppComptNonSaved(
    val itsDevMode: Boolean = true,
    val abdelwahabCompt_KeyId: String = "-OV9dYujH9cA3yEx8AYT",
    val abdelmomen_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s4",
    val currentActiveFocucedM9AppComptKeyID: String = abdelmomen_Compt_KeyId,
    val currentActiveFocucedM9AppComptDebugInfos: String = "",

    val activeWindowsSearchProduit: Boolean = false,
    val devStartUpScree: Screen = Screen.FragmentProduitFastSearchDialog,
    var enablePerformAutoClickImageDisplayer: Boolean = false,
    val isControleFabVisible: Boolean = false,
)
