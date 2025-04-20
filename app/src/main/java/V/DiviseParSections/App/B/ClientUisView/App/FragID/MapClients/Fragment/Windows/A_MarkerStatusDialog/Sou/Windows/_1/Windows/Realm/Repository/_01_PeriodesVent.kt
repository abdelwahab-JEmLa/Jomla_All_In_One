package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Produit.Companion.mapProduits
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Produit.Companion.testproduit
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Vendeur.Companion.createVendeur
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Vendeur.Companion.mapVendeurs
import com.google.firebase.database.DataSnapshot
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
        /**
         * Creates test period data
         */
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

        /**
         * Converts periods to Firebase format
         */
        fun convertToFirebaseFormat(periodes: List<_01_PeriodesVent>): Map<String, Any> {
            return periodes.associate { periode ->
                val validPeriodeKey = periode.keyID
                validPeriodeKey to mapOf(
                    "dateDebutDeCettePeriode" to periode.dateDebutDeCettePeriode,
                    "tempDebutDeCettePeriode" to periode.tempDebutDeCettePeriode,
                    "vendeurs" to mapVendeurs(periode.vendeurs, validPeriodeKey)
                )
            }
        }

        /**
         * Parses periode data from Firebase snapshot
         */
        fun parsePeriodeFromSnapshot(periodeSnapshot: DataSnapshot): _01_PeriodesVent? {
            val periodeKey = periodeSnapshot.key ?: return null
            if (!periodeKey.startsWith("{PV}->")) return null

            val dateDebut = periodeSnapshot.child("dateDebutDeCettePeriode").getValue(String::class.java) ?: ""
            val tempDebut = periodeSnapshot.child("tempDebutDeCettePeriode").getValue(String::class.java) ?: ""

            val periode = _01_PeriodesVent().apply {
                keyID = periodeKey
                dateDebutDeCettePeriode = dateDebut
                tempDebutDeCettePeriode = tempDebut
                vendeurs = realmListOf()
            }

            // Parse vendeurs
            periodeSnapshot.child("vendeurs").children.forEach { vendeurSnapshot ->
                Vendeur.parseVendeurFromSnapshot(vendeurSnapshot)?.let {
                    periode.vendeurs.add(it)
                }
            }

            return periode
        }
    }
}

class Vendeur : RealmObject {
    var idVendeur: Long = 0L
    var nomVendeur: String = ""

    @PrimaryKey
    var keyID: String = "_01_PeriodesVent.keyID-<{Ve}->(idVendeur=nomVendeur)"
    var produits: RealmList<Produit> = realmListOf()

    companion object {
        /**
         * Creates a vendeur with test products
         */
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

        /**
         * Maps vendeurs to Firebase format
         */
        fun mapVendeurs(vendeurs: List<Vendeur>, periodeKey: String): Map<String, Any> {
            return vendeurs.associate { vendeur ->
                val validVendeurKey = vendeur.keyID

                validVendeurKey to mapOf(
                    "idVendeur" to vendeur.idVendeur,
                    "nomVendeur" to vendeur.nomVendeur,
                    "produits" to mapProduits(vendeur.produits, validVendeurKey)
                )
            }
        }

        /**
         * Parses vendeur data from Firebase snapshot
         */
        fun parseVendeurFromSnapshot(vendeurSnapshot: DataSnapshot): Vendeur? {
            val vendeurKey = vendeurSnapshot.key ?: return null
            if (!vendeurKey.contains("<{Ve}->")) return null

            val vendeurId = vendeurSnapshot.child("idVendeur").getValue(Long::class.java) ?: 0L
            val vendeurNom = vendeurSnapshot.child("nomVendeur").getValue(String::class.java) ?: ""

            val vendeur = Vendeur().apply {
                keyID = vendeurKey
                idVendeur = vendeurId
                nomVendeur = vendeurNom
                produits = realmListOf()
            }

            // Parse produits
            vendeurSnapshot.child("produits").children.forEach { produitSnapshot ->
                Produit.parseProduitFromSnapshot(produitSnapshot)?.let {
                    vendeur.produits.add(it)
                }
            }

            return vendeur
        }
    }
}

class Produit : RealmObject {
    var idProduit: Long = 0L
    var nomProduit: String = ""

    @PrimaryKey
    var keyID: String = "Vendeur.keyID-<{Pr}->(idProduit=nomProduit)"
    var quantity: Int = 0

    companion object {
        /**
         * Creates a test product
         */
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

        /**
         * Maps products to Firebase format
         */
        fun mapProduits(produits: List<Produit>, vendeurKey: String): Map<String, Any> {
            return produits.associate { produit ->
                val validProduitKey = produit.keyID

                validProduitKey to mapOf(
                    "idProduit" to produit.idProduit,
                    "nomProduit" to produit.nomProduit,
                    "quantity" to produit.quantity
                )
            }
        }

        /**
         * Parses product data from Firebase snapshot
         */
        fun parseProduitFromSnapshot(produitSnapshot: DataSnapshot): Produit? {
            val produitKey = produitSnapshot.key ?: return null
            if (!produitKey.contains("<{Pr}->")) return null

            val idProduit = produitSnapshot.child("idProduit").getValue(Long::class.java) ?: 0L
            val nomProduit = produitSnapshot.child("nomProduit").getValue(String::class.java) ?: ""
            val quantity = produitSnapshot.child("quantity").getValue(Int::class.java) ?: 0

            return Produit().apply {
                keyID = produitKey
                this.idProduit = idProduit
                this.nomProduit = nomProduit
                this.quantity = quantity
            }
        }
    }
}
