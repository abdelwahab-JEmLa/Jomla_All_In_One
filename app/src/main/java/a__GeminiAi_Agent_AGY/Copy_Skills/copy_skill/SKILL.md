---
name: copy_skill
description: Use this skill to automatically copy, bundle, and format all sibling files and subdirectories of a specified reference package. Trigger this whenever the user asks to copy files to the clipboard, trigger `c_`, `cl_`, `cc_`, `cop_last`, `cop_`, `ca_`, `ca+t`, `dc_`, or explicitly mentions backing up files from clipboard or copying files to a prompt.
---
## Trigger Phrases
- "c_"
- "cl_"
- "cc_"
- "cop_last"
- "cop_"
- "ca_"
- "ca+t"
- "dc_"

# Skill - Copy to Clipboard & Backup (copy_skill)

This skill instructs the assistant on how to automatically copy, bundle, and format all sibling files and subdirectories of a specified reference package. It handles both direct copying to the Windows Clipboard and bundling into structured text backup files (`hist_copie.md` Global et r_ai du projet).

## Active Reference Package

The current default reference package is set below:
- **Reference Directory**: `app/src/main/java/EntreApps/Shared/Modules/Utils/M1/Module/Views`

## Last Copied Files

The following files were targeted during the last execution:
- `D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\EntreApps\Shared\Modules\Utils\M1\Module\Views\FastInit_Outlined_Int_Edite_Modulable_Proto4.kt`

## Emplacements fixes de `hist_copie.md` (Sauvegarde en Parallèle)

Toute modification (création, mise à jour ou suppression) de `hist_copie.md` doit être appliquée **en parallèle** sur les deux destinations :
1. **Global** : `C:\Users\Abou Mohamed\.gemini\config\skills\Copy_Skills\copy_skill\references\hist_copie.md`
2. **Projet / r_ai** : `<projectRoot>\app\src\main\java\a__GeminiAi_Agent_AGY\Copy_Skills\copy_skill\references\hist_copie.md`

> **Note** : Toujours vérifier l'existence des répertoires parents et les créer si nécessaire avant d'écrire.

## Steps to Execute

### 1. Identify Target Directory & Files
- **Case A: Default Trigger**: If the user triggers `cl_`, `cc_`, `cop_last`, `c_`, `ca_`, or `ca+t` without a path, first check the **actual Windows Clipboard** using the native Python script or PowerShell:
  `python "C:\Users\Abou Mohamed\.gemini\config\skills\Copy_Skills\copy_skill\scripts\read_clipboard_files.py"`
  If the user manually copied files in Windows/Android Studio, this script will quickly extract their paths and line counts. If no files are in the clipboard, fallback to reading the **Last Copied Files** section above.
- **Case B: Dynamic Request**: If a path is provided with `cop_` or `c_`, scan that directory recursively for `.kt` files. Overwrite the **Active Reference Package** and **Last Copied Files** sections in this file (`SKILL.md`).

### 2. Action: Copy to Clipboard (`cl_`, `cc_`, `cop_last`, `cop_`, `ca+t`)

- **Trigger `ca+t` (Append Simple_Todo Reference)**: Si le trigger contient `+t` (ex: `ca+t`), en plus du comportement normal `ca_` (append des fichiers ciblés), appender **automatiquement** la référence cliquable de `Simple_Todo.md` à la fin des deux fichiers `hist_copie.md` (Global & r_ai) :
  ```markdown
  ### 📝 [Simple_Todo.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/b__References_Files/Simple_Todo.md)
  ```

- **Placer dans le Presse-papiers Windows** :
  - **Via Python** :
    ```bash
    python "C:\Users\Abou Mohamed\.gemini\config\skills\Copy_Skills\copy_skill\scripts\fast_cc.py"
    ```
  - **Via PowerShell (.NET)** :
    ```powershell
    Add-Type -AssemblyName System.Windows.Forms
    $fileCollection = New-Object System.Collections.Specialized.StringCollection
    $fileCollection.Add("<chemin_absolu_fichier_1>")
    $fileCollection.Add("<chemin_absolu_fichier_2>")
    [System.Windows.Forms.Clipboard]::SetFileDropList($fileCollection)
    ```

### 3. Action: Manage Backup Files en Parallèle (`c_`, `ca_`, `dc_`)
These triggers interact with the `references/hist_copie.md` files (Global & r_ai) without touching the clipboard.
- **Trigger `c_` (Delete & Recreate as Tree)** : Écraser complètement les deux fichiers `hist_copie.md` (Global & r_ai) avec les nouveaux fichiers ciblés organisés sous forme d'**arborescence hiérarchique (Tree)** des dossiers et fichiers avec liens Markdown cliquables.
- **Trigger `ca_` / `ca+t` (Append Backup as Tree)** : Insère ou fusionne les liens Markdown cliquables des fichiers ciblés dans l'arborescence (tree) des deux `hist_copie.md` sous leur dossier parent respectif.
- **Trigger `dc_` (Delete Backup)** : Supprime les deux fichiers `hist_copie.md` (Global & r_ai).

### 4. Report Success (Table)
- Output a highly concise response containing:
  1. **Sauvegardes mises à jour** : Liens cliquables vers les deux `hist_copie.md`.
  2. **Nom court du package**.
  3. **Tableau des fichiers** (Nom du fichier | Lignes).
- Do not output the code in the chat.
