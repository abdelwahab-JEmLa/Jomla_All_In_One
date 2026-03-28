package Application4.App.Fragment.ID1.Fragment

import Application4.App.A.Start.Init.Proto.DropBox_Init
import Application4.App.A.Start.Init.Proto.Empty_App_Initialize_M1_3_16_App4Proto2
import Application4.App.A.Start.Init.Proto.Init_LightDataBases
import EntreApps.Shared.Models.Do
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.ifTrue
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.compose.koinInject

@Composable
fun A_Loading_Init_Screen(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase = koinInject()
) {
    val context = LocalContext.current
    var activeCompt by remember { mutableStateOf<Z_AppCompt?>(null) }
    var initDone by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var currentJobName by remember { mutableStateOf("") }

    fun setProgress(p: Float, job: String = currentJobName) {
        progress = p; currentJobName = job
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            val key = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            activeCompt = Z_AppCompt.ref.get().await()
                .children.mapNotNull { it.getValue(Z_AppCompt::class.java) }
                .find { it.keyID == key }
        }.join()

        (activeCompt?.next_start == Do.DeleteInsertAll_Active_Key).ifTrue {
            var seedResult = Empty_App_Initialize_M1_3_16_App4Proto2.SeedResult()
            val seedJob = launch(Dispatchers.IO) {
                seedResult = Empty_App_Initialize_M1_3_16_App4Proto2.getReturne_M1_3_16(
                    context = context,
                    on_Progress_Datas = { p -> setProgress(p, "Chargement produits…") },
                )
                launch {
                    DropBox_Init.syncAll(seedResult.colors) { p ->
                        setProgress(
                            p,
                            "Sync images…"
                        )
                    }
                }.join()
            }
            seedJob.join()

            val lightDbJob = launch(Dispatchers.IO) {
                setProgress(progress, "Chargement tarifs…")
                val r = Init_LightDataBases.returne_FireBase_LightDataBases()
                if (r.m13TarificationInfos.isNotEmpty()) appDatabase.dao_M13TarificationInfos()
                    .insertAll(r.m13TarificationInfos)
                if (r.m14VentPeriode.isNotEmpty()) appDatabase.dao_M14VentPeriode()
                    .insertAll(r.m14VentPeriode)
                if (r.m8BonVent.isNotEmpty()) appDatabase.dao_M8BonVent().insertAll(r.m8BonVent)
                if (r.m10OperationVentCouleur.isNotEmpty()) appDatabase.dao_M10OperationVentCouleur()
                    .insertAll(r.m10OperationVentCouleur)
            }
            val insertJob = launch(Dispatchers.IO) {
                setProgress(progress, "Insertion locale…")
                if (seedResult.colors.isNotEmpty()) appDatabase.dao_M03CouleurProduitInfos()
                    .insertAll(seedResult.colors)
                if (seedResult.products.isNotEmpty()) appDatabase.dao_M1Produit()
                    .insertAll(seedResult.products)
                if (seedResult.categories.isNotEmpty()) appDatabase.dao_16CategorieProduit()
                    .insertAll(seedResult.categories)
            }
            insertJob.join()
            lightDbJob.join()
        }

        setProgress(1f, "Prêt ✓")
        initDone = true
    }

    if (initDone) A_Compact_Presentoire_App_Produits_App4()
    else A_Loading_UI(progress, currentJobName)
}

@Composable
private fun A_Loading_UI(progress: Float, currentJobName: String) {
    val logoAlpha by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = ""
    )
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(com.example.clientjetpack.R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(220.dp)
                .alpha(logoAlpha)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                currentJobName,
                fontSize = 13.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFF4CAF50),
                trackColor = Color.White.copy(alpha = 0.25f),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "${(progress * 100).toInt()} %",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
