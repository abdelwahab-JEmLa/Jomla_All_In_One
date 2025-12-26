package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Sections

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.EditableField
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.FastEdite_OutlinedTextField
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun BasicInfoSection(
    etudiant: M19Etudiant,
    repo19Etudiant: Repo19Etudiant,
    isEditingNom: Boolean,
    nomInput: String,
    onNomInputChange: (String) -> Unit,
    onNomEditClick: () -> Unit,
    onNomSave: () -> Unit,
    nomFocusRequester: FocusRequester,
    isEditingPrenom: Boolean,
    prenomInput: String,
    onPrenomInputChange: (String) -> Unit,
    onPrenomEditClick: () -> Unit,
    onPrenomSave: () -> Unit,
    prenomFocusRequester: FocusRequester,
    isEditingAge: Boolean,
    ageInput: String,
    onAgeInputChange: (String) -> Unit,
    onAgeEditClick: () -> Unit,
    onAgeSave: () -> Unit,
    ageFocusRequester: FocusRequester,
    isEditingPosition: Boolean,
    positionInput: String,
    onPositionInputChange: (String) -> Unit,
    onPositionEditClick: () -> Unit,
    onPositionSave: () -> Unit,
    positionFocusRequester: FocusRequester,
    isEditingPhone: Boolean,
    phoneInput: String,
    onPhoneInputChange: (String) -> Unit,
    onPhoneEditClick: () -> Unit,
    onPhoneSave: () -> Unit,
    phoneFocusRequester: FocusRequester
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Nom (editable)
        EditableField(
            label = "الاسم:",
            value = etudiant.nom,
            isEditing = isEditingNom,
            inputValue = nomInput,
            onInputChange = onNomInputChange,
            onEditClick = onNomEditClick,
            onSave = onNomSave,
            focusRequester = nomFocusRequester
        )

        // Prenom (editable)
        EditableField(
            label = "اللقب:",
            value = etudiant.prenom,
            isEditing = isEditingPrenom,
            inputValue = prenomInput,
            onInputChange = onPrenomInputChange,
            onEditClick = onPrenomEditClick,
            onSave = onPrenomSave,
            focusRequester = prenomFocusRequester
        )

        // Age (editable)
        FastEdite_OutlinedTextField(
            label = "العمر:",
            value = etudiant.age.toString(),
            isEditing = isEditingAge,
            inputValue = ageInput,
            onInputChange = onAgeInputChange,
            onEditClick = onAgeEditClick,
            onSave = onAgeSave,
            focusRequester = ageFocusRequester
        )

        // Position in class (editable)
        FastEdite_OutlinedTextField(
            label = "المركز في الصف:",
            value = etudiant.positon_don_classe.toString(),
            isEditing = isEditingPosition,
            inputValue = positionInput,
            onInputChange = onPositionInputChange,
            onEditClick = onPositionEditClick,
            onSave = onPositionSave,
            focusRequester = positionFocusRequester
        )

        // Phone (editable)
        EditableField(
            label = "هاتف:",
            value = etudiant.num_telephone_parent,
            isEditing = isEditingPhone,
            inputValue = phoneInput,
            onInputChange = onPhoneInputChange,
            onEditClick = onPhoneEditClick,
            onSave = onPhoneSave,
            focusRequester = phoneFocusRequester,
            keyboardType = KeyboardType.Phone,
            textStyle = MaterialTheme.typography.bodySmall,
            width = 120.dp
        )
    }
}

