package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.C.Repository.BProto_ClientsDataBaseButton

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.C.Repository.B_ClientDataBaseProtoC
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.C.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.E._3_ClientsDataBase._3_ClientsDataBaseProtoE
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.D.Repository.B_ClientsDataBaseProtoD
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.ref_HeadOfModels
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
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
    val _0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3,
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
                            actuelleEtat = B_ClientDataBaseProtoC.DernierEtatAAffiche.NON_DEFINI
                        )
                    } else {
                        // Just upsertLenceCommandeRepoGroupedProtoAvanJuin3 the state for other clientAchteurs
                        client.copy(
                            actuelleEtat = B_ClientDataBaseProtoC.DernierEtatAAffiche.NON_DEFINI
                        )
                    }
                }

                // Convert to SnapshotStateList for the repository
                val snapshotList = SnapshotStateList<B_ClientDataBaseProtoC>()
                snapshotList.addAll(updatedClients)

                // Update the repository with all modified clientAchteurs
                mainRepo.updateMultiDatas(snapshotList)

                Log.d("ViewModel_BProto", "Successfully changed state of ${updatedClients.size} clientAchteurs to CIBLE_PRIORITE_2")
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
                    B_ClientDataBaseProtoC(
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
                            B_ClientsDataBaseProtoD.StatueDeBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT ->
                                B_ClientDataBaseProtoC.TypeDeSonMagasine.ATAYAT_MOUKASSARAT

                            B_ClientsDataBaseProtoD.StatueDeBase.TypeDeSonMagasine.AlIMENTATION_GENERALE ->
                                B_ClientDataBaseProtoC.TypeDeSonMagasine.AlIMENTATION_GENERALE

                            else ->
                                B_ClientDataBaseProtoC.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
                        },
                        clientTypeMode = when (ancienData.etatesMutable.clientTypeMode) {
                            B_ClientsDataBaseProtoD.EtatesMutable.ClientTypeMode.NEVEAU ->
                                B_ClientDataBaseProtoC.ClientTypeMode.NEVEAU

                            B_ClientsDataBaseProtoD.EtatesMutable.ClientTypeMode.ANCIEN ->
                                B_ClientDataBaseProtoC.ClientTypeMode.ANCIEN

                            B_ClientsDataBaseProtoD.EtatesMutable.ClientTypeMode.EVITE ->
                                B_ClientDataBaseProtoC.ClientTypeMode.EVITE
                        },
                        latitude = ancienData.gpsLocation.latitude,
                        longitude = ancienData.gpsLocation.longitude,
                        title = ancienData.gpsLocation.title,
                        snippet = ancienData.gpsLocation.snippet,
                        actuelleEtat = mapActuelleEtat(ancienData.gpsLocation.actuelleEtat)
                    )
                }

                // Convert to _3_ClientsDataBaseProtoE objects
                val convertedData = newDataList.map { client ->
                    convertToClientsDataBase(client)
                }

                // Create a SnapshotStateList from the converted data
                val snapshotList =
                    androidx.compose.runtime.snapshots.SnapshotStateList<_3_ClientsDataBaseProtoE>()
                snapshotList.addAll(convertedData)

                // Update repository with new data structure
                _0_0_HeadSQLRepositorys.repositorys_Model.repository_3_ClientsDataBase.addMultiDATAsEtReturnVIDsList(
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
                            _0_0_HeadSQLRepositorys.repositorys_Model.repository_3_ClientsDataBase.updateMultiDatas(
                                snapshotList
                            )
                            Log.d(
                                "ViewModel_BProto",
                                "Successfully migrated ${newDataList.size} clientAchteurs to _3_ClientsDataBase_Repository"
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

    // Helper function to convert B_ClientInfos to _3_ClientsDataBaseProtoE
    private fun convertToClientsDataBase(client: B_ClientDataBaseProtoC): _3_ClientsDataBaseProtoE {
        return _3_ClientsDataBaseProtoE(
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
    private fun convertTypeDeSonMagasine(typeDeSonMagasine: B_ClientDataBaseProtoC.TypeDeSonMagasine): _3_ClientsDataBaseProtoE.TypeDeSonMagasine {
        return when (typeDeSonMagasine) {
            B_ClientDataBaseProtoC.TypeDeSonMagasine.ATAYAT_MOUKASSARAT ->
                _3_ClientsDataBaseProtoE.TypeDeSonMagasine.ATAYAT_MOUKASSARAT

            B_ClientDataBaseProtoC.TypeDeSonMagasine.AlIMENTATION_GENERALE ->
                _3_ClientsDataBaseProtoE.TypeDeSonMagasine.AlIMENTATION_GENERALE
        }
    }

    // Helper function to convert ClientTypeMode enum
    private fun convertClientTypeMode(clientTypeMode: B_ClientDataBaseProtoC.ClientTypeMode): _3_ClientsDataBaseProtoE.ClientTypeMode {
        return when (clientTypeMode) {
            B_ClientDataBaseProtoC.ClientTypeMode.NEVEAU ->
                _3_ClientsDataBaseProtoE.ClientTypeMode.NEVEAU

            B_ClientDataBaseProtoC.ClientTypeMode.ANCIEN ->
                _3_ClientsDataBaseProtoE.ClientTypeMode.ANCIEN

            B_ClientDataBaseProtoC.ClientTypeMode.EVITE ->
                _3_ClientsDataBaseProtoE.ClientTypeMode.EVITE
        }
    }

    // Helper function to convert DernierEtatAAffiche enum
    private fun convertDernierEtatAAffiche(etat: B_ClientDataBaseProtoC.DernierEtatAAffiche): _3_ClientsDataBaseProtoE.DernierEtatAAffiche {
        return when (etat) {
            B_ClientDataBaseProtoC.DernierEtatAAffiche.NON_DEFINI ->
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.NON_DEFINI

            B_ClientDataBaseProtoC.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT ->
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT

            B_ClientDataBaseProtoC.DernierEtatAAffiche.VENDU_A_LUI ->
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.VENDU_A_LUI

            B_ClientDataBaseProtoC.DernierEtatAAffiche.Cible ->
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.Cible

            B_ClientDataBaseProtoC.DernierEtatAAffiche.CIBLE_PRIORITE_2 ->
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.CIBLE_PRIORITE_2

            B_ClientDataBaseProtoC.DernierEtatAAffiche.CIBLE_POUR_2 ->
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.CIBLE_POUR_2

            B_ClientDataBaseProtoC.DernierEtatAAffiche.ACHETEUR_NON_DISPO ->
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.CLIENT_ABSENT

            B_ClientDataBaseProtoC.DernierEtatAAffiche.AVEC_MARCHANDISE ->
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.AVEC_MARCHANDISE

            B_ClientDataBaseProtoC.DernierEtatAAffiche.FERME ->
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.FERME

            B_ClientDataBaseProtoC.DernierEtatAAffiche.A_EVITE ->
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.A_EVITE

            else -> {
                _3_ClientsDataBaseProtoE.DernierEtatAAffiche.NON_DEFINI
            }
        }
    }

    // Helper function to map the old DernierEtatAAffiche enum to the new one
    private fun mapActuelleEtat(oldEtat: B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche?): B_ClientDataBaseProtoC.DernierEtatAAffiche {
        return when (oldEtat) {
            B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche.آNON_DEFINI ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.NON_DEFINI

            B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT

            B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche.VENDU_A_LUI ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.VENDU_A_LUI

            B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche.Cible ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.Cible

            B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche.CIBLE_PRIORITE_2 ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.CIBLE_PRIORITE_2

            B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche.CIBLE_POUR_2 ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.CIBLE_POUR_2

            B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche.CLIENT_ABSENT ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.ACHETEUR_NON_DISPO

            B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche.AVEC_MARCHANDISE ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.AVEC_MARCHANDISE

            B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche.FERME ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.FERME

            B_ClientsDataBaseProtoD.GpsLocation.DernierEtatAAffiche.A_EVITE ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.A_EVITE

            null ->
                B_ClientDataBaseProtoC.DernierEtatAAffiche.NON_DEFINI
        }
    }

    suspend fun getAncienDataBase(): List<B_ClientsDataBaseProtoD> =
        withContext(
            Dispatchers.IO
        ) {
            return@withContext try {
                ref_HeadOfModels.child("B_ClientsDataBaseProtoD")
                    .get()
                    .await()
                    .children
                    .mapNotNull {
                        it.getValue(B_ClientsDataBaseProtoD::class.java)
                    }
            } catch (e: Exception) {
                Log.e("TAG", "Error retrieving ancien dataB: ${e.message}", e)
                emptyList()
            }
        }

}
