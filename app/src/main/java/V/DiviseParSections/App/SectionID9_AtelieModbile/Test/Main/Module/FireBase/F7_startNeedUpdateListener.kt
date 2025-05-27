package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.Module.FireBase

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.B.Models.A0_DataBasesGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

fun F0_FireBaseOperationsHandler.startNeedUpdateListener() {
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

fun F0_FireBaseOperationsHandler.resetNeedUpdateFlags(data: A0_DataBasesGroup) {
}
