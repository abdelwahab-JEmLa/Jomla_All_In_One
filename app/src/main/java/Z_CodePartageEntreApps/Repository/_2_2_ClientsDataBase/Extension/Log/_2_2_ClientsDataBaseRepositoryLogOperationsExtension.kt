package Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase.Extension.Log

import Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase._2_2_ClientsDataBase_RepositoryImpl
import Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase._2_2_ClientsDataBase_Repository
import android.util.Log

class _2_2_ClientsDataBaseRepositoryLogOperationsExtension(
    private val repositoryImpl: _2_2_ClientsDataBase_RepositoryImpl
) {
    private val TAG = _2_2_ClientsDataBase_Repository.TAG

    fun log(
        dataCount: Int,
        initialDataLoaded: Boolean,
        progressValue: Float,
        lastUpdateTimestamp: Long,
        isListenerActive: Boolean,
        isFlowListenerActive: Boolean
    ) {
        Log.d(TAG, "_2_2_ClientsDataBase_Repository status: ")
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
