package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


class _01_PeriodesVent : RealmObject {
    @PrimaryKey
    var keyID: String = ""
    var dateDebutDeCettePeriode: String = "yyyy_MM_dd"
    var tempDebutDeCettePeriode: String = "HH:mm"
    var vendeurs: RealmList<Vendeur> = realmListOf()
}

class Vendeur : RealmObject {
    @PrimaryKey
    var keyID: String = ""
    var startIndex: Int = 0
    var nom: String = ""
    var produits: RealmList<Produit> = realmListOf()
}

class Produit : RealmObject {
    @PrimaryKey
    var keyID: String = ""
    var startIndex: Int = 0
    var nom: String = ""
    var quantity: Int = 0
}
