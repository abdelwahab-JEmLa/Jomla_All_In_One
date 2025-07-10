package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules

import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AfficheKeyCouleurAvecVentDebugPanieT(data: M3CouleurProduitInfos) {
    val text = "${
        data.keyID.takeLast(4).uppercase()
    } ${data.nomImageFichieSansEtansion}.${data.extensionDisponible}"

    Text(
        text = text,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .background(
                color = Color.Red,
                shape = RoundedCornerShape(bottomStart = 8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
