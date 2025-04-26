package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Preview

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun addTestDataToFireBaseIfEmpty(
    viewModelScope: CoroutineScope,
    r_0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository
) {
    viewModelScope.launch(Dispatchers.IO) {
        // Add test period if needed
        if (r_0_0_HeadOfRepositorys_Repository.repositorys_Model.repository_1_4_PeriodeVent.modelDatasSnapList.isEmpty()) {
            val vendeurId = r_0_0_HeadOfRepositorys_Repository.currentVendeur?.vid ?: 0L
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            // Generate current week data
            // ---------------------------------------
            // Today
            val testPeriod1 = _1_4_PeriodeVent(
                vendeur_ParentVID = vendeurId,
                startDateInString = _1_4_PeriodeVent.getMainValeKey(),
                heurDebutInString = "08:00",
                endDateInString = "",
                etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
            )

            // Yesterday
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val yesterday = dateFormat.format(calendar.time)
            val testPeriod2 = _1_4_PeriodeVent(
                vendeur_ParentVID = vendeurId,
                startDateInString = yesterday,
                heurDebutInString = "09:00",
                endDateInString = "",
                etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
            )

            // Day before yesterday
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val dayBeforeYesterday = dateFormat.format(calendar.time)
            val testPeriod3 = _1_4_PeriodeVent(
                vendeur_ParentVID = vendeurId,
                startDateInString = dayBeforeYesterday,
                heurDebutInString = "10:00",
                endDateInString = "",
                etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.NA_PAS_COMMANDE
            )

            // Reset and get tomorrow
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val tomorrow = dateFormat.format(calendar.time)
            val testPeriod4 = _1_4_PeriodeVent(
                vendeur_ParentVID = vendeurId,
                startDateInString = tomorrow,
                heurDebutInString = "11:00",
                endDateInString = "",
                etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.ENTRE_MAIS_PAS_CONFIRME
            )

            // Generate last week data
            // ---------------------------------------
            calendar.time = Date()
            // Move to start of last week (1 week ago)
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val lastWeekMonday = dateFormat.format(calendar.time)
            val testPeriod5 = _1_4_PeriodeVent(
                vendeur_ParentVID = vendeurId,
                startDateInString = lastWeekMonday,
                heurDebutInString = "08:00",
                endDateInString = "17:00",
                etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
            )

            // Last week Wednesday
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
            val lastWeekWednesday = dateFormat.format(calendar.time)
            val testPeriod6 = _1_4_PeriodeVent(
                vendeur_ParentVID = vendeurId,
                startDateInString = lastWeekWednesday,
                heurDebutInString = "09:30",
                endDateInString = "18:30",
                etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
            )

            // Last week Friday
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
            val lastWeekFriday = dateFormat.format(calendar.time)
            val testPeriod7 = _1_4_PeriodeVent(
                vendeur_ParentVID = vendeurId,
                startDateInString = lastWeekFriday,
                heurDebutInString = "10:00",
                endDateInString = "16:00",
                etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
            )

            // Generate two weeks ago data
            // ---------------------------------------
            calendar.time = Date()
            calendar.add(Calendar.WEEK_OF_YEAR, -2)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
            val twoWeeksAgoTuesday = dateFormat.format(calendar.time)
            val testPeriod8 = _1_4_PeriodeVent(
                vendeur_ParentVID = vendeurId,
                startDateInString = twoWeeksAgoTuesday,
                heurDebutInString = "08:45",
                endDateInString = "15:30",
                etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
            )

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
            val twoWeeksAgoThursday = dateFormat.format(calendar.time)
            val testPeriod9 = _1_4_PeriodeVent(
                vendeur_ParentVID = vendeurId,
                startDateInString = twoWeeksAgoThursday,
                heurDebutInString = "09:15",
                endDateInString = "17:45",
                etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.NA_PAS_COMMANDE
            )

            // Generate one month ago data
            // ---------------------------------------
            calendar.time = Date()
            calendar.add(Calendar.MONTH, -1)
            calendar.set(Calendar.DAY_OF_MONTH, 15) // Middle of last month
            val oneMonthAgo = dateFormat.format(calendar.time)
            val testPeriod10 = _1_4_PeriodeVent(
                vendeur_ParentVID = vendeurId,
                startDateInString = oneMonthAgo,
                heurDebutInString = "10:30",
                endDateInString = "16:30",
                etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
            )

            // Add first period and create transaction for it (today)
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

            // Yesterday
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

            // Day before yesterday
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

            // Tomorrow
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

            // Last week Monday
            r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod5) { periodVid ->
                // Add transactions for last week Monday
                val transaction7 = _1_3_TransactionCommercial(
                    parentVID_1_4_PeriodeVent = periodVid,
                    clientAcheteurID = 2L,
                    heurDebutInString = "08:30",
                    heurFinInString = "10:15",
                    etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME,
                    fireBaseKeyID = "$periodVid->(2->(A_COMMANDE_CONFIRME))",
                    vid = 0L
                )
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction7)

                val transaction8 = _1_3_TransactionCommercial(
                    parentVID_1_4_PeriodeVent = periodVid,
                    clientAcheteurID = 2L,
                    heurDebutInString = "14:30",
                    heurFinInString = "16:00",
                    etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.AVEC_MARCHANDISE,
                    fireBaseKeyID = "$periodVid->(2->(AVEC_MARCHANDISE))",
                    vid = 0L
                )
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction8)
            }

            // Last week Wednesday
            r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod6) { periodVid ->
                // Add transactions for last week Wednesday
                val transaction9 = _1_3_TransactionCommercial(
                    parentVID_1_4_PeriodeVent = periodVid,
                    clientAcheteurID = 2L,
                    heurDebutInString = "09:45",
                    heurFinInString = "11:30",
                    etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME,
                    fireBaseKeyID = "$periodVid->(2->(A_COMMANDE_CONFIRME))",
                    vid = 0L
                )
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction9)

                // Also add client 1 transaction
                val transaction10 = _1_3_TransactionCommercial(
                    parentVID_1_4_PeriodeVent = periodVid,
                    clientAcheteurID = 1L,
                    heurDebutInString = "11:00",
                    heurFinInString = "12:30",
                    etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                    fireBaseKeyID = "$periodVid->(1->(ON_MODE_COMMEND_ACTUELLEMENT))",
                    vid = 0L
                )
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction10)
            }

            // Last week Friday
            r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod7) { periodVid ->
                // Add transactions for last week Friday
                val transaction11 = _1_3_TransactionCommercial(
                    parentVID_1_4_PeriodeVent = periodVid,
                    clientAcheteurID = 2L,
                    heurDebutInString = "10:15",
                    heurFinInString = "12:00",
                    etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.FERME,
                    fireBaseKeyID = "$periodVid->(2->(FERME))",
                    vid = 0L
                )
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction11)
            }

            // Two weeks ago Tuesday
            r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod8) { periodVid ->
                // Add transactions for two weeks ago Tuesday
                val transaction12 = _1_3_TransactionCommercial(
                    parentVID_1_4_PeriodeVent = periodVid,
                    clientAcheteurID = 2L,
                    heurDebutInString = "09:00",
                    heurFinInString = "10:45",
                    etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME,
                    fireBaseKeyID = "$periodVid->(2->(A_COMMANDE_CONFIRME))",
                    vid = 0L
                )
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction12)
            }

            // Two weeks ago Thursday
            r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod9) { periodVid ->
                // Add transactions for two weeks ago Thursday
                val transaction13 = _1_3_TransactionCommercial(
                    parentVID_1_4_PeriodeVent = periodVid,
                    clientAcheteurID = 2L,
                    heurDebutInString = "09:30",
                    heurFinInString = "11:15",
                    etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.A_EVITE,
                    fireBaseKeyID = "$periodVid->(2->(A_EVITE))",
                    vid = 0L
                )
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction13)
            }

            // One month ago
            r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod10) { periodVid ->
                // Add transactions for one month ago
                val transaction14 = _1_3_TransactionCommercial(
                    parentVID_1_4_PeriodeVent = periodVid,
                    clientAcheteurID = 2L,
                    heurDebutInString = "10:30",
                    heurFinInString = "12:15",
                    etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME,
                    fireBaseKeyID = "$periodVid->(2->(A_COMMANDE_CONFIRME))",
                    vid = 0L
                )
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction14)

                val transaction15 = _1_3_TransactionCommercial(
                    parentVID_1_4_PeriodeVent = periodVid,
                    clientAcheteurID = 2L,
                    heurDebutInString = "15:00",
                    heurFinInString = "16:30",
                    etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.AVEC_MARCHANDISE,
                    fireBaseKeyID = "$periodVid->(2->(AVEC_MARCHANDISE))",
                    vid = 0L
                )
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction15)
            }
        }
    }
}
