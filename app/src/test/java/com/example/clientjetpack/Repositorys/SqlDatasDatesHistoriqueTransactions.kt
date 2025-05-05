package com.example.clientjetpack.Repositorys

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("MutableCollectionMutableState")
class SqlDatasDatesHistoriqueTransactions(
    datesHistoriqueForTesting: MapsIDSDatesHistoriqueTransactions,
    private val testTransactions: List<TransactionCommercial>? = null
) {
    var semaines by mutableStateOf<MutableList<Semaine>>(mutableListOf())
    var jours by mutableStateOf<MutableList<Jour>>(mutableListOf())
    var clients by mutableStateOf<MutableList<Client>>(mutableListOf())
    var transactions by mutableStateOf<MutableList<Transaction>>(mutableListOf())

    init {
        // Initialize weeks data
        datesHistoriqueForTesting.semaines.keys.forEach { weekTimestamp ->
            val weekItem = Semaine()
            weekItem.vidTimeTemp = weekTimestamp
            weekItem.updateWeekNumber()
            semaines.add(weekItem)
        }

        // Initialize days data
        datesHistoriqueForTesting.jours.keys.forEach { dayTimestamp ->
            val dayItem = Jour()
            dayItem.vidTimeTemp = dayTimestamp
            dayItem.updateDateStr()
            jours.add(dayItem)
        }

        // Initialize clients data
        datesHistoriqueForTesting.clients.keys.forEach { clientId ->
            val clientItem = Client()
            clientItem.vidTimeTemp = clientId
            clientItem.updateNom(testTransactions)
            clients.add(clientItem)
        }

        // Initialize transactions data
        datesHistoriqueForTesting.transactions.forEach { (transactionId, transactionType) ->
            // Find the original transaction to get the correct timestamp
            val originalTransaction = findOriginalTransaction(transactionId)
            val transactionItem = Transaction()
            transactionItem.vidTimeTemp = transactionId
            // Use the timestamp from the original transaction if found
            if (originalTransaction != null) {
                transactionItem.timestamp = originalTransaction.timestamps
            }
            transactionItem.updateTempStr()
            transactionItem.etate = transactionType
            transactions.add(transactionItem)
        }
    }

    // Find the original transaction by its ID
    private fun findOriginalTransaction(transactionId: Long): TransactionCommercial? {
        return testTransactions?.find { it.vid == transactionId }
    }

    class Semaine {
        var vidTimeTemp by mutableStateOf(0L)
        var semainCountDonSonAnne by mutableStateOf(0)

        // Calculate week number when timestamp changes
        fun updateWeekNumber() {
            if (vidTimeTemp == 0L) return

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = vidTimeTemp
            semainCountDonSonAnne = calendar.get(Calendar.WEEK_OF_YEAR)
        }
    }

    class Jour {
        var vidTimeTemp by mutableStateOf(0L)
        var dateStr by mutableStateOf("N/A")

        // Update date string when timestamp changes
        fun updateDateStr() {
            if (vidTimeTemp == 0L) return

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateStr = dateFormat.format(Date(vidTimeTemp))
        }
    }

    class Client {
        var vidTimeTemp by mutableStateOf(0L)
        var nom by mutableStateOf("Client 0")

        // Update client name when ID changes
        fun updateNom(transactions: List<TransactionCommercial>? = null) {
            // Find transactions associated with this client ID
            val clientTransaction = transactions?.find { it.clientAcheteurID == vidTimeTemp }

            // Use the client name from transaction if found, otherwise use default format
            nom = if (clientTransaction != null) {
                clientTransaction.nomClientConcerned
            } else {
                "Client $vidTimeTemp"
            }
        }
    }

    class Transaction {
        var vidTimeTemp by mutableStateOf(0L)
        var timestamp by mutableStateOf(0L) // Store the actual timestamp
        var tempStr by mutableStateOf("N/A")
        var etate by mutableStateOf<Type>(Type.NON_DEFINI)

        // Update time string when timestamp changes
        fun updateTempStr() {
            // Use the transaction's timestamp instead of its ID
            if (timestamp > 0) {
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                tempStr = dateFormat.format(Date(timestamp))
            } else {
                tempStr = "N/A"
            }
        }
    }
}
