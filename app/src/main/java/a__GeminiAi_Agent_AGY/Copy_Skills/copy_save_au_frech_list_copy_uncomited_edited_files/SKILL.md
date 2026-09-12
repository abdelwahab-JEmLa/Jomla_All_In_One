---
name: copy_save_au_frech_list_copy_uncomited_edited_files
description: Copies all uncommitted (modified/staged/untracked) files from the currently open Android Studio project, ensures hist_copie.md (global and project a__GeminiAi_Agent_AGY) and list_copied_files exist/are updated, and copies files to clipboard. Trigger with: copy_uncomited, save_uncomited_, cu_, sc_u, copy_edited.
---

# Skill - Copy & Save Uncommitted Edited Files (`copy_uncomited` / `cu_`)

Ce skill détecte automatiquement tous les fichiers **modifiés, stagés ou non trackés** (non commités) du projet Android Studio actif, s'assure de l'existence et met à jour :
1. `list_copied_files` (chemins bruts)
2. `hist_copie.md` global ([references/hist_copie.md](file:///C:/Users/Abou%20Mohamed/.gemini/config/skills/Copy_Skills/copy_skill/references/hist_copie.md))
3. `hist_copie.md` du projet / r_ai ([a__GeminiAi_Agent_AGY/Copy_Skills/copy_skill/references/hist_copie.md](file:///D:/AndroidStudioProjects/ClientJetPack/app/src/main/java/a__GeminiAi_Agent_AGY/Copy_Skills/copy_skill/references/hist_copie.md))

Puis il copie les fichiers dans le presse-papiers Windows (compatible Windows PowerShell 5.1 & Core).

---

## ⚡ Triggers
- `copy_uncomited`
- `cu_`
- `sc_u`
- `save_uncomited_`
- `copy_edited`
- `v_u`
- `vu_`
- `v_cu_`
- `TODO: copy_uncomited`

---

## 📂 Destinations

- **Liste de fichiers copiés (cop_last) :**
  `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\Copy_Skills\cop_last\list_copied_files`
- **Historique Global Markdown :**
  `C:\Users\Abou Mohamed\.gemini\config\skills\Copy_Skills\copy_skill\references\hist_copie.md`
- **Historique Projet / r_ai Markdown :**
  `<projectRoot>\app\src\main\java\a__GeminiAi_Agent_AGY\Copy_Skills\copy_skill\references\hist_copie.md`

---

## 🚀 Étapes d'exécution

### Étape 1 - Détecter la racine Git du projet actif

```powershell
$workspaceRoot = "D:\AndroidStudioProjects\ClientJetPack"  # Workspace courant
$projectRoot = (git -C $workspaceRoot rev-parse --show-toplevel 2>&1).Trim().Replace('/', '\')
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERREUR: Aucun dépôt Git trouvé dans le workspace courant."
    exit 1
}
Write-Host "Projet Git détecté: $projectRoot"
```

---

### Étape 2 - Lister tous les fichiers non commités (filtrés)

**Extensions exclues (binaires)** : `.apk`, `.zip`, `.png`, `.jpg`, `.jpeg`, `.gif`, `.so`, `.aar`, `.jar`, `.webp`, `.db`, `.keystore`

**Fichiers exclus** : `hist_copie.md`, `list_copied_files`

**Répertoires exclus** : `build/`, `.gradle/`, `.git/`, `node_modules/`

```powershell
$excludeExtensions = @('.apk','.zip','.png','.jpg','.jpeg','.gif','.so','.aar','.jar','.webp','.db','.keystore')
$excludeDirs = @('build', '.gradle', '.git', 'node_modules')
$excludeFiles = @('hist_copie.md', 'list_copied_files')

$gitLines = git -C $projectRoot status --short --porcelain 2>&1
$uncommittedFiles = @()

foreach ($line in $gitLines) {
    if ([string]::IsNullOrWhiteSpace($line)) { continue }
    $filePart = $line.Substring(3).Trim().Trim('"')

    # Gérer les renommages (format: "old -> new")
    if ($filePart -match " -> ") {
        $filePart = ($filePart -split " -> ") | Select-Object -Last 1
    }

    $absPath = Join-Path $projectRoot $filePart

    # Filtrer fichiers exclus (ex: hist_copie.md)
    $leaf = Split-Path $absPath -Leaf
    if ($leaf -in $excludeFiles) { continue }

    # Filtrer répertoires exclus
    $skip = $false
    foreach ($dir in $excludeDirs) {
        if ($absPath -like "*\$dir\*") { $skip = $true; break }
    }
    if ($skip) { continue }

    # Filtrer extensions binaires
    $ext = [System.IO.Path]::GetExtension($absPath).ToLower()
    if ($ext -in $excludeExtensions) { continue }

    # Vérifier existence physique (ignorer les suppressions)
    if (Test-Path $absPath -PathType Leaf) {
        $uncommittedFiles += $absPath
    }
}

Write-Host "Total fichiers qualifiés: $($uncommittedFiles.Count)"
```

---

### Étape 3 - Assurer l'existence et mettre à jour `hist_copie.md` (Global & r_ai) & `list_copied_files` avant la copie

```powershell
$histCopieGlobal  = "C:\Users\Abou Mohamed\.gemini\config\skills\Copy_Skills\copy_skill\references\hist_copie.md"
$histCopieProject = Join-Path $projectRoot "app\src\main\java\a__GeminiAi_Agent_AGY\Copy_Skills\copy_skill\references\hist_copie.md"
$listFile         = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\Copy_Skills\cop_last\list_copied_files"

$targetsHist = @($histCopieGlobal, $histCopieProject)

foreach ($hPath in $targetsHist) {
    $parentHist = Split-Path $hPath -Parent
    if (-not (Test-Path $parentHist)) {
        New-Item -ItemType Directory -Path $parentHist -Force | Out-Null
    }
    if (-not (Test-Path $hPath)) {
        New-Item -ItemType File -Path $hPath -Force | Out-Null
    }
}

$parentList = Split-Path $listFile -Parent
if (-not (Test-Path $parentList)) {
    New-Item -ItemType Directory -Path $parentList -Force | Out-Null
}

if ($uncommittedFiles.Count -gt 0) {
    # 1. Construction du Markdown de l'arborescence (Tree)
    $grouped = $uncommittedFiles | Group-Object { Split-Path $_ -Parent }
    $histMd = "# 📁 Arborescence des Fichiers Copiés`n`n"
    foreach ($group in $grouped) {
        $parentNorm = $group.Name.Replace('/', '\')
        $folderRel = $parentNorm.Replace($projectRoot, "").TrimStart('\').Replace('\', '/')
        if ([string]::IsNullOrWhiteSpace($folderRel)) { $folderRel = "." }
        $histMd += "- 📂 ``$folderRel/```n"
        foreach ($f in $group.Group) {
            $fName = Split-Path $f -Leaf
            $fUri = "file:///" + $f.Replace('\', '/')
            $histMd += "  - 📄 [$fName]($fUri)`n"
        }
    }

    # Écriture dans les deux hist_copie.md (Global + r_ai)
    foreach ($hPath in $targetsHist) {
        [System.IO.File]::WriteAllText($hPath, $histMd, [System.Text.Encoding]::UTF8)
    }

    # 2. Mise à jour de list_copied_files
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $content  = "# Fichiers non commites copies le $timestamp"
    $content += "`n# Projet: $projectRoot"
    $content += "`n# Total: $($uncommittedFiles.Count) fichier(s)"
    $content += "`n"
    $content += ($uncommittedFiles -join "`n")
    Set-Content -Path $listFile -Value $content -Encoding UTF8
}
```

---

### 💡 Mode `v_u` / `vu_` (Vide, Structuré & Simple_Todo sans Copie)

Lorsque le déclencheur est `v_u`, `vu_` ou `v_cu_` :
1. **Effacer le contenu existant** : Écraser intégralement le contenu des deux fichiers `hist_copie.md` (Global & r_ai).
2. **Écrire l'arborescence des fichiers non commités** (fichiers qualifiés détectés à l'Étape 2).
3. **Appender la référence `Simple_Todo.md`** à la fin des deux fichiers `hist_copie.md` :
   ```markdown
   ### 📝 [Simple_Todo.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/b__References_Files/Simple_Todo.md)
   ```
4. **Ignorer l'Étape 4 (Copie presse-papiers)** : Ne PAS placer la liste de fichiers dans le presse-papiers Windows.
5. **Rapport** : Indiquer que `hist_copie.md` a été réinitialisé et mis à jour avec `Simple_Todo.md` sans copie au presse-papiers.

---

### Étape 4 - Copier dans le presse-papiers Windows (Ignoré en mode `v_u`)

```powershell
if ($uncommittedFiles.Count -gt 0) {
    Add-Type -AssemblyName System.Windows.Forms
    $fileCollection = New-Object System.Collections.Specialized.StringCollection
    $uncommittedFiles | ForEach-Object { $fileCollection.Add($_) }
    [System.Windows.Forms.Clipboard]::SetFileDropList($fileCollection)
    Write-Host "✅ $($uncommittedFiles.Count) fichier(s) copié(s) dans le presse-papiers. Ctrl+V prêt."
} else {
    Write-Host "⚠️ Aucun fichier non commité qualifié."
}
```

---

### Étape 5 - Rapport ultra-concis

Toujours afficher après exécution :

```
✅ copy_uncomited - Projet: ClientJetPack
📋 X fichiers non commités copiés (Ctrl+V prêt)
💾 Sauvegardé dans hist_copie.md (Global & r_ai) & cop_last/list_copied_files

Fichiers:
  app/src/.../MyScreen.kt
  ...
```
