package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Preview

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun addTestDataToFireBaseIfEmpty(
    viewModelScope: CoroutineScope,
    r_0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository
) {
        viewModelScope.launch(Dispatchers.IO) {
            // Add test period if needed
            if (r_0_0_HeadOfRepositorys_Repository.repositorys_Model.repository_1_4_PeriodeVent.modelDatasSnapList.isEmpty()) {
                // First test period (today)
                val testPeriod1 = _1_4_PeriodeVent(
                    vendeur_ParentVID = r_0_0_HeadOfRepositorys_Repository.currentVendeur?.vid ?: 0L,
                    startDateInString = _1_4_PeriodeVent.getMainValeKey(),
                    heurDebutInString = "08:00",
                    endDateInString = "",
                    etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
                )

                // Second test period (yesterday)
                val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                )
                val testPeriod2 = _1_4_PeriodeVent(
                    vendeur_ParentVID = r_0_0_HeadOfRepositorys_Repository.currentVendeur?.vid ?: 0L,
                    startDateInString = yesterday,
                    heurDebutInString = "09:00",
                    endDateInString = "",
                    etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
                )

                // Third test period (day before yesterday)
                val dayBeforeYesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)
                )
                val testPeriod3 = _1_4_PeriodeVent(
                    vendeur_ParentVID = r_0_0_HeadOfRepositorys_Repository.currentVendeur?.vid ?: 0L,
                    startDateInString = dayBeforeYesterday,
                    heurDebutInString = "10:00",
                    endDateInString = "",
                    etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.NA_PAS_COMMANDE
                )

                // Fourth test period (tomorrow)
                val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)
                )
                val testPeriod4 = _1_4_PeriodeVent(
                    vendeur_ParentVID = r_0_0_HeadOfRepositorys_Repository.currentVendeur?.vid ?: 0L,
                    startDateInString = tomorrow,
                    heurDebutInString = "11:00",
                    endDateInString = "",
                    etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.ENTRE_MAIS_PAS_CONFIRME
                )

                // Add first period and create transaction for it
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod1) { periodVid ->
                    // Add test transaction using the period ID
                    val testTransaction = _1_3_TransactionCommercial(
                        parentVID_1_4_PeriodeVent = periodVid,
                        clientAcheteurID = 1L,
                        heurDebutInString = "09:00",
                        heurFinInString = "10:30",
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                        fireBaseKeyID = "$periodVid->(1->(ON_MODE_COMMEND_ACTUELLEMENT))",
                        vid = 0L
                    )
                    r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(testTransaction)

                    // Add additional transaction for client ID 2 - different states, same period (today)
                    val transaction1 = _1_3_TransactionCommercial(
                        parentVID_1_4_PeriodeVent = periodVid,
                        clientAcheteurID = 2L,
                        heurDebutInString = "08:30",
                        heurFinInString = "09:45",
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                        fireBaseKeyID = "$periodVid->(2->(ON_MODE_COMMEND_ACTUELLEMENT))",
                        vid = 0L
                    )
                    r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction1)

                    val transaction2 = _1_3_TransactionCommercial(
                        parentVID_1_4_PeriodeVent = periodVid,
                        clientAcheteurID = 2L,
                        heurDebutInString = "11:00",
                        heurFinInString = "12:15",
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME,
                        fireBaseKeyID = "$periodVid->(2->(A_COMMANDE_CONFIRME))",
                        vid = 0L
                    )
                    r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction2)
                }

                // Add remaining periods
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod2) { periodVid ->
                    val transaction = _1_3_TransactionCommercial(
                        parentVID_1_4_PeriodeVent = periodVid,
                        clientAcheteurID = 2L,
                        heurDebutInString = "10:00",
                        heurFinInString = "11:45",
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME,
                        fireBaseKeyID = "$periodVid->(2->(A_COMMANDE_CONFIRME))",
                        vid = 0L
                    )
                    r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction)

                    // Add additional transaction for client ID 2 - yesterday period with different state
                    val transaction3 = _1_3_TransactionCommercial(
                        parentVID_1_4_PeriodeVent = periodVid,
                        clientAcheteurID = 2L,
                        heurDebutInString = "14:00",
                        heurFinInString = "15:30",
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.AVEC_MARCHANDISE,
                        fireBaseKeyID = "$periodVid->(2->(AVEC_MARCHANDISE))",
                        vid = 0L
                    )
                    r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction3)
                }

                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod3) { periodVid ->
                    // Add transactions for client ID 2 - day before yesterday with different states
                    val transaction4 = _1_3_TransactionCommercial(
                        parentVID_1_4_PeriodeVent = periodVid,
                        clientAcheteurID = 2L,
                        heurDebutInString = "09:30",
                        heurFinInString = "11:00",
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.ACHETEUR_NON_DISPO,
                        fireBaseKeyID = "$periodVid->(2->(ACHETEUR_NON_DISPO))",
                        vid = 0L
                    )
                    r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction4)
                }

                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod4) { periodVid ->
                    // Add transactions for client ID 2 - tomorrow with different states
                    val transaction5 = _1_3_TransactionCommercial(
                        parentVID_1_4_PeriodeVent = periodVid,
                        clientAcheteurID = 2L,
                        heurDebutInString = "10:30",
                        heurFinInString = "Non Defini",
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.COMMANDE_LENCE,
                        fireBaseKeyID = "$periodVid->(2->(COMMANDE_LENCE))",
                        vid = 0L
                    )
                    r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction5)

                    val transaction6 = _1_3_TransactionCommercial(
                        parentVID_1_4_PeriodeVent = periodVid,
                        clientAcheteurID = 2L,
                        heurDebutInString = "16:00",
                        heurFinInString = "Non Defini",
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.FERME,
                        fireBaseKeyID = "$periodVid->(2->(FERME))",
                        vid = 0L
                    )
                    r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction6)
                }
            }
        }
    }
