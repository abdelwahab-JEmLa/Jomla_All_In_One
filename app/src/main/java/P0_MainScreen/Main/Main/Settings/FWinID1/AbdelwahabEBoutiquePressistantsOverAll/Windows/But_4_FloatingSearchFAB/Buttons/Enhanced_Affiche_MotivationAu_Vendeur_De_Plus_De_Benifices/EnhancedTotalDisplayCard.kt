package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.util.concurrent.TimeUnit

@Composable
fun EnhancedTotalDisplayCard(
    totalProducts: Int,
    totalRevenue: Double,
    profitabilityAnalysis: String,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    val activeOnVent_M8BonVent = focusedValuesGetter.activeOnVent_M8BonVent
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Live time update every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Update every second
            currentTime = System.currentTimeMillis()
        }
    }
    (totalRevenue > 0).ifTrue {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50).copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Static order summary text (replaces blinking animation)
                activeOnVent_M8BonVent?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "طلبية $totalProducts ${get_BestNomArabDuPlurieul(totalProducts)}  لوقت اجمالي ${
                                getTimeElapsedStringWithSeconds(
                                    it.creationTimestamps,
                                    currentTime
                                )
                            }",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// Enhanced function with seconds support for live updates
fun getTimeElapsedStringWithSeconds(
    creationTimestamp: Long,
    currentTime: Long = System.currentTimeMillis()
): String {
    val elapsed = currentTime - creationTimestamp
    val days = TimeUnit.MILLISECONDS.toDays(elapsed)
    val hours = TimeUnit.MILLISECONDS.toHours(elapsed) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed) % 60

    return when {
        days > 0 -> "${days}ي ${hours}س"
        hours > 0 -> "${hours}س ${minutes}د"
        minutes > 0 -> "${minutes}د ${seconds}ث"
        else -> "${seconds} ث "
    }
}
