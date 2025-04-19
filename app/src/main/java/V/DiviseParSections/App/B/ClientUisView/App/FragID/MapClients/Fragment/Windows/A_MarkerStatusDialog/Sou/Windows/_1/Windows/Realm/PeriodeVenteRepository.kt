package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class PeriodeVenteRepository(val realm: Realm) {
    // Create a new PeriodeVente
    suspend fun createPeriodeVente(
        dateDebut: String,
        tempDebut: String
    ): PeriodeVente {
        val periodeVente = PeriodeVente().apply {
            keyID = UUID.randomUUID().toString()
            dateDebutDeCettePeriode = dateDebut
            tempDebutDeCettePeriode = tempDebut
        }

        realm.write {
            copyToRealm(periodeVente)
        }

        return periodeVente
    }

    // Get a PeriodeVente by its ID
    suspend fun getPeriodeVenteById(id: String): PeriodeVente? {
        return realm.query<PeriodeVente>("keyID == $0", id).first().find()
    }

    // Get all PeriodeVente instances
    fun getAllPeriodeVentes(): Flow<List<PeriodeVente>> {
        return realm.query<PeriodeVente>()
            .sort("dateDebutDeCettePeriode", Sort.DESCENDING)
            .asFlow()
            .map { it.list }
    }

    // Update a PeriodeVente
    suspend fun updatePeriodeVente(periodeVente: PeriodeVente) {
        realm.write {
            findLatest(periodeVente)?.let { realmPeriodeVente ->
                realmPeriodeVente.dateDebutDeCettePeriode = periodeVente.dateDebutDeCettePeriode
                realmPeriodeVente.tempDebutDeCettePeriode = periodeVente.tempDebutDeCettePeriode
            }
        }
    }

    // Delete a PeriodeVente
    suspend fun deletePeriodeVente(id: String) {
        realm.write {
            val periodeToDelete = query<PeriodeVente>("keyID == $0", id).first().find()
            periodeToDelete?.let { delete(it) }
        }
    }

    // Add a Vendeur to a PeriodeVente
    suspend fun addVendeurToPeriodeVente(periodeVenteId: String, nom: String): Vendeur? {
        var vendeur: Vendeur? = null

        realm.write {
            // Fixed: Query properly within the write transaction
            val periodeVente = query<PeriodeVente>("keyID == $0", periodeVenteId).first().find()
            periodeVente?.let {
                val newVendeur = Vendeur().apply {
                    keyID = UUID.randomUUID().toString()
                    startIndex = it.vendeurs.size
                    this.nom = nom
                }
                it.vendeurs.add(newVendeur)
                vendeur = newVendeur
            }
        }

        return vendeur
    }

    // Add a Produit to a Vendeur
    suspend fun addProduitToVendeur(periodeVenteId: String, vendeurId: String, nomProduit: String, quantity: Int): Produit? {
        var produit: Produit? = null

        realm.write {
            // Fixed: Same issue here, query within the write transaction
            val periodeVente = query<PeriodeVente>("keyID == $0", periodeVenteId).first().find()
            periodeVente?.let { periode ->
                val vendeur = periode.vendeurs.find { it.keyID == vendeurId }
                vendeur?.let {
                    val newProduit = Produit().apply {
                        keyID = UUID.randomUUID().toString()
                        startIndex = it.produits.size
                        nom = nomProduit
                        this.quantity = quantity
                    }
                    it.produits.add(newProduit)
                    produit = newProduit
                }
            }
        }

        return produit
    }

    // Update Produit quantity
    suspend fun updateProduitQuantity(periodeVenteId: String, vendeurId: String, produitId: String, newQuantity: Int) {
        realm.write {
            // Fixed: Same issue here
            val periodeVente = query<PeriodeVente>("keyID == $0", periodeVenteId).first().find()
            periodeVente?.let { periode ->
                val vendeur = periode.vendeurs.find { it.keyID == vendeurId }
                vendeur?.let { v ->
                    val produit = v.produits.find { it.keyID == produitId }
                    produit?.quantity = newQuantity
                }
            }
        }
    }
}
