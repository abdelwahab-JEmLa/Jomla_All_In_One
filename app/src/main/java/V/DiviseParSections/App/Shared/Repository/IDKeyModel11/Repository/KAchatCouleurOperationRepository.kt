package V.DiviseParSections.App.Shared.Repository.IDKeyModel11.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.MainRepositorysGetterFacade.Companion.centralRef
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

@Stable
class KAchatCouleurOperationRepository(
    val fVentCouleurOperationRepository: Repo10OperationVentCouleur,
) {
    private val sourceDatas by derivedStateOf { fVentCouleurOperationRepository.datasFilteredParCurrentHVentPeriod }
    val datasValue by derivedStateOf { initImplimentaion() }
    val bProduitKeyIDToListKAchatCouleurOperation by derivedStateOf { datasValue.groupBy { it.listFCouleurVentOperation.first().parentM1ProduitInfosKeyId } }

    private fun initImplimentaion(): List<KAchatCouleurOperation> {
        val operations = sourceDatas.groupBy { it.parentM3CouleurProduitInfosKeyID }

        return operations.map { (couleurKeyId, ventOperations) ->
            val totalQuantity = ventOperations.sumOf { it.quantityAchete }

            KAchatCouleurOperation(
                parentCouleurInfosKeyID = couleurKeyId,
                sumAchatQantity = totalQuantity,
                listFCouleurVentOperation = ventOperations
            )
        }
    }
}

data class KAchatCouleurOperation(
    val keyID: String = generePushKey(),
    val parentCouleurInfosKeyID: String,
    val parentGrossistKeyID: String = generePushKey(),
    val sumAchatQantity: Int,
    val listFCouleurVentOperation: List<M10OperationVentCouleur>
) {
    companion object {
        val keyIDModel ="Model11"
        fun generePushKey() =
            centralRef.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
    }
}
