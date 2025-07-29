package P0_MainScreen.Main

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ColorNameDisplayer
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ImageDisplayer
import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.io.File

@SuppressLint("UnrememberedMutableState", "ModifierParameter")
@Composable
fun App_PresenterEcran_Au_Client(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedActiveValuesFacade: FocusedActiveValuesFacade = aCentralFacade.focusedActiveValuesFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    modifier: Modifier = Modifier
) {
    val focusedValuesGetter = focusedActiveValuesFacade.focusedValuesGetter
    val currentActive_M9AppCompt = focusedValuesGetter.currentActive_M9AppCompt
    val active_ProduitKeyID_Au_DroopDown_PresenterEcran =
        currentActive_M9AppCompt?.active_ProduitKeyID_Au_DroopDown_PresenterEcran

    val active_CouleurKeyID_Extended_Image by derivedStateOf {
        currentActive_M9AppCompt?.active_CouleurKeyID_Extended_Image
    }

    val relative_List_Couleurs = active_ProduitKeyID_Au_DroopDown_PresenterEcran?.let {
        repositorysMainGetter.find_ListM3CouleurInfos_By_Parent_Produit_KeyID(
            it
        )
    }

    var clickedCouleurKeyID by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(active_CouleurKeyID_Extended_Image) {
        clickedCouleurKeyID = active_CouleurKeyID_Extended_Image
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(relative_List_Couleurs.orEmpty()) { couleur ->
                val isClicked = clickedCouleurKeyID == couleur.keyID

                // Animated values for smooth transitions
                val animatedHeight by animateDpAsState(
                    targetValue = if (isClicked) 500.dp else 100.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "height_animation"
                )

                val animatedImageSize by animateDpAsState(
                    targetValue = if (isClicked) 450.dp else 80.dp,
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = if (isClicked) 0 else 100
                    ),
                    label = "image_size_animation"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(animatedHeight)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                    onClick = {
                        clickedCouleurKeyID = if (isClicked) null else couleur.keyID
                        // Optional: Update the active value in the facade as well
                        // focusedActiveValuesFacade.updateActiveCouleurKeyID(clickedCouleurKeyID)
                    }
                ) {
                    if (isClicked) {
                        // Expanded layout - image takes more space
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Display the color using CouleurDisplayer
                            CouleurDisplayer(
                                keyCouleur = couleur.keyID,
                                size = animatedImageSize,
                                modifier = Modifier.size(animatedImageSize)
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = couleur.nomImageFichieSansEtansion,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                if (couleur.nomCouleurStrSiSonImageDispo.isNotBlank()) {
                                    Text(
                                        text = couleur.nomCouleurStrSiSonImageDispo,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = "Extension: ${couleur.extensionDisponible}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Index: ${couleur.indexCouleurDansAncienProto}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        // Collapsed layout - horizontal arrangement
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Display the color using CouleurDisplayer
                            CouleurDisplayer(
                                keyCouleur = couleur.keyID,
                                size = animatedImageSize,
                                modifier = Modifier.size(animatedImageSize)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = couleur.nomImageFichieSansEtansion,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                if (couleur.nomCouleurStrSiSonImageDispo.isNotBlank()) {
                                    Text(
                                        text = couleur.nomCouleurStrSiSonImageDispo,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CouleurDisplayer(
    modifier: Modifier = Modifier,
    keyCouleur: String,
    b1CouleurOuGoutProduitDataBaseRepository: Repo03CouleurProduitInfos = koinInject(),
    size: Dp = 200.dp,
    onClickToOpenWindow: (M3CouleurProduitInfos) -> Unit = {}
) {
    val datas = b1CouleurOuGoutProduitDataBaseRepository.datasValue
    val data = datas.find { it.keyID == keyCouleur }!!

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
                M3CouleurProduitInfos.Type.Image -> {
                    ImageDisplayer(
                        modifier = Modifier.fillMaxSize(), // Changed to fillMaxSize to use available space
                        imageFile = imageFile,
                        colorName = data.nomCouleurStrSiSonImageDispo,
                        contentScale = ContentScale.Crop,
                        imageSize = DpSize(size, size),
                        onClickToOpenWindow = { onClickToOpenWindow(data) }
                    )
                }

                M3CouleurProduitInfos.Type.Nom -> ColorNameDisplayer(
                    modifier = Modifier.fillMaxSize(), // Changed to fillMaxSize to use available space
                    colorName = data.nomCouleurStrSiSonImageDispo,
                    onClickToOpenWindow = { onClickToOpenWindow(data) }
                )
            }
        }
    }
}
