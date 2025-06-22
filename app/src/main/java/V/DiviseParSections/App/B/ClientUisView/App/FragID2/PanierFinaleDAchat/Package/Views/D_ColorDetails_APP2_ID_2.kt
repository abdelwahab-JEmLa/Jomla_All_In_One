package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.Windows.ColorSelectionDialogF2
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Modules.Glide.Module.Proto.CalculeCouleurHandler
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun D_ColorDetails_APP2_ID_2(
    calculeCouleurHandler: CalculeCouleurHandler = koinInject(),
    composeKeyVID: Long,
    _0_HeadOfRepositorys_Repository_Model: GroupeRepositorysProtoAvJuin3Model,
    relative_2_1_ProduitsDataBase_vid: Long?,
    onQuantitySelected: (Int) -> Unit,
) {
    val database = koinInject<AppDatabase>()
    val viewModelInitApp = koinInject<ViewModelInitApp>()

    var articlesBasesStatsModel by remember { mutableStateOf<List<Any>?>(null) }
    var showQuantityDialog by remember { mutableStateOf(false) }

    LaunchedEffect(composeKeyVID) {
        _0_HeadOfRepositorys_Repository_Model.repositoryC2_ProduitAcheteOperation
            .repositoryScope
            .launch {
                articlesBasesStatsModel = database.ArticlesBasesStatsModelDao().getAll()
            }
    }

    val relative_1_1_CouleurAcheteOperation = _0_HeadOfRepositorys_Repository_Model
        ._1_1_CouleurAcheteOperation_Repository
        .modelDatasSnapList
        .find { it.vid == composeKeyVID }


    val colorName = remember(relative_2_1_ProduitsDataBase_vid, relative_1_1_CouleurAcheteOperation?.couleurIndex_ParentVID) {
        relative_2_1_ProduitsDataBase_vid?.let { productId ->
            val product = calculeCouleurHandler.findProductById(productId)
            product?.let {
                val productImageInfos = calculeCouleurHandler.getProduitInfoImageParIndex(it)
                val colorIndex = relative_1_1_CouleurAcheteOperation?.couleurIndex_ParentVID?.toInt() ?: 0
                productImageInfos.getOrNull(colorIndex)?.colorName ?: "Unknown color"
            }
        } ?: "Unknown color"
    }

    // Create a dummy sale for the dialog
    val dummySale = remember {
        SoldArticlesTabelle()
    }

    if (showQuantityDialog && relative_1_1_CouleurAcheteOperation != null) {
        ColorSelectionDialogF2(
            onDismiss = { showQuantityDialog = false },
            currentQuantity = relative_1_1_CouleurAcheteOperation.totaleQuantity,
            colorName = colorName,
            onQuantitySelected = { newQuantity ->
                relative_1_1_CouleurAcheteOperation.let { colorItem ->
                    val updatedColorItem = colorItem.copy(
                        totaleQuantity = newQuantity
                    )
                    _0_HeadOfRepositorys_Repository_Model
                        ._1_1_CouleurAcheteOperation_Repository
                        .updateUnSeulData(updatedColorItem)

                    onQuantitySelected(newQuantity)
                    _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository.notifyDataChanged()

                }
            },
            currentSale = dummySale,
            viewModelInitApp = viewModelInitApp,
            indexColoreAcheter = relative_1_1_CouleurAcheteOperation.couleurIndex_ParentVID.toInt(),
            compose_1_1_CouleurAcheteOperationVid = composeKeyVID
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Added a white background with 30% opacity around the IconButton
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
                    .zIndex(1f)
            )

            IconButton(
                onClick = {
                    // Get the current color item
                    relative_1_1_CouleurAcheteOperation?.let { colorItem ->
                        // Create updated version with the new state
                        val updatedColorItem = colorItem.copy(
                            etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE
                        )
                        // Update the item in repository
                        _0_HeadOfRepositorys_Repository_Model
                            ._1_1_CouleurAcheteOperation_Repository
                            .upsertUneDataEtReturnVID(updatedColorItem)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp)
                    .size(24.dp)
                    .zIndex(1f)  // This ensures the button appears above other content
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete color",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            // Position the image in the center of the box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {
                A_GlideDisplayImageByKeyId_Proto_4_11(
                    produitVID = relative_2_1_ProduitsDataBase_vid,
                    couleurVID = relative_1_1_CouleurAcheteOperation?.couleurIndex_ParentVID?.plus(1),
                    size = 100.dp,
                    onImageNeExistePas = {
                        Text(
                            text = colorName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .graphicsLayer(rotationZ = 45f)  // Rotate 45 degrees
                        )
                    }
                )

                relative_1_1_CouleurAcheteOperation?.totaleQuantity?.let { quantity ->
                    if (quantity > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                .clickable {
                                    showQuantityDialog = true
                                }
                                .zIndex(2f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text =
                                    //"${relative_1_1_CouleurAcheteOperation.vid}-" +
                                    "$quantity",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }

        }
    }
}
