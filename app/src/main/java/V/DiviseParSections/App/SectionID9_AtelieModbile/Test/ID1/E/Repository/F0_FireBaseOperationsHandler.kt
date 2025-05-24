package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A0_DataBasesGroup
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.full.memberProperties

class F0_FireBaseOperationsHandler(
    private val onProgressUpdate: (Float) -> Unit = { }
) {
    val ref: DatabaseReference = _0_0_HeadOfRepositorys_Model
        .getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    private val childD_TarificationInfos = ref.child("D_TarificationInfos")
    private val childA_ProduitInfos = ref.child("A_ProduitInfos")

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var needUpdateListener: ValueEventListener? = null

    fun getDataFromFirebase(onAddSuccess: (
        List<D_TarificationInfos>,
        List<A_ProduitInfos>,
    ) -> Unit) {
        val products = mutableListOf<A_ProduitInfos>()
        val defaultModel = A0_DataBasesGroup()

        onProgressUpdate(0.1f)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        onProgressUpdate(0.5f)

                        val infosSqlDataBases = mapFromFirebaseSnapshot(snapshot)

                        try {
                            val productsSnapshot = snapshot.child("A_ProduitInfos")

                            if (productsSnapshot.exists() && productsSnapshot.hasChildren()) {
                                val mappedProducts = mapSnapshotToObjects(productsSnapshot, A_ProduitInfos::class)
                                products.addAll(mappedProducts)
                            } else {
                                findProductsInSnapshot(snapshot)?.let { foundProducts ->
                                    products.addAll(foundProducts)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        onProgressUpdate(0.9f)
                        onAddSuccess(
                            infosSqlDataBases.d_TarificationInfos,
                            products
                        )

                        onProgressUpdate(1f)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        onProgressUpdate(0f)
                        onAddSuccess(emptyList(), emptyList())
                    }
                } else {
                    onProgressUpdate(1f)
                    onAddSuccess(emptyList(), emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onProgressUpdate(0f)
                onAddSuccess(emptyList(), emptyList())
            }
        })
    }

    private fun findProductsInSnapshot(snapshot: DataSnapshot): List<A_ProduitInfos>? {
        fun searchRecursively(current: DataSnapshot): DataSnapshot? {
            if (current.hasChildren()) {
                val children = current.children.toList()
                if (children.isNotEmpty()) {
                    val firstChild = children.first()
                    if (firstChild.hasChildren()) {
                        val childKeys = firstChild.children.map { it.key }.toSet()
                        val productKeys = setOf("id", "nom", "keyFireBase", "timestamps")

                        if (productKeys.intersect(childKeys).size >= 3) {
                            return current
                        }
                    }
                }

                for (child in current.children) {
                    val result = searchRecursively(child)
                    if (result != null) return result
                }
            }
            return null
        }

        val productSnapshot = searchRecursively(snapshot)
        return if (productSnapshot != null) {
            try {
                mapSnapshotToObjects(productSnapshot, A_ProduitInfos::class)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    suspend fun upsertAllAndReturnListIdToData(
        mapData: Map<Long, D_TarificationInfos>,
        onAddSuccess: (Map<String, D_TarificationInfos>) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

            if (mapData.isEmpty()) {
                onProgressUpdate(1f)
                onAddSuccess(emptyMap())
                return@withContext
            }

            onProgressUpdate(0.3f)
            val tariffsMap = mutableMapOf<String, Any>()
            val resultMap = mutableMapOf<String, D_TarificationInfos>()

            var processedCount = 0
            val totalCount = mapData.size

            mapData.values.forEach { tariff ->
                try {
                    val tariffMap = mutableMapOf<String, Any>()

                    tariff::class.memberProperties.forEach { prop ->
                        try {
                            val value = prop.getter.call(tariff)
                            when {
                                value == null -> tariffMap[prop.name] = "null"
                                value::class.java.isEnum -> tariffMap[prop.name] = value.toString()
                                else -> tariffMap[prop.name] = value
                            }
                        } catch (e: Exception) {
                            tariffMap[prop.name] = "null"
                        }
                    }

                    val key = tariff.keyFireBase.ifEmpty {
                        getKeyFireBase(tariff.id, tariff.nom)
                    }

                    tariffsMap[key] = tariffMap
                    resultMap[key] = tariff

                    processedCount++
                    val progress = 0.3f + (processedCount.toFloat() / totalCount) * 0.4f
                    onProgressUpdate(progress)

                } catch (e: Exception) {
                    onProgressUpdate(0.5f)
                }
            }

            if (tariffsMap.isNotEmpty()) {
                try {
                    onProgressUpdate(0.8f)

                    suspendCancellableCoroutine<Unit> { continuation ->
                        childD_TarificationInfos.updateChildren(tariffsMap)
                            .addOnSuccessListener {
                                continuation.resume(Unit)
                            }
                            .addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                    }

                    onProgressUpdate(1f)

                } catch (firebaseException: Exception) {
                    onProgressUpdate(0.8f)
                }
            } else {
                onProgressUpdate(1f)
            }

            onAddSuccess(resultMap)

        } catch (e: Exception) {
            onProgressUpdate(0f)
            onAddSuccess(emptyMap())
        }
    }

    fun deleteTarificationInfosNode(onComplete: (Boolean) -> Unit = {}) {
        onProgressUpdate(0.3f)

        ref.child("D_TarificationInfos").removeValue()
            .addOnSuccessListener {
                onProgressUpdate(1f)
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                onProgressUpdate(0f)
                onComplete(false)
            }
    }

    fun startNeedUpdateListener() {
        onProgressUpdate(0.5f)

        needUpdateListener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onProgressUpdate(1f)
            }

            override fun onCancelled(error: DatabaseError) {
                onProgressUpdate(0f)
            }
        })
    }

    fun stopNeedUpdateListener() {
        needUpdateListener?.let {
            ref.removeEventListener(it)
            needUpdateListener = null
            onProgressUpdate(1f)
        }
    }
}
