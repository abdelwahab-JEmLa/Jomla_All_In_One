package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List.List_AcheteursDeCetteProduit
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.CouleurDisplayer
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun View_AchatCouleur(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    achatCouleur: M11AchatOperation
) {
    Card(
        modifier = Modifier.background(Color.Red)
    ) {
        Column {
            Box {
                // Add null safety check for the color key
                val couleurKey = achatCouleur.parent_M3CouleurProduit_KeyID
                if (couleurKey.isNotEmpty()) {
                    CouleurDisplayer(keyCouleur = couleurKey)
                } else {
                    // Fallback UI when color key is null or empty
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                color = Color.Gray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            text = "Couleur non disponible",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.70f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                ) {
                    Text(
                        text = "Qté: ${achatCouleur.sumAchatQantity}",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            List_AcheteursDeCetteProduit(viewModel, achatCouleur)
        }
    }
}
