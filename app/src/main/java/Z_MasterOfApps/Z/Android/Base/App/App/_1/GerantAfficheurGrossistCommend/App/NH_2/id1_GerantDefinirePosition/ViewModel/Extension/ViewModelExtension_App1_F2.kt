package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model.C_GrossistsDataBase.Companion.updateGrossistDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Frag2_A1_ExtVM(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<ProduitModel>,
) {
    val produitsAChoisireLeurClient = viewModel
        ._paramatersAppsViewModelModel.produitsAChoisireLeurClient

    private val grossistsDataBase = viewModel._modelAppsFather.grossistsDataBase

    var idAuFilter by mutableStateOf<Long?>(0)

    fun upButton(index: Int) {
        // Ensure index is valid and there's a previous element
        if (index <= 0 || index >= grossistsDataBase.size) {
            return
        }

        val currentElement = grossistsDataBase[index]
        val prev = grossistsDataBase[index - 1]

        // Swap their positions
        val currentPosition = currentElement.statueDeBase.itPositionInParentList
        val prevPosition = prev.statueDeBase.itPositionInParentList

        // Update positions
        currentElement.statueDeBase.itPositionInParentList = prevPosition
        prev.statueDeBase.itPositionInParentList = currentPosition

        // Update the list order
        grossistsDataBase[index] = prev
        grossistsDataBase[index - 1] = currentElement

        // Update both clients in the database
        currentElement.updateGrossistDataBase(viewModel)
        prev.updateGrossistDataBase(viewModel)
    }
}


