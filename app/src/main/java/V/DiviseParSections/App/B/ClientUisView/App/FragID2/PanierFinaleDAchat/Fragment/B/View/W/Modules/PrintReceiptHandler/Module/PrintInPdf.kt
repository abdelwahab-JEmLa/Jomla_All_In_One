package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class PrintInPdfHandler() {

    val storageRef = Firebase.storage.reference.child("bonVents_pdf")
    val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/bonVents_pds"
                  //<--
                  //TODO(1): trouve moi une lib facile pour print le recipe et l enregeestre au storage et locale
}
