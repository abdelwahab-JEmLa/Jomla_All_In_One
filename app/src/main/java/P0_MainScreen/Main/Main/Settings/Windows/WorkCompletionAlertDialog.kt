package P0_MainScreen.Main.Main.Settings.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import P0_MainScreen.Main.Main.Settings.Windows.g.HistoriqueWorck
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Repo18CentralParametresOfAllApps
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun WorkCompletionAlertDialog(
    modifier: Modifier = Modifier,
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repo18CentralParametresOfAllApps: Repo18CentralParametresOfAllApps = aCentralFacade.repositorysMainGetter.repo18CentralParametresOfAllApps,
    related_M14VentPeriode: M14VentPeriode? = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = {},
    nombreClientAvecCibleCommeLastBonAchat: Int = 0,
) {
    val currentActiveFocuced_M14VentPeriode = focusedValuesGetter.currentActiveFocuced_M14VentPeriode

    var isLoading by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }

    val isVerificationChecked by remember {
        mutableStateOf(
            related_M14VentPeriode?.son_verification_entre_vent_et_achat_est_fait ?: true
        )
    }

    suspend fun updateVerificationStatus(newStatus: Boolean) {
        isLoading = true
        try {
            related_M14VentPeriode?.let { ventPeriode ->
                val updatedData = ventPeriode.copy(son_verification_entre_vent_et_achat_est_fait = newStatus)
                aCentralFacade.repositorysMainSetter.update_M14VentPeriode(updatedData)
            }
        } finally {
            isLoading = false
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = {
                if (!isLoading) {
                    onDismiss()
                }
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = true,
                dismissOnBackPress = !isLoading,
                dismissOnClickOutside = !isLoading
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Box {
                    // Main content - no scroll here, let LazyColumn handle it
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(bottom = 80.dp), // Space for floating buttons
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header with title
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (nombreClientAvecCibleCommeLastBonAchat > 0) "تنبيه - عملاء معلقون" else "جاهز للإغلاق",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Icon(
                                imageVector = if (nombreClientAvecCibleCommeLastBonAchat > 0) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (nombreClientAvecCibleCommeLastBonAchat > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Message text
                        val messageText = if (nombreClientAvecCibleCommeLastBonAchat >= 1) {
                            "يرجى تعيين تقارير $nombreClientAvecCibleCommeLastBonAchat زبون لغلق فترة الطلبيات."
                        } else {
                            "يرجى تعيين تقارير الزبائن لغلق فترة الطلبيات."
                        }

                        Text(
                            text = messageText,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        // Verification status indicator
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isVerificationChecked) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isVerificationChecked) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (isVerificationChecked) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (isVerificationChecked) {
                                        "تم التحقق من الفترة"
                                    } else {
                                        "لم يتم التحقق من الفترة بعد"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isVerificationChecked) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }

                        // History content - wrapped in Box with height constraint
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f) // Take remaining space
                        ) {
                            HistoriqueWorck(
                                aCentralFacade = aCentralFacade,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Floating action buttons
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .zIndex(1f)
                            .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                enabled = !isLoading,
                                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "العودة",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            val canConfirm = nombreClientAvecCibleCommeLastBonAchat == 0 ||
                                    repo18CentralParametresOfAllApps.dataValue?.itsDevMode ?: false

                            if (canConfirm) {
                                Button(
                                    onClick = {
                                        kotlinx.coroutines.GlobalScope.launch {
                                            updateVerificationStatus(true)
                                            delay(500) // Small delay for better UX
                                            onConfirm()
                                            onDismiss()
                                        }
                                    },
                                    enabled = !isLoading,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isLoading) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                            Text(
                                                "جاري المعالجة...",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    } else {
                                        Text(
                                            "موافق",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { showToast = true },
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.outline
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "غير متاح",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Toast notification
    if (showToast) {
        LaunchedEffect(showToast) {
            delay(3000)
            showToast = false
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.inverseSurface
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "يرجى تأكيد التحقق من الفترة قبل الإغلاق",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }
}
