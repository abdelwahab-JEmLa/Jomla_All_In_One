package Z_CodePartageEntreApps.Repository._3_ClientsDataBase.Extension.Log

import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase_Repository
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase_RepositoryImpl
import android.util.Log

class _3_ClientsDataBaseRepositoryLogOperationsExtension(
    private val repositoryImpl: _3_ClientsDataBase_RepositoryImpl
) {
    private val TAG = _3_ClientsDataBase_Repository.TAG

    fun log(
        dataCount: Int,
        initialDataLoaded: Boolean,
        progressValue: Float,
        lastUpdateTimestamp: Long,
        isListenerActive: Boolean,
        isFlowListenerActive: Boolean
    ) {
        Log.d(TAG, "_3_ClientsDataBase_Repository status: ")
        Log.d(TAG, "- Data count: $dataCount")
        Log.d(TAG, "- Initial data loaded: $initialDataLoaded")
        Log.d(TAG, "- Progress value: $progressValue")
        Log.d(TAG, "- Last upsertLenceCommandeRepoGroupedProtoAvanJuin3 timestamp: $lastUpdateTimestamp")
        Log.d(
            TAG,
            "- Listeners active: isListenerActive=$isListenerActive, isFlowListenerActive=$isFlowListenerActive"
        )
    }
}
