package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import android.util.Log
import com.google.firebase.database.DataSnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FirebaseDataLogger {
    private const val TAG = "FirebaseDataLog"
    private const val TAG_ERROR = "FirebaseDataError"
    private const val TAG_INFO = "FirebaseDataInfo"
    private const val MAX_ITEMS_TO_LOG = 5
    private var isEnabled = true
    private var startTime: Long = 0

    fun startLogging() {
        startTime = System.currentTimeMillis()
        logInfo("====== Firebase Data Loading Started ======")
        logSystemInfo()
    }

    fun logLoadingComplete(totalProducts: Int, duration: Long) {
        logInfo("""
            ====== Loading Complete ======
            Total Products Loaded: $totalProducts
            Duration: ${duration}ms
            Average Time Per Product: ${if (totalProducts > 0) duration / totalProducts else 0}ms
            ==============================
        """.trimIndent())
    }

    fun logSnapshotDetails(snapshot: DataSnapshot) {
        if (!isEnabled) return
        
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        
        logInfo("""
            ===== Snapshot Details at $timestamp =====
            Exists: ${snapshot.exists()}
            Children Count: ${snapshot.childrenCount}
            Has Value: ${snapshot.value != null}
            Value Type: ${snapshot.value?.javaClass?.simpleName}
            First $MAX_ITEMS_TO_LOG Keys: ${snapshot.children.mapNotNull { it.key }.take(MAX_ITEMS_TO_LOG)}
            Priority: ${snapshot.priority}
            =======================================
        """.trimIndent())
    }

    fun logProductParsing(snapshot: DataSnapshot, product: _ModelAppsFather.ProduitModel?) {
        if (!isEnabled) return
        
        val productId = snapshot.key
        val success = product != null
        
        logInfo("""
            ----- Product Parse Result -----
            ID: $productId
            Success: $success
            ${if (success) getProductDetails(product!!) else getParseFailureDetails(snapshot)}
            -----------------------------
        """.trimIndent())
    }

    fun logDatabaseError(error: Exception, context: String) {
        logError("""
            !!!!! Database Error !!!!!
            Context: $context
            Error Type: ${error.javaClass.simpleName}
            Message: ${error.message}
            Stack Trace:
            ${error.stackTrace.take(5).joinToString("\n")}
            -------------------------
        """.trimIndent())
    }

    fun logStateUpdate(products: List<ProduitModel>, viewModelState: String) {
        if (!isEnabled) return
        
        logInfo("""
            >>>>> State Update >>>>>
            Products Count: ${products.size}
            ViewModel State: $viewModelState
            Memory Usage: ${getMemoryUsage()}
            Time Since Start: ${System.currentTimeMillis() - startTime}ms
            >>>>>>>>>>>>>>>>>>>>>
        """.trimIndent())
    }

    private fun getProductDetails(product: ProduitModel): String {
        return """
            |Name: ${product.nom}
            |Colors Count: ${product.coloursEtGouts.size}
            |Sales Orders: ${product.bonsVentDeCetteCota.size}
            |Purchase Orders: ${product.historiqueBonsCommend.size}
            |Status: ${if (product.isVisible) "Visible" else "Hidden"}
            |Needs Update: ${product.besoinToBeUpdated}
            |Not Found: ${product.non_Trouve}
        """.trimMargin()
    }

    private fun getParseFailureDetails(snapshot: DataSnapshot): String {
        val value = snapshot.value
        return """
            |Parse Failed
            |Available Fields: ${(value as? Map<*, *>)?.keys?.joinToString(", ")}
            |Raw Value Type: ${value?.javaClass?.simpleName}
            |Children Count: ${snapshot.childrenCount}
        """.trimMargin()
    }

    fun logDataValidation(products: List<ProduitModel>) {
        if (!isEnabled) return
        
        val validation = validateData(products)
        logInfo("""
            ##### Data Validation Report #####
            Total Products: ${products.size}
            Valid Products: ${validation.validCount}
            Invalid Products: ${validation.invalidCount}
            Issues Found: ${validation.issues.size}
            
            Detailed Issues:
            ${validation.issues.joinToString("\n")}
            ###############################
        """.trimIndent())
    }

    private data class ValidationResult(
        val validCount: Int,
        val invalidCount: Int,
        val issues: List<String>
    )

    private fun validateData(products: List<ProduitModel>): ValidationResult {
        val issues = mutableListOf<String>()
        var validCount = 0
        var invalidCount = 0

        products.forEach { product ->
            val productIssues = mutableListOf<String>()
            
            if (product.id == 0L) productIssues.add("Invalid ID")
            if (product.nom.isBlank()) productIssues.add("Empty name")
            if (product.coloursEtGouts.isEmpty()) productIssues.add("No colors defined")
            
            if (productIssues.isEmpty()) {
                validCount++
            } else {
                invalidCount++
                issues.add("Product ${product.id}: ${productIssues.joinToString(", ")}")
            }
        }

        return ValidationResult(validCount, invalidCount, issues)
    }

    private fun logSystemInfo() {
        val runtime = Runtime.getRuntime()
        logInfo("""
            ===== System Information =====
            Available Processors: ${runtime.availableProcessors()}
            Max Memory: ${runtime.maxMemory() / 1024 / 1024}MB
            Total Memory: ${runtime.totalMemory() / 1024 / 1024}MB
            Free Memory: ${runtime.freeMemory() / 1024 / 1024}MB
            Android API Level: ${android.os.Build.VERSION.SDK_INT}
            Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
            ============================
        """.trimIndent())
    }

    private fun getMemoryUsage(): String {
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        val maxMemory = runtime.maxMemory() / 1024 / 1024
        return "$usedMemory MB / $maxMemory MB"
    }

    private fun logInfo(message: String) {
        Log.i(TAG_INFO, message)
    }

    private fun logError(message: String) {
        Log.e(TAG_ERROR, message)
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        logInfo("Logging ${if (enabled) "enabled" else "disabled"}")
    }
}
