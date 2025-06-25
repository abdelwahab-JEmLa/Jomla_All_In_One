package Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase

import Z_CodePartageEntreApps.Model.A_ProduitInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Model.getKeyFireBase
import Z_CodePartageEntreApps.Model.parseDepuitOldAuNew
import Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase.ReflectionUtils.isSyntheticProperty
import Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase.ReflectionUtils.sanitizeValue
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class F0_FireBaseOperationsHandler(
    val onProgressUpdate: (Float) -> Unit = { }
) {
    val ref: DatabaseReference = GroupeRepositorysProtoAvJuin3Model
        .getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    val childD_TarificationInfos = ref.child("D_TarificationInfos")
    val childA_ProduitInfos = ref.child("A_ProduitInfos")

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var needUpdateListener: ValueEventListener? = null


    suspend fun <DataBase : Any> updateInFB(
        data: DataBase
    ): String? = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

            val childRef = when (data::class) {
                D_TarificationInfos::class -> childD_TarificationInfos
                A_ProduitInfos::class -> childA_ProduitInfos
                else -> return@withContext null
            }

            val key = when (data) {
                is D_TarificationInfos -> {
                    data.keyFireBase.ifEmpty { data.withProperDefaults().keyFireBase }
                }
                is A_ProduitInfos -> {
                    data.keyFireBase.ifEmpty { data.withProperKeyFireBase().keyFireBase }
                }
                else -> return@withContext null
            }

            onProgressUpdate(0.6f)

            // Update the existing item or create new one
            val updateResult = suspendCancellableCoroutine<String?> { continuation ->
                val updateMap = createUpdateMap(data)

                childRef.child(key).updateChildren(updateMap)
                    .addOnSuccessListener {
                        continuation.resume(key)
                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                        continuation.resume(null)
                    }
            }

            onProgressUpdate(1f)
            updateResult

        } catch (e: Exception) {
            e.printStackTrace()
            onProgressUpdate(0f)
            null
        }
    }

    private fun createUpdateMap(data: Any): Map<String, Any> {
        val updateMap = mutableMapOf<String, Any>()

        when (data) {
            is D_TarificationInfos -> {
                val updated = data.withProperDefaults()
                updated::class.memberProperties.forEach { prop ->
                    if (!isSyntheticProperty(prop.name)) {
                        try {
                            val value = prop.getter.call(updated)
                            updateMap[prop.name] = sanitizeValue(value)
                        } catch (e: Exception) {
                            // Skip problematic properties
                            e.printStackTrace()
                        }
                    }
                }
                updateMap["needUpdate"] = true
                updateMap["timestamps"] = System.currentTimeMillis()
            }
            is A_ProduitInfos -> {
                val updated = data.withProperKeyFireBase()
                updated::class.memberProperties.forEach { prop ->
                    if (!ReflectionUtils.isSyntheticProperty(prop.name)) {
                        try {
                            val value = prop.getter.call(updated)
                            updateMap[prop.name] = sanitizeValue(value)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                updateMap["needUpdate"] = true
                updateMap["timestamps"] = System.currentTimeMillis()
            }
        }

        return updateMap
    }

    suspend inline fun <reified DataBase : Any> setListDataInlineFun(
        datas: List<DataBase> = emptyList(),
        crossinline onAddSuccess: () -> Unit ={}
    ): Map<String, DataBase> = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

            if (datas.isEmpty()) {
                onProgressUpdate(1f)
                return@withContext emptyMap()
            }

            // Determine which child reference to use based on data type
            val childRef = when (DataBase::class) {
                D_TarificationInfos::class -> childD_TarificationInfos
                A_ProduitInfos::class -> childA_ProduitInfos
                else -> {
                    onProgressUpdate(0f)
                    return@withContext emptyMap()
                }
            }

            onProgressUpdate(0.3f)

            // Create batch update_showDetailsExpanded map for all items
            val batchUpdateMap = mutableMapOf<String, Any>()

            // Process each data item and update to batch
            for (data in datas) {
                try {
                    // Generate proper key for Firebase
                    val keyFireBase = when (data) {
                        is D_TarificationInfos -> {
                            val updated = data.withProperDefaults()
                            updated.keyFireBase
                        }
                        is A_ProduitInfos -> {
                            val updated = data.withProperKeyFireBase()
                            updated.keyFireBase
                        }
                        else -> {
                            // Fallback key generation
                            val id = try {
                                val idField = data::class.java.getDeclaredField("id")
                                idField.isAccessible = true
                                idField.getLong(data)
                            } catch (e: Exception) {
                                0L
                            }

                            val nom = try {
                                val nomField = data::class.java.getDeclaredField("nom")
                                nomField.isAccessible = true
                                nomField.get(data) as? String ?: ""
                            } catch (e: Exception) {
                                ""
                            }

                            getKeyFireBase(id, nom)
                        }
                    }

                    // Add to batch update_showDetailsExpanded map
                    batchUpdateMap[keyFireBase] = data

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Continue processing other items even if one fails
                }
            }

            onProgressUpdate(0.7f)

            // Perform batch update_showDetailsExpanded using updateChildren
            suspendCancellableCoroutine<Unit> { continuation ->
                childRef.updateChildren(batchUpdateMap)
                    .addOnSuccessListener {
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                        continuation.resume(Unit)
                    }
            }

            onProgressUpdate(1f)
            onAddSuccess()

            return@withContext emptyMap()

        } catch (e: Exception) {
            e.printStackTrace()
            onProgressUpdate(0f)
            return@withContext emptyMap()
        }
    }

    inline fun <reified T : Any> mapSnapshotToObjects(
        snapshot: DataSnapshot,
        kClass: KClass<T>
    ): List<T> {
        return try {
            val results = mutableListOf<T>()
            getDatasFixed<T>(snapshot, results)
            results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun convertArticlesBasesToProduitInfos(ancientsListe: List<ArticlesBasesStatsTable>): List<A_ProduitInfos> {
        return ancientsListe.map { ancien ->
            parseDepuitOldAuNew(ancien)
        }
    }

    private fun verifyFirebaseConnectivity() {
        coroutineScope.launch {
            try {
                val isConnected = F9_FirebaseDebugUtils.verifyFirebaseReference(ref)
                F9_FirebaseDebugUtils.logFirebaseOperation(
                    "verifyFirebaseConnectivity",
                    ref,
                    0,
                    isConnected
                )
            } catch (e: Exception) {
                F9_FirebaseDebugUtils.logFirebaseOperation(
                    "verifyFirebaseConnectivity",
                    ref,
                    0,
                    false,
                    e
                )
                e.printStackTrace()
            }
        }
    }

    fun getDataFromFirebase(
        onAddSuccess: (
            List<D_TarificationInfos>,
            List<A_ProduitInfos>,
        ) -> Unit
    ) {
        onProgressUpdate(0.1f)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.exists()) {
                        onProgressUpdate(0.3f)

                        val tarificationsSnapshot = snapshot.child("D_TarificationInfos")
                        val mappedTarifications = if (tarificationsSnapshot.exists()) {
                            mapSnapshotToObjects(tarificationsSnapshot, D_TarificationInfos::class)
                        } else {
                            emptyList()
                        }

                        onProgressUpdate(0.6f)

                        val productsSnapshot = snapshot.child("A_ProduitInfos")
                        val mappedProducts = if (productsSnapshot.exists()) {
                            mapSnapshotToObjects(productsSnapshot, A_ProduitInfos::class)
                        } else {
                            emptyList()
                        }

                        onProgressUpdate(0.9f)
                        onAddSuccess(mappedTarifications, mappedProducts)
                        onProgressUpdate(1f)

                    } else {
                        onProgressUpdate(1f)
                        onAddSuccess(emptyList(), emptyList())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    onProgressUpdate(0f)
                    onAddSuccess(emptyList(), emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onProgressUpdate(0f)
                onAddSuccess(emptyList(), emptyList())
            }
        })
    }

    init {
        verifyFirebaseConnectivity()
    }

    suspend fun getAncienDB_changeKeysFireBase(): Pair<Int, Map<String, A_ProduitInfos>> = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continuation ->
            coroutineScope.launch {
                try {
                    onProgressUpdate(0.1f)
                    val firebaseDatabase = FirebaseDatabase.getInstance()
                    val refDBJetPackExport = firebaseDatabase.getReference("e_DBJetPackExport")
                    val (originalCount, resultMap) = extractedFrom_getAncienDB_changeKeysFireBase(refDBJetPackExport)
                    continuation.resume(Pair(originalCount, resultMap))
                } catch (e: Exception) {
                    e.printStackTrace()
                    onProgressUpdate(0f)
                    continuation.resume(Pair(0, emptyMap()))
                }
            }
        }
    }


}
