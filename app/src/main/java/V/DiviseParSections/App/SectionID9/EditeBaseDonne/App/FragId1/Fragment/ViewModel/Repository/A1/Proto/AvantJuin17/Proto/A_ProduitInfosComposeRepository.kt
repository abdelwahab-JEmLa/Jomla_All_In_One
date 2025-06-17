package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.AvantJuin17.Proto

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_ProduitInfos.Repository.C.Update.Dao.A_ProduitInfosDao
import android.util.Log
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
import kotlinx.coroutines.tasks.await
import org.mongodb.kbson.BsonObjectId
@Stable
class A_ProduitInfosComposeRepository(val dao: A_ProduitInfosDao) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<A_ProduitInfos>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val tigerDataRecompose by derivedStateOf { _datas.value.map { it.dernierTimeTampsSynchronisationAvecFireBase } }

    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun addOrUpdateData(data: A_ProduitInfos) {
        val newData = data.withDernierTimeTampsSynchronisationAvecFireBase()
        val existingIndex = A_ProduitInfos.indexOfFirst(datasValue, newData)

        _datas.value = if (existingIndex >= 0) {
            datasValue.toMutableList().apply {
                this[existingIndex] = this[existingIndex].copy(
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            }
        } else {
            datasValue + newData
        }

        composScope.launch {
            if (existingIndex >= 0) {
                val preparedData = data.withDernierTimeTampsSynchronisationAvecFireBase()
                dao.update(preparedData)
                batchFireBaseUpdateA_ProduitInfos(listOf(preparedData))
            } else {
                val preparedData = data.withDernierTimeTampsSynchronisationAvecFireBase()
                dao.insert(preparedData)
                batchFireBaseUpdateA_ProduitInfos(listOf(preparedData))
            }
        }
    }

    private suspend fun batchFireBaseUpdateA_ProduitInfos(datas: List<A_ProduitInfos>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.bsonObjectId] = data
        }
        val firebaseRef = A_ProduitInfos.caRef
        firebaseRef.updateChildren(updates).await()
    }
}

@Entity
data class A_ProduitInfos(
    @PrimaryKey
    var bsonObjectId: String = BsonObjectId().toHexString(),
    var id: Long = 0L,

    var idParentCategorie: Long? = null,

    // Section InfosDeBase
    var nom: String = "",

    //
    var nombreUniteInt: Int = 0,
    var nombreProduitDonSonCarton: Int = 0,

    // Section Etates Mutable
    var dernierFireBaseUpdateTimestamps: Long = 0,

    // Section InfosCoutes
    var prixVent: Double = 0.0,
    var prixAchat: Double = 0.0,
    var clientPrixVentUnite: Double = 0.0,


    //image
    var actualiseSonImage: Int = 0,
    var actualiseSonImageTest2: Int = 0,


    // Add availability states with proper initialization
    var disponibilityEtates: DisponibilityEtates = DisponibilityEtates.DISPO,

    // Section keyFireBase
    var keyFireBase: String = "",


    var nomArab: String = "",
    var autreNomDarticle: String? = null,
    var couleur1: String? = null,
    var idcolor1: Long = 0,
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

    //Section Senior Tiger Update
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
) {
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

    fun withDernierTimeTampsSynchronisationAvecFireBase(): A_ProduitInfos {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        val caRef =
            Firebase.database.getReference(
                "00_DataPrototype-04-02" +
                        "/_1_developingRef" +
                        "/C_InfosSqlDataBases" +
                        "/A_ProduitInfos"
            )

        fun securedRemoveFireBaseDB() {
            caRef.removeValue()
        }

        fun logData(data: A_ProduitInfos, TAG: String) {
            Log.d(TAG, "Data: ${data.bsonObjectId} - ")
        }
        fun indexOfFirst(datasValue: List<A_ProduitInfos>, newData: A_ProduitInfos): Int {
            return datasValue.indexOfFirst { it.nom == newData.nom }
        }
    }
}

