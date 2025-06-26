package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9.Companion.getPushFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.DataBaseFactory_B1CouleurOuGoutProduitDataBase
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

enum class FilterQuery {
    NO_FILTER,
    SearchText
}

@Stable
class B1CouleurOuGoutProduitDataBaseRepository(
    val mainInitDataBase: DataBaseFactory_B1CouleurOuGoutProduitDataBase,
) {
    val dao = mainInitDataBase.dao
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<B1CouleurOuGoutProduitDataBase>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    val datasValueFiltred by derivedStateOf {
        when (filterQuery.value) {
            FilterQuery.SearchText -> if (filterTextSearch.value.isBlank()) datasValue else
                datasValue.filter { d ->
                    listOf(
                        d.nomCouleurStrSiSonImageDispo,
                        d.parentBProduitNom,
                        d.nomImageFichieSansEtansion,
                        d.parentBProduitOldID.toString()
                    ).any { it.contains(filterTextSearch.value, true) }
                }

            else -> datasValue
        }
    }

    private val _filterQuery = mutableStateOf(FilterQuery.NO_FILTER)
    val filterQuery get() = _filterQuery
    private val _filterTextSearch = mutableStateOf("")
    val filterTextSearch get() = _filterTextSearch

    init {
        composScope.launch {
            _datas.value = dao.getAll()
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun setFilterTextSearch(text: String) {
        _filterTextSearch.value = text
        if (text.isNotBlank()) _filterQuery.value = FilterQuery.SearchText
    }

    fun clearFilters() {
        _filterQuery.value = FilterQuery.NO_FILTER
        _filterTextSearch.value = ""
    }

    fun addOrUpdateData(data: B1CouleurOuGoutProduitDataBase) {
        val existingIndex =
            datasValue.indexOfFirst { B1CouleurOuGoutProduitDataBase.compareEntre(it, data) }
        val updatedData = data.copy(
            key = if (existingIndex >= 0) datasValue[existingIndex].key else data.key,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        composScope.launch { mainInitDataBase.addOrUpdatedAncienRepo(existingIndex, updatedData) }
    }

    fun deleteData(data: B1CouleurOuGoutProduitDataBase) {
        composScope.launch { mainInitDataBase.deleteDataAncienRepo(data) }
    }

    companion object {
        fun getRelatedCouleur(
            aCentralCompoRepositoryProtoJuin9: ACentralCompoRepositoryProtoJuin9,
            produit: ArticlesBasesStatsTable,
            colorIndex: Int
        ) =
            aCentralCompoRepositoryProtoJuin9.b1CouleurOuGoutProduitDataBaseRepository.datasValue
                .find {
                    it.parentBProduitOldID == produit.id
                            && it.indexCouleurDansAncienProto == colorIndex
                }!!
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
    val nomImageFichieSansEtansion: String = "Non Dispo",
    val nomCouleurStrSiSonImageDispo: String = "",

    var parentBProduitOldID: Long = 0,
    var parentBProduitNom: String = "",
    var indexCouleurDansAncienProto: Int = 0,
    val extensionDisponible: String = "webp", // Default extension
) {
    enum class Type { Image, Nom }

    companion object {
        val ref =
            Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/B1CouleurOuGoutProduitDataBase")

        fun compareEntre(
            ancien: B1CouleurOuGoutProduitDataBase,
            newData: B1CouleurOuGoutProduitDataBase
        ) =
            ancien.parentBProduitOldID == newData.parentBProduitOldID &&
                    ancien.nomCouleurStrSiSonImageDispo == newData.nomCouleurStrSiSonImageDispo &&
                    ancien.nomImageFichieSansEtansion == newData.nomImageFichieSansEtansion
    }
}
