package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import Application5.App.Repository.M19Etudiant
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.ViewModel.Repo19Etudiant
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.util.Calendar

@Composable
fun DropDownItem_Send_Cards_WhatsApp_Parent(
    nomFun: String = "إرسال البطاقات عبر واتساب",
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    context: Context = LocalContext.current
) {
    var isSending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val todayStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val todayStudents = remember(repo19Etudiant.datasValue) {
        repo19Etudiant.datasValue
            .filter { it.dernierTimeTampsSynchronisationAvecFireBase >= todayStart && !it.absent }
            .sortedWith(compareBy<M19Etudiant> { it.positon_don_classe }.thenBy { it.creationTimestamps })
    }

    // ── Query Pictures/whatsapp_cards/MM_dd/ for today's generated cards ─────
    // Returns keyID → Uri so we match by identity, not fragile page index.
    val cardMap = remember(repo19Etudiant.datasValue) {
        getStoredCardUriMap(context)        // see WhatsAppShareUtility.kt
    }

    val studentsWithCard = remember(todayStudents, cardMap) {
        todayStudents.filter { it.num_telephone_parent.isNotBlank() && cardMap.containsKey(it.keyID) }
    }

    val totalWithPhone   = todayStudents.count { it.num_telephone_parent.isNotBlank() }
    val availableCount   = studentsWithCard.size
    val hasImages        = availableCount > 0

    fun sendAll() {
        isSending = true
        scope.launch {
            try {
                // Re-query at send time (teacher might have re-generated since composable drew)
                val freshMap = withContext(Dispatchers.IO) { getStoredCardUriMap(context) }

                val pairs = todayStudents
                    .filter { it.num_telephone_parent.isNotBlank() && freshMap.containsKey(it.keyID) }

                if (pairs.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "⚠️ لا توجد بطاقات جاهزة — أنشئها أولاً", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "📲 جاري إرسال ${pairs.size} بطاقة…", Toast.LENGTH_SHORT).show()
                }

                pairs.forEachIndexed { index, student ->
                    val uri = freshMap[student.keyID] ?: return@forEachIndexed
                    withContext(Dispatchers.Main) {
                        shareImageToWhatsAppBusiness(context, uri, student.num_telephone_parent)
                        Log.d("SendCards", "[${index+1}/${pairs.size}] ${student.nom} → ${student.num_telephone_parent}")
                    }
                    if (index < pairs.lastIndex) delay(800L)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✅ تم إرسال ${pairs.size} بطاقة", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("SendCards", "❌ ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ خطأ: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                isSending = false
            }
        }
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSending  -> MaterialTheme.colorScheme.secondaryContainer
                hasImages  -> MaterialTheme.colorScheme.primaryContainer
                else       -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                if (isSending) CircularProgressIndicator(modifier = Modifier.padding(4.dp), strokeWidth = 2.dp)
                else Icon(Icons.Default.Send, null,
                    tint = if (hasImages) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f))
            },
            text = {
                Text(
                    text = when {
                        isSending  -> "جاري الإرسال…"
                        hasImages  -> "$nomFun  ($availableCount 🖼️ / $totalWithPhone 📱)"
                        else       -> "$nomFun — أنشئ البطاقات أولاً"
                    },
                    color = if (hasImages || isSending) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            },
            onClick  = { if (!isSending && hasImages) sendAll() },
            enabled  = !isSending && hasImages
        )
    }
}
