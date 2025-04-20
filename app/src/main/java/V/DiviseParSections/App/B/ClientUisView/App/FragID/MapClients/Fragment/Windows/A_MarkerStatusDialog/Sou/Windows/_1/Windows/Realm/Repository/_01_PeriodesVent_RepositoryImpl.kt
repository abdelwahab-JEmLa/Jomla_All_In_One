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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

class _01_PeriodesVent_RepositoryImpl : _01_PeriodesVent_Repository {
    private val TAG = "_01_PeriodesVent_Repo"

    override var modelDatasSnapList: SnapshotStateList<_01_PeriodesVent> = mutableStateListOf()
    var idComptDeCeTelephone: String = ""

    private val realm: Realm = createRealm()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val realmMutex = Mutex()
    private val pendingRealmUpdate = AtomicBoolean(false)
    private val modelUpdateInProgress = AtomicBoolean(false)
    private val firebaseRef = _01_PeriodesVent_Repository.sonDataBaseRef
    private var valueEventListener: ValueEventListener? = null
    private var productChangeListener: ValueEventListener? = null

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
                _01_PeriodesVent::class,
                Vendeur::class,
                Produit::class
            )
        )
        return Realm.open(config)
    }

    private fun initializeData() {
        val isRealmEmpty = realm.query<_01_PeriodesVent>().count().find() == 0L

        if (isRealmEmpty) {
            checkFirebaseOrCreateTestData()
        } else {
            loadFromRealmTOmodelDatasSnapList()
            loadFromFirebase()
        }
    }

    private fun checkFirebaseOrCreateTestData() {
        firebaseRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                createTestDataIfEmpty()
            } else {
                loadFromFirebase()
            }
        }.addOnFailureListener {
            loadFromFirebase()
        }
    }

    private fun createTestDataIfEmpty() {
        coroutineScope.launch {
            val testPeriodes = createTestData()
            updateModelDatasList(testPeriodes)
            updateRealmAndFirebase()
        }
    }

    private fun createTestData(): List<_01_PeriodesVent> {
        val testPeriodes = mutableListOf<_01_PeriodesVent>()

        for (i in 1..3) {
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

        return testPeriodes
    }

    private fun createVendeur(id: Long, nom: String, vendeurKey: String): Vendeur {
        return Vendeur().apply {
            keyID = vendeurKey
            idVendeur = id
            nomVendeur = nom
            produits = realmListOf()

            for (k in 1..5) {
                val produitId = k.toLong()
                val produitNom = "Produit $k"
                val produitKey = "$keyID-<{Pr}->($produitId=$produitNom)"

                val produit = Produit().apply {
                    keyID = produitKey
                    idProduit = produitId
                    nomProduit = produitNom
                    quantity = (k * 5)
                }

                produits.add(produit)
            }
        }
    }

    private fun loadFromRealmTOmodelDatasSnapList() {
        val allPeriodes = realm.query<_01_PeriodesVent>()
            .sort("dateDebutDeCettePeriode", Sort.DESCENDING)
            .find()

        updateModelDatasList(allPeriodes)
        _progressRepo.value = 1.0f
    }

    private fun updateModelDatasList(periodes: List<_01_PeriodesVent>) {
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
                        query<_01_PeriodesVent>().find().also { delete(it) }
                        query<Vendeur>().find().also { delete(it) }
                        query<Produit>().find().also { delete(it) }

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

    private fun updateFirebase() {
        coroutineScope.launch {
            try {
                val dataToUpdate = convertToFirebaseFormat(modelDatasSnapList)
                removeFirebaseListener()
                firebaseRef.setValue(dataToUpdate).addOnCompleteListener {
                    attachFirebaseListener()
                }
            } catch (e: Exception) {
                // Error handling would go here
            }
        }
    }

    private fun convertToFirebaseFormat(periodes: List<_01_PeriodesVent>): Map<String, Any> {
        return periodes.associate { periode ->
            val validPeriodeKey = ensureValidPeriodeKey(periode)

            validPeriodeKey to mapOf(
                "dateDebutDeCettePeriode" to periode.dateDebutDeCettePeriode,
                "tempDebutDeCettePeriode" to periode.tempDebutDeCettePeriode,
                "vendeurs" to mapVendeurs(periode.vendeurs, validPeriodeKey)
            )
        }
    }

    private fun ensureValidPeriodeKey(periode: _01_PeriodesVent): String {
        return if (periode.keyID.startsWith("{PV}->")) {
            periode.keyID
        } else {
            "{PV}->${periode.dateDebutDeCettePeriode}=${periode.tempDebutDeCettePeriode}"
        }
    }

    private fun mapVendeurs(vendeurs: List<Vendeur>, periodeKey: String): Map<String, Any> {
        return vendeurs.associate { vendeur ->
            val validVendeurKey = ensureValidVendeurKey(vendeur, periodeKey)

            validVendeurKey to mapOf(
                "idVendeur" to vendeur.idVendeur,
                "nomVendeur" to vendeur.nomVendeur,
                "produits" to mapProduits(vendeur.produits, validVendeurKey)
            )
        }
    }

    private fun ensureValidVendeurKey(vendeur: Vendeur, periodeKey: String): String {
        return if (vendeur.keyID.contains("<{Ve}->")) {
            vendeur.keyID
        } else {
            "$periodeKey-<{Ve}->(${vendeur.idVendeur}=${vendeur.nomVendeur})"
        }
    }

    private fun mapProduits(produits: List<Produit>, vendeurKey: String): Map<String, Any> {
        return produits.associate { produit ->
            val validProduitKey = ensureValidProduitKey(produit, vendeurKey)

            validProduitKey to mapOf(
                "idProduit" to produit.idProduit,
                "nomProduit" to produit.nomProduit,
                "quantity" to produit.quantity
            )
        }
    }

    private fun ensureValidProduitKey(produit: Produit, vendeurKey: String): String {
        return if (produit.keyID.contains("<{Pr}->")) {
            produit.keyID
        } else {
            "$vendeurKey-<{Pr}->(${produit.idProduit}=${produit.nomProduit})"
        }
    }

    private fun loadFromFirebase() {
        removeFirebaseListener()
        attachFirebaseListener()
        attachProductChangeListener()
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
                    parseFirebaseSnapshot(snapshot)
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

    private fun attachProductChangeListener() {
        productChangeListener = createProductChangeListener()
        try {
            firebaseRef.addValueEventListener(productChangeListener!!)
        } catch (e: Exception) {
            // Log error if needed
        }
    }

    private fun createProductChangeListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    var changesMade = processProductChanges(snapshot)
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

    private fun processProductChanges(snapshot: DataSnapshot): Boolean {
        var changesMade = false

        snapshot.children.forEach { periodeSnapshot ->
            val periodeKey = periodeSnapshot.key ?: return@forEach
            if (!periodeKey.startsWith("{PV}->")) return@forEach

            periodeSnapshot.child("vendeurs").children.forEach { vendeurSnapshot ->
                val vendeurKey = vendeurSnapshot.key ?: return@forEach
                if (!vendeurKey.contains("<{Ve}->")) return@forEach

                vendeurSnapshot.child("produits").children.forEach { produitSnapshot ->
                    val produitKey = produitSnapshot.key ?: return@forEach
                    if (!produitKey.contains("<{Pr}->")) return@forEach

                    val idProduit = produitSnapshot.child("idProduit").getValue(Long::class.java) ?: 0L
                    val nomProduit = produitSnapshot.child("nomProduit").getValue(String::class.java) ?: ""
                    val quantity = produitSnapshot.child("quantity").getValue(Int::class.java) ?: 0

                    val updated = updateProductInModel(periodeKey, vendeurKey, produitKey, idProduit, nomProduit, quantity)
                    if (updated) changesMade = true
                }
            }
        }

        return changesMade
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

    private fun updateProductInModel(
        periodeKey: String,
        vendeurKey: String,
        produitKey: String,
        id: Long,
        nom: String,
        quantity: Int
    ): Boolean {
        if (modelUpdateInProgress.getAndSet(true)) return false

        try {
            val periode = modelDatasSnapList.find { it.keyID == periodeKey } ?: return false
            val vendeur = periode.vendeurs.find { it.keyID == vendeurKey } ?: return false
            val produit = vendeur.produits.find { it.keyID == produitKey }

            return if (produit != null) {
                val changed = produit.quantity != quantity ||
                        produit.idProduit != id ||
                        produit.nomProduit != nom

                if (changed) {
                    produit.quantity = quantity
                    produit.idProduit = id
                    produit.nomProduit = nom
                    true
                } else {
                    false
                }
            } else {
                vendeur.produits.add(Produit().apply {
                    keyID = produitKey
                    this.idProduit = id
                    this.nomProduit = nom
                    this.quantity = quantity
                })
                true
            }
        } finally {
            modelUpdateInProgress.set(false)
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

            parsePeriodeVendeurs(periodeSnapshot, periode)
            newPeriodesVente.add(periode)
        }

        updateModelDatasList(newPeriodesVente)
    }

    private fun parsePeriodeVendeurs(periodeSnapshot: DataSnapshot, periode: _01_PeriodesVent) {
        periodeSnapshot.child("vendeurs").children.forEach { vendeurSnapshot ->
            val vendeurKey = vendeurSnapshot.key ?: return@forEach
            val vendeurId = vendeurSnapshot.child("id").getValue(Long::class.java) ?: 0L
            val vendeurNom = vendeurSnapshot.child("nom").getValue(String::class.java) ?: ""

            val vendeur = Vendeur().apply {
                keyID = vendeurKey
                idVendeur = vendeurId
                nomVendeur = vendeurNom
                produits = realmListOf()
            }

            parseVendeurProduits(vendeurSnapshot, vendeur)
            periode.vendeurs.add(vendeur)
        }
    }

    private fun parseVendeurProduits(vendeurSnapshot: DataSnapshot, vendeur: Vendeur) {
        vendeurSnapshot.child("produits").children.forEach { produitSnapshot ->
            val produitKey = produitSnapshot.key ?: return@forEach
            val produitId = produitSnapshot.child("id").getValue(Long::class.java) ?: 0L
            val produitNom = produitSnapshot.child("nom").getValue(String::class.java) ?: ""
            val quantity = produitSnapshot.child("quantity").getValue(Int::class.java) ?: 0

            vendeur.produits.add(Produit().apply {
                keyID = produitKey
                idProduit = produitId
                nomProduit = produitNom
                this.quantity = quantity
            })
        }
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

        productChangeListener?.let {
            try {
                firebaseRef.removeEventListener(it)
            } catch (e: Exception) {
                // Log error if needed
            }
            productChangeListener = null
        }
    }

    private fun updateRealmSafely() {
        coroutineScope.launch {
            realmMutex.withLock {
                try {
                    realm.write {
                        // Clear existing data
                        query<_01_PeriodesVent>().find().also { delete(it) }
                        query<Vendeur>().find().also { delete(it) }
                        query<Produit>().find().also { delete(it) }

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

    private fun createDeepCopyForRealm(source: _01_PeriodesVent): _01_PeriodesVent {
        val copy = _01_PeriodesVent().apply {
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

    private fun createVendeurCopy(sourceVendeur: Vendeur): Vendeur {
        val vendeurCopy = Vendeur().apply {
            keyID = sourceVendeur.keyID
            idVendeur = sourceVendeur.idVendeur
            nomVendeur = sourceVendeur.nomVendeur
            produits = realmListOf()
        }

        sourceVendeur.produits.forEach { sourceProduit ->
            vendeurCopy.produits.add(Produit().apply {
                keyID = sourceProduit.keyID
                idProduit = sourceProduit.idProduit
                nomProduit = sourceProduit.nomProduit
                quantity = sourceProduit.quantity
            })
        }

        return vendeurCopy
    }

    override suspend fun refreshData() {
        _progressRepo.value = 0f
        loadFromRealmTOmodelDatasSnapList()
        loadFromFirebase()
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
}
