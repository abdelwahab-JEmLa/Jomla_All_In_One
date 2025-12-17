package Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface K_TempTravailleRepository {
    var modelDatas: SnapshotStateList<K_TempTravaille>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun addNewIntervalForWalid(
        recordId: String?,
        intervalId: String?,
        startTime: String?
    )

    fun updateExistingIntervalForWalid(
        recordId: String?,
        intervalId: String?,
        startTime: String?,
        endTime: String?,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp?
    )

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<K_TempTravaille>, Flow<Float>>
    suspend fun updateDatas(datas: SnapshotStateList<K_TempTravaille>)
    fun stopDatabaseListener()
    fun checkConnectivityAndSync()
    fun deleteIntevaleDeTemp(intervalId: String)
    fun ajoutJour(date: String)

    fun updateUnSeulData(
        recordId: String? = null,
    )

    // Add new interval with default parameters
    fun addNewInterval(
        recordId: String? = null,
        intervalId: String? = null,
        startTime: String? = null
    )

    /**
     * Add a new interval to an existing K_TempTravaille record
     * @param k_TempTravaille The K_TempTravaille record to add the interval to
     * @param intervalesDeTravaille The interval to add
     */
    fun addNewIntervals_au_TempTravaille(
        k_TempTravaille: K_TempTravaille,
        intervalesDeTravaille: List<K_TempTravaille.IntervalesDeTravaille>
    )

    fun updateExistingInterval(
        recordId: String? = null,
        intervalId: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp? = null
    )

    fun updateOnPasseData(
        record: K_TempTravaille? = null,
    )

    fun ajouteRecodeAvecIntervaleDAchat(
        clientId: Long,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp
    ): K_TempTravaille?

    companion object {
        val caReference = K_TempTravaille.caRef
    }
}
