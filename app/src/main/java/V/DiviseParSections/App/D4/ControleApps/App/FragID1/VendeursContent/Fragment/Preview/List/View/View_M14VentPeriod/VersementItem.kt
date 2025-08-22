package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A.TransactionItem
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Loads transactions and versements for a specific period based on timestamp range
 * and filters by grossists that are active in the given period.
 */
suspend fun loadPeriodTransactions(
    periodStartTimestamp: Long,
    periodEndTimestamp: Long,
    ventPeriodKeyID: String,
    repositorysMainGetter: RepositorysMainGetter,
    onTransactionsLoaded: (List<TransactionItem>, List<VersementItem>) -> Unit,
    onError: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        val transactions = mutableListOf<TransactionItem>()
        val versements = mutableListOf<VersementItem>()
        var completedQueries = 0
        val totalQueries = 2

        fun checkComplete() {
            completedQueries++
            if (completedQueries == totalQueries) {
                onTransactionsLoaded(transactions, versements)
            }
        }

        fun isTimestampInPeriod(timestamp: Long): Boolean {
            return timestamp >= periodStartTimestamp && timestamp < periodEndTimestamp
        }

        try {
            // Load TransactionItems
            val transactionListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    transactions.clear()
                    for (child in snapshot.children) {
                        try {
                            val transaction = child.getValue(TransactionItem::class.java)
                            transaction?.let {
                                // Filter by timestamp and check if grossist is active in this period
                                if (isTimestampInPeriod(it.timestamp) &&
                                    isGrossistActiveInPeriod(
                                        it.parent_GrossistKeyID,
                                        ventPeriodKeyID,
                                        repositorysMainGetter
                                    )
                                ) {
                                    transactions.add(it)
                                }
                            }
                        } catch (e: Exception) {
                            // Handle parsing error silently
                        }
                    }
                    checkComplete()
                }

                override fun onCancelled(error: DatabaseError) {
                    onError("Erreur lors du chargement des transactions: ${error.message}")
                }
            }

            TransactionItem.ref.addListenerForSingleValueEvent(transactionListener)

            // Load VersementItems
            val versementRef = TransactionItem.ref.parent?.child("VersementItem")
            val versementListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    versements.clear()
                    for (child in snapshot.children) {
                        try {
                            val versementValue = child.child("versement").getValue(Double::class.java)
                            val grossistKeyID = child.child("parent_GrossistKeyID").getValue(String::class.java)
                            val timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                            val date = child.child("date").getValue(String::class.java) ?: ""
                            val time = child.child("time").getValue(String::class.java) ?: ""
                            val id = child.child("id").getValue(String::class.java) ?: child.key ?: ""

                            if (versementValue != null && grossistKeyID != null) {
                                // Filter by timestamp and check if grossist is active in this period
                                if (isTimestampInPeriod(timestamp) &&
                                    isGrossistActiveInPeriod(
                                        grossistKeyID,
                                        ventPeriodKeyID,
                                        repositorysMainGetter
                                    )
                                ) {
                                    val versement = VersementItem(
                                        id = id,
                                        parent_GrossistKeyID = grossistKeyID,
                                        versement = versementValue,
                                        date = date,
                                        time = time,
                                        timestamp = timestamp
                                    )
                                    versements.add(versement)
                                }
                            }
                        } catch (e: Exception) {
                            // Handle parsing error silently
                        }
                    }
                    checkComplete()
                }

                override fun onCancelled(error: DatabaseError) {
                    onError("Erreur lors du chargement des versements: ${error.message}")
                }
            }

            versementRef?.addListenerForSingleValueEvent(versementListener)
                ?: run {
                    // If VersementItem reference is null, just complete with transactions
                    checkComplete()
                }

        } catch (e: Exception) {
            onError("Erreur générale: ${e.message}")
        }
    }
}
