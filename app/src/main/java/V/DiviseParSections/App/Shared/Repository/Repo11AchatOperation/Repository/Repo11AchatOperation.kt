package V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.centralRef
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase11.Factory.DataBaseInitFactory_11AchatOperation
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo11AchatOperation(
    val context: Context,
    val dataBaseCreationFactory: DataBaseInitFactory_11AchatOperation,
    val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    val repo8BonVent: Repo8BonVent,
    val repo14VentPeriode: Repo14VentPeriode,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M11AchatOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    companion object {
        private const val TAG = "Repo11AchatOperation"
        private const val VALIDATION_TAG = "AchatOperationValidation"
    }

    sealed class FilterQuery {
        data object NO_FILTER : FilterQuery()
        data class F14VentPeriode(val data: M14VentPeriode,) : FilterQuery()
        data class Grossist(val m15Grossist: M15Grossist) : FilterQuery()
        data class Client(val m2Client: M2Client) : FilterQuery()
        data object NullGrossist : FilterQuery() // New filter for null grossist
    }
    private val _filterQuery = mutableStateOf<FilterQuery>(FilterQuery.NO_FILTER)

    init {
        composScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun getValidatedAchatOperations(): List<M11AchatOperation> {
        return datasValue.filter { achatOperation ->
            val validationResult = validateAchatOperation(achatOperation)

            if (!validationResult.isValid) {
                Log.w(VALIDATION_TAG, "Invalid AchatOperation [${achatOperation.keyID.takeLast(3)}]: ${validationResult.reason}")
            }

            validationResult.isValid
        }
    }

    private fun validateAchatOperation(achatOperation: M11AchatOperation): ValidationResult {
        // Enhanced validation with better null checking
        val hasValidCouleur = achatOperation.parent_M3CouleurProduit_KeyID.isNotBlank() &&
                achatOperation.parent_M3CouleurProduit_KeyID != "null" &&
                achatOperation.parent_M3CouleurProduit_KeyID != "NULL" &&
                achatOperation.parent_M3CouleurProduit_KeyID.length > 5 // Minimum key length check

        val hasValidProduit = achatOperation.parent_M1Produit_KeyID.isNotBlank() &&
                achatOperation.parent_M1Produit_KeyID != "null" &&
                achatOperation.parent_M1Produit_KeyID != "NULL" &&
                achatOperation.parent_M1Produit_KeyID.length > 5 // Minimum key length check

        // Enhanced grossist validation - allow null but validate if present
        val hasValidGrossist = achatOperation.parent_M15Grossist_KeyID.isBlank() ||
                achatOperation.parent_M15Grossist_KeyID == "null" ||
                (achatOperation.parent_M15Grossist_KeyID.isNotBlank() &&
                        achatOperation.parent_M15Grossist_KeyID != "null" &&
                        achatOperation.parent_M15Grossist_KeyID.length > 5)

        // Additional validation for quantity and price
        val hasValidQuantity = achatOperation.sumAchatQantity >= 0
        val hasValidPrice = achatOperation.prix_Achat_De_Cette_Grossist >= 0.0

        return when {
            !hasValidCouleur -> ValidationResult(false, "Invalid couleur key: '${achatOperation.parent_M3CouleurProduit_KeyID}' (length: ${achatOperation.parent_M3CouleurProduit_KeyID.length})")
            !hasValidProduit -> ValidationResult(false, "Invalid produit key: '${achatOperation.parent_M1Produit_KeyID}' (length: ${achatOperation.parent_M1Produit_KeyID.length})")
            !hasValidGrossist -> ValidationResult(false, "Invalid grossist key: '${achatOperation.parent_M15Grossist_KeyID}' (length: ${achatOperation.parent_M15Grossist_KeyID.length})")
            !hasValidQuantity -> ValidationResult(false, "Invalid quantity: ${achatOperation.sumAchatQantity}")
            !hasValidPrice -> ValidationResult(false, "Invalid price: ${achatOperation.prix_Achat_De_Cette_Grossist}")
            else -> ValidationResult(true, "Valid")
        }
    }

    data class ValidationResult(
        val isValid: Boolean,
        val reason: String
    )

    // Make filteredDatasValue public so it can be accessed from the list component
    val filteredDatasValue by derivedStateOf {
        val validatedData = getValidatedAchatOperations()

        Log.d(TAG, "Filtering ${validatedData.size} validated operations with filter: ${_filterQuery.value}")

        try {
            when (val filter = _filterQuery.value) {
                FilterQuery.NO_FILTER -> validatedData

                is FilterQuery.F14VentPeriode -> validatedData.filter {
                    it.parent_M14VentPeriod_KeyID == filter.data.keyID
                }

                is FilterQuery.Grossist -> validatedData.filter {
                    it.parent_M15Grossist_KeyID == filter.m15Grossist.keyID
                }

                FilterQuery.NullGrossist -> validatedData.filter {
                    it.parent_M15Grossist_KeyID == "null" || it.parent_M15Grossist_KeyID.isBlank()
                }

                is FilterQuery.Client -> validatedData.filter { achatOperation ->
                    try {
                        val relatedSalesOperations = achatOperation.get_list_v_Depuit_joinedStringKeys(
                            repo10OperationVentCouleur.datasValue
                        )

                        relatedSalesOperations.any { salesOperation ->
                            try {
                                val bonVent = repo8BonVent.datasValue.find {
                                    it.keyID == salesOperation.parentM8BonVentKeyId
                                }
                                bonVent?.parent_M2Client_KeyID == filter.m2Client.keyID
                            } catch (e: Exception) {
                                Log.e(TAG, "Error finding bonVent for salesOperation: ${e.message}")
                                false
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error filtering by client for operation ${achatOperation.keyID.takeLast(3)}: ${e.message}")
                        false
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in filteredDatasValue: ${e.message}")
            emptyList()
        }
    }

    // Update the bProduitKeyID_To_List_KAchatCouleurOperation to handle null safety
    val bProduitKeyID_To_List_KAchatCouleurOperation by derivedStateOf {
        try {
            filteredDatasValue.mapNotNull { achatOperation ->
                try {
                    val relatedSalesOperations = achatOperation.get_list_v_Depuit_joinedStringKeys(
                        repo10OperationVentCouleur.datasValue
                    )

                    if (relatedSalesOperations.isNotEmpty()) {
                        val salesOperation = relatedSalesOperations.firstOrNull()
                        val produitKeyId = salesOperation?.parentM1ProduitInfosKeyId

                        if (!produitKeyId.isNullOrBlank() && produitKeyId != "null" && produitKeyId.length > 5) {
                            produitKeyId to achatOperation
                        } else {
                            Log.w(TAG, "Invalid produit key for operation ${achatOperation.keyID.takeLast(3)}: '$produitKeyId'")
                            null
                        }
                    } else {
                        Log.w(TAG, "No related sales operations for achat operation ${achatOperation.keyID.takeLast(3)}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing achat operation ${achatOperation.keyID.takeLast(3)}: ${e.message}")
                    null
                }
            }.groupBy({ it.first }, { it.second })
        } catch (e: Exception) {
            Log.e(TAG, "Error in bProduitKeyID_To_List_KAchatCouleurOperation: ${e.message}")
            emptyMap()
        }
    }

    // Method to update filter query
    fun updateFilterQuery(newFilter: FilterQuery) {
        _filterQuery.value = newFilter
    }

    fun genere_Achats_Depuit_M11AchatOperation_List(
        m14VentPeriod: M14VentPeriode?,
        filtered_ListM10Vent_BY_Curr_M14VentPeriod: List<M10OperationVentCouleur>,
        produits: List<ArticlesBasesStatsTable>
    ): List<M11AchatOperation> {

        return try {
            val operations = filtered_ListM10Vent_BY_Curr_M14VentPeriod.groupBy {
                it.parentM3CouleurProduitInfosKeyID
            }

            operations.mapNotNull { (couleurKeyId, ventOperations) ->
                try {
                    // Validate couleur key
                    if (couleurKeyId.isBlank() || couleurKeyId == "null" || couleurKeyId.length <= 5) {
                        Log.w(TAG, "Skipping invalid couleur key: '$couleurKeyId'")
                        return@mapNotNull null
                    }

                    val totalQuantity = ventOperations.sumOf { it.quantity }

                    // Validate quantity
                    if (totalQuantity <= 0) {
                        Log.w(TAG, "Skipping operation with invalid quantity: $totalQuantity")
                        return@mapNotNull null
                    }

                    // Find the last achat operation with the same M3CouleurProduit
                    val lastAchatWithSameCouleur = datasValue
                        .filter { it.parent_M3CouleurProduit_KeyID == couleurKeyId }
                        .maxByOrNull { it.creationTimestamp }

                    // Get grossist info from the last achat operation
                    val grossistDebugInfos = lastAchatWithSameCouleur?.parent_M15Grossist_DebugInfos ?: "Non Defini Gros"
                    val grossistKeyID = lastAchatWithSameCouleur?.parent_M15Grossist_KeyID ?: "null"

                    val parent_M1Produit_KeyID = ventOperations.firstOrNull()?.parentM1ProduitInfosKeyId

                    // Validate produit key
                    if (parent_M1Produit_KeyID.isNullOrBlank() || parent_M1Produit_KeyID == "null" || parent_M1Produit_KeyID.length <= 5) {
                        Log.w(TAG, "Skipping operation with invalid produit key: '$parent_M1Produit_KeyID'")
                        return@mapNotNull null
                    }

                    val parent_M1Produit = produits.find { it.keyID == parent_M1Produit_KeyID }
                    val prix_Achat = parent_M1Produit?.prixAchat ?: 0.0

                    // Validate price
                    if (prix_Achat < 0.0) {
                        Log.w(TAG, "Invalid price for produit $parent_M1Produit_KeyID: $prix_Achat")
                    }

                    M11AchatOperation.get_default().first.copy(
                        prix_Achat_De_Cette_Grossist = prix_Achat,
                        parent_M15Grossist_DebugInfos = grossistDebugInfos,
                        parent_M15Grossist_KeyID = grossistKeyID,
                        parent_M14VentPeriod_KeyID = m14VentPeriod?.keyID ?: "null",
                        parent_M1Produit_DebugInfos = ventOperations.firstOrNull()?.parentM1ProduitDebugInfos ?: "Unknown Product",
                        parent_M1Produit_KeyID = parent_M1Produit_KeyID,
                        parent_M3CouleurProduit_DebugInfos = ventOperations.firstOrNull()?.parentM3CouleurProduitDebugInfos ?: "Unknown Color",
                        parent_M3CouleurProduit_KeyID = couleurKeyId,
                        sumAchatQantity = totalQuantity,
                        joined_Str_keys_De_Relatives_FCouleurVentOperation = ventOperations.joinToString(",") { it.keyID }
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error generating achat operation for couleur $couleurKeyId: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in genere_Achats_Depuit_M11AchatOperation_List: ${e.message}")
            emptyList()
        }
    }

    fun add_New(data: M11AchatOperation) {
        val dataUpdate = data.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )

        composScope.launch {
            try {
                withContext(Dispatchers.Main.immediate) {
                    _datas.value = _datas.value.toMutableList().apply {
                        add(dataUpdate)
                    }
                }
                dataBaseCreationFactory.addOrUpdatedAncienRepo(-1, dataUpdate)
            } catch (e: Exception) {
                Log.e(TAG, "Error adding new operation: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error adding operation: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun update_If_Exist(data: M11AchatOperation) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Item not found, cannot update", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }

        val updatedItem = data.copy(
            keyID = datasValue[existingIndex].keyID,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )

        composScope.launch {
            try {
                withContext(Dispatchers.Main.immediate) {
                    _datas.value = datasValue.toMutableList().apply {
                        this[existingIndex] = updatedItem
                    }
                }
                dataBaseCreationFactory.addOrUpdatedAncienRepo(existingIndex, updatedItem)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating operation: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error updating operation: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun deleteMulti(data: List<M11AchatOperation>) {
        composScope.launch {
            try {
                val keyIDsToDelete = data.map { it.keyID }.toSet()
                _datas.value = datasValue.filter { it.keyID !in keyIDsToDelete }
                data.forEach { item ->
                    dataBaseCreationFactory.delete(item)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting multiple operations: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error deleting items: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun delete(data: M11AchatOperation) {
        composScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting operation: ${e.message}")
            }
        }
    }
}

@Entity
data class M11AchatOperation(
    @PrimaryKey
    val keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    //---------------------------------ForgingKeys.----------------------------------------------------------------------------------------------------------------------------------
    val parent_M14VentPeriod_DebugInfos: String = "null",
    val parent_M14VentPeriod_KeyID: String = "null",
    //---------------------------------ForgingKeys.----------------------------------------------------------------------------------------------------------------------------------
    val parent_M1Produit_DebugInfos: String = "null",
    val parent_M1Produit_KeyID: String = "null",
    //---------------------------------ForgingKeys.----------------------------------------------------------------------------------------------------------------------------------
    val parent_M3CouleurProduit_DebugInfos: String = "null",
    val parent_M3CouleurProduit_KeyID: String = "null",
    //---------------------------------ForgingKeys.----------------------------------------------------------------------------------------------------------------------------------
    val parent_M15Grossist_DebugInfos: String = "null",
    val parent_M15Grossist_KeyID: String = "null",
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------

    val prix_Achat_De_Cette_Grossist: Double = 0.0,
    val sumAchatQantity: Int = 0,
    val joined_Str_keys_De_Relatives_FCouleurVentOperation: String = ""
) {
    fun get_DebugInfos(): String {
        return buildString {
            append("(M11=")
            append("")
            append("[")
            append(keyID.takeLast(3).uppercase())
            append("])")
        }
    }

    fun get_list_v_Depuit_joinedStringKeys(repo10datas: List<M10OperationVentCouleur>): List<M10OperationVentCouleur> {
        return try {
            if (joined_Str_keys_De_Relatives_FCouleurVentOperation.isBlank()) {
                emptyList()
            } else {
                val keyIds = joined_Str_keys_De_Relatives_FCouleurVentOperation
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() && it != "null" }

                if (keyIds.isEmpty()) {
                    emptyList()
                } else {
                    repo10datas.filter { operation ->
                        operation.keyID in keyIds
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("M11AchatOperation", "Error parsing joined string keys: ${e.message}")
            emptyList()
        }
    }

    companion object {
        val ref = centralRef.child("Datas_11AchatOperation")

        fun generePushKey() =
            ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")

        fun get_default(): Pair<M11AchatOperation, Modifier> {
            val data = M11AchatOperation()
            val modifier = Modifier.getSemanticsTag(
                nomVal = "m11AchatOperation",
                data = data
            )
            return Pair(data, modifier)
        }

        fun find_depuit_DB(
            data_List: List<M11AchatOperation>,
            data_A_Cherche: M11AchatOperation,
        ) = data_List.find { data ->
            data.keyID == data_A_Cherche.keyID
        }
    }
}
