---
name: copy_skill
description: Use this skill to automatically copy, bundle, and format all sibling files and subdirectories of a specified reference package. Trigger this whenever the user asks to copy files to the clipboard, trigger `c_` (copy files from hist_copie.md to clipboard), `cl_`, `cc_`, `cop_last`, `cop_`, `ca_`, `ca+t`, `dc_`, or explicitly mentions backing up files from clipboard or copying files to a prompt.
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

This skill instructs the assistant on how to automatically copy, bundle, and format files. It handles both direct copying to the Windows Clipboard (notamment depuis `hist_copie.md` via `c_`) and bundling into structured text backup files (`hist_copie.md` Global et r_ai du projet).

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

### 1. Identify Target Action & Files
- **Case 1: Trigger `c_` (Copy from `hist_copie.md` to Clipboard)**:
  - Lire le contenu de `references/hist_copie.md` (Global ou Projet).
  - Extraire tous les chemins de fichiers référencés (formats Markdown `[Nom](file:///...)` ou chemins relatifs).
  - Vérifier l'existence physique de chaque fichier.
  - Placer la liste complète des fichiers dans le presse-papiers Windows (Clipboard FileDropList).
  - Afficher un rapport concis des fichiers copiés prêts pour `Ctrl+V`.

- **Case 2: Trigger `cl_` / `cc_` / `cop_last` (Copy active clipboard / last copied)**:
  - Vérifier le presse-papiers Windows réel (`read_clipboard_files.py`) ou utiliser **Last Copied Files**.

- **Case 3: Dynamic Request (`cop_ <path>`)**:
  - Scanner le dossier cible pour `.kt` et mettre à jour **Active Reference Package** et **Last Copied Files**.

### 2. Action: Copy to Clipboard (`c_`, `cl_`, `cc_`, `cop_last`, `cop_`)

- **Placer dans le Presse-papiers Windows** :
  - **Via PowerShell (.NET)** :
    ```powershell
    Add-Type -AssemblyName System.Windows.Forms
    $fileCollection = New-Object System.Collections.Specialized.StringCollection
    $fileCollection.Add("<chemin_absolu_fichier_1>")
    $fileCollection.Add("<chemin_absolu_fichier_2>")
    [System.Windows.Forms.Clipboard]::SetFileDropList($fileCollection)
    ```
  - **Via Python** :
    ```bash
    python "C:\Users\Abou Mohamed\.gemini\config\skills\Copy_Skills\copy_skill\scripts\fast_cc.py"
    ```

### 3. Action: Manage Backup Files en Parallèle (`ca_,` `ca+t`, `dc_`)
Ces triggers interagissent avec les fichiers `references/hist_copie.md` (Global & r_ai).
- **Trigger `ca_` (Append / Update Backup as Tree)** : Insère ou fusionne les liens Markdown cliquables des fichiers ciblés dans l'arborescence (tree) des deux `hist_copie.md` sous leur dossier parent respectif.
- **Trigger `ca+t` (Append Simple_Todo Reference)** : En plus du comportement normal `ca_`, appender automatiquement la référence cliquable de `Simple_Todo.md` à la fin des deux fichiers `hist_copie.md` (Global & r_ai) :
  ```markdown
  ### 📝 [Simple_Todo.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/b__References_Files/Simple_Todo.md)
  ```
- **Trigger `dc_` (Delete / Clear Backup)** : Supprime ou réinitialise les deux fichiers `hist_copie.md` (Global & r_ai).

### 4. Report Success (Table)
- Afficher une réponse ultra-concise :
  1. Action effectuée (ex: `c_ -> Fichiers de hist_copie.md copiés au presse-papiers`).
  2. Liens cliquables vers les deux `hist_copie.md`.
  3. Liste / tableau des fichiers concernés (Nom du fichier | Lignes).
