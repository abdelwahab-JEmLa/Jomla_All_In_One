package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import android.util.Log
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

class _01_PeriodesVent_RepositoryImpl : _01_PeriodesVent_Repository {
    private val TAG = "_01_PeriodesVent_Repo" // Tag for logging

    override var modelDatasSnapList: SnapshotStateList<_01_PeriodesVent> = mutableStateListOf()
    var idComptDeCeTelephone: String = "2025_04_19->11:00->1(Vendeur 1)"

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

    // Add mutex for synchronizing Realm operations
    private val realmMutex = Mutex()

    // Flag to track if a batch update is needed
    private val pendingRealmUpdate = AtomicBoolean(false)

    // Flag to track if model update is in progress
    private val modelUpdateInProgress = AtomicBoolean(false)

    // Firebase reference
    private val firebaseRef = _01_PeriodesVent_Repository.sonDataBaseRef

    // Keep reference to the listener
    private var valueEventListener: ValueEventListener? = null
    private var productChangeListener: ValueEventListener? = null

    init {
        initializeData()
    }

    private fun initializeData() {
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
            // Create 3 periods with 2 vendors each, and 5 products per vendor
            val testPeriodes = mutableListOf<_01_PeriodesVent>()

            // Create 3 periods
            for (i in 1..3) {
                val date = "2025_04_${18 + i}"  // April 19-21, 2025
                val time = "${10 + i}:00"       // 11:00, 12:00, 13:00

                val periodeKey = "$date->$time"
                val periode = _01_PeriodesVent().apply {
                    keyID = periodeKey
                    dateDebutDeCettePeriode = date
                    tempDebutDeCettePeriode = time
                    vendeurs = realmListOf()
                }

                // Create 2 vendors per period
                for (j in 1..2) {
                    val vendeurId = j.toLong()
                    val vendeurNom = "Vendeur $j"
                    val vendeurKey = "${periodeKey}->${vendeurId}($vendeurNom)"

                    val vendeur = Vendeur().apply {
                        keyID = vendeurKey
                        id = vendeurId
                        nom = vendeurNom
                        produits = realmListOf()
                    }

                    // Create 5 products per vendor
                    for (k in 1..5) {
                        val produitId = k.toLong()
                        val produitNom = "Produit $k"
                        val produitKey = "${vendeurKey}->${produitId}($produitNom)"

                        val produit = Produit().apply {
                            keyID = produitKey
                            id = produitId
                            nom = produitNom
                            quantity = (k * 5)  // Different quantities for each product
                        }

                        vendeur.produits.add(produit)
                    }

                    periode.vendeurs.add(vendeur)
                }

                testPeriodes.add(periode)
            }

            // Add to modelDatasSnapList
            updateModelDatasList(testPeriodes)

            // Update both Realm and Firebase
            updateRealmAndFirebase()
        }
    }

    private fun loadFromRealmTOmodelDatasSnapList() {
        val allPeriodes = realm.query<_01_PeriodesVent>()
            .sort("dateDebutDeCettePeriode", Sort.DESCENDING)
            .find()

        updateModelDatasList(allPeriodes)
        _progressRepo.value = 1.0f
    }

    // Thread-safe update of modelDatasSnapList
    private fun updateModelDatasList(periodes: List<_01_PeriodesVent>) {
        if (modelUpdateInProgress.getAndSet(true)) {
            Log.d(TAG, "Model update already in progress, skipping")
            return
        }

        try {
            modelDatasSnapList.clear()
            modelDatasSnapList.addAll(periodes)
        } finally {
            modelUpdateInProgress.set(false)
        }
    }

    private fun updateRealmAndFirebase() {
        updateRealm()
        updateFirebase()
    }

    // Update Realm database with current modelDatasSnapList data
    private fun updateRealm() {
        coroutineScope.launch {
            realmMutex.withLock {
                Log.d(TAG, "Starting Realm update with lock acquired")
                try {
                    realm.write {
                        // Clear existing data
                        query<_01_PeriodesVent>().find().also { delete(it) }
                        query<Vendeur>().find().also { delete(it) }
                        query<Produit>().find().also { delete(it) }

                        // Create a defensive copy to avoid concurrent modification
                        val dataSnapshot = modelDatasSnapList.toList()

                        // Add all items from the list with guaranteed unique keys
                        dataSnapshot.forEach { periode ->
                            copyToRealm(createDeepCopyForRealm(periode))
                        }
                    }
                    Log.d(TAG, "Realm update completed successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating Realm: ${e.message}", e)
                } finally {
                    _progressRepo.value = 1.0f // Ensure progress completes even on error
                }
            }
        }
    }

    private fun updateFirebase() {
        coroutineScope.launch {
            try {
                // Create a defensive copy to avoid concurrent modification
                val dataSnapshot = modelDatasSnapList.toList()

                // Convert to a Map structure that Firebase can store
                val dataToUpdate = convertToFirebaseFormat(dataSnapshot)

                // Update Firebase - temporarily remove listener to avoid duplicate updates
                removeFirebaseListener()
                firebaseRef.setValue(dataToUpdate).addOnCompleteListener {
                    // Reattach listener after update is complete
                    attachFirebaseListener()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating Firebase: ${e.message}", e)
            }
        }
    }

    private fun convertToFirebaseFormat(periodes: List<_01_PeriodesVent>): Map<String, Any> {
        return periodes.associate { periode ->
            periode.keyID to mapOf(
                "dateDebutDeCettePeriode" to periode.dateDebutDeCettePeriode,
                "tempDebutDeCettePeriode" to periode.tempDebutDeCettePeriode,
                "vendeurs" to periode.vendeurs.associate { vendeur ->
                    vendeur.keyID to mapOf(
                        "id" to vendeur.id,
                        "nom" to vendeur.nom,
                        "produits" to vendeur.produits.associate { produit ->
                            produit.keyID to mapOf(
                                "id" to produit.id,
                                "nom" to produit.nom,
                                "quantity" to produit.quantity
                            )
                        }
                    )
                }
            )
        }
    }

    private fun loadFromFirebase() {
        // Remove any existing listener before adding a new one
        removeFirebaseListener()
        attachFirebaseListener()
        attachProductChangeListener()
    }

    private fun attachFirebaseListener() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    parseFirebaseSnapshot(snapshot)
                    scheduleSafeRealmUpdate()
                    _progressRepo.value = 1.0f
                } catch (e: Exception) {
                    Log.e(TAG, "Error in Firebase data change: ${e.message}", e)
                    _progressRepo.value = 1.0f
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase data change cancelled: ${error.message}")
                _progressRepo.value = 1.0f
            }
        }

        // Add try-catch to handle potential security exceptions
        try {
            firebaseRef.addValueEventListener(valueEventListener!!)
        } catch (e: Exception) {
            Log.e(TAG, "Error attaching Firebase listener: ${e.message}", e)
            _progressRepo.value = 1.0f
        }
    }

    // Listener specifically for product changes
    private fun attachProductChangeListener() {
        productChangeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Log product changes
                Log.d(TAG, "Product change detected in Firebase")

                try {
                    var changesMade = false

                    // Process only changes to products, identified by their path
                    snapshot.children.forEach { periodeSnapshot ->
                        val periodeKey = periodeSnapshot.key ?: return@forEach
                        Log.d(TAG, "Processing changes for period: $periodeKey")

                        periodeSnapshot.child("vendeurs").children.forEach { vendeurSnapshot ->
                            val vendeurKey = vendeurSnapshot.key ?: return@forEach
                            Log.d(TAG, "Processing changes for vendor: $vendeurKey")

                            vendeurSnapshot.child("produits").children.forEach { produitSnapshot ->
                                val produitKey = produitSnapshot.key ?: return@forEach
                                val quantity = produitSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                                val id = produitSnapshot.child("id").getValue(Long::class.java) ?: 0L
                                val nom = produitSnapshot.child("nom").getValue(String::class.java) ?: ""

                                Log.d(TAG, "Product change: $produitKey (ID: $id, Name: $nom, Quantity: $quantity)")

                                // Update the corresponding product in modelDatasSnapList
                                val updated = updateProductInModel(periodeKey, vendeurKey, produitKey, id, nom, quantity)
                                if (updated) changesMade = true
                            }
                        }
                    }

                    // Only schedule update if changes were actually made
                    if (changesMade) {
                        // Schedule a single Realm update after all changes are processed
                        scheduleSafeRealmUpdate()

                        // Trigger UI update
                        notifyUIUpdate()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing product changes: ${e.message}", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Product change listener cancelled: ${error.message}")
            }
        }

        try {
            firebaseRef.addValueEventListener(productChangeListener!!)
            Log.d(TAG, "Product change listener attached successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error attaching product change listener: ${e.message}", e)
        }
    }

    // Schedule a Realm update safely to avoid concurrent transactions
    private fun scheduleSafeRealmUpdate() {
        if (pendingRealmUpdate.compareAndSet(false, true)) {
            coroutineScope.launch {
                try {
                    // Small delay to batch potential updates
                    kotlinx.coroutines.delay(100)
                    updateRealmSafely()
                } finally {
                    pendingRealmUpdate.set(false)
                }
            }
        } else {
            Log.d(TAG, "Realm update already scheduled, skipping")
        }
    }

    private fun updateProductInModel(
        periodeKey: String,
        vendeurKey: String,
        produitKey: String,
        id: Long,
        nom: String,
        quantity: Int
    ): Boolean {
        // Synchronized to prevent concurrent modification
        if (modelUpdateInProgress.getAndSet(true)) {
            Log.d(TAG, "Model update already in progress, skipping product update")
            return false
        }

        try {
            // Find periode
            val periode = modelDatasSnapList.find { it.keyID == periodeKey }
            if (periode == null) {
                Log.w(TAG, "Period not found for key: $periodeKey")
                return false
            }

            // Find vendeur
            val vendeur = periode.vendeurs.find { it.keyID == vendeurKey }
            if (vendeur == null) {
                Log.w(TAG, "Vendor not found for key: $vendeurKey")
                return false
            }

            // Find produit
            val produit = vendeur.produits.find { it.keyID == produitKey }

            if (produit != null) {
                // Only update if there's an actual change
                val changed = produit.quantity != quantity ||
                        produit.id != id ||
                        produit.nom != nom

                if (changed) {
                    // Update existing product
                    Log.d(TAG, "Updating existing product: $produitKey, Quantity: ${produit.quantity} -> $quantity")
                    produit.quantity = quantity
                    produit.id = id
                    produit.nom = nom
                    return true
                }
                return false
            } else {
                // Create new product if not found
                Log.d(TAG, "Creating new product: $produitKey with quantity: $quantity")
                vendeur.produits.add(Produit().apply {
                    keyID = produitKey
                    this.id = id
                    this.nom = nom
                    this.quantity = quantity
                })
                return true
            }
        } finally {
            modelUpdateInProgress.set(false)
        }
    }

    // Trigger UI update without Realm update
    private fun notifyUIUpdate() {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                if (modelUpdateInProgress.getAndSet(true)) {
                    Log.d(TAG, "Model update already in progress, skipping UI notification")
                    return@launch
                }

                try {
                    // Force UI update by creating a new snapshot
                    val currentList = modelDatasSnapList.toList()
                    modelDatasSnapList.clear()
                    modelDatasSnapList.addAll(currentList)
                    Log.d(TAG, "UI update notification sent")
                } finally {
                    modelUpdateInProgress.set(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in UI notification: ${e.message}", e)
            }
        }
    }

    private fun parseFirebaseSnapshot(snapshot: DataSnapshot) {
        val newPeriodesVente = mutableListOf<_01_PeriodesVent>()

        snapshot.children.forEach { periodeSnapshot ->
            val periodeKey = periodeSnapshot.key ?: return@forEach
            val dateDebut = periodeSnapshot.child("dateDebutDeCettePeriode").getValue(String::class.java) ?: ""
            val tempDebut = periodeSnapshot.child("tempDebutDeCettePeriode").getValue(String::class.java) ?: ""

            val periode = _01_PeriodesVent().apply {
                keyID = periodeKey
                dateDebutDeCettePeriode = dateDebut
                tempDebutDeCettePeriode = tempDebut
                vendeurs = realmListOf()
            }

            periodeSnapshot.child("vendeurs").children.forEach { vendeurSnapshot ->
                val vendeurKey = vendeurSnapshot.key ?: return@forEach
                val vendeurId = vendeurSnapshot.child("id").getValue(Long::class.java) ?: 0L
                val vendeurNom = vendeurSnapshot.child("nom").getValue(String::class.java) ?: ""

                val vendeur = Vendeur().apply {
                    keyID = vendeurKey
                    id = vendeurId
                    nom = vendeurNom
                    produits = realmListOf()
                }

                vendeurSnapshot.child("produits").children.forEach { produitSnapshot ->
                    val produitKey = produitSnapshot.key ?: return@forEach
                    val produitId = produitSnapshot.child("id").getValue(Long::class.java) ?: 0L
                    val produitNom = produitSnapshot.child("nom").getValue(String::class.java) ?: ""
                    val quantity = produitSnapshot.child("quantity").getValue(Int::class.java) ?: 0

                    vendeur.produits.add(Produit().apply {
                        keyID = produitKey
                        id = produitId
                        nom = produitNom
                        this.quantity = quantity
                    })
                }

                periode.vendeurs.add(vendeur)
            }

            newPeriodesVente.add(periode)
        }

        // Update the model
        updateModelDatasList(newPeriodesVente)
    }

    private fun removeFirebaseListener() {
        valueEventListener?.let {
            try {
                firebaseRef.removeEventListener(it)
                Log.d(TAG, "Main Firebase listener removed")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing main Firebase listener: ${e.message}", e)
            }
            valueEventListener = null
        }

        productChangeListener?.let {
            try {
                firebaseRef.removeEventListener(it)
                Log.d(TAG, "Product change listener removed")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing product change listener: ${e.message}", e)
            }
            productChangeListener = null
        }
    }

    // Update only Realm without touching Firebase - with duplicate key handling
    private fun updateRealmSafely() {
        coroutineScope.launch {
            realmMutex.withLock {
                try {
                    // Create a defensive copy to avoid concurrent modification
                    val dataSnapshot = modelDatasSnapList.toList()

                    realm.write {
                        // Clear existing data
                        query<_01_PeriodesVent>().find().also { delete(it) }
                        query<Vendeur>().find().also { delete(it) }
                        query<Produit>().find().also { delete(it) }

                        // Now add all items from the list with guaranteed unique keys
                        dataSnapshot.forEach { periode ->
                            copyToRealm(createDeepCopyForRealm(periode))
                        }
                    }
                    Log.d(TAG, "Realm database updated successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating Realm safely: ${e.message}", e)
                }
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

        // Copy vendeurs with their existing keys (which should already be unique)
        source.vendeurs.forEach { sourceVendeur ->
            val vendeurCopy = Vendeur().apply {
                keyID = sourceVendeur.keyID
                id = sourceVendeur.id
                nom = sourceVendeur.nom
                produits = realmListOf()
            }

            // Copy produits with their existing keys (which should already be unique)
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
        Log.d(TAG, "Refreshing data from Realm and Firebase")
        loadFromRealmTOmodelDatasSnapList()
        loadFromFirebase()
    }

    override fun notifieDataChange() {
        Log.d(TAG, "Notifying data change")
        // Only schedule Realm update
        scheduleSafeRealmUpdate()
        // Trigger UI update
        notifyUIUpdate()
    }

    // Cleanup method to close Realm when repository is no longer needed
    fun cleanup() {
        Log.d(TAG, "Repository cleanup - removing listeners and closing Realm")
        removeFirebaseListener()
        realm.close()
    }
}
