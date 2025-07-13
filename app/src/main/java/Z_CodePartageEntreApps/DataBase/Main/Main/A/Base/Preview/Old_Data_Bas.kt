package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import android.annotation.SuppressLint
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

        // More specific path for better organization
        private val baseRef = Firebase.database.getReference(
            "e_DBJetPackExport"
        )

        // Main function to get old data with improved error handling
        @SuppressLint("RestrictedApi")
        suspend fun get_old_Datas(): List<OldDataBase_M1> {
            return try {
                val snapshot = baseRef.get().await()

                if (!snapshot.exists()) {
                    Log.w(TAG, "No data found at path: ${baseRef.path}")
                    return emptyList()
                }

                val oldDataList = mutableListOf<OldDataBase_M1>()

                snapshot.children.forEach { child ->
                    try {
                        when (val childValue = child.value) {
                            is Map<*, *> -> {
                                // This is an object, safe to convert
                                val oldData = child.getValue(OldDataBase_M1::class.java)
                                oldData?.let {
                                    oldDataList.add(it)
                                    Log.d(TAG, "Successfully parsed item with ID: ${it.id}")
                                }
                            }
                            is List<*> -> {
                                // Handle arrays if needed
                                Log.w(TAG, "Found array at ${child.key}, attempting to process...")
                                handleArrayData(childValue, oldDataList)
                            }
                            null -> {
                                Log.w(TAG, "Null value found at ${child.key}")
                            }
                            else -> {
                                Log.w(TAG, "Unknown data type at ${child.key}: ${childValue.javaClass.simpleName}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting child ${child.key}: ${e.message}", e)
                        // Continue with other children instead of crashing
                    }
                }

                Log.i(TAG, "Successfully loaded ${oldDataList.size} items from Firebase")
                oldDataList

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data from Firebase: ${e.message}", e)
                emptyList()
            }
        }

        // Helper function to handle array data
        private fun handleArrayData(arrayData: List<*>, targetList: MutableList<OldDataBase_M1>) {
            arrayData.forEach { item ->
                try {
                    when (item) {
                        is Map<*, *> -> {
                            // Convert map to OldDataBase_M1
                            val oldData = convertMapToOldDataBase(item)
                            oldData?.let {
                                targetList.add(it)
                                Log.d(TAG, "Successfully parsed array item with ID: ${it.id}")
                            }
                        }
                        else -> {
                            Log.w(TAG, "Unexpected item type in array: ${item?.javaClass?.simpleName}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing array item: ${e.message}", e)
                }
            }
        }

        // Helper function to manually convert map to OldDataBase_M1
        private fun convertMapToOldDataBase(map: Map<*, *>): OldDataBase_M1? {
            return try {
                OldDataBase_M1(
                    id = (map["id"] as? Number)?.toLong() ?: 0L,
                    affichageUniteState = map["affichageUniteState"] as? Boolean ?: false,
                    articleHaveUniteImages = map["articleHaveUniteImages"] as? Boolean ?: false,
                    articleItIdClassementInItCategorieInHVM = (map["articleItIdClassementInItCategorieInHVM"] as? Number)?.toInt() ?: 0,
                    benficeTotaleEntreMoiEtClien = (map["benficeTotaleEntreMoiEtClien"] as? Number)?.toInt() ?: 0,
                    benificeClient = (map["benificeClient"] as? Number)?.toInt() ?: 0,
                    benificeTotaleEn2 = (map["benificeTotaleEn2"] as? Number)?.toInt() ?: 0,
                    cartonState = map["cartonState"] as? String ?: "",
                    classementCate = (map["classementCate"] as? Number)?.toInt() ?: 0,
                    clienPrixVentUnite = (map["clienPrixVentUnite"] as? Number)?.toInt() ?: 0,
                    couleur1 = map["couleur1"] as? String ?: "",
                    dateCreationCategorie = map["dateCreationCategorie"] as? String ?: "",
                    dateLastIdSupplierChoseToBuy = map["dateLastIdSupplierChoseToBuy"] as? String ?: "",
                    dateLastSupplierIdBuyedFrom = map["dateLastSupplierIdBuyedFrom"] as? String ?: "",
                    diponibilityState = map["diponibilityState"] as? String ?: "",
                    funChangeImagsDimention = map["funChangeImagsDimention"] as? Boolean ?: false,
                    idArticle = (map["idArticle"] as? Number)?.toInt() ?: 0,
                    idArticlePlaceInCamionette = (map["idArticlePlaceInCamionette"] as? Number)?.toInt() ?: 0,
                    idCategorie = (map["idCategorie"] as? Number)?.toInt() ?: 0,
                    idCategorieNewMetode = (map["idCategorieNewMetode"] as? Number)?.toInt() ?: 0,
                    idPlaceStandartInStoreSupplier = (map["idPlaceStandartInStoreSupplier"] as? Number)?.toInt() ?: 0,
                    idcolor1 = (map["idcolor1"] as? Number)?.toInt() ?: 0,
                    idcolor3 = (map["idcolor3"] as? Number)?.toInt() ?: 0,
                    idcolor4 = (map["idcolor4"] as? Number)?.toInt() ?: 0,
                    imageDimention = map["imageDimention"] as? String ?: "",
                    itsNewArrivale = map["itsNewArrivale"] as? Boolean ?: false,
                    lastIdSupplierChoseToBuy = (map["lastIdSupplierChoseToBuy"] as? Number)?.toInt() ?: 0,
                    lastSupplierIdBuyedFrom = (map["lastSupplierIdBuyedFrom"] as? Number)?.toInt() ?: 0,
                    lastUpdateState = map["lastUpdateState"] as? String ?: "",
                    minQuan = (map["minQuan"] as? Number)?.toInt() ?: 0,
                    monBeneficeUniter = (map["monBeneficeUniter"] as? Number)?.toDouble() ?: 0.0,
                    monBenfice = (map["monBenfice"] as? Number)?.toInt() ?: 0,
                    monPrixAchat = (map["monPrixAchat"] as? Number)?.toInt() ?: 0,
                    monPrixAchatUniter = (map["monPrixAchatUniter"] as? Number)?.toInt() ?: 0,
                    monPrixVent = (map["monPrixVent"] as? Number)?.toInt() ?: 0,
                    monPrixVentUniter = (map["monPrixVentUniter"] as? Number)?.toDouble() ?: 0.0,
                    neaon1 = (map["neaon1"] as? Number)?.toInt() ?: 0,
                    neaon2 = map["neaon2"] as? String ?: "",
                    nmbrCaron = (map["nmbrCaron"] as? Number)?.toInt() ?: 0,
                    nmbrCat = (map["nmbrCat"] as? Number)?.toInt() ?: 0,
                    nmbrUnite = (map["nmbrUnite"] as? Number)?.toInt() ?: 0,
                    nomArab = map["nomArab"] as? String ?: "",
                    nomArticleFinale = map["nomArticleFinale"] as? String ?: "",
                    nomCategorie = map["nomCategorie"] as? String ?: "",
                    prixDeVentTotaleChezClient = (map["prixDeVentTotaleChezClient"] as? Number)?.toInt() ?: 0
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error converting map to OldDataBase_M1: ${e.message}", e)
                null
            }
        }

        // Alternative method with specific path for safer access
        suspend fun get_old_Datas_Safe(): List<OldDataBase_M1> {
            return try {
                // Use more specific path if you know where the valid data is
                val dataRef = Firebase.database.getReference(
                    "00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/AncienDataBase/A_ProduitInfos/validData"
                )

                val snapshot = dataRef.get().await()

                if (!snapshot.exists()) {
                    return emptyList()
                }

                val oldDataList = mutableListOf<OldDataBase_M1>()

                snapshot.children.forEach { child ->
                    try {
                        val oldData = child.getValue(OldDataBase_M1::class.java)
                        oldData?.let {
                            oldDataList.add(it)
                            Log.d(TAG, "Successfully parsed safe item with ID: ${it.id}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting safe child ${child.key}: ${e.message}", e)
                    }
                }

                Log.i(TAG, "Successfully loaded ${oldDataList.size} safe items from Firebase")
                oldDataList

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching safe data from Firebase: ${e.message}", e)
                emptyList()
            }
        }

        // Method to get data by specific ID
        suspend fun getDataById(id: Long): OldDataBase_M1? {
            return try {
                val snapshot = baseRef.orderByChild("id").equalTo(id.toDouble()).get().await()

                if (snapshot.exists()) {
                    snapshot.children.firstOrNull()?.getValue(OldDataBase_M1::class.java)
                } else {
                    Log.w(TAG, "No data found for ID: $id")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data by ID $id: ${e.message}", e)
                null
            }
        }

        // Method to validate data before processing
        fun validateData(data: OldDataBase_M1): Boolean {
            return when {
                data.id <= 0 -> {
                    Log.w(TAG, "Invalid ID: ${data.id}")
                    false
                }
                data.nomArticleFinale.isBlank() -> {
                    Log.w(TAG, "Empty article name for ID: ${data.id}")
                    false
                }
                data.nmbrCaron < 0 -> {
                    Log.w(TAG, "Invalid carton number for ID: ${data.id}")
                    false
                }
                else -> true
            }
        }
    }
}
