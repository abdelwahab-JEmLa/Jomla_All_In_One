package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Proto.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.A_ProduitDataBaseProtoJuin17
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Fonctions.Main.getKeyFireBase
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
import org.mongodb.kbson.BsonObjectId

@Stable
class A_ProduitDataBaseComposeRepositoryPJ17(
    val ancienRepo: A_ProduitDataBaseProtoJuin17,
    val b3CategoriesCompoRepository: C_CategoriesCompoRepository
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<ArticlesBasesStatsTable>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    val sortedDatasValue: List<ArticlesBasesStatsTable> by derivedStateOf {
        val categoryMap = b3CategoriesCompoRepository.datasValue.associateBy { it.id }
        val catalogues = B4CatalogueCategoriesRepository().associateBy { it.id }

        val (regularProducts, orphanProducts) = datasValue.partition { product ->
            val categoryId = product.idParentCategorie ?: 0L
            val category = categoryMap[categoryId]
            val catalogueId = category?.catalogueParentId ?: 4L

            category != null &&
                    catalogueId != 4L &&
                    !category.nom.equals("NONE", ignoreCase = true)
        }

        val sortedRegular = regularProducts.sortedWith(
            compareBy<ArticlesBasesStatsTable> { product ->
                val categoryId = product.idParentCategorie ?: 0L
                val category = categoryMap[categoryId]
                val catalogueId = category?.catalogueParentId ?: 4L
                catalogues[catalogueId]?.position ?: Int.MAX_VALUE
            }.thenBy { product ->
                val categoryId = product.idParentCategorie ?: 0L
                categoryMap[categoryId]?.position ?: Int.MAX_VALUE
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        val sortedOrphan = orphanProducts.sortedWith(
            compareBy<ArticlesBasesStatsTable> { product ->
                val categoryId = product.idParentCategorie ?: 0L
                val category = categoryMap[categoryId]
                category?.nom?.takeIf { !it.equals("NONE", ignoreCase = true) }
                    ?: "ZZZZZ_NO_CATEGORY"
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        sortedRegular + sortedOrphan
    }

    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun addOrUpdateData(data: ArticlesBasesStatsTable) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ArticlesBasesStatsTable.compareEntre(ancien = ancien, newData = data)
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

    fun deleteData(data: ArticlesBasesStatsTable) {
        _datas.value = datasValue.filter { existing ->
            !ArticlesBasesStatsTable.compareEntre(ancien = existing, newData = data)
        }
        deleteDataAncienRepo(data)
    }

    private fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        data: ArticlesBasesStatsTable
    ) {
        composScope.launch {
            ancienRepo.addOrUpdatedAncienRepo(existingIndex, data)
        }
    }

    private fun deleteDataAncienRepo(
        data: ArticlesBasesStatsTable
    ) {
        composScope.launch {
            ancienRepo.deleteDataAncienRepo(data)
        }
    }
}

@Entity
data class ArticlesBasesStatsTable(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var bsonObjectId: String = BsonObjectId().toHexString(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var dernierFireBaseUpdateTimestamps: Long = 0,

    //S P Ids
    var idParentCategorie: Long? = null,

    var positionDonSonCesFrereCategorieProduits: Int = 0,

    // Section InfosDeBase
    var nom: String = "",
    val etateActuelleOnFusionAvecBaseDonne: EtateActuelleOnFusionAvecBaseDonne = EtateActuelleOnFusionAvecBaseDonne.CategorieOriginaleDefinie,

    var nombreUniteInt: Int = 1,
    var nombreProduitDonSonCarton: Int = 1,

    // Section Etates Mutable
    val heldPrioriteDemandAuGrossist: Boolean = false,

    // Section InfosCoutes
    var prixVent: Double = 0.0,
    var cachePrixVent: Boolean = false,

    var prixAchat: Double = 0.0,
    var prixAchatDernierTimeTempUpdate: Long = 0L,
    var clientPrixVentUnite: Double = 0.0,


    //image
    var actualiseSonImage: Int = 0,
    var actualiseSonImageTest2: Int = 0,

    //Ui States Personele Paramater
    var afficheCesDetailPourComptBsonId: String = "",

    // Add availability states with proper initialization
    var disponibilityEtates: DisponibilityEtates = DisponibilityEtates.DISPO,

    // Section keyFireBase
    var keyFireBase: String = "",


    var nomArab: String = "",
    var autreNomDarticle: String? = null,
    var couleur1: String? = "couleur1",
    var idcolor1: Long = 1,
    var couleur2: String? = null,
    var idcolor2: Long = 0,
    var couleur3: String? = null,
    var idcolor3: Long = 0,
    var couleur4: String? = null,
    var idcolor4: Long = 0,
    var nomCategorie2: String? = null,
    var affichageUniteState: Boolean = false,
    var commmentSeVent: String? = null,
    var afficheBoitSiUniter: String? = null,
    var minQuan: Int = 0,
    var monBenfice: Double = 0.0,
    var neaon2: String = "",
    var catalogeParentID: Long = 0,
    var funChangeImagsDimention: Boolean = false, //imgStatIsSmall
    var nomCategorie: String = "",
    var neaon1: Double = 0.0,
    var lastUpdateState: String = "",
    var cartonState: String = "",
    var dateCreationCategorie: String = "",
    var prixDeVentTotaleChezClient: Double = 0.0,
    var benficeTotaleEntreMoiEtClien: Double = 0.0,
    var benificeTotaleEn2: Double = 0.0,
    var monPrixAchatUniter: Double = 0.0,
    var monPrixVentUniter: Double = 0.0,

    var articleHaveUniteImages: Boolean = false,
    var itsNewArrivale: Boolean = false,
    var imageDimention: String = "",
    var idForSearchArticles: Long = 0,

    ) {
    enum class EtateActuelleOnFusionAvecBaseDonne {
        CaprtureSonImage,
        PrixAchatPriseDepuitGrossist,
        PrixDeVentDefinie,
        CategorieOriginaleDefinie,
        PositionAvecCesFrereDefinie,
    }

    fun withDernierTimeTampsSynchronisationAvecFireBase(): ArticlesBasesStatsTable {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }


    fun withProperKeyFireBaseAndTimeTamp(): ArticlesBasesStatsTable {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(id, nom) }
        return this.copy(
            keyFireBase = safeKey,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    fun toggleDisponibilityEtates(): ArticlesBasesStatsTable {
        val newState = disponibilityEtates.toggleEntreEtates()
        return this.copy(
            disponibilityEtates = newState,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    fun getNomFilesDesCouleursImagesDispoDonSockage(): List<String> {
        return emptyList()
    }


    companion object {
        val ref =
            Firebase.database.getReference(
                "00_DataPrototype-04-02" +
                        "/_1_developingRef" +
                        "/C_InfosSqlDataBases" +
                        "/A_ProduitInfos"
            )

        fun securedRemoveFireBaseDB() {
            ref.removeValue()
        }

        fun removeRef(
            preparedData: ArticlesBasesStatsTable
        ) {
            ref.child(preparedData.keyFireBase).removeValue()
        }

        fun compareEntre(
            ancien: ArticlesBasesStatsTable,
            newData: ArticlesBasesStatsTable
        ): Boolean {
            val delimiterExistence =
                ancien.id == newData.id
            return delimiterExistence
        }
    }
}

enum class DisponibilityEtates(val nomArabe: String = "") {
    DISPO("متوفر"),
    NON_DISPO("غير متوفر"),
    PETITE_PROBABILITY("احتمال كبير");

    fun toggleEntreEtates(): DisponibilityEtates = when (this) {
        DISPO -> NON_DISPO
        NON_DISPO -> PETITE_PROBABILITY
        PETITE_PROBABILITY -> DISPO
    }

    companion object {
        fun fromString(value: String): DisponibilityEtates {
            return when (value) {
                "DISPO" -> DISPO
                "NON_DISPO" -> NON_DISPO
                "PETITE_PROBABILITY" -> PETITE_PROBABILITY
                else -> DISPO // default value
            }
        }
    }
}
