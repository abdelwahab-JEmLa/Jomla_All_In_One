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
    var idComptDeCeTelephone: String = "2->Jean Dupont"

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
            // Create first test period
            val testPeriode1 = _01_PeriodesVent().apply {
                keyID = "2025_04_19->10:00"
                dateDebutDeCettePeriode = "2025_04_19"
                tempDebutDeCettePeriode = "10:00"

                // Add a test vendeur
                val testVendeur1 = Vendeur().apply {
                    keyID = "1->Jean Dupont"
                    id = 1
                    nom = "Jean Dupont"

                    // Add test products
                    produits.add(Produit().apply {
                        keyID = "1->T-shirt"
                        id = 1
                        nom = "T-shirt"
                        quantity = 10
                    })

                    produits.add(Produit().apply {
                        keyID = "2->Pantalon"
                        id = 2
                        nom = "Pantalon"
                        quantity = 5
                    })
                }

                vendeurs.add(testVendeur1)

                // Add a second vendeur
                val testVendeur2 = Vendeur().apply {
                    keyID = "2->Marie Martin"
                    id = 2
                    nom = "Marie Martin"

                    // Add test products
                    produits.add(Produit().apply {
                        keyID = "3->Chaussures"
                        id = 3
                        nom = "Chaussures"
                        quantity = 8
                    })

                    produits.add(Produit().apply {
                        keyID = "4->Veste"
                        id = 4
                        nom = "Veste"
                        quantity = 3
                    })
                }

                vendeurs.add(testVendeur2)
            }

            // Create second test period
            val testPeriode2 = _01_PeriodesVent().apply {
                keyID = "2025_04_19->14:00"
                dateDebutDeCettePeriode = "2025_04_19"
                tempDebutDeCettePeriode = "14:00"

                // Add a test vendeur
                val testVendeur = Vendeur().apply {
                    keyID = "1->Jean Dupont"
                    id = 1
                    nom = "Jean Dupont"

                    // Add test products
                    produits.add(Produit().apply {
                        keyID = "1->T-shirt"
                        id = 1
                        nom = "T-shirt"
                        quantity = 7
                    })
                }

                vendeurs.add(testVendeur)
            }

            // Create third test period
            val testPeriode3 = _01_PeriodesVent().apply {
                keyID = "2025_04_20->09:00"
                dateDebutDeCettePeriode = "2025_04_20"
                tempDebutDeCettePeriode = "09:00"

                // Add a test vendeur
                val testVendeur = Vendeur().apply {
                    keyID = "2->Marie Martin"
                    id = 2
                    nom = "Marie Martin"

                    // Add test products
                    produits.add(Produit().apply {
                        keyID = "4->Veste"
                        id = 4
                        nom = "Veste"
                        quantity = 12
                    })
                }

                vendeurs.add(testVendeur)
            }

            // Add all periods to the list
            modelDatasSnapList.add(testPeriode1)
            modelDatasSnapList.add(testPeriode2)
            modelDatasSnapList.add(testPeriode3)

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

    private fun updateFirebase() {
        coroutineScope.launch {
            // Convert to a Map structure that Firebase can store
            val dataToUpdate = modelDatasSnapList.associate { periode ->
                periode.keyID to mapOf(
                    "dateDebutDeCettePeriode" to periode.dateDebutDeCettePeriode,
                    "tempDebutDeCettePeriode" to periode.tempDebutDeCettePeriode,
                    "vendeurs" to periode.vendeurs.associate { vendeur ->
                        vendeur.keyID to mapOf(
                            "startIndex" to vendeur.id,
                            "nom" to vendeur.nom,
                            "produits" to vendeur.produits.associate { produit ->
                                produit.keyID to mapOf(
                                    "startIndex" to produit.id, // Fixed: using id instead of undefined startIndex
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

    private fun attachFirebaseListener() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val periodesList = mutableListOf<_01_PeriodesVent>()

                for (periodeSnapshot in snapshot.children) {
                    val periode = _01_PeriodesVent().apply {
                        keyID = periodeSnapshot.key ?: ""
                        dateDebutDeCettePeriode = periodeSnapshot.child("dateDebutDeCettePeriode").getValue(String::class.java) ?: "yyyy_MM_dd"
                        tempDebutDeCettePeriode = periodeSnapshot.child("tempDebutDeCettePeriode").getValue(String::class.java) ?: "HH:mm"
                    }

                    // Variable to track if this periode includes changes to the current user's data
                    var shouldSkipUpdate = false

                    // Load vendeurs
                    val vendeursSnapshot = periodeSnapshot.child("vendeurs")
                    for (vendeurSnapshot in vendeursSnapshot.children) {
                        val vendeurKey = vendeurSnapshot.key ?: ""

                        // Check if this vendor is the current user's vendor
                        if (vendeurKey == idComptDeCeTelephone) {
                            // Skip updates for the current user's vendor to prevent listener conflicts
                            shouldSkipUpdate = true
                            continue
                        }

                        val vendeur = Vendeur().apply {
                            keyID = vendeurKey
                            id = vendeurSnapshot.child("id").getValue(Long::class.java) ?: 0
                            nom = vendeurSnapshot.child("nom").getValue(String::class.java) ?: ""
                        }

                        // Load produits
                        val produitsSnapshot = vendeurSnapshot.child("produits")
                        for (produitSnapshot in produitsSnapshot.children) {
                            val produit = Produit().apply {
                                keyID = produitSnapshot.key ?: ""
                                id = produitSnapshot.child("id").getValue(Long::class.java) ?: 0
                                nom = produitSnapshot.child("nom").getValue(String::class.java) ?: ""
                                quantity = produitSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                            }
                            vendeur.produits.add(produit)
                        }

                        periode.vendeurs.add(vendeur)
                    }

                    // Only add this period if it doesn't contain data from the current user
                    if (!shouldSkipUpdate) {
                        periodesList.add(periode)
                    }
                }

                // Only proceed with update if we have valid data and no conflicts
                if (periodesList.isNotEmpty()) {
                    // Update local data
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
                id = sourceVendeur.id
                nom = sourceVendeur.nom
                produits = realmListOf()
            }

            // Copy produits for each vendeur
            sourceVendeur.produits.forEach { sourceProduit ->
                val produitCopy = Produit().apply {
                    keyID = sourceProduit.keyID
                    id = sourceProduit.id
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
