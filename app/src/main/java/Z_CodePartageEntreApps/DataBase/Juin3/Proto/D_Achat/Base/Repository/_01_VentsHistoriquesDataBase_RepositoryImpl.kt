package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._012_ComptsVendeurs
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._013_ClientTransaction
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._015_Produits
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._01_PeriodVentHistorique
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._01_PeriodVentHistorique.Companion.map_01_VentsHistoriquesDataBase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._01_PeriodVentHistorique.Companion.parsePeriodeFromSnapshot
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._01_PeriodVentHistorique.Companion.test_01_PeriodesVent
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._14_TransactionStatue
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
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

class _01_VentsHistoriquesDataBase_RepositoryImpl(
    itsProductionMode: Boolean)
    : _01_VentsHistoriquesDataBase_Repository {

    private val TAG = "_01_PeriodesVent_Repo"
    var idComptDeCeTelephone: String = ""

    override var modelDatasSnapList: SnapshotStateList<_01_PeriodVentHistorique> = mutableStateListOf()

    private val _01_HeadRef = Firebase.database.getReference("01_DataPrototype-04-19")
    private val _1_developingRef = _01_HeadRef.child("_1_developingRef")
    private val _2_productionTestRef = _01_HeadRef.child("_2_productionTestRef")
    private val firebaseRef = if (!itsProductionMode)
        _1_developingRef else _2_productionTestRef
        .child("_01_PeriodVentHistorique")

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
                _01_PeriodVentHistorique::class,
                _012_ComptsVendeurs::class,
                _013_ClientTransaction::class,
                _015_Produits::class,
                _14_TransactionStatue::class
            )
        )
        return Realm.open(config)
    }

    private fun initializeData() {
        val isRealmEmpty = realm.query<_01_PeriodVentHistorique>().count().find() == 0L

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

        val dataToUpdate = map_01_VentsHistoriquesDataBase(testPeriodes)
        firebaseRef.setValue(dataToUpdate).addOnCompleteListener { task ->
            updateRealm()
            loadFromFirebase()
        }
    }


    override fun getClientTransactionsHistoriques(idClient: Long):
            List<Pair<_01_PeriodVentHistorique, List<_013_ClientTransaction>>> {
        val result = mutableListOf<Pair<_01_PeriodVentHistorique, List<_013_ClientTransaction>>>()

        for (period in modelDatasSnapList) {
            val clientTransactions = mutableListOf<_013_ClientTransaction>()

            // For each period, loop through all vendors
            for (vendeur in period.child_012_Compts_Vendeurs) {
                // For each vendor, find transactions of the specified client
                val transactions = vendeur.child_013_Acheteurs.filter {
                    it.idClient == idClient
                }

                // Add all matching transactions to our list
                clientTransactions.addAll(transactions)
            }

            // If we found any transactions for this client in this period
            if (clientTransactions.isNotEmpty()) {
                // Create add pair with the period and its matching transactions
                result.add(Pair(period, clientTransactions))
            }
        }

        return result
    }

    private fun createTestData(): List<_01_PeriodVentHistorique> {
        val testPeriodes = mutableListOf<_01_PeriodVentHistorique>()

        for (i in 1..3) {
            test_01_PeriodesVent(i, testPeriodes)
        }

        return testPeriodes
    }

    private fun updateFirebase() {
        coroutineScope.launch {
            try {
                val dataToUpdate = map_01_VentsHistoriquesDataBase(modelDatasSnapList)
                removeFirebaseListener()
                firebaseRef.setValue(dataToUpdate).addOnCompleteListener { task ->
                    attachFirebaseListener()
                }
            } catch (e: Exception) {
                _progressRepo.value = 1.0f
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

    private fun parseFirebaseSnapshot(snapshot: DataSnapshot): MutableList<_01_PeriodVentHistorique> {
        val newPeriodesVente = mutableListOf<_01_PeriodVentHistorique>()

        snapshot.children.forEach { periodeSnapshot ->
            parsePeriodeFromSnapshot(periodeSnapshot)?.let {
                newPeriodesVente.add(it)
            }
        }

        return newPeriodesVente
    }

    private fun loadFromRealmTOmodelDatasSnapList() {
        val allPeriodes = realm.query<_01_PeriodVentHistorique>()
            .sort("dateDebutDeCettePeriode", Sort.DESCENDING)
            .find()

        updateModelDatasList(allPeriodes)
        _progressRepo.value = 1.0f
    }

    private fun updateModelDatasList(periodes: List<_01_PeriodVentHistorique>) {
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
                        query<_01_PeriodVentHistorique>().find().also { delete(it) }
                        query<_012_ComptsVendeurs>().find().also { delete(it) }
                        query<_013_ClientTransaction>().find().also { delete(it) }
                        query<_015_Produits>().find().also { delete(it) }
                        query<_14_TransactionStatue>().find().also { delete(it) }

                        // Save current data
                        modelDatasSnapList.forEach { periode ->
                            copyToRealm(createDeepCopyForRealm(periode))
                        }
                    }
                } catch (e: Exception) {
                    _progressRepo.value = 1.0f
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
            // Exception handling without logging
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
                    // Exception handling without logging
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Error handling without logging
            }
        }
    }

    private fun processAcheteursChanges(snapshot: DataSnapshot): Boolean {
        var changesMade = false

        snapshot.children.forEach { periodeSnapshot ->
            val periodeKey = periodeSnapshot.key ?: return@forEach

            periodeSnapshot.child("vendeurs").children.forEach { vendeurSnapshot ->
                val vendeurKey = vendeurSnapshot.key ?: return@forEach

                vendeurSnapshot.child("_013_ClientTransaction").children.forEach { acheteurSnapshot ->
                    val acheteurKey = acheteurSnapshot.key ?: return@forEach

                    val acheteur = _013_ClientTransaction.parse_13_AcheteursFromSnapshot(acheteurSnapshot) ?: return@forEach
                    val updated = updateAcheteurInModel(periodeKey, vendeurKey, acheteur)
                    if (updated) changesMade = true

                    // Process products for this acheteur
                    acheteurSnapshot.child("child_15_Produits").children.forEach { produitSnapshot ->
                        val produit = _015_Produits.parseDataFromSnapshot(produitSnapshot) ?: return@forEach
                        val produitUpdated = updateProduitInAcheteur(periodeKey, vendeurKey, acheteurKey, produit)
                        if (produitUpdated) changesMade = true
                    }

                    // Process historiques for this acheteur
                    acheteurSnapshot.child("child_14A_HistoriquesDeCetteJour").children.forEach { historiqueSnapshot ->
                        val historique = _14_TransactionStatue.parseDataFromSnapshot(historiqueSnapshot) ?: return@forEach
                        val historiqueUpdated = updateHistoriqueInAcheteur(periodeKey, vendeurKey, acheteurKey, historique)
                        if (historiqueUpdated) changesMade = true
                    }
                }
            }
        }

        return changesMade
    }

    private fun updateAcheteurInModel(
        periodeKey: String,
        vendeurKey: String,
        acheteur: _013_ClientTransaction
    ): Boolean {
        if (modelUpdateInProgress.getAndSet(true)) return false

        try {
            val periode = modelDatasSnapList.find { it.fireBaseKeyID == periodeKey } ?: return false
            val vendeur = periode.child_012_Compts_Vendeurs.find { it.fireBaseKeyID == vendeurKey } ?: return false
            val existingAcheteur = vendeur.child_013_Acheteurs.find { it.fireBaseKeyID == acheteur.fireBaseKeyID }

            return if (existingAcheteur != null) {
                val changed = existingAcheteur.bsonObjectId != acheteur.bsonObjectId ||
                        existingAcheteur.startDesignation != acheteur.startDesignation ||
                        existingAcheteur.tempDateCreationStr != acheteur.tempDateCreationStr

                if (changed) {
                    existingAcheteur.bsonObjectId = acheteur.bsonObjectId
                    existingAcheteur.startDesignation = acheteur.startDesignation
                    existingAcheteur.tempDateCreationStr = acheteur.tempDateCreationStr
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
        produit: _015_Produits
    ): Boolean {
        if (modelUpdateInProgress.getAndSet(true)) return false

        try {
            val periode = modelDatasSnapList.find { it.fireBaseKeyID == periodeKey } ?: return false
            val vendeur = periode.child_012_Compts_Vendeurs.find { it.fireBaseKeyID == vendeurKey } ?: return false
            val acheteur = vendeur.child_013_Acheteurs.find { it.fireBaseKeyID == acheteurKey } ?: return false
            val existingProduit = acheteur.child_14Produits.find { it.fireBaseKeyID == produit.fireBaseKeyID }

            return if (existingProduit != null) {
                val changed = existingProduit.bsonObjectId != produit.bsonObjectId ||
                        existingProduit.startDesignation != produit.startDesignation ||
                        existingProduit.quantity != produit.quantity

                if (changed) {
                    existingProduit.bsonObjectId = produit.bsonObjectId
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

    private fun updateHistoriqueInAcheteur(
        periodeKey: String,
        vendeurKey: String,
        acheteurKey: String,
        historique: _14_TransactionStatue
    ): Boolean {
        if (modelUpdateInProgress.getAndSet(true)) return false

        try {
            val periode = modelDatasSnapList.find { it.fireBaseKeyID == periodeKey } ?: return false
            val vendeur = periode.child_012_Compts_Vendeurs.find { it.fireBaseKeyID == vendeurKey } ?: return false
            val acheteur = vendeur.child_013_Acheteurs.find { it.fireBaseKeyID == acheteurKey } ?: return false
            val existingHistorique = acheteur.child_14A_HistoriquesDeCetteJour.find { it.fireBaseKeyID == historique.fireBaseKeyID }

            return if (existingHistorique != null) {
                val changed = existingHistorique.bsonObjectId != historique.bsonObjectId ||
                        existingHistorique.etateTransactionName != historique.etateTransactionName ||
                        existingHistorique.description != historique.description ||
                        existingHistorique.dateCreationStr != historique.dateCreationStr ||
                        existingHistorique.tempCreationStr != historique.tempCreationStr

                if (changed) {
                    existingHistorique.bsonObjectId = historique.bsonObjectId
                    existingHistorique.etateTransactionName = historique.etateTransactionName
                    existingHistorique.description = historique.description
                    existingHistorique.dateCreationStr = historique.dateCreationStr
                    existingHistorique.tempCreationStr = historique.tempCreationStr
                    true
                } else {
                    false
                }
            } else {
                acheteur.child_14A_HistoriquesDeCetteJour.add(historique)
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
                // Exception handling without logging
            }
        }
    }

    private fun updateRealmSafely() {
        coroutineScope.launch {
            realmMutex.withLock {
                try {
                    realm.write {
                        // Clear existing data
                        query<_01_PeriodVentHistorique>().find().also { delete(it) }
                        query<_012_ComptsVendeurs>().find().also { delete(it) }
                        query<_013_ClientTransaction>().find().also { delete(it) }
                        query<_015_Produits>().find().also { delete(it) }
                        query<_14_TransactionStatue>().find().also { delete(it) }

                        // Save current data
                        modelDatasSnapList.forEach { periode ->
                            copyToRealm(createDeepCopyForRealm(periode))
                        }
                    }
                } catch (e: Exception) {
                    // Exception handling without logging
                }
            }
        }
    }

    private fun createDeepCopyForRealm(source: _01_PeriodVentHistorique): _01_PeriodVentHistorique {
        val copy = _01_PeriodVentHistorique.deepCopy(source)

        // Already handled in deepCopy, but keeping for explicitness and safety
        source.child_012_Compts_Vendeurs.forEach { sourceVendeur ->
            val vendeurCopy = _012_ComptsVendeurs.deepCopy(sourceVendeur)

            // Ensure deep copy of acheteurs and their historiques
            sourceVendeur.child_013_Acheteurs.forEach { sourceAcheteur ->
                val acheteurCopy = _013_ClientTransaction.deepCopy(sourceAcheteur)

                // Make sure historiques are properly copied
                sourceAcheteur.child_14A_HistoriquesDeCetteJour.forEach { sourceHistorique ->
                    acheteurCopy.child_14A_HistoriquesDeCetteJour.add(_14_TransactionStatue.deepCopy(sourceHistorique))
                }

                vendeurCopy.child_013_Acheteurs.add(acheteurCopy)
            }

            copy.child_012_Compts_Vendeurs.add(vendeurCopy)
        }

        return copy
    }

    override fun notifierDataChange() {
        try {
            // Update Firebase
            val dataToUpdate = map_01_VentsHistoriquesDataBase(modelDatasSnapList)

            // Remove listeners temporarily to avoid reentrant updates
            removeFirebaseListener()

            // Execute Firebase upsertLenceCommandeRepoGroupedProtoAvantJuin3
            firebaseRef.setValue(dataToUpdate)
                .addOnSuccessListener {
                    attachFirebaseListener() // Re-attach listeners
                }
                .addOnFailureListener {
                    attachFirebaseListener() // Re-attach listeners even on failure
                }

            // Also upsertLenceCommandeRepoGroupedProtoAvantJuin3 Realm database
            scheduleSafeRealmUpdate()
            notifyUIUpdate()
            _dataChangedEvent.value = System.currentTimeMillis()

        } catch (e: Exception) {
            // Exception handling without logging
        }
    }

    override fun upsert_01_PeriodesVentEtReturnItVid(
        period: _01_PeriodVentHistorique,
        onSuccess: (Long) -> Unit,
    ) {
        if (modelUpdateInProgress.getAndSet(true)) {
            return
        }

        try {
            // Find if the period already exists in the list
            val existingIndex = modelDatasSnapList.indexOfFirst { it.fireBaseKeyID == period.fireBaseKeyID }

            if (existingIndex != -1) {
                // Update existing period
                modelDatasSnapList[existingIndex] = period
            } else {
                // Add new period
                modelDatasSnapList.add(period)
            }

            // Update Firebase and Realm
            coroutineScope.launch {
                // Update Firebase
                try {
                    val dataToUpdate = map_01_VentsHistoriquesDataBase(modelDatasSnapList)

                    // Remove listeners temporarily
                    removeFirebaseListener()

                    firebaseRef.setValue(dataToUpdate)
                        .addOnSuccessListener {
                            attachFirebaseListener()
                            onSuccess(period.idPeriodDonAncienDataBase)
                        }
                        .addOnFailureListener {
                            attachFirebaseListener()
                        }

                    // Update Realm
                    scheduleSafeRealmUpdate()

                    // Notify UI and trigger change event
                    notifyUIUpdate()
                    _dataChangedEvent.value = System.currentTimeMillis()

                } catch (e: Exception) {
                    // Exception handling without logging
                }
            }
        } finally {
            modelUpdateInProgress.set(false)
        }
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
                // Exception handling without logging
            }
            valueEventListener = null
        }

        acheteursChangeListener?.let {
            try {
                firebaseRef.removeEventListener(it)
            } catch (e: Exception) {
                // Exception handling without logging
            }
            acheteursChangeListener = null
        }
    }
}
