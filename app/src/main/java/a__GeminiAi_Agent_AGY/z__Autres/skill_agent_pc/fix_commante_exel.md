# Skill - Fix Excel Comment & Filter (fix_commante_exel)

This skill enables the assistant to automatically open a specified Excel file on the host PC Desktop, scan all worksheets and cell comments, process specific calculations or constraints described in those comments, clear the processed comments, and save the updated workbooks.

---

## Trigger Phrases
- `fix_excels` (scans all Excel files on the Desktop)
- `fix_excels_`
- `fix_commante_exel_<filename.xlsx>`
- `fix_commante_exel_<filename.xlx>`
- `fix_comment_excel_<filename.xlsx>`
- `fix_excel_<filename.xlsx>`
- `fix_excel`
- `fix_commante_exel_`

---

## 🔍 Comment Detection Rules & Types (Façon de trouver les commentaires)
To avoid "No comments found" errors, the assistant and the python script must loop through all cells in all worksheets of the target workbook(s) using openpyxl `.comment` attribute. All comment text must be normalized (converted to lowercase, stripped of whitespace) before performing checks.

The system processes the following three categories of comments:

### 1. Standard Constraint Comments (e.g., `dep > 0`)
- **Detection**: Comment text contains `"dep"` (case-insensitive) and either `">0"`, `"> 0"`, or `"supérieur"`.
- **Action**: Identify the column matching `"count_Don_Depot"` or similar. Filter the worksheet, keeping only rows where that column's numeric value is strictly greater than 0.

### 2. External Sum/Lookup Comments (e.g., `"aller au M13"`)
- **Detection**: Comment text contains `"aller au m13"` or `"m13"`.
- **Action**: Typically found on cell `S1` (Column `Tariff_ItsWorkInGrossist_SuperGros`). Update all rows in that column with a SUMIFS formula referencing the external `M13Tariffication.xlsx` file:
  `=SUMIFS('[M13Tariffication.xlsx]M13Tariffication'!Q:Q, '[M13Tariffication.xlsx]M13Tariffication'!L:L, R{row}, '[M13Tariffication.xlsx]M13Tariffication'!T:T, "Tariff_ItsWorkInGrossist_SuperGros")`

### 3. Summary/SUMPRODUCT Comments (e.g., `"la sum de condepo m3 * Tariff_ItsWorkInGrossist_SuperGros"`)
- **Detection**: Comment text contains `"sum"`, `"condepo"`, or `"tariff_itsworkingrossist_supergros"`.
- **Action**: Typically found in `balance.xlsx` cell `A1`. Write a SUMPRODUCT formula to calculate the balance:
  `=SUMPRODUCT('[fb_m13_m3_ref.xlsx]M03Couleur'!$E$2:$E$114, '[fb_m13_m3_ref.xlsx]M03Couleur'!$S$2:$S$114)`

---

## Steps to Execute

### 1. Locate the Excel File
Verify that the target Excel file exists on the Desktop (checking local `C:\Users\Abou Mohamed\Desktop` and falling back if needed).
- If the trigger phrase specifies a filename like `fix_commante_exel_fb_m13_m3_ref.xlsx`, use that file.
- If only a prefix or `fix_excels` is given, scan all `.xlsx` files on the Desktop (skipping temporary ones starting with `~$`).

### 2. Run the Fix Script
Execute the python script [fix_excel_comments.py](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/copy_/fb_db/fix_excel_comments.py) to parse the workbooks, match the comments, execute the formulas/filters, clear the comments, and save:
```powershell
python copy_/fb_db/fix_excel_comments.py <filename.xlsx>
```

### 3. Display Results
Report the worksheets scanned, specific comments detected, formulas/filters applied, and rows kept/deleted.

---

## 🔗 Direct Links
* ⚙️ [PC Help Page (hw_)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/hw_.md)
* 🐍 [fix_excel_comments.py](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/copy_/fb_db/fix_excel_comments.py)
* 📄 [Fix Excel Comment Skill Config](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/fix_commante_exel.md)
