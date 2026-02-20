package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View.Z.Components

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.M3CouleurProduitInfos
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import org.koin.compose.koinInject
import java.io.File

@Composable
fun ColorImageDisplayer(
    colorInfo: M3CouleurProduitInfos,
    imageFile: File?,
    isImageAvailable: Boolean,
    size: Dp,
    colorMatrix: ColorMatrix?,
    onClickToOpenWindow: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    modifier: Modifier = Modifier
) {
    val currentApp_Est_Admin=focusedValuesGetter.currentApp_Est_Admin

    when {
        colorInfo.aAffiche == M3CouleurProduitInfos.Type.Nom -> {
            ColorNameDisplayer_Sec2FragID2(
                modifier = modifier.size(size),
                colorName = colorInfo.nomCouleurStrSiSonImageDispo,
                onClickToOpenWindow = onClickToOpenWindow
            )
        }

        colorInfo.aAffiche == M3CouleurProduitInfos.Type.Image && isImageAvailable -> {
            ImageDisplayerGlide_Sec2FragID2_SearchProduit(
                modifier = modifier.size(size),
                imageFile = imageFile,
                colorName = colorInfo.nomCouleurStrSiSonImageDispo,
                contentScale = ContentScale.Crop,
                imageSize = DpSize(size, size),
                colorFilter = colorMatrix?.let { ColorFilter.colorMatrix(it) },
                onClickToOpenWindow = onClickToOpenWindow,
                hideImage=false
            )
        }

        colorInfo.aAffiche == M3CouleurProduitInfos.Type.Image && !isImageAvailable -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clickable {
                        onClickToOpenWindow()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = colorInfo.nomCouleurStrSiSonImageDispo.ifBlank {
                        "Image\nNon Dispo"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = 45f
                    },
                    maxLines = 2
                )
            }
        }
    }
}
