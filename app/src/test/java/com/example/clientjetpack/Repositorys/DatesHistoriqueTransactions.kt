package com.example.clientjetpack.Repositorys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DatesHistoriqueTransactions {
        var cesSemains by mutableStateOf<List<Semain>>(emptyList())

        class Semain {
            var vid by mutableStateOf(0L)
            var key by mutableStateOf("")
            var cActive by mutableStateOf(false)

            var cesJours by mutableStateOf<List<Jour>>(emptyList())

            class Jour {
                var vid by mutableStateOf(0L)
                var key by mutableStateOf("")
                var cActive by mutableStateOf(false)

                var cesCommercialTransactions by mutableStateOf<List<TransactionCommercial>>(emptyList())
            }
        }
    }

fun testHardDataDatesHistoriqueTransactions(): DatesHistoriqueTransactions {
    // Create a test hierarchical structure for DatesHistoriqueTransactions
    val testData = DatesHistoriqueTransactions()

    // Create week 1
    val week1 = DatesHistoriqueTransactions.Semain().apply {
        vid = 1L
        key = "Semaine-1"
        cActive = true

        // Create days for week 1
        val days1 = listOf(
            DatesHistoriqueTransactions.Semain.Jour().apply {
                vid = 101L
                key = "1_الأحد_1" // Sunday in Arabic
                cActive = true

                // Add transactions for this day
                cesCommercialTransactions = listOf(
                    TransactionCommercial(
                        vid = 1001L,
                        etateActuellementEst = TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
                        nomClientConcerned = "عميل 1"
                    ),
                    TransactionCommercial(
                        vid = 1002L,
                        etateActuellementEst = TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
                        nomClientConcerned = "عميل 2"
                    )
                )
            },
            DatesHistoriqueTransactions.Semain.Jour().apply {
                vid = 102L
                key = "1_الثلاثاء_1" // Tuesday in Arabic
                cActive = false

                // Add transactions for this day
                cesCommercialTransactions = listOf(
                    TransactionCommercial(
                        vid = 1003L,
                        etateActuellementEst = TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
                        nomClientConcerned = "عميل 3"
                    )
                )
            }
        )
        cesJours = days1
    }

    // Create week 2
    val week2 = DatesHistoriqueTransactions.Semain().apply {
        vid = 2L
        key = "Semaine-2"
        cActive = false

        // Create days for week 2
        val days2 = listOf(
            DatesHistoriqueTransactions.Semain.Jour().apply {
                vid = 201L
                key = "2_الإثنين_2" // Monday in Arabic
                cActive = false

                // Add transactions for this day
                cesCommercialTransactions = listOf(
                    TransactionCommercial(
                        vid = 2001L,
                        etateActuellementEst = TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
                        nomClientConcerned = "عميل 4"
                    ),
                    TransactionCommercial(
                        vid = 2002L,
                        etateActuellementEst = TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
                        nomClientConcerned = "عميل 5"
                    )
                )
            },
            DatesHistoriqueTransactions.Semain.Jour().apply {
                vid = 202L
                key = "2_الخميس_2" // Thursday in Arabic
                cActive = true

                // Add transactions for this day
                cesCommercialTransactions = listOf(
                    TransactionCommercial(
                        vid = 2003L,
                        etateActuellementEst = TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
                        nomClientConcerned = "عميل 6"
                    ),
                    TransactionCommercial(
                        vid = 2004L,
                        etateActuellementEst = TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
                        nomClientConcerned = "عميل 7"
                    )
                )
            }
        )
        cesJours = days2
    }

    // Add weeks to the test data
    testData.cesSemains = listOf(week1, week2)

    return testData
}
