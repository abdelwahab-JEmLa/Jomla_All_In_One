package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows.BProto_ClientsDataBaseButton

import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.ref_HeadOfModels
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewModel_BProto_ClientsDataBase(
    val mainRepo: B_ClientDataBaseRepository
) : ViewModel() {
    fun populateB_ClientDataBaseParSonAncien() {
        viewModelScope.launch {
            try {
                // Get data from old database structure
                val ancienDataList = getAncienDataBase()

                if (ancienDataList.isEmpty()) {
                    Log.d("ViewModel_BProto", "No ancien data found to migrate")
                    return@launch
                }

                // Transform old data structure to new structure
                val newDataList = ancienDataList.map { ancienData ->
                    Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase(
                        id = ancienData.id,
                        nom = ancienData.nom,
                        numTelephone = ancienData.statueDeBase.numTelephone,
                        couleur = ancienData.statueDeBase.couleur,
                        bonDuClientsSu = ancienData.statueDeBase.bonDuClientsSu,
                        currentCreditBalance = ancienData.statueDeBase.currentCreditBalance,
                        positionDonClientsList = ancienData.statueDeBase.positionDonClientsList,
                        cUnClientTemporaire = ancienData.statueDeBase.cUnClientTemporaire,
                        auFilterFAB = ancienData.statueDeBase.auFilterFAB,
                        typeDeSonMagasine = when (ancienData.statueDeBase.typeDeSonMagasine) {
                            B_ClientsDataBase.StatueDeBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
                            B_ClientsDataBase.StatueDeBase.TypeDeSonMagasine.AlIMENTATION_GENERALE ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.TypeDeSonMagasine.AlIMENTATION_GENERALE
                            else ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
                        },
                        clientTypeMode = when (ancienData.etatesMutable.clientTypeMode) {
                            B_ClientsDataBase.EtatesMutable.ClientTypeMode.NEVEAU ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.ClientTypeMode.NEVEAU
                            B_ClientsDataBase.EtatesMutable.ClientTypeMode.ANCIEN ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.ClientTypeMode.ANCIEN
                            B_ClientsDataBase.EtatesMutable.ClientTypeMode.EVITE ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.ClientTypeMode.EVITE
                        },
                        latitude = ancienData.gpsLocation.latitude,
                        longitude = ancienData.gpsLocation.longitude,
                        title = ancienData.gpsLocation.title,
                        snippet = ancienData.gpsLocation.snippet,
                        actuelleEtat = mapActuelleEtat(ancienData.gpsLocation.actuelleEtat)
                    )
                }

                // Create a SnapshotStateList from the converted data
                val snapshotList = androidx.compose.runtime.snapshots.SnapshotStateList<Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase>()
                snapshotList.addAll(newDataList)

                // Update repository with new data structure
                mainRepo.updateMultiDatas(snapshotList)

                Log.d("ViewModel_BProto", "Successfully migrated ${newDataList.size} clients from ancien database")
            } catch (e: Exception) {
                Log.e("ViewModel_BProto", "Error migrating ancien database: ${e.message}", e)
            }
        }
    }

    // Helper function to map the old DernierEtatAAffiche enum to the new one
    private fun mapActuelleEtat(oldEtat: B_ClientsDataBase.GpsLocation.DernierEtatAAffiche?): Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche {
        return when (oldEtat) {
            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.آNON_DEFINI ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.NON_DEFINI
            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT
            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.VENDU_A_LUI ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.VENDU_A_LUI
            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.Cible ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.Cible
            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.CIBLE_PRIORITE_2 ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2
            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.CIBLE_POUR_2 ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.CIBLE_POUR_2
            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.CLIENT_ABSENT ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.CLIENT_ABSENT
            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.AVEC_MARCHANDISE ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.AVEC_MARCHANDISE
            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.FERME ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.FERME
            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.A_EVITE ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.A_EVITE
            null ->
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.NON_DEFINI
        }
    }

    suspend fun getAncienDataBase(): List<B_ClientsDataBase> =
        withContext(
            Dispatchers.IO
        ) {
            return@withContext try {
                ref_HeadOfModels.child("B_ClientsDataBase")
                    .get()
                    .await()
                    .children
                    .mapNotNull {
                        it.getValue(B_ClientsDataBase::class.java)
                    }
            } catch (e: Exception) {
                Log.e("TAG", "Error retrieving ancien dataB: ${e.message}", e)
                emptyList()
            }
        }

}
