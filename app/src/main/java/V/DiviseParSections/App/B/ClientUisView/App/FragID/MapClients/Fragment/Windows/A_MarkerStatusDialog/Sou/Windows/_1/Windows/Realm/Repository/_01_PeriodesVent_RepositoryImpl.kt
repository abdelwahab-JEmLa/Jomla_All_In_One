package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class _01_PeriodesVent_RepositoryImpl : _01_PeriodesVent_Repository {
    override var modelDatasSnapList: SnapshotStateList<_01_PeriodesVent> = mutableStateListOf()

    // Initialize Realm with proper configuration
    private var realm: Realm = Realm.open(
        RealmConfiguration.create(
            schema = setOf(
                _01_PeriodesVent::class,
                Vendeur::class,
                Produit::class
            )
        )
    )

    private val _progressRepo = MutableStateFlow(0f)
    override val progressRepo: StateFlow<Float> = _progressRepo
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Firebase reference
    private val firebaseRef = _01_PeriodesVent_Repository.sonDataBaseRef

    init {
        loadFromRealmTOmodelDatasSnapList()
        loadFromFirebase()
    }

    private fun loadFromRealmTOmodelDatasSnapList() {
        val allPeriodes = realm.query<_01_PeriodesVent>()
            .sort("dateDebutDeCettePeriode", Sort.DESCENDING)
            .find()

        modelDatasSnapList.clear()
        modelDatasSnapList.addAll(allPeriodes)

        _progressRepo.value = 1.0f
    }

    private fun updateRealmAndFirebase() {
        updateRealm()
        updateFirebase()
    }

    // Update Realm database with current modelDatasSnapList data
    private fun updateRealm() {
        coroutineScope.launch {
            realm.write {
                query<_01_PeriodesVent>().find().forEach { delete(it) }

                // Add all current items from the list
                modelDatasSnapList.forEach { periode ->
                    copyToRealm(createDeepCopyForRealm(periode))
                }
            }
        }
    }

    // Update Firebase with current modelDatasSnapList data
    private fun updateFirebase() {
        coroutineScope.launch {
            // Convert to a Map structure that Firebase can store
            val dataToUpdate = modelDatasSnapList.associate { periode ->
                periode.keyID to mapOf(
                    "dateDebutDeCettePeriode" to periode.dateDebutDeCettePeriode,
                    "tempDebutDeCettePeriode" to periode.tempDebutDeCettePeriode,
                    "vendeurs" to periode.vendeurs.associate { vendeur ->
                        vendeur.keyID to mapOf(
                            "startIndex" to vendeur.startIndex,
                            "nom" to vendeur.nom,
                            "produits" to vendeur.produits.associate { produit ->
                                produit.keyID to mapOf(
                                    "startIndex" to produit.startIndex,
                                    "nom" to produit.nom,
                                    "quantity" to produit.quantity
                                )
                            }
                        )
                    }
                )
            }

            // Update Firebase
            firebaseRef.setValue(dataToUpdate)
        }
    }

    // Load data from Firebase
    private fun loadFromFirebase() {
        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val periodesList = mutableListOf<_01_PeriodesVent>()

                for (periodeSnapshot in snapshot.children) {
                    val periode = _01_PeriodesVent().apply {
                        keyID = periodeSnapshot.key ?: ""
                        dateDebutDeCettePeriode = periodeSnapshot.child("dateDebutDeCettePeriode").getValue(String::class.java) ?: "yyyy_MM_dd"
                        tempDebutDeCettePeriode = periodeSnapshot.child("tempDebutDeCettePeriode").getValue(String::class.java) ?: "HH:mm"
                    }

                    // Load vendeurs
                    val vendeursSnapshot = periodeSnapshot.child("vendeurs")
                    for (vendeurSnapshot in vendeursSnapshot.children) {
                        val vendeur = Vendeur().apply {
                            keyID = vendeurSnapshot.key ?: ""
                            startIndex = vendeurSnapshot.child("startIndex").getValue(Int::class.java) ?: 0
                            nom = vendeurSnapshot.child("nom").getValue(String::class.java) ?: ""
                        }

                        // Load produits
                        val produitsSnapshot = vendeurSnapshot.child("produits")
                        for (produitSnapshot in produitsSnapshot.children) {
                            val produit = Produit().apply {
                                keyID = produitSnapshot.key ?: ""
                                startIndex = produitSnapshot.child("startIndex").getValue(Int::class.java) ?: 0
                                nom = produitSnapshot.child("nom").getValue(String::class.java) ?: ""
                                quantity = produitSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                            }
                            vendeur.produits.add(produit)
                        }

                        periode.vendeurs.add(vendeur)
                    }

                    periodesList.add(periode)
                }

                // Update local data
                coroutineScope.launch(Dispatchers.Main) {
                    modelDatasSnapList.clear()
                    modelDatasSnapList.addAll(periodesList)

                    // Also update Realm
                    updateRealm()
                }

                _progressRepo.value = 1.0f
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                _progressRepo.value = 1.0f
            }
        })
    }



    // Helper function to create deep copies for Realm
    private fun createDeepCopyForRealm(source: _01_PeriodesVent): _01_PeriodesVent {
        val copy = _01_PeriodesVent().apply {
            keyID = source.keyID
            dateDebutDeCettePeriode = source.dateDebutDeCettePeriode
            tempDebutDeCettePeriode = source.tempDebutDeCettePeriode
            vendeurs = realmListOf()
        }

        // Copy vendeurs
        source.vendeurs.forEach { sourceVendeur ->
            val vendeurCopy = Vendeur().apply {
                keyID = sourceVendeur.keyID
                startIndex = sourceVendeur.startIndex
                nom = sourceVendeur.nom
                produits = realmListOf()
            }

            // Copy produits for each vendeur
            sourceVendeur.produits.forEach { sourceProduit ->
                val produitCopy = Produit().apply {
                    keyID = sourceProduit.keyID
                    startIndex = sourceProduit.startIndex
                    nom = sourceProduit.nom
                    quantity = sourceProduit.quantity
                }
                vendeurCopy.produits.add(produitCopy)
            }

            copy.vendeurs.add(vendeurCopy)
        }

        return copy
    }

    override suspend fun refreshData() {
        _progressRepo.value = 0f
        loadFromRealmTOmodelDatasSnapList()
    }

    // Methods to modify the modelDatasSnapList that ensure synchronization
    fun addPeriodeVente(periode: _01_PeriodesVent) {
        modelDatasSnapList.add(periode)
        updateRealmAndFirebase()
    }

    fun updatePeriodeVente(index: Int, periode: _01_PeriodesVent) {
        if (index in 0 until modelDatasSnapList.size) {
            modelDatasSnapList[index] = periode
            updateRealmAndFirebase()
        }
    }

    fun removePeriodeVente(index: Int) {
        if (index in 0 until modelDatasSnapList.size) {
            modelDatasSnapList.removeAt(index)
            updateRealmAndFirebase()
        }
    }

    // Cleanup method to close Realm when repository is no longer needed
    fun cleanup() {
        realm.close()
    }
}
