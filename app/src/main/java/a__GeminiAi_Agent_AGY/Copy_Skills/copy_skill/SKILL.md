---
name: copy_skill
description: Use this skill to automatically copy, bundle, and format all sibling files and subdirectories of a specified reference package. Trigger this whenever the user asks to copy files to the clipboard, trigger `c_`, `cl_`, `cc_`, `cop_last`, `cop_`, `ca_`, `dc_`, or explicitly mentions backing up files from clipboard or copying files to a prompt.
---

# Skill - Copy to Clipboard & Backup (copy_skill)

This skill instructs the assistant on how to automatically copy, bundle, and format all sibling files and subdirectories of a specified reference package. It handles both direct copying to the Windows Clipboard and bundling into a text backup file (`hist_copie.md`).

## Active Reference Package

The current default reference package is set below:
- **Reference Directory**: `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\com\example\light_app_controles\B\Screens`

## Last Copied Files

The following files were targeted during the last execution:
- `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\com\example\light_app_controles\B\Screens\MainScreen.kt`

## Steps to Execute

### 1. Identify Target Directory & Files
- **Case A: Default Trigger**: If the user triggers `cl_`, `cc_`, `cop_last`, `c_`, or `ca_` without a path, first check the **actual Windows Clipboard** using the native Python script to avoid PowerShell latency:
  `python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_skill\scripts\read_clipboard_files.py"`
  If the user manually copied files in Windows/Android Studio, this script will quickly extract their paths and line counts. If no files are in the clipboard, fallback to reading the **Last Copied Files** section above.
- **Case B: Dynamic Request**: If a path is provided with `cop_` or `c_`, scan that directory recursively for `.kt` files. Overwrite the **Active Reference Package** and **Last Copied Files** sections in this file (`SKILL.md`).

### 2. Action: Copy to Clipboard (`cl_`, `cc_`, `cop_last`, `cop_`)
- Pour garantir la vitesse maximale (copie quasi-instantanée du contenu), exécutez le fichier batch :
  ```bash
  "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_skill\run_cc.bat"
  ```
  *(Ce script lit automatiquement `hist_copie.md` et injecte le texte directement dans le presse-papiers via `fast_cc.py`)*

### 3. Action: Manage Backup File (`c_`, `ca_`, `dc_`)
These triggers interact with the `app\src\main\java\skill_agent\copy_skill\references\hist_copie.md` file without touching the clipboard.
- **Trigger `c_` (Delete & Recreate)**: L'objectif est d'écraser (overwrite) complètement `hist_copie.md` avec les nouveaux liens ciblés. Exécutez simplement `python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_skill\scripts\read_clipboard_files.py"`. S'il retourne `EMPTY` (ou si vous utilisez une liste de fichiers spécifiques), vous **devez écraser manuellement** `hist_copie.md` (Overwrite: true) en y insérant les nouveaux liens sous le format `### 🔗 [Fichier](file:///...)`.
- **Trigger `ca_` (Append Backup)**: Append only the clickable Markdown links of the targeted files to the end of `app\src\main\java\skill_agent\copy_skill\references\hist_copie.md` (if they are not already present).
- **Trigger `dc_` (Delete Backup)**: Delete the `app\src\main\java\skill_agent\copy_skill\references\hist_copie.md` file.

### 4. Report Success (Table)
- Output a highly concise response containing:
  1. **Lien de sauvegarde** : `[app/src/main/java/skill_agent/copy_skill/references/hist_copie.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_skill/references/hist_copie.md)`.
  2. **Copie Rapide** : Fournis un lien cliquable vers le script VBS pour que l'utilisateur puisse copier sans taper `cc_` : `[🚀 Exécuter la Copie (run_cc_silent.vbs)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_skill/run_cc_silent.vbs)`
  3. **Temps d'exécution** : Calcule et affiche le temps écoulé (en secondes) depuis la requête de l'utilisateur.
  4. **Nom court du package**.
  5. **Tableau des fichiers** (Nom du fichier | Lignes).
- Do not output the code in the chat.
