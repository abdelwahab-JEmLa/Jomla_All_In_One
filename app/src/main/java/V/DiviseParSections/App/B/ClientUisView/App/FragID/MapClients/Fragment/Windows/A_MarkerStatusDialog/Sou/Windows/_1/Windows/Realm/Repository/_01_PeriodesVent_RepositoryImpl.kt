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

    // Keep reference to the listener
    private var valueEventListener: ValueEventListener? = null

    init {
        val isRealmEmpty = realm.query<_01_PeriodesVent>().count().find() == 0L

        if (isRealmEmpty) {
            // Check if Firebase is empty and create test data if needed
            firebaseRef.get().addOnSuccessListener { snapshot ->
                if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                    createTestDataIfEmpty()
                } else {
                    loadFromFirebase()
                }
            }.addOnFailureListener {
                // If Firebase check fails, load from Firebase anyway
                loadFromFirebase()
            }
        } else {
            // Realm has data, load it
            loadFromRealmTOmodelDatasSnapList()
            // Still attach Firebase listener to get updates
            loadFromFirebase()
        }
    }

    private fun createTestDataIfEmpty() {
        coroutineScope.launch {
            // Create sample data
            val testPeriode = _01_PeriodesVent().apply {
                keyID = "test_periode_${System.currentTimeMillis()}"
                dateDebutDeCettePeriode = "2025_04_19"
                tempDebutDeCettePeriode = "10:00"

                // Add a test vendeur
                val testVendeur = Vendeur().apply {
                    keyID = "test_vendeur_${System.currentTimeMillis()}"
                    startIndex = 0
                    nom = "Jean Dupont"

                    // Add test products
                    produits.add(Produit().apply {
                        keyID = "test_produit_1_${System.currentTimeMillis()}"
                        startIndex = 0
                        nom = "T-shirt"
                        quantity = 10
                    })

                    produits.add(Produit().apply {
                        keyID = "test_produit_2_${System.currentTimeMillis()}"
                        startIndex = 1
                        nom = "Pantalon"
                        quantity = 5
                    })
                }

                vendeurs.add(testVendeur)
            }

            modelDatasSnapList.add(testPeriode)

            // Update both Realm and Firebase
            updateRealmAndFirebase()
        }
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
                // Delete existing items
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

            // Update Firebase - temporarily remove listener to avoid duplicate updates
            removeFirebaseListener()
            firebaseRef.setValue(dataToUpdate).addOnCompleteListener {
                // Reattach listener after update is complete
                attachFirebaseListener()
            }
        }
    }

    private fun loadFromFirebase() {
        // Remove any existing listener before adding a new one
        removeFirebaseListener()
        attachFirebaseListener()
    }

    // 3. Modification de _01_PeriodesVent_RepositoryImpl.kt pour améliorer les mises à jour
    private fun attachFirebaseListener() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val periodesList = mutableListOf<_01_PeriodesVent>()

                for (periodeSnapshot in snapshot.children) {
                    // Code existant pour charger les données...
                }

                // Update local data on the main thread to ensure proper UI updates
                coroutineScope.launch(Dispatchers.Main) {
                    // Important: Clear and addAll in a single batched operation
                    modelDatasSnapList.clear()
                    modelDatasSnapList.addAll(periodesList)

                    // Notify that data has changed
                    _progressRepo.value = 0.5f  // Indicate that we're half done

                    // Also update Realm - but don't trigger another Firebase update
                    updateRealmSafely()

                    // Indicate that we're done
                    _progressRepo.value = 1.0f
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error on main thread
                coroutineScope.launch(Dispatchers.Main) {
                    _progressRepo.value = 1.0f
                }
            }
        }

        // Attach the listener
        firebaseRef.addValueEventListener(valueEventListener!!)
    }

    private fun removeFirebaseListener() {
        valueEventListener?.let {
            firebaseRef.removeEventListener(it)
            valueEventListener = null
        }
    }

    // Update only Realm without touching Firebase - with duplicate key handling
    private fun updateRealmSafely() {
        coroutineScope.launch {
            try {
                realm.write {
                    // First clear all existing objects to prevent primary key conflicts
                    query<_01_PeriodesVent>().find().forEach { delete(it) }
                    query<Vendeur>().find().forEach { delete(it) }
                    query<Produit>().find().forEach { delete(it) }

                    // Now safe to add all items from the list
                    modelDatasSnapList.forEach { periode ->
                        copyToRealm(createDeepCopyForRealm(periode))
                    }
                }
            } catch (e: Exception) {
                // Handle errors more gracefully - log or notify about the error
                e.printStackTrace()
            }
        }
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
        loadFromFirebase()
    }

    // Cleanup method to close Realm when repository is no longer needed
    fun cleanup() {
        removeFirebaseListener()
        realm.close()
    }
}
