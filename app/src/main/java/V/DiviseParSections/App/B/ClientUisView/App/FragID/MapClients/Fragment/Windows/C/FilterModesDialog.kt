package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.C

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

private val filtersToShow = listOf(
    MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR,
    MapClientsViewModel.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX,
    MapClientsViewModel.VisibleClientsNow.showClientsWithConfirmedProducts
)

@Composable
fun FilterView(
    currentFilterMode: MapClientsViewModel.VisibleClientsNow,
    onFilterSelect: (MapClientsViewModel.VisibleClientsNow) -> Unit,
    onDismiss: () -> Unit
) = Dialog(onDismissRequest = onDismiss) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // En-tête du dialog
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sélectionner le mode de filtrage",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer"
                    )
                }
            }

            // Liste des filtres
            LazyColumn {
                items(filtersToShow) { filterMode ->
                    // Élément de filtre intégré directement ici
                    val backgroundColor = if (filterMode == currentFilterMode)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onFilterSelect(filterMode)
                                onDismiss()
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Affichage de l'icône selon son type
                        when (val icon = filterMode.icon) {
                            is ImageVector -> {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = filterMode.couleur,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Text(
                            text = filterMode.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (filterMode == currentFilterMode) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
