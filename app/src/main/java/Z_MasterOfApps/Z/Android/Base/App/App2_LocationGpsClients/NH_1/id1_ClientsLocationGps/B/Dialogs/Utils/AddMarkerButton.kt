package Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.Utils

import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.ViewModel.Extension.ViewModelExtension_App2_F1
import Z_MasterOfApps.Z.Android.Res.XmlsFilesHandler.Companion.fixXmlResources
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import org.osmdroid.views.MapView

@Composable
fun AddMarkerButton(
    extensionVM: ViewModelExtension_App2_F1,
    showLabels: Boolean,
    mapView: MapView,
) {
    var clickCount by remember { mutableStateOf(0) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(fixXmlResources("reacticonanimatedjsonurl"))
    )

    DisposableEffect(Unit) {
        onDispose {
            clickCount = 0
        }
    }

    IconButton(
        onClick = {
            clickCount++
            extensionVM.onClickAddMarkerButton(mapView)
        },
        modifier = Modifier
            .size(90.dp) // Increased button size
            .semantics {
                contentDescription = "Add marker to map"
            }
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(90.dp), // Match parent size
            contentScale = ContentScale.FillBounds // Force fill available space
        )
    }
}
