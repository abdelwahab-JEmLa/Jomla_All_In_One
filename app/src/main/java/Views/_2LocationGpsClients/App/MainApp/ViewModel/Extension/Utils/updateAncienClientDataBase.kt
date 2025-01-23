package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ClientsModel
import com.google.firebase.Firebase
import com.google.firebase.database.database

fun updateAncienClientDataBase(newClient: _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations) {
    Firebase.database
        .getReference(newClient.statueDeBase.caRefDonAncienDataBase)
        .child(newClient.id.toString()).setValue(
            ClientsModel(
                idClientsSu = newClient.id,
                nomClientsSu = newClient.nom
            )
        )
}
