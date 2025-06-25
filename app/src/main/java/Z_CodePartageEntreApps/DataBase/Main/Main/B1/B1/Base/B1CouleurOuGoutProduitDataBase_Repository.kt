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
    val mainInitDataBase: DataBaseFactory_B1CouleurOuGoutProduitDataBase,
) {
    val dao = mainInitDataBase.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<B1CouleurOuGoutProduitDataBase>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            // Load initial data from database
            val initialData = dao.getAll()
            _datas.value = initialData

            // Then start collecting flow updates
            dao.getAllFlow().collect { newData ->
                _datas.value = newData
                println("B1CouleurOuGoutProduitDataBase_Repository: Data updated, size: ${newData.size}")
            }
        }
    }

    fun addOrUpdateData(data: B1CouleurOuGoutProduitDataBase) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            B1CouleurOuGoutProduitDataBase.compareEntre(ancien = ancien, newData = data)
        }

        val updatedData = if (existingIndex >= 0) {
            data.copy(
                key = datasValue[existingIndex].key, // Keep existing key
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        } else {
            data.copy(
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        }

        addOrUpdatedAncienRepo(existingIndex, updatedData)
    }

    fun deleteData(data: B1CouleurOuGoutProduitDataBase) {
        deleteDataAncienRepo(data)
    }

    private fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        data: B1CouleurOuGoutProduitDataBase
    ) {
        composScope.launch {
            mainInitDataBase.addOrUpdatedAncienRepo(existingIndex, data)
        }
    }

    private fun deleteDataAncienRepo(
        data: B1CouleurOuGoutProduitDataBase
    ) {
        composScope.launch {
            mainInitDataBase.deleteDataAncienRepo(data)
        }
    }
}

@Entity
data class B1CouleurOuGoutProduitDataBase(
    @PrimaryKey
    var key: String = getPushFireBase(ref),
    var pushKey: String = getPushFireBase(ref),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    val aAffiche: Type = Type.Image,
    val nomImageFichie: String = "Non Dispo",
    val nomCouleurStrSiSonImageDispo: String = "",

    var parentBProduitOldID: Long? = null,
    var parentBProduitNom: String = "",
) {
    enum class Type { Image, Nom }

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
            // Compare by parent product ID and color/image info for better matching
            return ancien.parentBProduitOldID == newData.parentBProduitOldID &&
                    ancien.nomCouleurStrSiSonImageDispo == newData.nomCouleurStrSiSonImageDispo &&
                    ancien.nomImageFichie == newData.nomImageFichie
        }
    }
}
