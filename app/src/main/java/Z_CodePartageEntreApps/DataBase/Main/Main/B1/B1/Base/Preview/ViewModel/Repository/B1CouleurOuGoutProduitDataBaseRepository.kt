package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.DataBaseFactory_B1CouleurOuGoutProduitDataBase
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ColorNameDisplayer
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ImageDisplayer
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9.Companion.getPushFireBase
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

enum class FilterQuery { NO_FILTER, SearchText, ProductId }

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
                datasValue.filter { d -> listOf(d.nomCouleurStrSiSonImageDispo, d.parentBProduitNom, d.nomImageFichie)
                    .any { it.contains(filterTextSearch.value, true) } }
            FilterQuery.ProductId -> filterProductId.value?.let { id -> datasValue.filter { it.parentBProduitOldID == id } } ?: datasValue
            else -> datasValue
        }
    }

    private val _filterQuery = mutableStateOf(FilterQuery.NO_FILTER)
    val filterQuery get() = _filterQuery
    private val _filterTextSearch = mutableStateOf("")
    val filterTextSearch get() = _filterTextSearch
    private val _filterProductId = mutableStateOf<Long?>(null)
    val filterProductId get() = _filterProductId

    init {
        composScope.launch {
            _datas.value = dao.getAll()
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun setFilterQuery(query: FilterQuery) { _filterQuery.value = query }
    fun setFilterTextSearch(text: String) {
        _filterTextSearch.value = text
        if (text.isNotBlank()) _filterQuery.value = FilterQuery.SearchText
    }
    fun setFilterProductId(productId: Long?) {
        _filterProductId.value = productId
        if (productId != null) _filterQuery.value = FilterQuery.ProductId
    }
    fun clearFilters() {
        _filterQuery.value = FilterQuery.NO_FILTER
        _filterTextSearch.value = ""
        _filterProductId.value = null
    }

    fun addOrUpdateData(data: B1CouleurOuGoutProduitDataBase) {
        val existingIndex = datasValue.indexOfFirst { B1CouleurOuGoutProduitDataBase.compareEntre(it, data) }
        val updatedData = data.copy(
            key = if (existingIndex >= 0) datasValue[existingIndex].key else data.key,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        composScope.launch { mainInitDataBase.addOrUpdatedAncienRepo(existingIndex, updatedData) }
    }

    fun deleteData(data: B1CouleurOuGoutProduitDataBase) {
        composScope.launch { mainInitDataBase.deleteDataAncienRepo(data) }
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
        val ref = Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/B1CouleurOuGoutProduitDataBase")
        fun compareEntre(ancien: B1CouleurOuGoutProduitDataBase, newData: B1CouleurOuGoutProduitDataBase) =
            ancien.parentBProduitOldID == newData.parentBProduitOldID &&
                    ancien.nomCouleurStrSiSonImageDispo == newData.nomCouleurStrSiSonImageDispo &&
                    ancien.nomImageFichie == newData.nomImageFichie
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CouleurDisplayer(
    modifier: Modifier = Modifier,
    data: B1CouleurOuGoutProduitDataBase,
    onClickToOpenWindow: (B1CouleurOuGoutProduitDataBase) -> Unit = {}
) {
    val imageFile by derivedStateOf {
        if (data.nomImageFichie != "Non Dispo") File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", data.nomImageFichie) else null
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (data.aAffiche) {
                B1CouleurOuGoutProduitDataBase.Type.Image -> ImageDisplayer(
                    modifier = Modifier.size(120.dp),
                    imageFile = imageFile,
                    colorName = data.nomCouleurStrSiSonImageDispo,
                    contentScale = ContentScale.Crop,
                    imageSize = DpSize(120.dp, 120.dp),
                    onClickToOpenWindow = { onClickToOpenWindow(data) }
                )
                B1CouleurOuGoutProduitDataBase.Type.Nom -> ColorNameDisplayer(
                    modifier = Modifier.size(120.dp),
                    colorName = data.nomCouleurStrSiSonImageDispo,
                    onClickToOpenWindow = { onClickToOpenWindow(data) }
                )
            }

            listOf("ID: ${data.key}", "Product: ${data.parentBProduitNom}", "Color: ${data.nomCouleurStrSiSonImageDispo}",
                "Type: ${data.aAffiche}", "Image: ${data.nomImageFichie}").forEach { Text(it) }
            data.parentBProduitOldID?.let { Text("Parent ID: $it") }
        }
    }
}
