package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._012_ComptsVendeurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._013_Acheteurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._014_Produits
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase.Companion.convertToFirebaseFormat
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase.Companion.parsePeriodeFromSnapshot
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase.Companion.test_01_PeriodesVent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
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

class _01_VentsHistoriquesDataBase_RepositoryImpl(itsProductionMode :Boolean = false)
    : _01_VentsHistoriquesDataBase_Repository {
    private val TAG = "_01_PeriodesVent_Repo"
    var idComptDeCeTelephone: String = ""

    override var modelDatasSnapList: SnapshotStateList<_01_VentsHistoriquesDataBase> = mutableStateListOf()

    private val _01_HeadRef = Firebase.database.getReference("01_DataPrototype-04-19")
    private val _1_developingRef = _01_HeadRef.child("_1_developingRef")
    private val _2_productionTestRef = _01_HeadRef.child("_2_productionTestRef")
    private val firebaseRef = if (!itsProductionMode)
        _1_developingRef else  _2_productionTestRef
        .child("_01_VentsHistoriquesDataBase")

    private val _progressRepo = MutableStateFlow(0f)
    override val progressRepo: StateFlow<Float> = _progressRepo

    private val realm: Realm = createRealm()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val realmMutex = Mutex()
    private val pendingRealmUpdate = AtomicBoolean(false)
    private val modelUpdateInProgress = AtomicBoolean(false)
    private var valueEventListener: ValueEventListener? = null
    private var acheteursChangeListener: ValueEventListener? = null
    private val _dataChangedEvent = MutableStateFlow(0L)
    override val dataChangedEvent: StateFlow<Long> = _dataChangedEvent

    init {
        initializeData()
    }

    private fun createRealm(): Realm {
        val config = RealmConfiguration.create(
            schema = setOf(
                _01_VentsHistoriquesDataBase::class,
                _012_ComptsVendeurs::class,
                _013_Acheteurs::class,
                _014_Produits::class
            )
        )
        return Realm.open(config)
    }

    private fun initializeData() {
        val isRealmEmpty = realm.query<_01_VentsHistoriquesDataBase>().count().find() == 0L

        if (isRealmEmpty) {
            loadFromFirebase()
        } else {
            loadFromRealmTOmodelDatasSnapList()
            loadFromFirebase()
        }
    }

    override fun addTestVals() {
        val testPeriodes = createTestData()
        updateModelDatasList(testPeriodes)

        val dataToUpdate = convertToFirebaseFormat(testPeriodes)
        firebaseRef.setValue(dataToUpdate).addOnCompleteListener { task ->
            updateRealm()
            loadFromFirebase()
        }
    }

    private fun createTestData(): List<_01_VentsHistoriquesDataBase> {
        val testPeriodes = mutableListOf<_01_VentsHistoriquesDataBase>()

        for (i in 1..3) {
            test_01_PeriodesVent(i, testPeriodes)
        }

        return testPeriodes
    }

    private fun updateFirebase() {
        coroutineScope.launch {
            try {
                val dataToUpdate = convertToFirebaseFormat(modelDatasSnapList)
                removeFirebaseListener()
                firebaseRef.setValue(dataToUpdate).addOnCompleteListener { task ->
                    attachFirebaseListener()
                }
            } catch (e: Exception) {
                // Error handling would go here
            }
        }
    }

    private fun attachFirebaseListener() {
        valueEventListener = createFirebaseValueEventListener()
        try {
            firebaseRef.addValueEventListener(valueEventListener!!)
        } catch (e: Exception) {
            _progressRepo.value = 1.0f
        }
    }

    private fun createFirebaseValueEventListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val parsedData = parseFirebaseSnapshot(snapshot)
                    updateModelDatasList(parsedData)
                    scheduleSafeRealmUpdate()
                    _progressRepo.value = 1.0f
                    _dataChangedEvent.value = System.currentTimeMillis()
                } catch (e: Exception) {
                    _progressRepo.value = 1.0f
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _progressRepo.value = 1.0f
            }
        }
    }

    private fun parseFirebaseSnapshot(snapshot: DataSnapshot): MutableList<_01_VentsHistoriquesDataBase> {
        val newPeriodesVente = mutableListOf<_01_VentsHistoriquesDataBase>()

        snapshot.children.forEach { periodeSnapshot ->
            parsePeriodeFromSnapshot(periodeSnapshot)?.let {
                newPeriodesVente.add(it)
            }
        }

        return newPeriodesVente
    }



    private fun loadFromRealmTOmodelDatasSnapList() {
        val allPeriodes = realm.query<_01_VentsHistoriquesDataBase>()
            .sort("dateDebutDeCettePeriode", Sort.DESCENDING)
            .find()

        updateModelDatasList(allPeriodes)
        _progressRepo.value = 1.0f
    }

    private fun updateModelDatasList(periodes: List<_01_VentsHistoriquesDataBase>) {
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
                        query<_01_VentsHistoriquesDataBase>().find().also { delete(it) }
                        query<_012_ComptsVendeurs>().find().also { delete(it) }
                        query<_013_Acheteurs>().find().also { delete(it) }
                        query<_014_Produits>().find().also { delete(it) }

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
            // Error handling would go here
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
                    // Error handling would go here
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Error handling would go here
            }
        }
    }

    private fun processAcheteursChanges(snapshot: DataSnapshot): Boolean {
        var changesMade = false

        snapshot.children.forEach { periodeSnapshot ->
            val periodeKey = periodeSnapshot.key ?: return@forEach

            periodeSnapshot.child("vendeurs").children.forEach { vendeurSnapshot ->
                val vendeurKey = vendeurSnapshot.key ?: return@forEach

                vendeurSnapshot.child("_013_Acheteurs").children.forEach { acheteurSnapshot ->
                    val acheteurKey = acheteurSnapshot.key ?: return@forEach

                    val acheteur = _013_Acheteurs.parse_13_AcheteursFromSnapshot(acheteurSnapshot) ?: return@forEach
                    val updated = updateAcheteurInModel(periodeKey, vendeurKey, acheteur)
                    if (updated) changesMade = true

                    // Process products for this acheteur
                    acheteurSnapshot.child(_013_Acheteurs.NomsValeursModel.child_15_Produits.name).children.forEach { produitSnapshot ->
                        val produit = _014_Produits.parseDataFromSnapshot(produitSnapshot) ?: return@forEach
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
        acheteur: _013_Acheteurs
    ): Boolean {
        if (modelUpdateInProgress.getAndSet(true)) return false

        try {
            val periode = modelDatasSnapList.find { it.keyID == periodeKey } ?: return false
            val vendeur = periode.child_012_Compts_Vendeurs.find { it.keyID == vendeurKey } ?: return false
            val existingAcheteur = vendeur.child_013_Acheteurs.find { it.keyID == acheteur.keyID }

            return if (existingAcheteur != null) {
                val changed = existingAcheteur.vid != acheteur.vid ||
                        existingAcheteur.startDesignation != acheteur.startDesignation ||
                        existingAcheteur.tempCreationString != acheteur.tempCreationString

                if (changed) {
                    existingAcheteur.vid = acheteur.vid
                    existingAcheteur.startDesignation = acheteur.startDesignation
                    existingAcheteur.tempCreationString = acheteur.tempCreationString
                    true
                } else {
                    false
                }
            } else {
                vendeur.child_013_Acheteurs.add(acheteur)
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
        produit: _014_Produits
    ): Boolean {
        if (modelUpdateInProgress.getAndSet(true)) return false

        try {
            val periode = modelDatasSnapList.find { it.keyID == periodeKey } ?: return false
            val vendeur = periode.child_012_Compts_Vendeurs.find { it.keyID == vendeurKey } ?: return false
            val acheteur = vendeur.child_013_Acheteurs.find { it.keyID == acheteurKey } ?: return false
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
                // Error handling would go here
            }
        }
    }

    private fun updateRealmSafely() {
        coroutineScope.launch {
            realmMutex.withLock {
                try {
                    realm.write {
                        // Clear existing data
                        query<_01_VentsHistoriquesDataBase>().find().also { delete(it) }
                        query<_012_ComptsVendeurs>().find().also { delete(it) }
                        query<_013_Acheteurs>().find().also { delete(it) }
                        query<_014_Produits>().find().also { delete(it) }

                        // Save current data
                        modelDatasSnapList.forEach { periode ->
                            copyToRealm(createDeepCopyForRealm(periode))
                        }
                    }
                } catch (e: Exception) {
                    // Error handling would go here
                }
            }
        }
    }

    private fun createDeepCopyForRealm(source: _01_VentsHistoriquesDataBase): _01_VentsHistoriquesDataBase {
        val copy = _01_VentsHistoriquesDataBase().apply {
            keyID = source.keyID
            dateDebutDeCettePeriode = source.dateDebutDeCettePeriode
            tempDebutDeCettePeriode = source.tempDebutDeCettePeriode
            child_012_Compts_Vendeurs = realmListOf()
        }

        source.child_012_Compts_Vendeurs.forEach { sourceVendeur ->
            val vendeurCopy = createVendeurCopy(sourceVendeur)
            copy.child_012_Compts_Vendeurs.add(vendeurCopy)
        }

        return copy
    }

    private fun createVendeurCopy(sourceVendeur: _012_ComptsVendeurs): _012_ComptsVendeurs {
        val vendeurCopy = _012_ComptsVendeurs().apply {
            keyID = sourceVendeur.keyID
            vid = sourceVendeur.vid
            startDesignation = sourceVendeur.startDesignation
            child_013_Acheteurs = realmListOf()
        }

        sourceVendeur.child_013_Acheteurs.forEach { sourceAcheteur ->
            val acheteurCopy = _013_Acheteurs().apply {
                keyID = sourceAcheteur.keyID
                vid = sourceAcheteur.vid
                startDesignation = sourceAcheteur.startDesignation
                tempCreationString = sourceAcheteur.tempCreationString
                child_14Produits = realmListOf()
            }

            sourceAcheteur.child_14Produits.forEach { sourceProduit ->
                acheteurCopy.child_14Produits.add(_014_Produits().apply {
                    keyID = sourceProduit.keyID
                    id = sourceProduit.id
                    startDesignation = sourceProduit.startDesignation
                    tempCreationString = sourceProduit.tempCreationString
                    quantity = sourceProduit.quantity
                })
            }

            vendeurCopy.child_013_Acheteurs.add(acheteurCopy)
        }

        return vendeurCopy
    }

    override fun notifierDataChange() {
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
                // Error handling would go here
            }
            valueEventListener = null
        }

        acheteursChangeListener?.let {
            try {
                firebaseRef.removeEventListener(it)
            } catch (e: Exception) {
                // Error handling would go here
            }
            acheteursChangeListener = null
        }
    }
}
