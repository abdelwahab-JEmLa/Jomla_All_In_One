package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Modules.splitCsvLine
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.Action.But1_Export_M8_Room_To_Csv
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.Action.But2_Export_M8_Csv_To_FireBase
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.Action.But3_Import_M8Csv_To_Room
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.Action.But6_Import_M8_FireBase_To_Csv
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.Action.But8_DeleteAll_M8_Room
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.Action.But9_Import_M8_FireBase_To_Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Button_State(
    val showLabels: Boolean = true,
    val its_Active: Boolean = false,
    val text_Label: String = "",
    val colors: Pair<Color, Color> = Pair(Color.White, Color.White),
    val icons: Pair<ImageVector, ImageVector> = Pair(Icons.Default.Remove, Icons.Default.Add),
    val description_Functionement: String = "",
) {
    companion object {
        fun get_Default() = Button_State()
    }
}

enum class PendingAction(
    val color: Color = Color(0xFFF8F8F8),
    val imageVector: ImageVector = Icons.Default.Numbers
) {
    But1_Export_M8_Room_To_Csv,
    But2_Export_M8_Csv_To_FireBase,
    But3_Import_M8Csv_To_Room,
    But5_Import_M8_Ui_To_Room,
    But6_Import_M8_FireBase_To_Csv,
    But_9_Import_M8_FireBase_To_Room(Color(0xFFE91E63), Icons.Default.LocalFireDepartment),
    But7_DeleteImport_M8Csv_To_Room,
    But8_DeleteAll_M8_Room, ;
}

@Composable
fun M8Bon_Operations_FragMap_DropdownMenu(
    modifier: Modifier = Modifier,
    vm: FeatureID1_ViewModel,
    expanded: Boolean,
    onDismiss: () -> Unit,
    on_vent_key: String = "",
    onClick_Lence_Capture: (() -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }

    // CSV stats for But7 label: total rows, new (not in Room), updates (already in Room)
    var csvRowCount by remember { mutableStateOf<Int?>(null) }
    var csvNewCount by remember { mutableStateOf<Int?>(null) }
    var csvUpdateCount by remember { mutableStateOf<Int?>(null) }
    var csvCreditCount by remember { mutableStateOf<Int?>(null) }
    // Bumped after any operation that writes to the CSV file, so stats always reflect the real file.
    var csvRefreshTrigger by remember { mutableStateOf(0) }

    var firebaseRowCount by remember { mutableStateOf<Int?>(null) }
    var firebaseCreditCount by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        runCatching {
            val (total, credit) = vm.setter_LongOperations.get_Firebase_M8_Counts(M8BonVent.ref_Test)
            firebaseRowCount = total
            firebaseCreditCount = credit
        }.onFailure {
            firebaseRowCount = -1
            firebaseCreditCount = -1
        }
    }

    LaunchedEffect(vm.active_Datas.list_M8bon, csvRefreshTrigger) {
        withContext(Dispatchers.IO) {
            val csv = M8BonVent.csv_test
            if (csv.exists() && csv.length() > 0L) {
                val lines = csv.readLines().filter { it.isNotBlank() }
                if (lines.size >= 2) {
                    val headers = lines[0].splitCsvLine()
                    val keyIdx = headers.indexOf("keyID")
                    val etatIdx = headers.indexOf("etateActuellementEst")
                    val creditNames = M8BonVent.EtateActuellementEst.entries
                        .filter { it.credit_type }
                        .map { it.name }
                        .toSet()

                    val dataLines = lines.drop(1)
                    val csvKeys = dataLines.mapNotNull { line ->
                        line.splitCsvLine().getOrNull(keyIdx)
                            ?.trim()?.removeSurrounding("\"")
                            ?.takeIf { it.isNotBlank() }
                    }.toSet()
                    val roomKeys = vm.active_Datas.list_M8bon
                        ?.map { it.keyID }?.toSet() ?: emptySet()

                    csvRowCount = csvKeys.size
                    csvNewCount = (csvKeys - roomKeys).size
                    csvUpdateCount = (csvKeys intersect roomKeys).size
                    csvCreditCount = dataLines.count { line ->
                        val cells = line.splitCsvLine()
                        val etat = cells.getOrNull(etatIdx)
                            ?.trim()?.removeSurrounding("\"")
                        etat != null && etat in creditNames
                    }
                }
            } else {
                csvRowCount = 0
                csvNewCount = 0
                csvUpdateCount = 0
                csvCreditCount = 0
            }
        }
    }

    var isEditingCredits by remember { mutableStateOf(false) }
    var out_val by remember { mutableStateOf("") }
    val creditsFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingCredits) {
        if (isEditingCredits) creditsFocusRequester.requestFocus()
    }

    pendingAction?.let { action ->
        when (action) {
            PendingAction.But8_DeleteAll_M8_Room -> But8_DeleteAll_M8_Room(
                vm = vm,
                coroutineScope = coroutineScope,
                onDismiss = onDismiss,
                onPendingClear = { pendingAction = null },
                action_definition = PendingAction.But8_DeleteAll_M8_Room,
            )

            PendingAction.But3_Import_M8Csv_To_Room -> But3_Import_M8Csv_To_Room(
                vm = vm,
                coroutineScope = coroutineScope,
                onDismiss = onDismiss,
                onPendingClear = { pendingAction = null },
                action_definition = PendingAction.But3_Import_M8Csv_To_Room,
            )

            PendingAction.But2_Export_M8_Csv_To_FireBase -> But2_Export_M8_Csv_To_FireBase(
                vm = vm,
                coroutineScope = coroutineScope,
                onDismiss = onDismiss,
                onPendingClear = { pendingAction = null },
                action_definition = PendingAction.But2_Export_M8_Csv_To_FireBase,
            )

            PendingAction.But1_Export_M8_Room_To_Csv -> But1_Export_M8_Room_To_Csv(
                vm = vm,
                coroutineScope = coroutineScope,
                onDismiss = onDismiss,
                onPendingClear = { pendingAction = null },
                onCsvWritten = { csvRefreshTrigger++ },
                action_definition = PendingAction.But1_Export_M8_Room_To_Csv,
            )

            PendingAction.But6_Import_M8_FireBase_To_Csv -> But6_Import_M8_FireBase_To_Csv(
                vm = vm,
                coroutineScope = coroutineScope,
                onDismiss = onDismiss,
                onPendingClear = { pendingAction = null },
                onCsvWritten = { csvRefreshTrigger++ },
                action_definition = PendingAction.But6_Import_M8_FireBase_To_Csv,
            )

            PendingAction.But5_Import_M8_Ui_To_Room -> {
                AvertissementDialog(
                    title = action.name,
                    message = "سيتم حفظ بيانات M8BonVent من الواجهة إلى قاعدة البيانات المحلية.\n" +
                            "الصفوف الموجودة ستُحدَّث والجديدة ستُضاف.\n" +
                            "هل تريد المتابعة؟",
                    onConfirm = {
                        pendingAction = null
                        coroutineScope.launch {
                            vm.active_Datas.list_M8bon?.let { bons ->
                                vm.setter_LongOperations.insertAll(bons)
                            }
                            onDismiss()
                        }
                    },
                    onDismiss = { pendingAction = null },
                )
            }

            PendingAction.But7_DeleteImport_M8Csv_To_Room -> {
                AvertissementDialog(
                    title = action.name,
                    message =
                        "هل تريد المتابعة؟",
                    onConfirm = {
                        pendingAction = null
                        coroutineScope.launch {
                            vm.active_Datas.list_M8bon?.let { bons ->
                                vm.setter_LongOperations.delete_All_M8()
                                vm.setter_LongOperations.insertAll(bons)
                            }
                            vm.reload()
                            onDismiss()
                        }
                    },
                    onDismiss = { pendingAction = null },
                )
            }

            PendingAction.But_9_Import_M8_FireBase_To_Room -> But9_Import_M8_FireBase_To_Room(
                vm = vm,
                coroutineScope = coroutineScope,
                onDismiss = onDismiss,
                onPendingClear = { pendingAction = null },
                action_definition = PendingAction.But_9_Import_M8_FireBase_To_Room,
            )

        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.background(Color.White, RoundedCornerShape(8.dp))
    ) {
        if (onClick_Lence_Capture != null) {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color(0xFF1E88E5)
                    )
                },
                text = {
                    Text(
                        text = "التقاط صورة الشاشة",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                onClick = {
                    onDismiss()
                    onClick_Lence_Capture.invoke()
                }
            )
            HorizontalDivider()
        }

        HorizontalDivider(thickness = 3.dp, color = Color.Red)
        HorizontalDivider()
        Text("FireBase")
        val action = PendingAction.But_9_Import_M8_FireBase_To_Room
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = action.imageVector,
                    contentDescription = null,
                    tint = action.color
                )
            },
            text = {
                Text(
                    text = action.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = action
            }
        )
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = Color(0xFFE65100)
                )
            },
            text = {
                Text(
                    text = PendingAction.But2_Export_M8_Csv_To_FireBase.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = PendingAction.But2_Export_M8_Csv_To_FireBase
            }
        )
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = Color(0xFFFF6F00)
                )
            },
            text = {
                val fbStatsLine = when {
                    firebaseRowCount == null -> "..."
                    firebaseRowCount == -1 -> "Firebase: خطأ في الاتصال"
                    firebaseRowCount == 0 -> "Firebase: فارغ"
                    else -> "Firebase: $firebaseRowCount (دين: ${firebaseCreditCount ?: "..."}) | CSV: ${csvRowCount ?: "..."} (دين: ${csvCreditCount ?: "..."})"
                }
                Column {
                    Text(
                        text = PendingAction.But6_Import_M8_FireBase_To_Csv.name,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = fbStatsLine,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            onClick = {
                pendingAction = PendingAction.But6_Import_M8_FireBase_To_Csv
            }
        )
        HorizontalDivider(thickness = 3.dp, color = Color.Red)
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = Color(0xFF1565C0)
                )
            },
            text = {
                Text(
                    text = "But1_Export_M8_Room_To_Csv",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = PendingAction.But1_Export_M8_Room_To_Csv
            }
        )

        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A)
                )
            },
            text = {
                val statsLine = when {
                    csvRowCount == null -> "..."
                    csvRowCount == 0 -> "CSV فارغ"
                    else -> "CSV: $csvRowCount | +${csvNewCount} جديد | ↺${csvUpdateCount} تحديث"
                }
                Column {
                    Text(
                        text = PendingAction.But7_DeleteImport_M8Csv_To_Room.name,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = statsLine,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            onClick = {
                pendingAction = PendingAction.But7_DeleteImport_M8Csv_To_Room
            }
        )
        HorizontalDivider()
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Details,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A)
                )
            },
            text = {
                Text(
                    text = PendingAction.But8_DeleteAll_M8_Room.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = PendingAction.But8_DeleteAll_M8_Room
            }
        )
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A)
                )
            },
            text = {
                Text(
                    text = PendingAction.But3_Import_M8Csv_To_Room.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = PendingAction.But3_Import_M8Csv_To_Room
            }
        )
        HorizontalDivider()

        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A)
                )
            },
            text = {
                Text(
                    text = PendingAction.But5_Import_M8_Ui_To_Room.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = PendingAction.But5_Import_M8_Ui_To_Room
            }
        )

        HorizontalDivider()
    }
}

