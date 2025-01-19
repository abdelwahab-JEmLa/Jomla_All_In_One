package Z_MasterOfApps.Z.Android.Actions._1.C_Serveur._1NavHost.Fragment_Id4

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.CreeDepuitAncienDataBases

class OnClickOn(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain() {
    }

    fun onClickOnGlobalFABsButton_1() {
        clearAchates(viewModelInitApp)
    }
     fun onClickOnGlobalFABsButton_2() {
        CreeDepuitAncienDataBases(
            viewModelInitApp._modelAppsFather,
            viewModelInitApp
        )
    }
}


