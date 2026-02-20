package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

// Helper function to load credit for a specific grossist
 fun loadCreditForGrossist(
    grossistKeyID: String,
    onCreditLoaded: (Double) -> Unit
): List<ValueEventListener> {
    var transactionTotal = 0.0
    var versementTotal = 0.0
    var completedQueries = 0
    val totalQueries = 2
    val listeners = mutableListOf<ValueEventListener>()

    fun checkComplete() {
        completedQueries++
        if (completedQueries == totalQueries) {
            onCreditLoaded(transactionTotal - versementTotal)
        }
    }

    // Load TransactionItems for this grossist
    val transactionListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            transactionTotal = 0.0
            for (child in snapshot.children) {
                try {
                    val transaction = child.getValue(TransactionItem::class.java)
                    transaction?.let { transactionTotal += it.credit }
                } catch (e: Exception) {
                    // Handle parsing error
                }
            }
            checkComplete()
        }

        override fun onCancelled(error: DatabaseError) {
            checkComplete()
        }
    }
    M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

    TransactionItem.ref
        .orderByChild("parent_GrossistKeyID")
        .equalTo(grossistKeyID)
        .addValueEventListener(transactionListener)

    listeners.add(transactionListener) }

    // Load VersementItems for this grossist
    try {
        val versementRef = TransactionItem.ref.parent?.child("VersementItem")
        val versementListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                versementTotal = 0.0
                for (child in snapshot.children) {
                    try {
                        val versement = child.child("versement").getValue(Double::class.java)
                        versement?.let { versementTotal += it }
                    } catch (e: Exception) {
                        // Handle parsing error
                    }
                }
                checkComplete()
            }

            override fun onCancelled(error: DatabaseError) {
                checkComplete()
            }
        }
        M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

        versementRef?.orderByChild("parent_GrossistKeyID")
            ?.equalTo(grossistKeyID)
            ?.addValueEventListener(versementListener)

        listeners.add(versementListener)    }

    } catch (e: Exception) {
        // If VersementItem loading fails, just complete with transaction total
        checkComplete()
    }

    return listeners
}
