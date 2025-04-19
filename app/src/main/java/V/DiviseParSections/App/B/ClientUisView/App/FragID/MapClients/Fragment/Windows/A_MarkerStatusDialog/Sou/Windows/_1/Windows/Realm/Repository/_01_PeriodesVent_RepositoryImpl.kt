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
            modelDatasSnapList.clear()
            modelDatasSnapList.addAll(testPeriodes)

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
            try {
                realm.write {

                    query<_01_PeriodesVent>().find().also { delete(it) }
                    query<Vendeur>().find().also { delete(it) }
                    query<Produit>().find().also { delete(it) }

                    // Now add all items from the list with guaranteed unique keys
                    modelDatasSnapList.forEach { periode ->
                        copyToRealm(createDeepCopyForRealm(periode))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _progressRepo.value = 1.0f // Ensure progress completes even on error
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
        // Fix the schema database connection
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val periodesList = mutableListOf<_01_PeriodesVent>()
                var hasChanges = false

                snapshot.children.forEach { periodeSnapshot ->
                    val periodeKey = periodeSnapshot.key ?: return@forEach

                    val periode = _01_PeriodesVent().apply {
                        keyID = periodeKey
                        dateDebutDeCettePeriode = periodeSnapshot.child("dateDebutDeCettePeriode").getValue(String::class.java) ?: "yyyy_MM_dd"
                        tempDebutDeCettePeriode = periodeSnapshot.child("tempDebutDeCettePeriode").getValue(String::class.java) ?: "HH:mm"
                    }

                    // Track if this period contains the current user's vendor
                    var containsCurrentUser = false

                    // Load vendeurs
                    val vendeursSnapshot = periodeSnapshot.child("vendeurs")
                    vendeursSnapshot.children.forEach { vendeurSnapshot ->
                        val vendeurKey = vendeurSnapshot.key ?: return@forEach

                        // Check if this is the current user's vendor
                        if (vendeurKey.contains(idComptDeCeTelephone)) {
                            containsCurrentUser = true
                            // Skip this vendeur to prevent listener conflicts
                            return@forEach
                        }

                        // Use the key format consistent with your schema
                        // The correct format should be "${periodeKey}->${vendeurId}(${vendeurNom})"
                        val vendeurId = vendeurSnapshot.child("id").getValue(Long::class.java) ?: 0
                        val vendeurNom = vendeurSnapshot.child("nom").getValue(String::class.java) ?: ""
                        val standardizedVendeurKey = "${periodeKey}->${vendeurId}(${vendeurNom})"

                        val vendeur = Vendeur().apply {
                            keyID = standardizedVendeurKey
                            id = vendeurId
                            nom = vendeurNom
                        }

                        // Load produits
                        val produitsSnapshot = vendeurSnapshot.child("produits")
                        produitsSnapshot.children.forEach { produitSnapshot ->
                            val produitKey = produitSnapshot.key ?: return@forEach

                            // Use the key format consistent with your schema
                            // The correct format should be "${vendeurKey}->${produitId}(${produitNom})"
                            val produitId = produitSnapshot.child("id").getValue(Long::class.java) ?: 0
                            val produitNom = produitSnapshot.child("nom").getValue(String::class.java) ?: ""
                            val standardizedProduitKey = "${standardizedVendeurKey}->${produitId}(${produitNom})"

                            val produit = Produit().apply {
                                keyID = standardizedProduitKey
                                id = produitId
                                nom = produitNom
                                quantity = produitSnapshot.child("quantity").getValue(Int::class.java)
                                    ?: produitSnapshot.child("quantity").getValue(Long::class.java)?.toInt()
                                            ?: 0
                            }
                            vendeur.produits.add(produit)
                        }

                        periode.vendeurs.add(vendeur)
                        hasChanges = true
                    }

                    // Only add periods that don't exclusively contain the current user
                    if (!containsCurrentUser || periode.vendeurs.isNotEmpty()) {
                        periodesList.add(periode)
                    }
                }

                // Only proceed with update if we have changes
                if (hasChanges) {
                    coroutineScope.launch(Dispatchers.Main) {
                        _progressRepo.value = 0.5f

                        // Safely update the modelDatasSnapList
                        if (periodesList.isNotEmpty()) {
                            modelDatasSnapList.clear()
                            modelDatasSnapList.addAll(periodesList)
                        }

                        // Update Realm without triggering Firebase updates
                        updateRealmSafely()
                        _progressRepo.value = 1.0f
                    }
                } else {
                    _progressRepo.value = 1.0f
                }
            }

            override fun onCancelled(error: DatabaseError) {
                coroutineScope.launch(Dispatchers.Main) {
                    _progressRepo.value = 1.0f
                }
            }
        }

        // Add try-catch to handle potential security exceptions
        try {
            firebaseRef.addValueEventListener(valueEventListener!!)
        } catch (e: Exception) {
            e.printStackTrace()
            _progressRepo.value = 1.0f
        }
    }

    private fun removeFirebaseListener() {
        valueEventListener?.let {
            try {
                firebaseRef.removeEventListener(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            valueEventListener = null
        }
    }

    // Update only Realm without touching Firebase - with duplicate key handling
    private fun updateRealmSafely() {
        coroutineScope.launch {
            try {
// In updateRealm() method
                realm.write {

                    query<_01_PeriodesVent>().find().also { delete(it) }
                    query<Vendeur>().find().also { delete(it) }
                    query<Produit>().find().also { delete(it) }

                    // Now add all items from the list with guaranteed unique keys
                    modelDatasSnapList.forEach { periode ->
                        copyToRealm(createDeepCopyForRealm(periode))
                    }
                }            } catch (e: Exception) {
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
        loadFromRealmTOmodelDatasSnapList()
        loadFromFirebase()
    }

    // Cleanup method to close Realm when repository is no longer needed
    fun cleanup() {
        removeFirebaseListener()
        realm.close()
    }
}
