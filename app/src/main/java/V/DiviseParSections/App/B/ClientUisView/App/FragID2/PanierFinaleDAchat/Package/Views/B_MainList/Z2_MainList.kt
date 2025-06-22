package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.D_AchatOperation
import Z_CodePartageEntreApps.Modules.D.Glide.Module.CouleurInfos
import Z_CodePartageEntreApps.Modules.D.Glide.Module.LazyRowAvailableColorsImageOuNom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.mongodb.kbson.BsonObjectId
import java.io.File

@Preview @Composable private fun Sec1Frag3Prv() { Sec1Frag3() }

@Composable
fun Sec1Frag3(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel()
) {
    val achats = viewModel.d_AchatOperationComposeRepositoryPJ17.filteredDatasValue

    MainList(
        modifier = modifier,
        viewModel=viewModel,
        achats = achats
    )
}

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    achats: List<D_AchatOperation> = emptyList(),
    viewModel: ZViewModel_Sec1Frag3
) {
    // Group achats by parentProduitBsonObjectId
    val groupedAchats = achats.groupBy { it.parentProduitBsonObjectId }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(groupedAchats.entries.toList()) { (productId, achatGroup) ->
            ProductGroup(
                viewModel=viewModel,
                productId = productId,
                achats = achatGroup
            )
        }
    }
}

@Composable
fun ProductGroup(
    productId: String,
    achats: List<D_AchatOperation>,
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3
) {
    val relatedProduitDataBase = viewModel.a_ProduitDataBaseComposeRepositoryPJ17.datasValue.find { it.bsonObjectId == productId }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = relatedProduitDataBase?.nom ?: "Product ID: $productId",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${achats.size} item(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show color options for the product if available
            relatedProduitDataBase?.let { produit ->
                val couleurInfos = remember(produit.id) {
                    createCouleurInfosFromProduct(produit)
                }

                if (couleurInfos.isNotEmpty()) {
                    LazyRowAvailableColorsImageOuNom(
                        data = produit,
                        couleurInfos = couleurInfos,
                        reloadTrigger = 0
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Horizontal scrollable list of purchase operations
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(achats) { achat ->
                    MainItem(
                        achat = achat,
                        modifier = Modifier.width(200.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MainItem(
    achat: D_AchatOperation,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (achat.etateActuellementEst) {
                D_AchatOperation.EtateActuellementEst.CONFIRME -> MaterialTheme.colorScheme.primaryContainer
                D_AchatOperation.EtateActuellementEst.Affiche -> MaterialTheme.colorScheme.surfaceVariant
                D_AchatOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK -> MaterialTheme.colorScheme.errorContainer
                D_AchatOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = achat.nomImageFichieOuApellationDuCouleur,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Qty: ${achat.quantityAchete}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${achat.provisoireMonPrix}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Status indicator
            Text(
                text = when (achat.etateActuellementEst) {
                    D_AchatOperation.EtateActuellementEst.CONFIRME -> "Confirmed"
                    D_AchatOperation.EtateActuellementEst.Affiche -> "Display"
                    D_AchatOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK -> "Removed"
                    D_AchatOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE -> "Deleted"
                },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = when (achat.etateActuellementEst) {
                    D_AchatOperation.EtateActuellementEst.CONFIRME -> MaterialTheme.colorScheme.primary
                    D_AchatOperation.EtateActuellementEst.Affiche -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

// Helper function to create CouleurInfos from ArticlesBasesStatsTable
private fun createCouleurInfosFromProduct(produit: ArticlesBasesStatsTable): List<CouleurInfos> {
    val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val couleurInfosList = mutableListOf<CouleurInfos>()

    // Check each color and create CouleurInfos if available
    listOf(
        produit.couleur1 to 1,
        produit.couleur2 to 2,
        produit.couleur3 to 3,
        produit.couleur4 to 4
    ).forEach { (couleur, index) ->
        if (!couleur.isNullOrBlank()) {
            val fileName = "${produit.id}_$index"
            val imageFile = listOf("jpg", "webp", "jpeg", "png")
                .map { File("$basePath/$fileName.$it") }
                .firstOrNull { it.exists() && it.canRead() && it.length() > 0 }
                ?: File("$basePath/NonTrouve.webp")

            couleurInfosList.add(
                CouleurInfos(
                    bsonObjectId = BsonObjectId(),
                    imageNameSiDispo = imageFile.name,
                    aAffiche = if (imageFile.exists()) CouleurInfos.Affiche.Image else CouleurInfos.Affiche.Nom,
                    imageCouleurFichie = imageFile,
                    nomSiDispo = couleur,
                    counteDeDisponibility = if (imageFile.exists()) 1 else 0
                )
            )
        }
    }

    return couleurInfosList
}
