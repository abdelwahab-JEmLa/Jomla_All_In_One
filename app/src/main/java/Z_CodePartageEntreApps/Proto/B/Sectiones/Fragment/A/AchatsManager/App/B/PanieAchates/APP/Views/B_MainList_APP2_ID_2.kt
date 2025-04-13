package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.PanieAchates.APP.Views

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun B_MainList_APP2_ID_2(
    composeKeyVID: Long?,
    modifier: Modifier = Modifier,
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
) {
    Log.d("ProductListDebug", "All products: ${_0_HeadOfRepositorys_Repository_Model._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList.size}")
    Log.d("ProductListDebug", "composeKeyVID: $composeKeyVID")

    // Ajouter un log spécifique pour le produit avec vid 22
    val produit22 = _0_HeadOfRepositorys_Repository_Model._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList.find { it.vid == 22L }
    if (produit22 != null) {
        Log.d("ProductListDebuga", "Produit 22 trouvé: $produit22")
        Log.d("ProductListDebuga", "parent_1_3_BonAchat: ${produit22.parent_1_3_BonAchat} (attendu: $composeKeyVID)")
        Log.d("ProductListDebuga", "etateActuellementEst: ${produit22.etateActuellementEst} (attendu: ${_1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME})")

        // Vérifier les opérations de couleur associées
        val couleursForProduit22 = _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository
            .modelDatasSnapList
            .filter { it.parentProduitAchateOperationVID == 22L }

        Log.d("ProductListDebuga", "Nombre de couleurs associées: ${couleursForProduit22.size}")
        couleursForProduit22.forEachIndexed { index, couleur ->
            Log.d("ProductListDebuga", "Couleur $index: ${couleur.vid}, état: ${couleur.etateActuellementEst}")
        }

        // Vérifier chaque condition séparément
        val condition1 = produit22.parent_1_3_BonAchat == composeKeyVID
        val condition2 = produit22.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
        val condition3 = _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository
            .modelDatasSnapList
            .any {
                it.parentProduitAchateOperationVID == produit22.vid &&
                        it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
            }

        Log.d("ProductListDebuga", "Conditions pour produit 22 - C1: $condition1, C2: $condition2, C3: $condition3")
    } else {
        Log.d("ProductListDebuga", "Produit avec vid 22 non trouvé dans la liste")
    }

    // First filter color operations to find valid ones
    val validColorOperations = _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository
        .modelDatasSnapList
        .filter {
            it.parentProduitAchateOperationVID != null &&
                    it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
        }

    Log.d("ProductListDebug", "Valid color operations: ${validColorOperations.size}")

    // Then filter products that match our criteria
    val produitsBonAchatIDs = _0_HeadOfRepositorys_Repository_Model
        ._1_2_ProduitAcheteOperation_Repository
        .modelDatasSnapList
        .filter { produitOpe ->
            val condition1 = produitOpe.parent_1_3_BonAchat == composeKeyVID
            val condition2 = produitOpe.etateActuellementEst == _1_2_ProduitAcheteOperation
                .EtateActuellementEst
                .CONFIRME
            val condition3 = validColorOperations
                .any { it.parentProduitAchateOperationVID == produitOpe.vid }

            // Log détaillé pour chaque produit, avec attention spéciale pour vid 22
            if (produitOpe.vid == 22L) {
                Log.d("Product22Filter", "FILTRAGE - Product ID: ${produitOpe.vid}, C1: $condition1, C2: $condition2, C3: $condition3")
            } else {
                Log.d("ProductFilter", "Product ID: ${produitOpe.vid}, C1: $condition1, C2: $condition2, C3: $condition3")
            }

            condition1 && condition2 && condition3
        }

    Log.d("ProductListDebug", "Products after filter: ${produitsBonAchatIDs.size}")
    Log.d("ProductListDebug", "Filtered product IDs: ${produitsBonAchatIDs.map { it.vid }}")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(produitsBonAchatIDs) { produitItem ->
            C_MainItem_APP2_ID_2(
                composeKeyVID = produitItem.vid,
                _0_HeadOfRepositorys_Repository_Model = _0_HeadOfRepositorys_Repository_Model,
            )
        }
    }
}
