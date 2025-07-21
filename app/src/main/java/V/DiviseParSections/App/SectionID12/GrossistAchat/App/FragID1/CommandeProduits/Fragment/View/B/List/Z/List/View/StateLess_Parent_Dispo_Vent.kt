package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.View

import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.CouleurDisplayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StateLess_Parent_Dispo_Vent(
    client_nom: String,
    quantity: String,
    relative_M3Couleur_KeyId: String?
) {
    Card {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(
                    color = Color.White.copy(alpha = 0.40f),
                    shape = RoundedCornerShape(10.dp)
                ),
        ) {
            if (relative_M3Couleur_KeyId != null) {
                CouleurDisplayer(keyCouleur = relative_M3Couleur_KeyId, size = 80.dp)
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = client_nom,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = quantity,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
        VerticalDivider()
    }
}

