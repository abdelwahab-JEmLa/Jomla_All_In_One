package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.Type
import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("MutableCollectionMutableState")
class D_SqlDatasDatesHistoriqueTransactions_Repository(
    datesHistoriqueForTesting: D_MapsIDSDatesHistoriqueTransactionsRep_Repository,
    private val testTransactions: List<D_TransactionCommercial_Repository>? = null
) {
    var semaines by mutableStateOf<MutableList<Semaine>>(mutableListOf())
    var jours by mutableStateOf<MutableList<Jour>>(mutableListOf())
    var clients by mutableStateOf<MutableList<Client>>(mutableListOf())
    var transactions by mutableStateOf<MutableList<Transaction>>(mutableListOf())

    init {
        // Initialize weeks data
        datesHistoriqueForTesting.semaines.keys
            .sortedByDescending { it }
            .forEach { weekTimestamp ->
            val weekItem = Semaine()
            weekItem.vidTimeTemp = weekTimestamp
            weekItem.updateWeekNumber()
            semaines.add(weekItem)
        }

        // Initialize days data
        datesHistoriqueForTesting.jours.keys
            .sortedByDescending { it }
            .forEach { dayTimestamp ->
            val dayItem = Jour()
            dayItem.vidTimeTemp = dayTimestamp
            dayItem.updateDateStr()
            jours.add(dayItem)
        }

        // Build a map of transaction IDs to client IDs for more efficient lookups
        val transactionToClientMap = mutableMapOf<Long, Long>()
        testTransactions
            ?.forEach { transaction ->
            transactionToClientMap[transaction.vid] = transaction.clientAcheteurID
        }

        // Initialize clients data
        datesHistoriqueForTesting.clients.keys
            .forEach { clientId ->
            val clientItem = Client()
            clientItem.vidTimeTemp = clientId

            // Find client transactions
            val clientTransactions = testTransactions?.filter { it.clientAcheteurID == clientId }

            // Update client name from transactions
            clientItem.nom = clientTransactions?.firstOrNull()?.nomClientConcerned ?: "Client $clientId"

            // Set the oldest transaction ID as ancientIdTransaction
            if (!clientTransactions.isNullOrEmpty()) {
                clientItem.ancientIdTransaction = clientTransactions.minByOrNull { it.timestamps }?.vid ?: 0L
            }

            clients.add(clientItem)
        }

        // Initialize transactions data
        datesHistoriqueForTesting.transactions.forEach { (transactionId, transactionType) ->
            // Find the original transaction to get the correct timestamp
            val originalTransaction = testTransactions?.find { it.vid == transactionId }
            val transactionItem = Transaction()
            transactionItem.vidTimeTemp = transactionId

            // Set client ID from the map we built
            transactionItem.clientId = transactionToClientMap[transactionId] ?: 0L

            // Use the timestamp from the original transaction if found
            transactionItem.timestamp = originalTransaction?.timestamps ?: 0L
            transactionItem.updateTempStr()
            transactionItem.etate = transactionType
            transactions.add(transactionItem)
        }
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

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateStr = dateFormat.format(Date(vidTimeTemp))
        }
    }

    class Client {
        var vidTimeTemp by mutableStateOf(0L)
        var nom by mutableStateOf("Client 0")
        var ancientIdTransaction by mutableStateOf(0L)
    }

    class Transaction {
        var vidTimeTemp by mutableStateOf(0L)
        var timestamp by mutableStateOf(0L) // Store the actual timestamp
        var clientId by mutableStateOf(0L)  // Store the client ID directly
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
