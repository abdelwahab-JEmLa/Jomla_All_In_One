package V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.DataBaseInitFactory_B1CouleurOuGoutProduitDataBase
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ColorNameDisplayer
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ImageDisplayer
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File

enum class FilterQuery {
    NO_FILTER,
    SearchText
}

@Stable
class Repo03CouleurProduitInfos(
    val mainInitDataBase: DataBaseInitFactory_B1CouleurOuGoutProduitDataBase,
) {
    val dao = mainInitDataBase.dao
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M3CouleurProduitInfos>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    val datasValueFiltered by derivedStateOf {
        when (filterQuery.value) {
            FilterQuery.SearchText -> if (filterTextSearch.value.isBlank()) datasValue else
                datasValue.filter { data ->
                    listOf(
                        data.nomCouleurStrSiSonImageDispo,
                        data.parentId1ProduitInfosDebugName,
                        data.nomImageFichieSansEtansion,
                        data.parentBProduitOldID.toString()
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

    fun addOrUpdateData(data: M3CouleurProduitInfos) {
        val existingIndex =
            datasValue.indexOfFirst { M3CouleurProduitInfos.compareEntre(it, data) }
        val updatedData = data.copy(
            keyID = if (existingIndex >= 0) datasValue[existingIndex].keyID else data.keyID,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        composScope.launch { mainInitDataBase.addOrUpdatedAncienRepo(existingIndex, updatedData) }
    }

    fun deleteData(data: M3CouleurProduitInfos) {
        composScope.launch { mainInitDataBase.deleteDataAncienRepo(data) }
    }

    companion object {
        fun getRelatedCouleur(
            aCentralCompoRepositoryProtoJuin9: RepositorysMainGetter,
            produit: ArticlesBasesStatsTable,
            colorIndex: Int
        ) =
            aCentralCompoRepositoryProtoJuin9.repo03CouleurProduitInfos.datasValue
                .find {
                    it.parentBProduitOldID == produit.id
                            && it.indexCouleurDansAncienProto == colorIndex
                }!!
    }
}

@Entity
data class M3CouleurProduitInfos(
    @PrimaryKey
    var keyID: String = getPushFireBase(ref),
    var debugInfos: String = "",
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    val processPositioningInFactory: ProcessPositioningInFactory = ProcessPositioningInFactory.CreeAuGeneralHandler,
    val aAffiche: Type = Type.Image,
    val nomImageFichieSansEtansion: String = "Non Dispo",

    val nomCouleurStrSiSonImageDispo: String = "",

    //---------------------------------Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parentBProduitInfosKeyID: String = "",

    var parentBProduitOldID: Long = 0,
    var parentId1ProduitInfosDebugName: String = "",
    var indexCouleurDansAncienProto: Int = 0,


    val extensionDisponible: String = "webp", // Default extension
) {
    fun get_DebugsInfos(): String {
        return buildString {
            append("03Coul")
            append("[")
            append("{${keyID.takeLast(4).uppercase()}}\n")
            append(" To ")
            append("[")
            append("{${parentBProduitInfosKeyID.takeLast(4).uppercase()}}\n")
            append("]")
            append("]")
        }
    }

    enum class Type { Nom,Image }
    enum class ProcessPositioningInFactory { CreeDepuitRechercheRapid , CreeAuGeneralHandler }

    companion object {
        val ref =
            Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases" +
                    "/B1CouleurOuGoutProduitDataBase")

        fun compareEntre(
            ancien: M3CouleurProduitInfos,
            newData: M3CouleurProduitInfos
        ) =
            ancien.parentBProduitOldID == newData.parentBProduitOldID &&
                    ancien.nomCouleurStrSiSonImageDispo == newData.nomCouleurStrSiSonImageDispo &&
                    ancien.nomImageFichieSansEtansion == newData.nomImageFichieSansEtansion

        fun get_default(): M3CouleurProduitInfos {
                return M3CouleurProduitInfos()
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CouleurDisplayer(
    modifier: Modifier = Modifier,
    keyCouleur: String,
    b1CouleurOuGoutProduitDataBaseRepository: Repo03CouleurProduitInfos = koinInject(),
    size: Dp = 200.dp,
    onClickToOpenWindow: (M3CouleurProduitInfos) -> Unit = {}
) {
    val datas = b1CouleurOuGoutProduitDataBaseRepository.datasValue
    val data = datas.find { it.keyID == keyCouleur }!!

    val imageFile by derivedStateOf {
        if (data.nomImageFichieSansEtansion != "Non Dispo") {
            val fileName = "${data.nomImageFichieSansEtansion}.${data.extensionDisponible}"
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", fileName)
        } else null
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            when (data.aAffiche) {
                M3CouleurProduitInfos.Type.Image -> {
                    ImageDisplayer(
                        modifier = Modifier.size(size),
                        imageFile = imageFile,
                        colorName = data.nomCouleurStrSiSonImageDispo,
                        contentScale = ContentScale.Crop,
                        imageSize = DpSize(size, size),
                        onClickToOpenWindow = { onClickToOpenWindow(data) }
                    )
                }

                M3CouleurProduitInfos.Type.Nom -> ColorNameDisplayer(
                    modifier = Modifier.size(size),
                    colorName = data.nomCouleurStrSiSonImageDispo,
                    onClickToOpenWindow = { onClickToOpenWindow(data) }
                )
            }
        }
    }
}
