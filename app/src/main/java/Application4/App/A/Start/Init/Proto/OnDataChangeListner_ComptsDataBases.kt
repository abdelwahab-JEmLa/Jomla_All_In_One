package Application4.App.A.Start.Init.Proto

import EntreApps.Shared.Models.Z_AppCompt
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object OnDataChangeListner_ComptsDataBases {
    fun getFlow_ListCompts(): Flow<List<Z_AppCompt>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children
                    .mapNotNull { it.getValue(Z_AppCompt::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        Z_AppCompt.ref.addValueEventListener(listener)
        awaitClose { Z_AppCompt.ref.removeEventListener(listener) }
    }
}
