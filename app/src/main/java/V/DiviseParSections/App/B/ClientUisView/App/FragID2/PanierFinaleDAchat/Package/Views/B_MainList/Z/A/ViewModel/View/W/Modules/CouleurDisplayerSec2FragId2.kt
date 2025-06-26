package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.W.Modules

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBaseRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.A.List.ColorNameDisplayer_Sec2FragID2
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.io.File

@SuppressLint("UnrememberedMutableState")
@Composable
fun CouleurDisplayerSec2FragId2(
    modifier: Modifier = Modifier,
    b1CouleurOuGoutProduitDataBaseRepository: B1CouleurOuGoutProduitDataBaseRepository = koinInject(),
    keyCouleur: String,
    onClickToOpenWindow: (B1CouleurOuGoutProduitDataBase) -> Unit = {},
    size: Dp = 200.dp,
    purchasedQuantity: Int = 0 // Added parameter for purchased quantity
) {
    val datas = b1CouleurOuGoutProduitDataBaseRepository.datasValue
    val data = datas.find { it.key == keyCouleur }!!

    val imageFile by derivedStateOf {
        if (data.nomImageFichieSansEtansion != "Non Dispo") {
            val fileName = "${data.nomImageFichieSansEtansion}.${data.extensionDisponible}"
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", fileName)
        } else null
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            when (data.aAffiche) {
                B1CouleurOuGoutProduitDataBase.Type.Image -> {
                    ImageDisplayerGlide_Sec2FragID2(
                        modifier = Modifier.size(size),
                        imageFile = imageFile,
                        colorName = data.nomCouleurStrSiSonImageDispo,
                        contentScale = ContentScale.Crop,
                        imageSize = DpSize(size, size),
                        onClickToOpenWindow = { onClickToOpenWindow(data) }
                    )
                }

                B1CouleurOuGoutProduitDataBase.Type.Nom -> ColorNameDisplayer_Sec2FragID2(
                    modifier = Modifier.size(size),
                    colorName = data.nomCouleurStrSiSonImageDispo,
                    onClickToOpenWindow = { onClickToOpenWindow(data) }
                )
            }

            if (data.key.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopEnd)
                ) {
                    AfficheKeyCouleurAvecVentDebugPanie(data)
                }
            }

            // FIX TODO(1): Added quantity display badge
            if (purchasedQuantity > 0) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = purchasedQuantity.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    // Empty content as the badge is what we want to show
                    Box(modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
