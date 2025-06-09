package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur.ViewModel

import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.D.Repository.B_ClientsDataBaseProtoD
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ExtensionVMApp1FragmentId_3(
    val viewModelInitApp: ViewModelInitApp,
) {
    private val clientDataBaseSnapList = viewModelInitApp._modelAppsFather.clientDataBase
    var iDClientAuFilter by mutableStateOf<Long?>(0)
    var clientFocused by mutableStateOf<Pair<B_ClientsDataBaseProtoD, List<A_ProduitModel>>?>(null)

    fun upButton(index: Int) {
        // Ensure index is valid and there's a previous element
        if (index <= 0 || index >= clientDataBaseSnapList.size) {
            return
        }

        // Get the current and previous clientAchteurs
        val currentClient = clientDataBaseSnapList[index]
        val prevClient = clientDataBaseSnapList[index - 1]

        // Swap their positions
        val currentPosition = currentClient.statueDeBase.positionDonClientsList
        val prevPosition = prevClient.statueDeBase.positionDonClientsList

        // Update positions
        currentClient.statueDeBase.positionDonClientsList = prevPosition
        prevClient.statueDeBase.positionDonClientsList = currentPosition

        // Update the list order
        clientDataBaseSnapList[index] = prevClient
        clientDataBaseSnapList[index - 1] = currentClient

        // Update both clientAchteurs in the database
        viewModelInitApp.updateClientsDataBase(currentClient)
        viewModelInitApp.updateClientsDataBase(prevClient)
    }
}
