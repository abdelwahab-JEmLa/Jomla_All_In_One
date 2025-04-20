package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._00_VentsHistoriquesDataBase
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._00_VentsHistoriquesDataBase.Companion.convertToFirebaseFormat
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._00_VentsHistoriquesDataBase.Companion.parsePeriodeFromSnapshot
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._00_VentsHistoriquesDataBase.Companion.test_01_PeriodesVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._12_Vendeur
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._13_Acheteurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._14_Produits
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

class _00VentsHistoriquesDataBase_RepositoryImpl : _00_VentsHistoriquesDataBase_Repository {
    private val TAG = "_01_PeriodesVent_Repo"

    override var modelDatasSnapList: SnapshotStateList<_00_VentsHistoriquesDataBase> = mutableStateListOf()

    var idComptDeCeTelephone: String = ""

    private val realm: Realm = createRealm()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val realmMutex = Mutex()
    private val pendingRealmUpdate = AtomicBoolean(false)
    private val modelUpdateInProgress = AtomicBoolean(false)
    private val firebaseRef = _00_VentsHistoriquesDataBase_Repository.sonDataBaseRef
    private var valueEventListener: ValueEventListener? = null
    private var acheteursChangeListener: ValueEventListener? = null

    private val _progressRepo = MutableStateFlow(0f)
    override val progressRepo: StateFlow<Float> = _progressRepo

    private val _dataChangedEvent = MutableStateFlow(0L)
    override val dataChangedEvent: StateFlow<Long> = _dataChangedEvent

    init {
        initializeData()
    }

    private fun createRealm(): Realm {
        val config = RealmConfiguration.create(
            schema = setOf(
                _00_VentsHistoriquesDataBase::class,
                _12_Vendeur::class,
                _13_Acheteurs::class,
                _14_Produits::class
            )
        )
        return Realm.open(config)
    }



    private fun checkFirebaseOrCreateTestData() {
        Log.d(TAG, "Checking if Firebase data exists...")
        firebaseRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                Log.d(TAG, "Firebase is empty, creating test data...")
                // Create test data first
                val testPeriodes = createTestData()
                Log.d(TAG, "Test data created: ${testPeriodes.size} periods")
                updateModelDatasList(testPeriodes)

                // Then explicitly update Firebase with this data
                Log.d(TAG, "Converting test data to Firebase format...")
                val dataToUpdate = convertToFirebaseFormat(testPeriodes)
                Log.d(TAG, "Updating Firebase with test data...")
                firebaseRef.setValue(dataToUpdate).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Firebase successfully updated with test data")
                    } else {
                        Log.e(TAG, "Failed to update Firebase: ${task.exception?.message}")
                    }
                    // Only after Firebase update completes, update the Realm database
                    Log.d(TAG, "Updating Realm database...")
                    updateRealm()
                    // Then attach listeners to monitor future changes
                    Log.d(TAG, "Setting up Firebase listeners...")
                    loadFromFirebase()
                }
            } else {
                Log.d(TAG, "Firebase has data (${snapshot.childrenCount} entries), loading existing data...")
                loadFromFirebase()
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to check Firebase data: ${exception.message}")
            // In case of network failure, still try to load what might be in Firebase
            Log.d(TAG, "Attempting to load from Firebase despite failure...")
            loadFromFirebase()
        }
    }

    private fun createTestData(): List<_00_VentsHistoriquesDataBase> {
        Log.d(TAG, "Creating test data...")
        val testPeriodes = mutableListOf<_00_VentsHistoriquesDataBase>()

        for (i in 1..3) {
            Log.d(TAG, "Creating test period $i")
            test_01_PeriodesVent(i, testPeriodes)
        }

        Log.d(TAG, "Test data creation complete: ${testPeriodes.size} periods with keys: ${testPeriodes.map { it.keyID }}")
        return testPeriodes
    }

    private fun updateFirebase() {
        coroutineScope.launch {
            try {
                Log.d(TAG, "Preparing to update Firebase...")
                val dataToUpdate = convertToFirebaseFormat(modelDatasSnapList)
                Log.d(TAG, "Firebase update data prepared, removing listeners...")
                removeFirebaseListener()
                Log.d(TAG, "Setting Firebase data...")
                firebaseRef.setValue(dataToUpdate).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Firebase update successful")
                    } else {
                        Log.e(TAG, "Firebase update failed: ${task.exception?.message}")
                    }
                    Log.d(TAG, "Re-attaching Firebase listeners...")
                    attachFirebaseListener()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during Firebase update: ${e.message}", e)
            }
        }
    }

    private fun attachFirebaseListener() {
        Log.d(TAG, "Creating and attaching main Firebase listener...")
        valueEventListener = createFirebaseValueEventListener()
        try {
            firebaseRef.addValueEventListener(valueEventListener!!)
            Log.d(TAG, "Main Firebase listener attached successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach main Firebase listener: ${e.message}", e)
            _progressRepo.value = 1.0f
        }
    }

    private fun createFirebaseValueEventListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "Firebase data changed, snapshot children count: ${snapshot.childrenCount}")
                    val parsedData = parseFirebaseSnapshot(snapshot)
                    Log.d(TAG, "Parsed ${parsedData.size} periods from Firebase")
                    updateModelDatasList(parsedData)
                    scheduleSafeRealmUpdate()
                    _progressRepo.value = 1.0f
                    _dataChangedEvent.value = System.currentTimeMillis()
                    Log.d(TAG, "Model data updated from Firebase successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing Firebase data change: ${e.message}", e)
                    _progressRepo.value = 1.0f
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase data change event cancelled: ${error.message}")
                _progressRepo.value = 1.0f
            }
        }
    }

    private fun parseFirebaseSnapshot(snapshot: DataSnapshot): MutableList<_00_VentsHistoriquesDataBase> {
        Log.d(TAG, "Parsing Firebase snapshot with ${snapshot.childrenCount} children")
        val newPeriodesVente = mutableListOf<_00_VentsHistoriquesDataBase>()

        snapshot.children.forEach { periodeSnapshot ->
            Log.d(TAG, "Parsing period with key: ${periodeSnapshot.key}")
            parsePeriodeFromSnapshot(periodeSnapshot)?.let {
                Log.d(TAG, "Successfully parsed period: ${it.keyID}")
                newPeriodesVente.add(it)
            } ?: Log.w(TAG, "Failed to parse period from snapshot: ${periodeSnapshot.key}")
        }

        Log.d(TAG, "Parsed ${newPeriodesVente.size} periods from Firebase")
        return newPeriodesVente
    }

    private fun initializeData() {
        Log.d(TAG, "Initializing data...")
        val isRealmEmpty = realm.query<_00_VentsHistoriquesDataBase>().count().find() == 0L
        Log.d(TAG, "Realm database is empty: $isRealmEmpty")

        if (isRealmEmpty) {
            Log.d(TAG, "Realm is empty, checking Firebase...")
            checkFirebaseOrCreateTestData()
        } else {
            Log.d(TAG, "Loading data from Realm to model...")
            loadFromRealmTOmodelDatasSnapList()
            Log.d(TAG, "Setting up Firebase listeners...")
            loadFromFirebase()
        }
    }

    private fun loadFromRealmTOmodelDatasSnapList() {
        val allPeriodes = realm.query<_00_VentsHistoriquesDataBase>()
            .sort("dateDebutDeCettePeriode", Sort.DESCENDING)
            .find()

        updateModelDatasList(allPeriodes)
        _progressRepo.value = 1.0f
    }

    private fun updateModelDatasList(periodes: List<_00_VentsHistoriquesDataBase>) {
        if (modelUpdateInProgress.getAndSet(true)) return

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

    private fun updateRealm() {
        coroutineScope.launch {
            realmMutex.withLock {
                try {
                    realm.write {
                        // Clear existing data
                        query<_00_VentsHistoriquesDataBase>().find().also { delete(it) }
                        query<_12_Vendeur>().find().also { delete(it) }
                        query<_13_Acheteurs>().find().also { delete(it) }
                        query<_14_Produits>().find().also { delete(it) }

                        // Save current data
                        modelDatasSnapList.forEach { periode ->
                            copyToRealm(createDeepCopyForRealm(periode))
                        }
                    }
                } catch (e: Exception) {
                    // Error handling would go here
                } finally {
                    _progressRepo.value = 1.0f
                }
            }
        }
    }


    private fun loadFromFirebase() {
        removeFirebaseListener()
        attachFirebaseListener()
        attachAcheteursChangeListener()
    }



    private fun attachAcheteursChangeListener() {
        acheteursChangeListener = createAcheteursChangeListener()
        try {
            firebaseRef.addValueEventListener(acheteursChangeListener!!)
        } catch (e: Exception) {
            // Log error if needed
        }
    }

    private fun createAcheteursChangeListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val changesMade = processAcheteursChanges(snapshot)
                    if (changesMade) {
                        scheduleSafeRealmUpdate()
                        notifyUIUpdate()
                        _dataChangedEvent.value = System.currentTimeMillis()
                    }
                } catch (e: Exception) {
                    // Log error if needed
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Log error if needed
            }
        }
    }

    private fun processAcheteursChanges(snapshot: DataSnapshot): Boolean {
        var changesMade = false

        snapshot.children.forEach { periodeSnapshot ->
            val periodeKey = periodeSnapshot.key ?: return@forEach

            periodeSnapshot.child("vendeurs").children.forEach { vendeurSnapshot ->
                val vendeurKey = vendeurSnapshot.key ?: return@forEach

                vendeurSnapshot.child("_13_Acheteurs").children.forEach { acheteurSnapshot ->
                    val acheteurKey = acheteurSnapshot.key ?: return@forEach

                    val acheteur = _13_Acheteurs.parse_13_AcheteursFromSnapshot(acheteurSnapshot) ?: return@forEach
                    val updated = updateAcheteurInModel(periodeKey, vendeurKey, acheteur)
                    if (updated) changesMade = true

                    // Process products for this acheteur
                    acheteurSnapshot.child(_13_Acheteurs.NomsValeursModel.child_15_Produits.name).children.forEach { produitSnapshot ->
                        val produit = _14_Produits.parseDataFromSnapshot(produitSnapshot) ?: return@forEach
                        val produitUpdated = updateProduitInAcheteur(periodeKey, vendeurKey, acheteurKey, produit)
                        if (produitUpdated) changesMade = true
                    }
                }
            }
        }

        return changesMade
    }

    private fun updateAcheteurInModel(
        periodeKey: String,
        vendeurKey: String,
        acheteur: _13_Acheteurs
    ): Boolean {
        if (modelUpdateInProgress.getAndSet(true)) return false

        try {
            val periode = modelDatasSnapList.find { it.keyID == periodeKey } ?: return false
            val vendeur = periode.vendeurs.find { it.keyID == vendeurKey } ?: return false
            val existingAcheteur = vendeur.acheteurs.find { it.keyID == acheteur.keyID }

            return if (existingAcheteur != null) {
                val changed = existingAcheteur.id != acheteur.id ||
                        existingAcheteur.startDesignation != acheteur.startDesignation ||
                        existingAcheteur.tempCreationString != acheteur.tempCreationString

                if (changed) {
                    existingAcheteur.id = acheteur.id
                    existingAcheteur.startDesignation = acheteur.startDesignation
                    existingAcheteur.tempCreationString = acheteur.tempCreationString
                    true
                } else {
                    false
                }
            } else {
                vendeur.acheteurs.add(acheteur)
                true
            }
        } finally {
            modelUpdateInProgress.set(false)
        }
    }

    private fun updateProduitInAcheteur(
        periodeKey: String,
        vendeurKey: String,
        acheteurKey: String,
        produit: _14_Produits
    ): Boolean {
        if (modelUpdateInProgress.getAndSet(true)) return false

        try {
            val periode = modelDatasSnapList.find { it.keyID == periodeKey } ?: return false
            val vendeur = periode.vendeurs.find { it.keyID == vendeurKey } ?: return false
            val acheteur = vendeur.acheteurs.find { it.keyID == acheteurKey } ?: return false
            val existingProduit = acheteur.child_14Produits.find { it.keyID == produit.keyID }

            return if (existingProduit != null) {
                val changed = existingProduit.id != produit.id ||
                        existingProduit.startDesignation != produit.startDesignation ||
                        existingProduit.quantity != produit.quantity

                if (changed) {
                    existingProduit.id = produit.id
                    existingProduit.startDesignation = produit.startDesignation
                    existingProduit.quantity = produit.quantity
                    true
                } else {
                    false
                }
            } else {
                acheteur.child_14Produits.add(produit)
                true
            }
        } finally {
            modelUpdateInProgress.set(false)
        }
    }

    private fun scheduleSafeRealmUpdate() {
        if (pendingRealmUpdate.compareAndSet(false, true)) {
            coroutineScope.launch {
                try {
                    kotlinx.coroutines.delay(100)
                    updateRealmSafely()
                } finally {
                    pendingRealmUpdate.set(false)
                }
            }
        }
    }

    private fun notifyUIUpdate() {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                if (modelUpdateInProgress.getAndSet(true)) return@launch

                try {
                    val currentList = modelDatasSnapList.toList()
                    modelDatasSnapList.clear()
                    modelDatasSnapList.addAll(currentList)
                } finally {
                    modelUpdateInProgress.set(false)
                }
            } catch (e: Exception) {
                // Log error if needed
            }
        }
    }


    private fun updateRealmSafely() {
        coroutineScope.launch {
            realmMutex.withLock {
                try {
                    realm.write {
                        // Clear existing data
                        query<_00_VentsHistoriquesDataBase>().find().also { delete(it) }
                        query<_12_Vendeur>().find().also { delete(it) }
                        query<_13_Acheteurs>().find().also { delete(it) }
                        query<_14_Produits>().find().also { delete(it) }

                        // Save current data
                        modelDatasSnapList.forEach { periode ->
                            copyToRealm(createDeepCopyForRealm(periode))
                        }
                    }
                } catch (e: Exception) {
                    // Log error if needed
                }
            }
        }
    }

    private fun createDeepCopyForRealm(source: _00_VentsHistoriquesDataBase): _00_VentsHistoriquesDataBase {
        val copy = _00_VentsHistoriquesDataBase().apply {
            keyID = source.keyID
            dateDebutDeCettePeriode = source.dateDebutDeCettePeriode
            tempDebutDeCettePeriode = source.tempDebutDeCettePeriode
            vendeurs = realmListOf()
        }

        source.vendeurs.forEach { sourceVendeur ->
            val vendeurCopy = createVendeurCopy(sourceVendeur)
            copy.vendeurs.add(vendeurCopy)
        }

        return copy
    }

    private fun createVendeurCopy(sourceVendeur: _12_Vendeur): _12_Vendeur {
        val vendeurCopy = _12_Vendeur().apply {
            keyID = sourceVendeur.keyID
            id = sourceVendeur.id
            startDesignation = sourceVendeur.startDesignation
            acheteurs = realmListOf()
        }

        sourceVendeur.acheteurs.forEach { sourceAcheteur ->
            val acheteurCopy = _13_Acheteurs().apply {
                keyID = sourceAcheteur.keyID
                id = sourceAcheteur.id
                startDesignation = sourceAcheteur.startDesignation
                tempCreationString = sourceAcheteur.tempCreationString
                child_14Produits = realmListOf()
            }

            sourceAcheteur.child_14Produits.forEach { sourceProduit ->
                acheteurCopy.child_14Produits.add(_14_Produits().apply {
                    keyID = sourceProduit.keyID
                    id = sourceProduit.id
                    startDesignation = sourceProduit.startDesignation
                    tempCreationString = sourceProduit.tempCreationString
                    quantity = sourceProduit.quantity
                })
            }

            vendeurCopy.acheteurs.add(acheteurCopy)
        }

        return vendeurCopy
    }

    override fun notifieDataChange() {
        scheduleSafeRealmUpdate()
        notifyUIUpdate()
        _dataChangedEvent.value = System.currentTimeMillis()
    }

    fun cleanup() {
        removeFirebaseListener()
        realm.close()
    }

    private fun removeFirebaseListener() {
        valueEventListener?.let {
            try {
                firebaseRef.removeEventListener(it)
            } catch (e: Exception) {
                // Log error if needed
            }
            valueEventListener = null
        }

        acheteursChangeListener?.let {
            try {
                firebaseRef.removeEventListener(it)
            } catch (e: Exception) {
                // Log error if needed
            }
            acheteursChangeListener = null
        }
    }
}
