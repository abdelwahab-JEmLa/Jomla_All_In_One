package Z_CodePartageEntreApps.Repository._2_2_CouleursDataBase.Extension.Log

import Z_CodePartageEntreApps.Repository._2_2_CouleursDataBase._2_2_CouleursDataBase_Repository
import Z_CodePartageEntreApps.Repository._2_2_CouleursDataBase._2_2_CouleursDataBase_RepositoryImpl
import android.util.Log

class _2_2_CouleursDataBaseRepositoryLogOperationsExtension(
    private val repositoryImpl: _2_2_CouleursDataBase_RepositoryImpl
) {
    private val TAG = _2_2_CouleursDataBase_Repository.TAG

    fun log(
        dataCount: Int,
        initialDataLoaded: Boolean,
        progressValue: Float,
        lastUpdateTimestamp: Long,
        isListenerActive: Boolean,
        isFlowListenerActive: Boolean
    ) {
        Log.d(TAG, "_2_2_CouleursDataBase_Repository status: ")
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
