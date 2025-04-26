package Z_CodePartageEntreApps.Repository._1_5_Vendeur.Extension.Log

import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_VendeurRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
import android.util.Log

class _1_5_VendeurRepositoryLogOperationsExtention(
    private val repositoryImpl: _1_5_VendeurRepositoryImpl
) {
    private val TAG = _1_5_Vendeur_Repository.TAG

    fun log(
        dataCount: Int,
        initialDataLoaded: Boolean,
        progressValue: Float,
        lastUpdateTimestamp: Long,
        isListenerActive: Boolean,
        isFlowListenerActive: Boolean
    ) {
        Log.d(TAG, "_1_5_Vendeur_Repository status: ")
        Log.d(TAG, "- Data count: $dataCount")
        Log.d(TAG, "- Initial data loaded: $initialDataLoaded")
        Log.d(TAG, "- Progress value: $progressValue")
        Log.d(TAG, "- Last upsert_1_3_TransactionCommercial timestamp: $lastUpdateTimestamp")
        Log.d(
            TAG,
            "- Listeners active: isListenerActive=$isListenerActive, isFlowListenerActive=$isFlowListenerActive"
        )
    }
}
