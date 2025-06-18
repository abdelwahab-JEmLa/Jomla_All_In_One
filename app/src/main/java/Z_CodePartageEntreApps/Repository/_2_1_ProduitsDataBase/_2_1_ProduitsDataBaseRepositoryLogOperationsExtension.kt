package Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase

import android.util.Log

class _2_1_ProduitsDataBaseRepositoryLogOperationsExtension(
    private val repository: _2_1_ProduitsDataBase_RepositoryImpl
) {
    private val TAG = _2_1_ProduitsDataBase_Repository.TAG

    fun log(
        modelDatasSize: Int,
        initialDataLoaded: Boolean,
        progressValue: Float,
        lastUpdateTimestamp: Long,
        isListenerActive: Boolean,
        isFlowListenerActive: Boolean
    ) {
        try {
            Log.d(TAG, "=== _2_1_ProduitsDataBase Repository Status ===")
            Log.d(TAG, "Data size: $modelDatasSize")
            Log.d(TAG, "Initial data loaded: $initialDataLoaded")
            Log.d(TAG, "Progress value: $progressValue")
            Log.d(TAG, "Last update_showDetailsExpanded timestamp: $lastUpdateTimestamp")
            Log.d(TAG, "Listener active: $isListenerActive")
            Log.d(TAG, "Flow listener active: $isFlowListenerActive")
            
            // Log Firebase reference path details for debugging
            logFirebaseReferencePath()
        } catch (e: Exception) {
            Log.e(TAG, "Error in log operation: ${e.message}")
        }
    }
    
    fun logFirebaseReferencePath() {
        try {
            val firebaseRef = _2_1_ProduitsDataBase_Repository.sonDataBaseRef
            
            // Get complete reference path
            val refPath = firebaseRef.toString()
            
            Log.d(TAG, "=== Firebase Reference Debug Info ===")
            Log.d(TAG, "Complete Firebase reference path: $refPath")
            
            // Break down the path components for easier debugging
            val pathComponents = refPath.split("/")
            Log.d(TAG, "Path components:")
            pathComponents.forEachIndexed { index, component ->
                Log.d(TAG, "  Component $index: $component")
            }
            
            // Check if the path matches expected structure from the comments
            val expectedPath = listOf(
                "00_DataPrototype-04-02",
                "_1_developingRef", // or _2_productionTestRef based on mode
                "A_ProduitsDataBase",
                "A_MainDataBase"
            )
            
            Log.d(TAG, "Expected path components: ${expectedPath.joinToString(" -> ")}")
            
            // Log Firebase database URL
            val databaseUrl = firebaseRef.root.toString()
            Log.d(TAG, "Firebase database URL: $databaseUrl")
            
            // Check if the database URL matches the expected URL from the comments
            val expectedUrl = "https://abdelwahab-jemla-com-default-rtdb.europe-west1.firebasedatabase.app"
            Log.d(TAG, "Expected database URL: $expectedUrl")
            Log.d(TAG, "URL match: ${databaseUrl.contains(expectedUrl)}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in logFirebaseReferencePath: ${e.message}")
        }
    }
}
