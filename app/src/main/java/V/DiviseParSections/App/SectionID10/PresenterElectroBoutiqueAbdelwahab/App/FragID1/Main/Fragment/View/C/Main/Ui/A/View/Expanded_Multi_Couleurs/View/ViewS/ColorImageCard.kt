package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.ViewS

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Expand_Produit_Couleur.updateExpandedCouleur
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import org.koin.compose.koinInject
import java.io.File

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
 fun ColorImageCard(
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    isSelected: Boolean,
    onIconClick: () -> Unit,
    on_pour_send_data: (String, String) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    val imageFile = remember(
        relative_M3CouleurProduitInfos.nomImageFichieSansEtansion,
        relative_M3CouleurProduitInfos.extensionDisponible
    ) {
        if (relative_M3CouleurProduitInfos.nomImageFichieSansEtansion != "Non Dispo") {
            val fileName =
                "${relative_M3CouleurProduitInfos.nomImageFichieSansEtansion}.${relative_M3CouleurProduitInfos.extensionDisponible}"
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", fileName)
        } else {
            null
        }
    }

    val elevation = if (isSelected) 4.dp else 2.dp

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .aspectRatio(if (isSelected) 370.dp / 500.dp else 100.dp / 60.dp)
        ) {
            if (imageFile != null && imageFile.exists()) {
                GlideImage(
                    model = imageFile,
                    contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Color image" },
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .clickable(enabled = !isSelected) {
                            updateExpandedCouleur(
                                relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                                focusedValuesGetter = focusedValuesGetter,
                                on_pour_send_data = on_pour_send_data
                            )
                        },
                    contentScale = if (isSelected) ContentScale.Companion.Fit else ContentScale.Companion.Crop
                ) {
                    it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos)
                }
            } else {
                Box(modifier = Modifier.Companion.fillMaxSize())
            }

            if (!isSelected) {
                IconButton(
                    onClick = onIconClick,
                    modifier = Modifier.Companion
                        .align(Alignment.Companion.TopStart)
                        .padding(4.dp)
                        .size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Companion.White.copy(alpha = 0.7f),
                        contentColor = Color.Companion.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Expand,
                        contentDescription = "Expand color details",
                        tint = Color.Companion.White,
                        modifier = Modifier.Companion
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.Companion.Red.copy(alpha = 0.6f))
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}                     private fun RequestBuilder<Drawable>.applyOptimizedImageOptions(
    couleur: M3CouleurProduitInfos
) = this
    .dontAnimate()
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    .priority(Priority.NORMAL)
    .dontTransform()
    .signature(ObjectKey("${couleur.keyID}_${couleur.dernierTimeTampsSynchronisationAvecFireBase}"))
    .override(400, 400)
    .disallowHardwareConfig()
    .format(DecodeFormat.PREFER_RGB_565)
    .encodeQuality(70)
    .skipMemoryCache(false)
