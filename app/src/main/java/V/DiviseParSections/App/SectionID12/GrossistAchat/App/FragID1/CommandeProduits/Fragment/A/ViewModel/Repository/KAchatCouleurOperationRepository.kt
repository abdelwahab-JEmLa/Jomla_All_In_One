package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository

import V.DiviseParSections.App.Shared.Repository.ACentralCompoRepositoryProtoJuin9.Companion.centralRef
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

@Stable
class KAchatCouleurOperationRepository(
    val fVentCouleurOperationRepository: FVentCouleurOperationRepository,
) {
    private val sourceDatas by derivedStateOf { fVentCouleurOperationRepository.datasFilteredParCurrentHVentPeriod }
    val datasValue by derivedStateOf { initImplimentaion() }
    val bProduitKeyIDToListKAchatCouleurOperation by derivedStateOf { datasValue.groupBy { it.listFCouleurVentOperation.first().parentBProduitInfosKeyId } }

    private fun initImplimentaion(): List<KAchatCouleurOperation> {
        val operations = sourceDatas.groupBy { it.parentCouleurInfosKeyID }

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
    val listFCouleurVentOperation: List<FCouleurVentOperationInfos>
) {
    companion object {
        fun generePushKey() =
            centralRef.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
    }
}
