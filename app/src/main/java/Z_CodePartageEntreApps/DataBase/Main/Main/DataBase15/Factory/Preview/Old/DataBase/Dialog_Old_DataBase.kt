package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.Preview.Old.DataBase

import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.Preview.Preview_DataBaseInitFactory_15Grossist
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun Dialog_Old_DataBase(
    viewModel: Preview_DataBaseInitFactory_15Grossist,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Dialog(
        onDismissRequest = { viewModel.hideOldDataBaseDialog() }
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Old Database Records",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (uiState.oldDataBase.isEmpty()) {
                    Text(
                        text = "No old database records found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.oldDataBase) { oldData ->
                            OldDataItem(oldData = oldData)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { viewModel.hideOldDataBaseDialog() }
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
private fun OldDataItem(
    oldData: Preview_DataBaseInitFactory_15Grossist.Old_DataBase,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = oldData.nomSupplierSu.ifEmpty { "No Name" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (oldData.nomVocaleArabeDuSupplier.isNotEmpty()) {
                Text(
                    text = oldData.nomVocaleArabeDuSupplier,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${oldData.idSupplierSu}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Credit: ${oldData.currentCreditBalance}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (oldData.couleurSu.isNotEmpty()) {
                Text(
                    text = "Color: ${oldData.couleurSu}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rank: ${oldData.classmentSupplier}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = if (oldData.longTermCredit) "Long Term" else "Short Term",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
