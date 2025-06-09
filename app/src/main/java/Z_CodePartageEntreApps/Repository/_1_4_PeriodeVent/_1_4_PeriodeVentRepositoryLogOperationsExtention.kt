package Z_CodePartageEntreApps.Repository._1_4_PeriodeVent

import android.util.Log

class _1_4_PeriodeVentRepositoryLogOperationsExtention(
    private val repositoryImpl: _1_4_PeriodeVentRepositoryImpl
) {
    private val TAG = _1_4_PeriodeVent_Repository.TAG

    fun log(
        dataCount: Int,
        initialDataLoaded: Boolean,
        progressValue: Float,
        lastUpdateTimestamp: Long,
        isListenerActive: Boolean,
        isFlowListenerActive: Boolean
    ) {
        Log.d(TAG, "_1_4_PeriodeVent_Repository status: ")
        Log.d(TAG, "- Data count: $dataCount")
        Log.d(TAG, "- Initial data loaded: $initialDataLoaded")
        Log.d(TAG, "- Progress value: $progressValue")
        Log.d(TAG, "- Last upsertLenceCommandeRepoGroupedProtoAvantJuin3 timestamp: $lastUpdateTimestamp")
        Log.d(
            TAG,
            "- Listeners active: isListenerActive=$isListenerActive, isFlowListenerActive=$isFlowListenerActive"
        )
    }
}
