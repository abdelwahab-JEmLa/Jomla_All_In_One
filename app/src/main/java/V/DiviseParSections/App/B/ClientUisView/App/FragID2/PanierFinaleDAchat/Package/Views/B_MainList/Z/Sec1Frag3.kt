package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.Module.LazyRowAvailableColorsImageOuNom
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.D_AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Sec1Frag3Prv() {
    Sec1Frag3()
}

@Composable
fun Sec1Frag3(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel()
) {
    val achats = viewModel.d_AchatOperationComposeRepositoryPJ17.filteredDatasValue

    MainList(
        modifier = modifier,
        viewModel = viewModel,
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
                viewModel = viewModel,
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
                    text = productId,
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

            LazyRowAvailableColorsImageOuNom(productId, achats, viewModel.a_ProduitDataBaseComposeRepositoryPJ17)
        }
    }
}
