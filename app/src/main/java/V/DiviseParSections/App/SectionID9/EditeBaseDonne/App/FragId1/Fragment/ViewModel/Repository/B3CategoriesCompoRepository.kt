package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update.addOrUpdateDatas
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Fonctions.Main.getKeyFireBase
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
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
    val parentRepo = a_MasterRepositorysGrpProtoJuin3.repoC_CategorieProduitInfos
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<CategoriesTabelle>>(emptyList())
    val datasState: State<List<CategoriesTabelle>> = _datas

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

    fun addOrUpdateData(data: CategoriesTabelle) {
        data.let { dataSansProper ->
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
    fun updateSonRepositoryProtoJuin3(newData: CategoriesTabelle) { parentRepo.addOrUpdateData(newData) }

    fun addOrUpdateDatas(datas: List<CategoriesTabelle>) {
        val processedDatas = datas.map { it.withProperKeyFireBaseAndTimeTamp() }

        val currentList = _datas.value.toMutableList()
        processedDatas.forEach { newData ->
            val existingIndex = currentList.indexOfFirst { it.id == newData.id }
            if (existingIndex >= 0) {
                currentList[existingIndex] = newData
            } else {
                currentList.add(newData)
            }
        }
        _datas.value = currentList

        updateDatasDonSonRepositoryProtoJuin3(processedDatas)
    }

    fun updateDatasDonSonRepositoryProtoJuin3(newDatas: List<CategoriesTabelle>) {
        parentRepo.addOrUpdateDatas(newDatas)
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

    // Section Centralization Valeurs Pour Injection a TOu modules
    var cSelectionePourDeplace: Boolean = false,

    // Section keyFireBase et dernierFireBaseUpdateTimestamps
    var keyFireBase: String = "",
    var dernierTimeTampsSynchronisationAvecFireBase: Long = DatesHandler().getCurrentTimestamps(),
    ) {
    fun withProperKeyFireBaseAndTimeTamp(): CategoriesTabelle {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(id, nom) }
        return this.copy(
            keyFireBase = safeKey,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        val caRef =
            Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/C_CategorieProduitInfos")
    }
}
