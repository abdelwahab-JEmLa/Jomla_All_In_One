package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init.onLoadFromFireBase
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.DataBaseFactoryDCouleurAchatOperation
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo10OperationVentCouleur(
    val context: Context,
    val dataBaseCreationFactory: DataBaseFactoryDCouleurAchatOperation,
    val zAppComptRepositoryComposable: Repo9AppCompt,
) {
    val dao = dataBaseCreationFactory.dao
    val repoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val depuitTestData = false
    private val _datas = mutableStateOf<List<M10OperationVentCouleur>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    val onVentFilteredDatas by derivedStateOf {
        val targetKey = zAppComptRepositoryComposable.currentAppCompt?.onVentM8BonVentKey
        datasValue.filter { it.parent_M8BonVent_KeyId == targetKey }
    }

    init {
        repoScope.launch {
            try {
                if (depuitTestData) {
                    withContext(Dispatchers.Main) {
                        _datas.value = getTestDate()
                    }
                } else {
                    dao.getAllFlow().collect { data ->
                        try {
                            withContext(Dispatchers.Main) {
                                _datas.value = data
                            }
                        } catch (e: Exception) {
                        }
                    }
                    // Clean up invalid operations if M18 parameter is enabled
                    if (_datas.value.isNotEmpty() && M00CentralParametresOfAllApps().au_Lence_Diminue_DatasFB) {
                        cleanupInvalidOperations(this@Repo10OperationVentCouleur)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun refresh_Datas() {
        repoScope.launch {
            try {
                dataBaseCreationFactory.dao.deleteAll()

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = emptyList()
                }

                val freshDataFromFirebase = dataBaseCreationFactory.onLoadFromFireBase()

                dataBaseCreationFactory.dao.insertAll(freshDataFromFirebase)

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = freshDataFromFirebase
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Data refreshed successfully", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to refresh data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun addOrUpdateData(data: M10OperationVentCouleur) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            M10OperationVentCouleur.isSame(ancien = ancien, newData = data)
        }

        _datas.value = if (existingIndex >= 0) {
            datasValue.toMutableList().apply {
                val updatedItem = data.copy(
                    keyID = datasValue[existingIndex].keyID,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
                this[existingIndex] = updatedItem
            }
        } else {
            val newItem = data.copy(
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            datasValue + newItem
        }

        val dataForRepo = if (existingIndex >= 0) {
            data.copy(
                keyID = datasValue[existingIndex].keyID,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        } else {
            data.copy(
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        }

        dataBaseCreationFactory.addOrUpdatedAncienRepo(existingIndex, dataForRepo)
    }

    fun fixExistingOperationsWithEmptyBonVentKey() {
        val currentBonVentKey = zAppComptRepositoryComposable.currentAppCompt?.onVentM8BonVentKey
        if (currentBonVentKey.isNullOrEmpty()) return

        val operationsToFix = datasValue.filter { it.parent_M8BonVent_KeyId.isEmpty() }
        operationsToFix.forEach { operation ->
            val fixedOperation = operation.copy(
                parent_M8BonVent_KeyId = currentBonVentKey,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            addOrUpdateData(fixedOperation)
        }
    }

    fun acheterUneCouleur(
        zCompt: M09AppCompt,
        relatedVentOperation: M3CouleurProduitInfos,
        quantity: Int,
    ) {
        repoScope.launch {
            try {
                val couleurVentOperation = createSafeCouleurVentOperation(
                    relatedCouleur = relatedVentOperation,
                    zCompt = zCompt,
                    quantity = quantity
                )
                addOrUpdateData(couleurVentOperation)
            } catch (e: OutOfMemoryError) {
                System.gc()
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    private fun createSafeCouleurVentOperation(
        relatedCouleur: M3CouleurProduitInfos,
        zCompt: M09AppCompt,
        quantity: Int
    ): M10OperationVentCouleur {
        return M10OperationVentCouleur(
            parent_M14VentPeriod_KeyId = zCompt.current_OnVent_M14VentPeriode_KeyID,
            parent_M8BonVent_KeyId = zCompt.onVentM8BonVentKey,
            parent_M1Produit_KeyId = relatedCouleur.parentBProduitInfosKeyID,
            parent_M1Produit_DebugInfos = relatedCouleur.parentId1ProduitInfosDebugName,
            parentProduitInfosOldId = relatedCouleur.parentBProduitOldID,
            parent_M3CouleurProduit_KeyID = relatedCouleur.keyID,
            etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ChoisiQuantityConfirme,
            quantity = quantity,
            type = M10OperationVentCouleur.Type.CommandeDeLui,
        )
    }

    fun delete(data: M10OperationVentCouleur) {
        repoScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun getTestDate(): List<M10OperationVentCouleur> {
        return emptyList()
    }

    companion object {
        private const val TAG = "ColorOperation"
        fun String?.findData(repo: Repo10OperationVentCouleur) =
            repo.datasValue.find { it.keyID == this }
    }

    fun add_New(data: M10OperationVentCouleur) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        dataBaseCreationFactory.addOrUpdatedAncienRepo(-1, data)
    }

    fun update_If_Exist(data: M10OperationVentCouleur) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            repoScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Item not found, cannot update", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            return
        }

        val updatedItem = data.copy(
            keyID = datasValue[existingIndex].keyID,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply {
                    this[existingIndex] = updatedItem
                }
            }
        }

        dataBaseCreationFactory.addOrUpdatedAncienRepo(existingIndex, data)
    }
}

