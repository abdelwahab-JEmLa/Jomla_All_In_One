---
name: copy_save_au_frech_list_copy_uncomited_edited_files
description: Copies all uncommitted (modified/staged/untracked) files from the currently open Android Studio project and saves their absolute paths to C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\Copy_Skills\cop_last\list_copied_files. Trigger with: copy_uncomited, save_uncomited_, cu_, sc_u, copy_edited.
---

# Skill — Copy & Save Uncommitted Edited Files (`copy_uncomited` / `cu_`)

Ce skill détecte automatiquement tous les fichiers **modifiés, stagés ou non trackés** (non commités) du projet Android Studio actif, les copie dans le presse-papiers Windows, et **écrase la liste de référence** `cop_last/list_copied_files` avec leurs chemins absolus.

---

## 🎯 Triggers
- `copy_uncomited`
- `cu_`
- `sc_u`
- `save_uncomited_`
- `copy_edited`
- `TODO: copy_uncomited`

---

## 📂 Destination fixe de la liste

```
C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\Copy_Skills\copy_skill\references\hist_copie.md
```

---
                                                               
## 🚀 Étapes d'exécution

### Étape 1 — Détecter la racine Git du projet actif

```powershell
$workspaceRoot = "D:\AndroidStudioProjects\ClientJetPack"  # Workspace courant
$projectRoot = (git -C $workspaceRoot rev-parse --show-toplevel 2>&1).Trim()
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERREUR: Aucun dépôt Git trouvé dans le workspace courant."
    exit 1
}
Write-Host "Projet Git détecté: $projectRoot"
```

> **Note** : Le `$workspaceRoot` est toujours le workspace actif de l'utilisateur. Le déduire depuis le contexte de la conversation ou du projet ouvert.

---

### Étape 2 — Lister tous les fichiers non commités (filtrés)

**Extensions exclues (binaires)** : `.apk`, `.zip`, `.png`, `.jpg`, `.jpeg`, `.gif`, `.so`, `.aar`, `.jar`, `.webp`, `.db`, `.keystore`

**Répertoires exclus** : `build/`, `.gradle/`, `.git/`, `node_modules/`

```powershell
$excludeExtensions = @('.apk','.zip','.png','.jpg','.jpeg','.gif','.so','.aar','.jar','.webp','.db','.keystore')
$excludeDirs = @('build', '.gradle', '.git', 'node_modules')

$gitLines = git -C $projectRoot status --short --porcelain 2>&1
$uncommittedFiles = @()

foreach ($line in $gitLines) {
    if ([string]::IsNullOrWhiteSpace($line)) { continue }
    $statusCode = $line.Substring(0, 2).Trim()
    $filePart   = $line.Substring(3).Trim().Trim('"')

    # Gérer les renommages (format: "old -> new")
    if ($filePart -match " -> ") {
        $filePart = ($filePart -split " -> ") | Select-Object -Last 1
    }

    $absPath = Join-Path $projectRoot $filePart

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
$uncommittedFiles | ForEach-Object { Write-Host "  $_" }
```

---

### Étape 3 — Copier dans le presse-papiers Windows

```powershell
if ($uncommittedFiles.Count -gt 0) {
    Set-Clipboard -Path $uncommittedFiles
    Write-Host "✅ $($uncommittedFiles.Count) fichier(s) copié(s) dans le presse-papiers. Ctrl+V pour coller."
} else {
    Write-Host "⚠️ Aucun fichier non commité qualifié. list_copied_files non modifié."
    exit 0
}
```

---

### Étape 4 — Sauvegarder dans `list_copied_files` (écrasement complet)

```powershell
$listFile = "C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\Copy_Skills\cop_last\list_copied_files"
$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"

# Créer le répertoire parent si nécessaire
$parentDir = Split-Path $listFile -Parent
if (-not (Test-Path $parentDir)) {
    New-Item -ItemType Directory -Path $parentDir -Force | Out-Null
}

# Construire et écraser le contenu
$content  = "# Fichiers non commités copiés le $timestamp"
$content += "`n# Projet: $projectRoot"
$content += "`n# Total: $($uncommittedFiles.Count) fichier(s)"
$content += "`n"
$content += ($uncommittedFiles -join "`n")

Set-Content -Path $listFile -Value $content -Encoding UTF8
Write-Host "✅ Liste sauvegardée: $listFile"
```

---

### Étape 5 — Rapport ultra-concis

Toujours afficher après exécution (jamais le contenu des fichiers) :

```
✅ copy_uncomited — Projet: ClientJetPack
📋 8 fichiers non commités copiés (Ctrl+V prêt)
💾 Sauvegardé → cop_last/list_copied_files

Fichiers:
  app/src/.../MyScreen.kt
  app/src/.../ViewModel.kt
  ...
```

---

## 🔁 Intégration avec `cop_last` (`cl_`)

Après exécution, le skill `cop_last` peut re-copier immédiatement la même liste sans relancer la détection Git :  
`cl_` → lit `list_copied_files` → re-copie dans le presse-papiers.

---

## ⚠️ Cas limites

| Cas | Comportement |
|:---|:---|
| Aucun fichier non commité | Avertissement, ne pas écraser `list_copied_files` |
| Fichier supprimé (`D`) | Ignoré (pas de chemin valide) |
| Renommage (`R`) | Copier le **nouveau** nom uniquement |
| Projet non Git | Afficher erreur et stopper |
| `list_copied_files` absent | Créer automatiquement fichier + répertoire |
