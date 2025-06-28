package Z_CodePartageEntreApps.Model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

class F_BonsAchats(
    val vid: Long = 0,
){
    var infosDeBase by mutableStateOf(InfosDeBase())
    @IgnoreExtraProperties
    class InfosDeBase

    var etatesMutable by mutableStateOf(EtatesMutable())
    @IgnoreExtraProperties
    class EtatesMutable

    data class Ancien_SoldArticlesTabelle_Main internal constructor(
        val vid: Long = 0,
        val idArticle: Long = 0,
        val nameArticle: String = "",
        val clientSoldToItId: Long = 0,
        val date: String = "",
        val color1IdPicked: Long = 0,
        val color1SoldQuantity: Int = 0,
        val color2IdPicked: Long = 0,
        val color2SoldQuantity: Int = 0,
        val color3IdPicked: Long = 0,
        val color3SoldQuantity: Int = 0,
        val color4IdPicked: Long = 0,
        val color4SoldQuantity: Int = 0,
        val confimed: Boolean = false,
    )

    @IgnoreExtraProperties
    class ClientBonVentModel(
        vid: Long = 0,
        var clientIdChoisi: Long = 0,
        var produitStatueDeBaseDeChezCeClient: StatueDeBase = StatueDeBase(),
        init_colours_achete: List<ColorAchatModel> = emptyList(),
    ) {
        // Basic information
        var bonStatueDeBase by mutableStateOf(BonStatueDeBase())
        @IgnoreExtraProperties
        data class StatueDeBase(
            var positionDonClientsList: Int = 0,
        )
        // Status management
        @IgnoreExtraProperties
        class BonStatueDeBase {
            var lastUpdateTimestamp: Long by mutableStateOf(System.currentTimeMillis())
        }

        @get:Exclude
        var colours_Achete: SnapshotStateList<ColorAchatModel> =
            init_colours_achete.toMutableStateList()

        var coloursAcheteList: List<ColorAchatModel>
            get() = colours_Achete.toList()
            set(value) {
                colours_Achete.clear()
                colours_Achete.addAll(value)
            }
        @IgnoreExtraProperties
        class ColorAchatModel(
            var vidPosition: Long = 0,
            var couleurId: Long = 0,
            var nom: String = "",
            var quantity_Achete: Int = 0,
            var imogi: String = ""
        )
    }


}


     /*
class J_AppInstalleDonTelephone(
    var keyID: Long = 0,
) {
    var infosDeBase by mutableStateOf(InfosDeBase())
    @IgnoreExtraProperties
    class InfosDeBase {
        var nom by mutableStateOf("Non Defini")
        var widthScreen by mutableIntStateOf(0)
        var itsTablette by mutableStateOf(false)
    }

    var etatesMutable by mutableStateOf(EtatesMutable())
    @IgnoreExtraProperties
    class EtatesMutable {
        var itsReciverTelephone by mutableStateOf(false)
        var indexDonsParentList by mutableLongStateOf(0)
        var nearbyWifiAdressIpConexion by mutableStateOf("")
    }
}

interface J_AppInstalleDonTelephoneRepository {
    var modelDatas: SnapshotStateList<J_AppInstalleDonTelephone>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List_GroupeAchatProduit<J_AppInstalleDonTelephone>, Flow<Float>>
    suspend fun updateUnSeulData(datas: SnapshotStateList<J_AppInstalleDonTelephone>)
    fun updatePhones()

    companion object {
        // Convert width pixels to dp
        private val displayMetrics = Resources.getSystem().displayMetrics
        private val widthPixels = displayMetrics.widthPixels
        val metricsWidthPixels = widthPixels.pixelsToDp(displayMetrics)

        val caReference = _ModelAppsFather.ref_HeadOfModels.child("J_AppInstalleDonTelephone")

        // Extension function to convert pixels to dp
        private fun Int.pixelsToDp(displayMetrics: DisplayMetrics): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                this.toFloat(),
                displayMetrics
            ).toInt()
        }
    }
}

class J_AppInstalleDonTelephoneRepositoryImpl : J_AppInstalleDonTelephoneRepository {
    private val TAG = "J_AppInstalleDonTelephoneRepo"
    override var modelDatas: SnapshotStateList<J_AppInstalleDonTelephone> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private var listener: ValueEventListener? = null

    init {
        startDatabaseListener()
        verifyAndAddPhone(
            "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}",
            J_AppInstalleDonTelephoneRepository.metricsWidthPixels
        )
    }

    private fun verifyAndAddPhone(phoneName: String, screenWidth: Int) {
        // Obtenir tous les téléphones et vérifier localement
        J_AppInstalleDonTelephoneRepository.caReference.get().addOnSuccessListener { snapshot ->
            var phoneExists = false
            var maxId = 0L

            // Parcourir tous les téléphones pour trouver celui avec le même nom
            snapshot.children.forEach { phoneSnapshot ->
                try {
                    val keyID = phoneSnapshot.child("keyID").getValue(Long::class.java) ?: 0
                    val nom = phoneSnapshot.child("infosDeBase/nom").getValue(String::class.java)
                        ?: phoneSnapshot.child("infosDeBase").child("nom")
                            .getValue(String::class.java)
                        ?: ""

                    // Mettre à jour l'ID maximum
                    if (keyID > maxId) {
                        maxId = keyID
                    }

                    // Vérifier si le téléphone existe déjà
                    if (nom == phoneName) {
                        phoneExists = true

                        // Créer l'objet téléphone à partir des données Firebase
                        val phone = J_AppInstalleDonTelephone().apply {
                            this.keyID = keyID
                            infosDeBase.nom = nom
                            infosDeBase.widthScreen = phoneSnapshot.child("infosDeBase/widthScreen")
                                .getValue(Int::class.java)
                                ?: phoneSnapshot.child("infosDeBase").child("widthScreen")
                                    .getValue(Int::class.java)
                                        ?: screenWidth
                            // Check if the device is add tablet based on screen width
                            infosDeBase.itsTablette = infosDeBase.widthScreen > 400
                            etatesMutable.itsReciverTelephone =
                                phoneSnapshot.child("etatesMutable/itsReciverTelephone")
                                    .getValue(Boolean::class.java)
                                    ?: phoneSnapshot.child("etatesMutable")
                                        .child("itsReciverTelephone").getValue(Boolean::class.java)
                                            ?: false
                        }

                        // Vérifier si le téléphone est déjà dans la liste locale
                        if (modelDatas.none { it.keyID == keyID }) {
                            modelDatas.upsert(phone)
                        }
                    }
                } catch (e: Exception) {
                    // Exception silently handled
                }
            }

            // Si le téléphone n'existe pas, l'ajouter
            if (!phoneExists) {
                val newId = maxId + 1

                val newPhone = J_AppInstalleDonTelephone().apply {
                    keyID = newId
                    infosDeBase.nom = phoneName
                    infosDeBase.widthScreen = screenWidth
                    // Check if the device is add tablet based on screen width
                    infosDeBase.itsTablette = screenWidth > 400
                    etatesMutable.itsReciverTelephone = false
                }

                // Ajouter à la liste locale
                if (modelDatas.none { it.keyID == newId }) {
                    modelDatas.upsert(newPhone)

                    // Ajouter à Firebase
                    J_AppInstalleDonTelephoneRepository.caReference
                        .child(newId.toString())
                        .setValue(newPhone)
                }
            }
        }.addOnFailureListener {
            // Exception silently handled
        }
    }

    private fun startDatabaseListener() {
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val totalItems = snapshot.childrenCount.toInt()
                    var processedItems = 0

                    modelDatas.clear()
                    progressRepo.value = 0f

                    for (dataSnapshot in snapshot.children) {
                        val category = dataSnapshot.getValue(J_AppInstalleDonTelephone::class.java)
                        category?.let { cat ->
                            // Set tablet status based on screen width
                            cat.infosDeBase.itsTablette = cat.infosDeBase.widthScreen > 400
                            modelDatas.upsert(cat)
                        }

                        processedItems++
                        progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                    }

                    // Sort categories by position (classmentDonsParentList)
                    modelDatas.sortBy { it.etatesMutable.indexDonsParentList }

                    progressRepo.value = 1.0f
                } catch (e: Exception) {
                    progressRepo.value = 0f
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressRepo.value = 0f
            }
        }

        // Attach the listener to the Firebase reference
        listener?.let {
            J_AppInstalleDonTelephoneRepository.caReference.addValueEventListener(it)
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List_GroupeAchatProduit<J_AppInstalleDonTelephone>, Flow<Float>> {
        val progressFlow = MutableStateFlow(0f)

        return suspendCancellableCoroutine { continuation ->
            val listener = object : ValueEventListener {
                private var isResumed = false

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        // Prevent multiple resumptions
                        if (isResumed) return

                        val categories = mutableListOf<J_AppInstalleDonTelephone>()
                        val totalItems = snapshot.childrenCount.toInt()
                        var processedItems = 0

                        modelDatas.clear()
                        progressFlow.value = 0f
                        progressRepo.value = 0f

                        for (dataSnapshot in snapshot.children) {
                            val category =
                                dataSnapshot.getValue(J_AppInstalleDonTelephone::class.java)
                            category?.let { cat ->
                                // Set tablet status based on screen width
                                cat.infosDeBase.itsTablette = cat.infosDeBase.widthScreen > 400
                                categories.upsert(cat)
                                modelDatas.upsert(cat)
                            }

                            processedItems++
                            progressFlow.value = processedItems.toFloat() / totalItems.toFloat()
                            progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                        }

                        // Sort categories by position
                        categories.sortBy { it.etatesMutable.indexDonsParentList }
                        modelDatas.sortBy { it.etatesMutable.indexDonsParentList }

                        progressFlow.value = 1.0f
                        progressRepo.value = 1.0f

                        // Ensure resumption happens only once
                        if (!isResumed) {
                            isResumed = true
                            continuation.resume(Pair(categories, progressFlow))

                            // Remove the listener after successful data retrieval
                            J_AppInstalleDonTelephoneRepository.caReference.removeEventListener(this)
                        }
                    } catch (e: Exception) {
                        if (!isResumed) {
                            isResumed = true
                            continuation.resumeWithException(e)
                            progressRepo.value = 0f

                            // Remove the listener in case of error
                            J_AppInstalleDonTelephoneRepository.caReference.removeEventListener(this)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (!isResumed) {
                        isResumed = true
                        continuation.resumeWithException(Exception("Database error: ${error.message}"))
                        progressRepo.value = 0f

                        // Remove the listener in case of cancellation
                        J_AppInstalleDonTelephoneRepository.caReference.removeEventListener(this)
                    }
                }
            }

            // Attach the listener
            J_AppInstalleDonTelephoneRepository.caReference.addValueEventListener(listener)

            // Ensure listener is removed if coroutine is cancelled
            continuation.invokeOnCancellation {
                J_AppInstalleDonTelephoneRepository.caReference.removeEventListener(listener)
            }
        }
    }

    override suspend fun updateUnSeulData(datas: SnapshotStateList<J_AppInstalleDonTelephone>) {
        // Update local modelDatas with the new data
        modelDatas.clear()
        modelDatas.addAll(datas)

        // Update Firebase with the new data
        datas.forEach { category ->
            J_AppInstalleDonTelephoneRepository.caReference.child(category.keyID.toString())
                .setValue(category)
        }
    }

    override fun updatePhones() {
        modelDatas.forEach { phone ->
            J_AppInstalleDonTelephoneRepository.caReference
                .child(phone.keyID.toString())
                .setValue(phone)
        }
    }
}

                */
