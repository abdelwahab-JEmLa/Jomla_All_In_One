package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

data class OldDataBase_M1(
    val id: Long = 0,
    val affichageUniteState: Boolean = false,
    val articleHaveUniteImages: Boolean = false,
    val articleItIdClassementInItCategorieInHVM: Int = 0,
    val benficeTotaleEntreMoiEtClien: Int = 0,
    val benificeClient: Int = 0,
    val benificeTotaleEn2: Int = 0,
    val cartonState: String = "",
    val classementCate: Int = 0,
    val clienPrixVentUnite: Int = 0,
    val couleur1: String = "",
    val dateCreationCategorie: String = "",
    val dateLastIdSupplierChoseToBuy: String = "",
    val dateLastSupplierIdBuyedFrom: String = "",
    val diponibilityState: String = "",
    val funChangeImagsDimention: Boolean = false,
    val idArticle: Int = 0,
    val idArticlePlaceInCamionette: Int = 0,
    val idCategorie: Int = 0,
    val idCategorieNewMetode: Int = 0,
    val idPlaceStandartInStoreSupplier: Int = 0,
    val idcolor1: Int = 0,
    val idcolor3: Int = 0,
    val idcolor4: Int = 0,
    val imageDimention: String = "",
    val itsNewArrivale: Boolean = false,
    val lastIdSupplierChoseToBuy: Int = 0,
    val lastSupplierIdBuyedFrom: Int = 0,
    val lastUpdateState: String = "",
    val minQuan: Int = 0,
    val monBeneficeUniter: Double = 0.0,
    val monBenfice: Int = 0,
    val monPrixAchat: Int = 0,
    val monPrixAchatUniter: Int = 0,
    val monPrixVent: Int = 0,
    val monPrixVentUniter: Double = 0.0,
    val neaon1: Int = 0,
    val neaon2: String = "",
    val nmbrCaron: Int = 0,
    val nmbrCat: Int = 0,
    val nmbrUnite: Int = 0,
    val nomArab: String = "",
    val nomArticleFinale: String = "",
    val nomCategorie: String = "",
    val prixDeVentTotaleChezClient: Int = 0
) {
    companion object {
        private const val TAG = "OldDataBase_M1"

        val ref = Firebase.database.getReference(
            "00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases"
        )

        suspend fun get_old_Datas(): MutableList<OldDataBase_M1> {
            val oldDataList = mutableListOf<OldDataBase_M1>()

            try {
                val snapshot = ref.get().await()

                if (snapshot.exists()) {
                    snapshot.children.forEach { child ->
                        try {
                            // Check if the child value is actually an object, not an array
                            val childValue = child.value

                            when (childValue) {
                                is Map<*, *> -> {
                                    // This is an object, safe to convert
                                    val oldData = child.getValue(OldDataBase_M1::class.java)
                                    oldData?.let { oldDataList.add(it) }
                                }
                                is List<*> -> {
                                    // This is an array, handle differently
                                    Log.w(TAG, "Found array at ${child.key}, skipping conversion")
                                    // You could handle arrays here if needed
                                }
                                else -> {
                                    Log.w(TAG, "Unknown data type at ${child.key}: ${childValue?.javaClass?.simpleName}")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error converting child ${child.key}: ${e.message}", e)
                            // Continue with other children instead of crashing
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data from Firebase: ${e.message}", e)
            }

            return oldDataList
        }

        // Alternative method if you know the exact path to valid data
        suspend fun get_old_Datas_Safe(): MutableList<OldDataBase_M1> {
            val oldDataList = mutableListOf<OldDataBase_M1>()

            try {
                // Update this path to point to where your actual object data is stored
                val dataRef = Firebase.database.getReference(
                    "00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/AncienDataBase/A_ProduitInfos"
                )

                val snapshot = dataRef.get().await()

                if (snapshot.exists()) {
                    snapshot.children.forEach { child ->
                        try {
                            val oldData = child.getValue(OldDataBase_M1::class.java)
                            oldData?.let { oldDataList.add(it) }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error converting child ${child.key}: ${e.message}", e)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching safe data from Firebase: ${e.message}", e)
            }

            return oldDataList
        }
    }
}
