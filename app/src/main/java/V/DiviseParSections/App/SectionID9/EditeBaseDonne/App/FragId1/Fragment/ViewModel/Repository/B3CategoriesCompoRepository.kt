package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Fonctions.Main.getKeyFireBase
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
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
class B3CategoriesCompoRepository(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<CategoriesTabelle>>(emptyList())
    val datasState: State<List<CategoriesTabelle>> = _datas
    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    model.repoStateC_CategorieProduitInfos?.modelListFlow
                    _datas.value =
                        model.repoStateC_CategorieProduitInfos?.modelListFlow ?: emptyList()
                }
            }
        }
    }

    fun addOrUpdateData(data: CategoriesTabelle?) {
        data?.let { dataSansProper ->
            val newData = dataSansProper.withProperKeyFireBaseAndTimeTamp()
            _datas.value = _datas.value.map {
                if (it.id == newData.id)
                    newData
                else it
            }.let { list ->
                if (list.none { it.id == newData.id }) list + newData else list
            }
            updateSonRepositoryProtoJuin3(newData)
        }
    }

    private fun updateSonRepositoryProtoJuin3(newData: CategoriesTabelle) {
    }
}

@Entity
data class CategoriesTabelle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    //Parent Forging Ids
    val catalogueParentId: Long = 0,

    var nom: String = "",

    var position: Int = 0,

    var displayedHeader: Boolean = false,

    val itsHeldPourDeplacement: Boolean = false,

    // Section Etates Mutable
    var dernierFireBaseUpdateTimestamps: Long = 0,

    // Section keyFireBase
    var keyFireBase: String = "",
    ) {
    fun withProperKeyFireBaseAndTimeTamp(): CategoriesTabelle {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(id, nom) }
        return this.copy(
            keyFireBase = safeKey,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    companion object {
        val caRef =
            Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/C_CategorieProduitInfos")
    }
}
