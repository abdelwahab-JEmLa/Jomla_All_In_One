package com.example.clientjetpack.ID4.Test.Archive

import Fragment.ViewModel.TarificationViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true)
@Composable
fun PrixPrevDirect() {
    FragmentMain()
}

@Composable
private fun FragmentMain(
    viewModel: TarificationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = 0) {
            Tab(
                text = { Text("UI") },
                selected = true,
                onClick = { }
            )
            Tab(
                text = { Text("Logs") },
                selected = false,
                onClick = { }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "D_TarificationInfos Dashboard ",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(uiState.outputModel.produits) { produit ->
                        val produitName =
                            viewModel.getSqlProduitParSonNoSql(produit)?.nom
                                ?: "Produit ${produit.infosId}"

                        ProduitCard(
                            produit = produit,
                            produitName = produitName,
                            tarificationViewModel = viewModel
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

        }
    }
}
