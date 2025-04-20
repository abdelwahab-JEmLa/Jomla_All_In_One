package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Produit.Companion.testproduit
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Vendeur.Companion.createVendeur
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


class _01_PeriodesVent : RealmObject {
    var dateDebutDeCettePeriode: String = "yyyy.MM.dd"
    var tempDebutDeCettePeriode: String = "HH:mm"

    @PrimaryKey
    var keyID: String =  "{PV}->dateDebutDeCettePeriode=HH:mm"

    var vendeurs: RealmList<Vendeur> = realmListOf()
    companion object {
        fun test_01_PeriodesVent(
            i: Int,
            testPeriodes: MutableList<_01_PeriodesVent>,
        ) {
            val date = "2025_04_${18 + i}"
            val time = "${10 + i}:00"
            val periodeKey = "{PV}->$date=$time"

            val periode = _01_PeriodesVent().apply {
                keyID = periodeKey
                dateDebutDeCettePeriode = date
                tempDebutDeCettePeriode = time
                vendeurs = realmListOf()
            }

            for (j in 1..2) {
                val vendeurId = j.toLong()
                val vendeurNom = "Vendeur $j"
                val vendeurKey = "$periodeKey-<{Ve}->($vendeurId=$vendeurNom)"
                val vendeur = createVendeur(vendeurId, vendeurNom, vendeurKey)
                periode.vendeurs.add(vendeur)
            }

            testPeriodes.add(periode)
        }
    }
}

class Vendeur : RealmObject {
    var idVendeur: Long = 0L
    var nomVendeur: String = ""

    @PrimaryKey
    var keyID: String = "_01_PeriodesVent.keyID-<{Ve}->(idVendeur=nomVendeur)"
    var produits: RealmList<Produit> = realmListOf()
    companion object{

        fun createVendeur(id: Long, nom: String, vendeurKey: String): Vendeur {
            return Vendeur().apply {
                keyID = vendeurKey
                idVendeur = id
                nomVendeur = nom
                produits = realmListOf()

                for (k in 1..5) {
                    val produitId = k.toLong()
                    val produitNom = "Produit $k"
                    val produitKey = "$keyID-<{Pr}->($produitId=$produitNom)"

                    val produit = testproduit(produitKey, produitId, produitNom, k)

                    produits.add(produit)
                }
            }
        }
    }
}

class Produit : RealmObject {
    var idProduit: Long = 0L
    var nomProduit: String = ""

    @PrimaryKey
    var keyID: String = "Vendeur.keyID-<{Pr}->(idProduit=nomProduit)"
    var quantity: Int = 0

    companion object{

        fun testproduit(
            produitKey: String,
            produitId: Long,
            produitNom: String,
            k: Int,
        ): Produit {
            val produit = Produit().apply {
                keyID = produitKey
                idProduit = produitId
                nomProduit = produitNom
                quantity = (k * 5)
            }
            return produit
        }
    }
}
