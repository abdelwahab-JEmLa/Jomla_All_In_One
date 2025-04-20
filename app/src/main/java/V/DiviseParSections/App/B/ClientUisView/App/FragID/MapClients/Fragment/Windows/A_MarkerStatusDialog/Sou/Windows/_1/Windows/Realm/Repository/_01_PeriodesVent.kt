package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


class _01_PeriodesVent : RealmObject {
    var dateDebutDeCettePeriode: String = "yyyy_MM_dd"
    var tempDebutDeCettePeriode: String = "HH:mm"

    @PrimaryKey
    var keyID: String =  "{PV}->dateDebutDeCettePeriode=HH:mm"

    var vendeurs: RealmList<Vendeur> = realmListOf()
}

class Vendeur : RealmObject {
    var idVendeur: Long = 0L
    var nomVendeur: String = ""

    @PrimaryKey
    var keyID: String = "_01_PeriodesVent.keyID-<{Ve}->(idVendeur=nomVendeur)"
    var produits: RealmList<Produit> = realmListOf()
}

class Produit : RealmObject {
    var idProduit: Long = 0L
    var nomProduit: String = ""

    @PrimaryKey
    var keyID: String = "Vendeur.keyID-<{Pr}->(idProduit=nomProduit)"
    var quantity: Int = 0
}
