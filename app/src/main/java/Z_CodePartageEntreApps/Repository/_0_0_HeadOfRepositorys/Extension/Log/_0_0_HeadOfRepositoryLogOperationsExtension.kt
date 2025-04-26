package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.Extension.Log

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_RepositoryImpl
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class _0_0_HeadOfRepositoryLogOperationsExtension(
    private val repositoryImpl: _0_0_HeadOfRepositorys_RepositoryImpl
) {
    private val TAG = _0_0_HeadOfRepositorys_Repository.TAG


    fun log(
        dataCount: Int,
        initialDataLoaded: Boolean = false,
        progressValue: Float = 0f,
        lastUpdateTimestamp: Long = 0L,
        isListenerActive: Boolean = false,
        isFlowListenerActive: Boolean = false
    ) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = if (lastUpdateTimestamp > 0) {
            dateFormat.format(Date(lastUpdateTimestamp))
        } else {
            "Not updated yet"
        }

        Log.d(TAG, "=== _0_0_HeadOfRepositorys Repository Status ===")
        Log.d(TAG, "- Data count: $dataCount")
        Log.d(TAG, "- Initial data loaded: $initialDataLoaded")
        Log.d(TAG, "- Progress value: ${String.format("%.2f", progressValue * 100)}%")
        Log.d(TAG, "- Last upsert_1_3_TransactionCommercial timestamp: $formattedDate")
        Log.d(
            TAG,
            "- Listeners active: isListenerActive=$isListenerActive, isFlowListenerActive=$isFlowListenerActive"
        )
        Log.d(TAG, "=========================================")
    }

    fun logRepositoryProgress(
        repository: String,
        progress: Float,
        initialDataLoaded: Boolean = false,
        dataCount: Int = 0,
        isListenerActive: Boolean = false,
        isUpdating: Boolean = false
    ) {
        Log.d(TAG, "=== Repository '$repository' Details ===")
        Log.d(TAG, "- Progress: ${String.format("%.2f", progress * 100)}%")
        Log.d(TAG, "- Initial data loaded: $initialDataLoaded")
        Log.d(TAG, "- Data count: $dataCount")
        Log.d(TAG, "- Listener active: $isListenerActive")
        Log.d(TAG, "- Currently updating: $isUpdating")

        // Identify potential issues preventing progress completion
        if (progress < 1.0f) {
            Log.d(TAG, "- Progress incomplete reasons:")
            if (!initialDataLoaded) {
                Log.d(TAG, "  * Initial data not yet loaded")
            }
            if (isUpdating) {
                Log.d(TAG, "  * Repository currently performing updates")
            }
            if (dataCount == 0) {
                Log.d(TAG, "  * No data available in repository")
            }
        }
        Log.d(TAG, "----------------------------------------")
    }

    fun logError(
        operation: String,
        error: Exception
    ) {
        Log.e(TAG, "Error during $operation: ${error.message}")
        Log.e(TAG, "Stack trace: ${error.stackTrace.joinToString("\n")}")
    }
}
