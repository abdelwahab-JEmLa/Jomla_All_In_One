package Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand.Extension.Log

import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_Repository
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_RepositoryImpl
import android.util.Log

class _4_CouleurOperationCommandRepositoryLogOperationsExtension(
    private val repositoryImpl: _4_CouleurOperationCommand_RepositoryImpl
) {
    private val TAG = _4_CouleurOperationCommand_Repository.TAG

    fun log(
        dataCount: Int,
        initialDataLoaded: Boolean,
        progressValue: Float,
        lastUpdateTimestamp: Long,
        isListenerActive: Boolean,
        isFlowListenerActive: Boolean
    ) {
        Log.d(TAG, "_4_CouleurOperationCommand_Repository status: ")
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
