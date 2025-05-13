package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.ClientDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.TypeTarificationDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.TypeTarificationEnum
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Mock TarificationViewModel to use in the preview
class TarificationViewModel {
    private val clientMap = mapOf(
        2L to ClientDataBase(id = 2, nom = "Client Beta"),
        3L to ClientDataBase(id = 3, nom = "Client Gamma")
    )

    private val produitMap = mapOf(
        1L to ProduitInfos(id = 1, nom = "Produit A"),
        2L to ProduitInfos(id = 2, nom = "Produit B"),
        3L to ProduitInfos(id = 3, nom = "Produit C")
    )

    private val typeTarificationMap = mapOf(
        1L to TypeTarificationDataBase(id = 1, typeTarificationEnum = TypeTarificationEnum.ParBenifice),
        2L to TypeTarificationDataBase(id = 2, typeTarificationEnum = TypeTarificationEnum.Historique),
        3L to TypeTarificationDataBase(id = 3, typeTarificationEnum = TypeTarificationEnum.LeMaxPrixArrive)
    )

    fun getSqlClient(id: Long) = clientMap[id]
    fun getSqlProduit(id: Long) = produitMap[id]
    fun getSqlTypeTarification(id: Long) = typeTarificationMap[id]
}

// Helper function to format timestamp to readable date and time
fun strDateEtTempFromVidTimestamp(timestamp: Long): Pair<String, String> {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return Pair(dateFormat.format(date), timeFormat.format(date))
}

// Second preview function showing direct use of NoSqlToOutputModelPreviewProvider
@Preview(showBackground = true)
@Composable
fun PrixPrevDirect(
    @PreviewParameter(NoSqlToOutputModelPreviewProvider::class) outputModel: OutputNoSqlModel
) {
    val viewModel = remember { TarificationViewModel() }

    MaterialTheme {
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
                            "Tarification Dashboard (Direct Model)",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Button(onClick = { }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(outputModel.produits) { produit ->
                            val produitName = viewModel.getSqlProduit(produit.id)?.nom ?: "Produit ${produit.id}"
                            ProduitCard(
                                produit = produit,
                                produitName = produitName,
                                tarificationViewModel = viewModel
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Test Data")
                }
            }
        }
    }
}
