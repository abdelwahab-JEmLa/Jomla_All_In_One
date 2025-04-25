package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment._1_3_TransactionCommercial
import android.util.Log

class _1_3_TransactionCommercialRepositoryLogOperationsExtention(
    private val repositoryImpl: _1_3_TransactionCommercialRepositoryImpl
) {
    private val TAG = _1_3_TransactionCommercial_Repository.TAG

    fun log(
        dataCount: Int,
        initialDataLoaded: Boolean,
        progressValue: Float,
        lastUpdateTimestamp: Long,
        isListenerActive: Boolean,
        isFlowListenerActive: Boolean
    ) {
        Log.d(TAG, "_1_3_TransactionCommercial_Repository status: ")
        Log.d(TAG, "- Data count: $dataCount")
        Log.d(TAG, "- Initial data loaded: $initialDataLoaded")
        Log.d(TAG, "- Progress value: $progressValue")
        Log.d(TAG, "- Last update timestamp: $lastUpdateTimestamp")
        Log.d(
            TAG,
            "- Listeners active: isListenerActive=$isListenerActive, isFlowListenerActive=$isFlowListenerActive"
        )
    }

    // New method to log data additions
    fun logDataAdd(data: _1_3_TransactionCommercial) {
        Log.d(TAG, "Data added to repository:")
        Log.d(TAG, "- ID (vid): ${data.vid}")
        Log.d(TAG, "- Timestamp: ${System.currentTimeMillis()}")
        // Log additional fields from the data model as needed
        // For example, if your _1_3_TransactionCommercial class has fields like name, amount, etc.
        // You can log them here
    }
}
