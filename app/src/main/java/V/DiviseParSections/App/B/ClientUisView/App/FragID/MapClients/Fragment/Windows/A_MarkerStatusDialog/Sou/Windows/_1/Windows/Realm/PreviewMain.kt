package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

// In PreviewMain.kt
@Preview
@Composable
private fun PreviewPeriodeVenteScreen() {
    // Create a mock ViewModel for preview
    val mockViewModel = object : PeriodeVenteViewModel(
        // Create a mock repository here
        PeriodeVenteRepository(
            // Use a dummy Realm instance or mock it
            Realm.open(
                RealmConfiguration.Builder(
                    schema = setOf(PeriodeVente::class, Vendeur::class, Produit::class)
                ).inMemory().build()
            )
        )
    ) {
        // Override necessary methods if needed
    }

    MaterialTheme {
        // Pass the mock ViewModel explicitly
        PeriodeVenteScreen(viewModel = mockViewModel)
    }
}
