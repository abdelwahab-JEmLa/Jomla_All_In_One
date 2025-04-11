package Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase.Extension.Log

import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBaseRepositoryImpl
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_Repository
import android.util.Log

class _2_1_ProduitsDataBaseRepositoryLogOperationsExtension(
    private val repositoryImpl: _2_1_ProduitsDataBaseRepositoryImpl
) {
    private val TAG = _2_1_ProduitsDataBase_Repository.TAG

    fun log(
        dataCount: Int,
        initialDataLoaded: Boolean,
        progressValue: Float,
        lastUpdateTimestamp: Long,
        isListenerActive: Boolean,
        isFlowListenerActive: Boolean
    ) {
        Log.d(TAG, "_2_1_ProduitsDataBase_Repository status: ")
        Log.d(TAG, "- Data count: $dataCount")
        Log.d(TAG, "- Initial data loaded: $initialDataLoaded")
        Log.d(TAG, "- Progress value: $progressValue")
        Log.d(TAG, "- Last update timestamp: $lastUpdateTimestamp")
        Log.d(
            TAG,
            "- Listeners active: isListenerActive=$isListenerActive, isFlowListenerActive=$isFlowListenerActive"
        )
    }
}
