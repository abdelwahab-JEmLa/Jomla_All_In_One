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

    // Updated method signature to match the implementation in MockTempTravailleRepository
    fun updateUnSeulData(
        recordId: String? = null,
    )


    // Added methods that were previously in ViewModel
    fun addNewInterval(
        recordId: String? = null,
        intervalId: String? = null,
        startTime: String? = null
    )

    fun updateExistingInterval(
        recordId: String? = null,
        intervalId: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp? = null
    )
    // Updated method signature to match the implementation in MockTempTravailleRepository
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



