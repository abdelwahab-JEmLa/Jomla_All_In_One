package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A.TransactionItem
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Updated loadCalculatedAchatTotals function with timestamp filtering
fun loadCalculatedAchatTotals(
    ventPeriodKeyID: String,
    repositorysMainGetter: RepositorysMainGetter,
    onTotalLoaded: (Double) -> Unit
) {
    var totalCredits = 0.0
    var totalVersements = 0.0
    var completedQueries = 0
    val totalQueries = 2

    // Find the current and next vent periods to determine timestamp range
    val currentPeriod = repositorysMainGetter.repo14VentPeriode.datasValue
        .firstOrNull { it.keyID == ventPeriodKeyID }

    if (currentPeriod == null) {
        onTotalLoaded(0.0)
        return
    }

    // Get all periods for the same parent, sorted by creation timestamp
    val allPeriodsForSameParent = repositorysMainGetter.repo14VentPeriode.datasValue
        .filter { it.parent_M9AppCompt_KeyID == currentPeriod.parent_M9AppCompt_KeyID }
        .sortedBy { it.creationTimestamp }

    // Find the timestamp range for this period
    val currentPeriodIndex = allPeriodsForSameParent.indexOfFirst { it.keyID == ventPeriodKeyID }
    val periodStartTimestamp = currentPeriod.creationTimestamp
    val periodEndTimestamp = if (currentPeriodIndex < allPeriodsForSameParent.size - 1) {
        // Not the last period, use next period's start timestamp
        allPeriodsForSameParent[currentPeriodIndex + 1].creationTimestamp
    } else {
        // Last period, use current timestamp
        System.currentTimeMillis()
    }

    fun checkComplete() {
        completedQueries++
        if (completedQueries == totalQueries) {
            onTotalLoaded(totalCredits - totalVersements)
        }
    }

    // Helper function to check if a timestamp is within the period range
    fun isTimestampInPeriod(timestamp: Long): Boolean {
        return timestamp >= periodStartTimestamp && timestamp < periodEndTimestamp
    }

    // Load TransactionItems filtered by timestamp and active grossists
    val transactionListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            totalCredits = 0.0
            for (child in snapshot.children) {
                try {
                    val transaction = child.getValue(TransactionItem::class.java)
                    transaction?.let {
                        // Filter by timestamp first, then check if grossist is active in this period
                        if (isTimestampInPeriod(it.timestamp) &&
                            isGrossistActiveInPeriod(
                                it.parent_GrossistKeyID,
                                ventPeriodKeyID,
                                repositorysMainGetter
                            )
                        ) {
                            totalCredits += it.credit
                        }
                    }
                } catch (e: Exception) {
                    // Handle parsing error silently
                }
            }
            checkComplete()
        }

        override fun onCancelled(error: DatabaseError) {
            checkComplete()
        }
    }

    TransactionItem.Companion.ref.addListenerForSingleValueEvent(transactionListener)

    // Load VersementItems filtered by timestamp and active grossists
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val versementRef = TransactionItem.Companion.ref.parent?.child("VersementItem")
            val versementListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    totalVersements = 0.0
                    for (child in snapshot.children) {
                        try {
                            val versement = child.child("versement").getValue(Double::class.java)
                            val grossistKeyID =
                                child.child("parent_GrossistKeyID").getValue(String::class.java)
                            val timestamp =
                                child.child("timestamp").getValue(Long::class.java) ?: 0L

                            if (versement != null && grossistKeyID != null) {
                                // Filter by timestamp first, then check if grossist is active in this period
                                if (isTimestampInPeriod(timestamp) &&
                                    isGrossistActiveInPeriod(
                                        grossistKeyID,
                                        ventPeriodKeyID,
                                        repositorysMainGetter
                                    )
                                ) {
                                    totalVersements += versement
                                }
                            }
                        } catch (e: Exception) {
                            // Handle parsing error silently
                        }
                    }
                    checkComplete()
                }

                override fun onCancelled(error: DatabaseError) {
                    checkComplete()
                }
            }

            versementRef?.addListenerForSingleValueEvent(versementListener)
        } catch (e: Exception) {
            // If VersementItem loading fails, just complete with transaction total
            checkComplete()
        }
    }
}
