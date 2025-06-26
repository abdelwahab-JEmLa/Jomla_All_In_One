package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ExtensionVMApp1FragmentId_3(
    val viewModelInitApp: ViewModelInitApp,
) {
    private val clientDataBaseSnapList = viewModelInitApp._modelAppsFather.clientDataBase
    var iDClientAuFilter by mutableStateOf<Long?>(0)
    var clientFocused by mutableStateOf<Pair<B_ClientInfosProtoJuin3, List<A_ProduitModel>>?>(null)

}
