package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9.Companion.getPushFireBase
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Stable
class B1CouleurOuGoutProduitDataBase_Repository(
    private val ancienRepo: DataBaseFactory_B1CouleurOuGoutProduitDataBase,
) {
    val dao = ancienRepo.dao

    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<B1CouleurOuGoutProduitDataBase>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun addOrUpdateData(data: B1CouleurOuGoutProduitDataBase) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            B1CouleurOuGoutProduitDataBase.compareEntre(ancien = ancien, newData = data)
        }
        _datas.value = if (existingIndex >= 0) {
            datasValue.toMutableList().apply {
                this[existingIndex] = this[existingIndex].copy(
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            }
        } else {
            datasValue + data
        }

        addOrUpdatedAncienRepo(existingIndex, data)
    }

    fun deleteData(data: B1CouleurOuGoutProduitDataBase) {
        _datas.value = datasValue.filter { existing ->
            !B1CouleurOuGoutProduitDataBase.compareEntre(ancien = existing, newData = data)
        }
        deleteDataAncienRepo(data)
    }

    private fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        data: B1CouleurOuGoutProduitDataBase
    ) {
        composScope.launch {
            ancienRepo.addOrUpdatedAncienRepo(existingIndex, data)
        }
    }

    private fun deleteDataAncienRepo(
        data: B1CouleurOuGoutProduitDataBase
    ) {
        composScope.launch {
            ancienRepo.deleteDataAncienRepo(data)
        }
    }
}

@Entity
data class B1CouleurOuGoutProduitDataBase(
    @PrimaryKey
    var id: String = "",

    var pushKey: String = getPushFireBase(ref),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    var parentBProduitOldID: Long? = null,
    var parentBProduitKeyID: String = "",

    ) {
    companion object {
        val ref =
            Firebase.database.getReference(
                "00_DataPrototype-04-02" +
                        "/_1_developingRef" +
                        "/C_InfosSqlDataBases" +
                        "/B1CouleurOuGoutProduitDataBase"
            )

        fun compareEntre(
            ancien: B1CouleurOuGoutProduitDataBase,
            newData: B1CouleurOuGoutProduitDataBase
        ): Boolean {
            val delimiterExistence =
                ancien.id == newData.id
            return delimiterExistence
        }
    }
}
