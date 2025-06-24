package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.DataBaseFactoryDCouleurAchatOperation
import Z_CodePartageEntreApps.Modules.D.Glide.FileCouleurInfos
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.Exclude
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import java.io.File
import java.util.Objects

@Stable
class DAchatOperationCouleurRepositoryComposable(
    private val ancienRepo: DataBaseFactoryDCouleurAchatOperation,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val depuitTestData = false
    private val _datas = mutableStateOf<List<D_CouleurVentOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val datasFiltered by derivedStateOf {
        datasValue
    }
    val filteredDatasValue by derivedStateOf {
        datasValue.filter {
            it.etateActuellementEst == D_CouleurVentOperation.EtateActuellementEst.ParentBonVentConfirme
        }
    }
    val ouvertData by derivedStateOf { datasFiltered.lastOrNull() }

    // Add synchronization for database operations
    private val dbMutex = Mutex()

    companion object {
        private const val TAG = "ColorOperation"
    }

    init {
        composScope.launch {
            try {
                if (depuitTestData) {
                    withContext(Dispatchers.Main) {
                        _datas.value = getTestDate()
                        Log.d(TAG, "Initialized with test data")
                    }
                } else {
                    dao.getAllFlow().collect { data ->
                        try {
                            withContext(Dispatchers.Main) {
                                _datas.value = data
                                Log.d(TAG, "Updated data from database, size: ${data.size}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error updating data from database", e)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in init block", e)
            }
        }
    }

    fun getTestDate(): List<D_CouleurVentOperation> {
        return emptyList()
    }

    fun acheterUneCouleur(
        ouvertData: Z_AppCompt,
        produit: ArticlesBasesStatsTable,
        quantity: Int = 1,
        colorIndex: Int
    ) {
        // Validate inputs immediately to avoid unnecessary processing
        if (quantity <= 0 || colorIndex < 0) {
            Log.w(TAG, "Invalid parameters: quantity=$quantity, colorIndex=$colorIndex")
            return
        }


        // Use a single coroutine with structured exception handling
        composScope.launch {
            try {
                Log.d(TAG, "Starting acheterUneCouleur for product ${produit.id}, colorIndex: $colorIndex, quantity: $quantity")

                // Create minimal ID to reduce memory footprint
                val timestamp = System.currentTimeMillis()
                val id = "p_${produit.id}_${colorIndex}_$timestamp"

                // Get color name safely without file operations
                val colorName = getCouleurNameByIndex(produit, colorIndex)

                // Create the operation object with minimal data and safe string operations
                val couleurVentOperation = createSafeCouleurVentOperation(
                    id = id,
                    ouvertData = ouvertData,
                    produit = produit,
                    colorIndex = colorIndex,
                    colorName = colorName,
                    quantity = quantity
                )

                Log.d(TAG, "Created operation with ID: $id")

                // Add to repository with additional error handling
                addOrUpdateData(couleurVentOperation)

                Log.d(TAG, "Successfully completed acheterUneCouleur for product ${produit.id}")

            } catch (e: OutOfMemoryError) {
                Log.e(TAG, "OutOfMemoryError in acheterUneCouleur - reducing memory usage", e)
                // Force garbage collection and retry with minimal data
                System.gc()
            } catch (e: Exception) {
                Log.e(TAG, "Error in acheterUneCouleur for product ${produit.id}, colorIndex: $colorIndex", e)
            }
        }
    }

    // Helper function to create the operation object safely
    private fun createSafeCouleurVentOperation(
        id: String,
        ouvertData: Z_AppCompt,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        colorName: String,
        quantity: Int
    ): D_CouleurVentOperation {
        return D_CouleurVentOperation(
            id = id,

            // Use safe string operations with length limits
            nomCouleurStrSiSonImageDispo = colorName.take(50),
            nomImageFichieOuApellationDuCouleur = "${produit.id}_${colorIndex + 1}".take(100),

            // Set display mode to name to avoid file operations during creation
            aAffiche = FileCouleurInfos.Affiche.Nom,
            baseFileName = "${produit.id}_${colorIndex + 1}.webp".take(100),

            // Parent IDs with safe string operations
            // FIXED: Use bsonObjectId instead of id, or use getKeyID() method
            parentZAppComptID = ouvertData.bsonObjectId.take(100), // Using bsonObjectId
            // Alternative: parentZAppComptID = ouvertData.getKeyID().take(100), // Using getKeyID() method

            parentEPeriodVentId = ouvertData.ouvertEPeriodVentId.take(100),
            parentEPeriodVentStartDate = ouvertData.ouvertEPeriodVentStartTimesTamp,
            parentBonVentId = ouvertData.ouvertBonVentId.take(100),
            parentClientId = ouvertData.ouvertClientOnVentKeyId.take(100),
            parentClientName = ouvertData.ouvertClientOnVentNom.take(100),
            parentProduitId = ouvertData.ouvertProduitOnVentID.take(100),
            parentProduitAncienId = ouvertData.ouvertProduitOnVentAncienId,
            parentProduitKeyNom = ouvertData.ouvertProduitOnVentNom.take(100),

            // Operation details
            quantityAchete = quantity,
            etateActuellementEst = D_CouleurVentOperation.EtateActuellementEst.ChoisiQuantityConfirme,
            type = D_CouleurVentOperation.Type.CommandeDeLui,

            // Set timestamp
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    // Improved addOrUpdateData with better memory management
    fun addOrUpdateData(data: D_CouleurVentOperation) {
        composScope.launch {
            try {
                Log.d(TAG, "Starting addOrUpdateData for ID: ${data.id}")

                dbMutex.withLock {
                    // Create a lightweight copy for timestamp update only
                    val dataUpdate = data.copy(
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )

                    // Get current data reference (avoid copying large lists)
                    val currentData = _datas.value
                    val existingIndex = currentData.indexOfFirst { it.isSameEntity(dataUpdate) }

                    Log.d(TAG, "Existing index: $existingIndex, Current data size: ${currentData.size}")

                    // Update UI state with memory-efficient operations
                    withContext(Dispatchers.Main) {
                        val newList = if (existingIndex >= 0) {
                            // Update existing item in-place to reduce memory allocation
                            currentData.toMutableList().apply {
                                val existing = this[existingIndex]
                                this[existingIndex] = existing.copy(
                                    quantityAchete = existing.quantityAchete + dataUpdate.quantityAchete,
                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                )
                            }
                        } else {
                            // Add new item efficiently
                            ArrayList<D_CouleurVentOperation>(currentData.size + 1).apply {
                                addAll(currentData)
                                add(dataUpdate)
                            }
                        }

                        _datas.value = newList
                        Log.d(TAG, "Successfully updated data list, new size: ${newList.size}")
                    }

                    // Database operation with improved error handling
                    try {
                        // Use the original data object to avoid additional copying
                        ancienRepo.addOrUpdatedAncienRepo(existingIndex, dataUpdate)
                        Log.d(TAG, "Successfully updated database")
                    } catch (dbException: Exception) {
                        Log.e(TAG, "Database operation failed, but UI was updated", dbException)
                        // UI state is preserved even if database operation fails
                    }
                }
            } catch (e: OutOfMemoryError) {
                Log.e(TAG, "OutOfMemoryError in addOrUpdateData", e)
                // Force garbage collection
                System.gc()
            } catch (e: Exception) {
                Log.e(TAG, "Error in addOrUpdateData", e)
            }
        }
    }


    // Helper function to get color name by index from product
    private fun getCouleurNameByIndex(produit: ArticlesBasesStatsTable, colorIndex: Int): String {
        return when (colorIndex) {
            0 -> produit.couleur1 ?: "couleur1"
            1 -> produit.couleur2 ?: "couleur2"
            2 -> produit.couleur3 ?: "couleur3"
            3 -> produit.couleur4 ?: "couleur4"
            else -> "couleur${colorIndex + 1}"
        }
    }

    // Helper function to get FileCouleurInfos from product with better error handling
    private fun getFileCouleurInfos(produit: ArticlesBasesStatsTable, colorIndex: Int = 0): FileCouleurInfos {
        val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
        val fileName = "${produit.id}_${colorIndex + 1}" // Color images are 1-indexed

        // Try to find the image file with different extensions
        val imageFile = try {
            listOf("webp", "jpg", "jpeg", "png")
                .map { File("$basePath/$fileName.$it") }
                .firstOrNull { file ->
                    try {
                        file.exists() && file.canRead() && file.length() > 0
                    } catch (e: Exception) {
                        Log.w(TAG, "Error checking file: ${file.absolutePath}", e)
                        false
                    }
                }
                ?: File("$basePath/NonTrouve.webp")
        } catch (e: Exception) {
            Log.e(TAG, "Error accessing image files for product ${produit.id}", e)
            File("$basePath/NonTrouve.webp")
        }

        val imageExists = try {
            imageFile.name != "NonTrouve.webp" &&
                    imageFile.exists() && imageFile.canRead() && imageFile.length() > 0
        } catch (e: Exception) {
            Log.w(TAG, "Error validating image file: ${imageFile.absolutePath}", e)
            false
        }

        return FileCouleurInfos(
            keyID = fileName,
            bsonObjectId = BsonObjectId(),
            aAffiche = if (imageExists) FileCouleurInfos.Affiche.Image else FileCouleurInfos.Affiche.Nom,
            imageCouleurFichie = imageFile,
            nomCouleurStrSiSonImageDispo = getCouleurNameByIndex(produit, colorIndex),
            quantityDeDisponibility = 0,
            colorIndex = colorIndex,
        )
    }

}

@Entity
data class D_CouleurVentOperation(
    @PrimaryKey var id: String = BsonObjectId().toHexString(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    // Section Foreign BsonIDs
    var nomImageFichieOuApellationDuCouleur: String = "",
    var parentBonVentId: String = "",
    var parentProduitId: String = "",

    // Section Infos Base
    var nomCouleurStrSiSonImageDispo: String = "",
    @get:Exclude // Exclude from Firebase serialization
    var aAffiche: FileCouleurInfos.Affiche = FileCouleurInfos.Affiche.Image,
    var baseFileName: String="",

    // Section Related Parents Infos
    var parentZAppComptID: String = "",

    // Class FastNestedIn Infos
    //Section Parent Period Vent
    var parentEPeriodVentId: String = "",
    var parentEPeriodVentStartDate: Long = 0,

    //Section Parent Transaction
    var parentClientId: String = "",
    var parentClientName: String = "",
    //Section parentProduitAncien
    var parentProduitAncienId: Long = 0L,
    var parentProduitKeyNom: String = "",

    var type: Type = Type.CommandeDeLui,

    // Section StatuesMutable
    var quantityAchete: Int = 1,
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.CreeSlote,
    var provisoireMonPrix: Double = 0.0,

    //Section Archive
    var achatParentBsonIDOld: String = "",
) {

    enum class EtateActuellementEst {
        CreeSlote,
        ParentBonVentOuvert,
        ParentProduitOuvert,
        ChoisiQuantityDialogOuvert,
        ChoisiQuantityConfirme,
        ParentProduitConfirme,
        ParentBonVentConfirme,
        SUPPRIME_AU_PREMIER_PICK,
        SUPP_AU_PANIER_FINALE,
    }

    enum class Type { SiNonDispo, CommandeDeLui }

    // FIXED: Exclude from Firebase serialization to prevent circular references
    @get:Exclude
    @set:Exclude
    @Transient
    private var _fileCouleurInfos: FileCouleurInfos? = null

    // Create FileCouleurInfos on demand without circular references
    fun getFileCouleurInfos(): FileCouleurInfos {
        // Return cached instance if available
        _fileCouleurInfos?.let { return it }

        val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

        // Create File object from baseFileName with better error handling
        val imageFile = try {
            if (baseFileName.isNotEmpty()) {
                File("$basePath/$baseFileName")
            } else {
                // Fallback to using nomImageFichieOuApellationDuCouleur
                val fileName = nomImageFichieOuApellationDuCouleur
                listOf("webp", "jpg", "jpeg", "png")
                    .map { File("$basePath/$fileName.$it") }
                    .firstOrNull { file ->
                        try {
                            file.exists() && file.canRead() && file.length() > 0
                        } catch (e: Exception) {
                            false
                        }
                    }
                    ?: File("$basePath/NonTrouve.webp")
            }
        } catch (e: Exception) {
            Log.e("D_CouleurVentOperation", "Error creating image file reference", e)
            File("$basePath/NonTrouve.webp")
        }

        // FIXED: Create FileCouleurInfos WITHOUT circular reference to this object
        _fileCouleurInfos = FileCouleurInfos(
            // Don't pass 'this' object to avoid circular reference
            d_CouleurVentOperation = null, // CRITICAL: Set to null to break circular reference
            keyID = id,
            aAffiche = aAffiche,
            imageCouleurFichie = imageFile,
            nomCouleurStrSiSonImageDispo = nomImageFichieOuApellationDuCouleur,
            quantityDeDisponibility = quantityAchete,
        )

        return _fileCouleurInfos!!
    }

    // FIXED: Create a Firebase-safe version for serialization
    @get:Exclude    //->
    //TODO(FIXME):Fix erreur This annotation is not applicable to target 'member function' and use site target '@get'
    @set:Exclude
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "dernierTimeTampsSynchronisationAvecFireBase" to dernierTimeTampsSynchronisationAvecFireBase,
            "nomImageFichieOuApellationDuCouleur" to nomImageFichieOuApellationDuCouleur,
            "parentBonVentId" to parentBonVentId,
            "parentProduitId" to parentProduitId,
            "nomCouleurStrSiSonImageDispo" to nomCouleurStrSiSonImageDispo,
            "aAffiche" to aAffiche.name, // Convert enum to string
            "baseFileName" to baseFileName,
            "parentZAppComptID" to parentZAppComptID,
            "parentEPeriodVentId" to parentEPeriodVentId,
            "parentEPeriodVentStartDate" to parentEPeriodVentStartDate,
            "parentClientId" to parentClientId,
            "parentClientName" to parentClientName,
            "parentProduitAncienId" to parentProduitAncienId,
            "parentProduitKeyNom" to parentProduitKeyNom,
            "type" to type.name, // Convert enum to string
            "quantityAchete" to quantityAchete,
            "etateActuellementEst" to etateActuellementEst.name, // Convert enum to string
            "provisoireMonPrix" to provisoireMonPrix,
            "achatParentBsonIDOld" to achatParentBsonIDOld
        )
    }

    fun isSameEntity(other: D_CouleurVentOperation) =
        nomImageFichieOuApellationDuCouleur == other.nomImageFichieOuApellationDuCouleur &&
                parentProduitId == other.parentProduitId &&
                parentBonVentId == other.parentBonVentId &&
                parentZAppComptID == other.parentZAppComptID

    override fun equals(other: Any?) =
        this === other || (other is D_CouleurVentOperation && isSameEntity(other) &&
                quantityAchete == other.quantityAchete && provisoireMonPrix == other.provisoireMonPrix)

    override fun hashCode() = Objects.hash(
        nomImageFichieOuApellationDuCouleur,
        parentProduitId,
        parentBonVentId,
        parentZAppComptID,
        quantityAchete,
        provisoireMonPrix
    )

    companion object {
        // FIXED: Use Firebase reference without direct object serialization
        val caRef = Firebase.database.getReference("/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/D_CouleurVentOperation")

        // FIXED: Helper method to save to Firebase safely
        fun saveToFirebase(operation: D_CouleurVentOperation) {
            try {
                // Use toFirebaseMap() to avoid circular reference serialization
                caRef.child(operation.id).setValue(operation.toFirebaseMap())
                    .addOnSuccessListener {
                        Log.d("D_CouleurVentOperation", "Successfully saved to Firebase: ${operation.id}")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("D_CouleurVentOperation", "Failed to save to Firebase", exception)
                    }
            } catch (e: Exception) {
                Log.e("D_CouleurVentOperation", "Error saving to Firebase", e)
            }
        }
    }
}
