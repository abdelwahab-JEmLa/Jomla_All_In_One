package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.View

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Function.formatTimestamp
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitsNoSqlDataBase
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProductClientInfoCard(
    modifier: Modifier = Modifier,
    produitsNoSqlDataBase: ProduitsNoSqlDataBase,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Safely access active product with proper null handling
            val activeProduit = produitsNoSqlDataBase.produits.find { it.itsActiveOne }

            Text(
                text = "produit.name > (Id=${activeProduit?.infosId ?: "Unknown"})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Safely access active client with proper null handling
            val activeClient = activeProduit?.clientAchteurs?.find { it.itsActiveOne }

            Text(
                text = "Client ID: ${activeClient?.infosId ?: "None"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val date = formatTimestamp(System.currentTimeMillis()).first
            val time = formatTimestamp(System.currentTimeMillis()).second

            Text(
                text = "Date: $date $time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
