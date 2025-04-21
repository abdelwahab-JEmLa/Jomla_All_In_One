package Z_CodePartageEntreApps.Windows.A.B_DataBaseEdite.Windows.BProto_ClientsDataBaseButton

import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.ref_HeadOfModels
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewModel_BProto_ClientsDataBase(
    val mainRepo: B_ClientDataBaseRepository,
    val _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository,
    ) : ViewModel() {
    fun changeToutLesclientStateAuCIBLE_PRIORITE_2() {
        viewModelScope.launch {
            try {
                // Create a copy of the current client list
                val updatedClients = mainRepo.modelDatas.map { client ->
                    // Create a copy with modified properties
                    if (client.nom.startsWith("Nouveau client")) {
                        // Replace "Nouveau client" with "ز" but keep any numbers/text after it
                        val newName = client.nom.replace("Nouveau client", "ز")
                        client.copy(
                            nom = newName,
                            actuelleEtat = B_ClientDataBase.DernierEtatAAffiche.NON_DEFINI
                        )
                    } else {
                        // Just update the state for other clients
                        client.copy(
                            actuelleEtat = B_ClientDataBase.DernierEtatAAffiche.NON_DEFINI
                        )
                    }
                }

                // Convert to SnapshotStateList for the repository
                val snapshotList = SnapshotStateList<B_ClientDataBase>()
                snapshotList.addAll(updatedClients)

                // Update the repository with all modified clients
                mainRepo.updateMultiDatas(snapshotList)

                Log.d("ViewModel_BProto", "Successfully changed state of ${updatedClients.size} clients to CIBLE_PRIORITE_2")
            } catch (e: Exception) {
                Log.e("ViewModel_BProto", "Error changing client states: ${e.message}", e)
            }
        }
    }
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

                // Convert to _3_ClientsDataBase objects
                val convertedData = newDataList.map { client ->
                    convertToClientsDataBase(client)
                }

                // Create a SnapshotStateList from the converted data
                val snapshotList =
                    androidx.compose.runtime.snapshots.SnapshotStateList<_3_ClientsDataBase>()
                snapshotList.addAll(convertedData)

                // Update repository with new data structure
                _0_0_HeadOfRepositorys_Repository.repositorys_Model._3_ClientsDataBase_Repository.addMultiDATAsEtReturnVIDsList(
                    convertedData
                ) { vids ->
                    // Update the snapshotList with new VIDs if needed
                    vids.forEachIndexed { index, vid ->
                        if (index < snapshotList.size) {
                            snapshotList[index].vid = vid
                        }
                    }

                    // Update the repository with the updated objects
                    viewModelScope.launch {
                        try {
                            _0_0_HeadOfRepositorys_Repository.repositorys_Model._3_ClientsDataBase_Repository.updateMultiDatas(
                                snapshotList
                            )
                            Log.d(
                                "ViewModel_BProto",
                                "Successfully migrated ${newDataList.size} clients to _3_ClientsDataBase_Repository"
                            )
                        } catch (e: Exception) {
                            Log.e(
                                "ViewModel_BProto",
                                "Error updating _3_ClientsDataBase_Repository: ${e.message}",
                                e
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel_BProto", "Error migrating ancien database: ${e.message}", e)
            }
        }
    }

    // Helper function to convert B_ClientDataBase to _3_ClientsDataBase
    private fun convertToClientsDataBase(client: Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase): _3_ClientsDataBase {
        return _3_ClientsDataBase(
            vid = client.id,
            nom = client.nom,
            numTelephone = client.numTelephone,
            couleur = client.couleur,
            bonDuClientsSu = client.bonDuClientsSu,
            currentCreditBalance = client.currentCreditBalance,
            positionDonClientsList = client.positionDonClientsList,
            cUnClientTemporaire = client.cUnClientTemporaire,
            auFilterFAB = client.auFilterFAB,
            typeDeSonMagasine = convertTypeDeSonMagasine(client.typeDeSonMagasine),
            clientTypeMode = convertClientTypeMode(client.clientTypeMode),
            latitude = client.latitude,
            longitude = client.longitude,
            title = client.title,
            snippet = client.snippet,
            actuelleEtat = convertDernierEtatAAffiche(client.actuelleEtat)
        )
    }

    // Helper function to convert TypeDeSonMagasine enum
    private fun convertTypeDeSonMagasine(typeDeSonMagasine: Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.TypeDeSonMagasine): _3_ClientsDataBase.TypeDeSonMagasine {
        return when (typeDeSonMagasine) {
            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT ->
                _3_ClientsDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.TypeDeSonMagasine.AlIMENTATION_GENERALE ->
                _3_ClientsDataBase.TypeDeSonMagasine.AlIMENTATION_GENERALE
        }
    }

    // Helper function to convert ClientTypeMode enum
    private fun convertClientTypeMode(clientTypeMode: Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.ClientTypeMode): _3_ClientsDataBase.ClientTypeMode {
        return when (clientTypeMode) {
            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.ClientTypeMode.NEVEAU ->
                _3_ClientsDataBase.ClientTypeMode.NEVEAU

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.ClientTypeMode.ANCIEN ->
                _3_ClientsDataBase.ClientTypeMode.ANCIEN

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.ClientTypeMode.EVITE ->
                _3_ClientsDataBase.ClientTypeMode.EVITE
        }
    }

    // Helper function to convert DernierEtatAAffiche enum
    private fun convertDernierEtatAAffiche(etat: Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche): _3_ClientsDataBase.DernierEtatAAffiche {
        return when (etat) {
            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.NON_DEFINI ->
                _3_ClientsDataBase.DernierEtatAAffiche.NON_DEFINI

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT ->
                _3_ClientsDataBase.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.VENDU_A_LUI ->
                _3_ClientsDataBase.DernierEtatAAffiche.VENDU_A_LUI

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.Cible ->
                _3_ClientsDataBase.DernierEtatAAffiche.Cible

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2 ->
                _3_ClientsDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.CIBLE_POUR_2 ->
                _3_ClientsDataBase.DernierEtatAAffiche.CIBLE_POUR_2

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.ACHETEUR_NON_DISPO ->
                _3_ClientsDataBase.DernierEtatAAffiche.CLIENT_ABSENT

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.AVEC_MARCHANDISE ->
                _3_ClientsDataBase.DernierEtatAAffiche.AVEC_MARCHANDISE

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.FERME ->
                _3_ClientsDataBase.DernierEtatAAffiche.FERME

            Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.A_EVITE ->
                _3_ClientsDataBase.DernierEtatAAffiche.A_EVITE

            else -> {
                _3_ClientsDataBase.DernierEtatAAffiche.NON_DEFINI
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
                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.DernierEtatAAffiche.ACHETEUR_NON_DISPO

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
