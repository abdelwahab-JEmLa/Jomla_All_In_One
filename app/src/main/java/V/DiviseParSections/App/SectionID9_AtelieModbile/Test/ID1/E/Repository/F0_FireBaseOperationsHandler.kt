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

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var needUpdateListener: ValueEventListener? = null

    // FIXED: Helper function to safely get reference path information
    private fun getReferencePath(reference: DatabaseReference): String {
        return try {
            // Alternative approach: build path from reference structure
            var pathBuilder = ""
            var currentRef: DatabaseReference? = reference
            val pathSegments = mutableListOf<String>()

            // Extract key information that's safely accessible
            reference.key?.let { key ->
                pathSegments.add(key)
            }

            // Build a descriptive path string without using .path
            if (pathSegments.isNotEmpty()) {
                "Firebase Reference: ${pathSegments.joinToString("/")}"
            } else {
                "Firebase Reference: [Root or undefined path]"
            }
        } catch (e: Exception) {
            "Firebase Reference: [Path access restricted]"
        }
    }

    // Debug utility function to explore Firebase structure
    private fun debugFirebaseStructure(snapshot: DataSnapshot, path: String = "", maxDepth: Int = 3, currentDepth: Int = 0) {
        if (currentDepth > maxDepth) return

        val indent = "  ".repeat(currentDepth)
        println("${indent}🔍 Path: '$path' | Key: '${snapshot.key}' | Exists: ${snapshot.exists()} | Children: ${snapshot.childrenCount}")

        if (snapshot.exists()) {
            // Show value if it's a leaf node
            if (!snapshot.hasChildren()) {
                val value = snapshot.value
                println("${indent}   📄 Value: $value (${value?.javaClass?.simpleName})")
            } else {
                // Recursively explore children
                snapshot.children.forEachIndexed { index, child ->
                    if (index < 10) { // Limit to first 10 children to avoid spam
                        val childPath = if (path.isEmpty()) child.key ?: "unknown" else "$path/${child.key}"
                        debugFirebaseStructure(child, childPath, maxDepth, currentDepth + 1)
                    } else if (index == 10) {
                        println("${indent}   ... (${snapshot.childrenCount - 10} more children)")
                    }
                }
            }
        }
    }

    // Function to manually explore specific paths
    private fun exploreSpecificPaths(snapshot: DataSnapshot) {
        println("\n🎯 === EXPLORING SPECIFIC PATHS ===")

        val pathsToCheck = listOf(
            "",
            "A_ProduitInfos",
            "D_TarificationInfos",
            "C_InfosSqlDataBases",
            "C_InfosSqlDataBases/A_ProduitInfos",
            "C_InfosSqlDataBases/D_TarificationInfos"
        )

        pathsToCheck.forEach { path ->
            println("\n📍 Checking path: '$path'")
            val targetSnapshot = if (path.isEmpty()) {
                snapshot
            } else {
                val parts = path.split("/")
                var current = snapshot
                for (part in parts) {
                    current = current.child(part)
                }
                current
            }

            println("   Exists: ${targetSnapshot.exists()}")
            println("   Children count: ${targetSnapshot.childrenCount}")
            println("   Value: ${targetSnapshot.value}")

            if (targetSnapshot.exists() && targetSnapshot.hasChildren()) {
                println("   Child keys: ${targetSnapshot.children.map { it.key }.take(5)}")
            }
        }
    }

    // Function to scan for products at any level
    private fun scanForProducts(snapshot: DataSnapshot, path: String = "") {
        println("\n🔎 === SCANNING FOR A_PRODUITINFOS ===")
        scanRecursively(snapshot, path, "A_ProduitInfos")

        println("\n🔎 === SCANNING FOR ANY PRODUCT-LIKE DATA ===")
        scanForProductLikeData(snapshot, path)
    }

    private fun scanRecursively(snapshot: DataSnapshot, path: String, target: String) {
        val currentPath = if (path.isEmpty()) snapshot.key ?: "root" else "$path/${snapshot.key}"

        if (snapshot.key == target || currentPath.contains(target, ignoreCase = true)) {
            println("🎯 FOUND MATCH at path: '$currentPath'")
            println("   Exists: ${snapshot.exists()}")
            println("   Children: ${snapshot.childrenCount}")
            if (snapshot.exists() && snapshot.hasChildren()) {
                snapshot.children.take(3).forEach { child ->
                    println("   Child: ${child.key} = ${child.value}")
                }
            }
        }

        snapshot.children.forEach { child ->
            scanRecursively(child, currentPath, target)
        }
    }

    private fun scanForProductLikeData(snapshot: DataSnapshot, path: String = "") {
        snapshot.children.forEach { child ->
            val childPath = if (path.isEmpty()) child.key ?: "unknown" else "$path/${child.key}"

            // Look for objects that might be products
            if (child.hasChildren()) {
                val childKeys = child.children.map { it.key }.toSet()
                val productIndicators = setOf("nom", "id", "timestamps", "keyFireBase")

                if (productIndicators.intersect(childKeys).size >= 2) {
                    println("🔍 Potential product data at: '$childPath'")
                    println("   Keys: ${childKeys.take(10)}")

                    // Try to extract basic info
                    val id = child.child("id").value
                    val nom = child.child("nom").value
                    val keyFireBase = child.child("keyFireBase").value
                    println("   Sample data - id: $id, nom: $nom, keyFireBase: $keyFireBase")
                }

                // Recurse
                scanForProductLikeData(child, childPath)
            }
        }
    }

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
                        println("\n🚀 === FIREBASE DEBUG SESSION STARTED ===")
                        println("📊 Root snapshot info:")
                        // FIXED: Use the helper function instead of direct .path access
                        println("   Reference info: ${getReferencePath(ref)}")
                        println("   Key: ${snapshot.key}")
                        println("   Exists: ${snapshot.exists()}")
                        println("   Children count: ${snapshot.childrenCount}")

                        // Comprehensive structure analysis
                        println("\n📋 === FULL STRUCTURE ANALYSIS ===")
                        debugFirebaseStructure(snapshot)

                        // Explore specific paths
                        exploreSpecificPaths(snapshot)

                        // Scan for products
                        scanForProducts(snapshot)

                        println("\n⚙️ === PROCESSING DATA ===")
                        onProgressUpdate(0.5f)
                        val infosSqlDataBases = mapFromFirebaseSnapshot(snapshot)

                        // Try multiple strategies to find products
                        val strategies = listOf(
                            { snapshot.child(defaultModel.refFireBaseA_ProduitInfos) },    //<--
                            //TODO(1): pk ca ne trouve pas  /00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/A_ProduitInfos
                            { snapshot.child("A_ProduitInfos") },
                            { snapshot.child("C_InfosSqlDataBases").child("A_ProduitInfos") },
                            { snapshot.child("a_produitinfos") }, // lowercase
                            { snapshot.child("AProduitInfos") }, // without underscore
                        )

                        var productsFound = false
                        strategies.forEachIndexed { index, strategy ->
                            if (!productsFound) {
                                try {
                                    val productsSnapshot = strategy()
                                    println("\n🔧 Strategy ${index + 1}: Checking for products...")
                                    println("   Exists: ${productsSnapshot.exists()}")
                                    println("   Children: ${productsSnapshot.childrenCount}")

                                    if (productsSnapshot.exists()) {
                                        println("   ✅ Found products! Attempting to map...")
                                        val mappedProducts = mapSnapshotToObjects(productsSnapshot, A_ProduitInfos::class)
                                        products.addAll(mappedProducts)
                                        println("   ✅ Successfully mapped ${mappedProducts.size} products")
                                        productsFound = true
                                    }
                                } catch (e: Exception) {
                                    println("   ❌ Strategy ${index + 1} failed: ${e.message}")
                                }
                            }
                        }

                        if (!productsFound) {
                            println("\n❌ No products found with any strategy")
                            println("🔍 Manual search for any product-like structures...")

                            // Last resort: try to find product data anywhere
                            findAndMapProductsManually(snapshot)?.let { manualProducts ->
                                products.addAll(manualProducts)
                                println("✅ Found ${manualProducts.size} products via manual search")
                            }
                        }

                        println("\n📈 === FINAL RESULTS ===")
                        println("Tarifications: ${infosSqlDataBases.d_TarificationInfos.size}")
                        println("Products: ${products.size}")

                        onProgressUpdate(0.9f)
                        onAddSuccess(
                            infosSqlDataBases.d_TarificationInfos,
                            products
                        )

                        onProgressUpdate(1f)
                        println("🏁 === DEBUG SESSION COMPLETED ===\n")

                    } catch (e: Exception) {
                        println("❌ ERROR: Exception in getDataFromFirebase: ${e.message}")
                        e.printStackTrace()
                        onProgressUpdate(0f)
                    }
                } else {
                    println("❌ DEBUG: Main snapshot does not exist")
                    onProgressUpdate(1f)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("❌ ERROR: Firebase operation cancelled: ${error.message}")
                onProgressUpdate(0f)
            }
        })
    }

    private fun findAndMapProductsManually(snapshot: DataSnapshot): List<A_ProduitInfos>? {
        // Search through all children for product-like data
        fun searchForProducts(current: DataSnapshot): DataSnapshot? {
            // Check if current node has product-like children
            if (current.hasChildren()) {
                val firstChild = current.children.firstOrNull()
                if (firstChild != null && firstChild.hasChildren()) {
                    val childKeys = firstChild.children.map { it.key }.toSet()
                    val productKeys = setOf("id", "nom", "keyFireBase", "timestamps")

                    if (productKeys.intersect(childKeys).size >= 3) {
                        println("🎯 Found product-like structure at current location")
                        return current
                    }
                }

                // Recursively search children
                for (child in current.children) {
                    val result = searchForProducts(child)
                    if (result != null) return result
                }
            }
            return null
        }

        val productSnapshot = searchForProducts(snapshot)
        return if (productSnapshot != null) {
            try {
                mapSnapshotToObjects(productSnapshot, A_ProduitInfos::class)
            } catch (e: Exception) {
                println("❌ Failed to map manually found products: ${e.message}")
                null
            }
        } else {
            null
        }
    }

    // Rest of the existing methods remain unchanged...
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
