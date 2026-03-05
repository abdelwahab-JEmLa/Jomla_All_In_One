package Application4.App.A.Start

import Application4.App.Fragment.Compact_Presentoire_App_Produits_FragID4
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_NewProtoPattern(
    modifier: Modifier = Modifier,
) {
    Compact_Presentoire_App_Produits_FragID4()
}
