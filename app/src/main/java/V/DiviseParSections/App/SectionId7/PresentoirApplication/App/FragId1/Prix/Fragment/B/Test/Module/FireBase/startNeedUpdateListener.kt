package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.Module.FireBase

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

fun FireBaseOperationsHandler.startNeedUpdateListener() {
    if (needUpdateListener != null) return

    needUpdateListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (!snapshot.exists()) return

            coroutineScope.launch {
                try {
                    val needsUpdate = checkIfNeedsUpdate(snapshot)
                    if (needsUpdate) {
                        val firebaseData = mapFromFirebaseSnapshot(snapshot)
                        roomOperationsHandler.updateData(firebaseData)
                        resetNeedUpdateFlags(firebaseData)
                    }
                } catch (e: Exception) { }
            }
        }

        override fun onCancelled(error: DatabaseError) { }
    }

    ref.addValueEventListener(needUpdateListener!!)
}

fun FireBaseOperationsHandler.stopNeedUpdateListener() {
    needUpdateListener?.let {
        ref.removeEventListener(it)
        needUpdateListener = null
    }
}

private fun checkIfNeedsUpdate(snapshot: DataSnapshot): Boolean {
    val defaultModel = DataBasesInfosSql()

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

fun FireBaseOperationsHandler.resetNeedUpdateFlags(data: DataBasesInfosSql) {
    val updatedData = DataBasesInfosSql(
        a_ProduitInfos = data.a_ProduitInfos.map { it.copy(needUpdate = false) }.toMutableList(),
        b_ClientInfos = data.b_ClientInfos.map { it.copy(needUpdate = false) }.toMutableList(),
        c_TypeTarificationInfos = data.c_TypeTarificationInfos.map { it.copy(needUpdate = false) }.toMutableList(),
        d_TarificationInfos = data.d_TarificationInfos.map { it.copy(needUpdate = false) }.toMutableList()
    )

    addToFirebaseAsync(updatedData)
}
