package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.E_GroupedDataBasesRepository
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.lifecycle.ViewModel

class ViewModelPanierFinaleDAchat_FragIdB2(
    private val groupedDataBasesRepository: E_GroupedDataBasesRepository,
) : ViewModel() {

    fun updatePrice(
        priceText: String,
        defaultPrice: Double,
        produitAcheteOperation: _1_2_ProduitAcheteOperation?,
        repositoryModel: _0_0_HeadOfRepositorys_Model,
        updateChangePrixDeBase: Boolean = false
    ) {
        val newPrice = priceText.toDoubleOrNull() ?: defaultPrice

        produitAcheteOperation?.let { product ->
            val updatedProduct = product.copy(
                provisoireMonPrix = newPrice
            )
            repositoryModel
                .repositoryC2_ProduitAcheteOperation
                .updateUnSeulData(updatedProduct)
        }

        // Only call updateChangePrixDeBase if the flag is true
        if (updateChangePrixDeBase) {
            updateChangePrixDeBase(newPrice, produitAcheteOperation?.produitAcheterID ?: 0L)
        }
    }

    private fun updateChangePrixDeBase(newPrice: Double, produitAcheterID: Long) {
        val currentData = groupedDataBasesRepository.modelListFlow.value
            .firstOrNull()
            ?.a_ProduitInfos
            ?.find { it.id == produitAcheterID }

        currentData?.let { produitInfo ->
            val updatedProduitInfo = produitInfo.copy(
                monPrixVent = newPrice,
                needUpdate = true
            )

            // Insert the updated data
            groupedDataBasesRepository.update(
                data = updatedProduitInfo,
            )
        }
    }
}
