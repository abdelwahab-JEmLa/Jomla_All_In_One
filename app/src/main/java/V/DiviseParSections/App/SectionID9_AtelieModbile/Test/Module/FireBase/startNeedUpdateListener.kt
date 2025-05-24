package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.F_FireBaseOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.mapFromFirebaseSnapshot
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A0_DataBasesGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

fun F_FireBaseOperationsHandler.startNeedUpdateListener() {
    if (needUpdateListener != null) return

    needUpdateListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (!snapshot.exists()) return

            coroutineScope.launch {
                try {
                    val needsUpdate = checkIfNeedsUpdate(snapshot)
                    if (needsUpdate) {
                        val firebaseData = mapFromFirebaseSnapshot(snapshot)
                        resetNeedUpdateFlags(firebaseData)
                    }
                } catch (e: Exception) {
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    ref.addValueEventListener(needUpdateListener!!)
}

private fun checkIfNeedsUpdate(snapshot: DataSnapshot): Boolean {
    val defaultModel = A0_DataBasesGroup()

    val produitsSnapshot = snapshot.child(defaultModel.refFireBaseA_ProduitInfos)
    if (produitsSnapshot.exists()) {
        for (productSnap in produitsSnapshot.children) {
            val needUpdateSnapshot = productSnap.child("needUpdate")
            if (needUpdateSnapshot.exists() && needUpdateSnapshot.getValue(Boolean::class.java) == true) {
                return true
            }
        }
    }

    val clientsSnapshot = snapshot.child(defaultModel.refFireBaseB_ClientInfos)
    if (clientsSnapshot.exists()) {
        for (clientSnap in clientsSnapshot.children) {
            val needUpdateSnapshot = clientSnap.child("needUpdate")
            if (needUpdateSnapshot.exists() && needUpdateSnapshot.getValue(Boolean::class.java) == true) {
                return true
            }
        }
    }

    val typeTarifsSnapshot = snapshot.child(defaultModel.refFireBaseC_TypeTarificationInfos)
    if (typeTarifsSnapshot.exists()) {
        for (typeSnap in typeTarifsSnapshot.children) {
            val needUpdateSnapshot = typeSnap.child("needUpdate")
            if (needUpdateSnapshot.exists() && needUpdateSnapshot.getValue(Boolean::class.java) == true) {
                return true
            }
        }
    }

    val tarifsSnapshot = snapshot.child(defaultModel.refFireBaseD_TarificationInfos)
    if (tarifsSnapshot.exists()) {
        for (tarifSnap in tarifsSnapshot.children) {
            val needUpdateSnapshot = tarifSnap.child("needUpdate")
            if (needUpdateSnapshot.exists() && needUpdateSnapshot.getValue(Boolean::class.java) == true) {
                return true
            }
        }
    }

    return false
}

fun F_FireBaseOperationsHandler.resetNeedUpdateFlags(data: A0_DataBasesGroup) {
}
